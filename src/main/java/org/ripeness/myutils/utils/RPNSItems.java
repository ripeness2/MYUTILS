package org.ripeness.myutils.utils;

import de.tr7zw.nbtapi.NBTItem;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.ripeness.myutils.muc;
import org.ripeness.myutils.utils.inventoryies.inventoryutil;
import org.ripeness.myutils.utils.items.ItemBuilder;
import org.ripeness.myutils.utils.items.ItemStringer;
import org.ripeness.myutils.utils.nbt.NBTMaker;

import javax.annotation.Nullable;
import java.util.*;

import static org.ripeness.myutils.utils.RPNSItems.papi.applyPAPI;
import static org.ripeness.myutils.utils.chat.tt.rcc;

public class RPNSItems {


    public static class config {

        private static boolean checkSingleItemCondition(OfflinePlayer player, ConfigurationSection cs) {
            String type = cs.getString("type", "string:equals").toLowerCase();
            String v1 = parseConditionVal(player, cs.getString("value1", ""));
            String v2 = parseConditionVal(player, cs.getString("value2", ""));

            switch (type) {
                case "int:higher":
                    return getConditionInt(v1) > getConditionInt(v2);
                case "!int:higher":
                    return getConditionInt(v1) < getConditionInt(v2);
                case "int:higherorequals":
                    return getConditionInt(v1) >= getConditionInt(v2);
                case "!int:higherorequals":
                    return getConditionInt(v1) <= getConditionInt(v2);
                case "string:equals":
                    return v1.equals(v2);
                case "!string:equals":
                    return !v1.equals(v2);
                case "string:contains":
                    return v1.contains(v2);
                case "!string:contains":
                    return !v1.contains(v2);
                case "string:isempty":
                    return v1.isEmpty();
                case "!string:isempty":
                    return !v1.isEmpty();
                case "string:howmany;higherorequals":
                    return checkConditionHowMany(v1, v2, false);
                case "!string:howmany;higherorequals":
                    return checkConditionHowMany(v1, v2, true);
                default:
                    return false;
            }
        }

        private static String parseConditionVal(OfflinePlayer player, String val) {
            if (val == null || val.isEmpty()) return "";
            if (val.contains("%") || val.contains("[")) {
                // Kendi mevcut applyPAPI ve Bracket yapını kullanıyoruz
                String processed = applyPAPI(player, val);
                if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    processed = PlaceholderAPI.setPlaceholders(player, processed);
                    processed = PlaceholderAPI.setBracketPlaceholders(player, processed);
                }
                return processed;
            }
            return val;
        }

