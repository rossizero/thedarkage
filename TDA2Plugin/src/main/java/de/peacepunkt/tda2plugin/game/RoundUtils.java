package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundUtils {
    public static Map<String, Boolean> getAllArenas() {
        YamlConfiguration config = new YamlConfiguration();
        File f = new File(Main.arenasPath+"/arenas.yml");
        try {
            config.load(f);
            List<String> ret = (List<String>) config.getList("arenas");
            if(ret != null) {
                Map<String, Boolean> rett = new HashMap<>();
                for (String s : ret) {
                    String settingsFile = Main.arenasPath + s + "/settings.yml";
                    YamlConfiguration configuration = new YamlConfiguration();
                    File file = new File(settingsFile);
                    try {
                        configuration.load(file);
                        configuration.addDefault("staged", false);
                        boolean staged = (Boolean) configuration.get("staged");
                        rett.put(s, staged);
                    } catch (IOException | InvalidConfigurationException e1) {
                        e1.printStackTrace();
                    }
                }
                return rett;
            }
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
        return new HashMap<>();
    }

    public static String getDisplayNameOfMap(String mapName) {
        String settingsFile = Main.arenasPath + mapName + "/settings.yml";
        YamlConfiguration configuration = new YamlConfiguration();
        File file = new File(settingsFile);
        try {
            configuration.load(file);
            configuration.addDefault("staged", false);
            boolean staged = (Boolean) configuration.get("staged");
            configuration.addDefault("displayName", mapName);
            String name = (String) configuration.getString("displayName");
            if (staged)
                return name;
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getStagedArenas() {
        YamlConfiguration config = new YamlConfiguration();
        File f = new File(Main.arenasPath+"/arenas.yml");
        try {
            config.load(f);
            List<String> ret = (List<String>) config.getList("arenas");
            if(ret != null) {
                List<String> rett = new ArrayList<>();
                for (String s : ret) {
                    String settingsFile = Main.arenasPath + s + "/settings.yml";
                    YamlConfiguration configuration = new YamlConfiguration();
                    File file = new File(settingsFile);
                    try {
                        configuration.load(file);
                        configuration.addDefault("staged", false);
                        boolean staged = (Boolean) configuration.get("staged");
                        if (staged)
                            rett.add(s);
                    } catch (IOException | InvalidConfigurationException e1) {
                        e1.printStackTrace();
                    }
                }
                return rett;
            }
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public static List<String> getStagedArenaDisplayNames() {
        YamlConfiguration config = new YamlConfiguration();
        File f = new File(Main.arenasPath+"/arenas.yml");
        try {
            config.load(f);
            List<String> ret = (List<String>) config.getList("arenas");
            if(ret != null) {
                List<String> rett = new ArrayList<>();
                for (String s : ret) {
                    String settingsFile = Main.arenasPath + s + "/settings.yml";
                    YamlConfiguration configuration = new YamlConfiguration();
                    File file = new File(settingsFile);
                    try {
                        configuration.load(file);
                        configuration.addDefault("staged", false);
                        boolean staged = (Boolean) configuration.get("staged");
                        configuration.addDefault("displayName", s);
                        String name = (String) configuration.getString("displayName");
                        if (staged)
                            rett.add(name);
                    } catch (IOException | InvalidConfigurationException e1) {
                        e1.printStackTrace();
                    }
                }
                return rett;
            }
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void printMVPofPlayer(Player player, Main main) {
                Map<Teem, PlayerRoundStats> map = new HashMap<>();
                for(Teem t: main.getRoundHandler().getRound().getTeams()) {
                    map.put(t, t.getRoundStats().getCurrentMVP());
                }
                ChatColor lila = ChatColor.LIGHT_PURPLE;
                boolean mvp = false;
                player.sendMessage(lila + "+--------------MVPS---------------------------------+");
                for(Teem t : map.keySet()) {
                    PlayerRoundStats ps = map.get(t);
                    if(ps != null) {
                        if(player.getUniqueId().equals(ps.getPlayer().getUniqueId()))
                            mvp = true;
                        player.sendMessage(lila + "MVP of the " +  t.getChatColor() + t.getName() +  lila + " is " + t.getChatColor() + ps.getPlayer().getName());
                        player.sendMessage(lila + "Score | Kills | Deaths | K/D | Assists | Captures | Repairs | Heals");
                        player.sendMessage(lila + String.format("%-8.1f|", ps.getScore())  + String.format(" %-6d|", ps.getKills())
                                + String.format(" %-8d|", ps.getDeaths()) + String.format(" %-2.2f |",ps.getKD()) +  String.format(" %-10d|",ps.getAssists())
                                + String.format(" %-11d|",ps.getCaptures()) + String.format(" %-10d|",ps.getRepairs()) + String.format(" %-5d",ps.getHeals()));
                        player.sendMessage(lila + "+---------------------------------------------------+");
                    } else {
                        player.sendMessage(lila + "MVP of the " +  t.getChatColor() + t.getName() +  lila + " is currently noone");
                        player.sendMessage(lila + "+---------------------------------------------------+");
                    }
                }

                if(!mvp) {
                    Teem ownTeem = main.getRoundHandler().getRound().getTeam(player);
                    PlayerRoundStats ps = ownTeem.getRoundStats().getPlayerRoundStats(player);
                    player.sendMessage(lila + String.format("%-8.1f|", ps.getScore())  + String.format(" %-6d|", ps.getKills())
                            + String.format(" %-8d|", ps.getDeaths()) + String.format(" %-2.2f |",ps.getKD()) +  String.format(" %-10d|",ps.getAssists())
                            + String.format(" %-11d|",ps.getCaptures()) + String.format(" %-10d|",ps.getRepairs()) + String.format(" %-5d",ps.getHeals()));
                } else {
                    player.sendMessage(lila + "You're the MVP of your current Team!");
                }

    }
}
