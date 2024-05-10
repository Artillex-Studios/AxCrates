package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axcrates.crates.Crate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SubCommandOpen {
    public void execute(CommandSender sender, Crate crate, Player player, Integer amount, boolean force, boolean silent) {
        if (amount == null) amount = 1;
        if (!force) {
            // todo: check for keys
        }
        crate.open(player, amount, silent);
    }
}