        private static int getConditionInt(String val) {
            try {
                if ("00".equals(val)) return 0;
                return Integer.parseInt(val);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        private static boolean checkConditionHowMany(String main, String val2, boolean invert) {
            String[] parts = val2.split(";", 2);
            if (parts.length < 2) return false;
            int threshold = getConditionInt(parts[0]);
            String sub = parts[1];

            int count = 0, idx = 0;
            while ((idx = main.indexOf(sub, idx)) != -1) {
                count++;
                idx += sub.length();
            }
            return invert == (count < threshold);
        }

        private static ItemStack applyItemOutActions(ItemStack item, ConfigurationSection actionsCs, OfflinePlayer pl, boolean directOnly) {
            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            for (String actionKey : actionsCs.getKeys(false)) {
                ConfigurationSection actionData = actionsCs.getConfigurationSection(actionKey);
                if (actionData == null) continue;

                // applyDirect kontrolü (varsayılan: false)
                boolean isDirect = actionData.getBoolean("applyDirect", false);

                // Filtreye uymuyorsa bu turu atla
                if (isDirect != directOnly) continue;

                String actionType = actionData.getString("type", "").toLowerCase();

                if (actionType.equalsIgnoreCase("add_lores") || actionType.equalsIgnoreCase("addlores")) {
                    List<String> loresToAdd = actionData.getStringList("lores");
                    if (!loresToAdd.isEmpty()) {
                        List<String> currentLore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                        for (String s : loresToAdd) {
                            if (currentLore != null) {
                                currentLore.add(rcc(applyPAPI(pl, s)));
                            }
                        }
                        meta.setLore(currentLore);
                    }
                }
                // Diğer tipler...
            }

            item.setItemMeta(meta);
            return item;
        }

        public static ItemStack buildItemInConfig(ConfigurationSection cs, @Nullable OfflinePlayer pl, Plugin plugin) {
            if (cs == null) return null;

            // 1. TEMEL MATERYAL KONTROLÜ
            String typeStr = cs.getString("type");
            if (typeStr == null) return null;
            Material type = Material.getMaterial(typeStr.toUpperCase());
            if (type == null) return null;

            // 2. ITEMBUILDER İLE İNŞA BAŞLIYOR
            ItemBuilder ib = new ItemBuilder(type);
            ItemStack res1 = ib.build();

            // --- [YENİ] ITEM STRING LOGIC BURAYA ---
            // Bu kısım, konfigürasyondaki diğer ayarlardan önce gelmeli ki temel eşyayı belirleyebilsin.
            String rawItemString = cs.getString("itemString", "");
            // Not: muc.replaceData listenin dışarıdan geldiğini varsayıyorum, eğer metodun içinde yoksa hata verebilir.
            // Eğer replaceDatas listesi bu metodun içinde değilse burayı kendi sistemine göre düzenle.
            if (!rawItemString.isEmpty()) {
                String processedString = applyPAPI(pl, rawItemString);

                ItemStringer.resultClass resultClass = ItemStringer.stringToItem(processedString);
                ItemStack stringResult = resultClass.getResult();

                if (stringResult != null) {
                    ItemStringer.Options options = resultClass.getOptions();
                    ItemBuilder stringBuilder = new ItemBuilder(res1);
                    stringBuilder.setType(stringResult.getType());

                    if (options.isIncludeModelData() && stringResult.hasItemMeta() && stringResult.getItemMeta().hasCustomModelData())
                        stringBuilder.setCustomModelData(stringResult.getItemMeta().getCustomModelData());

                    if (options.isIncludeLore() && stringResult.hasItemMeta() && stringResult.getItemMeta().hasLore())
                        stringBuilder.setLore(stringResult.getItemMeta().getLore());

                    if (options.isIncludeDisplay() && stringResult.hasItemMeta() && stringResult.getItemMeta().hasDisplayName())
                        stringBuilder.setDisplayName(stringResult.getItemMeta().getDisplayName());

                    res1 = stringBuilder.build();

                    if (options.isIncludeAmount()) res1.setAmount(stringResult.getAmount());

                    if (options.isIncludeFlags() && stringResult.hasItemMeta()) {
                        ItemMeta im = res1.getItemMeta();
                        if (im != null) {
                            for (ItemFlag flag : stringResult.getItemMeta().getItemFlags()) im.addItemFlags(flag);
                            res1.setItemMeta(im);
                        }
                    }

                    // Banner ve Potion Meta aktarımları
                    if (options.isIncludeBannerMeta() && stringResult.getItemMeta() instanceof BannerMeta) {
                        ItemMeta resMeta = res1.getItemMeta();
                        if (resMeta instanceof BannerMeta) {
                            ((BannerMeta) resMeta).setPatterns(((BannerMeta) stringResult.getItemMeta()).getPatterns());
                            res1.setItemMeta(resMeta);
                        }
                    }

                    if (options.isIncludePotionData() && stringResult.getItemMeta() instanceof PotionMeta) {
                        ItemMeta resMeta = res1.getItemMeta();
                        if (resMeta instanceof PotionMeta) {
                            ((PotionMeta) resMeta).setBasePotionData(((PotionMeta) stringResult.getItemMeta()).getBasePotionData());
                            res1.setItemMeta(resMeta);
                        }
                    }
                }
            }
            ib = new ItemBuilder(res1);
            // --- ITEM STRING LOGIC SONU ---

            // Display Name
            if (cs.isString("display")) {
                ib.setDisplayName(rcc(applyPAPI(pl, cs.getString("display", ""))));
            }

            // Lore (Hem 'lores' hem 'lore' desteği)
            List<String> rawLores = cs.getStringList(cs.isList("lores") ? "lores" : "lore");
            if (!rawLores.isEmpty()) {
                List<String> processedLores = new ArrayList<>();
                for (String s : rawLores) {
                    processedLores.add(rcc(applyPAPI(pl, s)));
                }
                ib.setLore(processedLores);
            }

            // Miktar, Parlama ve Model Data
            ib.setAmount(Math.max(1, Math.min(64, cs.getInt("amount", 1))));
            ib.setGlow(cs.getBoolean("glow", false));
            if (cs.isInt("modeldata")) ib.setCustomModelData(cs.getInt("modeldata"));

            // Kafa Dokuları (Kafaysa)
            if (type == Material.PLAYER_HEAD) {
                String hv = cs.getString("headValue");
                String ho = cs.getString("headOwner");
                if (hv != null && !hv.isEmpty()) ib.setHeadTextureValue(hv);
                if (ho != null && !ho.isEmpty()) ib.setHead(applyPAPI(pl, ho));
            }

            ItemStack res = ib.build();
            ItemMeta meta = res.getItemMeta();
            if (meta == null) return res;

            // 3. ÖZEL META İŞLEMLERİ (Potion, Leather, Flags)

            // Potion Efekti
            if (meta instanceof PotionMeta && cs.getBoolean("otoEffectPotion", true)) {
                ((PotionMeta) meta).setBasePotionData(new PotionData(PotionType.POISON));
            }

            // Zırh ve İksir Renkleri
// --- Leather Armor Kısmı ---
            String lColor = cs.isSet("leatherColor") ? "leatherColor" : (cs.isSet("leather_color") ? "leather_color" : "color");

            if (cs.isSet(lColor) && meta instanceof LeatherArmorMeta) {
                String[] s = applyPAPI(pl, cs.getString(lColor)).split(",");
                if (s.length == 3) {
                    ((LeatherArmorMeta) meta).setColor(Color.fromRGB(
                            Integer.parseInt(s[0].trim()),
                            Integer.parseInt(s[1].trim()),
                            Integer.parseInt(s[2].trim())
                    ));
                }
            }

// --- Potion Kısmı ---
            String pColor = cs.isSet("potionColor") ? "potionColor" : (cs.isSet("potion_color") ? "potion_color" : "color");

            if (cs.isSet(pColor) && meta instanceof PotionMeta) {
                String[] s = applyPAPI(pl, cs.getString(pColor)).split(",");
                if (s.length == 3) {
                    ((PotionMeta) meta).setColor(Color.fromRGB(
                            Integer.parseInt(s[0].trim()),
                            Integer.parseInt(s[1].trim()),
                            Integer.parseInt(s[2].trim())
                    ));
                }
            }

            // Item Flagleri (HideAll)
            if (cs.getBoolean("hideAll", true)) {
                for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
            }

            res.setItemMeta(meta);

            // 4. NBT VE KOMUTLAR (Plugin varsa)
            if (plugin != null) {
                NBTMaker nbt = new NBTMaker(res, true);

                // Komutları NBT'ye göm
                List<String> consoleCmds = cs.getStringList("consolecmds");
                List<String> playerCmds = cs.getStringList("playercmds");
                nbt.setString(plugin.getName() + "+myutils_cmdexecutor_console", String.join(";;;;", consoleCmds));
                nbt.setString(plugin.getName() + "+myutils_cmdexecutor_player", String.join(";;;;", playerCmds));

                // Özel NBT'ler (nbties)
                ConfigurationSection nbtSection = cs.getConfigurationSection("nbties");
                if (nbtSection != null) {
                    for (String rawKey : nbtSection.getKeys(false)) {
                        String rawValue = nbtSection.getString(rawKey);
                        if (rawValue == null || !rawValue.contains(";")) continue;

                        String keyName = rawKey;
                        if (rawKey.contains(";;")) {
                            String[] compSplit = rawKey.split(";;", 2);
                            nbt.setCompound(compSplit[0].trim());
                            keyName = compSplit[1].trim();
                        }

                        String[] valSplit = rawValue.split(";", 2);
                        String nbtType = valSplit[0].toUpperCase(Locale.ENGLISH).trim();
                        String finalVal = applyPAPI(pl, valSplit[1].trim());

                        try {
                            switch (nbtType) {
                                case "INTEGER":
                                    nbt.setInteger(keyName, Integer.parseInt(finalVal));
                                    break;
                                case "BOOLEAN":
                                    nbt.setBoolean(keyName, Boolean.parseBoolean(finalVal));
                                    break;
                                default:
                                    nbt.setString(keyName, finalVal);
                                    break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                res = nbt.getItem(); // NBT'li item'ı geri al
            }

            return res;
        }

        public static ItemStack getItemInConfig(String loc, FileConfiguration cf, @Nullable OfflinePlayer pl, Plugin plugin, boolean checkConditions) {
            ConfigurationSection cs = cf.getConfigurationSection(loc);
            if (cs == null) return null;

            List<ConfigurationSection> metConditions = new ArrayList<>();
            String targetOutput = null;
            ConfigurationSection outputCondition = null; // Hangi koşulun output verdiğini tutması için ekledik

            // --- 1. AŞAMA: ŞARTLARI KONTROL ET VE SAĞLANANLARI TOPLA ---
            if (checkConditions && cs.isConfigurationSection("conditions")) {
                ConfigurationSection condSection = cs.getConfigurationSection("conditions");

                if (condSection != null) {
                    for (String key : condSection.getKeys(false)) {
                        ConfigurationSection condData = condSection.getConfigurationSection(key);
                        if (condData != null && checkSingleItemCondition(pl, condData)) {
                            metConditions.add(condData);
                        }
                    }
                }

                // Önceliğe göre sırala (Düşük rakam = Yüksek öncelik)
                metConditions.sort(Comparator.comparingInt(c -> c.getInt("priority", 888)));

                // En yüksek öncelikli (ilk sıradaki) geçerli output'u bul
                for (ConfigurationSection cond : metConditions) {
                    String out = cond.getString("output");
                    if (out != null && !out.isEmpty()) {
                        targetOutput = out;
                        outputCondition = cond; // Output'u tetikleyen şartı kaydet
                        break; // Output bulunduğunda dur (Diğer koşulların out_actions'ları hala listede duruyor)
                    }
                }
            }

            ItemStack resultItem = null;

            // --- 2. AŞAMA: ANA EŞYAYI OLUŞTUR VEYA ÇEK ---
            if (targetOutput != null) {
                // Eğer koşullardan biri başka bir output'a yönlendirdiyse, ana eşya olarak onu çek (Sonsuz döngüyü önlemek için checkConditions=false)
                resultItem = getItemInConfig(targetOutput, cf, pl, plugin, false);

                // EĞER OUTPUT VARSA: Sadece bu "outputCondition" içindeki applyDirect: true olan aksiyonları o an uygula
                //noinspection ConstantValue
                if (resultItem != null && outputCondition != null && outputCondition.isConfigurationSection("out_actions")) {
                    // applyDirect: true olarak çağırıyoruz
                    applyItemOutActions(resultItem, outputCondition.getConfigurationSection("out_actions"), pl, true);
                }
            } else {
                // Eğer hiçbir koşulda output yoksa (veya checkConditions false ise), eşyayı buildItemInConfig ile inşa et
                resultItem = buildItemInConfig(cs, pl, plugin);
            }

            // --- 3. AŞAMA: SAĞLANAN TÜM KOŞULLARIN GLOBAL (applyDirect: false) OUT_ACTIONS'LARINI SIRAYLA UYGULA ---
            if (resultItem != null && !metConditions.isEmpty()) {
                for (ConfigurationSection cond : metConditions) {
                    if (cond.isConfigurationSection("out_actions")) {
                        // Burada applyDirect: false olanları (veya yazılmamış olanları) topluca en sona ekliyoruz
                        applyItemOutActions(resultItem, cond.getConfigurationSection("out_actions"), pl, false);
                    }
                }
            }

            return resultItem;
        }

        public static ItemStack getItemInConfig(String loc, FileConfiguration cf, @Nullable OfflinePlayer pl, Plugin plugin) {
            return getItemInConfig(loc, cf, pl, plugin, true);
        }

        public static ItemStack getItemInConfigNullable(String loc, FileConfiguration cf, @Nullable String pln) {
            Material type = null;
            String display = null;
            List<String> lores = null;
            int cmodeldata = -1;
            boolean isGlow = false;

            if (cf.isConfigurationSection(loc)) {
                ConfigurationSection cs = cf.getConfigurationSection(loc);
                if (cs.isString("itemType")) {
                    type = Material.getMaterial(Objects.requireNonNull(cs.getString("itemType")));
                }
                if (cs.isString("displayName")) {
                    display = rcc(Objects.requireNonNull(cs.getString("displayName"))).replace("%player%", pln == null ? "" : pln);
                }
                if (cs.isList("lores")) {
                    List<String> ex = new ArrayList<>();
                    for (String s : cs.getStringList("lores")) {
                        ex.add(rcc(s).replace("%player%", pln == null ? "" : pln));
                    }
                    lores = ex;
                }
                if (cs.isInt("customModelData")) {
                    cmodeldata = cf.getInt("customModelData");
                }
                if (cs.isBoolean("isGlow")) {
                    isGlow = cs.getBoolean("isGlow");
                }

                if (type == null) type = Material.PAPER;
                ItemBuilder ib = new ItemBuilder(type);
                ib.setDisplayName(display);
                ib.setLore(lores);
                ib.setGlow(isGlow);
                if (cmodeldata != -1) ib.setCustomModelData(cmodeldata);
                return ib.build();
            }
            return null;
        }

    }


    public static class papi {

        public static void replaceMetaItem(ItemStack item, List<muc.replaceData> replaceInfos) {
            if (item == null) return;
            if (replaceInfos == null) return;
            if (replaceInfos.isEmpty()) return;
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta == null) return;
            if (itemMeta.hasDisplayName()) {
                String displayName = itemMeta.getDisplayName();
                for (muc.replaceData ri : replaceInfos) {
                    displayName = displayName.replace(ri.getOldChar(), ri.getNewChar());
                }
                itemMeta.setDisplayName(displayName);
            }
            if (itemMeta.hasLore()) {
                List<String> lore = itemMeta.getLore();
                if (lore != null) {
                    List<String> newLore = new ArrayList<>();
                    for (String s : lore) {
                        for (muc.replaceData ri : replaceInfos) {
                            s = s.replace(ri.getOldChar(), ri.getNewChar());
                        }
                        newLore.add(s);
                    }
                    itemMeta.setLore(newLore);
                }
            }
            item.setItemMeta(itemMeta);
        }

        public static ItemStack applyPAPItem(OfflinePlayer player, ItemStack item) {
            // Eşya veya Meta verisi boşsa direkt geri dön (NullPointerException koruması)
            if (item == null || !item.hasItemMeta()) {
                return item;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return item;

            // 1. Display Name (Görünen İsim) İşlemi
            if (meta.hasDisplayName()) {
                String updatedName = RPNSItems.papi.applyPAPI(player, meta.getDisplayName());
                meta.setDisplayName(rcc(updatedName));
            }

            // 2. Lore (Açıklama) İşlemi
            if (meta.hasLore()) {
                List<String> updatedLore = new ArrayList<>();
                //noinspection DataFlowIssue
                for (String line : meta.getLore()) {
                    // Her bir satıra tek tek placeholder'ları uygula
                    updatedLore.add(rcc(PlaceholderAPI.setPlaceholders(player, line)));
                }
                meta.setLore(updatedLore);
            }

            // Meta'yı eşyaya geri yükle
            item.setItemMeta(meta);

            return item;
        }


//        public static boolean isMatchingItem(ItemStack mainItem, ItemStack otherItem) {
//            // 1. Null ve Temel Kontroller
//            if (mainItem == null || otherItem == null) return false;
//
//            // 2. Type (Material) Kontrolü
//            // Config'deki String'i Material'e çeviriyoruz (Büyük/küçük harf duyarsız olması için toUpperCase)
//            Material configMaterial = otherItem.getType();
//
//            // Eğer configdeki material geçersizse veya eşleşmiyorsa false
//            if (mainItem.getType() != configMaterial) {
//                return false;
//            }
//
//            // Meta verisini alıyoruz (Display ve ModelData için gerekli)
//            ItemMeta meta = mainItem.getItemMeta();
//            ItemMeta otherMeta = otherItem.getItemMeta();
//            if (otherMeta == null && meta == null) return true;
//            if (otherMeta == null) return false;
//            if (meta == null) return false;
//            String displayName = otherMeta.getDisplayName();
//
//            // 3. Display Name Kontrolü
//            // KURAL: Display null değilse VE boş string ("") değilse kontrol et.
//            if (!displayName.isEmpty()) {
//                // Configde isim istenmiş ama eşyanın metası veya ismi yoksa -> false
//                if (!meta.hasDisplayName()) {
//                    return false;
//                }
//
//                boolean nameMatches = isNameMatches(displayName, meta);
//
//                if (!nameMatches) return false;
//            }
//
//            // 4. Custom Model Data Kontrolü
//            // KURAL: Modeldata -1 değilse kontrol et.
//            if (config.getModeldata() != -1) {
//                // Configde model istenmiş ama eşyanın model datası yoksa -> false
//                if (meta == null || !meta.hasCustomModelData()) {
//                    return false;
//                }
//
//                // Sayısal değer eşit değilse -> false
//                if (meta.getCustomModelData() != config.getModeldata()) {
//                    return false;
//                }
//            }
//
//            // Tüm engelleri aştıysa eşleşmiştir
//            return true;
//        }


        public static String applyPAPI(OfflinePlayer pl, String str) {
            if (pl == null) return str;
            return PlaceholderAPI.setPlaceholders(pl, PlaceholderAPI.setBracketPlaceholders(pl, str));
        }

        public static String applyPAPI(Player pl, String str) {
            if (pl == null) return str;
            return PlaceholderAPI.setPlaceholders(pl, PlaceholderAPI.setBracketPlaceholders(pl, str));
        }

        public static NBTItem getNBT(ItemStack item) {
            if (item == null || item.getType().isAir()) return null;
            return new NBTItem(item);
        }

        public static void setBooleanNBT(ItemStack item, String bo) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setBoolean(bo, true);
                n.mergeNBT(item);
            }
        }

        public static void setStringNBT(ItemStack item, String key, String value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setString(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setIntegerNBT(ItemStack item, String key, Integer integer) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setInteger(key, integer);
                n.mergeNBT(item);
            }
        }

        public static void setDoubleNBT(ItemStack item, String key, Double value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setDouble(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setByteNBT(ItemStack item, String key, Byte value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setByte(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setByteArrayNBT(ItemStack item, String key, byte[] value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setByteArray(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setObjectNBT(ItemStack item, String key, Object value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setObject(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setFloatNBT(ItemStack item, String key, Float value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setFloat(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setItemStackNBT(ItemStack item, String key, ItemStack value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setItemStack(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setLongNBT(ItemStack item, String key, Long value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setLong(key, value);
                n.mergeNBT(item);
            }
        }

        public static void setShortNBT(ItemStack item, String key, Short value) {
            if (item != null) {
                NBTItem n = getNBT(item);
                n.setShort(key, value);
                n.mergeNBT(item);
            }
        }


        public static boolean hasNBTTag(ItemStack item, String nbttag) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).hasTag(nbttag);
            }
            return false;
        }

        public static boolean hasNBTTagEqualsString(ItemStack item, String nbttag, String checkingStr) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).getString(nbttag).equals(checkingStr);
            }
            return false;
        }

        public static boolean hasNBTCompound(ItemStack item, String compstr) {
            if (item != null) {
                return RPNSItems.papi.getNBT(item).getCompound(compstr) != null;
            }
            return false;
        }

        public static <T> T getItemNbtResultInInventory(Inventory in, String nbtKey, Class<T> resultClazz) {
            if (in != null) {
                int i = inventoryutil.getIndexOfItemFromNbtInMenu(nbtKey, in);
                if (i == -1) return null;
                ItemStack item = in.getItem(i);
                if (item != null) {
                    NBTItem nbtItem = getNBT(item);
                    if (nbtItem != null) {
                        if (resultClazz == String.class) {
                            return resultClazz.cast(nbtItem.getString(nbtKey));
                        } else if (resultClazz == Integer.class) {
                            return resultClazz.cast(nbtItem.getInteger(nbtKey));
                        } else if (resultClazz == Boolean.class) {
                            return resultClazz.cast(nbtItem.getBoolean(nbtKey));
                        } else if (resultClazz == Double.class) {
                            return resultClazz.cast(nbtItem.getDouble(nbtKey));
                        } // stringlist
                        else if (resultClazz == List.class) {
                            if (nbtItem.hasTag(nbtKey)) {
                                return resultClazz.cast(nbtItem.getStringList(nbtKey));
                            } else {
                                return resultClazz.cast(new ArrayList<String>());
                            }
                        } else {
                            return null;
                        }

                    }
                }
            }
            return null;
        }


    }


}
