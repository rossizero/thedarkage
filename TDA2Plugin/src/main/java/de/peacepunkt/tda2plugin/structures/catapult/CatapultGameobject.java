package de.peacepunkt.tda2plugin.structures.catapult;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.ExplosiveFallingBlock;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class CatapultGameobject extends AbstractGameobject {
    private Location Sign1, Sign2, seed_location;
    private int dir;
    private int reload_time = 30; //30*20ticks = 30 secs
    private int stat = 0; // 0: can shoot, 1:half done, 2: grade geschossen
    private boolean flame = false;
    private int explosion_radius = 3;
    private int rotated = 0; //0 = n, 1 = o, 2 = s, 3 = w
    private LocalDateTime lastShot;

    private float up = 0, left = 0;
    private double force;
    private int max = 90;

    public CatapultGameobject(Catapult structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw); //to soon
    }

    @Override
    protected void setup(AbstractStructure structure, World world, boolean noFunc, boolean draw) {
        seed_location = de.peacepunkt.tda2plugin.structures.Vector.locationFromVector(world, structure.seed);
        dir = ((Catapult)structure).dir;
        Sign1 = seed_location.clone().add(-1,0,0);
        Sign2 = seed_location.clone().add(-2,0,0);
        setSigns();
        if(!noFunc) {
            init();
            force = MainHolder.main.getRoundHandler().getRound().getCataStrength();
        }
    }

    private void init() {
        Random random = new Random();
        int randomInt = random.nextInt(10);
        if(randomInt == 0) {
            flame = true;
        }
        drawReady();
    }

    private void setSigns() {
        Location s1 = StructureUtils.rotate(Sign1, seed_location, dir);
        Sign sign = (Sign) s1.getBlock().getState();
        sign.setLine(0, "left / right");
        sign.setLine(2, String.valueOf(left));
        sign.update();

        Location s2 = StructureUtils.rotate(Sign2, seed_location, dir);
        sign = (Sign) s2.getBlock().getState();
        sign.setLine(0, "up / down");
        sign.setLine(2, String.valueOf(up));
        sign.update();
    }
    private void vertical(Boolean up) {
        if(up && !(this.up > max)) {
            this.up+= 1.5;
        } else if(!up && !(this.up < -max)){
            this.up-= 1.5;
        }
        setSigns();
    }
    private void horizontal(Boolean right) {
        if(right && !(this.left > max)) {
            this.left+= 1.5;
        } else if(!right && !(this.left < -max)){
            this.left-= 1.5;
        }
        setSigns();
    }

    public void draw() {
        if (stat == 0) {
            drawReady();
        } else if (stat == 1) {
            drawHalf();
        } else {
            drawShot();
        }
        setSigns();
    }

    private Vector fromSettings() {
        switch (dir) {
            case 2: //90 degrees == south
                return new Vector(-(left)/max * force, (up+90)/(max/2) * force, 1*force);
            case 3: //180 degrees == west
                return new Vector(-1*force, (up+90)/(max/2) * force, -(left)/max * force);
            case 0: //270 degrees == north
                return new Vector((left)/max * force, (up+90)/(max/2) * force, -1*force);
            default: //0 degrees == east
                return new Vector(1*force, (up+90)/(max/2) * force, (left)/max * force);
        }
    }
    private void reload() {
        //half done
        new BukkitRunnable() {
            @Override
            public void run() {
                stat = 1;
                draw();
            }
        }.runTaskLater(MainHolder.main, reload_time/2 * 20);

        //completely reloaded
        new BukkitRunnable() {
            @Override
            public void run() {
                stat = 0;
                draw();
            }
        }.runTaskLater(MainHolder.main, reload_time * 20);
    }
    private long getTime() {
        return lastShot.until(LocalDateTime.now(), ChronoUnit.SECONDS);
    }
    private void fire(Location a, Player p) {
        if(stat == 0) {
            Material test = Material.COBBLESTONE;
            if(flame)
                test = Material.MAGMA_BLOCK;
            p.playSound(p.getLocation(), Sound.BLOCK_PISTON_EXTEND, 5, 0);
            new ExplosiveFallingBlock(MainHolder.main, StructureUtils.rotate(a.clone().add(3, 7, 2), seed_location, dir), test)
                    .setFire(flame)
                    .setDeathMessage("You were shot by @t with a catapult!")
                    .setKillMessage("You killed @t with a catapult!")
                    .setVelocity(fromSettings())
                    .setExplosionRadius(explosion_radius)
                    .setAddStats(true)
                    .setTimeout(reload_time)
                    //.addPassenger(p.isSneaking() ? p : null) //TODO check if this works again (falling block passangers)
                    .setFallThroughWater(true)
                    .setMakeExplodedBlocks(true)
                    .setPlayer(p);
            stat = 2;
            draw();
            lastShot = LocalDateTime.now();
            reload();
        } else {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "Catapult is ready in " + (reload_time - getTime()) + " seconds!"));
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block != null) {
            if(block.getType().equals(Material.LEVER)) {
                if(block.getBlockPower() >= 1) {
                    if (event.getClickedBlock().getLocation().equals(seed_location.clone().add(0, 1, 0))){
                        fire(seed_location, event.getPlayer());
                    }
                }
            } else if(block.getType().equals(Material.OAK_WALL_SIGN)) {
                if(block.getLocation().equals(StructureUtils.rotate(Sign1, seed_location, dir))) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        horizontal(false);
                    } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        horizontal(true);
                    }
                } else if(block.getLocation().equals(StructureUtils.rotate(Sign2, seed_location, dir))) {
                    if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        vertical(true);
                    } else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        vertical(false);
                    }
                }
            }
        }
    }

    public void hide() {
        allAir();
        Location loc = seed_location;
        StructureUtils.rotate(loc.clone().add(0,1,0), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-1,0,0), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-2,0,0), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc, seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-2,0,1), seed_location, dir).getBlock().setType(Material.AIR);
    }
    public void allAir() {
        Location loc = seed_location;
        StructureUtils.rotate(loc.clone().add(-1,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-2,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(0,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-1,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-2,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-3,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-4,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-5,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-6,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-7,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-6,0,1), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-6,0,3), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(-6,1,2), seed_location, dir).getBlock().setType(Material.AIR);


        StructureUtils.rotate(loc.clone().add(1,0,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,1,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,2,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,3,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,4,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,5,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,6,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,7,2), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,6,1), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,7,1), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,6,3), seed_location, dir).getBlock().setType(Material.AIR);
        StructureUtils.rotate(loc.clone().add(1,7,3), seed_location, dir).getBlock().setType(Material.AIR);

    }
    public void drawReady() {
        Location loc = seed_location;
        drawHalf();
        //stone
        StructureUtils.rotate(loc.clone().add(-6,1,2), seed_location, dir).getBlock().setType(Material.COBBLESTONE_SLAB);
        if(flame)
            StructureUtils.rotate(loc.clone().add(-6,1,2), seed_location, dir).getBlock().setType(Material.MAGMA_BLOCK);
    }
    public void drawHalf() {
        allAir();
        Location loc = seed_location;
        //east
        StructureUtils.rotate(loc, seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(0,1,0), seed_location, dir).getBlock().setType(Material.LEVER);
        BlockState stairs = StructureUtils.rotate(loc.clone().add(0,1,0), seed_location, dir).getBlock().getState();
        BlockData data = Bukkit.createBlockData("minecraft:lever[face=floor, facing="+StructureUtils.rotate("south", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        //rotate(loc.clone().add(-1,0,0)).getBlock().setType(Material.OAK_WALL_SIGN);
        StructureUtils.rotate(Sign1.clone(), seed_location, dir).getBlock().setType(Material.OAK_WALL_SIGN);
        //stairs = rotate(loc.clone().add(-1,0,0)).getBlock().getState();
        stairs = StructureUtils.rotate(Sign1.clone(), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_wall_sign[facing="+StructureUtils.rotate("west", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        //rotate(loc.clone().add(-2,0,0)).getBlock().setType(Material.OAK_WALL_SIGN);
        StructureUtils.rotate(Sign2.clone(), seed_location, dir).getBlock().setType(Material.OAK_WALL_SIGN);
        //stairs = rotate(loc.clone().add(-2,0,0)).getBlock().getState();
        stairs = StructureUtils.rotate(Sign2.clone(), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_wall_sign[facing="+StructureUtils.rotate("north", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(-2,0,1), seed_location, dir).getBlock().setType(Material.OAK_WOOD);

        //Stick

        StructureUtils.rotate(loc.clone().add(1,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(0,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(-1,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(-2,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(-3,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(-4,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);

        StructureUtils.rotate(loc.clone().add(-5,0,2), seed_location, dir).getBlock().setType(Material.OAK_PLANKS);
        StructureUtils.rotate(loc.clone().add(-6,0,2), seed_location, dir).getBlock().setType(Material.OAK_SLAB);
        StructureUtils.rotate(loc.clone().add(-7,0,2), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(-7,0,2), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("east", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(-6,0,1), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(-6,0,1), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("south", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(-6,0,3), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(-6,0,3), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("north", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();

        StructureUtils.rotate(loc.clone().add(-6,1,2), seed_location, dir).getBlock().setType(Material.AIR);
    }
    public void drawShot() {
        allAir();
        Location loc = seed_location;
        StructureUtils.rotate(loc.clone().add(1,0,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(1,1,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(1,2,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(1,3,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(1,4,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);
        StructureUtils.rotate(loc.clone().add(1,5,2), seed_location, dir).getBlock().setType(Material.OAK_WOOD);


        StructureUtils.rotate(loc.clone().add(1,6,2), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        BlockState stairs = StructureUtils.rotate(loc.clone().add(1,6,2), seed_location, dir).getBlock().getState();
        BlockData data = Bukkit.createBlockData("minecraft:oak_stairs[half=bottom, facing="+StructureUtils.rotate("west", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(1,7,2), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(1,7,2), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("west", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();

        StructureUtils.rotate(loc.clone().add(1,6,1), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(1,6,1), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("south", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(1,7,1), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(1,7,1), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=bottom, facing="+StructureUtils.rotate("south", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();

        StructureUtils.rotate(loc.clone().add(1,6,3), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(1,6,3), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=top, facing="+StructureUtils.rotate("north", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
        StructureUtils.rotate(loc.clone().add(1,7,3), seed_location, dir).getBlock().setType(Material.OAK_STAIRS);
        stairs = StructureUtils.rotate(loc.clone().add(1,7,3), seed_location, dir).getBlock().getState();
        data = Bukkit.createBlockData("minecraft:oak_stairs[half=bottom, facing="+StructureUtils.rotate("north", dir)+"]");
        stairs.setBlockData(data);
        stairs.update();
    }
}
