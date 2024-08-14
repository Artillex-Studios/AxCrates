package com.artillexstudios.axcrates;

import com.artillexstudios.axapi.AxPlugin;
import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.data.ThreadedQueue;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.dvs.versioning.BasicVersioning;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.dumper.DumperSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.general.GeneralSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.loader.LoaderSettings;
import com.artillexstudios.axapi.libs.boostedyaml.boostedyaml.settings.updater.UpdaterSettings;
import com.artillexstudios.axapi.libs.libby.BukkitLibraryManager;
import com.artillexstudios.axapi.utils.FeatureFlags;
import com.artillexstudios.axapi.utils.MessageUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.MainCommand;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.database.Database;
import com.artillexstudios.axcrates.database.impl.H2;
import com.artillexstudios.axcrates.hooks.HookManager;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.lang.LanguageManager;
import com.artillexstudios.axcrates.libraries.Libraries;
import com.artillexstudios.axcrates.listeners.BreakListener;
import com.artillexstudios.axcrates.listeners.InteractListener;
import com.artillexstudios.axcrates.listeners.PlayerListeners;
import com.artillexstudios.axcrates.scheduler.PlacedCrateTicker;
import com.artillexstudios.axcrates.utils.CommandMessages;
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
import java.util.Locale;

public final class AxCrates extends AxPlugin {
    public static Config CONFIG;
    public static Config LANG;
    public static MessageUtils MESSAGEUTILS;
    private static AxPlugin instance;
    private static ThreadedQueue<Runnable> threadedQueue;
    public static BukkitAudiences BUKKITAUDIENCES;
    private static Database database;

    public static ThreadedQueue<Runnable> getThreadedQueue() {
        return threadedQueue;
    }

    public static Database getDatabase() {
        return database;
    }

    public static AxPlugin getInstance() {
        return instance;
    }

    public void load() {
        instance = this;
        BukkitLibraryManager libraryManager = new BukkitLibraryManager(this, "lib");
        libraryManager.addMavenCentral();
        libraryManager.addJitPack();
        libraryManager.addRepository("https://repo.codemc.org/repository/maven-public/");
        libraryManager.addRepository("https://repo.papermc.io/repository/maven-public/");

        for (Libraries lib : Libraries.values()) {
            libraryManager.loadLibrary(lib.getLibrary());
        }
    }

    public void enable() {
        instance = this; // todo: fix comments in yml

        int pluginId = 21234; // todo: placeholders
        new Metrics(this, pluginId);

        CONFIG = new Config(new File(getDataFolder(), "config.yml"), getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());
        LANG = new Config(new File(getDataFolder(), "lang.yml"), getResource("lang.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setKeepAll(true).setVersioning(new BasicVersioning("version")).build());

        switch (CONFIG.getString("database.type").toLowerCase()) {
//            case "sqlite" -> database = new SQLite();
//            case "mysql" -> database = new MySQL();
//            case "postgresql" -> database = new PostgreSQL();
            default -> database = new H2();
        }

        database.setup();

        LanguageManager.reload();

        if (FileUtils.PLUGIN_DIRECTORY.resolve("crates/").toFile().mkdirs()) {
            FileUtils.copyFromResource("crates");
        }

        if (FileUtils.PLUGIN_DIRECTORY.resolve("keys/").toFile().mkdirs()) {
            FileUtils.copyFromResource("keys");
        }

        if (FileUtils.PLUGIN_DIRECTORY.resolve("previews/").toFile().mkdirs()) {
            FileUtils.copyFromResource("previews");
        }

        MESSAGEUTILS = new MessageUtils(LANG.getBackingDocument(), "prefix", CONFIG.getBackingDocument());

        threadedQueue = new ThreadedQueue<>("AxCrates-Datastore-thread");

        BUKKITAUDIENCES = BukkitAudiences.create(this);

        HookManager.setupHooks();

        KeyManager.refresh();
        CrateManager.refresh();

        getServer().getPluginManager().registerEvents(new InteractListener(), this);
        getServer().getPluginManager().registerEvents(new BreakListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListeners(), this);

        PlacedCrateTicker.start();

        final BukkitCommandHandler handler = BukkitCommandHandler.create(this);

        handler.getAutoCompleter().registerParameterSuggestions(Crate.class, (args, sender, command) -> {
            return CrateManager.getCrates().keySet();
        });

        handler.getAutoCompleter().registerParameterSuggestions(Key.class, (args, sender, command) -> {
            return KeyManager.getKeys().keySet();
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

        handler.getTranslator().add(new CommandMessages());
        handler.setLocale(new Locale("en", "US"));

        handler.register(new MainCommand());
        handler.registerBrigadier();
        handler.enableAdventure(BUKKITAUDIENCES);

        Bukkit.getConsoleSender().sendMessage(StringUtils.formatToString("&#FF4400[AxCrates] Loaded plugin!"));
    }

    public void disable() {
        PlacedCrateTicker.stop();
        for (Crate crate : CrateManager.getCrates().values()) {
            crate.remove();
        }
        database.disable();
    }

    public void updateFlags() {
        FeatureFlags.USE_LEGACY_HEX_FORMATTER.set(true);
        FeatureFlags.PACKET_ENTITY_TRACKER_ENABLED.set(true);
        FeatureFlags.HOLOGRAM_UPDATE_TICKS.set(5L);
        FeatureFlags.PACKET_ENTITY_TRACKER_THREADS.set(5);
    }
}