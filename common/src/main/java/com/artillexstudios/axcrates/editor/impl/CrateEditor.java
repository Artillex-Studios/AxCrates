package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.editor.EditorBase;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CrateEditor extends EditorBase { // todo: better description to all buttons
    private final EditorBase lastGui;
    public CrateEditor(Player player, EditorBase lastGui) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .pageSize(36)
                .rows(6)
                .title(StringUtils.format("&0Editor > &lCrates"))
                .create()
        );
        this.lastGui = lastGui;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        ((PaginatedGui) gui).clearPageItems();

        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        CrateManager.getCrates().forEach((key, value) -> {
            final ItemStack item = ItemBuilder.create(value.material)
                    .setName(value.displayName)
                    .setLore(Arrays.asList("",
                            "&#DDDDDDɪᴅ: " + key,
                            "&#FF4400&l> &#FF4400Click &8- &#FF4400Open Settings",
                            "&#FF4400&l> &#FF4400Shift + Right Click &8- &#EE4400Delete Crate"))
                    .get();

            super.addCustom(item, event -> {
                if (event.isRightClick() && event.isShiftClick()) {
                    new File(AxCrates.getInstance().getDataFolder(), "crates/" + key + ".yml").delete();
                    CrateManager.refresh();
                    open();
                    return;
                }

                new CrateSettingEditor(player, this, value).open();
            });
        });

        super.addCustom(makeItem(
                        Material.BELL,
                        "&#FF4400&lNew Crate",
                        " ",
                        " &7- &fIf you hold something in your cursor when clicking,",
                        " &7- &fits material will be used to display the crate.",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Create New Crate"
                ),
                event -> {
                    final SignInput signGUI = new SignInput.Builder().setLines(StringUtils.formatList(List.of("",
                            "-----------",
                            "Write the name of",
                            "the new crate!"))
                    ).setHandler((player1, result) -> {
                        String name = result[0];
                        if (name.isBlank()) return;
                        final Config config = new Config(new File(AxCrates.getInstance().getDataFolder(), "crates/" + name + ".yml"), AxCrates.getInstance().getResource("empty-crate.yml"));
                        config.set("name", "&#FF4400&l" + name + " &fCrate");
                        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                            config.set("material", event.getCursor().getType().name());
                            event.getCursor().setAmount(0);
                        }
                        config.save();
                        CrateManager.refresh();
                        Scheduler.get().run(scheduledTask -> open());
                    }).build(player);
                    signGUI.open();
                },
                "4"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBack"
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
                    ((PaginatedGui) gui).previous();
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
                    ((PaginatedGui) gui).next();
                },
                "51"
        );

        gui.open(player);
    }
}
