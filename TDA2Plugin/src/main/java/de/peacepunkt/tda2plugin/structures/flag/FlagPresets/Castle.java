package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import org.bukkit.Location;
import org.bukkit.Material;

public class Castle implements FlagTypeInterface {
    FlagGameobject flag;

    Castle(FlagGameobject flagGameobject) {
        this.flag = flagGameobject;
    }

    @Override
    public void draw(int status) {
        Location origin = flag.getSeedLocation();
        //stick
        origin.getBlock().setType(Material.COBBLESTONE_WALL);
        origin = reset();
        for (int i = 0; i < Flag.max + 1; i++) { //max+1 weil die FLagge 2 Bl�cke hoch ist
            origin.add(0, i, 0).getBlock().setType(Material.OAK_FENCE);
            origin = reset(); //reset
        }
        //0 = n, 1 = o, 2 = s, 3 = w
        //n < z < s
        //w < x < o
        int x = 0;
        int z = 0;
        if (flag.getDirection() == 0) {
            z = -1;
        } else if (flag.getDirection() == 2) {
            z = 1;
        } else if (flag.getDirection() == 1) {
            x = 1;
        } else {
            x = -1;
        }
        //fill with air
        for (int j = 0; j < Flag.max + 1; j++) { //H�he der Flagge
            for (int i = 1; i < 5; i++) { //l�nge der Flagge
                origin.add(i * x, j, i * z).getBlock().setType(Material.AIR);
                origin = reset(); //reset
            }

        }
        //update flag position
        for (int i = 1; i < 5; i++) {
            if (status == 0) {
                origin.add(i * x, status, i * z).getBlock().setType(DefaultTeemThemes.getDefaultTeemTheme(-1).TeamMaterial);
                origin = reset(); //reset
                origin.add(i * x, status + 1, i * z).getBlock().setType(DefaultTeemThemes.getDefaultTeemTheme(-1).SecondTeamMaterial);
                origin = reset(); //reset
            } else {
                origin.add(i * x, status, i * z).getBlock().setType(flag.getCurrent().getTeamMaterial());
                origin = reset(); //reset
                origin.add(i * x, status - 1, i * z).getBlock().setType(flag.getCurrent().getSecondTeamMaterial());
                origin = reset(); //reset
            }
        }
    }

    public Location reset() {
        //returns the lowest part of the Flag which usually should be oak fence
        return flag.getSeedLocation().clone().add(0, 1, 0);
    }
}
