package de.peacepunkt.tda2plugin.team;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.command.SpawnWorldGenerator;

public class TeemUtils {
	
	/*public  static Teem[] loadTeams(String worldName) throws FileNotFoundException, IOException, InvalidConfigurationException {
		YamlConfiguration config = new YamlConfiguration();
		String teamFile = Main.arenasPath + worldName + "/teams.yml";
		File f = new File(teamFile);
		config.load(f);
		List<Map<String, String>> test = (List<Map<String, String>>) config.get("teams");
		ArrayList<Teem> teems = new ArrayList<>();
		for(Map<String, String> m: test) {	
			String name = m.get("name");
			int id = Integer.valueOf(m.get("id"));
			int themeId = Integer.valueOf(m.get("idTheme"));
			Teem tmp = new Teem(name, id, themeId);
			teems.add(tmp);
		}
		
		//adding corresponding spawn worlds to Team per Team-ID
		for(Teem t : teems) {
			String tmpName = worldName + "Spawn"+String.valueOf(t.getId());
			t.setWorld(getWorld(tmpName));
			System.out.println("Set spawn to " + t.getWorld());
		}
		return ((Teem[]) teems.toArray(new Teem[teems.size()]));
	}*/
	
	/**
	 * returns team by id or null if team does not exist
	 * @param id
	 * @return
	 */
	/*public static Teem getTeamByID(int id) {
		Teem[] teams = RoundHandler.getInstance().getRound().getTeems();
		for(Teem t : teams) {
			if(t.getId() == id)
				return t;
		}
		return null;
	}*/
	
	/**
	 * returns team of player p or null if no team
	 * @param p
	 * @return
	 */
	/*public static Teem getTeam(Player p) {
		Teem[] teams = PlayRound.getInstance().getRound().getTeems();
		for(Teem t : teams) {
			if(t.contains(p)) {
				return t;
			}
		}
		return null;
	}*/
	
	public  static World getWorld(String worldName) {
		if(Bukkit.getWorld(worldName) == null) {
			System.out.println("Trying to load "+ worldName);
			WorldCreator creator = new WorldCreator(worldName);
			//creator.type(WorldType.CUSTOMIZED);
			creator.environment(World.Environment.NORMAL);
			creator.generateStructures(false);
			creator.generator(new SpawnWorldGenerator());
			World ret = Bukkit.createWorld(creator);
			ret.setDifficulty(Difficulty.PEACEFUL);
			ret.setGameRule(GameRule.DO_FIRE_TICK, false);
			ret.setGameRule(GameRule.DO_MOB_SPAWNING, false);
			ret.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
			ret.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
			ret.setGameRule(GameRule.MOB_GRIEFING, false);
			ret.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			ret.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
			ret.setDifficulty(Difficulty.NORMAL);
			return ret;
		} else {
			return Bukkit.getWorld(worldName);
		}
	}
}
