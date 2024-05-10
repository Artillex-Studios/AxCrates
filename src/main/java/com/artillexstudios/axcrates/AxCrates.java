package com.artillexstudios.axcrates;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.data.ThreadedQueue;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.MainCommand;
import com.artillexstudios.axcrates.commands.annotations.CrateCompleter;
import com.artillexstudios.axcrates.commands.annotations.CrateKeys;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.FileUtils;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import revxrsal.commands.bukkit.BukkitCommandHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class AxCrates extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;
    private static ThreadedQueue<Runnable> threadedQueue;
    public static BukkitAudiences BUKKITAUDIENCES;

    public static ThreadedQueue<Runnable> getThreadedQueue() {
        return threadedQueue;
    }

    public static AxPlugin getInstance() {
        return instance;
    }

    public void enable() {
        instance = this;

        int pluginId = 21234;
        new Metrics(this, pluginId);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

//        LanguageManager.reload();

        if (FileUtils.PLUGIN_DIRECTORY.resolve("crates/").toFile().mkdirs()) {
            FileUtils.copyFromResource("crates");
        }

        if (FileUtils.PLUGIN_DIRECTORY.resolve("keys/").toFile().mkdirs()) {
            FileUtils.copyFromResource("keys");
        }

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        threadedQueue = new ThreadedQueue<>("AxCrates-Datastore-thread");

        BUKKITAUDIENCES = BukkitAudiences.create(this);

        KeyManager.refresh();
        CrateManager.refresh();

//        getServer().getPluginManager().registerEvents(new a(), this);
        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.getAutoCompleter().registerSuggestionFactory(parameter -> {
            if (parameter.hasAnnotation(CrateCompleter.class)) {
                return (args, sender, command) -> CrateManager.getCrates().keySet();
            }
            if (parameter.hasAnnotation(CrateKeys.class)) {
                return (args, sender, command) -> KeyManager.getKeys().keySet();
            }
            return null;
        });

        handler.registerValueResolver(Crate.class, resolver -> CrateManager.getCrate(resolver.pop()));
        handler.registerValueResolver(Key.class, resolver -> KeyManager.getKey(resolver.pop()));

        handler.getAutoCompleter().registerSuggestion("x", (args, sender, command) -> {
            final Player player = Bukkit.getPlayer(sender.getUniqueId());
            final List<String> list = new ArrayList<>();
            if (player == null) return list;
            final Location pl = player.getLocation();
            list.add("" + (pl.getBlockX() + 0.5f));
            list.add((pl.getBlockX() + 0.5f) + " " + pl.getBlockY() + " " + (pl.getBlockZ() + 0.5f));
            final Block b = player.getTargetBlockExact(5);
            if (b == null) return list;
            list.add("" + (b.getX() + 0.5f));
            list.add((b.getX() + 0.5f) + " " + (b.getY() + 1.0f) + " " + (b.getZ() + 0.5f));
            return list;
        });
        handler.getAutoCompleter().registerSuggestion("y", (args, sender, command) -> {
            final Player player = Bukkit.getPlayer(sender.getUniqueId());
            final List<String> list = new ArrayList<>();
            if (player == null) return list;
            final Location pl = player.getLocation();
            list.add("" + (pl.getBlockY() + 0.5f));
            list.add(pl.getBlockY() + " " + (pl.getBlockZ() + 0.5f));
            final Block b = player.getTargetBlockExact(5);
            if (b == null) return list;
            list.add("" + (b.getY() + 0.5f));
            list.add((b.getY() + 1.0f) + " " + (b.getZ() + 0.5f));
            return list;
        });
        handler.getAutoCompleter().registerSuggestion("z", (args, sender, command) -> {
            final Player player = Bukkit.getPlayer(sender.getUniqueId());
            final List<String> list = new ArrayList<>();
            if (player == null) return list;
            final Location pl = player.getLocation();
            list.add("" + (pl.getBlockZ() + 0.5f));
            final Block b = player.getTargetBlockExact(5);
            if (b == null) return list;
            list.add("" + (b.getZ() + 0.5f));
            return list;
        });

        handler.register(new MainCommand());
        handler.registerBrigadier();
        handler.enableAdventure(BUKKITAUDIENCES);

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400[AxCrates] Loaded plugin!"));
    }
}