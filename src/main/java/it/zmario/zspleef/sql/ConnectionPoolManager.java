package it.zmario.zspleef.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.zmario.zspleef.zSpleef;
import it.zmario.zspleef.utils.ConfigHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private final zSpleef plugin;

    private HikariDataSource dataSource;

    private String hostname, port, database, username, password;

    public ConnectionPoolManager(zSpleef plugin) {
        this.plugin = plugin;
        init();
        setupPool();
        makeTables();
    }

    private void init() {
        hostname = ConfigHandler.getConfig().getString("MySQL.Host");
        port = ConfigHandler.getConfig().getString("MySQL.Port");
        database = ConfigHandler.getConfig().getString("MySQL.Database");
        username = ConfigHandler.getConfig().getString("MySQL.Username");
        password = ConfigHandler.getConfig().getString("MySQL.Password");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("zSpleef");
        config.addDataSourceProperty("useSSL", ConfigHandler.getConfig().getBoolean("MySQL.Properties.UseSSL"));
        dataSource = new HikariDataSource(config);
    }

    private void makeTables() {
        try (Connection connection = dataSource.getConnection(); PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `player_stats` (UUID varchar(36), wins INTEGER(10), loses INTEGER(10), level INTEGER, xp INTEGER)")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}