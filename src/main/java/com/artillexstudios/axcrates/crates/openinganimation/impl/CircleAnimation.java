package com.artillexstudios.axcrates.crates.openinganimation.impl;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.crates.openinganimation.Animation;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class CircleAnimation extends Animation {
    private final ArrayList<Hologram> entities = new ArrayList<>();

    public CircleAnimation(List<CrateReward> rewards, Crate crate, Location location) {
        super(100, rewards, crate, location);

        for (CrateReward reward : rewards) {
            Hologram hologram = new Hologram(location, location.toString(), 0.3);
            hologram.addLine(StringUtils.formatToString(ItemUtils.getFormattedItemName(reward.getDisplay())), HologramLine.Type.TEXT);
            hologram.addLine(WrappedItemStack.wrap(reward.getDisplay()).toSNBT(), HologramLine.Type.ITEM_STACK);
            entities.add(hologram);
        }
    }

    @Override
    protected void run() {
        if (frame % 3 != 0) return;
        double radius = 5;
        double angle = frame * 3;
        for (int i = 0; i < entities.size(); i++) {
            final Location loc = location.clone();
            double x, z;
            x = Math.cos(Math.PI * 2 * angle / 360) * radius;
            z = Math.sin(Math.PI * 2 * angle / 360) * radius;
            loc.add(x, 4f, z);
            entities.get(i).teleport(loc);
            angle += 360D / entities.size();
        }
    }

    protected void end() {
        for (Hologram entity : entities) {
            entity.remove();
        }
    }
}
