package com.artillexstudios.axcrates.database;

import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.artillexstudios.axcrates.keys.Key;

public interface Database {

    String getType();

    void setup();

    int getKeyId(Key key);

    int getPlayerId(OfflinePlayer player);

    void setVirtualKey(OfflinePlayer player, Key key, int amount);

    void giveVirtualKey(OfflinePlayer player, Key key, int amount);

    boolean takeVirtualKey(OfflinePlayer player, Key key, int amount);

    void resetVirtualKey(OfflinePlayer player, Key key);

    void reset(OfflinePlayer player);

    int getVirtualKeys(OfflinePlayer player, Key key);

    int getCrateId(String crateName);

    int getRewardId(String crateName, int rewardIndex);

    void addBlacklist(OfflinePlayer player, String crateName, int rewardIndex);

    void removeBlacklist(OfflinePlayer player, String crateName, int rewardIndex);

    boolean isBlacklisted(OfflinePlayer player, String crateName, int rewardIndex);

    Set<Integer> getBlacklist(OfflinePlayer player, String crateName);

    void disable();
}
