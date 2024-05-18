package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axapi.hologram.Hologram;
import com.artillexstudios.axapi.hologram.HologramLine;
import com.artillexstudios.axapi.hologram.HologramPage;
import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.Location;

import java.util.ArrayList;

public class CircleAnimation extends Animation {
    private final ArrayList<Hologram> entities = new ArrayList<>();

    public CircleAnimation(Crate crate, Location location) {
        super(180, crate, location);

        for (CrateReward reward : super.getRewards()) {
            Hologram hologram = new Hologram(location, location.toString(), 0.3);
            hologram.addLine(StringUtils.formatToString(ItemUtils.getFormattedItemName(reward.getDisplay())), HologramLine.Type.TEXT);
            hologram.addLine(WrappedItemStack.wrap(reward.getDisplay()).toSNBT(), HologramLine.Type.ITEM_STACK);

//            var page = new HologramPage(hologram);
//            page.addLine("", HologramLine.Type.TEXT);
//            page.addLine("", HologramLine.Type.TEXT);
//            hologram.addPage(page);
            entities.add(hologram);
        }

        totalFrames = totalFrames + (entities.size() * 15);
    }

    @Override
    protected void run() {
        if (frame > 165) {
            if (frame % 15 != 0) return;
            int c = (frame - 180) / 15;

            final Hologram entity = entities.get(c);
            Scheduler.get().runAt(entity.location(), scheduledTask -> entity.location().getWorld().strikeLightningEffect(entity.location()));
            entity.remove();
        } else {
            double radius = 5;
            double angle = frame * 3;

            var rewards = super.getRewards();

            for (int i = 0; i < entities.size(); i++) {
                final Location loc = location.clone();
                double x, z;
                x = Math.cos(Math.PI * 2 * angle / 360) * radius;
                z = Math.sin(Math.PI * 2 * angle / 360) * radius;
                loc.add(x, 4f, z);
                if (frame % 21 == 0) {
                    entities.get(i).setLine(0, StringUtils.formatToString(ItemUtils.getFormattedItemName(rewards.get(i).getDisplay())));
                    entities.get(i).setLine(1, WrappedItemStack.wrap(rewards.get(i).getDisplay()).toSNBT());
                }
                entities.get(i).page(1);
                entities.get(i).teleport(loc);
                entities.get(i).page(0);
                angle += 360D / entities.size();
            }
        }
    }

    protected void end() {
        for (Hologram entity : entities) {
            entity.remove();
        }
    }
}
