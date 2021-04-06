package de.peacepunkt.tda2plugin.kits;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.gui.BuyGUI;
import de.peacepunkt.tda2plugin.persistence.novote.Extra;
import de.peacepunkt.tda2plugin.persistence.novote.ExtraDaoImpl;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ExtraCommands {
    Map<Player, Player> pendingRecruitRequests;
    public ExtraCommands(Main main) {
        pendingRecruitRequests = new HashMap<>();
        main.getCommand("buy").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                BuyGUI gui = new BuyGUI(main);
                main.getServer().getPluginManager().registerEvents(gui, main);
                gui.openInventory((Player)commandSender);
                return true;
            }
        });
        main.getCommand("xp").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    Xp xp = new XpDaoImpl().get(sender.getUniqueId().toString());
                    if (xp != null) {
                        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "New Xp will be added at the end of the round.");
                        sender.sendMessage(ChatColor.GREEN + "Current Xp: " + ChatColor.DARK_RED + xp.getXp());
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Nope I don't find you in the database, sorry. Maybe just wait for the round to finish...");
                    }
                }
                return false;
            }
        });
        main.getCommand("kit").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if(!sender.getWorld().getName().equals("arena")) {
                        if (strings.length == 1) {
                            String classname = strings[0];
                            KitHandler.getInstance().setKit(sender, classname, true, true, false);
                            //Kits.getInstance(main).setKit(sender, classname, true, true, false);
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.GREEN + "Nope you can't change kit in the arena...");
                        return true;
                    }
                }
                return false;
            }
        });
        main.getCommand("addxp").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if(strings.length == 2) {
                        try {
                            String targetName = strings[0];
                            int xp = Integer.parseInt(strings[1]);
                            Player target = Bukkit.getPlayer(targetName);
                            if(target != null) {
                                Xp xpp = new XpDaoImpl().get(target.getUniqueId().toString());
                                xpp.addXp(xp, false);
                                target.sendMessage(ChatColor.GREEN + "We gave you " + ChatColor.DARK_RED + xp + ChatColor.GREEN + " xp! Be nice and say thanks!");
                                sender.sendMessage(ChatColor.GREEN + "xp sent!");
                                new XpDaoImpl().update(xpp);
                            }
                        } catch (Exception e) {

                        }
                    }
                }
                return false;
            }
        });
        main.getCommand("vote").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    sender.sendMessage(ChatColor.RED+"-");
                    sender.sendMessage(ChatColor.GREEN + "By voting you get some XP to buy 'premium' classes. Useful commands: /xp and /buy");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.GREEN+"To get Sharpness and stuff " +
                            "we decided to go for a recruitment system. You can recruit friends with /recruited <player name>. " +
                            "Useful commands /recruited and /recruitmentstats for more details!");
                    sender.sendMessage("");
                    sender.sendMessage("https://www.serverliste.net/vote/3469");
                    sender.sendMessage("https://topg.org/Minecraft/in-607235");
                    sender.sendMessage("https://minebrowse.com/server/1102");
                    sender.sendMessage("https://list-minecraft-server.com/server-thedarkage.293/vote");
                    sender.sendMessage("https://www.planetminecraft.com/server/thedarkage/vote/");
                    sender.sendMessage("https://minecraft-server.net/details/sero97/");
                    sender.sendMessage("https://minecraft-mp.com/server/261147/vote/");
                    sender.sendMessage("https://minecraftservers.org/vote/588316");
                    sender.sendMessage(ChatColor.RED+"-");

                    //main.voteListener.voted(Bukkit.getOfflinePlayer(sender.getUniqueId()));
                }
                return true;
            }
        });
        main.getCommand("recruitmentstats").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    /*1 = more ladders & more arrows
                    2 = feather falling * extra heart
                    3 = sharpness 1
                    4 = bold displayName or so*/
                    int level = new ExtraDaoImpl().getExtraLevelOfPlayer(sender);
                    sender.sendMessage(ChatColor.RED + "-");
                    sender.sendMessage(ChatColor.GREEN+"Find someone to recruit and do /recruited <recruits name> to get following perks:");
                    sender.sendMessage(ChatColor.GREEN+"Your recruitment level: " + ChatColor.RED + level);
                    sender.sendMessage(ChatColor.GREEN+"You recruited:");
                    for(String name: new ExtraDaoImpl().getRecruited(sender)) {
                        sender.sendMessage(ChatColor.GREEN+"    "+name);
                    }
                    sender.sendMessage(ChatColor.GREEN+"You were recruited by: " + new ExtraDaoImpl().getRecruiter(sender));
                    sender.sendMessage((level >= 1 ? ChatColor.GREEN : ChatColor.GRAY) + "1 : more ladders & more arrows");
                    sender.sendMessage((level >= 2 ? ChatColor.GREEN : ChatColor.GRAY) + "2 : feather falling");
                    sender.sendMessage((level >= 3 ? ChatColor.GREEN : ChatColor.GRAY) + "3 : Sharpness I");
                    sender.sendMessage((level >= 4 ? ChatColor.GREEN : ChatColor.GRAY) + "4 : extra heart &"+ ChatColor.BOLD + " bold name in Chat");
                    sender.sendMessage(ChatColor.GREEN+"Those perks add up! Some won't be applied to some of the special classes though.");
                    sender.sendMessage(ChatColor.RED + "-");
                    return true;
                }
                return false;
            }
        });
        main.getCommand("recruited").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player sender = (Player) commandSender;
                    if (strings.length == 1) {
                        String targetName = strings[0];
                        Player target = Bukkit.getPlayer(targetName);
                        if (target != null) {
                            if(!target.equals(sender)) {
                                if (pendingRecruitRequests.containsValue(target)) {
                                    sender.sendMessage(ChatColor.GREEN + "This player has already been requested by someone else. Tell him to deny the recruitment request and accept yours!.");
                                    target.sendMessage(ChatColor.GREEN + target.getName() + " also wants to recruit you. Type /denyrecruitment");
                                    return true;
                                } else {
                                    sender.sendMessage(ChatColor.GREEN + "I'll just let " + targetName + " validate this request real quick...");
                                    target.sendMessage(ChatColor.GREEN + sender.getName() + " claims to have recruited you. If this is true type /acceptrecruitment otherwise just ignore me...");
                                    pendingRecruitRequests.put(sender, target);
                                    return true;
                                }
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "I am not mad at you, I am just disappointed...");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN +"Your recruit has to be on this server. Just tell them to get online real quick!");
                            return true;
                        }

                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
        main.getCommand("denyrecruitment").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player target = (Player) commandSender;
                    for(Player p : pendingRecruitRequests.keySet()) {
                        if(pendingRecruitRequests.get(p).equals(target)) {
                            pendingRecruitRequests.remove(p);
                            target.sendMessage(ChatColor.GREEN + "denied recruitment...");
                            p.sendMessage(ChatColor.GREEN +target.getName()+" denied your recruitment. Shame on you!");
                            return true;
                        }
                    }
                    target.sendMessage(ChatColor.GREEN + "There was no pending recruitment request for you...");
                }
                return true;
            }
        });
        main.getCommand("acceptrecruitment").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player target = (Player) commandSender;
                    for(Player p : pendingRecruitRequests.keySet()) {
                        if(pendingRecruitRequests.get(p).equals(target)) {
                            boolean already = new ExtraDaoImpl().isAlreadyRecruited(target);
                            boolean own = new ExtraDaoImpl().isOwnRecruiter(target, p);
                            if(!already && !own) {
                                pendingRecruitRequests.remove(p);
                                target.sendMessage(ChatColor.GREEN + "accepted recruitment by " + p.getName());
                                p.sendMessage(ChatColor.GREEN + target.getName() + " accepted your recruitment. Pretty dope! Your perks will be available after you died once or if you click a CLASS sign!");
                                new ExtraDaoImpl().add(new Extra(p, target));
                                ExtraHandler.update(p);
                                return true;
                            } else {
                                pendingRecruitRequests.remove(p);
                                if(already) {
                                    target.sendMessage(ChatColor.GREEN + "What the * are you doing? You've already been recruited");
                                    p.sendMessage(ChatColor.GREEN + target.getName() + " has already been recruited by someone else. Nice try though...");
                                }
                                if(own) {
                                    target.sendMessage(ChatColor.GREEN + "What the * are you doing? You already recruited " + p.getName() + " yourself. This is pretty suspicious guys.");
                                    p.sendMessage(ChatColor.GREEN + target.getName() + " Is your very own recruiter. Nice try though...");
                                }
                                return true;
                            }
                        }
                    }
                    target.sendMessage(ChatColor.GREEN + "There was no pending recruitment request for you...");
                }
                return false;
            }
        });
    }
}
