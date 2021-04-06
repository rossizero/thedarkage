package de.peacepunkt.tda2plugin.def;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Structure {
	List<Block> list;
	Location seed;
	static String savePath;
    public void draw() {
    	seed.getBlock().setType(Material.ACACIA_PLANKS);
    }
    public Location getSeed() {
    	return this.seed;
    }
    public static void setSavePath(String s) {
    	Structure.savePath = s;
    }
    
}
