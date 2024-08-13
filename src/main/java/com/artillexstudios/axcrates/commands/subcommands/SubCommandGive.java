package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.bukkit.EntitySelector;

import java.util.List;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum SubCommandGive {
    INSTANCE;

    public void execute(CommandSender sender, EntitySelector<Player> player, Key key, boolean virtual, boolean silent, Integer amount) {
        if (amount == null) amount = 1;
        // todo: virtual
        // todo: silent

        final ItemStack item = key.item().clone();
        item.setAmount(amount);

        final String players = player.size() == 1 ? player.get(0).getName() : LANG.getString("x-players").replace("%amount%", "" + player.size());
        var map = Map.of("%key%", ItemUtils.getFormattedItemName(item), "%amount%", "" + item.getAmount(), "%player%", players);
        MESSAGEUTILS.sendLang(sender, "key.given", map);
        if (!silent) {
            for (Player pl : player) {
                MESSAGEUTILS.sendLang(pl, "key.got", map);
            }
        }

        for (Player pl : player) {
            ContainerUtils.INSTANCE.addOrDrop(pl.getInventory(), List.of(item), pl.getLocation());
        }
    }
}
