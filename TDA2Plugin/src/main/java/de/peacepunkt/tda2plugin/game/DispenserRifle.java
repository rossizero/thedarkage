package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.Random;

public class DispenserRifle {
    Block block;
    Player player;
    Main main;

    public DispenserRifle(Block block, Player player, Main main) {
        this.block = block;
        setOwner(player);
        this.main = main;
    }

    public void check() {
        if(this.getBlock().getBlockPower() > 0) {
            for(int i = 0; i < new Random().nextInt(5); i++) {
                shoot();
            }
        }
    }
    public void setOwner(Player player) {
        player.sendMessage("You now own this auto-gun! All kills made by this gun will be added to your stats!");
        this.player = player;
    }
    public Block getBlock() {
        return block;
    }
    private void shoot() {
        double dirx = 0;
        double dirz = 0;

        if (block.getBlockData() instanceof Directional) {
            final Directional directional = (Directional) block.getBlockData();
            BlockFace dir = directional.getFacing();
            if(dir.equals(BlockFace.NORTH))  {
                dirz = -2.5;
            } else if(dir.equals(BlockFace.EAST))  {
                dirx = 2.5;
            } else if(dir.equals(BlockFace.SOUTH))  {
                dirz = 2.5;
            } else if(dir.equals(BlockFace.WEST)) {
                dirx = -2.5;
            }
            Random random = new Random();

            float speed = 3 + random.nextFloat() * (7 - 3);
            Arrow arrow = block.getWorld().spawnArrow(block.getLocation().add(dirx, 0.5, dirz), new Vector(dirx, 0, dirz), speed, 12);
            if(this.player != null) {
                arrow.setShooter(this.player);
                arrow.setMetadata("autogun", new SimpleMetadataValue("true", main));
            }
        }
    }
}
