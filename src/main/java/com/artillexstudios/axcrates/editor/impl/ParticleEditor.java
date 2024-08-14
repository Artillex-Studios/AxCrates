package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ParticleEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public ParticleEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Editor > &lEditing " + crate.displayName))
                .create());
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        boolean enabled = crate.placedParticleEnabled;
        super.addInputBoolean(makeItem(
                        enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lParticles Enabled",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + enabled
                ),
                enabled,
                bool -> {
                    crate.settings.set("placed.particles.enabled", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "4"
        );

        String animation = crate.placedParticleAnimation;
        super.addInputEnum(makeItem(
                        Material.BEACON,
                        "&#FF4400&lAnimation Type",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + animation
                ),
                Arrays.stream(Animation.Options.values()).map(Enum::name).toList(),
                animation,
                val -> {
                    crate.settings.set("placed.particles.animation", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "19"
        );

        boolean reverse = crate.placedParticleReverse;
        super.addInputBoolean(makeItem(
                        reverse ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lParticles Reverse After End",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + reverse
                ),
                reverse,
                bool -> {
                    crate.settings.set("placed.particles.reverse", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        boolean backwards = crate.placedParticleBackwards;
        super.addInputBoolean(makeItem(
                        backwards ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lParticles Go In Backwards Direction",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + backwards
                ),
                backwards,
                bool -> {
                    crate.settings.set("placed.particles.backwards", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "23"
        );

        float speed = crate.placedParticleSpeed;
        super.addInputDouble(makeItem(
                        Material.SUGAR,
                        "&#FF4400&lParticle Playback Speed",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", speed) + "x"
                ),
                speed,
                num -> {
                    crate.settings.set("placed.particles.speed", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "25"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBack",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Back to the Main Menu"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
