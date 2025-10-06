package org.ripeness.myutils.commandexecutor;

import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.ripeness.myutils.utils.RPNSItems;

import static org.ripeness.myutils.utils.chat.tt.rcc;

public class commandExecuterListener implements Listener {

    private static Plugin plugin;

    public static void init(Plugin plugin) {
        commandExecuterListener.plugin = plugin;
    }

    public static void register() {
        if (plugin == null) return;
        plugin.getServer().getPluginManager().registerEvents(new commandExecuterListener(), plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClickInventoryItem(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        if (item.getType().isAir()) return;
        if (item.getItemMeta() == null) return;
        NBTItem nbt = RPNSItems.papi.getNBT(item);
        if (nbt == null) return;
        if (!nbt.hasKey("myutils_command_executer")) return;
        Player p = (Player) e.getWhoClicked();
        String stringList = nbt.getString("myutils_cmdexecutor_console");
        if (stringList == null) return;
        if (stringList.isEmpty()) return;
        // nbtItem.setString("myutils_cmdexecutor_console", String.join(";;;;", consolecmds));

        // myutils_cmdexecutor_console içerisinde ;;;; ile ayır ve console ye yazdır.
        // stringList içerisinde örn: "say Hello;;;;time set day;;;;give @p minecraft:diamond 1"
        String[] commands = stringList.split(";;;;");
        for (String s : commands) {
            if (s == null) continue;
            if (s.isEmpty()) continue;
            s = rcc(RPNSItems.papi.applyPAPI(p, s));
            if (s.isEmpty()) continue;
            if (s.startsWith("player:")) {
                s = removeFirstString(s, "player:");
                if (s.isEmpty()) continue;
                p.performCommand(s);
                continue;
            }
            if (s.startsWith("console:")) {
                s = removeFirstString(s, "console:");
                if (s.isEmpty()) continue;
            }
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), s);
        }


//        for (String s : stringList) {
//            if (s == null) continue;
//            if (s.isEmpty()) continue;
//            s = rcc(RPNSItems.papi.applyPAPI(p, s));
//            if (s.isEmpty()) continue;
//            if (s.startsWith("player:")) {
//                s = removeFirstString(s, "player:");
//                if (s.isEmpty()) continue;
//                p.performCommand(s);
//                continue;
//            }
//            if (s.startsWith("console:")) {
//                s = removeFirstString(s, "console:");
//                if (s.isEmpty()) continue;
//            }
//            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), s);
//        }
    }

    private String removeFirstString(String original, String toRemove) {
        if (original.startsWith(toRemove)) {
            return original.substring(toRemove.length());
        }
        return original;
    }

}
