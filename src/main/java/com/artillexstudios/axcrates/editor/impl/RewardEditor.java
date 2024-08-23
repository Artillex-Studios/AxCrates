package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private Crate crate;
    private int idx = 0;
    private CrateTier tier;

    public RewardEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .rows(6)
                .pageSize(36)
                .title(StringUtils.format("&0Editor > &lEditing " + crate.displayName))
                .create()
        );
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() { // TODO: set tier somehow
        if (idx > crate.getCrateRewards().getTiers().size() - 1) idx = 0;
        else if (idx < 0) idx = crate.getCrateRewards().getTiers().size() - 1;
        tier = new ArrayList<>(crate.getCrateRewards().getTiers().values()).get(idx);

        final PaginatedGui rewardGui = (PaginatedGui) gui;
        rewardGui.clearPageItems();

        rewardGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        int rollAmount = tier.getRollAmount();
        super.addInputInteger(makeItem(
                        Material.ANVIL,
                        "&#FF4400&lAmount of Rewards to Give at Once",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + rollAmount
                ),
                rollAmount,
                integer -> {
                    tier.setRollAmount(integer);
                    crate.getCrateRewards().save();
                    open();
                },
                "2"
        );

        super.addCustom(makeItem(
                        Material.BELL,
                        "&#FF4400&lTier",
                        " ",
                        "&#FF4400&l> &#FFCC00Selected: &f" + tier.getName(),
                        " ",
                        "&#FF4400&l> &#FF4400Left Click &8- &#FF4400Next Tier",
                        "&#FF4400&l> &#FF4400Right Click &8- &#FF4400Previous Tier",
                        "&#FF4400&l> &#FF4400Shift + Left Click &8- &#FF4400Create New Tier",
                        "&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF4400Delete Selected Tier"
                ),
                event -> {
                    if (event.isShiftClick() && event.isLeftClick()) {
                        final SignInput signGUI = new SignInput.Builder().setLines(StringUtils.formatList(List.of("",
                                "-----------",
                                "Write the name of",
                                "the new tier!"))
                        ).setHandler((player1, result) -> {
                            String name = PlainTextComponentSerializer.plainText().serialize(result[0]);
                            if (name.isBlank()) return;
                            crate.getCrateRewards().createNewTier(name);
                            crate.getCrateRewards().save();
                            Scheduler.get().run(scheduledTask -> open());
                        }).build(player);
                        signGUI.open();
                        return;
                    }

                    if (event.isShiftClick() && event.isRightClick()) {
                        crate.getCrateRewards().getTiers().get(tier.getName());
                        crate.getCrateRewards().save();
                        open();
                        return;
                    }

                    if (event.isLeftClick()) idx++;
                    else idx--;
                    open();
                },
                "4"
        );

        super.addCustom(makeItem(
                        Material.GOLD_INGOT,
                        "&#FF4400&lAdd Reward",
                        " ",
                        "&#FF4400&l> &#FF4400Left Click While Holding Item &8- &#FF4400Add New Item Reward",
                        "&#FF4400&l> &#FF4400Right Click While Holding Item &8- &#FF4400Add New Command Reward"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                        // todo: message: hold something on your cursor
                        return;
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
                                    tier.addRewardCommand(event.getCursor(), input); // todo: fix command reward adding
                                    crate.getCrateRewards().save();
                                }

                                open();
                                return END_OF_CONVERSATION;
                            }
                        });
                    } else {
                        tier.addRewardItem(event.getCursor());
                        event.getCursor().setAmount(0);
                        crate.getCrateRewards().save();
                        open();
                    }
                },
                "6"
        );

        int i = 0;
        for (CrateReward reward : tier.getRewards()) {
            final ItemStack item = reward.getDisplay().clone();

            extendLore(item,
                    "",
                    "&#FF4400&l> &#FFCC00Chance: &f" + reward.getChance() + "%",
                    "",
                    "&#FF4400&l> &#FF4400Click &8- &#FF4400Edit Reward",
                    "&#FF4400&l> &#FF4400Drop &8- &#FF4400Remove Reward",
                    "&#FF4400&l> &#FF4400Shift + Left Click &8- &#FF4400Move to the Left",
                    "&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF4400Move to the Right"
            );

            int id = i;
            rewardGui.addItem(new GuiItem(item, event -> {
                if (event.getClick() == ClickType.DROP) {
                    // delete
                    tier.getRewards().remove(reward);
                } else if (event.isShiftClick() && event.isLeftClick()) {
                    // move left
                    if (id == 0) return;
                    Collections.swap(tier.getRewards(), id, id - 1);
                } else if (event.isShiftClick() && event.isRightClick()) {
                    // move right
                    if (id == tier.getRewards().size() - 1) return;
                    Collections.swap(tier.getRewards(), id, id + 1);
                } else {
                    new ItemEditor(player, this, crate, reward).open();
                    return;
                }

                crate.getCrateRewards().save();
                open();
            }));
            i++;
        }

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBack",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Back to the Main Menu" // todo: change all of this to the right menu
                ),
                lastGui,
                "49"
        );

        super.addCustom(makeItem(
                        Material.ARROW,
                        "&#FF4400&lPrevious",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Previous Page"
                ),
                event -> {
                    rewardGui.previous();
                },
                "47"
        );

        super.addCustom(makeItem(
                        Material.ARROW,
                        "&#FF4400&lNext",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Next Page"
                ),
                event -> {
                    rewardGui.next();
                },
                "51"
        );

        gui.open(player);
    }
}
