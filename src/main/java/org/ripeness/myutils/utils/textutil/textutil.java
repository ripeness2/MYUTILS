package org.ripeness.myutils.utils.textutil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class textutil {

    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    public static Location stringToLocation(String str) {
        String[] parts = str.split(",");
        if (parts.length != 4) return null; // Geçersiz format

        World world = Bukkit.getWorld(parts[0]);
        if (world == null) return null; // Dünya bulunamazsa

        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);

        return new Location(world, x, y, z);
    }

}
