package de.peacepunkt.tda2plugin.structures.Ramme;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import org.bukkit.World;

public class Ramme extends AbstractStructure {
    public Ramme() {}
    public static String getStructureName() {
        return "ramme";
    }
    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return null;
    }
}
