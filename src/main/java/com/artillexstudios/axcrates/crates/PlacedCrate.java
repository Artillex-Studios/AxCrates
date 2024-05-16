package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axapi.utils.placeholder.StaticPlaceholder;
import com.artillexstudios.axcrates.crates.placedanimation.Animation;
import com.artillexstudios.axcrates.crates.placedanimation.BeamsAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.ChainsAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.HaloAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.SimpleAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.SphereAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.DoubleSpiralAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.SpiralAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.TornadoAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.ConeAnimation;
import com.artillexstudios.axcrates.crates.placedanimation.VortexAnimation;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class PlacedCrate {
    private final Location location;
    private final Crate crate;
    private Hologram hologram = null;
    private Animation animation = null;

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

        if (crate.placedParticleEnabled) {
            final Location particleLoc = location.clone();
            particleLoc.add(0.5, 0.5, 0.5);
            String[] anim = crate.placedParticleAnimation.split("-");
            animation = switch (anim[0]) {
                case "vanilla" -> new SimpleAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "spiral" -> new SpiralAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "doublespiral" -> new DoubleSpiralAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "tornado" -> new TornadoAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "chains" -> new ChainsAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "sphere" -> new SphereAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "beams" -> new BeamsAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "halo" -> new HaloAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "cone" -> new ConeAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                case "vortex" -> new VortexAnimation(particleLoc, crate.placedParticleParticle, anim.length == 2);
                default -> throw new IllegalStateException("Animation does not exist: " + crate.placedParticleAnimation);
            };
        }
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
