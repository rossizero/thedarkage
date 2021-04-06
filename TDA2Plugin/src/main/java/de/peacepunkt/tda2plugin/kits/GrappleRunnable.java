package de.peacepunkt.tda2plugin.kits;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GrappleRunnable extends BukkitRunnable {
    Arrow arrow;
    Location now;
    Location before;
    LocalDateTime  arrowStill = null;
    List<Block> list;
    int distance = 30;

    public  GrappleRunnable(Arrow arrow) {
        this.arrow = arrow;
        list = new ArrayList<>();
    }
    @Override
    public void run() {
        if(before != null) {
            drawBetween();
        }
        before = now;
        now = arrow.getLocation();
        if(arrowStill != null) {
            long counter = arrowStill.until(LocalDateTime.now(), ChronoUnit.SECONDS);
            if(counter > 20) {
                undo();
                cancel();
            }
        }
    }

    private void undo() {
        for(Block b: list) {
            b.setType(Material.AIR);
            b.getWorld().playSound(b.getLocation(), Sound.BLOCK_SCAFFOLDING_BREAK, 2, 0);
        }
    }

    private void drawBetween() {
        //" A value of 0 indicates no limit "
        int dis = (int) now.distance(before);
        if(dis > 0) {
            if(distance > 0) {
                BlockIterator iter = new BlockIterator(arrow.getWorld(), before.toVector(), arrow.getVelocity(), -1, dis + 1);
                Block lastBlock = iter.next();
                while (iter.hasNext()) {
                    lastBlock = iter.next();
                    if (lastBlock.getType() == Material.AIR) {
                        lastBlock.setType(Material.IRON_BARS);
                        list.add(lastBlock);
                    }
                }
                distance -= dis;
            } else {
                undo();
                cancel();
            }
        } else {
            if(arrowStill == null) {
                arrowStill = LocalDateTime.now();
            }
        }
    }
}
