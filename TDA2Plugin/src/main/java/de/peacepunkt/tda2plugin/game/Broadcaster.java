package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Broadcaster extends BukkitRunnable {
    String message;
    public Broadcaster(Main main, String message, int ticksBetween, int delay) {
        this.message = message;
        runTaskTimer(main, delay,  ticksBetween);
    }

    @Override
    public void run() {
        Bukkit.broadcastMessage(message);
    }
}
