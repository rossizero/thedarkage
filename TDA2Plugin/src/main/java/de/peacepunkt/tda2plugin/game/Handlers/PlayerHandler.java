package de.peacepunkt.tda2plugin.game.Handlers;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler {
    private static PlayerHandler instance;
    private Map<UUID, AbstractKitSuperclass> currentKits;

    private PlayerHandler() {
        currentKits = new HashMap<>();
    }

    public static PlayerHandler getInstance() {
        if(PlayerHandler.instance == null) {
            instance = new PlayerHandler();
        }
        return instance;
    }

    public AbstractKitSuperclass getKit(Player player) {
        for(UUID offlinePlayer: currentKits.keySet()) {
            if(offlinePlayer.equals(player.getUniqueId())) {
                return currentKits.get(offlinePlayer);
            }
        }
        return null;
    }
    public AbstractKitSuperclass getKit(OfflinePlayer player) {
        for(UUID offlinePlayer: currentKits.keySet()) {
            if(offlinePlayer.equals(player.getUniqueId())) {
                return currentKits.get(offlinePlayer);
            }
        }
        return null;
    }
    public Teem getTeam(Player player) {
        return MainHolder.main.getRoundHandler().getRound().getTeam(player);
    }
    public Teem getTeam(OfflinePlayer player) {
        return null; //TODO remember team of relogging player
    }

    /**
     *
     * @param player
     * @return if player is at her spawn, false if she has no team
     */
    public boolean isAtSpawn(Player player) {
        if(getTeam(player) == null)
            return false;
        return player.getWorld().equals(getTeam(player).getSpawnWorld());
    }

    public void updateKit(Player player, AbstractKitSuperclass kit) {
        if(currentKits.containsKey(player.getUniqueId()))
            currentKits.replace(player.getUniqueId(), kit);
        else
            currentKits.put(player.getUniqueId(), kit);
    }
}
