package de.peacepunkt.tda2plugin.structures;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ExplosiveFallingBlock extends BukkitRunnable implements Listener {
    private FallingBlock fallingBlock;
    private LocalDateTime spawnTime;
    private Main main;
    private ArrayList<Player> players_near_explosion;
    //settings
    private int timeout = 30;
    private boolean fire = false;
    private boolean fallThroughWater = true;
    private Player player;
    private String killMessage;
    private String deathMessage;
    private boolean addStats = false;
    private float explosionRadius = 3;
    private boolean makeExplodedBlocks = false;
    private boolean smoke = true;

    public ExplosiveFallingBlock(Main main, FallingBlock fallingBlock) {
        this.fallingBlock = fallingBlock;
        fallingBlock.setDropItem(false);
        spawnTime = LocalDateTime.now();
        this.main = main;
        players_near_explosion = new ArrayList<Player>();
        start();
    }
    public ExplosiveFallingBlock(FallingBlock fallingBlock) {
        this.fallingBlock = fallingBlock;
        fallingBlock.setDropItem(false);
        spawnTime = LocalDateTime.now();
        this.main = MainHolder.main;
        players_near_explosion = new ArrayList<Player>();
        start();
    }
    public ExplosiveFallingBlock(Main main, Location location, Material material) {
        spawnTime = LocalDateTime.now();
        this.main = main;
        fallingBlock = location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(material));
        fallingBlock.setDropItem(false);
        players_near_explosion = new ArrayList<Player>();
        start();
    }

    public ExplosiveFallingBlock(Location location, Material material) {
        spawnTime = LocalDateTime.now();
        this.main = MainHolder.main;
        fallingBlock = location.getWorld().spawnFallingBlock(location, Bukkit.createBlockData(material));
        fallingBlock.setDropItem(false);
        players_near_explosion = new ArrayList<Player>();
        start();
    }

    private void start() {
        main.getServer().getPluginManager().registerEvents(this, main);
        runTaskTimer(main, 0 , 1);
    }
    private void delete() {
        if(fallingBlock != null)
            fallingBlock.remove();
        //TODOfallingBlock = null;
        cancel();
        HandlerList.unregisterAll(this);
    }
    @Override
    public void run() {
        if(fallingBlock != null) {
            if(!alive()) {
                explode();
            } else {
                if(fire)
                    fallingBlock.getWorld().spawnParticle(Particle.FLAME, fallingBlock.getLocation(), 3, 0.5, 0.5, 0.5, 0, null, true);
                if(smoke)
                    fallingBlock.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, fallingBlock.getLocation(), 3, 0.5, 0.5, 0.5, 0, null, true);
                checkNearbyBlocks();
            }
        } else { //if it got deleted by the game. Eg if it is below y 0 or so.
            delete();
        }
    }

    public boolean alive() {
        long seconds = spawnTime.until(LocalDateTime.now(), ChronoUnit.SECONDS);
        return timeout >= seconds;
    }
    private boolean checkBlock(Location loc) {
        if(loc.getBlock().getType().equals(Material.AIR) || (fallThroughWater && loc.getBlock().getType().equals(Material.WATER))) {
            return false;
        } else {
            return true;
        }
    }

    private void checkNearbyBlocks() {
            if (fallingBlock.getVelocity().equals(new Vector(0.0, 0.0, 0.0))) {

                explode();
            } else {
                for (int i = 0; i < 2; i++) {
                    if (fallingBlock != null) {
                        if(fallingBlock.getLocation().getY()-i > 0) {
                            boolean left = checkBlock(fallingBlock.getLocation().clone().add(1, -i, 0));
                            boolean right = checkBlock(fallingBlock.getLocation().clone().add(-1, -i, 0));
                            boolean front = checkBlock(fallingBlock.getLocation().clone().add(0, -i, 1));
                            boolean back = checkBlock(fallingBlock.getLocation().clone().add(0, -i, -1));
                            boolean left2 = checkBlock(fallingBlock.getLocation().clone().add(1, -i, 1));
                            boolean right2 = checkBlock(fallingBlock.getLocation().clone().add(-1, -i, 1));
                            boolean front2 = checkBlock(fallingBlock.getLocation().clone().add(-1, -i, 1));
                            boolean back2 = checkBlock(fallingBlock.getLocation().clone().add(1, -i, -1));
                            if (front2 || back2 || left2 || right2 || left || right || front || back) {
                                explode();
                                return;
                            }
                        }
                    } else { //could be already removed by explode
                        return;
                    }
                }
            }
    }
    public ExplosiveFallingBlock setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    public ExplosiveFallingBlock setFire(boolean fire) {
        this.fire = fire;
        return this;
    }

    public ExplosiveFallingBlock setFallThroughWater(boolean fallThroughWater) {
        this.fallThroughWater = fallThroughWater;
        return this;
    }

    public ExplosiveFallingBlock setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ExplosiveFallingBlock setKillMessage(String killMessage) {
        this.killMessage = killMessage;
        return this;
    }

    public ExplosiveFallingBlock setDeathMessage(String deathMessage) {
        this.deathMessage = deathMessage;
        return this;
    }

    public ExplosiveFallingBlock setSmoke(boolean bool) {
        this.smoke = bool;
        return this;
    }

    public ExplosiveFallingBlock setAddStats(boolean addStats) {
        this.addStats = addStats;
        return this;
    }

    public ExplosiveFallingBlock setVelocity(Vector velocity) {
        fallingBlock.setVelocity(velocity);
        return this;
    }
    public ExplosiveFallingBlock setExplosionRadius(float explosionRadius) {
        this.explosionRadius = explosionRadius;
        return this;
    }
    public ExplosiveFallingBlock addPassenger(Player p) {
        if(p != null) {
            fallingBlock.addPassenger(p);
            counterHelp = LocalDateTime.now();
        }
        return this;
    }

    public ExplosiveFallingBlock setMakeExplodedBlocks(boolean makeExplodedBlocks) {
        this.makeExplodedBlocks = makeExplodedBlocks;
        return this;
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getHitEntity() != null ) {
            if (event.getHitEntity().equals(fallingBlock)) {
                explode();
            }
        }
    }
    @EventHandler
    public void onEntityChangeBlockEvent(EntityChangeBlockEvent event) {
        if (event.getEntity().equals(fallingBlock)) {
            event.setCancelled(true); //makes the falling block disapear
            explode();
        }
    }
    private  void explode() {
        List<Player> players = fallingBlock.getWorld().getPlayers();
        for (Player other : players) {
            if (other.getLocation().distance(fallingBlock.getLocation()) < 2 * explosionRadius) {
                players_near_explosion.add(other);
            }
        }
        if(makeExplodedBlocks)  {
            createExplodedBlocks();
        }
        fallingBlock.getWorld().createExplosion(fallingBlock.getLocation(), explosionRadius, fire);
        counterHelp = null;
        delete();
    }
    private double randomDouble(double min, double max) {
        return min + new Random().nextFloat() * (max + min);
    }
    private void createExplodedBlocks() {
        List<Block> blocks = new ArrayList<>();
        for(int x = (int) (-explosionRadius); x < explosionRadius; x++) {
            for (int y = (int) (-explosionRadius); y < explosionRadius; y++) {
                for (int z = (int) (-explosionRadius); z < explosionRadius; z++) {
                    Block block = fallingBlock.getLocation().getBlock().getRelative(x, y, z);
                    if(!block.getType().equals(Material.AIR) && !block.getType().equals(Material.CAVE_AIR) && !block.getType().equals(Material.WATER) && !block.getType().equals(Material.LAVA)) {
                        Vector direction = block.getLocation().toVector().subtract(fallingBlock.getLocation().toVector());
                        double force = explosionRadius - direction.length();
                        direction.normalize().multiply(force / 2);
                        direction.add(new Vector(0, 1, 0));
                        direction.add(new Vector(randomDouble(-0.3, 0.3), randomDouble(-0.3, 0.3), randomDouble(-0.3, 0.3)));
                        FallingBlock fall = block.getWorld().spawnFallingBlock(block.getLocation(), Bukkit.createBlockData(block.getType()));
                        fall.setVelocity(direction);
                        fall.setDropItem(false);
                        blocks.add(block);
                    }
                }
            }
        }
        //To remove ghost blocks, I dont know if the delay is necessary
        new BukkitRunnable() {
            List<Player> players = fallingBlock.getWorld().getPlayers();
            @Override
            public void run() {
                for(Player player : players) {
                    for(Block block : blocks) {
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                }
            }
        }.runTaskLater(main, 10);
    }

    LocalDateTime counterHelp;
    @EventHandler
    public void onEntityDismountEvent(EntityDismountEvent event) {
        if(fallingBlock != null) {
            if (event.getEntity() instanceof Player) {
                if(fallingBlock.getPassengers().size() > 0) {
                    if (fallingBlock.getPassengers().contains(event.getEntity())) {
                        if (counterHelp != null) {
                            Duration duration = Duration.between(counterHelp, LocalDateTime.now());
                            if (Duration.between(counterHelp, LocalDateTime.now()).getSeconds() < 3) {
                                event.setCancelled(true);
                                ((Player)event.getEntity()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("You can leave in "  + (3-duration.getSeconds())));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player p = (Player) e.getEntity();
        if(p.getLastDamageCause() != null) {
            if (p.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && players_near_explosion.contains(p)) {
                if(player != null && addStats) {
                    player.sendMessage(getKillMessage(p));
                    main.getRoundHandler().getRound().getTeam(player).getRoundStats().addKill(player, PlayerHandler.getInstance().getKit(player).getKitDescription().getName());
                    p.sendMessage(getDeathMessage(player));
                    e.setDeathMessage("");
                    //main.getRoundHandler().getRound().getTeam(p).getRoundStats().addDeath(p);
                }
            }
        }
    }

    /**
     * Replaces all @t with killer.getName()
     * @t at beginning will be skipped though, cause I am lazy
     * @param killer
     * @return
     */
    private String getDeathMessage(Player killer) {
        if(deathMessage != null) {
            String[] parts = deathMessage.split("@t");
            String ret = parts[0] + " " + killer.getName();
            for (int i = 1; i < parts.length - 1; i++) {
                ret += parts[i] + " " + killer.getName();
            }
            ret += parts[parts.length-1];
            return ret;
        }
        return "";
    }

    /**
     * Replaces all @t with killed.getName()
     * @t at beginning will be skipped though, cause I am lazy
     * @param killed
     * @return
     */
    private String getKillMessage(Player killed) {
        if(killMessage != null) {
            String[] parts = killMessage.split("@t");
            String ret = parts[0] + " " + killed.getName();
            for (int i = 1; i < parts.length - 1; i++) {
                ret += parts[i] + " " + killed.getName();
            }
            return ret;
        }
        return "";
    }
}
