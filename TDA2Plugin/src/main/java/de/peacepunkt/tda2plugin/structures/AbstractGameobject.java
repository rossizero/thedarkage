package de.peacepunkt.tda2plugin.structures;

import de.peacepunkt.tda2plugin.MainHolder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Ingame objects to interact with
 * contains all logic
 */
public abstract class AbstractGameobject implements Listener {
    AbstractStructure structure;
    World world;

    public AbstractGameobject(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        this.structure = structure;
        this.world = world;
        setup(structure, world, noFunc, draw);

        if(!noFunc)
            register();
        if(draw)
            draw();

        System.out.println(this.getClass() + " gameobject made");
    }

    public Location getLocation() {
        return Vector.locationFromVector(world, structure.seed); //new Location(world, structure.seed.x, structure.seed.y, structure.seed.z);
    }
    protected abstract void setup(AbstractStructure structure, World world, boolean noFunc, boolean draw);

    public void register() {
        MainHolder.main.getServer().getPluginManager().registerEvents(this, MainHolder.main);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    public void draw(){}

    public AbstractStructure getStructure() {
        return structure;
    }
}
