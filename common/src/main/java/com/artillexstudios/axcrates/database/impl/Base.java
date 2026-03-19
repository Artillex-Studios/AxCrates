package com.artillexstudios.axcrates.database.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.artillexstudios.axcrates.database.Database;
import com.artillexstudios.axcrates.keys.Key;

public class Base implements Database {

    public Connection getConnection() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setup() {
        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_keys (
                	id INT NOT NULL AUTO_INCREMENT,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (name),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_crates (
                	id INT NOT NULL AUTO_INCREMENT,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (name),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_players (
                	id INT NOT NULL AUTO_INCREMENT,
                	uuid VARCHAR(36) NOT NULL,
                	name VARCHAR(512) NOT NULL,
                	UNIQUE (uuid),
                	PRIMARY KEY (id)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_keydata (
                	playerId INT NOT NULL,
                	keyId INT NOT NULL,
                	amount INT NOT NULL,
                	PRIMARY KEY (playerId, keyId)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_statistics (
                	playerId INT NOT NULL,
                	crateId INT NOT NULL,
                	opens INT NOT NULL,
                    	lastOpen BIGINT NOT NULL,
                	PRIMARY KEY (playerId, crateId)
                );
                """
        );

        execute("""
                CREATE TABLE IF NOT EXISTS axcrates_blacklist (
                	playerId INT NOT NULL,
                	crateId INT NOT NULL,
                	rewardIndex INT NOT NULL,
                	PRIMARY KEY (playerId, crateId, rewardIndex)
                );
                """
        );
    }

    private void execute(String sql) {
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getKeyId(Key key) {
        String sql = "SELECT id FROM axcrates_keys WHERE name = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String sql2 = "INSERT INTO axcrates_keys (name) VALUES (?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, key.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Key not found in database!");
    }

    @Override
    public int getPlayerId(OfflinePlayer player) {
        String sql = "SELECT id FROM axcrates_players WHERE uuid = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUniqueId().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String sql2 = "INSERT INTO axcrates_players (uuid, name) VALUES (?, ?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        throw new RuntimeException("Key not found in database!");
    }

    @Override
    public void setVirtualKey(OfflinePlayer player, Key key, int amount) {
        String sql = "UPDATE axcrates_keydata SET amount = amount + ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void giveVirtualKey(OfflinePlayer player, Key key, int amount) {
        if (createPlayerKey(player, key, amount)) return;

        String sql = "UPDATE axcrates_keydata SET amount = amount + ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean takeVirtualKey(OfflinePlayer player, Key key, int amount) {
        if (createPlayerKey(player, key, 0)) return false;

        int keys = getVirtualKeys(player, key);
        amount = Math.min(keys, amount);

        String sql = "UPDATE axcrates_keydata SET amount = amount - ? WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, amount);
            stmt.setInt(2, getPlayerId(player));
            stmt.setInt(3, getKeyId(key));
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean createPlayerKey(OfflinePlayer player, Key key, int amount) {
        String sql = "INSERT INTO axcrates_keydata (playerId, keyId, amount) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            stmt.setInt(3, amount);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    @Override
    public void resetVirtualKey(OfflinePlayer player, Key key) {
        String sql = "DELETE FROM axcrates_keydata WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void reset(OfflinePlayer player) {
        String sql = "DELETE FROM axcrates_keydata WHERE playerId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getVirtualKeys(OfflinePlayer player, Key key) {
        String sql = "SELECT amount FROM axcrates_keydata WHERE playerId = ? AND keyId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getKeyId(key));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getCrateId(String crateName) {
        String sql = "SELECT id FROM axcrates_crates WHERE name = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, crateName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        String sql2 = "INSERT INTO axcrates_crates (name) VALUES (?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, crateName);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getRewardId(String crateName, int rewardIndex) {
        return rewardIndex;
    }

    @Override
    public void addBlacklist(OfflinePlayer player, String crateName, int rewardIndex) {
        String sql = "INSERT IGNORE INTO axcrates_blacklist (playerId, crateId, rewardIndex) VALUES (?, ?, ?);";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getCrateId(crateName));
            stmt.setInt(3, rewardIndex);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void removeBlacklist(OfflinePlayer player, String crateName, int rewardIndex) {
        String sql = "DELETE FROM axcrates_blacklist WHERE playerId = ? AND crateId = ? AND rewardIndex = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getCrateId(crateName));
            stmt.setInt(3, rewardIndex);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isBlacklisted(OfflinePlayer player, String crateName, int rewardIndex) {
        String sql = "SELECT 1 FROM axcrates_blacklist WHERE playerId = ? AND crateId = ? AND rewardIndex = ? LIMIT 1;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getCrateId(crateName));
            stmt.setInt(3, rewardIndex);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Set<Integer> getBlacklist(OfflinePlayer player, String crateName) {
        Set<Integer> blacklist = new HashSet<>();
        String sql = "SELECT rewardIndex FROM axcrates_blacklist WHERE playerId = ? AND crateId = ?;";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, getPlayerId(player));
            stmt.setInt(2, getCrateId(crateName));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    blacklist.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return blacklist;
    }

    @Override
    public void disable() {

    }
}
