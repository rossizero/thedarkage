package de.peacepunkt.tda2plugin.structures.Gate;

import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.*;

public class Gate extends AbstractStructure{
    public Vector lever;

    public int width;
    public int height;
    public int dir;

    public boolean hideCompletely = false;
    public boolean drawBridge = false;
    public Material material;

    public Gate(){}

    public static String getStructureName() {
        return "gate";
    }

    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return new GateGameobject((Gate) structure, world, noFunc, draw);
    }

    public void setLever(Location lever) {
        this.lever = Vector.vectorFromLocation(lever);
    }
}
