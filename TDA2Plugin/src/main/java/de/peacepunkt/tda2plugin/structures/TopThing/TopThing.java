package de.peacepunkt.tda2plugin.structures.TopThing;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import org.bukkit.World;

public class TopThing extends AbstractStructure {
    public int type = 0;
    public String worldname;

    public TopThing(){}

    public static String getStructureName() {
        return "topthing";
    }

    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return new TopThingGameobject((TopThing) structure, world, noFunc, draw);
    }
}
