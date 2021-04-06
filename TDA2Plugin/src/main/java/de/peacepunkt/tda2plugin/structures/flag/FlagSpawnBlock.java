package de.peacepunkt.tda2plugin.structures.flag;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;

import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import de.peacepunkt.tda2plugin.team.Teem;

public class FlagSpawnBlock implements Listener{
	public int teamId;
	public Vector seed;

	private Location seedLocation;
	private FlagGameobject targetFlag;
	private boolean first = true;

	public static int RESPAWN_DELAY = (int) Main.flagCaptureSpeed;
	/*public FlagSpawnBlock(int id, Flag target, Location seed) {
		this.teamId = id;
		this.targetFlag = target;
		this.seedLocation = seed;
		this.seed = Vector.vectorFromLocation(seedLocation);
	}*/

	public FlagSpawnBlock() {}
	public void draw() {
		if(seedLocation != null) {
			//System.out.println("World of spawnblock " + this + " " + seedLocation.getWorld());
			Teem tmp = targetFlag.getCurrent();
			if (tmp != null) {
				if (targetFlag.getStatus() < Flag.max) {
					seedLocation.getBlock().setType(tmp.getSecondTeamMaterial());
				} else {
					seedLocation.getBlock().setType(tmp.getTeamMaterial());
				}
			} else { //neutral
				seedLocation.getBlock().setType(DefaultTeemThemes.getDefaultTeemTheme(-1).TeamMaterial);
			}
			if (first) {
				first = false;
				ArmorStand a = null;
				if (seedLocation.clone().add(1, 0, 0).getBlock().getType().equals(Material.AIR)) {
					a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(1.25, 0, 0.5), EntityType.ARMOR_STAND);
				} else if (seedLocation.clone().add(-1, 0, 0).getBlock().getType().equals(Material.AIR)) {
					a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(-1.25, 0, 0.5), EntityType.ARMOR_STAND);
				} else if (seedLocation.clone().add(0, 0, -1).getBlock().getType().equals(Material.AIR)) {
					a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(0.5, 0, -1.25), EntityType.ARMOR_STAND);
				} else if (seedLocation.clone().add(0, 0, 1).getBlock().getType().equals(Material.AIR)) {
					a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(0.5, 0, 1.25), EntityType.ARMOR_STAND);
				} else {
					//Bat b = (Bat) seed.getWorld().spawnEntity(seed.clone().add(0.5, -1.5, 0.5), EntityType.BAT);
					a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(0.5, 1.25, 0.5), EntityType.ARMOR_STAND);
					//b.addPassenger(a);
					//b.setAI(false);
					//b.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
				}
				a.setMarker(true);
				a.setCustomName(ChatColor.WHITE + targetFlag.getName());
				a.setCustomNameVisible(true);
				a.setVisible(false);
				a.setGravity(false);
				a.setInvulnerable(true);
			}
		}
	}
	public void spawnPlayer(Player p) {
		targetFlag.spawnAtFlag(p);
	}
	
	@EventHandler
	public void onPlayerInteractEvent (PlayerInteractEvent e) {
		if(e.getPlayer().getWorld().equals(seedLocation.getWorld())) {
			Location tmp = getTargetBlock(e.getPlayer(), 15).getLocation();
			if (tmp != null) {
				if (seedLocation.equals(tmp)) {
					spawnPlayer(e.getPlayer());
				}
			}
		}
	}

	public final Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
	public Location getLocation() {
		return seedLocation;
	}

	/*public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public void setLocation (Location seed) {
		this.seedLocation = seed;
	}

	public FlagGameobject getTargetFlag() {
		return targetFlag;
	}*/

	public void setWorld(World world) {
		this.seedLocation = Vector.locationFromVector(world, seed);
	}

	void setTargetFlag(FlagGameobject targetFlag) {
		this.targetFlag = targetFlag;
	}
}
