package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssistHandler {
    //a little abuse of Map...
    List<AssistEntry> list;
    public AssistHandler() {
        list = new ArrayList<AssistEntry>();
    }
    private AssistEntry getOrAdd(Player player) {
        for(AssistEntry a : list) {
            if (a.getTarget().getUniqueId().equals(player.getUniqueId())) {
                return a;
            }
        }
        AssistEntry a = new AssistEntry(player);
        list.add(a);
        return a;
    }

    public void add(Player target, Player damager) {
        AssistEntry a = getOrAdd(target);
        a.add(damager);
    }

    public List<Player> get(Player killed) {
        for(AssistEntry a : list) {
            if (a.getTarget().getUniqueId().equals(killed.getUniqueId())) {
                return a.get();
            }
        }
        return new ArrayList<>();
    }

    public LocalDateTime getTimeStampOf(Player player) {
        for(AssistEntry a : list) {
            if (a.getTarget().getUniqueId().equals(player.getUniqueId())) {
               return a.getLastHitTimestamp();
            }
        }
        return null;
    }
    public void remove(Player player) {
        for(AssistEntry a : list) {
            if(a.getTarget().equals(player) || a.getTarget().getName().equals(player.getName())) {
                list.remove(a);
                return;
            }
        }
    }

    private class AssistEntry {
        Map<Player, LocalDateTime> map;
        Player target;
        public AssistEntry(Player target) {
            this.target = target;
            map = new HashMap<>();
        }
        public void add(Player player) {
            if(map.containsKey(player)) { //update if exists
                map.remove(player);
                map.put(player, LocalDateTime.now());
            } else {
                map.put(player, LocalDateTime.now());
            }
            if(map.size() > Main.numOfAssists) {
                System.out.println(map.size() + " " + Main.numOfAssists);
                Player max = null;
                LocalDateTime maxT = null;
                for(Player p : map.keySet()) {
                    LocalDateTime time = map.get(p);
                    if(maxT == null) {
                        maxT = time;
                        max = p;
                    }
                    if(time.isBefore(maxT)) {
                        maxT = time;
                        max = p;
                    }
                }
                map.remove(max);
                //System.out.println("removed " + max.getName());
            }
            /*System.out.println(target.getName() + " has assisters:");
            for(Player p : map.keySet()) {
                System.out.println("\t" + p.getName());
            }
            System.out.println();*/
        }
        public List<Player> get() {
            List<Player> ret = new ArrayList<>();
            for (Player player : map.keySet()) {
                LocalDateTime oldTime = map.get(player);
                long seconds = oldTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
                //System.out.println(player.getName() + " assisted killing " + target.getName()+" " + seconds+ " ago");
                if (seconds <= Main.maxAssistTime) { //only add to assist list if assist is still valid
                    ret.add(player);
                    //System.out.println("thats why we added him to the return list");
                }
            }
            //System.out.println("There are " + ret.size() + " assisters for player " + target.getName());
            return ret;
        }
        public Player getTarget() {
            return target;
        }

        public LocalDateTime getLastHitTimestamp() {
            LocalDateTime ret = null;
            for (Player player : map.keySet()) {
                LocalDateTime oldTime = map.get(player);
                if(ret == null || oldTime.isAfter(ret)) {
                    ret = oldTime;
                }
            }
            return ret;
        }
    }
}
