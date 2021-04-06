package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.OfflinePlayer;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="mvp")
public class MVP {
    int id;
    String uuid;
    LocalDateTime timeStamp;
    long score;
    float kd;
    long kills;
    long deaths;
    long captures;
    long heals;
    long repairs;
    long assists;

    public MVP(OfflinePlayer player, long kills, long deaths, long captures, long heals, long repairs, long assists) {
        this(player.getUniqueId().toString(), kills, deaths, captures, heals, repairs, assists);
    }
    public MVP(String uuid, long kills, long deaths, long captures, long heals, long repairs, long assists) {
        timeStamp = LocalDateTime.now();
        this.uuid = uuid;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.kd = deaths > 0 ? (float)kills / deaths : (float)kills;
        this.captures = captures;
        this.heals = heals;
        this.repairs = repairs;
        this.score = (int) (kills*PlayerStats.killPoints - deaths*PlayerStats.deathPoints + captures*PlayerStats.captuePoints + heals*PlayerStats.healPoints + repairs*PlayerStats.repairPoints + assists*PlayerStats.assistPoints);

    }
    public MVP() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public long getScore() {
        return score;
    }

    public float getKd() {
        return kd;
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

    public long getHeals() {
        return heals;
    }

    public long getRepairs() {
        return repairs;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setKd(float kd) {
        this.kd = kd;
    }

    public void setKills(long kills) {
        this.kills = kills;
    }

    public void setDeaths(long deaths) {
        this.deaths = deaths;
    }

    public void setCaptures(long captures) {
        this.captures = captures;
    }

    public void setHeals(long heals) {
        this.heals = heals;
    }

    public void setRepairs(long repairs) {
        this.repairs = repairs;
    }

    public long getAssists() {
        return assists;
    }

    public void setAssists(long assists) {
        this.assists = assists;
    }
}
