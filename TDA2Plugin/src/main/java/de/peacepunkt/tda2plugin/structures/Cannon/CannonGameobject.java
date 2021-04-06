package de.peacepunkt.tda2plugin.structures.Cannon;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Switch;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CannonGameobject extends AbstractGameobject {
    private static long shootDelay = 30;
    LocalDateTime lastShot;

    public CannonGameobject(Cannon structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw);
    }

    @Override
    protected void setup(AbstractStructure structure, World world, boolean noFunc, boolean draw) {

    }

    @Override
    public void draw() {
        //nothing to draw here
    }

    private double random(double a, double b) {
        return (a + Math.random() * (b - a));
    }
    private void shoot(Player player) {
        lastShot = LocalDateTime.now();
        Switch button = (Switch) getLocation().clone().getBlock().getBlockData();
        Location location = null;
        Vector v = null;
        double force = 5;
        double drift = 0.1;
        if(button.getFacing().equals(BlockFace.NORTH)) {
            location = getLocation().clone().add(0, 0, 6);
            v = new Vector(random(-drift, drift) * force, 0.5, force);
        } else if (button.getFacing().equals(BlockFace.EAST)) {
            location = getLocation().clone().add(-6, 0, 0);
            v = new Vector(-force, 0.5, random(-drift, drift) * force);
        } else if (button.getFacing().equals(BlockFace.SOUTH)) {
            location = getLocation().clone().add(0, 0, -6);
            v = new Vector(random(-drift, drift) * force, 0.5, -force);
        } else if((button.getFacing().equals(BlockFace.WEST))) {
            location = getLocation().clone().add(6, 0, 0);
            v = new Vector(force, 0.5, random(-drift, drift) * force);
        }
        if(location != null) {
            new ExplosiveFallingBlock(location, Material.COAL_BLOCK)
                    .setFire(false)
                    .setDeathMessage("You were shot by @t with a cannon!")
                    .setKillMessage("You killed @t with a cannon!")
                    .setVelocity(v)
                    .setExplosionRadius(2)
                    .setAddStats(true)
                    .setTimeout((int) shootDelay)
                    .setFallThroughWater(true)
                    .setPlayer(player)
                    .setMakeExplodedBlocks(true);
            location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 5, 0);
        }
    }

    private boolean canShoot(Player player) {
        if(lastShot != null) {
            long seconds = lastShot.until(LocalDateTime.now(), ChronoUnit.SECONDS);
            if (seconds >= shootDelay) {
                return true;
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "Cannon ready in " + (shootDelay - seconds) + " seconds!"));
                return false;
            }
        } else {
            return true;
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().equals(Material.STONE_BUTTON) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if (event.getClickedBlock().getLocation().equals(getLocation())) {
                    if (canShoot(event.getPlayer())) {
                        shoot(event.getPlayer());
                        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 1));
                    }
                }
            }
        }
    }
}
