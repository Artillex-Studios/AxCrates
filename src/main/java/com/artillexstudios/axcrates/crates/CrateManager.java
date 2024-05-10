package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.AxCrates;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;

public class CrateManager {
    private static final HashMap<String, Crate> crates = new HashMap<>();

    public static void refresh() {
        crates.clear();

        final File path = new File(AxCrates.getInstance().getDataFolder(), "crates");
        if (path.exists()) {
            for (File file : path.listFiles()) {
                final Config settings = new Config(file);
                final String name = file.getName().replace(".yml", "");
                crates.put(name, new Crate(settings, name));
            }
        }
    }

    @Nullable
    public static Crate getCrate(String name) {
        return crates.getOrDefault(name, null);
    }

    @NotNull
    public static HashMap<String, Crate> getCrates() {
        return crates;
    }
}
