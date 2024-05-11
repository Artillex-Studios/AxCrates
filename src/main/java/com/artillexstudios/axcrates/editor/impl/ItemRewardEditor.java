package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.utils.ItemUtils;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemRewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;
    private CrateTier tier;
    private CrateReward reward;
    private final int rewardIdx;

    public ItemRewardEditor(Player player, Config file, EditorBase lastGui, Crate crate, CrateTier tier, int rewardIdx) {
        super(player, file, Gui.storage().disableItemSwap().rows(6).title(StringUtils.format("&0Editor > &lEditing " + crate.displayName)).create());
        this.lastGui = lastGui;
        this.crate = crate;
        this.tier = tier;
        this.rewardIdx = rewardIdx;
    }

    public void open() {
        reward = new LinkedList<>(tier.getRewards().keySet()).get(rewardIdx);
        gui.setDefaultTopClickAction(event -> {
            if (event.getSlot() < 9 || event.getSlot() > 44) {
                event.setCancelled(true);
                return;
            }
        });

        super.addFiller(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        // todo: save on exit
        gui.setCloseGuiAction(event -> {
            LinkedList<Map<Object, Object>> d = new LinkedList<>(crate.settings.getMapList("rewards." + tier.getName()));
            int n = 0;
            for (Map<Object, Object> str : d) {
                if (str.containsKey("roll-amount")) {
                    d.remove(n);
                    break;
                }
                n++;
            }

            LinkedList<Map<Object, Object>> d2 = new LinkedList<>();
            int slot = -1;
            for (ItemStack it : gui.getInventory()) {
                slot++;
                if (gui.getGuiItem(slot) != null) continue;
                if (it == null) continue;
                d2.add(ItemUtils.saveItem(it));
            }
            d.get(rewardIdx).put("items", d2);
            d.add(0, Map.of("roll-amount", tier.getRollAmount()));
            crate.settings.set("rewards." + tier.getName(), d);
            crate.settings.save();
            CrateManager.refresh();
        });

        super.addInputCustom(49,
                Material.BARRIER,
                "&#FF4400&lBack",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Back to the Main Menu"
                ),
                event -> lastGui.open()
        );

//        super.addInputCustom(47,
//                Material.ARROW,
//                "&#FF4400&lPrevious",
//                Arrays.asList(
//                        " ",
//                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Previous Page"
//                ),
//                event -> ((PaginatedGui) gui).previous()
//        );
//
//        super.addInputCustom(51,
//                Material.ARROW,
//                "&#FF4400&lNext",
//                Arrays.asList(
//                        " ",
//                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Next Page"
//                ),
//                event -> ((PaginatedGui) gui).next()
//        );

        gui.open(player);
        int n = 9;
        for (ItemStack it : reward.getItems()) {
            gui.getInventory().setItem(n, it);
            n++;
        }
    }
}
