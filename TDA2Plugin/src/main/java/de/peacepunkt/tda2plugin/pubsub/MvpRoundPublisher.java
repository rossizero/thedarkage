package de.peacepunkt.tda2plugin.pubsub;

import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MvpRoundPublisher implements Listener {
    //DatagramSocket datagramSocket;
    Main main;
    EasyUnixSocket socket;
    public MvpRoundPublisher(Main main) {
        //datagramSocket = new DatagramSocket();
        //new File(main.getDataFolder().getAbsolutePath()+"/out/round").mkdirs();
        socket = new EasyUnixSocket(main, "/out/", "round");
        this.main = main;
    }
    @EventHandler
    public void onMvpChangeEvent(MvpChangeEvent event) throws IOException {
        //publish();
    }

    private void publish() throws IOException {
        JsonArray languages = new JsonArray();
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
            languages.add(new Gson().toJson(map2));
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("worldname", new Gson().toJsonTree(main.getRoundHandler().getRound().getDisplayName()));
        jsonObject.add("roundDuration", new Gson().toJsonTree(main.getRoundHandler().getRound().getTimeRun()));
        jsonObject.add("roundLength", new Gson().toJsonTree(main.getRoundHandler().getRound().getRoundLength()));
        jsonObject.add("mvps", languages);
        //System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
        byte buffer[] = jsonObject.toString().getBytes();
        try {
            socket.send(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //DatagramPacket DpSend = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1234);
        //datagramSocket.send(DpSend);

    }
}
