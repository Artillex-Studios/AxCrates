package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.crates.CrateManager;
import com.artillexstudios.axcrates.editor.EditorBase;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CrateEditor extends EditorBase {
    private final EditorBase lastGui;
    public CrateEditor(Player player, Config file, EditorBase lastGui) {
        super(player, file, Gui.paginated().disableItemSwap().pageSize(36).rows(6).title(StringUtils.format("&0Editor > &lCrates")).create());
        this.lastGui = lastGui;
    }

    public void open() {
        super.addFiller(List.of(0, 1, 2, 3, 5, 6, 7, 8, 45, 46, 47, 48, 50, 51, 52, 53),
                Material.RED_STAINED_GLASS_PANE,
                " ",
                Arrays.asList()
        );

        ((PaginatedGui) gui).clearPageItems();

        gui.setDefaultTopClickAction(event -> event.setCancelled(true));

        CrateManager.getCrates().forEach((key, value) -> {
            final ItemStack item = new ItemBuilder(value.material)
                    .setName(value.displayName)
                    .setLore(Arrays.asList("",
                            "&#DDDDDDɪᴅ: " + key,
                            "&#FF4400&l> &#FF4400Click &8- &#FF4400Open Settings",
                            "&#FF4400&l> &#FF4400Shift + Right Click &8- &#EE4400Delete Crate"))
                    .get();

            gui.addItem(new GuiItem(item, event -> {
                if (event.isRightClick() && event.isShiftClick()) {
                    final File fl = new File(AxCrates.getInstance().getDataFolder(), "crates/" + key + ".yml");
                    fl.delete();
                    CrateManager.refresh();
                    open();
                    return;
                }

                new CrateSettingEditor(player, value.settings, this, value).open();
            }));
        });

        super.addInputCustom(4,
                Material.BELL,
                "&#FF4400&lNew Crate",
                Arrays.asList(
                        " ",
                        " &7- &fIf you hold something in your cursor when clicking,",
                        " &7- &fits material will be used to display the crate.",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Create New Crate"
                ),
                event -> {
                    final SignGUI signGUI = SignGUI.builder().setLines("", "-----------", "Write the name of", "the new crate!").setHandler((player1, result) -> List.of(SignGUIAction.runSync(AxCrates.getInstance(), () -> {
                        if (result.getLine(0).isBlank()) return;
                        final Config config = new Config(new File(AxCrates.getInstance().getDataFolder(), "crates/" + result.getLine(0) + ".yml"), AxCrates.getInstance().getResource("empty-crate.yml"));
                        config.set("name", "&#FF4400" + result.getLine(0) + " &fCrate");
                        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
                            config.set("material", event.getCursor().getType().name());
                            event.getCursor().setAmount(0);
                        }
                        config.save();
                        CrateManager.refresh();
                        open();
                    }))).build();
                    signGUI.open(player.getPlayer());
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
