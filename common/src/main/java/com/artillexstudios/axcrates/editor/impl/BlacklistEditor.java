package com.artillexstudios.axcrates.editor.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.blacklist.PlayerBlacklistManager;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.utils.ItemUtils;
import com.artillexstudios.axcrates.utils.SoundUtils;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

public class BlacklistEditor extends EditorBase {
    private final PlayerBlacklistManager blacklistManager = new PlayerBlacklistManager();
    private final Crate crate;
    private final Runnable backAction;
    private final Config previewSettings;

    public BlacklistEditor(Player player, Runnable backAction, Crate crate, Config previewSettings) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .rows(6)
                .pageSize(36)
                .title(StringUtils.format(previewSettings.getString("blacklist-editor.title", "&0Blacklist > %crate%")
                        .replace("%crate%", crate.displayName)))
                .create()
        );
        this.backAction = backAction;
        this.crate = crate;
        this.previewSettings = previewSettings;
    }

    @Override
    public void open() {
        final PaginatedGui pGui = (PaginatedGui) gui;
        pGui.clearPageItems();

        pGui.setDefaultTopClickAction(event -> event.setCancelled(true));

        super.addFiller(
                getItemOrDefault("blacklist-editor.filler", makeItem(Material.RED_STAINED_GLASS_PANE, "")),
                getSlotsFromConfig("blacklist-editor.filler", "0-8", "45-53")
        );

        super.addCustom(
            getItemOrDefault("blacklist-editor.back", makeItem(Material.ARROW, "&#FF4400&lBack")),
            event -> {
                playSound("blacklist-editor.back.sound");
                backAction.run();
            },
            getSlotsFromConfig("blacklist-editor.back", "45")
        );

        super.addCustom(
            getItemOrDefault("blacklist-editor.filter-info", makeItem(
                    Material.HOPPER,
                    "&#FF4400&lFilter Info",
                    " ",
                    "&#44FF44&l✔ &#DDDDDDAllowed &8- &#DDDDDDreward can be received",
                    "&#FF4444&l✖ &#DDDDDDBlacklisted &8- &#DDDDDDreward will be deleted",
                    " ",
                    "&#FFAA00Left Click &8- &#DDDDDDToggle status"
            )),
            event -> {
            },
            getSlotsFromConfig("blacklist-editor.filter-info", "49")
        );

        Set<String> blacklist = blacklistManager.getBlacklist(player, crate.name);

        for (CrateTier tier : crate.getCrateRewards().getTiers().values()) {
            for (CrateReward reward : tier.getRewards()) {
            boolean isBlacklisted = blacklist.contains(reward.getId());
                String formattedItemName = ItemUtils.getFormattedItemName(reward.getDisplay());

                final LinkedHashMap<String, String> replacements = new LinkedHashMap<>();
                replacements.put("%item%", formattedItemName);
                replacements.put("%crate%", crate.displayName);
                replacements.put("%tier%", tier.getName());
                replacements.put("%chance%", String.valueOf(reward.getChance()));

                ItemStack display = reward.getDisplay().clone();
                if (isBlacklisted
                        && previewSettings.getBoolean("blacklist-editor.blacklisted-item.enabled", true)
                        && previewSettings.getSection("blacklist-editor.blacklisted-item") != null) {
                    display = ItemBuilder.create(previewSettings.getSection("blacklist-editor.blacklisted-item"), replacements).get();
                }

                ItemMeta meta = display.getItemMeta();
                if (meta != null) {
                    String statusPrefix = isBlacklisted
                            ? previewSettings.getString("blacklist-editor.reward-name.blacklisted-prefix", "&#FF4444&l✖ ")
                            : previewSettings.getString("blacklist-editor.reward-name.allowed-prefix", "&#44FF44&l✔ ");

                    meta.setDisplayName(StringUtils.formatToString(applyReplacements(statusPrefix + "&f%item%", replacements)));

                    List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

                    List<String> statusLore = previewSettings.getStringList(
                            isBlacklisted ? "blacklist-editor.reward-lore.blacklisted" : "blacklist-editor.reward-lore.allowed"
                    );

                    if (statusLore == null || statusLore.isEmpty()) {
                        statusLore = isBlacklisted
                                ? List.of(" ", "&#FF4444Status: Blacklisted", "&#FFAA00Left Click &8- &#44FF44Allow this reward")
                                : List.of(" ", "&#44FF44Status: Allowed", "&#FFAA00Left Click &8- &#FF4444Blacklist this reward");
                    }

                    for (String line : statusLore) {
                        lore.add(StringUtils.formatToString(applyReplacements(line, replacements)));
                    }

                    meta.setLore(lore);
                    display.setItemMeta(meta);
                }

                final String rewardId = reward.getId();
                GuiItem guiItem = new GuiItem(display);
                guiItem.setAction(event -> {
                    blacklistManager.toggleBlacklist(player, crate.name, rewardId);
                    playSound(isBlacklisted ? "blacklist-editor.unblacklist.sound" : "blacklist-editor.blacklist.sound");
                    open();
                });
                pGui.addItem(guiItem);
            }
        }

        if (pGui.getPagesNum() > 1) {
            super.addCustom(
                getItemOrDefault("previous-page", makeItem(
                        Material.ARROW,
                        "&#FF4400&lPrevious Page",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Go to Previous Page"
                )),
                event -> {
                    playSound("previous-page.sound");
                    pGui.previous();
                },
                getSlotsFromConfig("previous-page", "47")
            );

            super.addCustom(
                getItemOrDefault("next-page", makeItem(
                        Material.ARROW,
                        "&#FF4400&lNext Page",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Go to Next Page"
                )),
                event -> {
                    playSound("next-page.sound");
                    pGui.next();
                },
                getSlotsFromConfig("next-page", "51")
            );
        }

        pGui.open(player);
    }

    private String applyReplacements(String text, Map<String, String> replacements) {
        String result = text;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private ItemStack getItemOrDefault(String route, ItemStack fallback) {
        if (previewSettings.getSection(route) == null) return fallback;
        return ItemBuilder.create(previewSettings.getSection(route)).get();
    }

    private String[] getSlotsFromConfig(String route, String... defaults) {
        List<String> slots = previewSettings.getStringList(route + ".slot");
        if (slots != null && !slots.isEmpty()) {
            return slots.toArray(String[]::new);
        }

        String singleSlot = previewSettings.getString(route + ".slot");
        if (singleSlot != null && !singleSlot.isBlank()) {
            return new String[]{singleSlot};
        }

        return defaults;
    }

    private void playSound(String route) {
        SoundUtils.playSound(player, previewSettings.getString(route, ""));
    }
}
