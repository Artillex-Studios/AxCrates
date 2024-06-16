package com.artillexstudios.axcrates.editor.impl;

import com.artillexstudios.axapi.serializers.Serializers;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.AxCrates;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.editor.EditorBase;
import com.artillexstudios.axcrates.keys.Key;
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

        List<Location> lines2 = crate.placedLocations;
        for (Location str : lines2) {
            lore2.add("&f" + Serializers.LOCATION.serialize(str));
        }

        super.addInputMultiLocation(makeItem(
                        Material.CHEST,
                        "&#FF4400&lCrate Locations",
                        lore2.toArray(new String[0])
                ),
                crate.placedLocations,
                locations -> {
                    final ArrayList<String> locs = new ArrayList<>();
                    for (Location location : locations) {
                        locs.add(Serializers.LOCATION.serialize(location));
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

        super.addInputEnum(makeItem(
                        Material.MAP,
                        "&#FF4400&lCrate Preview Template",
                        " ",
                        "&#FF4400&l> &#FFCC00Currently: &f" + crate.previewTemplate
                ),
                Stream.of(new File(AxCrates.getInstance().getDataFolder(), "previews").listFiles())
                        .filter(file -> !file.isDirectory())
                        .map(file -> file.getName().replace(".yml", "").replace(".yaml", ""))
                        .toList(),
                crate.previewTemplate,
                string -> {
                    crate.settings.set("preview-template", string);
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
                        "&#FF4400&l> &#FFCC00Current value: &f" + animation
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
