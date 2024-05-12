package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HologramEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    
    public HologramEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Editor > &lEditing " + crate.displayName))
                .create());
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() { // TODO: reload holograms after actions
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        boolean enabled = crate.placedHologramEnabled;
        super.addInputBoolean(makeItem(
                        enabled ? Material.LIME_DYE : Material.GRAY_DYE,
                        "&#FF4400&lHologram Enabled",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + enabled
                ),
                enabled,
                bool -> {
                    crate.settings.set("placed.hologram.enabled", bool);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "4"
        );

        float offsetX = crate.placedHologramOffsetX;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lLocation Offset X",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", offsetX) + " blocks"
                ),
                offsetX,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.x", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "19"
        );

        float offsetY = crate.placedHologramOffsetY;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lLocation Offset Y",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", offsetY) + " blocks"
                ),
                offsetY,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.y", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "20"
        );

        float offsetZ = crate.placedHologramOffsetZ;
        super.addInputDouble(makeItem(
                        Material.BOOK,
                        "&#FF4400&lLocation Offset Z",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", offsetZ) + " blocks"
                ),
                offsetZ,
                num -> {
                    crate.settings.set("placed.hologram.location-offset.z", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        float lineHeight = crate.settings.getFloat("placed.hologram.line-height");
        super.addInputDouble(makeItem(
                        Material.LADDER,
                        "&#FF4400&lHologram Line Height",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + String.format("%.1f", lineHeight) + " blocks"
                ),
                lineHeight,
                num -> {
                    crate.settings.set("placed.hologram.line-height", num);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "21"
        );

        final List<String> lore = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Current value:"
        ));

        List<String> lines = crate.placedHologramLines;
        for (String str : lines) {
            lore.add("&f" + str);
        }
        super.addInputMultiText(makeItem(
                        Material.ANVIL,
                        "&#FF4400&lHologram Lines",
                        lore.toArray(new String[0])

                ),
                lines,
                strings -> {
                    crate.settings.set("placed.hologram.lines", strings);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "23"
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
