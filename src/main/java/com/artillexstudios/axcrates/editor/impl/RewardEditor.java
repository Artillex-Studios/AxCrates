package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.utils.ItemUtils;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;
    private final ArrayList<String> tiers = new ArrayList<>();
    private int idx = 0;

    public RewardEditor(Player player, Config file, EditorBase lastGui, Crate crate) {
        super(player, file, Gui.paginated().disableItemSwap().rows(6).pageSize(36).title(StringUtils.format("&0Editor > &lEditing " + crate.name)).create());
        this.lastGui = lastGui;
        this.crate = crate;
        tiers.addAll(crate.getCrateRewards().getTiers().keySet());
    }

    public void open() {
        tiers.clear();
        tiers.addAll(crate.getCrateRewards().getTiers().keySet());

        final PaginatedGui rewardGui = (PaginatedGui) gui;
        rewardGui.clearPageItems();
        final HashMap<String, CrateTier> map = crate.getCrateRewards().getTiers();

        rewardGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(List.of(0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        super.addInputCustom(2,
                Material.ANVIL,
                "&#FF4400&lAmount of Rewards to Give at Once",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + map.get(tiers.get(idx)).getRollAmount(),
                        " ",
                        "&#FF4400&l> &#FF4400Left Click &8- &#00FF00+1",
                        "&#FF4400&l> &#FF4400Right Click &8- &#FF0000-1",
                        "&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+10",
                        "&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-10"
                ),
                event -> {
                    int current = map.get(tiers.get(idx)).getRollAmount();

                    if (!event.isShiftClick())
                        if (event.isLeftClick()) current = current + 1;
                        else current = current - 1;
                    else
                    if (event.isLeftClick()) current = current + 10;
                    else current = current - 10;
                    if (current < 1) current = 1;
                    map.get(tiers.get(idx)).setRollAmount(current);
                    var d = file.getMapList("rewards." + tiers.get(idx));
                    int n = 0;
                    for (Map<Object, Object> str : d) {
                        if (str.containsKey("roll-amount")) {
                            d.set(n, Map.of("roll-amount", current));
                        }
                        n++;
                    }
                    file.set("rewards." + tiers.get(idx), d);
                    file.save();
                    open();
                }
        );

        super.addInputCustom(4,
                Material.BELL,
                "&#FF4400&lTier",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FFCC00Selected: &f" + tiers.get(idx),
                        " ",
                        "&#FF4400&l> &#FF4400Left Click &8- &#FF4400Next Tier",
                        "&#FF4400&l> &#FF4400Right Click &8- &#FF4400Previous Tier",
                        "&#FF4400&l> &#FF4400Shift + Left Click &8- &#FF4400Create New Tier",
                        "&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF4400Delete Selected Tier"
                ),
                event -> {
                    if (event.isShiftClick() && event.isLeftClick()) {
                        final SignGUI signGUI = SignGUI.builder().setLines("", "-----------", "Write the name of", "the new tier!").setHandler((player1, result) -> List.of(SignGUIAction.runSync(AxCrates.getInstance(), () -> {
                            if (result.getLine(0).isBlank()) return;
                            crate.settings.set("rewards." + result.getLine(0), List.of(Map.of("roll-amount", 1)));
                            crate.settings.save();
                            CrateManager.refresh();
                            tiers.add(result.getLine(0));
                            new RewardEditor(player, file, lastGui, crate).open();
                        }))).build();
                        signGUI.open(player.getPlayer());
                        return;
                    }

                    if (event.isShiftClick() && event.isRightClick()) {
                        crate.settings.getBackingDocument().remove("rewards." + tiers.get(idx));
                        crate.settings.save();
                        CrateManager.refresh();
                        tiers.remove(tiers.get(idx));
                        idx = 0;
                        open();
                        return;
                    }

                    if (event.isLeftClick()) {
                        if (++idx >= tiers.size()) idx = 0;
                    }
                    else {
                        if (--idx < 0) idx = tiers.size() - 1;
                    }
                    open();
                }
        );

        super.addInputCustom(6,
                Material.GOLD_INGOT,
                "&#FF4400&lAdd Reward",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Left Click While Holding Item &8- &#FF4400Add New Item Reward",
                        "&#FF4400&l> &#FF4400Right Click While Holding Item &8- &#FF4400Add New Command Reward"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                        // todo: message: hold something on your cursor
                        return;
                    }
                    LinkedList<Map<Object, Object>> d = new LinkedList<>(file.getMapList("rewards." + tiers.get(idx)));
                    int n = 0;
                    for (Map<Object, Object> str : d) {
                        if (str.containsKey("roll-amount")) {
                            d.remove(n);
                            break;
                        }
                        n++;
                    }
                    if (event.isRightClick()) {
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
                                    d.add(Map.of(
                                            "chance", 10.0f,
                                            "display", ItemUtils.saveItem(event.getCursor()),
                                            "commands", List.of(input)
                                    ));
                                }

                                event.getCursor().setAmount(0);
                                d.add(0, Map.of("roll-amount", map.get(tiers.get(idx)).getRollAmount()));
                                file.set("rewards." + tiers.get(idx), d);
                                file.save();
                                CrateManager.refresh();
                                open();
                                return END_OF_CONVERSATION;
                            }
                        });
                    } else {
                        var item = ItemUtils.saveItem(event.getCursor());
                        d.add(Map.of(
                                "chance", 10.0f,
                                "display", item,
                                "items", List.of(item)
                        ));
                        event.getCursor().setAmount(0);
                        d.add(0, Map.of("roll-amount", map.get(tiers.get(idx)).getRollAmount()));
                        file.set("rewards." + tiers.get(idx), d);
                        file.save();
                        CrateManager.refresh();
                        open();
                    }
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

        AtomicInteger n = new AtomicInteger(-1);
        crate.getCrateRewards().getTiers().get(tiers.get(idx)).getRewards().forEach((reward, chance) -> {
            n.getAndIncrement();
            final ItemStack item = reward.getDisplay().clone();
//            for (ItemStack it : reward.getItems()) {
//                item = it.clone();
//                break;
//            }
//
//            for (String it : reward.getCommands()) {
//                item = new ItemBuilder(Material.COMMAND_BLOCK).setName("&#FFCC00/" + it).get();
//                break;
//            }

            final ArrayList<String> lore2 = new ArrayList<>();
            lore2.add(" ");
            lore2.add("&#FF4400&l> &#FFCC00Chance: &f" + chance + "%");
            lore2.add(" ");
            lore2.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Edit Reward");
            lore2.add("&#FF4400&l> &#FF4400Drop &8- &#FF4400Remove Reward");
            lore2.add("&#FF4400&l> &#FF4400Shift + Left Click &8- &#FF4400Move to the Left");
            lore2.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF4400Move to the Right");
            final ItemMeta meta = item.getItemMeta();
            final ArrayList<String> lore = new ArrayList<>();
            if (meta.getLore() != null)
                lore.addAll(meta.getLore());
            lore.addAll(lore2);

            meta.setLore(StringUtils.formatListToString(lore));
            item.setItemMeta(meta);
            final int num = n.get();
            rewardGui.addItem(new GuiItem(item, event -> {
                LinkedList<Map<Object, Object>> d = new LinkedList<>(crate.settings.getMapList("rewards." + tiers.get(idx)));
                int i = 0;
                for (Map<Object, Object> str : d) {
                    if (str.containsKey("roll-amount")) {
                        d.remove(i);
                        break;
                    }
                    i++;
                }
                if (event.getClick() == ClickType.DROP) {
                    // delete
                    d.remove(num);
                } else if (event.isShiftClick() && event.isLeftClick()) {
                    // move left
                    if (num == 0) return;
                    Collections.swap(d, num, num - 1);
                } else if (event.isShiftClick() && event.isRightClick()) {
                    // move right
                    if (num == d.size() - 1) return;
                    Collections.swap(d, num, num + 1);
                } else {
                    new ItemEditor(player, file, this, crate, map.get(tiers.get(idx)), reward, num).open();
                    return;
                }
                d.add(0, Map.of("roll-amount", map.get(tiers.get(idx)).getRollAmount()));
                crate.settings.set("rewards." + tiers.get(idx), d);
                crate.settings.save();
                CrateManager.refresh();
                open();
            }));
        });

        super.addInputCustom(47,
                Material.ARROW,
                "&#FF4400&lPrevious",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Previous Page"
                ),
                event -> ((PaginatedGui) gui).previous()
        );

        super.addInputCustom(51,
                Material.ARROW,
                "&#FF4400&lNext",
                Arrays.asList(
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Next Page"
                ),
                event -> ((PaginatedGui) gui).next()
        );

        gui.open(player);
    }
}
