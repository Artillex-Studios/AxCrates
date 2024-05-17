package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.StaticPlaceholder;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.placedanimation.Animation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.BeamsAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.ChainsAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.CircleAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.AuraAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.OrbitAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.HaloAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.SimpleAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.ForceFieldAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.SpawnerAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.SphereAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.DoubleSpiralAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.SpiralAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.TornadoAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.ConeAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.impl.VortexAnimation;
import com.artillexstudios.axcrates.crates.previews.impl.PreviewGui;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PlacedCrate {
    private final Location location;
    private final Crate crate;
    private Hologram hologram = null;
    private Animation animation = null;
    private PreviewGui previewGui;

    public PlacedCrate(@NotNull Location location, @NotNull Crate crate) {
        this.location = location;
        this.crate = crate;

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

        final File preview = new File(AxCrates.getInstance().getDataFolder(), "previews/" + crate.previewTemplate + ".yml");
        if (preview.exists()) {
            previewGui = new PreviewGui(new Config(preview), crate);
        } else previewGui = null;

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
        if (previewGui == null) return;
        previewGui.open(player);
    }

    public void tick() {
        if (animation == null) return;
        animation.play();
    }

    public void remove() {
        if (hologram == null) return;
        hologram.remove();
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
}
