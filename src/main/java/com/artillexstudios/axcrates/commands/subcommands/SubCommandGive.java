package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axcrates.keys.Key;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.bukkit.EntitySelector;

import java.util.List;

public class SubCommandGive {
    public void execute(CommandSender sender, EntitySelector<Player> player, Key key, boolean virtual, boolean silent, Integer amount) {
        if (amount == null) amount = 1;
        // todo: virtual
        // todo: silent

        final ItemStack item = key.item().clone();
        item.setAmount(amount);
        for (Player pl : player) {
            ContainerUtils.INSTANCE.addOrDrop(pl.getInventory(), List.of(item), pl.getLocation());
        }
    }
}
