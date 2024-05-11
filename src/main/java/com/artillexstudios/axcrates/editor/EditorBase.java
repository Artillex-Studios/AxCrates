package com.artillexstudios.axcrates.editor;

import com.artillexstudios.axapi.config.Config;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.ClassUtils;
import com.artillexstudios.axapi.utils.ItemBuilder;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditorBase {
    protected final Player player;
    protected final Config file;
    protected final BaseGui gui;

    public EditorBase(Player player, Config file, BaseGui gui) {
        this.player = player;
        this.gui = gui;
        this.file = file;
    }

    public void addInputInteger(int slot, String route, Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final Map<String, String> replacements = new HashMap<>();
        int original = file.getInt(route);
        replacements.put("{0}", "" + original);

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00+1");
        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000-1");
        lore.add("&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+10");
        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-10");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());

        List<String> finalLore = lore;
        guiItem.setAction(event -> {
            int current = file.getInt(route);

            if (!event.isShiftClick())
                if (event.isLeftClick()) file.set(route, current + 1);
                else file.set(route, current - 1);
            else
            if (event.isLeftClick()) file.set(route, current + 10);
            else file.set(route, current - 10);

            file.save();
            replacements.put("{0}", "" + file.getInt(route));
            guiItem.setItemStack(new ItemBuilder(material).setName(name, replacements).setLore(finalLore, replacements).get());
            gui.update();
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputFloat(int slot, String route, Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final Map<String, String> replacements = new HashMap<>();
        float original = file.getFloat(route);
        replacements.put("{0}", "" + original);

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00+0.1");
        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000-0.1");
        lore.add("&#FF4400&l> &#FF4400Shift + Left Click &8- &#00FF00+1");
        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#FF0000-1");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());

        List<String> finalLore = lore;
        guiItem.setAction(event -> {
            float current = file.getFloat(route);

            if (!event.isShiftClick())
                if (event.isLeftClick()) current = current + 0.1f;
                else current = current - 0.1f;
            else
            if (event.isLeftClick()) current = current + 1f;
            else current = current - 1f;

            file.set(route, Float.parseFloat(String.format("%.1f", current)));
            file.save();
            replacements.put("{0}", "" + file.getFloat(route));
            guiItem.setItemStack(new ItemBuilder(material).setName(name, replacements).setLore(finalLore, replacements).get());
            gui.update();
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputBoolean(int slot, String route, Material enabled, Material disabled, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final Map<String, String> replacements = new HashMap<>();
        boolean original = file.getBoolean(route);
        replacements.put("{0}", "" + original);

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Toggle");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(original ? enabled : disabled).setName(name, replacements).setLore(lore, replacements).get());

        List<String> finalLore = lore;
        guiItem.setAction(event -> {
            boolean current = file.getBoolean(route);

            file.set(route, !current);

            file.save();
            replacements.put("{0}", "" + file.getBoolean(route));
            guiItem.setItemStack(new ItemBuilder(!current ? enabled : disabled).setName(name, replacements).setLore(finalLore, replacements).get());
            gui.update();
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputOpenMenu(int slot, EditorBase menu, Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Open Menu");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());

        guiItem.setAction(event -> menu.open());

        gui.setItem(slot, guiItem);
    }

    public void addInputEmpty(int slot, Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
        gui.setItem(slot, guiItem);
    }

    public void addInputEnum(int slot, String route, List<String> values,  Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final Map<String, String> replacements = new HashMap<>();
        String original = file.getString(route);
        replacements.put("{0}", original);

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#FF4400Next Value");
        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF4400Previous Value");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());

        List<String> finalLore = lore;
        guiItem.setAction(event -> {
            String current = file.getString(route);

            int idx = values.indexOf(current);
            if (event.isLeftClick()) {
                if (idx + 1 >= values.size()) idx = 0;
                file.set(route, values.get(idx + 1));
            }
            else {
                if (idx - 1 < 0) idx = values.size() - 1;
                file.set(route, values.get(idx - 1));
            }

            file.save();
            replacements.put("{0}", file.getString(route));
            guiItem.setItemStack(new ItemBuilder(material).setName(name, replacements).setLore(finalLore, replacements).get());
            gui.update();
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputText(int slot, String route,  Material material, String name, List<String> lore) {
        lore = new ArrayList<>(lore);
        final Map<String, String> replacements = new HashMap<>();
        String original = file.getString(route);
        replacements.put("{0}", original);

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Click &8- &#FF4400Edit Text");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());

        guiItem.setAction(event -> {
            startConversation(event.getWhoClicked(), new StringPrompt() {
                @Override
                public String getPromptText(@NotNull ConversationContext context) {
                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF6600Write the new text: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)"));
                    return "";
                }
                @Override
                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                    assert input != null;
                    if (!input.equalsIgnoreCase("cancel")) {
                        file.set(route, input);
                        file.save();
                    }

                    open();
                    return END_OF_CONVERSATION;
                }
            });
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputMultiText(int slot, String route,  Material material, String name, List<String> l2) {
        List<String> original = new ArrayList<>(file.getStringList(route));

        List<String> lore = new ArrayList<>();
        for (String str : l2) {
            if (str.equals("{0}")) {
                for (String o : original) {
                    lore.add(StringUtils.formatToString(o));
                }
                continue;
            }
            lore.add(str);
        }

        lore.add(" ");
        lore.add("&#FF4400&l> &#FF4400Left Click &8- &#00FF00Add New Line");
        lore.add("&#FF4400&l> &#FF4400Right Click &8- &#FF0000Delete Last Line");
        lore.add("&#FF4400&l> &#FF4400Shift + Right Click &8- &#DD0000Clear All Lines");

        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());

        guiItem.setAction(event -> {
            if (event.isShiftClick() && event.isRightClick()) {
                file.set(route, new ArrayList<>());
                file.save();
                open();
                return;
            }

            if (event.isRightClick()) {
                if (original.isEmpty()) return;
                original.remove(original.size() - 1);
                file.set(route, original);
                file.save();
                open();
                return;
            }

            startConversation(event.getWhoClicked(), new StringPrompt() {
                @Override
                public String getPromptText(@NotNull ConversationContext context) {
                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF6600Write the new line: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)"));
                    return "";
                }
                @Override
                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
                    assert input != null;
                    if (!input.equalsIgnoreCase("cancel")) {
                        original.add(input);
                        file.set(route, original);
                        file.save();
                    }

                    open();
                    return END_OF_CONVERSATION;
                }
            });
        });

        gui.setItem(slot, guiItem);
    }

    public void addInputCustom(int slot, Material material, String name, List<String> lore, GuiAction<InventoryClickEvent> action) {
        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
        guiItem.setAction(action);

        gui.setItem(slot, guiItem);
    }

    public void addFiller(List<Integer> slots, Material material, String name, List<String> lore) {
        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name).setLore(lore).get());
        gui.setItem(slots, guiItem);
    }

//    public void addInputLocation(int slot, String route,  Material material, String name, List<String> lore) {
//        lore = new ArrayList<>(lore);
//        final Map<String, String> replacements = new HashMap<>();
//        final String strLoc = file.getString(route);
//        if (strLoc.isEmpty()) {
//            replacements.put("{0}", "---");
//            replacements.put("{1}", "---");
//            replacements.put("{2}", "---");
//            replacements.put("{3}", "---");
//        } else {
//            Location original = Serializers.LOCATION.deserialize(strLoc);
//            final DecimalFormat df = new DecimalFormat("#.##");
//            replacements.put("{0}", original.getWorld().getName());
//            replacements.put("{1}", df.format(original.getX()));
//            replacements.put("{2}", df.format(original.getY()));
//            replacements.put("{3}", df.format(original.getZ()));
//        }
//
//        lore.add(" ");
//        lore.add("&#FFEE00Click &7- &#FFEE00Edit Location");
//
//        final GuiItem guiItem = new GuiItem(new ItemBuilder(material).setName(name, replacements).setLore(lore, replacements).get());
//
//        guiItem.setAction(event -> {
//            if (ClassUtils.INSTANCE.classExists("io.papermc.paper.threadedregions.RegionizedServer")) {
//                event.getWhoClicked().sendMessage(StringUtils.formatToString("&#FF0000This feature is not supported on folia, please edit manually in the sumo's config!"));
//                return;
//            }
//            player.closeInventory();
//
//            final StringPrompt prompt = new StringPrompt() {
//                @NotNull
//                public String getPromptText(@NotNull ConversationContext context) {
//                    return "";
//                }
//
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (input.equalsIgnoreCase("DONE")) {
//                        file.set(route, Serializers.LOCATION.serialize(player.getLocation()));
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            };
//
//            startConversation(player, new StringPrompt() {
//                @NotNull
//                @Override
//                public String getPromptText(@NotNull ConversationContext context) {
//                    context.getForWhom().sendRawMessage(StringUtils.formatToString("&#FF4400Go to the location and write &#00FF00'DONE' &#FFEE00or write &#FF0000'CANCEL' &#FFEE00to stop"));
//                    return "";
//                }
//
//                @Nullable
//                @Override
//                public Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
//                    assert input != null;
//                    if (input.equalsIgnoreCase("DONE")) {
//                        file.set(route, Serializers.LOCATION.serialize(player.getLocation()));
//                        file.save();
//                    }
//
//                    open();
//                    return END_OF_CONVERSATION;
//                }
//            });
//        });
//
//        gui.setItem(slot, guiItem);
//    }

    public void open() {

    }

    public void startConversation(HumanEntity player, StringPrompt prompt) {
        Scheduler.get().executeAt(player.getLocation(), () -> {
//                if (ClassUtils.INSTANCE.classExists("io.papermc.paper.threadedregions.RegionizedServer")) {
//                    player.sendMessage(StringUtils.formatToString("&#FF0000This feature is not supported on folia, please edit manually in the config!"));
//                    return;
//                }
            player.closeInventory();

            final ConversationFactory cf = new ConversationFactory(AxCrates.getInstance());
            cf.withFirstPrompt(prompt);
            cf.withLocalEcho(true);
            final Conversation conversation = cf.buildConversation((Conversable) player);
            conversation.begin();
        });
    }
}
