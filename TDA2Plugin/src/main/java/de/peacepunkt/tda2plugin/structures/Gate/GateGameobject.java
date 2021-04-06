package de.peacepunkt.tda2plugin.structures.Gate;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.type.Fence;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class GateGameobject extends AbstractGameobject {
    private Location seedLocation;
    private Location leverLocation;

    private int width;
    private int height;
    private int dir;

    private boolean hideCompletely;
    private boolean drawBridge;
    private Material material;

    public GateGameobject(Gate structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw);
    }
    @Override
    protected void setup(AbstractStructure s, World world, boolean noFunc, boolean draw) {
        Gate structure = (Gate) s;

        this.seedLocation = Vector.locationFromVector(world, structure.seed);
        this.leverLocation = Vector.locationFromVector(world, structure.lever);

        this.width = structure.width;
        this.height = structure.height;
        this.dir = structure.dir;
        this.hideCompletely = structure.hideCompletely;
        this.drawBridge = structure.drawBridge;
        this.material = structure.material;

        if(draw)
            drawShown();
    }
    public void hide() {
        leverLocation.getBlock().setType(Material.AIR);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                StructureUtils.rotate(seedLocation.clone().add(i, 0, j), seedLocation, dir).getBlock().setType(Material.AIR);
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock().setType(Material.AIR);
            }
        }
    }

    private void drawHidden()  {
        if (leverLocation != null) {
            if(!leverLocation.getBlock().getType().equals(Material.LEVER)) {
                leverLocation.getBlock().setType(Material.LEVER);
            }
        }
        if(!drawBridge) {
            World arena = Bukkit.getWorld("arena");
            if(arena != null) {
                arena.playSound(seedLocation, Sound.BLOCK_ANVIL_LAND, 3, 0);
            }
            if (hideCompletely) {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock().setType(Material.AIR);
                    }
                }
            } else {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height - 1; j++) {
                        StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock().setType(Material.AIR);
                    }
                }
            }
        } else { //open / down
            World arena = Bukkit.getWorld("arena");
            if(arena != null) {
                arena.playSound(seedLocation, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 3, 0);
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock().setType(Material.AIR);
                }
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    StructureUtils.rotate(seedLocation.clone().add(i, 0, j), seedLocation, dir).getBlock().setType(material);
                }
            }
        }
    }

    /**
     * draws the gate :
     * ........
     * ........
     * *....... where * is the seed
     */
    private void drawShown() {
        if (leverLocation != null) {
            if(!leverLocation.getBlock().getType().equals(Material.LEVER)) {
                leverLocation.getBlock().setType(Material.LEVER);
            }
        }
        if(!drawBridge) {
            World arena = Bukkit.getWorld("arena");
            if(arena != null) {
                Sound clack = Sound.BLOCK_ANVIL_LAND;
                arena.playSound(seedLocation, clack, 1, 0);
                /*    new BukkitRunnable() {
                        @Override
                        public void run() {
                            rasselSound(true);
                        }
                    }.runTaskAsynchronously(MainHolder.main);*/
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Block block = StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock();
                    block.setType(material);
                    //update gates and iron_bars connections
                    if(block.getState().getBlockData() instanceof MultipleFacing) {
                        Fence fence = (Fence) block.getState().getBlockData();
                        for(BlockFace face: fence.getAllowedFaces()) {
                            if(!block.getLocation().add(face.getDirection()).getBlock().getType().equals(Material.AIR))
                                fence.setFace(face, true);
                        }
                        block.setBlockData(fence);
                    }
                }
            }
        } else { //closed / up
            World arena = Bukkit.getWorld("arena");
            if(arena != null) {
                arena.playSound(seedLocation, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 3, 0);
            }
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    StructureUtils.rotate(seedLocation.clone().add(i, 0, j), seedLocation, dir).getBlock().setType(Material.AIR);
                }
            }

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock().setType(material);
                }
            }
        }
    }
    private void rasselSound(boolean wood) {
        World arena = Bukkit.getWorld("arena");
        if(arena != null) {
            try {
                Sound clack = Sound.BLOCK_ANVIL_LAND;
                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(100);
                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(80);
                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(50);

                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(30);
                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(20);
                arena.playSound(seedLocation, clack, 1, 0);
                Thread.sleep(10);
                arena.playSound(seedLocation, clack, 1, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /*private void updateBlocks() {
        if(!drawBridge) {
            new BukkitRunnable() {
                List<Player> players = leverLocation.getWorld().getPlayers();
                List<Block> blocks = new ArrayList<>();

                @Override
                public void run() {
                    for (int i = 0; i < width; i++) {
                        for (int j = 0; j < height; j++) {
                            blocks.add(StructureUtils.rotate(seedLocation.clone().add(i, j, 0), seedLocation, dir).getBlock());
                        }
                    }
                    for (Player player : players) {
                        for (Block block : blocks) {
                            player.sendBlockChange(block.getLocation(), block.getBlockData());
                        }
                    }
                }
            }.runTaskLaterAsynchronously(MainHolder.main, 1);
        }
    }*/
    @EventHandler
    public void onInteract(PlayerInteractEvent Event) {
        if(leverLocation != null) {
            Block block = Event.getClickedBlock();
            if (block != null) {
                if (block.getType().equals(Material.LEVER)) {
                    if (Event.getClickedBlock().getLocation().equals(leverLocation.clone())) {
                        if (block.getBlockPower() >= 1) {
                            drawShown();
                        } else {
                            drawHidden();
                        }
                        //updateBlocks();
                    }
                }
            }
        }
    }
}
