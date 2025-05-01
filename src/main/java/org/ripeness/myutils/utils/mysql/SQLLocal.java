package org.ripeness.myutils.utils.mysql;

import java.sql.*;

/**
 * MySQLUtil (RNSQLUtil)
 * This Util made from _ripeness
 * this is my discord name: _ripeness
 * you can add friend me =)
 *
 *
 */

public class SQLLocal {
    private final String host;
    private final String username;
    private final String password;
    private final int port;
    private final String database;
    private final String table;

    private Connection connection;

    public SQLLocal(String host, String username, String password, int port, String database, String table) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
        this.table = table;
    }

    public Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    public void createTable() {
        try (Statement stmt = connect().createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS " + getTable() + " ("
                    + "`key` VARCHAR(255) NOT NULL PRIMARY KEY, "
                    + "`value` TEXT"
                    + ")";
            stmt.executeUpdate(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existsTable() {
        try (ResultSet rs = connect().getMetaData().getTables(null, null, getTable(), null)) {
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTable() {
        return table;
    }
}
