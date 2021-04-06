package de.peacepunkt.tda2plugin.structures.flag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import de.peacepunkt.tda2plugin.structures.Vector;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class FlagCommands implements Listener {
	static Map<OfflinePlayer, FlagCustomBlock> customBlockMap = new HashMap<>();

	public FlagCommands(Main main) {
		main.getCommand("setFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg0 instanceof Player) {
					Player player = (Player) arg0;
					if (arg3.length >= 2) {
						try {
							int wantedTeamId = Integer.parseInt(arg3[0]);
							boolean conquerable = true;
							if (arg3[1].equals("false")) {
								conquerable = false;
							}
							String name = "";
							if (arg3.length >= 3) {
								name = arg3[2].replace("_", " ");
							}
							int type = 0;
							if (arg3.length == 4) {
								type = Integer.parseInt(arg3[3]);
							}

							Flag flag = new Flag();
							flag.seed = Vector.vectorFromLocation(player.getLocation());
							flag.direction = StructureUtils.getDir(player);
							flag.birds = false;
							flag.name = name;
							flag.type = type;
							flag.conquerable = conquerable;
							flag.connections = new ArrayList<>();
							flag.customBlocks = new ArrayList<>();
							flag.spawn = flag.seed;
							flag.teamId = wantedTeamId;

							EditorNew e = EditorHandler.getInstance().getEditor(player.getWorld());
							if (e != null) {
								int biggest_id = 0;
								for(AbstractGameobject abs: e.getAll(Flag.class)) {
									Flag f = (Flag) abs.getStructure();
									if(f.id > biggest_id)
										biggest_id = f.id;
								}
								flag.id = biggest_id+1;

								for (Teem teem : e.getTeams()) {
									FlagSpawnBlock flagSpawnBlock = new FlagSpawnBlock();
									flagSpawnBlock.seed = new Vector(0, 0, 0);
									flagSpawnBlock.teamId = teem.ID;
									flag.connections.add(flagSpawnBlock);
								}
								e.addStructure(flag);
								player.sendMessage("Flag created!");
								return true;
							}
						} catch (Exception e) {
							player.sendMessage("/setflag teamId [0 1] conquerable [true/false] [name] Optional:type[0 1 2]");
							return false;
						}
					} else {
						player.sendMessage("/setflag teamId [0 1] conquerable [true/false] [name] Optional:type[0 1 2]");
					}
				}
				return false;
			}
		});

		main.getCommand("getFlags").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
					if(e != null) {
						FlagGameobject closest = (FlagGameobject) e.getClosest(Flag.class, p);
						for(AbstractGameobject abs: e.getAll(Flag.class)) {
							Flag f = (Flag) abs.getStructure();
							if(closest != null) {
								if(f.equals(closest.getStructure())) {
									p.sendMessage("Flag name: " + f.name + " | id " + f.id + " <--- closest to you");
								} else {
									p.sendMessage("Flag name: " + f.name + " | id " + f.id);
								}
							} else {
								p.sendMessage("Flag name: " + f.name + " | id " + f.id);
							}
						}
					}
				}
				return false;
			}
		});

		main.getCommand("showSpawnOfFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject closest = getFlagById(id, p);
							if (closest != null) {
								Location target = Vector.locationFromVector(p.getWorld(), ((Flag) closest.getStructure()).spawn);
								Material old = target.getBlock().getType();
								target.getBlock().setType(Material.END_GATEWAY);
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);
								return true;
							}
						} else {
							p.sendMessage("No flag with that id!");
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
		main.getCommand("setTeamOfFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject closest = (FlagGameobject) e.getClosest(Flag.class, p);
							if (closest != null) {
								Flag flag = (Flag) closest.getStructure();
								flag.teamId = id;
								e.saveAll();
								e.reload();
								p.sendMessage("set team-id to team " + flag.teamId);
							}
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
		main.getCommand("setCustomFlagBlock").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 2) {
					try {
						int id = Integer.parseInt(strings[0]);
						int status = Integer.parseInt(strings[1]);
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject flag = getFlagById(id, p);
							FlagCustomBlock b = new FlagCustomBlock();
							b.status = status;
							b.setFlag(flag);
							customBlockMap.put(Bukkit.getOfflinePlayer(p.getUniqueId()), b);
							return true;
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
		main.getCommand("showCappingZoneOfFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject closest = getFlagById(id, p);
							Flag flag = (Flag) closest.getStructure();
							Location start = closest.getCappingLocation();

							for(int i = (int) Math.round(-flag.cappingRadius); i < flag.cappingRadius+1; i++) {
								for(int k = (int) Math.round(-flag.cappingRadius); k < flag.cappingRadius+1; k++) {
									for (int j = 0; j < flag.cappingHeight; j++) {
										//only draw edges
										if((i == (int) Math.round(-flag.cappingRadius) || i == flag.cappingRadius) || (k == (int) Math.round(-flag.cappingRadius) || k == flag.cappingRadius) || (j == 0 || j == flag.cappingHeight-1)) {
											if((i+j+k)%4 == 0)
												e.getWorld().spawnParticle(Particle.BARRIER, start.clone().add(new org.bukkit.util.Vector(i + 0.5, j, k + 0.5)), 10);
										}
									}
								}
							}
						}
						return true;
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
		main.getCommand("setCappingZoneOfFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						int cappingRadius = -1;
						int cappingHeight = -1;
						if(strings.length >= 2) {
							cappingRadius = Integer.parseInt(strings[1]);
						}
						if(strings.length >= 3) {
							cappingHeight = Integer.parseInt(strings[2]);
						}
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject closest = getFlagById(id, p);
							if (closest != null) {
								Flag flag = (Flag) closest.getStructure();
								Vector capping = Vector.vectorFromLocation(p.getLocation());
								flag.cappingZone = capping;
								if(cappingRadius > 0)
									flag.cappingRadius = cappingRadius;
								if(cappingHeight > 0)
									flag.cappingHeight = cappingHeight;
								e.saveAll();
								e.reload();

								Location target = Vector.locationFromVector(p.getWorld(), capping);
								Material old = target.getBlock().getType();
								target.getBlock().setType(Material.END_GATEWAY);
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);
								return true;
							}
						} else {
							p.sendMessage("No flag with that id!");
							return false;
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
		main.getCommand("setSpawnOfFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if (commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						Player p = (Player) commandSender;
						EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
						if (e != null) {
							FlagGameobject closest = getFlagById(id, p);
							if (closest != null) {
								Flag flag = (Flag) closest.getStructure();
								Vector spawn = Vector.vectorFromLocation(p.getLocation());
								flag.spawn = spawn;
								flag.spawnRotation = StructureUtils.getDir(p);
								e.saveAll();
								e.reload();

								Location target = Vector.locationFromVector(p.getWorld(), spawn);
								Material old = target.getBlock().getType();
								target.getBlock().setType(Material.END_GATEWAY);
								Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);
								return true;
							}
						} else {
							p.sendMessage("No flag with that id!");
							return false;
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});

		main.getCommand("removeFlag").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
					if(e != null) {
						e.removeClosest(Flag.class, p);
					}
				}
				return true;
			}
		});

		main.getCommand("removeNameTags").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if(commandSender instanceof Player) {
					Player player = (Player) commandSender;
					EditorNew editor = EditorHandler.getInstance().getEditor(player.getWorld());
					if(editor != null) {
						if (editor.isSpawnWorld()) {
							editor.removeNameTags();
						}
					}
					return true;
				}
				return false;
			}
		});

		main.getCommand("setSpawnBlock").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
				if(commandSender instanceof Player && strings.length >= 1) {
					try {
						int id = Integer.parseInt(strings[0]);
						Player player = (Player) commandSender;
						EditorNew editor = EditorHandler.getInstance().getEditor(player.getWorld());
						if (editor != null) {
							if (editor.isSpawnWorld()) {
								int teamId = Integer.parseInt(player.getWorld().getName().split("Spawn")[1]);
								FlagGameobject closest = getFlagById(id, player);
								Flag flag = (Flag) closest.getStructure();
								for (FlagSpawnBlock fsb : flag.connections) {
									if (fsb.teamId == teamId) {
										fsb.seed = Vector.vectorFromLocation(player.getLocation());
										player.sendMessage("Block configured!");
										editor.saveAll();
										editor.reload();
										return true;
									}
								}
							} else {
								player.sendMessage("Not a Spawn world.. has to have a name like NameOfMap+Spawn+TeamId");
								player.sendMessage("check your current location with /worlds");
								return false;
							}
						} else {
							player.sendMessage("You somehow got into this world without /edit. Please do /edit worldname to use commands from tda3 plugin.");
						}
					} catch (NumberFormatException e) {
						return false;
					}
				}
				return false;
			}
		});
	}

	private FlagGameobject getFlagById(int id, Player player) {
		EditorNew editor = EditorHandler.getInstance().getEditor(player.getWorld());
		if(editor != null) {
			for(AbstractGameobject abs: editor.getAll(Flag.class)) {
				Flag flag = (Flag) abs.getStructure();
				if(flag.id == id)
					return (FlagGameobject) abs;
			}
		}
		return null;
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		OfflinePlayer player = Bukkit.getOfflinePlayer(event.getPlayer().getUniqueId());
		if(customBlockMap.containsKey(player)) {
			EditorNew editor = EditorHandler.getInstance().getEditor(event.getPlayer().getWorld());
			if(editor != null) {
				FlagCustomBlock block = customBlockMap.get(player);
				block.location = Vector.vectorFromLocation(event.getBlock().getLocation());
				Flag flag = (Flag) block.getFlag().getStructure();
				event.getPlayer().sendMessage("Added new FlagCustomBlock to flag " + flag.id + " with change at flag status " + block.status);
				if (flag.customBlocks == null)
					flag.customBlocks = new ArrayList<>();
				flag.customBlocks.add(block);
				customBlockMap.remove(player);
				event.setCancelled(true);
				editor.saveAll();
				editor.reload();
			}
		} else {
			EditorNew editor = EditorHandler.getInstance().getEditor(event.getPlayer().getWorld());
			if(editor != null) {
				for(AbstractGameobject flag : editor.getAll(Flag.class)) {
					FlagGameobject flagGameobject = (FlagGameobject) flag;
					Flag f = (Flag) flagGameobject.getStructure();
					if(f.customBlocks != null) {
						for (FlagCustomBlock b : f.customBlocks) {
							if (b.isAt(event.getBlock().getLocation()) && !b.getFlag().isNoFunc()) { //TODO NPE in spawn worlds
								f.customBlocks.remove(b);
								event.getPlayer().sendMessage("You removed FlagCustomBlock of flag " + f.id + " at " + b.location.toString() + " " + f.customBlocks.size() + " custom blocks left.");
								editor.saveAll();
								editor.reload();
								return;
							}
						}
					}
				}

			}
		}
	}
}
