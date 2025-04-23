package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.packetentity.PacketEntity;
import com.artillexstudios.axapi.packetentity.meta.entity.ItemEntityMeta;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.StaticPlaceholder;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.animation.placed.impl.AuraAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.BeamsAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ChainsAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.CircleAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ConeAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.DoubleSpiralAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.ForceFieldAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.HaloAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.OrbitAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SimpleAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SpawnerAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SphereAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.SpiralAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.TornadoAnimation;
import com.artillexstudios.axcrates.animation.placed.impl.VortexAnimation;
import com.artillexstudios.axcrates.crates.previews.impl.PreviewGui;
import com.artillexstudios.axcrates.hooks.HookManager;
import com.artillexstudios.axcrates.hooks.models.ModelHook;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;

import static com.artillexstudios.axcrates.AxCrates.CONFIG;
import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class PlacedCrate {
    private final Location location;
    private final Crate crate;
    private Hologram hologram = null;
    private Animation animation = null;
    private final File preview;
    private final boolean hasPreview;
    private PacketEntity entity;

    public PlacedCrate(@NotNull Location location, @NotNull Crate crate) { // todo: update preview when adding rewards
        this.location = location;
        this.crate = crate;

        ModelHook modelHook = getModelHook();
        if (modelHook != null) {
            modelHook.spawnCrate(this);
        }

        if (crate.placedHologramEnabled) {
            final Location holoLoc = location.clone();
            holoLoc.add(0.5, 0.5, 0.5);
            holoLoc.add(crate.placedHologramOffsetX, crate.placedHologramOffsetY, crate.placedHologramOffsetZ);
            hologram = new Hologram(holoLoc, Serializers.LOCATION.serialize(location), crate.placedHologramLineHeight);
            hologram.addLines(StringUtils.formatListToString(crate.placedHologramLines), HologramLine.Type.TEXT);
            hologram.addPlaceholder(new StaticPlaceholder(string -> {
                return string.replace("%crate%", crate.displayName);
            }));
        }

        preview = new File(AxCrates.getInstance().getDataFolder(), "previews/" + crate.previewTemplate + ".yml");
        hasPreview = preview.exists();

        if (crate.placedParticleEnabled) {
            String[] anim = crate.placedParticleAnimation.split("-");
            animation = switch (anim[0].toLowerCase()) {
                case "simple" -> new SimpleAnimation(this);
                case "spiral" -> new SpiralAnimation(this);
                case "doublespiral" -> new DoubleSpiralAnimation(this);
                case "tornado" -> new TornadoAnimation(this);
                case "chains" -> new ChainsAnimation(this);
                case "sphere" -> new SphereAnimation(this);
                case "beams" -> new BeamsAnimation(this);
                case "halo" -> new HaloAnimation(this);
                case "cone" -> new ConeAnimation(this);
                case "vortex" -> new VortexAnimation(this);
                case "forcefield" -> new ForceFieldAnimation(this);
                case "orbit" -> new OrbitAnimation(this);
                case "spawner" -> new SpawnerAnimation(this);
                case "circle" -> new CircleAnimation(this);
                case "aura" -> new AuraAnimation(this);
                default -> throw new IllegalStateException("Animation does not exist: " + crate.placedParticleAnimation);
            };
        }
    }

    public void openPreview(Player player) {
        if (!hasPreview) {
            MESSAGEUTILS.sendLang(player, "errors.no-preview", Map.of("%crate%", crate.displayName));
            return;
        }
        new PreviewGui(new Config(preview), crate).open(player);
    }

    private long lastOpen = 0;
    public void open(Player player) {
        if (!CONFIG.getBoolean("actually-open-container.enabled", true)) return;
        final Block block = location.getBlock();
        lastOpen = System.currentTimeMillis();
        if (block.getState() instanceof Lidded lidded) {
            lidded.open();

            long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
            Scheduler.get().runLater(scheduledTask -> {
                if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
                lidded.close();
            }, stayOpenTime / 50);
            return;
        }

        ModelHook modelHook = getModelHook();
        if (modelHook != null) {
            modelHook.open(player, this);

            long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
            Scheduler.get().runLater(scheduledTask -> {
                if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
                modelHook.close(player, this);
            }, stayOpenTime / 50);
        }
    }

    public void showReward(Player player, ItemStack reward, String display) {
        if (!CONFIG.getBoolean("actually-open-container.show-reward", true)) return;
        EntityType entityType;
        try {
            entityType = EntityType.valueOf("ITEM");
        } catch (Exception ex) {
            entityType = EntityType.valueOf("DROPPED_ITEM");
        }

        if (entity != null) entity.remove();
        entity = NMSHandlers.getNmsHandler().createEntity(entityType, location.clone().add(0.5, 2, 0.5));
        final ItemEntityMeta meta = (ItemEntityMeta) entity.meta();
        meta.hasNoGravity(true);
        meta.customNameVisible(true);
        meta.itemStack(WrappedItemStack.wrap(reward));
        meta.name(StringUtils.format(display));
        entity.spawn();
        long stayOpenTime = CONFIG.getLong("actually-open-container.open-time-miliseconds", 3_000L);
        Scheduler.get().runLater(scheduledTask2 -> {
            if (System.currentTimeMillis() - lastOpen < stayOpenTime - 50L) return;
            entity.remove();
        }, stayOpenTime / 50);
    }

    public void tick() {
        if (animation == null) return;
        animation.play();
    }

    public void remove() {
        if (hologram == null) return;
        hologram.remove();
        ModelHook modelHook = getModelHook();
        if (modelHook != null) modelHook.removeCrate(this);
    }

    public Location getLocation() {
        return location;
    }

    public Crate getCrate() {
        return crate;
    }

    public Hologram getHologram() {
        return hologram;
    }

    @Nullable
    private ModelHook getModelHook() {
        return HookManager.getModelHooks().stream().filter(mh -> mh.getName().equalsIgnoreCase(crate.placedTextureMode)).findAny().orElse(null);
    }
}
