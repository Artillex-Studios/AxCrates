package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axapi.entity.PacketEntityFactory;
import com.artillexstudios.axapi.entity.impl.PacketItem;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CircleAnimation extends Animation {
    private final ArrayList<PacketItem> entities = new ArrayList<>();

    public CircleAnimation(Player player, Crate crate, Location location) {
        super(player, 180, crate, location);

        for (CrateReward reward : super.getRewards()) {
            PacketItem hologram = (PacketItem) PacketEntityFactory.get().spawnEntity(location, EntityType.DROPPED_ITEM);
            hologram.setName(StringUtils.format(ItemUtils.getFormattedItemName(reward.getDisplay())));
            hologram.setGravity(false);

            entities.add(hologram);
        }

        totalFrames = totalFrames + (entities.size() * 15);
    }

    @Override
    protected void run() {
        if (frame > 165) {
            if (frame % 15 != 0) return;
            int c = (frame - 180) / 15;

            finalRewards.get(c).run(player);
            final PacketItem entity = entities.get(c);
            Scheduler.get().runAt(entity.getLocation(), scheduledTask -> entity.getLocation().getWorld().strikeLightningEffect(entity.getLocation()));
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
                    entities.get(i).setName(StringUtils.format(ItemUtils.getFormattedItemName(rewards.get(i).getDisplay())));
                    entities.get(i).setItemStack(rewards.get(i).getDisplay());
                }

                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).hide(player);
                entities.get(i).teleport(loc);
                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).show(player);

                angle += 360D / entities.size();
            }
        }
    }

    protected void end() {
        for (PacketItem entity : entities) {
            entity.remove();
        }
    }
}
