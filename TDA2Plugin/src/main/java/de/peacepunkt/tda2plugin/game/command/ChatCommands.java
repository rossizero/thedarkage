package de.peacepunkt.tda2plugin.game.command;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.*;

public class ChatCommands implements Listener {
    List<Player> teamchat;
    Map<Player, Player> lockedPm;
    Main main;
    public ChatCommands(Main main) {
        this.teamchat = new ArrayList<>();
        this.lockedPm = new HashMap<>();
        this.main = main;
        main.getCommand("teamchat").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    teamchat.add(sender);
                    sender.sendMessage(ChatColor.GREEN + "Now chatting in your teams chat.");
                }
                return false;
            }
        });
        main.getCommand("whois").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    if(strings.length == 1) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                try {
                                    Player sender = (Player) commandSender;
                                    NameHistoryOfPlayer.PreviousPlayerNameEntry[] previousNames = NameHistoryOfPlayer.getPlayerPreviousNamesByUsername(strings[0]);
                                    if(previousNames != null) {
                                        sender.sendMessage(ChatColor.RED + "-");
                                        sender.sendMessage(ChatColor.GREEN + "Here you go little stalker:");

                                        for (NameHistoryOfPlayer.PreviousPlayerNameEntry entry : previousNames) {
                                            if (entry.getChangeTime() == 0) {
                                                sender.sendMessage(ChatColor.GREEN + "     original username: " + ChatColor.DARK_RED + entry.getPlayerName());
                                            } else {
                                                sender.sendMessage(ChatColor.GREEN + "     changed to: " + ChatColor.DARK_RED + entry.getPlayerName() + ChatColor.GREEN + " on the " + new Date(entry.getChangeTime()));
                                            }
                                        }
                                        sender.sendMessage("");
                                        if(Bukkit.getPlayer(strings[0]) != null) {
                                            sender.sendMessage(ChatColor.GREEN + "last seen: " + new PlayerStatsDaoImpl().getMyStats(Bukkit.getPlayer(strings[0]).getUniqueId().toString()).getModifyDate().toString());
                                        }
                                        sender.sendMessage(ChatColor.RED + "-");
                                    } else {
                                        sender.sendMessage(ChatColor.GREEN + "Dude, thats not a player...");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.runTaskAsynchronously(main);
                        return true;

                    }
                }
                return false;
            }
        });
        main.getCommand("bomb").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if(sender.getWorld().getName().equals("arena")) {
                        new ExplosiveFallingBlock(main, sender.getLocation().clone().add(0, 6, 0), Material.BLACK_CONCRETE)
                                .setExplosionRadius(2)
                                .setTimeout(30)
                                .setVelocity(new Vector(0, -0.4, 0))
                                .setMakeExplodedBlocks(true);
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "You can only do this in the arena.");
                    }
                }
                return false;
            }
        });
        main.getCommand("globalchat").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    teamchat.remove(sender);
                    sender.sendMessage(ChatColor.GREEN + "Now chatting in global chat.");
                }
                return false;
            }
        });
        main.getCommand("pm").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if(strings.length == 1) {
                        Player target = Bukkit.getPlayer(strings[0]);
                        if(target != null) {
                            lockedPm.put(sender, target);
                            sender.sendMessage(ChatColor.GREEN + "Now chatting with " + target.getName() + ". To leave this private chat simply type /pm");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + strings[0] + " is not an online player...");
                            return false;
                        }
                    } else if (strings.length > 1) {
                        Player target = Bukkit.getPlayer(strings[0]);
                        Teem t = main.getRoundHandler().getRound().getTeam(sender);
                        if(target != null) {
                            String message = "";
                            int count = 0;
                            for(String str : strings) {
                                if(count != 0) {
                                    message += str + " ";
                                }
                                count++;
                            }
                            Teem t2 = main.getRoundHandler().getRound().getTeam(target);
                            sender.sendMessage(ChatColor.LIGHT_PURPLE+"[to " + t2.getChatColor() + target.getName()+ChatColor.LIGHT_PURPLE+"] " + ChatColor.RESET + message);
                            target.sendMessage(ChatColor.LIGHT_PURPLE+"[from " + t.getChatColor()+sender.getName()+ChatColor.LIGHT_PURPLE+"] " + ChatColor.RESET + message);
                            System.out.println("[PM] from " + sender.getName() + " to " + target.getName() + ": " +  message);

                        } else {
                            sender.sendMessage(ChatColor.GREEN + strings[0] + " is not an online player...");
                            return false;
                        }
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "now chatting with everybody again");
                        lockedPm.remove(sender);
                    }
                }
                return false;
            }
        });
    }
    public boolean isTeamChat(Player player) {
        return teamchat.contains(player);
    }
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        teamchat.remove(event.getPlayer());
        lockedPm.remove(event.getPlayer());
    }
    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event){
        if(lockedPm.containsKey(event.getPlayer())) {
            Teem t = main.getRoundHandler().getRound().getTeam(event.getPlayer());
            Teem t2 = main.getRoundHandler().getRound().getTeam(lockedPm.get(event.getPlayer()));
            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE+"[to " + t2.getChatColor() + lockedPm.get(event.getPlayer()).getName()+ChatColor.LIGHT_PURPLE+"] " + ChatColor.RESET+ event.getMessage());
            lockedPm.get(event.getPlayer()).sendMessage(ChatColor.LIGHT_PURPLE+"[from " + t.getChatColor() + event.getPlayer().getName()+ChatColor.LIGHT_PURPLE+"] " + ChatColor.RESET+ event.getMessage());
            System.out.println("[Private Message] from " + event.getPlayer().getName() + " to " + lockedPm.get(event.getPlayer()).getName() + ": " +  event.getMessage());
            event.setCancelled(true);
        } else {
            if (isTeamChat(event.getPlayer())) {
                Teem t = main.getRoundHandler().getRound().getTeam(event.getPlayer());
                System.out.println("[Team Message] from " + event.getPlayer().getName() + ": " + event.getMessage());
                for(Player player : t.getInmates()) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE+"[TEAM] " + ChatColor.RESET +"<"+t.getChatColor()+event.getPlayer().getName()+ ChatColor.RESET +"> " + event.getMessage());
                }
                event.setCancelled(true);
            }
        }
    }
}
