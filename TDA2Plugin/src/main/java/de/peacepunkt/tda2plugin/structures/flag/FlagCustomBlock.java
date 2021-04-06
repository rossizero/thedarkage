package de.peacepunkt.tda2plugin.structures.flag;

import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.Location;

/**
 * To store locations of blocks that change if their flag changes
 */
public class FlagCustomBlock {
    public int status; //at what state of the flag do we change to a teams color
    public Vector location;

    private FlagGameobject flag;

    FlagCustomBlock() {}

    public void setFlag(FlagGameobject flag) {
        this.flag = flag;
    }
    public FlagGameobject getFlag() {return flag;}

    public boolean isAt(Location location) {
        return this.location.equals(location);
    }
}
