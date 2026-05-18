package com.artillexstudios.axcrates.blacklist;

import java.util.Set;

import org.bukkit.entity.Player;

import com.artillexstudios.axcrates.AxCrates;

public class PlayerBlacklistManager {

    public void addBlacklist(Player player, String crateName, String rewardId) {
        AxCrates.getDatabase().addBlacklist(player, crateName, rewardId);
    }

    public void removeBlacklist(Player player, String crateName, String rewardId) {
        AxCrates.getDatabase().removeBlacklist(player, crateName, rewardId);
    }

    public boolean isBlacklisted(Player player, String crateName, String rewardId) {
        return AxCrates.getDatabase().isBlacklisted(player, crateName, rewardId);
    }

    public Set<String> getBlacklist(Player player, String crateName) {
        return AxCrates.getDatabase().getBlacklist(player, crateName);
    }

    public void toggleBlacklist(Player player, String crateName, String rewardId) {
        if (isBlacklisted(player, crateName, rewardId)) {
            removeBlacklist(player, crateName, rewardId);
        } else {
            addBlacklist(player, crateName, rewardId);
        }
    }
}
