package de.peacepunkt.tda2plugin.pubsub;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LivePublisher implements Listener {
    EasyUnixSocket socket;
    Main main;

    public LivePublisher(Main main) {
        this.main = main;
        socket = new EasyUnixSocket(main, "/out/", "livedata");
    }
    @EventHandler
    public void onMvpChangeEvent(MvpChangeEvent event) throws IOException {
        publish();
    }
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        publish(makePlayersList(event.getPlayer()));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        publish(makePlayersList(null));
    }
    private JsonArray makePlayersList(Player left) {
        JsonArray players = new JsonArray();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(left != null) {
                if (!p.equals(left))
                    players.add(p.getUniqueId().toString());
            } else {
                players.add(p.getName());
            }
        }
        return players;
    }
    private void publish() {
        publish(makePlayersList(null));
    }
    private void publish(JsonArray playerList) {
        JsonArray out = new JsonArray();
        Map<Teem, PlayerRoundStats> map = new HashMap<>();
        for(Teem t: main.getRoundHandler().getRound().getTeams()) {
            PlayerRoundStats prs = t.getRoundStats().getCurrentMVP();
            HashMap<String,Object> map2 = new HashMap();
            map2.put("teamName", t.getName());
            map2.put("teamColor", t.getChatColor().name());
            map2.put("mvpExists", (prs != null));
            if(prs != null) {
                map2.put("name", prs.getPlayer().getName());
                map2.put("uuid", prs.getPlayer().getUniqueId().toString());
                map2.put("kit", PlayerHandler.getInstance().getKit(prs.getPlayer()));
                map2.put("score", prs.getScore());
                map2.put("kills", prs.getKills());
                map2.put("deaths", prs.getDeaths());
                map2.put("kd", prs.getKD());
                map2.put("assists", prs.getAssists());
                map2.put("captures", prs.getCaptures());
                map2.put("repairs", prs.getRepairs());
                map2.put("heals", prs.getHeals());
            }
            out.add(new Gson().toJson(map2));
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("worldname", new Gson().toJsonTree(main.getRoundHandler().getRound().getDisplayName()));
        jsonObject.add("roundDuration", new Gson().toJsonTree(main.getRoundHandler().getRound().getTimeRun()));
        jsonObject.add("roundLength", new Gson().toJsonTree(main.getRoundHandler().getRound().getRoundLength()));
        jsonObject.add("mvps", out);
        jsonObject.add("players", playerList);
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
        byte buffer[] = jsonObject.toString().getBytes();
        try {
            try (PrintWriter file = new PrintWriter(new FileOutputStream(main.getDataFolder().getAbsolutePath()+"/out/copy.txt", false))) {
                file.println(jsonObject.toString());
            }
            socket.send(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
