package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.MainCommand;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainEditor extends EditorBase {
    public MainEditor(Player player) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Editor > &lMain"))
                .create()
        );
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
                        "&#FF4400&lCrates"
                ),
                new CrateEditor(player, this),
                "20"
        );

        super.addOpenMenu(makeItem(
                        Material.RED_CANDLE,
                        "&#FF4400&lKeys"
                ),
                new KeyEditor(player, this),
                "24"
        );

        super.addCustom(makeItem(
                        Material.LIME_DYE,
                        "&#FF4400&lReload"
                ),
                event -> {
                    player.closeInventory();
                    new MainCommand().reload(player);
                },
                "22"
        );

        super.addCustom(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lClose",
                        "",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Close Inventory"
                ),
                event -> {
                    player.closeInventory();
                },
                "49"
        );

        gui.open(player);
    }
}
