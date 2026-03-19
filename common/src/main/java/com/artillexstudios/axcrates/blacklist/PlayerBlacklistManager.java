package com.artillexstudios.axcrates.blacklist;

import java.util.Set;

import org.bukkit.entity.Player;

import com.artillexstudios.axcrates.AxCrates;

public class PlayerBlacklistManager {

    public void addBlacklist(Player player, String crateName, int rewardIndex) {
        AxCrates.getDatabase().addBlacklist(player, crateName, rewardIndex);
    }

    public void removeBlacklist(Player player, String crateName, int rewardIndex) {
        AxCrates.getDatabase().removeBlacklist(player, crateName, rewardIndex);
    }

    public boolean isBlacklisted(Player player, String crateName, int rewardIndex) {
        return AxCrates.getDatabase().isBlacklisted(player, crateName, rewardIndex);
    }

    public Set<Integer> getBlacklist(Player player, String crateName) {
        return AxCrates.getDatabase().getBlacklist(player, crateName);
    }

    public void toggleBlacklist(Player player, String crateName, int rewardIndex) {
        if (isBlacklisted(player, crateName, rewardIndex)) {
            removeBlacklist(player, crateName, rewardIndex);
        } else {
            addBlacklist(player, crateName, rewardIndex);
        }
    }
}
