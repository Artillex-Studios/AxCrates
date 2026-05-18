package com.artillexstudios.axcrates.crates.rewards;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ContainerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class CrateReward {
    protected String id;
    protected LinkedList<String> commands = new LinkedList<>();
    protected LinkedList<ItemStack> items = new LinkedList<>();
    protected double chance;
    protected ItemStack display;

    public CrateReward() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
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
        Scheduler.get().run(scheduledTask -> {
            for (String cmd : commands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getName()));
            }
            ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), items.stream().map(ItemStack::clone).toList(), player.getLocation());
        });
    }

    public void setCommands(List<String> commands) {
        if (commands == null) return;
        this.commands = new LinkedList<>(commands);
    }

    public void setItems(List<ItemStack> items) {
        if (items == null) return;
        this.items = new LinkedList<>(items);
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

    public void setDisplay(ItemStack display) {
        this.display = display.clone();
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            this.id = UUID.randomUUID().toString();
            return;
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return "CrateReward{" +
                "id='" + id + '\'' +
                ", " +
                "commands=" + commands +
                ", items=" + items +
                ", chance=" + chance +
                ", display=" + display +
                '}';
    }
}
