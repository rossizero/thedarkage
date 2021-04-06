package de.peacepunkt.tda2plugin.pubsub;


import com.google.gson.JsonArray;
import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.net.*;

public class PlayerPublisher implements Listener {
    DatagramSocket datagramSocket;
    EasyUnixSocket socket;
    public PlayerPublisher(Main main) {
        try {
            socket = new EasyUnixSocket(main, "/out/", "players");
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        try {
            publish(event.getPlayer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        try {
            publish(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publish(Player not) throws IOException {
        JsonArray languages = new JsonArray();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(not != null) {
                if (!p.equals(not))
                    languages.add(p.getName());
            } else {
                languages.add(p.getName());
            }
        }
        byte buffer[] = languages.toString().getBytes();
        //byte buffer[] = jsonObject.toString().getBytes();
        try {
            socket.send(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*DatagramPacket DpSend = new DatagramPacket(buffer, buffer.length, InetAddress.getLocalHost(), 1234);
        datagramSocket.send(DpSend);*/
    }
}
