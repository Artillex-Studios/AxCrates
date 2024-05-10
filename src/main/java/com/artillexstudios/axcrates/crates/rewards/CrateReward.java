package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.utils.ContainerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CrateReward {
    protected final List<String> commands;
    protected final List<ItemStack> items;
    protected final double chance;

    public CrateReward(List<String> commands, List<ItemStack> items, double chance) {
        this.commands = commands;
        this.items = items;
        this.chance = chance;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public double getChance() {
        return chance;
    }

    public void run(Player player) {
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
        ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), items, player.getLocation());
    }
}
