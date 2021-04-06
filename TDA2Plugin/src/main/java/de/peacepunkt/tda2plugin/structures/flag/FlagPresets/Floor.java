package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import org.bukkit.Location;
import org.bukkit.Material;

public class Floor implements FlagTypeInterface {
    FlagGameobject flag;

    Floor(FlagGameobject flagGameobject) {
        this.flag = flagGameobject;
    }

    @Override
    public void draw(int status) {
        Location origin = flag.getSeedLocation();
        Material[] m = new Material[9];
        for(int i = 0; i < 9; i++) {
            if(status != Flag.max) {
                if (i < status) {
                    m[i] = flag.getCurrent().getSecondTeamMaterial();
                } else {
                    m[i] = DefaultTeemThemes.getDefaultTeemTheme(-1).TeamMaterial;
                }
            } else {
                m[i] = flag.getCurrent().getTeamMaterial();
            }
        }
        origin.getBlock().setType(m[0]);
        origin.add(1,0,0).getBlock().setType(m[1]);
        origin.add(0,0,1).getBlock().setType(m[2]);
        origin.add(-1,0,0).getBlock().setType(m[3]);
        origin.add(-1,0,0).getBlock().setType(m[4]);

        origin.add(0,0,-1).getBlock().setType(m[5]);
        origin.add(0,0,-1).getBlock().setType(m[6]);
        origin.add(1,0,0).getBlock().setType(m[7]);
        origin.add(1,0,0).getBlock().setType(m[8]);
    }
}
