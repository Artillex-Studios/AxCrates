package com.artillexstudios.axcrates.crates.previews.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.items.component.DataComponents;
import com.artillexstudios.axapi.items.component.type.ItemLore;
import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.blacklist.PlayerBlacklistManager;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.previews.GuiFrame;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.crates.rewards.CrateTier;
import com.artillexstudios.axcrates.editor.impl.BlacklistEditor;
import com.artillexstudios.axcrates.utils.ItemUtils;
import com.artillexstudios.axcrates.utils.SoundUtils;

import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;

public class PreviewGui extends GuiFrame {
    private final PaginatedGui previewGui;
    private final Crate crate;
    private final PlayerBlacklistManager blacklistManager = new PlayerBlacklistManager();

    public PreviewGui(Config settings, Crate crate) {
        super(settings);
        this.crate = crate;
        this.previewGui = Gui.paginated()
            .disableItemSwap()
                .title(StringUtils.format(settings.getString("title")
                        .replace("%crate%", crate.displayName)))
                .pageSize(settings.getInt("page-size", 36))
                .rows(settings.getInt("rows", 6))
                .create();
        setGui(previewGui);
    }

    public void update(Player player) {
        previewGui.setDefaultTopClickAction(event -> event.setCancelled(true));
        super.createItem("filler");

        previewGui.clearPageItems();
        if (previewGui.getPagesNum() > 1) {
            super.createItem("previous-page", event -> {
                SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("previous-page.sound"));
            previewGui.previous();
            });

            super.createItem("next-page", event -> {
                SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("next-page.sound"));
            previewGui.next();
            });
        }

        super.createItem("close", event -> {
            SoundUtils.playSound((Player) event.getWhoClicked(), file.getString("close.sound"));
            event.getWhoClicked().closeInventory();
        });

        super.createItem("hopper", event -> {
            Player clicker = (Player) event.getWhoClicked();
            SoundUtils.playSound(clicker, file.getString("hopper.sound"));
            Scheduler.get().run(scheduledTask -> new BlacklistEditor(clicker, () -> open(clicker), crate, file).open());
        });

        final boolean replaceBlacklistedInPreview = file.getBoolean("blacklist-editor.blacklisted-item.enabled", true);

        final Section blacklistedSection = file.getSection("blacklist-editor.blacklisted-item");

        int rewardIndex = 0;
        for (CrateTier tier : crate.getCrateRewards().getTiers().values()) {
            for (CrateReward reward : tier.getRewards()) {
                ItemStack display = reward.getDisplay().clone();

                if (player != null
                        && replaceBlacklistedInPreview
                        && blacklistedSection != null
                        && blacklistManager.isBlacklisted(player, crate.name, rewardIndex)) {
                    Map<String, String> replacements = Map.of(
                            "%item%", ItemUtils.getFormattedItemName(reward.getDisplay()),
                            "%crate%", crate.displayName,
                            "%tier%", tier.getName(),
                            "%chance%", String.valueOf(reward.getChance())
                    );
                    display = ItemBuilder.create(blacklistedSection, replacements).get();
                }

                previewGui.addItem(new GuiItem(makeReward(display, tier, reward)));
                rewardIndex++;
            }
        }
    }

    public void open(Player player) {
        open(1, player);
    }

    public void open(int page, Player player) {
        update(player);
        previewGui.open(player, page);
    }

    public ItemStack makeReward(ItemStack it, CrateTier tier, CrateReward reward) {
        List<String> loreText = file.getStringList("reward");
        List<Component> lore = new ArrayList<>();

        WrappedItemStack wrap = WrappedItemStack.wrap(it);

        Map<String, String> replacements = Map.of(
                "%chance%", "" + reward.getChance(),
                "%tier%", tier.getName()
        );

        for (String line : loreText) {
            if (line.equals("%lore%")) {
                lore.addAll(wrap.get(DataComponents.lore()).lines());
            } else {
                lore.add(StringUtils.format(line, replacements));
            }
        }

        wrap.set(DataComponents.lore(), new ItemLore(lore));

        return wrap.toBukkit();
    }
}
