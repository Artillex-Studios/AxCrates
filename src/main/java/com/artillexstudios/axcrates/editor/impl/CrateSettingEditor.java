package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CrateSettingEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public CrateSettingEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui().disableAllInteractions().rows(6).title(StringUtils.format("&0Editor > &lEditing " + crate.name)).create());
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

        super.addOpenMenu(makeItem(
                        Material.CHEST,
                        "&#FF4400&lRewards"
                ),
                new RewardEditor(player, this, crate),
                "19"
        );

        super.addOpenMenu(makeItem(
                        Material.ARMOR_STAND,
                        "&#FF4400&lHologram"
                ),
                new HologramEditor(player, this, crate),
                "20"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBack"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
