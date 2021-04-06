package de.peacepunkt.tda2plugin.def;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Boid {
    Bat bat;
    public static double neighbordist = 10;
    public static double maxspeed = 0.4;
    public static double maxforce = 0.03;
    public Vector velocity;
    Swarm parent;
    public Boid(Bat bat, Swarm swarm) {
        this.bat = bat;
        this.parent = swarm;
        velocity = new Vector(0,0,0);
    }
    private double map(double x, double a, double b, double c, double d) {
        return  (x-a)/(b-a) * (d-c) + c;
    }
    public Vector toSeed(Vector seed, boolean always) {
        if(seed.distance(bat.getLocation().toVector()) > 40 || always) {
            Vector ret = seed.subtract(bat.getLocation().toVector());
            ret.normalize().multiply(always ? maxspeed*2 : maxspeed);
            return ret;
        }
        Vector ret = seed.subtract(bat.getLocation().toVector());
        ret.normalize().multiply(maxspeed/4);
        return ret;
    }

    public void checkTargetDistance(Player target, Player hit) {
        if (bat.getLocation().distance(target.getLocation()) < 1.5) {
            target.damage(0.5, hit);
        }
    }
    // Cohesion: Steer towards flockmates that have moved too far away.
    // Collect the average pos of all nearby boids of same species. This becomes "desired".
    // Steer towards desired.
    public Vector cohesion () {
        Vector sum = new Vector(0, 0, 0);   // Start with empty vector to accumulate all pos's
        int count = 0;
        for (Entity other : bat.getNearbyEntities(neighbordist,neighbordist,neighbordist)) {
            if(other instanceof Bat) {
                double d = bat.getLocation().distance(other.getLocation());
                if ((d > 0) && (d < neighbordist)) {
                    sum.add(other.getLocation().toVector()); // Add pos
                    count++;
                }
            }
        }
        if (count > 0) {
            sum.divide(new Vector(count, count, count)); // average
            sum.subtract(bat.getLocation().toVector()); //  vec pointing from current pos to "desired" pos
            sum.normalize();
            sum.multiply(maxspeed*1.8);
            // Steering = Desired - vel
            sum.subtract(bat.getVelocity());
            //double setMax = map(Math.sqrt(sum.getX()*sum.getX() + sum.getY()* sum.getY() + sum.getZ()*sum.getZ()), 0, 5, 0, maxforce);
            //sum.limit(setMax)
            sum.normalize().multiply(maxspeed*1.8);
            return sum;
        } else {
            return new Vector(0, 0, 0);
        }
    }
    // Separate: Steer away from flockmates, of all species, that are too close.
    // Calc vector pointing away from each nearby flockmate.
    // The  average of these vectors becomes "desired".
    public Vector separate () {
        float sepDist = 10;// r*6;
        Vector sum = new Vector(0, 0, 0); // ends up as "desired"
        int count = 0;
        float dAv = 0, dTotal = 0;
        for (Entity other : bat.getNearbyEntities(neighbordist,neighbordist,neighbordist)) {
            if(other instanceof Bat) {
                double d = bat.getLocation().distance(other.getLocation());
                // d>0 stops self-checking but allows some boids to piggy-back from time to time
                if ((d > 0) && (d < sepDist)) {
                    dTotal += d;
                    Vector diff = bat.getLocation().toVector().subtract(other.getLocation().toVector());// PVector.sub(pos, other.pos); // points away
                    diff.normalize();
                    sum.add(diff);
                    count++;
                }
            }
        }
        // Average == divide by how many
        if (count > 0) {
            sum.divide(new Vector(count, count, count));
            dAv = dTotal/count;
        }
        if (Math.sqrt(sum.getX()*sum.getX() + sum.getY()* sum.getY() + sum.getZ()*sum.getZ()) > 0) {
            // force has inverse relatioship to average distance
            // if flockmates are closer, use more force, further away, use less
            double setMaxForce = map(dAv, 0, sepDist, .2, 0);
            //  Steering = Desired - vel
            sum.normalize();
            sum.multiply(maxspeed);
            sum.subtract(bat.getVelocity()); // sum is "desired"
            //sum.limit(setMaxForce);
            sum.normalize().multiply(setMaxForce);
            //sum.setMag(setMaxForce);//not js mode
        }
        return sum;
    }

    // Align: Steer towards average heading (not pos) of nearby flockmates of same species.
    // Collect heading vector for each nearby flockmate.
    // the average of these vecs becomes "desired".
    public Vector align () {
        Vector sum = new Vector(0, 0, 0);
        int count = 0;
        for (Entity other : bat.getNearbyEntities(neighbordist,neighbordist,neighbordist)) {
            if(other instanceof Bat) {
                double d = bat.getLocation().distance(other.getLocation());
                if ((d > 0) && (d < neighbordist)) {
                    Boid o = parent.getBoid((Bat) other);
                    if(o != null) {
                        sum.add(o.velocity);// total of headings of each local flockmate
                        count++;
                    }
                }
            }
        }
        if (count > 0) {
            //sum.div((float)count); // average
            sum.divide(new Vector(count, count*1.7, count));
            //  Steering = Desired - vel
            if(!(sum.getZ() + sum.getY() +sum.getX() == 0.0))
                sum.normalize();
            sum.multiply(maxspeed*1.9);
            sum.subtract(velocity);
            //double setMax = map(Math.sqrt(sum.getX()*sum.getX() + sum.getY()* sum.getY() + sum.getZ()*sum.getZ()), 0, 5, 0, .03);
            if(!(sum.getZ() + sum.getY() +sum.getX() == 0.0))
                sum.normalize();
            sum.multiply(maxspeed*1.9);
            //sum.limit(setMax);
            return sum;
        } else return new Vector(0, 0,  0);
    }
    static int raduis = 2;

    public Vector checkSurroundings() {
        Vector sum = new Vector(0, 0, 0);
        Block closest = null;
        for(int i = -raduis; i < raduis; i++) {
            for (int j = -raduis; j < raduis; j++) {
                for (int k = -raduis; k < raduis; k++) {
                    if(!bat.getLocation().clone().add(i, j, k).getBlock().getType().equals(Material.AIR)) {
                        if(closest == null)  {
                            closest = bat.getLocation().clone().add(i, j, k).getBlock();
                        } else if (bat.getLocation().clone().add(i, j, k).distance(bat.getLocation()) < bat.getLocation().distance(closest.getLocation())) {
                            closest = bat.getLocation().clone().add(i, j, k).getBlock();
                        }
                    }
                }
            }
        }
        if(closest != null) {
            Vector dirClosestBlock = bat.getLocation().toVector().subtract(closest.getLocation().toVector());
            dirClosestBlock.normalize().multiply(maxspeed*3);
            sum = dirClosestBlock;
        }
        return sum;
    }

    public void playSound() {
        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_PARROT_FLY, 2, -3);
    }
    public void playAggro() {
        bat.getWorld().playSound(bat.getLocation(), Sound.ENTITY_PARROT_AMBIENT, 3, 0);
    }
}
