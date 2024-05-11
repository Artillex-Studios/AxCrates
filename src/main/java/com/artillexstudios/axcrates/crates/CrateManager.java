package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axcrates.AxCrates;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class CrateManager {
    private static final HashMap<String, Crate> crates = new HashMap<>();

    public static void refresh() {
        final HashSet<String> loadedCrates = new HashSet<>();
        final File path = new File(AxCrates.getInstance().getDataFolder(), "crates");
        if (path.exists()) {
            for (File file : path.listFiles()) {
                final String name = file.getName().replace(".yml", "");
                loadedCrates.add(name);
                if (crates.containsKey(name)) {
                    crates.get(name).reload();
                    continue;
                }
                final Config settings = new Config(file);
                crates.put(name, new Crate(settings, name));
            }
        }

        for (String str : crates.keySet()) {
            if (loadedCrates.contains(str)) continue;
            crates.remove(str);
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
