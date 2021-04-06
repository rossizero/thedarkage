package de.peacepunkt.tda2plugin.minipvp.Commands;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.minipvp.MiniPvPHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MiniPvPCommands {
    public MiniPvPCommands(Main main, MiniPvPHandler handler) {
        main.getCommand("entertest").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    if(!((Player) commandSender).getWorld().getName().equals("arena")) {
                        if(!EditorHandler.getInstance().isPlayerInEditMode((Player)commandSender)) {
                            handler.addPlayer((Player) commandSender);
                        } else {
                            commandSender.sendMessage(ChatColor.GREEN+"Do /editleave first!");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.GREEN+"You can only tp to the test arena from your spawn!");
                    }
                    return true;
                }
                return false;
            }
        });
        main.getCommand("leavetest").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    handler.removePlayer((Player) commandSender);
                    return true;
                }
                return false;
            }
        });
        main.getCommand("addMiniPvpArenaSpawn").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    if(((Player) commandSender).getWorld().getName().equals("miniarena")) {
                        int type = 0;
                        ((Player) commandSender).sendMessage(strings);
                        ((Player) commandSender).sendMessage(String.valueOf(strings.length));
                        if(strings.length > 0) {
                            type = Integer.parseInt(strings[0]);
                        }
                        handler.addSpawn((Player) commandSender, type);
                        ((Player) commandSender).sendMessage(ChatColor.GREEN + "Spawn added! (type: " + type +")");
                        handler.showSpawns();
                        return false;
                    } else {
                        ((Player) commandSender).sendMessage(ChatColor.GREEN + "You have to be in the miniarena!");
                        return true;
                    }
                }
                return false;
            }
        });
        main.getCommand("showMiniPvpArenaSpawns").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                handler.showSpawns();
                return true;
            }
        });
    }
}
