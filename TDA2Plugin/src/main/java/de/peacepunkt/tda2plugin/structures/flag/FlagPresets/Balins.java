package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Location;
import org.bukkit.Material;

public class Balins implements FlagTypeInterface {
    FlagGameobject flag;

    Balins(FlagGameobject flagGameobject) {
        this.flag = flagGameobject;
    }

    @Override
    public void draw(int status) {
        Location origin = flag.getSeedLocation();
        Teem current = flag.getCurrent();
        Material[] m = new Material[8];
        for(int i = 0; i < 8; i++) {
            if(status != Flag.max) {
                if (i < status) {
                    m[i] = current.getSecondTeamMaterial();
                } else {
                    m[i] = DefaultTeemThemes.getDefaultTeemTheme(-1).TeamMaterial;
                }
            } else {
                m[i] = current.getTeamMaterial();
            }
        }
        if(flag.getDirection() == 0 || flag.getDirection() == 2) {
            origin.getBlock().setType(m[0]);
            origin.clone().add(1, 0, 0).getBlock().setType(m[1]);

            origin.clone().add(0, 0, 1).getBlock().setType(m[2]);
            origin.clone().add(1, 0, 1).getBlock().setType(m[3]);

            origin.clone().add(0, 0, 2).getBlock().setType(m[4]);
            origin.clone().add(1, 0, 2).getBlock().setType(m[5]);

            origin.clone().add(0, 0, 3).getBlock().setType(m[6]);
            origin.clone().add(1, 0, 3).getBlock().setType(m[7]);
        } else {
            origin.getBlock().setType(m[0]);
            origin.clone().add(0, 0, 1).getBlock().setType(m[1]);

            origin.clone().add(1, 0, 0).getBlock().setType(m[2]);
            origin.clone().add(1, 0, 1).getBlock().setType(m[3]);

            origin.clone().add(2, 0, 0).getBlock().setType(m[4]);
            origin.clone().add(2, 0, 1).getBlock().setType(m[5]);

            origin.clone().add(3, 0, 0).getBlock().setType(m[6]);
            origin.clone().add(3, 0, 1).getBlock().setType(m[7]);
        }
    }
}
