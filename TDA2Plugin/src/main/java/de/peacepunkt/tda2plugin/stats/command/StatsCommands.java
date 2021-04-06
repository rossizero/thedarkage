package de.peacepunkt.tda2plugin.stats.command;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.RoundUtils;
import de.peacepunkt.tda2plugin.persistence.MiniArenaBlocksDaoImpl;
import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsCommands {
    private Main main;

    public StatsCommands(Main main) {
            this.main = main;

            main.getCommand("mystats").setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        printMyStats(player);
                    }
                    return true;
                }
            });

            main.getCommand("toplist").setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        if(strings.length == 0) {
                            printToplistPage(player, 0);
                            return true;
                        } else {
                            String arg = strings[0];
                            try { //to cast to an integer
                                int a = Integer.valueOf(arg);
                                if(a > 0) {
                                    a--;
                                    System.out.println(a);
                                    printToplistPage(player, a);
                                }
                                return true;
                            } catch (Exception e) { //else cast to a person
                                printToplistPlayer(player, arg);
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });


            main.getCommand("mvp").setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                    if(commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        printMVP(player);
                    }
                    return true;
                }
            });
        }

    private void printMyStats(Player player) {
        BukkitScheduler scheduler = main.getServer().getScheduler();
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                //TODO add values from other team playerstats (after player did sw there could be some)
                Teem t = main.getRoundHandler().getRound().getTeam(player);
                //get current round stats
                PlayerRoundStats prs = t.getRoundStats().getPlayerRoundStats(player);
                //get persisted round stats
                PlayerStats st = new PlayerStatsDaoImpl().getMyStats(player.getUniqueId().toString());
                //new PlayerStats to add them up and display
                PlayerStats stats = new PlayerStats();
                stats.setMvps(st.getMvps());
                stats.addKills(st.getKills() + prs.getKills());
                stats.addDeaths(st.getDeaths() + prs.getDeaths());
                stats.setKillStreak(st.getKillStreak());
                stats.addAssists(st.getAssists() + prs.getAssists());
                stats.addCaptures(st.getCaptures() + prs.getCaptures());
                stats.addRepairs(st.getRepairs() + prs.getRepairs());
                stats.addHeals(st.getHeals() + prs.getHeals());
                stats.setRounds(st.getRounds());
                stats.setScore((long) (stats.getKills()* PlayerStats.killPoints + stats.getDeaths()*PlayerStats.deathPoints +
                                        stats.getCaptures()*PlayerStats.captuePoints + stats.getHeals()*PlayerStats.healPoints + stats.getRepairs()*PlayerStats.repairPoints));
                stats.setKd((double) (stats.getKills() / (stats.getDeaths() > 0 ? stats.getDeaths() : 1)));

                if(stats != null) {
                    ChatColor lila = ChatColor.LIGHT_PURPLE;
                    player.sendMessage(lila + "Stats of " + ChatColor.GREEN + player.getName());
                    player.sendMessage(lila + String.format("+%-8s+%-8s+", "", "").replace(' ', '-'));
                    player.sendMessage(lila + "| Score:     |" + String.format(" %-9.1f|", stats.getScore()));
                    player.sendMessage(lila + "| MVP's:      |" + String.format(" %-10d|", stats.getMvps()));
                    player.sendMessage(lila + "| Kills:       |" + String.format(" %-10d|", stats.getKills()));
                    player.sendMessage(lila + "| Deaths:    |" + String.format(" %-10d|",stats.getDeaths()));
                    player.sendMessage(lila + "| Killstreak:|" + String.format(" %-10d|",stats.getKillStreak()));
                    player.sendMessage(lila + "| K/D:        |" + String.format(" %-9.2f|",(double)stats.getKills() / (double)stats.getDeaths()));
                    player.sendMessage(lila + "| Assists:   |" + String.format(" %-10d|",stats.getAssists()));
                    player.sendMessage(lila + "| Captures: |" + String.format(" %-10d|",stats.getCaptures()));
                    player.sendMessage(lila + "| Repairs:   |" + String.format(" %-10d|",stats.getRepairs()));
                    player.sendMessage(lila + "| Heals:      |" + String.format(" %-10d|",stats.getHeals()));
                    player.sendMessage(lila + "| Rounds:    |" + String.format(" %-10d|",stats.getRounds()));
                    player.sendMessage(lila + "| " +  new MiniArenaBlocksDaoImpl().getNumberOfBlocksPlacedBy(player)+" blocks placed! |");
                    player.sendMessage(lila + String.format("+%-8s+%-8s+", "", "").replace(' ', '-'));
                } else {
                    player.sendMessage(ChatColor.RED + "This is an error that you should never read. If you still can see this please contact an admin. Lol!");
                }
            }
        });
    }

    private void printMVP(Player player) {
        RoundUtils.printMVPofPlayer(player, main);
    }

    private void printToplistPlayer(Player player, String target) {
        BukkitScheduler scheduler = main.getServer().getScheduler();
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                List<PlayerStats> stats = new PlayerStatsDaoImpl().getToplistAroundUsername(target);
                if (stats != null) {
                    if(stats.size() > 0) {
                        ChatColor lila = ChatColor.DARK_PURPLE;
                        long rankOfTarget = new PlayerStatsDaoImpl().getRankOfPlayerUsername(target);
                        long rank = rankOfTarget - PlayerStatsDaoImpl.numberOfEntriesAboveRequestedPlayer;
                        if (rank < 0) {
                            rank = 0;
                        }
                        player.sendMessage(lila + "+-----------Page: " + ((int) (rank / 10) + 1) + "--------------------------------+");
                        for (PlayerStats ps : stats) {
                            if (rank != rankOfTarget)
                                player.sendMessage(lila + "" + (rank + 1) + ": " + ps.getUsername() + " | " + ps.getScore());
                            else
                                player.sendMessage(ChatColor.GREEN + "" + (rank + 1) + ": " + ps.getUsername() + " | " + ps.getScore());
                            rank++;
                        }
                        player.sendMessage(lila + "+---------------------------------------------+");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "This player has not been here yet. Just tell him to join and try again!");
                    }
                } else {
                    player.sendMessage(ChatColor.GREEN + "This player has not been here yet or long enough. Just tell him to join and try again!");
                }
            }
        });
    }
    private void printToplistPage(Player player, int page) {
        BukkitScheduler scheduler = main.getServer().getScheduler();
        scheduler.runTaskAsynchronously(main, new Runnable() {
            @Override
            public void run() {
                long rankOfPlayer = new PlayerStatsDaoImpl().getRankOfPlayer(player);
                List<PlayerStats> stats = new PlayerStatsDaoImpl().getTopList(page);
                if(stats != null) {
                    if(stats.size() != 0) {
                        ChatColor lila = ChatColor.DARK_PURPLE;
                        int count = 1;
                        player.sendMessage(lila + "+-----------Page: " + (page + 1) + "--------------------------------+");
                        for (PlayerStats ps : stats) {
                            int rank = count + page * PlayerStatsDaoImpl.entriesPerToplistPage;
                            System.out.println("rank " + rank + " player " + rankOfPlayer);
                            if(rank-1 != rankOfPlayer)
                                player.sendMessage(lila + "" + rank + ": " + ps.getUsername() + " | " + ps.getScore());
                            else {
                                player.sendMessage(ChatColor.GREEN + "" + rank + ": " + ps.getUsername() + " | " + ps.getScore());
                            }
                            count++;
                        }
                        player.sendMessage(lila + "+---------------------------------------------+");
                    } else {
                        player.sendMessage("This page does not yet exist. Go tell some friends about this server and try again! :P");
                    }
                } else {
                    player.sendMessage("This page does not yet exist. Go tell some friends about this server and try again! :P");
                }
            }
        });
    }
}
