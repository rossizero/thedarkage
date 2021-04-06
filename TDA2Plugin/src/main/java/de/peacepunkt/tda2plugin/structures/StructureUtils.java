package de.peacepunkt.tda2plugin.structures;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class StructureUtils {
    public static String rotate(String direction, int dir) {
        String ret = direction;
        switch (dir) {
            case 1: //0 degrees == east
                break;
            case 2: //90 degrees == south
                if(direction.equals("north")) {
                    ret = "west";
                } else if(direction.equals("east")) {
                    ret = "south";
                } else if(direction.equals("south")) {
                    ret = "east";
                } else if(direction.equals("west")){
                    ret = "north";
                }
                break;
            case 3: //180 degrees == west
                if(direction.equals("north")) {
                    ret = "south";
                } else if(direction.equals("east")) {
                    ret = "west";
                } else if(direction.equals("south")) {
                    ret = "north";
                } else if(direction.equals("west")){
                    ret = "east";
                }
                break;
            case 0: //270 degrees == north
                if(direction.equals("north")) {
                    ret = "east";
                } else if(direction.equals("east")) {
                    ret = "north";
                } else if(direction.equals("south")) {
                    ret = "west";
                } else if(direction.equals("west")){
                    ret = "south";
                }
                break;
        }
        return ret;
    }

    /**
     * returns the direction the player is looking
     * 0 = north
     * 1 = east
     * 2 = south
     * 3 = west
     *
     * @param player
     * @return
     */
    public static int getDir(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return 2; //s
        } else if (yaw < 135) {
            return 3; //w
        } else if (yaw < 225) {
            return 0; //n
        } else if (yaw < 315) {
            return 1; //o
        }
        return 0;
    }

    public static int getYaw(int dir) {
        switch (dir) {
            default:
                return 180;
            case 1:
                return 270;
            case 2:
                return 0;
            case 3:
                return 90;
        }
    }
    /**
     * rotates loc around center in direction of dir
     * @param loc
     * @param center
     * @param dir
     * @return
     */
    public static Location rotate(Location loc, Location center, int dir) {
        Location seedClone = center.clone();
        Location ret = loc.clone().subtract(seedClone);
        double x = ret.getX();
        double z = ret.getZ();
        switch (dir) {
            case 1: //0 degrees == east
                break;
            case 2: //90 degrees == south
                double tmp = x;
                x = z;
                z = tmp;
                break;
            case 3: //180 degrees == west
                x *= -1;
                z *= -1;
                break;
            case 0: //270 degrees == north
                x *= -1;
                z *= -1;
                double tmp2 = x;
                x = z;
                z = tmp2;
                break;
        }
        ret.setX(x);
        ret.setZ(z);
        return ret.add(seedClone);
    }
}
