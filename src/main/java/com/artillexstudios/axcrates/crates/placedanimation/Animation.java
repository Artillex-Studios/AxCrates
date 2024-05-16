package com.artillexstudios.axcrates.crates.placedanimation;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public class Animation {
    protected final int totalFrames;
    protected final Location location;
    protected int frame = 0;
    protected final Particle particle;
    protected Particle.DustOptions options;
    protected boolean reverse = false;
    protected int dir = 1;

    public Animation(int totalFrames, Location location, String particle) {
        this.totalFrames = totalFrames;
        this.location = location;

        String[] s = particle.split(";");
        if (s.length == 4) {
            options = new Particle.DustOptions(Color.fromRGB(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])), 1f);
        } else if (s.length == 5) {
            options = new Particle.DustOptions(Color.fromRGB(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3])), Float.parseFloat(s[4]));
        } else options = null;

        this.particle = Particle.valueOf(s[0].toUpperCase());
    }

    public Animation(int totalFrames, Location location, String particle, boolean reverse) {
        this(totalFrames, location, particle);
        this.reverse = reverse;
    }

    public void play() {
        if (dir == 1 && frame > totalFrames || dir == -1 && frame == 0) {
            if (reverse) dir = dir * -1;
            else frame = 0;
        }
        else frame += dir;
        run();
    }

    protected void run() {
    }
}
