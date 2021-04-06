package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class BombTrace {
    Location start;
    int bombs;
    World world;
    Vector dir;
    Location lastend;
    double distance;
    double step;
    Main main;

    public BombTrace(Main main, Location start, Vector dir, int bombs, World world, double distance) {
        this.start = start;
        this.bombs = bombs;
        this.world = world;
        this.dir = dir;
        this.lastend = start;
        this.distance = distance;
        this.step = distance / bombs;
        this.main = main;

        start();
    }

    private void start() {
        for(int i = 0; i < bombs; i++) {
            Location l = start.clone().toVector().add(dir.clone().normalize().multiply(i*step)).toLocation(world);
            l.add(0, 5 * i, 0);
            new ExplosiveFallingBlock(main, l, Material.BLACK_CONCRETE)
                    .setExplosionRadius(2)
                    .setTimeout(30)
                    .setVelocity(new Vector(0, -0.4, 0))
                    .setMakeExplodedBlocks(true)
                    .setSmoke(false);

        }
    }
}
