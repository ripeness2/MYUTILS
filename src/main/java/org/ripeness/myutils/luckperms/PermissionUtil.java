package org.ripeness.myutils.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class PermissionUtil {

    /**
     * LuckPerms API ile, oyuncunun kendi ve grup tabanlı izinlerini kontrol eder.
     *
     * @param perm       Kontrol edilecek izin düğümü (örneğin "myplugin.command.use")
     * @param playerName Kontrol edilecek oyuncunun kullanıcı adı (exact Minecraft username)
     * @return Eğer oyuncu (user ve/veya gruplardan) bu izne sahip ise true, aksi takdirde false.
     */
    public static boolean checkPlayerPerm(String perm, String playerName) {
        // 1) PlayerName'den UUID bul
        OfflinePlayer offline = Bukkit.getOfflinePlayer(playerName);
        UUID uuid = offline.getUniqueId();
        //noinspection ConstantValue
        if (uuid == null) {
            // Oyuncu hiç sunucuya bağlanmamışsa UUID null olabilir; direkt false dönebiliriz.
            return false;
        }

        // 2) LuckPerms instance'ını al
        LuckPerms luckPerms = LuckPermsProvider.get();
        //noinspection ConstantValue
        if (luckPerms == null) {
            // LuckPerms plugin'i yüklü değilse
            return false;
        }

        // 3) Kullanıcıyı (User) yükle (eğer önbellekte yoksa, sunucudaki veritabanından çekilir)
        //    Bu işlem arka planda asenkron yapılır; join() ile bloklanıyoruz.
//        User user = luckPerms.getUserManager().loadUser(uuid).join();
//        if (user == null) {
//            // Kullanıcı bulunamadı (olası bir hata durumu)
//            return false;
//        }

        AtomicBoolean has = new AtomicBoolean(false);
        if (offline.isOnline()) {
            User user = luckPerms.getUserManager().getUser(uuid);
            has.set(user.getCachedData().getPermissionData().checkPermission(perm).asBoolean());
        } else {
            CompletableFuture<User> uc = luckPerms.getUserManager().loadUser(uuid);
            has.set(uc.join().getCachedData().getPermissionData().checkPermission(perm).asBoolean());
        }
        return has.get();
        // 4) Kullanıcının bağlam (context) bilgilerini al
        //    QueryOptions: kullanıcının hangi dünyada/context'te olduğu, IP vb. dinamik veri
        //    getQueryOptions(...) yeni LuckPerms sürümlerinde kullanılıyor.
//        @NonNull Optional<QueryOptions> queryOptions = luckPerms.getContextManager().getQueryOptions(user);
//
//        // 5) Önbellekteki izinler üzerinden kontrol et
//        CachedDataManager cachedDataManager = user.getCachedData();
//        CachedPermissionData permissionData = cachedDataManager.getPermissionData(queryOptions.get());
//
//        // 6) İznin geçerli olup olmadığını kontrol et
//        boolean hasPermission = permissionData.checkPermission(perm).asBoolean();
//
//        // 7) Sonucu döndür
//        return hasPermission;
    }

//    public static boolean checkIsInGroup(UUID uuid, String groupName) {
//        LuckPerms api = LuckPermsProvider.get();
//
////        // Yükleme şeklinde (async)
////        CompletableFuture<User> future = api.getUserManager().loadUser(uuid);
////        future.thenAccept(user -> {
////            if (user == null) {
////                // user yüklenemedi
////                return;
////            }
////            boolean inGroup = user.getPrimaryGroup().equalsIgnoreCase(groupName) ||
////                    user.getNodes().stream()
////                            .filter(n -> n.getType() == NodeType.INHERITANCE)
////                            .map(Node::getKey)
////                            .anyMatch(k -> k.equalsIgnoreCase("group." + groupName));
////
////            // buraya sonucu işle
////        });
//
//        // Veya modifyUser (kısa süreli değişiklik/okuma için kullanılır)
//        AtomicBoolean inGroup = new AtomicBoolean(false);
//        api.getUserManager().modifyUser(uuid, user -> {
//            inGroup.set(user.getPrimaryGroup().equalsIgnoreCase(groupName) ||
//                    user.getNodes().stream()
//                            .filter(n -> n.getType() == NodeType.INHERITANCE)
//                            .map(Node::getKey)
//                            .anyMatch(k -> k.equalsIgnoreCase("group." + groupName)));
//            // sonuçla işle
//        });
//
//
//        return inGroup.get();
//    }

    public static CompletableFuture<Boolean> checkIsInGroupAsync(UUID uuid, String groupName) {
        LuckPerms api = LuckPermsProvider.get();
        return api.getUserManager().loadUser(uuid) // CompletableFuture<User>
                .thenApply(user -> {
                    if (user == null) return false;
                    String groupKey = "group." + groupName;
                    boolean inPrimary = groupName.equalsIgnoreCase(user.getPrimaryGroup());
                    boolean inInheritance = user.getNodes().stream()
                            .filter(n -> n.getType() == NodeType.INHERITANCE)
                            .map(n -> n.getKey()) // bazen "group.name" dönüyor
                            .anyMatch(k -> k.equalsIgnoreCase(groupKey) || k.equalsIgnoreCase(groupName));
                    return inPrimary || inInheritance;
                });
    }

}
