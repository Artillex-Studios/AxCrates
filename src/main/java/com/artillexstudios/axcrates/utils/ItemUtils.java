package com.artillexstudios.axcrates.utils;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemUtils {

    public static void saveItem(ItemStack item, Config config, String route) {
        config.set(route, new ItemBuilder(item.clone()).serialize(false));
        final ItemStack itOriginal = new ItemBuilder(item.clone()).get();
        final ItemStack itNew = new ItemBuilder(config.getSection(route)).get();
        if (!itNew.isSimilar(itOriginal)) {
            config.set(route, new ItemBuilder(item.clone()).serialize(true));
        }
        config.save();
    }

    public static Map<Object, Object> saveItem(ItemStack item) {
        var map = new ItemBuilder(item.clone()).serialize(false);
        final ItemStack itOriginal = new ItemBuilder(item.clone()).get();
        final ItemStack itNew = new ItemBuilder(map).get();
        if (!itNew.isSimilar(itOriginal)) {
            return new ItemBuilder(item.clone()).serialize(true);
        }
        return map;
    }
}
