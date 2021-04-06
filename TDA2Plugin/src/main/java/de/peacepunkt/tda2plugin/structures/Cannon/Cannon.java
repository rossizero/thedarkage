package de.peacepunkt.tda2plugin.structures.Cannon;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import org.bukkit.World;

public class Cannon extends AbstractStructure {
    public Cannon() {}

    public static String getStructureName() {
        return "cannon";
    }

    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return new CannonGameobject((Cannon) structure, world, noFunc, draw);
    }
}
