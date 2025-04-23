package com.artillexstudios.axcrates.hooks;

import com.artillexstudios.axapi.reflection.ClassUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.hooks.models.ModelEngine3Hook;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import com.artillexstudios.axcrates.hooks.other.PlaceholderAPIHook;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HookManager {
    private static final List<ModelHook> modelHooks = new ArrayList<>();

    public static void setupHooks() {
        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            if (ClassUtils.INSTANCE.classExists("com.ticxo.modelengine.api.generator.Hitbox")) {
                ModelEngine3Hook hook = new ModelEngine3Hook();
                modelHooks.add(hook);
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

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook().register();
            Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#00FF00[AxCrates] Hooked into PlaceholderAPI!"));
        }
    }

    public static List<ModelHook> getModelHooks() {
        return modelHooks;
    }
}
