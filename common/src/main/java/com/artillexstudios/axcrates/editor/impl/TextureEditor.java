package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.animation.placed.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class TextureEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public TextureEditor(Player player, EditorBase lastGui, Crate crate) {
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

        boolean enabled = crate.placedTextureEnabled;
        super.addInputBoolean(makeItem(
                        enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lTexture Support Enabled",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + enabled
                ),
                enabled,
                bool -> {
                    crate.settings.set("placed.texture.enabled", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "4"
        );

        String placedTextureMode = crate.placedTextureMode;
        super.addInputEnum(makeItem(
                        Material.BEACON,
                        "&#FF4400&lTexture Plugin",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + placedTextureMode.toLowerCase()
                ),
                List.of("modelengine"),
                placedTextureMode,
                val -> {
                    crate.settings.set("placed.texture.mode", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "19"
        );

        String model = crate.placedTextureModel;
        super.addInputText(makeItem(
                        Material.PAPER,
                        "&#FF4400&lModel Name",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + model
                ),
                "&#FF6600Write the name of the model here: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)",
                bool -> {
                    crate.settings.set("placed.texture.model", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        float rotation = crate.placedTextureRotation;
        super.addInputDouble(makeItem(
                        Material.ARMOR_STAND,
                        "&#FF4400&lModel Texture Rotation (degrees)",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", rotation)
                ),
                rotation,
                num -> {
                    crate.settings.set("placed.texture.rotation", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "23"
        );

        String openAnimation = crate.placedTextureOpenAnimation;
        super.addInputText(makeItem(
                        Material.GOLD_NUGGET,
                        "&#FF4400&lOpen Animation",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + (openAnimation.isBlank() ? "none" : openAnimation)
                ),
                "&#FF6600Write the name of the model here: &#DDDDDD(write &#FF6600none &#DDDDDDto disable, write &#FF6600cancel &#DDDDDDto stop)",
                val -> {
                    crate.settings.set("placed.texture.open-animation", val.equals("none") ? "" : val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "24"
        );

        String closeAnimation = crate.placedTextureCloseAnimation;
        super.addInputText(makeItem(
                        Material.IRON_NUGGET,
                        "&#FF4400&lOpen Animation",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + (closeAnimation.isBlank() ? "none" : openAnimation)
                ),
                "&#FF6600Write the name of the model here: &#DDDDDD(write &#FF6600none &#DDDDDDto disable, write &#FF6600cancel &#DDDDDDto stop)",
                val -> {
                    crate.settings.set("placed.texture.close-animation", val.equals("none") ? "" : val);
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
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Back to the Previous Menu"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
