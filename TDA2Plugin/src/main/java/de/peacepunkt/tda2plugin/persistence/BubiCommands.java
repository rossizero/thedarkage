package de.peacepunkt.tda2plugin.persistence;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BubiCommands {
    public BubiCommands(Main main) {
        main.getCommand("bubi").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if(strings.length > 0) {
                        if(strings[0].equals("top")) {
                            int count = 1;
                            player.sendMessage(ChatColor.DARK_PURPLE + "Top 13 Bubi killer:");
                            for(BubiCounter bubiCounter: new BubiCounterDaoImpl().getTopList()) {
                                player.sendMessage(ChatColor.DARK_PURPLE + "" + count + ChatColor.WHITE +": " +  Bukkit.getOfflinePlayer(UUID.fromString(bubiCounter.getUuid())).getName() + " (" + bubiCounter.getCount()+")");
                                count++;
                            }
                            return true;
                        }
                    } else {
                        BubiCounter counter = new BubiCounterDaoImpl().get(player);
                        if(counter != null) {
                            player.sendMessage(ChatColor.DARK_PURPLE + "Your Bubi kills: " +  ChatColor.WHITE + counter.getCount());
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
