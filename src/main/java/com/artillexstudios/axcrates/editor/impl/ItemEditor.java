package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.NumberUtils;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.utils.ItemUtils;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    private final CrateReward reward;

    public ItemEditor(Player player, EditorBase lastGui, Crate crate, CrateReward reward) {
        super(player, Gui.gui().disableItemSwap().rows(6).title(StringUtils.format("&0Editor > &lSettings of " + ItemUtils.getFormattedItemName(reward.getDisplay()))).create());
        this.lastGui = lastGui;
        this.crate = crate;
        this.reward = reward;
    }

    public void open() {
        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        final ItemStack item = reward.getDisplay().clone();
        extendLore(item,
                " ",
                "&#FF4400&l> &#FF4400Click &8- &#FF4400Get Item",
                "&#FF4400&l> &#FF4400Click Whole Holding Item &8- &#FF4400Replace Display Item"
        );
        super.addCustom(item,
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                        ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(reward.getDisplay().clone()), player.getLocation());
                        return;
                    }

                    reward.setDisplay(event.getCursor());
                    event.getCursor().setAmount(0);
                    crate.getCrateRewards().save();
                    open();
                },
                "4"
        );

        super.addOpenMenu(makeItem(
                        Material.GOLD_INGOT,
                        "&#FF4400&lItem Rewards"
                ),
                new ItemRewardEditor(player, this, crate, reward),
                "22"
        );

        final List<String> lore = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Current commands:"
        ));

        for (String cmd : reward.getCommands()) {
            lore.add("&#DDDDDD/" + cmd);
        }

        super.addInputMultiText(makeItem(
                        Material.COMMAND_BLOCK,
                        "&#FF4400&lCommand Rewards",
                        lore.toArray(new String[0])
                ),
                reward.getCommands(),
                strings -> {
                    reward.setCommands(strings);
                    crate.getCrateRewards().save();
                    open();
                },
                "24"
        );

        super.addInputText(makeItem(
                        Material.DIAMOND,
                        "&#FF4400&lChance",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + reward.getChance() + "%"
                ),
                "&#FF6600Write the new chance: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)",
                current -> {
                    if (NumberUtils.isDouble(current)) {
                        reward.setChance(Double.parseDouble(current));
                        crate.getCrateRewards().save();
                    } else {
                        // todo: message, not a number
                    }
                    open();
                },
                "20"
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
