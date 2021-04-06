package de.peacepunkt.tda2plugin.vote;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;

public class BasicVoteListener implements Listener {
    Main main;
    public BasicVoteListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onVoteReceived(VotifierEvent event) {
        Vote v = event.getVote();
        System.out.println(v.getAddress());
        OfflinePlayer p = Bukkit.getPlayer(v.getUsername());
        voted(p);
    }
    public void voted(OfflinePlayer p) {
        if(p != null) {
            Xp xp = new XpDaoImpl().get(p.getUniqueId().toString());
            if(xp != null) {
                xp.addXp(50, true);
            } else {
                p.getPlayer().sendMessage(ChatColor.GREEN + "som ting wong...");
            }
            if(p.isOnline()) {
                p.getPlayer().sendMessage(ChatColor.GREEN + "Thanks for voting!");

                PlayerStats tmp = new PlayerStatsDaoImpl().getMyStats(p.getUniqueId().toString());
                tmp.setLastVote(LocalDateTime.now());
                new PlayerStatsDaoImpl().update(tmp);
                KitHandler.getInstance().restockKit(p.getPlayer(), true, false,false);

                new XpDaoImpl().update(xp);
            }

        }
    }
}
