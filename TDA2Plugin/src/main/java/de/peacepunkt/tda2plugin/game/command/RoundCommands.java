package de.peacepunkt.tda2plugin.game.command;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.def.DragonRider;
import de.peacepunkt.tda2plugin.def.Swarm;
import de.peacepunkt.tda2plugin.game.RoundUtils;
import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.team.TeemUtils;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundCommands {
	public RoundCommands(Main main) {
		main.getCommand("maps").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if(commandSender instanceof Player) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Player player = (Player) commandSender;
							player.sendMessage("All maps:");
							for(String name: RoundUtils.getStagedArenaDisplayNames()) {
								player.sendMessage(ChatColor.GRAY + "   * " + name);
							}
							player.sendMessage("Next map (if not voted otherwise): " + ChatColor.GREEN + RoundUtils.getDisplayNameOfMap(main.getRoundHandler().getNextRoundName()));
						}
					}.runTaskAsynchronously(main);

				}
				return true;
			}
		});
		main.getCommand("swarm").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					int number = 1;
					if(strings.length == 1) {
						number = Integer.parseInt(strings[0]);
					}
					if(p.isOp()) {
						//new Swarm(p.getLocation(), MainHolder.main, number);
					}
				}
				return false;
			}
		});
		main.getCommand("dragon").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					Player target = null;
					if(strings.length == 1) {
						target = Bukkit.getPlayer(strings[0]);
					}
					if(target == null) {
						target = p;
					}
					if(p.isOp() && p.getWorld().getName().equals("arena")) {
						if(target.getVehicle() == null || !(target.getVehicle() instanceof EnderDragon)) {
							new DragonRider(target, main).spawn();
						}
					}
				}
				return false;
			}
		});


		main.getCommand("howmany").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if(commandSender instanceof Player) {
					Player sender = (Player) commandSender;
					int number = new PlayerStatsDaoImpl().getNumberOfPlayers();
					sender.sendMessage(String.valueOf(number) + " players have joined the server so far.");
					List<PlayerStats> today = new PlayerStatsDaoImpl().getPlayerOnDay(LocalDate.now());
					if(today != null) {
						sender.sendMessage(ChatColor.GREEN + "today:");
						for(PlayerStats ps : today) {
							sender.sendMessage(ChatColor.GREEN + "    - " + ps.getUsername());
						}
					}
					List<PlayerStats> yesterday = new PlayerStatsDaoImpl().getPlayerOnDay(LocalDate.now().minusDays(1));
					if(today != null) {
						sender.sendMessage(ChatColor.GREEN + "yesterday:");
						for(PlayerStats ps : yesterday) {
							sender.sendMessage(ChatColor.GREEN + "    - " + ps.getUsername());
						}
					}
				}
				return true;
			}
		});
		main.getCommand("warp").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg0 instanceof Player) {
					if(arg3.length == 1) {
						Player p = (Player) arg0;
						String worldName = arg3[0];
						boolean done = false;
						World target = Bukkit.getWorld(worldName);
						if(target != null) {
							Location loc = new Location(target, target.getSpawnLocation().getX(),target.getSpawnLocation().getY(), target.getSpawnLocation().getZ());
				            p.teleport(loc); 
				            done = true;
						}
						if(!done) {
							p.sendMessage("World " + arg3[0] + " could be unloaded.");
						}
						return true;
					} else {
						Player p = (Player) arg0;
						p.sendMessage("/warp <worldName>");
					}
				} 
				return false;
			}
		});
		
		main.getCommand("makeArenaFrom").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg0 instanceof Player) {
					//0: name of world, 1: number of teams
					if(arg3.length == 2) {
							try {
							String nameOfWorld = arg3[0];
							int numOfTeams = Integer.valueOf(arg3[1]);
							if(Bukkit.getWorld(nameOfWorld) == null) 
								addArena(nameOfWorld, numOfTeams);
							for(int i = 0; i < numOfTeams; i++) {
								createWorld(main, nameOfWorld+"Spawn"+String.valueOf(i), (Player) arg0);
							}
							return true;
						} catch (Exception e) {
							Player p = (Player) arg0;
							p.sendMessage("/makeArenaFrom <worldName> <numberOfTeams>");
						}
					} else {
						Player p = (Player) arg0;
						p.sendMessage("/makeArenaFrom <worldName> <numberOfTeams>");
					}
				}
				return false;
			}
		});

		main.getCommand("makeNewArena").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg0 instanceof Player) {
					//0: name of world, 1: number of teams
					if(arg3.length == 2) {
							try {
							String nameOfWorld = arg3[0];
							int numOfTeams = Integer.valueOf(arg3[1]);
							if(Bukkit.getWorld(nameOfWorld) == null) 
								addArena(nameOfWorld,numOfTeams);
							
							createWorld(main, nameOfWorld, (Player) arg0);
							for(int i = 0; i < numOfTeams; i++) {
								createWorld(main, nameOfWorld+"Spawn"+String.valueOf(i), (Player) arg0);
							}
							return true;
						} catch (Exception e) {
							Player p = (Player) arg0;
							p.sendMessage("/makeArenaFrom <worldName> <numberOfTeams>");
						}
					} else {
						Player p = (Player) arg0;
						p.sendMessage("/makeArenaFrom <worldName> <numberOfTeams>");
					}
				}
				return false;
			}
		});
		main.getCommand("mapvote").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if(commandSender instanceof Player) {
					Player player = (Player) commandSender;
					if(strings.length > 0) {
						try {
							int id = Integer.parseInt(strings[0]);
							main.getRoundHandler().vote(player, id);
						} catch (NumberFormatException ignored) {
						}
					}
				}
				return true;
			}
		});
		main.getCommand("skip").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if(arg3.length == 0) {
					main.skip(null);
				} else {
					main.skip(arg3[0]);
				}
				return true;
			}
		});

		main.getCommand("getArenas").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if( commandSender instanceof Player) {
					commandSender.sendMessage("There are following arenas: \n");
					Map<String, Boolean> map = RoundUtils.getAllArenas();
					for(String name : map.keySet()) {
						if(map.get(name)) {
							commandSender.sendMessage("*    " + name + " (staged: " + map.get(name) + ")");
						} else {
							commandSender.sendMessage(ChatColor.GRAY + "*    " +  name + " (staged: " + map.get(name) + ")");
						}
					}
					commandSender.sendMessage(ChatColor.GRAY+"You can access them with /edit name or their spawn worlds with /edit nameSpawn[0/1/2/...] depending on how many teams there are for the selected map.");
				}
				return false;
			}
		});
		main.getCommand("worlds").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if( arg0 instanceof Player) {
					arg0.sendMessage("Found following currently LOADED worlds:");
					Map<World, Integer> count = new HashMap<>();
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(!count.containsKey(p.getWorld())) {
							count.put(p.getWorld(), 1);
						} else {
							int tmp = count.get(p.getWorld());
							tmp++;
							count.put(p.getWorld(), Integer.valueOf(tmp));
						}
					}
					for(World w : Bukkit.getWorlds()) {
						if(!count.containsKey(w)) {
							count.put(w, 0);
						}
					}
					for (World w : count.keySet()) {
						if(((Player) arg0).getWorld().equals(w)) {
							arg0.sendMessage(count.get(w) == 1 ? w.getName() + " | " +  ChatColor.RED +" only you" : w.getName() + " | " +  ChatColor.GREEN +  count.get(w) + " Players (incl. "+ChatColor.RED+"you"+ChatColor.GREEN+")");
						} else {
							arg0.sendMessage(count.get(w) == 1 ? w.getName() + " | " +  count.get(w) + " Player" : w.getName() + " | " + ChatColor.GREEN + count.get(w) + " Players");
						}
					}
				}
				return true;
			}
		});

		main.getCommand("suicide").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if( commandSender instanceof Player) {
					Player p = (Player) commandSender;
					boolean tpp = !(MainHolder.main.getPlayersNotToTp().contains(p) || MainHolder.main.isPlayerInMiniArena(p));
					if(tpp) {
						p.sendMessage(ChatColor.GRAY + "You will be killed in 3 secs. Have fun!");
						new BukkitRunnable() {
							@Override
							public void run() {
								p.setHealth(0);
							}
						}.runTaskLater(main, 3 * 20);
					}
				}
				return true;
			}
		});
	}

	public void createWorld(Main main, String name, Player notifyTarget) {
		if(Bukkit.getWorld(name) != null) {
			if(notifyTarget != null)
				notifyTarget.sendMessage("this world already exists");
			return;
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				if(notifyTarget == null)
					System.out.println("starting world creation");
				else
					notifyTarget.sendMessage(ChatColor.GREEN + "starting world creation");
				WorldCreator creator = new WorldCreator(name);
				//creator.type(WorldType.CUSTOMIZED);
				creator.environment(World.Environment.NORMAL);
				creator.generateStructures(false);
				creator.generator(new SpawnWorldGenerator());
				World w = Bukkit.createWorld(creator);

				w.setGameRule(GameRule.DO_FIRE_TICK, false);
				w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
				w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
				w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
				w.setGameRule(GameRule.MOB_GRIEFING, false);
				w.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
				w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
				w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
				w.setSpawnLocation(0, 151, 0);
				w.setTime(1000);
				if(notifyTarget == null)
					System.out.println("World creation successful.");
				else
					notifyTarget.sendMessage(ChatColor.GREEN + "world " + name + " was created");
			}
			
		});
	}
	
	public static void addArena(String worldName, int numberOfTeams) {
		//creates arenas folder
		new File(Main.arenasPath+"/"+worldName).mkdirs();
		//adds entry in arenas.yml
		try {
			YamlConfiguration config = new YamlConfiguration();
			//creates all necessary files
			new File(Main.arenasPath+worldName+"/flags.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/flagsSpawn.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/catas.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/gates.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/volcanos.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/cannons.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/topThings.yml").createNewFile();
			new File(Main.arenasPath+worldName+"/settings.yml").createNewFile();

			File team = new File(Main.arenasPath+worldName+"/teams.yml");
			team.createNewFile();
			
			//add entry to arenas list
			File f = new File(Main.arenasPath+"arenas.yml");
			if(!f.exists()) {
				f.createNewFile();
			}
			config.load(f);
			List<String> list = (List<String>) config.getList("arenas");
			if(list == null) {
				list = new ArrayList<>();
			}
			list.add(worldName);
			config.set("arenas", list);
			config.save(f);
			
			//create List with numberOfTeams entrys and default Team values
			List<Map> map = new ArrayList<>();
			//Map<Integer, Map<String, String>> map = new HashMap<>();
			config = new YamlConfiguration();
			config.load(team);
			for(int i = 0; i < numberOfTeams; i++) {
				Map<String, String> teamList = new HashMap<>();
				teamList.put("idTheme", String.valueOf(i));
				teamList.put("name", "default"+String.valueOf(i));
				teamList.put("defaultKit", "Swordsman");
				teamList.put("id",  String.valueOf(i));
				map.add(teamList);
			}
			config.set("teams", map);
			config.save(team);
			//create List with kits
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
	
}
