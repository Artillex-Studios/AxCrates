package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;
    private final ArrayList<String> tiers = new ArrayList<>();
    private int idx = 0;

    public RewardEditor(Player player, Config file, EditorBase lastGui, Crate crate) {
        super(player, file, Gui.paginated().disableItemSwap().rows(6).pageSize(36).title(StringUtils.format("&0Editor > &lEditing " + crate.name)).create());
        this.lastGui = lastGui;
        this.crate = crate;
        tiers.addAll(crate.getCrateRewards().getTiers().keySet());
    }

    public void open() {
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
                        "&#FF4400&l> &#FF4400Drag & Drop Item &8- &#FF4400Add New Reward",
                        "&#FF4400&l> &#FF4400Right Click &8- &#FF4400Add New Command"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;
                    if (event.isRightClick()) {
                        // todo: add command
                        return;
                    }

                    var d = file.getMapList("rewards." + tiers.get(idx));
                    d.add(Map.of(
                            "chance", 10.0f,
                            "items", List.of(ItemUtils.saveItem(event.getCursor()))
                    ));
                    event.getCursor().setAmount(0);
                    file.set("rewards." + tiers.get(idx), d);
                    file.save();
                    CrateManager.refresh();
                    new RewardEditor(player, file, lastGui, CrateManager.getCrate(crate.name)).open();
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

        crate.getCrateRewards().getTiers().get(tiers.get(idx)).getRewards().forEach((reward, chance) -> {
            ItemStack item = null;
            for (ItemStack it : reward.getItems()) {
                item = it.clone();
                break;
            }

            for (String it : reward.getCommands()) {
                item = new ItemBuilder(Material.COMMAND_BLOCK).setName("&#FFCC00/" + it).get();
                break;
            }

            if (item == null) item = new ItemBuilder(Material.BARRIER).setName("&#FFCC00No rewards").get();

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
            rewardGui.addItem(new GuiItem(item, event -> {
                if (event.getClick() == ClickType.DROP) {
                    // todo: delete
                    return;
                }
                if (event.isShiftClick() && event.isLeftClick()) {
                    // todo: move left
                    return;
                }
                if (event.isShiftClick() && event.isRightClick()) {
                    // todo: move right
                    return;
                }
                // todo: editor
            }));
        });

//
//        final ArrayList<String> lore2 = new ArrayList<>();
//        lore2.add(" ");
//        lore2.add("&7> &#FFCC00Click to remove!");
//
//        for (String cmd : koth.getSettings().getStringList("reward-commands")) {
//            final GuiItem guiItem = new GuiItem(new ItemBuilder(Material.PAPER).setName("&#FF6600/" + cmd).setLore(lore2).get());
//            rewardGui.addItem(guiItem);
//
//            guiItem.setAction(event -> {
//                final List<String> ar = koth.getSettings().getStringList("reward-commands");
//                ar.remove(cmd);
//                koth.getSettings().set("reward-commands", ar);
//                koth.getSettings().save();
//                rewardGui.removePageItem(guiItem);
//            });
//        }
//
//        final GuiItem addItem = new GuiItem(new ItemBuilder(Material.GOLD_INGOT).setName("&#FF6600&lClick here to add a new command!").get());
//        rewardGui.setItem(41, addItem);
//
//        addItem.setAction(event -> {
//            if (ClassUtils.INSTANCE.classExists("io.papermc.paper.threadedregions.RegionizedServer")) {
//                event.getWhoClicked().sendMessage(StringUtils.formatToString("&#FF0000This feature is not supported on folia, please edit manually in the koth's config!"));
//                return;
//            }
//            int page = rewardGui.getCurrentPageNum();
//            player.closeInventory();
//
//            final StringPrompt prompt = new StringPrompt() {
//                @NotNull
//                public String getPromptText(@NotNull ConversationContext context) {
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF6600Write the command here without the slash:"));
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#DDDDDDValid placeholder: %player%"));
//                    return "";
//                }
//
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    final List<String> ar = koth.getSettings().getStringList("reward-commands");
//                    ar.add(input);
//                    koth.getSettings().set("reward-commands", ar);
//                    koth.getSettings().save();
//
//                    open();
//                    for (int i = 1; i < page; i++) rewardGui.next();
//                    return END_OF_CONVERSATION;
//                }
//            };
//
//            final ConversationFactory cf = new ConversationFactory(AxKoth.getInstance());
//            cf.withFirstPrompt(prompt);
//            cf.withLocalEcho(true);
//            final Conversation conversation = cf.buildConversation(player);
//            conversation.begin();
//        });
//
//        final GuiItem previousItem = new GuiItem(new ItemBuilder(Material.ARROW).setName("&#FF6600&lPrevious").get());
//        rewardGui.setItem(37, previousItem);
//
//        final GuiItem nextItem = new GuiItem(new ItemBuilder(Material.ARROW).setName("&#FF6600&lNext").get());
//        rewardGui.setItem(43, nextItem);
//
//        final GuiItem backItem = new GuiItem(new ItemBuilder(Material.BARRIER).setName("&#FF6600&lBack").get());
//        rewardGui.setItem(39, backItem);
//
//        previousItem.setAction(event -> rewardGui.previous());
//        nextItem.setAction(event -> rewardGui.next());
//        backItem.setAction(event -> lastGui.open());
//
        gui.open(player);
    }
}
