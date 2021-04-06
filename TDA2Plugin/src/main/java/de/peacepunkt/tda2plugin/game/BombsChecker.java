package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class BombsChecker {
    LocalDateTime lastcheck;
    int delay;
    Main main;

    public BombsChecker() {
        init();
        this.main = MainHolder.main;
    }
    private void init() {
        lastcheck = LocalDateTime.now();
        int min = 5;
        int max = 25;
        delay = (int) (min + new Random().nextFloat() * (max - min));
    }
    public void check(World world) {
        if (LocalDateTime.now().minusSeconds(delay).isAfter(lastcheck)) {
            init();
            attack(world);
        }
    }

    private void attack(World world) {
        List<Player> players = world.getPlayers();
        if(players.size() != 0) {
            Vector middle = new Vector(0, 0, 0);
            Vector maxOffset = new Vector(0, 0, 0);
            Vector sum = new Vector(0, 0, 0);
            int count = 0;
            for (Player player : players) {
                count++;
                sum.add(player.getLocation().toVector());
            }
            middle = sum.divide(new Vector(count, count, count));

            for (Player player : players) {
                if (player.getLocation().toVector().distance(middle) > maxOffset.length()) {
                    maxOffset = player.getLocation().toVector().subtract(middle.clone().setY(player.getLocation().getY()));
                }
            }

            //System.out.println("middle " + middle);
            //System.out.println("offset " + maxOffset.length() * 2);

            Location dot1 = dot(middle, maxOffset.length() * 2).toLocation(world);
            Location dot2 = dot(middle, maxOffset.length() * 4).toLocation(world);
            double min = 60;
            double max = 120;
            //if only one player was there (offset = 0)
            if (dot1.equals(dot2)) {
                dot1.add((int) (-min + new Random().nextFloat() * (max + min)), 0, (int) (-min + new Random().nextFloat() * (max + min)));
            }

            //System.out.println("dot1 " + dot1);
            //System.out.println("dot 2" + dot2);

            Vector dir = dot1.toVector().subtract(dot2.toVector());
            //sth in this function causes a small lag spike
            new BombTrace(main, dot1, dir, (int) (dot1.distance(dot2) / 8), world, dot1.distance(dot2));
        }
    }

    private Vector dot(Vector center, double radius) {
        double alpha = 2 * Math.PI * new Random().nextFloat();
        double r = radius * Math.sqrt(new Random().nextFloat());

        double x = r * Math.cos(alpha) + center.getX();
        double z = r * Math.sin(alpha) + center.getZ();
        Vector ret = new Vector(x, 230, z);
        return ret;
    }
}
