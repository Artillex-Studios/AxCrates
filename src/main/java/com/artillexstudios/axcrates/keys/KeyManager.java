package com.artillexstudios.axcrates.keys;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.utils.NBTUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;

public class KeyManager {
    private static final HashMap<String, Key> keys = new HashMap<>();

    public static void refresh() {
        keys.clear();

        final File path = new File(AxCrates.getInstance().getDataFolder(), "keys");
        if (path.exists()) {
            for (File file : path.listFiles()) {
                final Config settings = new Config(file);
                final String name = file.getName().replace(".yml", "");

                final ItemBuilder builder = new ItemBuilder(settings.getSection("item"));
                final ItemStack original = builder.clonedGet();
                final ItemStack item = builder.get();
                NBTUtils.writeToNBT(item, "axcrates-key", name);
                keys.put(name, new Key(settings, item, original));
            }
        }
    }

    @Nullable
    public static Key getKey(String name) {
        return keys.getOrDefault(name, null);
    }

    @NotNull
    public static HashMap<String, Key> getKeys() {
        return keys;
    }
}
