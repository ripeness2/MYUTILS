package org.ripeness.myutils.hikarisql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariSQL {

    public static HikariConfig getDefaultConfig(String host, int port, String database, String username, String password) {

        HikariConfig config = new HikariConfig();

        // JDBC URL ve temel ayarlar
        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                host, port, database
        );
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);


        // Havuz boyutları (örnek değerler)
        config.setMinimumIdle(2);
        config.setIdleTimeout(600_000);      // ms
        config.setMaxLifetime(1800_000);     // ms

        config.setMaximumPoolSize(50);      // eşzamanlı en fazla 50 bağlantı
        config.setConnectionTimeout(10_000); // bekleme süresi 10 saniye
        config.setLeakDetectionThreshold(30_000); // kaçakları 15 s içinde logla

        // İsteğe bağlı ek özellikler
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        return config;
    }

    private final HikariDataSource dataSource;

    public HikariSQL(HikariConfig config) {

        // DataSource’u oluştur
        dataSource = new HikariDataSource(config);
    }

    /** SQL bağlantısını havuzdan alır */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /** Uygulama kapanırken havuzu kapatmak için */
    public void close() {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
