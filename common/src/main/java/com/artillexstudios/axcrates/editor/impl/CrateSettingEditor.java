package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.Key;
import com.artillexstudios.axcrates.utils.DynamicLocation;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static com.artillexstudios.axcrates.AxCrates.MESSAGEUTILS;

public class CrateSettingEditor extends EditorBase {
    private final EditorBase lastGui;
    private final Crate crate;

    public CrateSettingEditor(Player player, EditorBase lastGui, Crate crate) {
        super(player, Gui.gui()
                .disableAllInteractions()
                .rows(6)
                .title(StringUtils.format("&0Editor > &lEditing " + crate.displayName))
                .create()
        );
        this.lastGui = lastGui;
        this.crate = crate;
    }

    public void open() {
        super.addFiller(makeItem(
                        Material.RED_STAINED_GLASS_PANE,
                        ""
                ),
                "0-8", "45-53"
        );

        super.addOpenMenu(makeItem(
                        Material.GOLD_INGOT,
                        "&#FF4400&lRewards"
                ),
                new RewardEditor(player, this, crate),
                "19"
        );

        super.addOpenMenu(makeItem(
                        Material.ARMOR_STAND,
                        "&#FF4400&lHologram"
                ),
                new HologramEditor(player, this, crate),
                "20"
        );

        final List<String> lore = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Currently:"
        ));

        Set<Key> lines = crate.keysAllowed;
        for (Key str : lines) {
            lore.add("&f" + str.name());
        }

        super.addOpenMenu(makeItem(
                        Material.TRIPWIRE_HOOK,
                        "&#FF4400&lAllowed Keys",
                        lore.toArray(new String[0])
                ),
                new AllowedKeyEditor(player, this, crate),
                "21"
        );

        final List<String> lore2 = new ArrayList<>(Arrays.asList(
                " ",
                "&#FF4400&l> &#FFCC00Currently:"
        ));

        List<DynamicLocation> lines2 = crate.placedLocations;
        for (DynamicLocation str : lines2) {
            lore2.add("&f" + DynamicLocation.serialize(str));
        }

        super.addInputMultiLocation(makeItem(
                        Material.CHEST,
                        "&#FF4400&lCrate Locations",
                        lore2.toArray(new String[0])
                ),
                crate.placedLocations,
                locations -> {
                    final ArrayList<String> locs = new ArrayList<>();
                    for (DynamicLocation location : locations) {
                        locs.add(DynamicLocation.serialize(location));
                    }
                    crate.settings.set("placed.locations", locs);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "22"
        );

        super.addOpenMenu(makeItem(
                        Material.BEACON,
                        "&#FF4400&lPlaced Particles"
                ),
                new ParticleEditor(player, this, crate),
                "23"
        );

        List<String> templates = new ArrayList<>(Stream.of(new File(AxCrates.getInstance().getDataFolder(), "previews").listFiles())
                .filter(file -> !file.isDirectory())
                .map(file -> file.getName().replace(".yml", "").replace(".yaml", ""))
                .toList());
        templates.add(0, "none");
        super.addInputEnum(makeItem(
                        Material.MAP,
                        "&#FF4400&lCrate Preview Template",
                        " ",
                        "&#FF4400&l> &#FFCC00Currently: &f" + crate.previewTemplate
                ),
                templates,
                crate.previewTemplate,
                string -> {
                    crate.settings.set("preview-template", string.equalsIgnoreCase("none") ? "NONE" : string);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "24"
        );

        String animation = crate.openAnimation;
        super.addInputEnum(makeItem(
                        Material.ENDER_EYE,
                        "&#FF4400&lCrate Opening Animation",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + animation.toLowerCase()
                ),
                Arrays.stream(Animation.Options.values()).map(Enum::name).toList(),
                animation,
                val -> {
                    crate.settings.set("open-animation", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "25"
        );

        String keyMode = crate.keyMode;
        super.addInputEnum(makeItem(
                        Material.ENDER_CHEST,
                        "&#FF4400&lKey Mode",
                        " ",
                        " &#AAAAAA- &#FFCC00crate: &fkey requires a crate to be placed in the world to open",
                        " &#AAAAAA- &#FFCC00lootbox: &fkey can be right-clicked anywhere to open the crate",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + keyMode
                ),
                List.of("crate", "lootbox"),
                keyMode,
                val -> {
                    crate.settings.set("key.mode", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "28"
        );

        Material material = crate.material;
        super.addInputText(makeItem(
                        Material.ITEM_FRAME,
                        "&#FF4400&lDisplay Material (Editor Only)",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + material.name().toLowerCase()
                ),
                "&#FF6600Write the new material: &#DDDDDD(write &#FF6600cancel &#DDDDDDto stop)",
                val -> {
                    Material validated = Material.matchMaterial(val);
                    if (validated == null) {
                        MESSAGEUTILS.sendLang(player, "editor.invalid-material");
                        open();
                        return;
                    }
                    crate.settings.set("material", validated.name());
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "29"
        );

        boolean placedKnockback = crate.placedKnockback;
        super.addInputBoolean(makeItem(
                        placedKnockback ? Material.SLIME_BALL : Material.MAGMA_CREAM,
                        "&#FF4400&lFail Knockback",
                        " ",
                        " &#AAAAAA- &fIf the player doesn't",
                        " &#AAAAAA- &fhave a key or if they are",
                        " &#AAAAAA- &fmissing an open requirement,",
                        " &#AAAAAA- &fshould they be knocked back?",
                        " ",
                        "&#FF4400&l> &#FFCC00Current value: &f" + placedKnockback
                ),
                placedKnockback,
                val -> {
                    crate.settings.set("placed.knockback", val);
                    crate.settings.save();
                    crate.reload();
                    open();
                },
                "30"
        );

        super.addOpenMenu(makeItem(
                        Material.DANGER_POTTERY_SHERD,
                        "&#FF4400&lCrate Texture",
                        " ",
                        " &#AAAAAA- &fCustomize look of crate blocks.",
                        " &#AAAAAA- &fRequires a supported model plugin."
                ),
                new TextureEditor(player, this, crate),
                "31"
        );

        super.addOpenMenu(makeItem(
                        Material.BARRIER,
                        "&#FF4400&lBack"
                ),
                lastGui,
                "49"
        );

        gui.open(player);
    }
}
