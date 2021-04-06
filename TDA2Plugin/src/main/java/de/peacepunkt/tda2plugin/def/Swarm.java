package de.peacepunkt.tda2plugin.def;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Swarm extends BukkitRunnable {
    List<Boid> boids;
    List<Location> seeds;
    Location currentSeed;
    int switchSeed;
    static int period = 3;
    static int maxSeconds = 30;
    Player target;
    Wither wither = null;
    Player attacker;
    public Swarm(List<Location> seeds, Main main, int number) {
        this(seeds.get(new Random().nextInt(seeds.size())), main, number);
        this.seeds = seeds;
    }

    public Swarm(Location seed, Main main, int number) {
        this.boids = new ArrayList<>();
        int bound = 3;
        this.currentSeed = seed;
        this.seeds = new ArrayList<>();
        this.seeds.add(seed);
        for(int i = 0; i < number; i++) {
            Bat bat = (Bat) seed.getWorld().spawnEntity(seed.clone().add(randomDouble(-bound, bound), randomDouble(-bound, bound), randomDouble(-bound, bound)), EntityType.BAT);
            bat.setAwake(true);
            bat.setSilent(true);
            boids.add(new Boid(bat, this));
        }
        this.switchSeed = new Random().nextInt(20/period * maxSeconds);
        runTaskTimer(main, 0, 1);
    }
    public void setTarget(Player player, Player attacker) {
        target = player;
        this.attacker = attacker;
        this.switchSeed = new Random().nextInt(20 / period * (maxSeconds-10))+10*20/period;
    }
    private double randomDouble(double lower, double upper) {
        return new Random().nextDouble() * (upper - lower) + lower;
    }
    int calc = period;
    @Override
    public void run() {
        if(calc == 0) {
            switchSeed--;
            if (switchSeed <= 0) {
                if (seeds.size() > 1) {
                    Location old = currentSeed;
                    while (currentSeed.equals(old)) {
                        this.currentSeed = seeds.get(new Random().nextInt(seeds.size()));
                    }
                }
                target = null;
                attacker = null;
                this.switchSeed = new Random().nextInt(20 / period * maxSeconds);
            }
            int bound = 3;

            for(Entity  e :  boids.get(0).bat.getWorld().getEntities()) {
                if(e instanceof Wither) {
                    wither = (Wither) e;
                    break;
                }
            }
            if(wither != null) {
                if (wither.isDead()) {
                    wither = null;
                }
            }
            for (Boid b : boids) {
                if (b.bat != null && b.bat.isDead()) {
                    b.bat = (Bat) currentSeed.getWorld().spawnEntity(currentSeed.clone().add(randomDouble(-bound, bound), randomDouble(-bound, bound), randomDouble(-bound, bound)), EntityType.BAT);
                    b.bat.setAwake(true);
                    b.bat.setSilent(true);
                } else {
                    b.velocity.add(b.cohesion());
                    b.velocity.add(b.align());
                    b.velocity.add(b.separate());
                    b.velocity.add(b.checkSurroundings());
                    if(target != null && attacker != null) {
                        b.velocity.add(b.toSeed(target.getLocation().toVector(), true));
                        if(target.getWorld().equals(b.bat.getWorld())) {
                            b.checkTargetDistance(target, attacker);
                        } else {
                            switchSeed = 0;
                            calc = 0;
                        }

                    }
                    if(wither != null) {
                        b.velocity.add(b.toSeed(wither.getLocation().toVector(), true));
                        if(wither.getWorld().equals(b.bat.getWorld())) {
                            wither.damage(5);
                        } else {
                            switchSeed = 0;
                            calc = 0;
                        }
                    }
                    b.velocity.add(b.toSeed(currentSeed.toVector(), false));
                    b.bat.setVelocity(b.velocity.normalize().multiply(Boid.maxspeed));
                }
            }
            calc = period;
        } else {
            for (Boid b : boids) {
                if(new Random().nextInt(boids.size()*8) < (target != null ? 2 : 1)) {
                    b.playSound();
                }
                if(target != null || wither != null) {
                    if(new Random().nextInt(boids.size()*8) < (target != null ? 2 : 1)) {
                        b.playAggro();
                    }
                }
                b.bat.setVelocity(b.velocity);
            }
            calc--;
        }

    }

    public Boid getBoid(Bat bat) {
        for(Boid b : boids) {
            if(b.bat.equals(bat)) {
                return b;
            }
        }
        return null;
    }

    public void kill() {
        for(Boid b : boids) {
            b.bat.remove();
        }
        cancel();
    }
}
