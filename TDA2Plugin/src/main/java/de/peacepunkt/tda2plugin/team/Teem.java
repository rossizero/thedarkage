package de.peacepunkt.tda2plugin.team;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.peacepunkt.tda2plugin.stats.RoundStats;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;


public class Teem {

	@JsonIgnoreProperties
	private Team scoreboardTeam;
	private ArrayList<Player> inmates;

	//A teem (all values needed to be saved)
	private DefaultTeemThemes theme;
	public int themeId;

	public String defaultKit;
	public int ID;
	public String name;
	
	//Their spawn world
	private World spawn;

	//Stats for the current Round

	private RoundStats roundStats;

	public Teem() {
		inmates = new ArrayList<>();
		roundStats = new RoundStats(this);
	}

	public void setup() {
		this.theme = DefaultTeemThemes.getDefaultTeemTheme(this.themeId);
	}

	public void setScoreboardTeam(Team t) {
		this.scoreboardTeam = t;
	}
	public Team getScoreboardTeam() {
		return this.scoreboardTeam;
	}
	public void join(Player p, boolean tp) {
		if(!inmates.contains(p)) {
			inmates.add(p);
			//scoreboardTeam.addEntry(p.getName());
			roundStats.addPlayer(p);
			p.setPlayerListName(getChatColor() + p.getName());
			p.sendMessage("You're part of the " + this.theme.colorCode + name);

			p.setHealth(20);
			p.setFoodLevel(20);
			p.setDisplayName(getChatColor() + p.getName() + ChatColor.RESET);
			if(tp) {
				p.teleport(spawnLocation());
			}
		}
	}
	public Location spawnLocation() {
		return new Location(spawn, spawn.getSpawnLocation().getX(), spawn.getSpawnLocation().getY(), spawn.getSpawnLocation().getZ());
	}
	public void leave(Player p) {
		if(inmates.contains(p))
			inmates.remove(p);
	}
	public boolean contains(Player p) {
		return inmates.contains(p);
	}
	public String getName() {
		return name;
	}
	public Material getTeamMaterial() {
		return this.theme.TeamMaterial;
	}
	public Material getSecondTeamMaterial() {
		return this.theme.SecondTeamMaterial;
	}
	public int getTeamSize() {
		return inmates.size();
	}
	public ChatColor getChatColor() {
		return this.theme.colorCode;
	}
	public void setWorld(World world) {
		spawn = world;
		if(spawn != null) { //after unloading at rounds end the spawn ist set to null to get gc to collect this object
			//clear spawn from all Entities
			for(Entity e: spawn.getEntities()) {
				if(e.getType().equals(EntityType.BAT) || e.getType().equals(EntityType.ARMOR_STAND)) {
					e.remove();
				}
			}
		}
	}
	public World getSpawnWorld() {
		return spawn;
	}
	public int getId() {
		return ID;
	}

	public RoundStats getRoundStats() {
		return roundStats;
	}

	public String getDefaultKit() {
		return defaultKit;
	}

	public ArrayList<Player> getInmates() {
		return inmates;
	}
}
