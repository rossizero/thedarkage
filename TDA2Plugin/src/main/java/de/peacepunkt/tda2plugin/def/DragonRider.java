package de.peacepunkt.tda2plugin.def;

import de.peacepunkt.tda2plugin.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class DragonRider implements Listener {
    Player player;
    EnderDragon enderDragon;
    Main main;

    public DragonRider(Player player, Main main) {
        this.player = player;
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    public void spawn() {
        if(player.getWorld().getName().equals("arena")) {
            this.enderDragon = (EnderDragon) player.getWorld().spawnEntity(player.getLocation(), EntityType.ENDER_DRAGON);
            enderDragon.addPassenger(player);
            enderDragon.setCollidable(false);
            enderDragon.setPhase(EnderDragon.Phase.FLY_TO_PORTAL);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(enderDragon != null) {
                        enderDragon.setRotation(player.getLocation().getYaw()+180, player.getLocation().getPitch());
                        enderDragon.setVelocity(player.getEyeLocation().getDirection().multiply(0.5));
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(main, 0, 3);
        } else {
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onEntityDismountEvent(EntityDismountEvent event) {
        if(event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            if(entity.equals(player)) {
                enderDragon.remove();
                enderDragon = null;
                HandlerList.unregisterAll(this);
            }
        }
    }
    @EventHandler
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if(event.getEntity().equals(enderDragon)) {
            event.setDroppedExp(0);
            enderDragon.remove();
            enderDragon = null;
            HandlerList.unregisterAll(this);
        }
    }
    private void spawnMissle(Player player, EnderDragon target) {
        if(target != null) {
            System.out.println("Spawned missle");
            Bat bat = (Bat) enderDragon.getWorld().spawnEntity(enderDragon.getLocation().add(enderDragon.getLocation().getDirection().multiply(4)), EntityType.BAT);
            bat.setGlowing(true);
            new BukkitRunnable() {
                double count = 0;
                double maxcount = 20 * 10;
                double speed = 3.0;

                @Override
                public void run() {
                    count++;
                    if (count < maxcount) {
                        bat.getLocation().setDirection(target.getLocation().toVector());
                        Vector diff = target.getLocation().subtract(bat.getLocation()).toVector().normalize();
                        bat.setVelocity(diff.multiply(speed));
                        bat.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, bat.getLocation(), 3, 0.5, 0.5, 0.5, 0, null, true);

                        if (bat.getLocation().distance(target.getLocation()) < 4.20) {
                            bat.getWorld().createExplosion(bat.getLocation(), 4);
                            bat.remove();
                            cancel();
                        }
                    } else {
                        bat.getWorld().createExplosion(bat.getLocation(), 3, false, false);
                        bat.remove();
                        cancel();
                    }

                }
            }.runTaskTimer(main, 0, 2);
        } else {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "no target close"));
        }
    }
    @EventHandler
    public void onPlayerRightCLick(PlayerInteractEvent event) {
        if(event.getPlayer().equals(player)) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                Arrow arrow = enderDragon.launchProjectile(Arrow.class, player.getLocation().getDirection().multiply(4));
                arrow.getLocation().setDirection(player.getLocation().getDirection());
            }
            if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                EnderDragon target = null;
                for(Entity e : enderDragon.getNearbyEntities(70,70,70)) {
                    if(e instanceof EnderDragon) {
                        if(!e.equals(enderDragon)) {
                            target = (EnderDragon) e;
                            spawnMissle(player, target);
                            break;
                        }
                    }
                }
                spawnMissle(player, target);
            }
        }
    }
}
