package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DispenserChecker implements Listener {
    List<DispenserRifle> dispenserRifles;
    Main main;

    public DispenserChecker() {
        this.dispenserRifles = new ArrayList<>();
        this.main = MainHolder.main;
    }

    public void check() {
        for(DispenserRifle rifle : dispenserRifles) {
            if(new Random().nextFloat() > 0.5) {
                rifle.check();
            }
        }
    }
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getClickedBlock().getType().equals(Material.LEVER)) {
                Block block = event.getClickedBlock();
                if (block.getBlockPower() > 0) {
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++) {
                            for (int z = -1; z < 2; z++) {
                                if (block.getRelative(x, y, z).getType().equals(Material.DISPENSER)) {
                                    for (DispenserRifle rifle : dispenserRifles) {
                                        if (rifle.getBlock().equals(block.getRelative(x, y, z))) {
                                            rifle.setOwner(event.getPlayer());
                                            return;
                                        }
                                    }
                                    dispenserRifles.add(new DispenserRifle(block.getRelative(x, y, z), event.getPlayer(), main));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
