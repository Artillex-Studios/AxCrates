package com.artillexstudios.axcrates.animation.opening.impl;

import com.artillexstudios.axapi.items.WrappedItemStack;
import com.artillexstudios.axapi.nms.NMSHandlers;
import com.artillexstudios.axapi.packetentity.PacketEntity;
import com.artillexstudios.axapi.packetentity.meta.EntityMeta;
import com.artillexstudios.axapi.packetentity.meta.Metadata;
import com.artillexstudios.axapi.packetentity.meta.entity.ItemEntityMeta;
import com.artillexstudios.axapi.packetentity.meta.serializer.EntityDataSerializers;
import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axapi.utils.StringUtils;
import com.artillexstudios.axcrates.crates.Crate;
import com.artillexstudios.axcrates.animation.opening.Animation;
import com.artillexstudios.axcrates.crates.rewards.CrateReward;
import com.artillexstudios.axcrates.utils.ItemUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CircleAnimation extends Animation {
    private final ArrayList<PacketEntity> entities = new ArrayList<>();

    public CircleAnimation(Player player, Crate crate, Location location) {
        super(player, 180, crate, location);

        generateRewards();
        for (CrateReward reward : super.getCompactRewards()) {
            PacketEntity entity = NMSHandlers.getNmsHandler().createEntity(EntityType.ITEM_DISPLAY, location); // todo: fix 1.18.2 compatibility
            EntityMeta meta = entity.meta();
            meta.name(StringUtils.format(ItemUtils.getFormattedItemName(reward.getDisplay())));
            Metadata metadata = entity.meta().metadata();
            metadata.define(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(new ItemStack(Material.AIR)));
            metadata.set(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(reward.getDisplay()));
            metadata.define(EntityDataSerializers.INT.createAccessor(10), 0);
            metadata.set(EntityDataSerializers.INT.createAccessor(10), 1);
            metadata.define(EntityDataSerializers.BYTE.createAccessor(24), (byte) 0);
            metadata.set(EntityDataSerializers.BYTE.createAccessor(24), (byte) 7);

            meta.hasNoGravity(true);
            meta.customNameVisible(true);
            entity.spawn();

            entities.add(entity);
        }

        totalFrames = totalFrames + (entities.size() * 15);
    }

    @Override
    protected void run() {
        if (frame > 165) {
            if (frame % 15 != 0) return;
            int c = (frame - 180) / 15;

            getCompactRewards().get(c).run(player);
            final PacketEntity entity = entities.get(c);
            final Location loc = entity.location();
            Scheduler.get().runAt(loc, scheduledTask -> loc.getWorld().strikeLightningEffect(loc));
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
                    final PacketEntity entity = entities.get(i);
                    EntityMeta meta = entity.meta();
                    meta.name(StringUtils.format(ItemUtils.getFormattedItemName(getCompactRewards().get(i).getDisplay())));
                    Metadata metadata = entity.meta().metadata();
                    metadata.define(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(new ItemStack(Material.AIR)));
                    metadata.set(EntityDataSerializers.ITEM_STACK.createAccessor(23), WrappedItemStack.wrap(getCompactRewards().get(i).getDisplay()));
                }

//                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).hide(player);
                entities.get(i).teleport(loc);
//                for (Player player : Bukkit.getOnlinePlayers()) entities.get(i).show(player);

                angle += 360D / entities.size();
            }
        }
    }

    protected void end() {
        for (PacketEntity entity : entities) {
            entity.remove();
        }
    }
}
