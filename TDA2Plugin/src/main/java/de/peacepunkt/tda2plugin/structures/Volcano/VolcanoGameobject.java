package de.peacepunkt.tda2plugin.structures.Volcano;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.Random;

public class VolcanoGameobject extends AbstractGameobject {
    private Location seedLocation;
    private static int maxSameTime = 4;
    private static int force = 2;
    private BukkitScheduler scheduler;
    private int taskId;

    VolcanoGameobject(Volcano structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw);


    }

    @Override
    protected void setup(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        this.seedLocation = de.peacepunkt.tda2plugin.structures.Vector.locationFromVector(world, structure.seed);
        if(!noFunc) {
            scheduler = MainHolder.main.getServer().getScheduler();
            taskId = scheduler.scheduleSyncRepeatingTask(MainHolder.main, new Runnable() {
                @Override
                public void run() {
                    timerCallback();
                }
            }, 0L, 10);
        }
    }
    private float random(float a, float b) {
        return (float) (a + Math.random() * (b - a));
    }

    private void timerCallback() {
        for(int i = 0; i < maxSameTime; i++) {
            if(new Random().nextInt(25) < 1) {
                Vector v = new Vector(random(-1, 1) * force, random(force, force*3), random(-1, 1) * force);
                new ExplosiveFallingBlock(MainHolder.main, seedLocation.clone(), Material.MAGMA_BLOCK)
                        .setFire(true)
                        .setVelocity(v)
                        .setExplosionRadius(3)
                        .setAddStats(true)
                        .setTimeout(30)
                        .setMakeExplodedBlocks(true)
                        .setFallThroughWater(true);
            }
        }
        if(new Random().nextInt(500) < 1) {
            seedLocation.getWorld().strikeLightning(seedLocation);
        }
    }


    public void unregister() {
        super.unregister();
        cancelTasks();
    }

    public void cancelTasks() {
        if(taskId != -1) {
            scheduler.cancelTask(taskId);
        }
    }
}
