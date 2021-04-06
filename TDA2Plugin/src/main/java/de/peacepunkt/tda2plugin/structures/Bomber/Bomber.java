package de.peacepunkt.tda2plugin.structures.Bomber;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.World;

public class Bomber extends AbstractStructure {
    public String lalala;

    public Bomber() {}

    public static String getStructureName() {
        return "bomber";
    }

    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return null;
    }

}
