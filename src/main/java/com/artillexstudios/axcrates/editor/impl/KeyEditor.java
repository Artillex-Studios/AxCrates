package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.gui.SignInput;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.ContainerUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.KeyManager;
import com.artillexstudios.axcrates.utils.ItemUtils;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyEditor extends EditorBase {
    private final EditorBase lastGui;
    public KeyEditor(Player player, EditorBase lastGui) {
        super(player, Gui.paginated()
                .disableItemSwap()
                .pageSize(36)
                .rows(6)
                .title(StringUtils.format("&0Editor > &lKeys"))
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

        KeyManager.getKeys().forEach((key, value) -> {
            final List<String> lore = new ArrayList<>();
            if (value.item().getItemMeta() != null && value.item().getItemMeta().getLore() != null)
                lore.addAll(value.item().getItemMeta().getLore());
            lore.addAll(Arrays.asList("",
                    "&#DDDDDDɪᴅ: " + key,
                    "&#FF4400&l> &#FF4400Click &8- &#EE4400Get Key",
                    "&#FF4400&l> &#FF4400Shift + Left Click &8- &#EE4400Get Original Item",
                    "&#FF4400&l> &#FF4400Shift + Right Click &8- &#EE4400Delete Key"));
            final ItemStack item = new ItemBuilder(value.item().clone())
                    .setLore(lore)
                    .get();

            super.addCustom(item, event -> {
                if (event.isRightClick() && event.isShiftClick()) {
                    final File fl = new File(AxCrates.getInstance().getDataFolder(), "keys/" + key + ".yml");
                    fl.delete();
                    KeyManager.refresh();
                    open();
                    return;
                }

                if (event.isLeftClick() && event.isShiftClick()) {
                    ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(value.original()), player.getLocation());
                    return;
                }

                ContainerUtils.INSTANCE.addOrDrop(player.getInventory(), List.of(value.item()), player.getLocation());
            });
        });

        super.addCustom(makeItem(
                        Material.BELL,
                        "&#FF4400&lNew Key",
                        " ",
                        " &7- &fHold the item in your cursor",
                        " &7- &fand click to create a new key.",
                        " ",
                        "&#FF4400&l> &#FF4400Click &8- &#FF4400Create New Key"
                ),
                event -> {
                    if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) return;
                    final SignInput signGUI = new SignInput.Builder().setLines(StringUtils.formatList(List.of("",
                            "-----------",
                            "Write the name of",
                            "the new key!"))
                    ).setHandler((player1, result) -> {
                        String name = PlainTextComponentSerializer.plainText().serialize(result[0]);
                        if (name.isBlank()) return;
                        final Config config = new Config(new File(AxCrates.getInstance().getDataFolder(), "keys/" + name + ".yml"));
                        ItemUtils.saveItem(event.getCursor(), config, "item");
                        config.save();
                        KeyManager.refresh();
                        Scheduler.get().run(scheduledTask -> open());
                    }).build(player);
                    signGUI.open();
                },
                "4"
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
