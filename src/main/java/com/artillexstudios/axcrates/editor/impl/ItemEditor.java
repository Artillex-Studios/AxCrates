package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.utils.ItemUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;
    private CrateTier tier;
    private CrateReward reward;
    private final int rewardIdx;

    public ItemEditor(Player player, Config file, EditorBase lastGui, Crate crate, CrateTier tier, CrateReward reward, int rewardIdx) {
        super(player, file, Gui.gui().disableItemSwap().rows(6).title(StringUtils.format("&0Editor > &lSettings of " + ItemUtils.getFormattedItemName(reward.getDisplay()))).create());
        this.lastGui = lastGui;
        this.crate = crate;
        this.tier = tier;
        this.reward = reward;
        this.rewardIdx = rewardIdx;
    }

    public void open() {
        reward = new LinkedList<>(tier.getRewards().keySet()).get(rewardIdx);
        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(List.of(0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        final ItemStack item = reward.getDisplay().clone();
        final ItemMeta meta = item.getItemMeta();
        var ar = Arrays.asList(
                " ",
                "&#FF4400&l> &#FF4400Click &8- &#FF4400Get Item",
                "&#FF4400&l> &#FF4400Click Whole Holding Item &8- &#FF4400Replace Display Item"
                );
        ArrayList<String> lore = new ArrayList<>();
        if (meta.getLore() != null)
            lore.addAll(meta.getLore());
        lore.addAll(StringUtils.formatListToString(ar));
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(4, new GuiItem(item, event -> {
            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(reward.getDisplay().clone()), player.getLocation());
                return;
            }
            LinkedList<Map<Object, Object>> d = new LinkedList<>(file.getMapList("rewards." + tier.getName()));
            int n = 0;
            for (Map<Object, Object> str : d) {
                if (str.containsKey("roll-amount")) {
                    d.remove(n);
                    break;
                }
                n++;
            }
            d.get(rewardIdx).put("display", ItemUtils.saveItem(event.getCursor()));
            d.add(0, Map.of("roll-amount", tier.getRollAmount()));
            event.getCursor().setAmount(0);
            file.set("rewards." + tier.getName(), d);
            file.save();
            CrateManager.refresh();
            open();
        }));

        super.addInputOpenMenu(22,
                new ItemRewardEditor(player, file, this, crate, tier, rewardIdx),
                Material.GOLD_INGOT,
                "&#FF4400&lItem Rewards",
                Arrays.asList()
        );

        LinkedList<Map<Object, Object>> d2 = new LinkedList<>(crate.settings.getMapList("rewards." + tier.getName()));
        int n = 0;
        for (Map<Object, Object> str : d2) {
            if (str.containsKey("roll-amount")) {
                d2.remove(n);
                break;
            }
            n++;
        }

        LinkedList<String> original = new LinkedList<>();
        if (d2.get(rewardIdx).containsKey("commands"))
            original.addAll((List<String>) d2.get(rewardIdx).get("commands"));

        var l2 = Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Current commands:",
                "{0}"
        );

        List<String> lore2 = new ArrayList<>();
        for (String str : l2) {
            if (str.equals("{0}")) {
                for (String o : original) {
                    lore2.add(StringUtils.formatToString("&#DDDDDD" + o));
                }
                continue;
            }
            lore2.add(str);
        }

        lore2.add(" ");
        lore2.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00Add New Command");
        lore2.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000Delete Last Commands");
        lore2.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#DD0000Clear All Commands");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(Material.COMMAND_BLOCK).setName("&#FF4400&lCommand Rewards").setLore(lore2).get());
        gui.setItem(24, guiItem);

        guiItem.setAction(event -> {
            if (event.isShiftClick() && event.isRightClick()) {
                d2.get(rewardIdx).put("commands", List.of());
                d2.add(0, Map.of("roll-amount", tier.getRollAmount()));
                crate.settings.set("rewards." + tier.getName(), d2);
                crate.settings.save();
                CrateManager.refresh();
                open();
                return;
            }

            if (event.isRightClick()) {
                if (original.isEmpty()) return;
                original.remove(original.size() - 1);
                d2.get(rewardIdx).put("commands", original);
                d2.add(0, Map.of("roll-amount", tier.getRollAmount()));
                crate.settings.set("rewards." + tier.getName(), d2);
                crate.settings.save();
                CrateManager.refresh();
                open();
                return;
            }

            startConversation(event.getWhoClicked(), new StringPrompt() {
                @Override
                public String getPromptText(@NotNull ConversationContext context) {
                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF4400Write the command here without the /slash: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)"));
                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#DDDDDD(use %player% as a placeholder for the player)"));
                    return "";
                }
                @Override
                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                    assert input != null;
                    if (!input.equalsIgnoreCase("cancel")) {
                        original.add(input);
                        d2.get(rewardIdx).put("commands", original);
                        d2.add(0, Map.of("roll-amount", tier.getRollAmount()));
                        crate.settings.set("rewards." + tier.getName(), d2);
                        crate.settings.save();
                        CrateManager.refresh();
                    }

                    open();
                    return END_OF_CONVERSATION;
                }
            });
        });

        super.addInputCustom(20,
                Material.DIAMOND,
                "&#FF4400&lChance",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + reward.getChance() + "%",
                        " ",
                        "&#FF4400&l> &#FF4400Left Click &8- &#00FF00+1",
                        "&#FF4400&l> &#FF4400Right Click &8- &#FF0000-1",
                        "&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+10",
                        "&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-10"
                ),
                event -> {
                    double current = reward.getChance();

                    if (!event.isShiftClick())
                        if (event.isLeftClick()) current = current + 1;
                        else current = current - 1;
                    else
                    if (event.isLeftClick()) current = current + 10;
                    else current = current - 10;
                    if (current < 1) current = 1;

                    LinkedList<Map<Object, Object>> d = new LinkedList<>(file.getMapList("rewards." + tier.getName()));
                    int n2 = 0;
                    for (Map<Object, Object> str : d) {
                        if (str.containsKey("roll-amount")) {
                            d.remove(n2);
                            break;
                        }
                        n2++;
                    }
                    d.get(rewardIdx).put("chance", current);
                    d.add(0, Map.of("roll-amount", tier.getRollAmount()));
                    event.getCursor().setAmount(0);
                    file.set("rewards." + tier.getName(), d);
                    file.save();
                    CrateManager.refresh();
                    open();
                }
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
