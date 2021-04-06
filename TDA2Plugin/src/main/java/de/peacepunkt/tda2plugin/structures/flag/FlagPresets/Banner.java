package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import org.bukkit.Location;
import org.bukkit.Material;


public class Banner implements FlagTypeInterface {
    FlagGameobject flag;

    Banner(FlagGameobject flagGameobject) {
        this.flag = flagGameobject;
    }

    @Override
    public void draw(int status) {
        Location origin = flag.getSeedLocation();
        origin.getBlock().setType(Material.OAK_FENCE);
        origin.add(0,1,0).getBlock().setType(Material.OAK_FENCE);
        origin.add(0,1,0).getBlock().setType(Material.OAK_FENCE);
        origin.add(0,1,0).getBlock().setType(Material.OAK_FENCE);
        origin.add(0,1,0).getBlock().setType(Material.OAK_FENCE);
        origin.add(0,1,0).getBlock().setType(Material.OAK_FENCE);
        Material[] m = new Material[8];
        for(int i = 0; i < 8; i++) {
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
        if(flag.getDirection() == 0 || flag.getDirection() == 2) {
            origin.add(1,0,0).getBlock().setType(Material.OAK_FENCE);
            origin.add(1,0,0).getBlock().setType(Material.OAK_FENCE);
            Location tmp = origin.clone();
            tmp.add(0, -1, 0).getBlock().setType(m[0]);
            tmp.add(0, -1, 0).getBlock().setType(m[2]);
            tmp.add(0, -1, 0).getBlock().setType(m[4]);
            tmp.add(0, -1, 0).getBlock().setType(m[6]);

            origin.add(-3,0,0).getBlock().setType(Material.OAK_FENCE);
            origin.add(-1,0,0).getBlock().setType(Material.OAK_FENCE);
            tmp = origin.clone();
            tmp.add(0, -1, 0).getBlock().setType(m[1]);
            tmp.add(0, -1, 0).getBlock().setType(m[3]);
            tmp.add(0, -1, 0).getBlock().setType(m[5]);
            tmp.add(0, -1, 0).getBlock().setType(m[7]);

        } else {
            origin.add(0,0,1).getBlock().setType(Material.OAK_FENCE);
            origin.add(0,0,1).getBlock().setType(Material.OAK_FENCE);
            Location tmp = origin.clone();
            tmp.add(0, -1, 0).getBlock().setType(m[0]);
            tmp.add(0, -1, 0).getBlock().setType(m[2]);
            tmp.add(0, -1, 0).getBlock().setType(m[4]);
            tmp.add(0, -1, 0).getBlock().setType(m[6]);

            origin.add(0,0,-3).getBlock().setType(Material.OAK_FENCE);
            origin.add(0,0,-1).getBlock().setType(Material.OAK_FENCE);
            tmp = origin.clone();
            tmp.add(0, -1, 0).getBlock().setType(m[1]);
            tmp.add(0, -1, 0).getBlock().setType(m[3]);
            tmp.add(0, -1, 0).getBlock().setType(m[5]);
            tmp.add(0, -1, 0).getBlock().setType(m[7]);
        }
    }
}
