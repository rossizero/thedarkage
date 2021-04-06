package de.peacepunkt.tda2plugin.stats;

import de.peacepunkt.tda2plugin.game.MiniarenaCounter;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclassDaoImpl;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.persistence.DailyStats;
import de.peacepunkt.tda2plugin.persistence.DailyStatsDaoImpl;
import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.persistence.NoResultException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class RoundStats { //Stats of Players of one team in current round
    private Map<Player, PlayerRoundStats> currentStats;
    private Teem forTeem;

    public RoundStats(Teem team) {
        currentStats = new HashMap<>();
        this.forTeem = team;
    }
    public void addPlayer (Player player) {
        boolean done = false;
        for(Player p : currentStats.keySet()) {
            if(p.getUniqueId().equals(player.getUniqueId())) {
                PlayerRoundStats t = currentStats.get(p);
                t.setPlayer(player);
                currentStats.remove(p);
                currentStats.put(player, t);
                done = true;
                break;
            }
        }
        if(!done) {
            currentStats.put(player, new PlayerRoundStats(player));
        }
    }
    private void ping() {
        //simple non expensive request to reset jdbc idle timout..
        System.out.println(ChatColor.BOLD + "Pinged mysql!");
        new PlayerStatsDaoImpl().getTopList(0);
    }
    public void save() {
        ping();
        DailyStats daily = new DailyStatsDaoImpl().get(LocalDate.now());
        if(daily == null) {
            daily = new DailyStats();
        }
        MiniarenaCounter mini = MiniarenaCounter.getInstance();
        if(!mini.isHasTobeRefreshed()) {
            daily.addKilledPigsMiniarena(mini.getPigs());
            daily.addKillsMiniarena(mini.getKills());
            mini.setHasTobeRefreshed();
        }
        for(OfflinePlayer p: currentStats.keySet()) {
            PlayerRoundStats ps = currentStats.get(p);
            if(ps != null) { //can happen after reload
                ps.setKillStreak();
                daily.add(ps);
                PlayerStats real = new PlayerStatsDaoImpl().getMyStats(p.getUniqueId().toString());
                if(real != null) { //connection to db lost. Shouldnt happen again because of the ping
                    real.addHeals(ps.getHeals());
                    real.addCaptures(ps.getCaptures());
                    real.addDeaths(ps.getDeaths());
                    real.addKills(ps.getKills());
                    real.addRepairs(ps.getRepairs());
                    real.addRounds(1);
                    real.addAssists(ps.getAssists());
                    real.setKillStreak(Math.max(real.getKillStreak(), ps.getKillStreak()));
                    real.addUselessKills(ps.getUselessKills());
                    new PlayerStatsDaoImpl().update(real);
                    OfflinePlayer player = ps.getPlayer();
                    if(ps.getXp() > 0) { //TODO to be checked cancel -xp at end of round
                        Xp xp = new XpDaoImpl().get(player.getUniqueId().toString());
                        if (ps.getPlayer().getPlayer() != null)
                            ps.getPlayer().getPlayer().sendMessage(ChatColor.DARK_RED + "" + ps.getXp() + ChatColor.GREEN + " xp has been added to your account!");
                        xp.addXp(ps.getXp(), false);
                        new XpDaoImpl().update(xp);
                    }
                }
                for(KitStats k : ps.getKitStats()) {
                    System.out.println("now checking " + p.getUniqueId().toString() + " and class " + k.getName() + " and java class " + k.getClass());
                    //TODO generics
                    AbstractKitSuperclassDaoImpl dao = new AbstractKitSuperclassDaoImpl(KitHandler.getInstance().getKitByName(k.getName()).getClass());//Kits.getTypedDaoForKit(k.getName());
                    try {
                        AbstractKitSuperclass entity = dao.get(p.getUniqueId().toString());
                        System.out.println("before: " + entity.getKills() + " " + entity.getDeaths());
                        k.setKillStreak();
                        entity.addNewStats(k);
                        dao.update(entity);
                        System.out.println("after: " + entity.getKills() + " " + entity.getDeaths());
                        System.out.println("done " + entity.getClass());
                    } catch (NoResultException exception) {
                        System.out.println("###################################################");
                        System.out.println("unable to find AbstractKitSuperclass for player " + p.getName() + "("+p.getUniqueId().toString()+") and class " + k.getName());
                        System.out.println("###################################################");
                    }
                }
            }
        }
        System.out.println("Saved data for Team " + forTeem.getName());
        new DailyStatsDaoImpl().update(daily);
        System.out.println("Daily " + daily.getKills());
    }
    public PlayerRoundStats getCurrentMVP() {
        if(currentStats.keySet().size() > 0) {
            //populate unsorted Map
            Map<PlayerRoundStats, Double> unSortedMap = new HashMap<>();
            for (OfflinePlayer p : currentStats.keySet()) {
                unSortedMap.put(currentStats.get(p), currentStats.get(p).getScore());
            }
            //sort this Map
            LinkedHashMap<PlayerRoundStats, Double> sortedMap = new LinkedHashMap<>(); //LinkedHashMap keeps insertion order
            unSortedMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
            Map.Entry<PlayerRoundStats, Double> entry = sortedMap.entrySet().iterator().next();
            return entry.getKey();
        }
        return null;
    }
    //TODO NPE if p left -> Offlineplayer

    /**
     * adds a kill to current player
     *
     * @param p
     * @return current kills since last death
     */
    public int addKill(Player p, String kit) {
        currentStats.get(p).addKills(1, kit);
        return  currentStats.get(p).getCurrentKillStreak();
    }
    public void addCapture(Player p) {
        currentStats.get(p).addCaptures(1);
    }
    public void addDeath(Player p, String kit) {
        currentStats.get(p).addDeaths(1, kit);
    }
    public void addHeal(Player p) {
        currentStats.get(p).addHeals(1);
    }
    public void addRepair(Player p) {
        currentStats.get(p).addRepairs(1);
    }
    public void addAssist(Player p, String kit) {
        currentStats.get(p).addAssists(1, kit);
    }
    public PlayerRoundStats getPlayerRoundStats(Player player) {
        return currentStats.get(player);
    }

    public void addUselessKill(Player p) {
        currentStats.get(p).addUselessKills(1);
    }

}
