package com.artillexstudios.axcrates.listeners;

import com.artillexstudios.axcrates.hooks.HookManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListeners implements Listener {

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        if (HookManager.getModelHook() != null)
            HookManager.getModelHook().join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        if (HookManager.getModelHook() != null)
            HookManager.getModelHook().leave(event.getPlayer());
    }
}
