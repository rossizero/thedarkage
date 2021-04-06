package de.peacepunkt.tda2plugin.team;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.peacepunkt.tda2plugin.MainHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.peacepunkt.tda2plugin.Main;

public class TeamCommands {
	public TeamCommands(Main main) {
		main.getCommand("sw").setExecutor(new CommandExecutor() {
			@Override
			public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
				if (arg0 instanceof Player) {
					//boolean tp = !((Player) arg0).getWorld().getName().equals("miniarena");
					boolean tpp = !(MainHolder.main.getPlayersNotToTp().contains((Player) arg0) || MainHolder.main.isPlayerInMiniArena((Player) arg0));
					if(tpp) {
						main.getRoundHandler().getRound().switchTeam((Player) arg0, true);
					}
				}
				return false;
			}
		});
	}
}
