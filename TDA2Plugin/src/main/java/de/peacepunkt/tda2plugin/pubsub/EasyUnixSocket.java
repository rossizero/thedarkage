package de.peacepunkt.tda2plugin.pubsub;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class EasyUnixSocket {
    Socket socket;
    public EasyUnixSocket(Main main, String path, String filename) {
        try {
            new File(main.getDataFolder().getAbsolutePath()+path).mkdirs();
            File socketFile = new File(main.getDataFolder().getAbsolutePath()+path + filename);
            if(!socketFile.exists()) {
                socketFile.createNewFile();
            }
            System.out.println("file created");
            AFUNIXServerSocket server = AFUNIXServerSocket.newInstance();
            System.out.println("server created " + server);
            server.bind(new AFUNIXSocketAddress(socketFile));
            System.out.println("server bind to " + server.getLocalSocketAddress());
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        socket = server.accept();
                        System.out.println("socket created " + socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskAsynchronously(main);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] data) throws IOException {
        if(socket != null) {
            OutputStream stream = socket.getOutputStream();
            stream.write(data);
            stream.flush();
            System.out.println("sent " + data.toString());
        } else {
            System.out.println("still no client there. Didnt send anything");
        }
    }
}
