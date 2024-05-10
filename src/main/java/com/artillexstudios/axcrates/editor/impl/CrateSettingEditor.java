package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class CrateSettingEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public CrateSettingEditor(Player player, Config file, EditorBase lastGui, Crate crate) {
        super(player, file, Gui.gui().disableAllInteractions().rows(6).title(StringUtils.format("&0Editor > &lEditing " + crate.name)).create());
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        super.addFiller(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        super.addInputOpenMenu(19,
                new RewardEditor(player, crate.settings, this, crate),
                Material.GOLD_INGOT,
                "&#FF4400&lRewards",
                Arrays.asList()
        );

        super.addInputOpenMenu(20,
                new HologramEditor(player, crate.settings, this, crate),
                Material.ARMOR_STAND,
                "&#FF4400&lHologram",
                Arrays.asList()
        );

        super.addInputCustom(49,
                Material.BARRIER,
                "&#FF4400&lBack",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Back to the Main Menu"
                ),
                event -> lastGui.open()
        );

        gui.open(player);
    }
}
