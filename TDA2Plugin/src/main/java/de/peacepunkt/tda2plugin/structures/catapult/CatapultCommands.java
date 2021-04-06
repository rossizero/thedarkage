package de.peacepunkt.tda2plugin.structures.catapult;

import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.peacepunkt.tda2plugin.Main;

public class CatapultCommands {
	public CatapultCommands(Main main) {
		main.getCommand("setCata").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if(arg0 instanceof Player) {
					Player player = (Player) arg0;
					Catapult catapult = new Catapult();
					catapult.seed = Vector.vectorFromLocation(player.getLocation());
					catapult.dir = StructureUtils.getDir(player);
					EditorNew e = EditorHandler.getInstance().getEditor(player.getWorld());
					if(e != null) {
						e.addStructure(catapult);
						player.sendMessage("Catapult created!");
					}
				}
				return false;
			}
		});

		main.getCommand("removeCata").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender commandSender, Command arg1, String arg2, String[] arg3) {
				if (commandSender instanceof Player) {
					Player p = (Player) commandSender;
					EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
					if(e != null) {
						e.removeClosest(Catapult.class, p);
					}
				}
				return true;
			}
		});
	}
}
