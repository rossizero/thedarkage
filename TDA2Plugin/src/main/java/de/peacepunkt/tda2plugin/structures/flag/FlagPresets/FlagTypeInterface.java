package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;

public interface FlagTypeInterface {
    public void draw(int status);

    public static FlagTypeInterface getFlagTypeById(int id, FlagGameobject flagGameobject) {
        switch (id) {
            default:
                return new Castle(flagGameobject);
            case 1:
                return new Banner(flagGameobject);
            case 2:
                return new Floor(flagGameobject);
            case 3:
                return new Balins(flagGameobject);
            case 4:
                return new Custom(flagGameobject);
        }
    }
}
