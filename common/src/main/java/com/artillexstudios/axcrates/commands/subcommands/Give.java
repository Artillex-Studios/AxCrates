package com.artillexstudios.axcrates.commands.subcommands;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.LANG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public enum Give {
    INSTANCE;

    public void execute(CommandSender sender, List<Player> player, Key key, boolean virtual, boolean silent, Integer amount) {
        if (amount == null) amount = 1;
        player = checkLimit(player);

        ItemStack item = key.item().clone();
        item.setAmount(amount);

        String players = player.size() == 1 ? player.getFirst().getName() : LANG.getString("x-players").replace("%amount%", "" + player.size());

        Map<String, String> replacements = Map.of(
                "%key%", ItemUtils.getFormattedItemName(item),
                "%amount%", "" + item.getAmount(),
                "%player%", players
        );

        MESSAGEUTILS.sendLang(sender, "key.give.staff", replacements);
        if (!silent) {
            for (Player pl : player) {
                MESSAGEUTILS.sendLang(pl, "key.give.player", replacements);
            }
        }
        if (!virtual) {
            for (Player pl : player) {
                ContainerUtils.INSTANCE.addOrDrop(pl.getInventory(), List.of(item.clone()), pl.getLocation());
            }
        } else {
            for (Player pl : player) {
                AxCrates.getDatabase().giveVirtualKey(pl, key, amount);
            }
        }
    }

    private List<Player> checkLimit(List<Player> list) {
        int ipLimit = CONFIG.getInt("key-give-ip-limit", -1);
        if (ipLimit <= 0) return list;

        List<Player> players = new ArrayList<>();
        Map<String, Integer> limits = new HashMap<>();
        for (Player player : list) {
            if (player.getAddress() == null) continue;
            String address = player.getAddress().getAddress().getHostAddress();
            int limit = limits.getOrDefault(address, 0);
            if (limit >= ipLimit) continue;
            players.add(player);
            limits.put(address, limit + 1);
        }
        return players;
    }
}
