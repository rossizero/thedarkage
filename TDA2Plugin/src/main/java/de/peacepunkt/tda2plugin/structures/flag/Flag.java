package de.peacepunkt.tda2plugin.structures.flag;
import java.util.List;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.World;

public class Flag extends AbstractStructure {
	public static int max = 9; //height of flag

	public int id = 0;
	public int teamId = -1; //team id
	public Vector spawn;
	public int spawnRotation = 0; //new field
	public Vector cappingZone = null;
	public double cappingRadius = 8.0;
	public double cappingHeight = max;

	public int direction;
	public int type; //0: flag, 1: banner, 2 : 3x3 flat, 3: no blocks
	public boolean conquerable;
	public boolean flyOnSpawn;
	public boolean birds;
	public String name;
	public List<FlagSpawnBlock> connections;
	public List<FlagCustomBlock> customBlocks;

	@Override
	public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
		return new FlagGameobject((Flag) structure, world, noFunc, draw);
	}
	public static String getStructureName() {
		return "flag";
	}
}

