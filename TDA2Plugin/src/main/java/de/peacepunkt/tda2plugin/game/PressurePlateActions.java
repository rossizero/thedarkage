package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CopyOnWriteArrayList;

public class PressurePlateActions extends BukkitRunnable implements Listener {
    CopyOnWriteArrayList<PlayerTimeStamp> noFallDamage;
    public PressurePlateActions(Main m) {
        noFallDamage = new CopyOnWriteArrayList<>();
        m.getServer().getPluginManager().registerEvents(this, m);
        this.runTaskTimer(m, 0, 10);
    }

    @Override
    public void run() {
        for(PlayerTimeStamp p : noFallDamage) {
            if(p.canBeRemoved() && (p.getPlayer().isOnGround() || p.getPlayer().isSwimming())) {
                noFallDamage.remove(p);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent (PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            double force = 0;
            if (event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
                force = 1;
            } else  if (event.getClickedBlock().getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
                force = 3;
            } else  if (event.getClickedBlock().getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
                force = 2;
            }
            if(force != 0) {
                Material m = event.getClickedBlock().getLocation().add(0, -1, 0).getBlock().getType();
                boolean doo = false;
                if(applyForce(event.getPlayer())) {
                    if (m.equals(Material.REDSTONE_BLOCK)) {
                        doo = true;
                        //Vector dir = new Vector(Math.cos(Math.toRadians(event.getPlayer().getLocation().getYaw() + 90)), 0, Math.sin(Math.toRadians(event.getPlayer().getLocation().getYaw() + 90)));
                        //event.getPlayer().setVelocity(dir.multiply(force * 10));
                        Vector dirOhneHeight = new Vector(event.getPlayer().getEyeLocation().getDirection().getX(), 0, event.getPlayer().getEyeLocation().getDirection().getZ());
                        event.getPlayer().setVelocity(dirOhneHeight.multiply(force * 10));
                    } else if (m.equals(Material.GOLD_BLOCK)) {
                        doo = true;
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(new Vector(0, force, 0)));
                    } else if (m.equals(Material.IRON_BLOCK)) {
                        doo = true;
                        Vector dirOhneHeight = new Vector(event.getPlayer().getEyeLocation().getDirection().getX(), 0, event.getPlayer().getEyeLocation().getDirection().getZ());
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(dirOhneHeight.multiply(force * 10)).add(new Vector(0, force, 0)));
                    } else if (m.equals(Material.EMERALD_BLOCK)) {
                        doo = true;
                        Vector dirOhneHeight = new Vector(event.getPlayer().getEyeLocation().getDirection().getX(), 0, event.getPlayer().getEyeLocation().getDirection().getZ());
                        event.getPlayer().setVelocity(event.getPlayer().getVelocity().add(dirOhneHeight.multiply(force * 5)).add(new Vector(0, force / 1.2, 0)));
                    }
                    if (doo) {
                        event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                        if (!contains(event.getPlayer())) {
                            noFallDamage.add(new PlayerTimeStamp(event.getPlayer()));
                        }
                    }
                }
            }
        }
    }
    private boolean applyForce(Player player) {
        for(PlayerTimeStamp p: noFallDamage) {
            if (p.getPlayer().equals(player)) {
                if(p.canBeRemoved()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        //if not yet exists
        return true;
    }


    public boolean contains(Player entity) {
        for(PlayerTimeStamp p: noFallDamage) {
            if(p.getPlayer().equals(entity)) {
                return true;
            }
        }
        return false;
    }

    public void remove(Player entity) {
        for(PlayerTimeStamp p: noFallDamage) {
            if (p.getPlayer().equals(entity)) {
                noFallDamage.remove(p);
                return;
            }
        }
    }


    private class PlayerTimeStamp {
        Player player;
        LocalDateTime now;

        public  PlayerTimeStamp(Player player) {
            this.player = player;
            this.now = LocalDateTime.now();
        }

        public boolean canBeRemoved() {
            long seconds = now.until(LocalDateTime.now(), ChronoUnit.SECONDS);
            if(seconds > 1) {
                return true;
            }
            return false;
        }

        public Player getPlayer(){
            return player;
        }
    }
}
