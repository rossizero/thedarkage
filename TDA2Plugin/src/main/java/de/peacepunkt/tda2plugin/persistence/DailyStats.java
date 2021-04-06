package de.peacepunkt.tda2plugin.persistence;

import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="daily")
public class DailyStats {
    @Id
    private LocalDate timestamp;
    private long kills;
    private long deaths;
    private long captures;
    private long repairs;
    private long heals;
    private int rounds;
    private long assists;
    private int uselessKills;
    private int miniarenaKills;
    private int miniarenaPigsKilled;

    public DailyStats() {
        timestamp = LocalDate.now();
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public long getKills() {
        return kills;
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public long getDeaths() {
        return deaths;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public long getCaptures() {
        return captures;
    }

    public void setCaptures(long captures) {
        this.captures = captures;
    }

    public long getRepairs() {
        return repairs;
    }

    public void setRepairs(long repairs) {
        this.repairs = repairs;
    }

    public long getHeals() {
        return heals;
    }

    public void setHeals(long heals) {
        this.heals = heals;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public long getAssists() {
        return assists;
    }

    public void setAssists(long assists) {
        this.assists = assists;
    }

    public int getUselessKills() {
        return uselessKills;
    }

    public void setUselessKills(int uselessKills) {
        this.uselessKills = uselessKills;
    }

    public int getMiniarenaKills() {
        return miniarenaKills;
    }

    public void setMiniarenaKills(int miniarenaKills) {
        this.miniarenaKills = miniarenaKills;
    }

    public int getMiniarenaPigsKilled() {
        return miniarenaPigsKilled;
    }
    public void setMiniarenaPigsKilled(int miniarenaPigsKilled) {
        this.miniarenaPigsKilled = miniarenaPigsKilled;
    }
    public void addKilledPigsMiniarena(int count) {
        this.miniarenaPigsKilled += count;
    }
    public void addKillsMiniarena(int count) {
        this.miniarenaKills += count;
    }
    public void add(PlayerRoundStats p) {
        kills += p.getKills();
        deaths += p.getDeaths();
        captures += p.getCaptures();
        repairs += p.getRepairs();
        heals += p.getHeals();
        rounds += p.getRounds();
        assists += p.getAssists();
        uselessKills += p.getUselessKills();
    }
}
