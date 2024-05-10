package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.commands.MainCommand;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class MainEditor extends EditorBase {
    public MainEditor(Player player, Config file) {
        super(player, file, Gui.gui().disableAllInteractions().rows(6).title(StringUtils.format("&0Editor > &lMain")).create());
    }

    public void open() {
        super.addFiller(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        super.addInputOpenMenu(20,
                new CrateEditor(player, file, this),
                Material.CHEST,
        "&#FF4400&lCrates",
                Arrays.asList()
        );

        super.addInputOpenMenu(24,
                new KeyEditor(player, file, this),
                Material.RED_CANDLE,
                "&#FF4400&lKeys",
                Arrays.asList()
        );

        super.addInputCustom(31,
                Material.LIME_DYE,
                "&#FF4400&lReload",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Reload Plugin"
                ),
                event -> {
                    player.closeInventory();
                    new MainCommand().reload(player);
                }
        );

        super.addInputCustom(49,
                Material.BARRIER,
                "&#FF4400&lClose",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Close Inventory"
                ),
                event -> player.closeInventory()
        );

//        super.addInputOpenMenu(21,
//                new RewardEditor(player, file, this),
//                Material.GOLD_INGOT,
//                "&#FF6600&lRewards",
//                Arrays.asList()
//        );
//
//        super.addInputOpenMenu(38,
//                new HologramEditor(player, file, this),
//                Material.ARMOR_STAND,
//                "&#FF6600&lHologram Editor",
//                Arrays.asList()
//        );

        gui.open(player);
    }
}
