package de.peacepunkt.tda2plugin.structures.flag.FlagPresets;

import de.peacepunkt.tda2plugin.structures.Vector;
import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagCustomBlock;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.team.DefaultTeemThemes;
import org.bukkit.Location;

public class Custom implements FlagTypeInterface {
    FlagGameobject flag;

    Custom(FlagGameobject flagGameobject) {
        this.flag = flagGameobject;
    }

    @Override
    public void draw(int status) {
        if(flag.getCustomBlocks() != null) {
            for (FlagCustomBlock block : flag.getCustomBlocks()) {
                Location location = Vector.locationFromVector(flag.getSeedLocation().getWorld(), block.location);
                if(status == Flag.max) {
                    location.getBlock().setType(flag.getCurrent().getTeamMaterial());
                } else if(status >= block.status) {
                    location.getBlock().setType(flag.getCurrent().getSecondTeamMaterial());
                } else {
                    location.getBlock().setType(DefaultTeemThemes.getDefaultTeemTheme(-1).TeamMaterial);
                }
            }
        }
    }
}
