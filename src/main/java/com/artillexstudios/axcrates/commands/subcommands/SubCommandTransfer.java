package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public enum SubCommandTransfer {
    INSTANCE;

    public void execute(Player sender, OfflinePlayer player, Key key, Integer amount) {
        if (amount == null) amount = 1;
    }
}
