package de.peacepunkt.tda2plugin.structures.catapult;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import org.bukkit.*;

public class Catapult extends AbstractStructure {
	public int dir = 1;

	public Catapult(){}
	public static String getStructureName() {
		return "catapult";
	}

	@Override
	public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
		return new CatapultGameobject((Catapult) structure, world, noFunc, draw);
	}
}
