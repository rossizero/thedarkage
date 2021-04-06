package de.peacepunkt.tda2plugin.structures.Volcano;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Volcano extends AbstractStructure {//BukkitRunnable {
    public Volcano() {}
    public static String getStructureName() {
        return "volcano";
    }
    @Override
    public AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        return new VolcanoGameobject((Volcano) structure, world, noFunc, draw);
    }
}
