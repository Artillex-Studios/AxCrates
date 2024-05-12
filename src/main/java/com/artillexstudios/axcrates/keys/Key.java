package com.artillexstudios.axcrates.keys;

import com.artillexstudios.axapi.config.Config;
import org.bukkit.inventory.ItemStack;

public record Key(String name, Config settings, ItemStack item, ItemStack original) {
}
