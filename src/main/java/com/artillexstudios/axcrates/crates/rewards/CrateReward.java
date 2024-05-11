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
    protected final ItemStack display;

    public CrateReward(List<String> commands, List<ItemStack> items, double chance, ItemStack display) {
        this.commands = commands;
        this.items = items;
        this.chance = chance;
        this.display = display;
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

    public ItemStack getDisplay() {
        return display;
    }

    public void run(Player player) {
        for (String cmd : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
        }
        ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), items, player.getLocation());
    }

    @Override
    public String toString() {
        return "CrateReward{" +
                "commands=" + commands +
                ", items=" + items +
                ", chance=" + chance +
                ", display=" + display +
                '}';
    }
}
