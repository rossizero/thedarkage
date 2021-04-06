package de.peacepunkt.tda2plugin.kits;

import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.persistence.novote.ExtraDaoImpl;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExtraHandler {
    static Map<Player, Integer> entries = new HashMap<>();
    public ExtraHandler() {

    }
    private static int getLevel(Player player) {
        if(entries.get(player) == null) {
            int level = new ExtraDaoImpl().getExtraLevelOfPlayer(player);
            entries.put(player, level);
            return level;
        } else {
            return  entries.get(player);
        }
    }
    public static void update(Player player) {
        int level = new ExtraDaoImpl().getExtraLevelOfPlayer(player);
        entries.put(player, level);
    }
    /*
        order of perks (they add up)
        1 = more ladders & more arrows
        2 = feather falling * extra heart
        3 = sharpness 1
        4 = bold displayName or so
     */
    public static boolean getExtraLadders(Player player) {
        return getLevel(player) > 0;
    }
    public static boolean getExtraFeatherFalling(Player player) {
        return getLevel(player) > 1;
    }
    public static boolean getExtraHeart(Player player) {
        return getLevel(player) > 3;
    }
    public static boolean getExtraSharpness(Player player) {
        PlayerStats tmp = new PlayerStatsDaoImpl().getMyStats(player.getUniqueId().toString());
        if(tmp != null) {
            if(tmp.getLastVote() != null) {
                if (tmp.getLastVote().plusHours(8).isAfter(LocalDateTime.now())){
                    return true;
                }
            }
        }
        return getLevel(player) > 2;
    }
    public static boolean getExtraArrows(Player player) {
        return getLevel(player) > 0;
    }
    public static boolean getExtraDisplayName(Player player) {
        return getLevel(player) > 3;
    }
}
