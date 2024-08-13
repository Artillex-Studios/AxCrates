package com.artillexstudios.axcrates.hooks;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.hooks.models.ModelEngine3Hook;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

public class HookManager {
    private static ModelHook modelHook;

    public static void setupHooks() {
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            if (ClassUtils.INSTANCE.classExists("com.ticxo.modelengine.api.generator.Hitbox")) {
                ModelEngine3Hook hook = new ModelEngine3Hook();
                modelHook = hook;
                AxCrates.getInstance().getServer().getPluginManager().registerEvents(hook, AxCrates.getInstance());
                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hooked into ModelEngine3!"));
            }
            else {
//                ModelEngine4Hook hook = new ModelEngine4Hook();
//                modelHook = hook;
//                AxCrates.getInstance().getServer().getPluginManager().registerEvents(hook, AxCrates.getInstance());
//                Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hooked into ModelEngine4!"));
            }
        }
    }

    @Nullable
    public static ModelHook getModelHook() {
        return modelHook;
    }
}
