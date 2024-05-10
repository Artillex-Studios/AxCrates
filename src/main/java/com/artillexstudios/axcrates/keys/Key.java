package com.artillexstudios.axcrates.keys;

import com.artillexstudios.axapi.config.Config;
import org.bukkit.inventory.ItemStack;

public record Key(Config settings, ItemStack item) {
}
