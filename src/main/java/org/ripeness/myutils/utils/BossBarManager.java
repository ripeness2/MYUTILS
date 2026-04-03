// BossBarManager.java
package org.ripeness.myutils.utils;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BossBarManager {
    private final JavaPlugin plugin;
    private final Map<UUID, BossBar> bars = new ConcurrentHashMap<>();

    public BossBarManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Oluşturup oyuncuya gösterir
    public BossBar createFor(Player player, String title, BarColor color, BarStyle style, double progress) {
        // Title null olabilir; Bukkit.createBossBar String başlık bekler
        BossBar bar = Bukkit.createBossBar(title, color, style);
        bar.setProgress(clamp(progress));
        bar.addPlayer(player);
        bars.put(player.getUniqueId(), bar);
        return bar;
    }

    // Mevcut bar'ın progress'ini günceller (0..1)
    public void updateProgress(Player player, double progress) {
        BossBar bar = bars.get(player.getUniqueId());
        if (bar != null) bar.setProgress(clamp(progress));
    }

    // Mevcut bar'ın başlığını değiştirir (String)
    public void setTitle(Player player, String title) {
        BossBar bar = bars.get(player.getUniqueId());
        if (bar != null) bar.setTitle(title);
    }

    // Oyuncudan bossbar'ı kaldırır ve kayıt temizlenir
    public void remove(Player player) {
        BossBar bar = bars.remove(player.getUniqueId());
        if (bar != null) {
            bar.removePlayer(player);
            // Eğer bar başka oyunculara da eklenmişse onları da temizlemek istersiniz
        }
    }

    // Plugin kapandığında veya temizleme gerektiğinde çağırın
    public void removeAll() {
        for (Map.Entry<UUID, BossBar> e : bars.entrySet()) {
            BossBar bar = e.getValue();
            for (Player p : bar.getPlayers()) bar.removePlayer(p);
        }
        bars.clear();
    }

    private double clamp(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }
}
