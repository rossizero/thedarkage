package de.peacepunkt.tda2plugin.structures.TopThing;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TopThingCommands {
	public TopThingCommands(Main main) {
		main.getCommand("setToplistDisplay").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] strings) {
				if (arg0 instanceof Player) {
					Player player = (Player) arg0;
					String worldname = player.getWorld().getName();//rh.getPlayRound().getRound().getArena().getName();
					Location location = player.getLocation();
					EditorNew editor = EditorHandler.getInstance().getEditor(player.getWorld());
					if(editor != null) {
						if(editor.isSpawnWorld()) {
							if(strings.length == 0) {
								TopThing topThing = new TopThing();
								topThing.type = 0;
								topThing.seed = Vector.vectorFromLocation(player.getLocation());
								topThing.worldname = worldname;
								editor.addStructure(topThing);
								player.sendMessage("Toplist display created!");
							} else if (strings.length == 1) {
								try {
									int type = Integer.parseInt(strings[0]);
									TopThing topThing = new TopThing();
									topThing.type =type;
									topThing.seed = Vector.vectorFromLocation(player.getLocation());
									topThing.worldname = worldname;
									editor.addStructure(topThing);
									player.sendMessage(type == 0 ? "Toplist display created!" : "Miniarena portal created!");
								} catch (NumberFormatException e) {
									return false;
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

				}
				return false;
			}

		});

		main.getCommand("removeTopThing").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
					if(e != null) {
						e.removeClosest(TopThing.class, p);
					}
				}
				return true;
			}
		});
	}
}
