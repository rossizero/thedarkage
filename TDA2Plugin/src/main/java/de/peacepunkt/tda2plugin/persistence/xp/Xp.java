package de.peacepunkt.tda2plugin.persistence.xp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="xp")
public class Xp {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    String uuid;
    int xp;

    public Xp() {

    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void addXp(int i, boolean tell) {
        OfflinePlayer player = Bukkit.getPlayer(UUID.fromString(uuid));
        if(player != null) {
            if(player.isOnline() && tell) {
                player.getPlayer().sendMessage(ChatColor.DARK_RED + "" + i + ChatColor.GREEN + " xp have been added to your account! (/xp)");
            }
        }
        this.xp += i;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
