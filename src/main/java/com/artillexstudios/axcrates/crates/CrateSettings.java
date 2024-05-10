package com.artillexstudios.axcrates.crates;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.keys.KeyManager;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CrateSettings {
    public final Config settings;
    public String displayName;
    public Material material;
    public boolean placedTextureEnabled;
    public String placedTextureType;
    public boolean placedHologramEnabled;
    public float placedHologramOffsetX;
    public float placedHologramOffsetY;
    public float placedHologramOffsetZ;
    public float placedHologramLineHeight;
    public List<String> placedHologramLines;
    public String placedParticleAnimation;
    public String placedParticleParticle;
    public boolean placedKnockback;
    public List<Location> placedLocations;
    public List<String> openRequirements;
    public List<String> openActions;
    public String openAnimation;
    public String keyMode;
    public List<Key> keyAllowed;

    public CrateSettings(Config settings) {
        this.settings = settings;
        reload();
    }

    protected void refreshSettings() {
        settings.reload();
        reload();
    }

    private void reload() {
        displayName = settings.getString("name");
        material = Material.matchMaterial(settings.getString("material"));
        placedTextureEnabled = settings.getBoolean("placed.texture.enabled");
        placedTextureType = settings.getString("placed.texture.type");
        placedHologramEnabled = settings.getBoolean("placed.hologram.enabled");
        placedHologramOffsetX = settings.getFloat("placed.hologram.location-offset.x");
        placedHologramOffsetY = settings.getFloat("placed.hologram.location-offset.y");
        placedHologramOffsetZ = settings.getFloat("placed.hologram.location-offset.z");
        placedHologramLineHeight = settings.getFloat("placed.hologram.line-height");
        placedHologramLines = settings.getStringList("placed.hologram.lines");
        placedParticleAnimation = settings.getString("placed.particles.animation");
        placedParticleParticle = settings.getString("placed.particles.particles");
        placedKnockback = settings.getBoolean("placed.knockback");
        placedLocations = settings.getStringList("placed.locations").stream().map(Serializers.LOCATION::deserialize).toList();
        openRequirements = settings.getStringList("open-requirements", new ArrayList<>());
        openActions = settings.getStringList("open-actions", new ArrayList<>());
        openAnimation = settings.getString("open-animation");
        keyMode = settings.getString("item.mode");
        keyAllowed = settings.getStringList("item.allowed").stream().map(KeyManager::getKey).toList();
    }
}
