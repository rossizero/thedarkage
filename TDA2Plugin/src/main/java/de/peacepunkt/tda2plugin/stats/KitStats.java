package de.peacepunkt.tda2plugin.stats;

public class KitStats {
    String name;
    int kills;
    int deaths;
    int killStreak;
    int assists;
    int currentKillStreak;

    public KitStats(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public int getAssists() {
        return assists;
    }

    public void addKill() {
        kills++;
        currentKillStreak++;
    }
    public void addDeath() {
        deaths++;
        killStreak = Math.max(currentKillStreak, killStreak);
        currentKillStreak = 0;
    }

    /**
     * Sets the killstreak to currentKillStreak if its greater
     * Used at the end of a game
     */
    public void setKillStreak() {
        killStreak = Math.max(currentKillStreak, killStreak);
        currentKillStreak = 0;
    }

    public void addAssist() {
        assists++;
    }
}
