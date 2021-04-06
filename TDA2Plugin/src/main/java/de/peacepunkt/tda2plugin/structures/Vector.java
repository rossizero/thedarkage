package de.peacepunkt.tda2plugin.structures;

import org.bukkit.Location;
import org.bukkit.World;

public class Vector {
    public int x, y, z;
    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector(){}

    public static Vector vectorFromLocation(Location location) {
        return new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static Location locationFromVector(World world, Vector vector) {
        if(vector != null)
            return new Location(world, vector.x, vector.y, vector.z);
        return null;
    }
    public boolean equals(Location location) {
        if(location.getBlockX() == x && location.getBlockY() == y && location.getBlockZ() == z) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "V(x: " + x + ", y: " + y + ", z: " + z +")";
    }
}
