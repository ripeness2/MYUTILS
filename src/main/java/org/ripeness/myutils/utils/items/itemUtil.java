//package org.ripeness.myutils.utils.items;
//
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class itemUtil {
//
//    public static ConcurrentHashMap<String, Object> itemToMap(ItemStack i) {
//        if (i == null) return null;
//        if (i.getType().isAir()) return null;
//        ConcurrentHashMap<String, Object> c = new ConcurrentHashMap<>();
//        ItemStack item = i.clone();
//        ItemMeta m = item.getItemMeta();
//        String name = item.getType().name();
//        int amount = item.getAmount();
//        List<String> lore = new ArrayList<>();
//        if (m.hasLore()) lore = m.getLore();
//        String displayName = m.getDisplayName();
//        int customModelData = m.getCustomModelData();
//
//        return c;
//    }
//
//}
