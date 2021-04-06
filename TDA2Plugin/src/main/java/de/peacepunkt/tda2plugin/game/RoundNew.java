package de.peacepunkt.tda2plugin.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.def.Swarm;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.pubsub.MvpChangeEvent;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.Cannon.Cannon;
import de.peacepunkt.tda2plugin.structures.Gate.Gate;
import de.peacepunkt.tda2plugin.structures.StructureHandler;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThing;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThingGameobject;
import de.peacepunkt.tda2plugin.structures.Vector;
import de.peacepunkt.tda2plugin.structures.Volcano.Volcano;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import de.peacepunkt.tda2plugin.team.TeamHandler;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.structures.catapult.Catapult;
import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagSpawnBlock;
import de.peacepunkt.tda2plugin.team.Teem;
import de.peacepunkt.tda2plugin.team.TeemUtils;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class RoundNew {

	/*private String flagFile;
	private String flagSpawnFile;
	private String catapultFile;
	private String settingsFile;
	private String teamFile;
	private String gateFile;
    private String volcanoFile;
    private String cannonFile;
    private String topThingsFile;*/


	//private Teem[] teams;

	private World arena;
	List<AbstractGameobject> gameobjects;
	private String worldName;
	List<Teem> teams;
	private RoundHandlerNew roundhandler;

	/*private List<Flag> flags;
	private List<Catapult> catas;
	private List<Gate> gates;
	private List<Volcano> volcanoes;
	private List<Cannon> cannons;
	private List<TopThing> topThings;

	private Main main;*/
	//Play Round
	//Timer timer;
	private int seconds = 0;
	private Scoreboard scoreboard;
	private Objective objective;
	private BukkitRunnable runnable;
	private int spawnWorldSuffix;
	private Long timeOfDay;
	private Boolean storm;
	private Boolean thunder;
	private String displayName;
	private double cataStrength;
	private boolean staged;
	int windHeight;
	private boolean bombs;
	private BombsChecker bombsChecker;
	private Swarm swarm;
	private DispenserChecker dispenserChecker;

	private String path;

    public RoundNew(String worldName, RoundHandlerNew roundhandler, int spawnWorldSuffix){
		this.roundhandler = roundhandler;
		this.worldName = worldName;
		this.path = Main.arenasPath + worldName + "/new/";
		System.out.println(path);
		/*this.flagFile = Main.arenasPath + worldName + "/flags.yml";
		this.flagSpawnFile = Main.arenasPath + worldName + "/flagsSpawn.yml";
		this.catapultFile = Main.arenasPath + worldName + "/catas.yml";
		this.teamFile = Main.arenasPath + worldName + "/teams.yml";
		this.gateFile = Main.arenasPath + worldName + "/gates.yml";
		this.settingsFile =
		this.volcanoFile = Main.arenasPath + worldName + "/volcanos.yml";
		this.cannonFile = Main.arenasPath + worldName + "/cannons.yml";
		this.topThingsFile = Main.arenasPath + worldName + "/topThings.yml";

		createFilesIfNotExist();*/

		loadWorldSettings();
        if(staged) {
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            objective = this.scoreboard.registerNewObjective("timer", "dummy", String.valueOf(Main.roundLength));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score = objective.getScore(ChatColor.BOLD + "Map: " + ChatColor.RESET + ChatColor.YELLOW + displayName);
            score.setScore(16);
            score = objective.getScore(" ");
            score.setScore(15);

            this.spawnWorldSuffix = spawnWorldSuffix;
            load();
        }
	}
	private void loadWorldSettings() {
		try {
			YamlConfiguration config = new YamlConfiguration();
			File f = new File(Main.arenasPath + worldName + "/settings.yml");
			config.load(f);
			config.addDefault("timeOfDay", "1000");
			config.addDefault("storm", "false");
			config.addDefault("thunder", "false");
			config.addDefault("displayName", worldName);
			config.addDefault("staged", false);
			config.addDefault("cataStrength", 2.0);
			config.addDefault("windHeight", 255);
			config.addDefault("bombs", false);
			config.options().copyDefaults(true);
            staged = (Boolean) config.get("staged");
			cataStrength = (double) config.get("cataStrength");
			displayName = (String) config.get("displayName");
			thunder = config.get("thunder").equals("true");
			storm = config.get("storm").equals("true");
			windHeight = config.getInt("windHeight");
			bombs = config.getBoolean("bombs");
			timeOfDay = Long.parseLong((String) config.get("timeOfDay"));
		} catch (InvalidConfigurationException | IOException e) {
			e.printStackTrace();
		}
	}

	/*private void createFilesIfNotExist() {
		try {
			new File(this.flagFile).createNewFile();
			new File(this.flagSpawnFile).createNewFile();
			new File(this.catapultFile).createNewFile();
			new File(this.teamFile).createNewFile();
			new File(this.gateFile).createNewFile();
			new File(this.settingsFile).createNewFile();
            new File(this.volcanoFile).createNewFile();
            new File(this.cannonFile).createNewFile();
            new File(this.topThingsFile).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	public double getCataStrength() {
		return cataStrength;
	}
	private void load() {
		TeamHandler teamHandler = new TeamHandler(path);
		teams = teamHandler.loadAll();
		for(Teem t : teams) {
			String tmpName = worldName + "Spawn"+String.valueOf(t.getId());
			t.setWorld(loadNewSpawn(tmpName, t.getId()));
			//System.out.println("Set spawn to " + t.getSpawnWorld());
		}
		//teams = loadTeamsIntern(worldName); //should load all spawns
		KitHandler.getInstance().setRound(this); //important!
		populateTeams(); //should tp all players to their new spawns
		this.dispenserChecker = new DispenserChecker();
		this.bombsChecker = new BombsChecker();
		MainHolder.main.getServer().getPluginManager().registerEvents(this.dispenserChecker, MainHolder.main);
	}
	
	public boolean start() {
		if(staged) {
			roundhandler.disableVotes();
			loadArena();

			//load all structures
			if(path != null) {
				gameobjects = new ArrayList<>();
				List<Class<? extends AbstractStructure>> subclasses = AbstractStructure.getAllSubclasses();
				for (Class clazz : subclasses) {
					StructureHandler structureHandler = new StructureHandler<>(clazz, path);
					List<AbstractStructure> structures = (List<AbstractStructure>)structureHandler.loadAll(clazz);
					if (structures != null) {
						for(AbstractStructure structure: structures) {
							if(!clazz.getName().equals(TopThing.class.getName())) {
								AbstractGameobject abs = structure.getGameobject(structure, arena, false, true);
								if(abs != null) {
									gameobjects.add(abs);
								}
							}
						}
						System.out.println("Found " + structures.size() + " " + structureHandler.name + "'s");
					} else {
						System.out.println("Found no " + structureHandler.name + "'s");
					}
				}
			}
			gameobjects.addAll(loadTopThings());
			//TODO  initSwarm()
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.sendTitle(displayName, "", 10, 70, 10);
				p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 7, 0);
				if(!p.getGameMode().equals(GameMode.CREATIVE))
					p.setAllowFlight(false);
			}


			runnable = new BukkitRunnable() {
				@Override
				public void run() {
					running();
				}
			};
			//runs synchronously!
			runnable.runTaskTimer(MainHolder.main, 0, 20);
			return true;
		}

		return false;
	}

	public List<? extends AbstractGameobject> getAll(Class<? extends AbstractStructure> subclass) {
		List<AbstractGameobject> ret = new ArrayList<>();
		for(AbstractGameobject object : gameobjects) {
			if(object.getStructure().getClass().equals(subclass))
				ret.add(object);
		}
		return ret;
	}

	public void initSwarm() {
    	List<Location> locations = new ArrayList<>();
    	for(AbstractGameobject f : getAll(Flag.class)) {
    		Flag flag = (Flag) f.getStructure();
    		if(flag.birds) {
				locations.add(f.getLocation().clone().add(0, Flag.max+4, 0));
			}
		}
    	if(locations.size() >= 1) {
			swarm = new Swarm(locations, MainHolder.main, Main.numberOfBirds);
		}
	}

	public void callSwarm(Player target, Player attacker) {
    	if(swarm != null) {
    		swarm.setTarget(target, attacker );
		}
	}

	public String getDisplayName() {
    	return displayName;
	}
    private List<TopThingGameobject> loadTopThings() {
		ArrayList<TopThingGameobject> ret = new ArrayList<>();
		StructureHandler structureHandler = new StructureHandler<>(TopThing.class, path);
		List<TopThing> structures = (List<TopThing>)structureHandler.loadAll(TopThing.class);
		if (structures != null) {
			for (TopThing topThing : structures) {
				String tmpSpawnWorld = "Spawn"+topThing.worldname.split("Spawn")[1]+""+spawnWorldSuffix;
				World world = Bukkit.getWorld(tmpSpawnWorld);
				topThing.worldname = tmpSpawnWorld;
				ret.add((TopThingGameobject) topThing.getGameobject(topThing, world, false, true));
			}
		}
		return ret;
    }

	private void running() {
		updateTime();
		dispenserChecker.check();
		if(bombs) {
			bombsChecker.check(arena);
		}
	}
	private void updateTime() {
		if(seconds < Main.roundLength*60) {
			seconds++;
			Bukkit.getPluginManager().callEvent(new MvpChangeEvent());
			int timeLeft =(int)(Main.roundLength * 60 - seconds);
			long hours = TimeUnit.SECONDS.toHours(timeLeft);
			timeLeft -= TimeUnit.HOURS.toSeconds(hours);
			long minutes = TimeUnit.SECONDS.toMinutes(timeLeft);
			timeLeft -= TimeUnit.MINUTES.toSeconds(minutes);
			long seconds = TimeUnit.SECONDS.toSeconds(timeLeft);
			redoScoreboard();
		} else {
			checkFlags(true);
		}
	}

	public int getTimeRun() {
		return seconds;
	}
	public int getRoundLength() {
    	return (int)(Main.roundLength * 60);
	}
	/**
	 * checks every flag's current teem
	 * if one teem has no flags and there are no neutral ones left game is done
	 * if two teams have same amount of max flags == tie
	 * @param finish
	 */
	private void checkFlags(boolean finish) {
		Map<Teem, Integer> count = new HashMap<>();
		int neutrals = 0;
		for(AbstractGameobject abs : getAll(Flag.class)) {
			FlagGameobject f = (FlagGameobject) abs;
			if(count.get(f.getCurrent()) == null) {
				if(f.getCurrent() != null) {
					count.put(f.getCurrent(), 1);
				} else {
					neutrals++;
				}
			} else {
				int curr = count.get(f.getCurrent()) +1;
				count.put(f.getCurrent(), curr);
			}
		}
		Teem max = null;
		int maxInt = 0;
		boolean tie = false;
		for(Teem t: teams) {
			if(count.get(t) == null) {
				if(neutrals == 0) {
					finish = true;
				}
			} else {
				if (maxInt < count.get(t)) {
					maxInt = count.get(t);
					max = t;
					tie = false;
				} else if (maxInt == count.get(t)) {
					//Two max
					tie = true;
				}
			}
		}
		if(finish) {
			if(max != null) {
				if (!tie) {
					Bukkit.broadcastMessage("The " + max.getChatColor() + max.getName() + ChatColor.WHITE + " won this round...");
					finished();
				} else {
					Bukkit.broadcastMessage("The round ended in a tie.......");
					finished();
				}
			} else {
				finished();
			}
		}
	}

	public void redoScoreboard() {
		int count = 14;
		for(AbstractGameobject abs : getAll(Flag.class)) {
			FlagGameobject f = (FlagGameobject) abs;
			for(Teem t : teams) {
				scoreboard.resetScores(t.getChatColor() + f.getName());
			}
			scoreboard.resetScores(DefaultTeemThemes.getDefaultTeemTheme(-1).colorCode + f.getName());
			if(f.getCurrent() == null) {
				DefaultTeemThemes cur = DefaultTeemThemes.getDefaultTeemTheme(-1);
				Score score = objective.getScore(cur.colorCode + f.getName());
				score.setScore(count);
			} else {
				ChatColor cur = f.getCurrent().getChatColor();
				Score score = objective.getScore(cur + f.getName());
				score.setScore(count);
			}
			count--;
		}

		int timeLeft = (int)(Main.roundLength*60-seconds);
		long hours = TimeUnit.SECONDS.toHours(timeLeft);
		timeLeft -= TimeUnit.HOURS.toSeconds(hours);
		long minutes = TimeUnit.SECONDS.toMinutes(timeLeft);
		timeLeft -= TimeUnit.MINUTES.toSeconds(minutes);
		long seconds = TimeUnit.SECONDS.toSeconds(timeLeft);

		objective.setDisplayName(((hours > 0) ? String.format("%02d", hours)+":":"" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds)));
		checkFlags(false);
	}

	private void loadArena() {
		//copy current world
		//System.out.println(Bukkit.getWorlds().toString());
		//World source = TeemUtils.getWorld(worldName);
		//File sourceFolder = source.getWorldFolder();
		File sourceFolder = new File(MainHolder.main.getServer().getWorldContainer()+"/"+worldName);
		File targetFolder = new File(MainHolder.main.getServer().getWorldContainer()+"/arena");
		//TODO really okay to not do that?
		if(Bukkit.getWorld("arena") != null) //after reboot or at very first start
			Bukkit.unloadWorld("arena", false);
		this.arena = copyWorld(sourceFolder, targetFolder, "arena");

		arena.setThundering(thunder);
		arena.setStorm(storm);
		arena.setTime(timeOfDay);
		System.out.println("Copied world " + worldName +" into world arena " + this.arena);
	}

	public World getArena() {
		return arena;
	}


	private World loadNewSpawn(String completeName, int id) {
		String spawnName = "Spawn"+id+""+spawnWorldSuffix;
		File sourceFolder = new File(Bukkit.getServer().getWorldContainer()+"/"+completeName);
		File targetFolder = new File(Bukkit.getServer().getWorldContainer()+"/"+spawnName);

		if(Bukkit.getWorld(spawnName) != null) {
			System.out.println("------------------------------ found old Spawn. Unloading now.");
			Bukkit.unloadWorld(spawnName, false);
		}
		World ret = copyWorld(sourceFolder, targetFolder, spawnName);
		System.out.println("Copied world " + completeName +" into world " + spawnName);
		//TODO Maybe ArmorStand removale of Spawn worlds was done somewhere else too?
		System.out.println("--------->" + " " + ret.getEntities().size());
		for (Entity entity : ret.getEntities()) {
			System.out.println(entity.getType());
		    if(entity instanceof ArmorStand) {
		        entity.remove();
				System.out.println(entity);
            }
        }
		return ret;
	}

	/*private Teem[] loadTeamsIntern(String worldName) {
		try {
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
				String defaultKit = m.get("defaultKit");
				if(defaultKit == null) { //if no team defaultkit specified, load map defaultkit
					defaultKit = this.defaultKit;
				}
				Teem tmp = new Teem(name, id, themeId, defaultKit);
				teems.add(tmp);
			}

			//adding corresponding spawn worlds to Team per Team-ID
			for(Teem t : teems) {
				String tmpName = worldName + "Spawn"+String.valueOf(t.getId());
				t.setWorld(loadNewSpawn(tmpName, t.getId()));
				System.out.println("Set spawn to " + t.getSpawnWorld());
			}
			return ((Teem[]) teems.toArray(new Teem[teems.size()]));
		}  catch (IOException | InvalidConfigurationException e) {
			return null;
		}
	}*/



	/*public static Teem[] loadTeams(String worldName) {
		try {
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
				String defaultKit = m.get("defaultKit");
				if(defaultKit == null) { //if no team defaultkit specified, load map defaultkit
					defaultKit = "Swordsman";
				}
				Teem tmp = new Teem(name, id, themeId, defaultKit);
				teems.add(tmp);
			}
			
			//adding corresponding spawn worlds to Team per Team-ID
			for(Teem t : teems) {
				String tmpName = worldName + "Spawn"+String.valueOf(t.getId());
				t.setWorld(null);
				System.out.println("Set spawn to " + t.getSpawnWorld());
			}
			return ((Teem[]) teems.toArray(new Teem[teems.size()]));
		}  catch (IOException | InvalidConfigurationException e) {
			return null;
		}
	}*/
	
	private void populateTeams() {
		for(Player p: Bukkit.getOnlinePlayers()) {
			if(MainHolder.main.getPlayersNotToTp().contains(p)) {
				joinRandomTeam(p, false);
			} else {
				joinRandomTeam(p, true);
			}
		}
	}
	
	public Teem joinRandomTeam(Player p, boolean tp) {
		Teem smallest = teams.get(0);
		for(Teem t : teams) {
			if(t.getTeamSize() <= smallest.getTeamSize()) {
				smallest = t;
			}
		}
		p.setScoreboard(scoreboard);
		smallest.join(p, tp);
		if(tp) {
			KitHandler.getInstance().setKitOnNewRound(p, smallest.getDefaultKit(), false, true);
			//KitHandler.getInstance().setKit(p, smallest.getDefaultKit(), false, true, true);
			//Kits.getInstance(main).setKit(p, smallest.getDefaultKit(), false, true, true);
		}
        return smallest;
	}
	
	public void switchTeam(Player p, boolean tp) {
		Teem curr = getTeam(p);
		if(curr != null) {
			Teem smallest = curr;
			for(Teem t : teams) {
				System.out.println(t.getName() + " " + t.getTeamSize());
				if(!t.equals(curr)) {
					if(t.getTeamSize() <= smallest.getTeamSize()+1) {
						smallest = t;
					}
				}
			}
			if(!smallest.equals(curr)) {
				p.setHealth(0);//kill
				curr.leave(p);
				smallest.join(p, tp);
				KitHandler.getInstance().setKit(p, smallest.getDefaultKit(), false, true, true);
			} else {
				p.sendMessage("Sorry, you can't change team right now. The teams would be too unbalanced");
			}
		} else {
			joinRandomTeam(p, true);
		}
	}
	public Teem getTeam(Player p) {
		if(teams != null) { //happens if new round and then /sw or so
			for (Teem t : teams) {
				if (t.contains(p)) {
					return t;
				}
			}
		}
		return null;
	}
	
	/*private List<FlagSpawnBlock> getFlagSpawnBlocks(Flag flag) {
		List<FlagSpawnBlock> ret = new ArrayList<>();
		try {
			YamlConfiguration config = new YamlConfiguration();
			File f = new File(flagSpawnFile);
			config.load(f);
			
			
			List< Map<String, List<Map<String, Map<String, Integer>>>>> map = (List<Map<String, List<Map<String, Map<String, Integer>>>>>) config.getList("spawns");
			//Map<String, Map<String, Integer>> m = map.get(String.valueOf(flag.getId())); //get only entries for flag id
			for(Map<String, List<Map<String, Map<String, Integer>>>> m: map) {
				for(String s: m.keySet()) {
					int flagId = Integer.valueOf(s);
					if(flagId == flag.getId()) {
						List<Map<String, Map<String, Integer>>> tmp = m.get(s);
						for(Map<String, Map<String, Integer>> m2: tmp) {
							for(String s2: m2.keySet()) {
								int teamId = Integer.parseInt(s2);
								Map<String, Integer> tmp2 = m2.get(s2);
								int x = Integer.valueOf(tmp2.get("x"));
								int y = Integer.valueOf(tmp2.get("y"));
								int z = Integer.valueOf(tmp2.get("z"));
								Teem teem = null;
								for(Teem tmpTeem : teams) {
									if(tmpTeem.getId() == teamId) {
										teem = tmpTeem;
										break;
									}
								}
								//ret.add(new FlagSpawnBlock(teamId, flag, new Location(Bukkit.getWorld("Spawn"+teamId+""+spawnWorldSuffix), x, y, z)));
								ret.add(new FlagSpawnBlock(teamId, flag, new Location(teem.getSpawnWorld(), x, y, z)));
							}
						}
					}
				}
			}
			
			

		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return ret;
	}*/

	public static World copyWorld(File source, File target, String name) {
		RoundNew.copyWorld(source, target);
		return TeemUtils.getWorld(name);
	}

	/**
	 * Copy pasted, might suck
	 * Make sure to unload targetWorld first if exists!
	 * @param source
	 * @param target
	 */
	private static void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	        
	    } catch (IOException e) {
	 
	    }
	}
	
	public String getName() {
		return worldName;
	}
	public void leave(Player p) {
		Teem team = getTeam(p);
		if(team != null) {
			team.leave(p);
		}
	}
	public List<Teem> getTeams() {
		return teams;
	}
	/**
	 * unregisters all Events from tda-Structures like flags etc to make sure old arenas can be unloaded completely
	 */
	private void unregisterAllEvents() {
		for(AbstractGameobject abs: gameobjects) {
			abs.unregister();
		}
		/*if(flags != null) {
			for(Flag f: flags) {
				for(FlagSpawnBlock ff: f.getConnections()) {
					HandlerList.unregisterAll(ff);
					ff = null;
				}
				f.cancelTasks();
				f = null;
			}
		}
		flags = null;

		if(catas != null) {
			for(Catapult c: catas) {
				HandlerList.unregisterAll(c);
				c = null;
			}
		}
		catas = null;

		if(gates != null) {
			for(Gate g: gates) {
				HandlerList.unregisterAll(g);
				g = null;
			}
		}
		gates = null;

		if(cannons != null) {
		    for (Cannon c: cannons) {
		    	HandlerList.unregisterAll(c);
            }
        }
		cannons = null;

		if(volcanoes != null) {
		    for(Volcano v : volcanoes) {
		        v.cancel();
		        v = null;
            }
        }
		volcanoes = null;*/

		if(this.dispenserChecker != null) {
			HandlerList.unregisterAll(this.dispenserChecker);
		}
	}
	private void finished() {
		stop();
		roundhandler.enableVotes();
		BossBar bar = Bukkit.createBossBar("vote started", BarColor.PINK, BarStyle.SOLID, BarFlag.CREATE_FOG);
		bar.setProgress(0.0);
		List<Player> not = MainHolder.main.getPlayersNotToTp();
		for(Player player: Bukkit.getOnlinePlayers()) {
			if (!not.contains(player)) {
				bar.addPlayer(player);
				player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, 300));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 15 * 20, 5));
				MapVoteUtils.openBook(player, roundhandler.getNextVotingRoundNames());
			}
		}
		RoundNew round = this;
		double step = (double)1/(20*15);
		new BukkitRunnable(){
			@Override
			public void run() {
				double prog = (bar.getProgress() + step);
				if(prog > 1.0) {
					bar.setProgress(1.0);
					cancel();
				} else {
					bar.setProgress(prog);
				}
			}
		}.runTaskTimer(MainHolder.main, 0, 1);

		new BukkitRunnable(){
			@Override
			public void run() {
				bar.setVisible(false);
				bar.removeAll();
				roundhandler.onFinish(round, true);
			}
		}.runTaskLater(MainHolder.main, 15*20);
	}



	private void stop() {
		if(runnable != null) {
			runnable.cancel();
		}
		if(swarm != null) {
			swarm.kill();
			swarm = null;
		}
		unregisterAllEvents();
	}

	public void kill() {
		if(staged) {
			stop();
			//unload all worlds
			/*if (Bukkit.getWorld("arena") != null) {
				boolean f = Bukkit.unloadWorld("arena", false);
				System.out.println("unloaded " + " arena " + f);
				arena = null;
			}*/

			for (Teem t : teams) {
				String spawnWorldName = "Spawn" + String.valueOf(t.getId()) + "" + spawnWorldSuffix;
				if (Bukkit.getWorld(spawnWorldName) != null) {
					t.setWorld(null);
					boolean f = Bukkit.unloadWorld(spawnWorldName, false);
					System.out.println("unloaded " + spawnWorldName + " " + f);
				}
			}
		}
	}


	public Swarm getSwarm() {
		return swarm;
	}

	/*public List<TopThing> getTopThings() {
		return topThings;
	}*/
}
