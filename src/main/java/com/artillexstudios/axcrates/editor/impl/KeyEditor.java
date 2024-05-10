package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.ItemUtils;
import de.rapha149.signgui.SignGUI;
import de.rapha149.signgui.SignGUIAction;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyEditor extends EditorBase {
    private final EditorBase lastGui;
    public KeyEditor(Player player, Config file, EditorBase lastGui) {
        super(player, file, Gui.paginated().disableItemSwap().pageSize(36).rows(6).title(StringUtils.format("&0Editor > &lKeys")).create());
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

        KeyManager.getKeys().forEach((key, value) -> {
            final List<String> lore = new ArrayList<>();
            if (value.item().getItemMeta() != null && value.item().getItemMeta().getLore() != null)
                lore.addAll(value.item().getItemMeta().getLore());
            lore.addAll(Arrays.asList("",
                    "&#DDDDDDɪᴅ: " + key,
                    "&#FF4400&l> &#FF4400Click &8- &#EE4400Get Key",
                    "&#FF4400&l> &#FF4400Shift + Right Click &8- &#EE4400Delete Key"));
            final ItemStack item = new ItemBuilder(value.item().clone())
                    .setLore(lore)
                    .get();

            gui.addItem(new GuiItem(item, event -> {
                if (event.isRightClick() && event.isShiftClick()) {
                    final File fl = new File(AxCrates.getInstance().getDataFolder(), "keys/" + key + ".yml");
                    fl.delete();
                    KeyManager.refresh();
                    open();
                    return;
                }
                ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(value.item()), player.getLocation());
            }));
        });

        super.addInputCustom(4,
                Material.BELL,
                "&#FF4400&lNew Key",
                Arrays.asList(
                        " ",
                        " &7- &fHold the item in your cursor",
                        " &7- &fand click to create a new key.",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Create New Key"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;
                    final SignGUI signGUI = SignGUI.builder().setLines("", "-----------", "Write the name of", "the new key!").setHandler((player1, result) -> List.of(SignGUIAction.runSync(AxCrates.getInstance(), () -> {
                        if (result.getLine(0).isBlank()) return;
                        final Config config = new Config(new File(AxCrates.getInstance().getDataFolder(), "keys/" + result.getLine(0) + ".yml"));
                        ItemUtils.saveItem(event.getCursor(), config, "item");
                        config.save();
                        KeyManager.refresh();
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
