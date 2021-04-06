package de.peacepunkt.tda2plugin.structures;

import org.bukkit.World;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * to save data
 */
public abstract class AbstractStructure {
    public Vector seed;
    public AbstractStructure() {}

    /**
     * do not rename ever!
     * @return
     */
    public static String getStructureName() {
        return "undefined";
    }

    public static List<Class<? extends AbstractStructure>> getAllSubclasses() {
        Reflections reflections = new Reflections("de.peacepunkt");
        Set<Class<? extends AbstractStructure>> structures = reflections.getSubTypesOf(AbstractStructure.class);
        return new ArrayList<>(structures);
    }

    public abstract AbstractGameobject getGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw);
}
