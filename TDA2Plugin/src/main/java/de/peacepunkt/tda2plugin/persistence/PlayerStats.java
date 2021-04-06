package de.peacepunkt.tda2plugin.persistence;

import org.bukkit.entity.Player;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name ="playerstats")
public class PlayerStats {
    public final static int killPoints = 2;
    public final static int deathPoints = -1;
    public final static int captuePoints = 1;
    public final static double repairPoints = 0.2;
    public final static double healPoints = 0.1;
    public final static int assistPoints = 1;
    //can't be calculated
    private String uuid;
    private String username;
    private long kills;
    private long deaths;
    private long captures;
    private long repairs;
    private long heals;
    private int rounds;
    private long assists;
    private int killStreak;
    private int uselessKills;

    //can be calculated
    private long playtime;
    private double score;
    private double kd;
    private int mvps; //number of MVPs

    public PlayerStats() {

    }
    public PlayerStats(String uuid, String username) {
        setUuid(uuid);
        setUsername(username);
    }
    public PlayerStats(Player player) {
        setUuid(player);
        setUsername(player.getName());
    }

    private LocalDateTime lastVote;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createDate;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifyDate;

    @PrePersist
    protected void onCreate() {
        modifyDate = createDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        modifyDate = LocalDateTime.now();
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public LocalDateTime getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(LocalDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    @Id
    @Column(length = 36) //32 hex digits + 4 dashes
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void setUuid(Player uuid) {
        this.uuid = uuid.getUniqueId().toString();
    }
    @Formula("kills *" +killPoints+" + deaths*"+deathPoints+"+ captures*"+captuePoints +"+heals*"+healPoints+"+repairs*"+repairPoints +"+assists*"+assistPoints)
    //@Transient
    public double getScore() {
        return score;//(long) (kills*killPoints - deaths*deathPoints + captures*captuePoints + heals*healPoints + repairs*repairPoints);
    }
    @Formula("(kills / deaths)")
    //@Transient
    public double getKd() {
        return kd;//(float)kills / deaths;
    }
    public long getKills() {
        return kills;
    }

    public void addKills(long newKills) {
        this.kills += newKills;
    }

    public long getDeaths() {
        return deaths;
    }

    public void addDeaths(long newDeaths) {
        this.deaths += newDeaths;
    }

    public long getCaptures() {
        return captures;
    }

    public void addCaptures(long newCaptures) {
        this.captures += newCaptures;
    }

    public long getRepairs() {
        return repairs;
    }

    public void addRepairs(long newRepairs) {
        this.repairs += newRepairs;
    }
    public void addUselessKills(int newKills) {
        this.uselessKills += newKills;
    }
    public long getHeals() {
        return heals;
    }

    public void addHeals(long newHeals) {
        this.heals += newHeals;
    }
    //@Transient
    @Formula("(select count(*) from mvp MVP where uuid = MVP.uuid)")
    public int getMvps() {
        return mvps;
    }

    public long getPlaytime() {
        return playtime;
    }

    public void addPlaytime(long newPlaytime) {
        this.playtime += newPlaytime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public void setRepairs(long repairs) {
        this.repairs = repairs;
    }

    public void setHeals(long heals) {
        this.heals = heals;
    }

    public void setPlaytime(long playtime) {
        this.playtime = playtime;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void setKd(Double kd) {
        if(kd == null)
            kd = 0.0;
        this.kd = kd;
    }

    public void setMvps(Integer mvps) {
        if(mvps == null)
            mvps = 0;
        this.mvps = mvps;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }
    public void addRounds(int rounds) {
        this.rounds += rounds;
    }

    public long getAssists() {
        return assists;
    }

    public void setAssists(long assists) {
        this.assists = assists;
    }
    public void addAssists(long assists) {
        this.assists += assists;
    }

    public int getKillStreak() {
        return killStreak;
    }

    public void setKillStreak(int killStreak) {
        this.killStreak = killStreak;
    }

    public int getUselessKills() {
        return uselessKills;
    }

    public void setUselessKills(int uselessKills) {
        this.uselessKills = uselessKills;
    }

    public LocalDateTime getLastVote() {
        return lastVote;
    }

    public void setLastVote(LocalDateTime lastVote) {
        this.lastVote = lastVote;
    }
}
