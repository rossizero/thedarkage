package de.peacepunkt.tda2plugin.stats;

import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerRoundStats {
    OfflinePlayer player;
    private long kills;
    private long deaths;
    private long captures;
    private long repairs;
    private long heals;
    private int rounds;
    private long assists;
    private int killStreak;
    private int currentKillStreak;
    private int uselessKills;

    List<KitStats> kitStatsList;

    public PlayerRoundStats(OfflinePlayer player) {
        this.player = player;
        this.killStreak = 0;
        this.currentKillStreak = 0;
        this.kitStatsList = new ArrayList<KitStats>();
    }

    public void addKills(long newKills, String kit) {
        this.kills += newKills;
        this.currentKillStreak += newKills;

        boolean addedToKit = false;
        for(KitStats k: kitStatsList) {
            if(k.getName().equals(kit)) {
                k.addKill();
                addedToKit = true;
                break;
            }
        }
        if(!addedToKit) {
            KitStats stats = new KitStats(kit);
            stats.addKill();
            kitStatsList.add(stats);
            System.out.println("Added new class stats for player " + player.getName() + " " + kit);
        }
    }

    public void addDeaths(long newDeaths, String kit) {
        this.deaths += newDeaths;
        setKillStreak(this.currentKillStreak);
        this.currentKillStreak = 0;

        boolean addedToKit = false;
        for(KitStats k: kitStatsList) {
            if(k.getName().equals(kit)) {
                k.addDeath();
                addedToKit = true;
                break;
            }
        }
        if(!addedToKit) {
            KitStats stats = new KitStats(kit);
            stats.addDeath();
            kitStatsList.add(stats);
            System.out.println("Added new class stats for player " + player.getName() + " " + kit);
        }
    }
    public void addCaptures(long newCaptures) {
        this.captures += newCaptures;
    }
    public void addRepairs(long newRepairs) {
        this.repairs += newRepairs;
    }
    public void addHeals(long newHeals) {
        this.heals += newHeals;
    }
    public void addRound(int newRounds) {
        this.rounds += newRounds;
    }
    public void addAssists(long newAssits, String kit) {
        this.assists += newAssits;
        boolean addedToKit = false;
        for(KitStats k: kitStatsList) {
            if(k.getName().equals(kit)) {
                k.addAssist();
                addedToKit = true;
                break;
            }
        }
        if(!addedToKit) {
            KitStats stats = new KitStats(kit);
            stats.addAssist();
            kitStatsList.add(stats);
            System.out.println("Added new class stats for player " + player.getName() + " " + kit);
        }
    }
    private void setKillStreak(int newKillStreak) {
        this.killStreak = Math.max(newKillStreak, this.killStreak);
    }
    public void setKillStreak() {
        this.killStreak = Math.max(currentKillStreak, this.killStreak);
        currentKillStreak = 0;
    }
    public OfflinePlayer getPlayer() {
        return player;
    }

    public long getKills() {
        return kills;
    }

    public long getDeaths() {
        return deaths;
    }

    public long getCaptures() {
        return captures;
    }

    public long getRepairs() {
        return repairs;
    }

    public long getHeals() {
        return heals;
    }

    public int getRounds() {
        return rounds;
    }

    public double getScore() {
        return (kills* PlayerStats.killPoints + deaths*PlayerStats.deathPoints + captures*PlayerStats.captuePoints + heals*PlayerStats.healPoints + repairs*PlayerStats.repairPoints);
    }
    public double getKD() {
        return deaths > 0 ?  (double)kills/deaths : (double)kills;
    }

    public long getAssists() {
        return assists;
    }

    public int getKillStreak() {return killStreak;}
    public int getCurrentKillStreak() {return currentKillStreak;}

    public void addUselessKills(int i) {
        this.uselessKills += i;
    }

    public int getUselessKills() {
        return this.uselessKills;
    }

    public void setPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public int getXp() {
        return (int)getScore();
    }

    public List<KitStats> getKitStats() {
        return this.kitStatsList;
    }
}
