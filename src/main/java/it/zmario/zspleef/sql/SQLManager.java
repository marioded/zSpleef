package it.zmario.zspleef.sql;

import it.zmario.zspleef.zSpleef;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class SQLManager {

    private final ConnectionPoolManager pool;
    private final zSpleef zSpleef;

    public SQLManager(zSpleef plugin) {
        this.zSpleef = plugin;
        pool = new ConnectionPoolManager(plugin);
    }

    public void onDisable() {
        pool.closePool();
    }

    public CompletableFuture<Integer> getStatistic(Player player, String statistic) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement("SELECT " + statistic + " FROM player_stats WHERE uuid=?")) {
                select.setString(1, player.getUniqueId().toString());
                try (ResultSet result = select.executeQuery()) {
                    if (result.next())
                        return result.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        });
    }

    public void setStatistic(Player player, String statistic, int value) {
        Bukkit.getScheduler().runTaskAsynchronously(zSpleef, () -> {
            try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement("UPDATE player_stats SET " + statistic + "=? WHERE uuid=?")) {
                update.setInt(1, value);
                update.setString(2, player.getUniqueId().toString());
                update.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addStatistic(Player player, String statistic, int value) {
        Bukkit.getScheduler().runTaskAsynchronously(zSpleef, () -> {
            try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement("UPDATE player_stats SET " + statistic + "=" + statistic + "+? WHERE uuid=?")) {
                update.setInt(1, value);
                update.setString(2, player.getUniqueId().toString());
                update.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeStatistic(Player player, String statistic, int value) {
        Bukkit.getScheduler().runTaskAsynchronously(zSpleef, () -> {
            try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement("UPDATE player_stats SET " + statistic + "=" + statistic + "-? WHERE uuid=?")) {
                update.setInt(1, value);
                update.setString(2, player.getUniqueId().toString());
                update.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void createPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(zSpleef, () -> {
            try (Connection connection = pool.getConnection(); PreparedStatement update = connection.prepareStatement("INSERT INTO player_stats (UUID, wins, loses, level, xp) VALUES(?, ?, ?, ?, ?)")) {
                update.setString(1, player.getUniqueId().toString());
                update.setInt(2, 0);
                update.setInt(3, 0);
                update.setInt(4, 1);
                update.setInt(5, 1);
                update.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public CompletableFuture<Boolean> isPresent(Player player) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = pool.getConnection(); PreparedStatement select = connection.prepareStatement("SELECT uuid FROM player_stats WHERE uuid=?")) {
                select.setString(1, player.getUniqueId().toString());
                try (ResultSet result = select.executeQuery()) {
                    if (result.next())
                        return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }

}