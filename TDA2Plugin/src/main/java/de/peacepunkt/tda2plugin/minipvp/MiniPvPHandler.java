package de.peacepunkt.tda2plugin.minipvp;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.def.Swarm;
import de.peacepunkt.tda2plugin.game.command.SpawnWorldGenerator;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.minipvp.Commands.MiniPvPCommands;
import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MiniPvPHandler implements Listener {
    private List<Player> currentPlayers;
    Main main;
    List<LocationDirection> respawns;
    List<HitCounter> hitCounters;
    World miniarena;
    String fileName;
    final int inner_fight_radius = 20; //no build
    final int no_fight_radius = 30; //no pvp no build -> after that free build and pvp
    Swarm swarm;
    public MiniPvPHandler(Main main) {
        this.main = main;
        new MiniPvPCommands(main, this);
        currentPlayers = new ArrayList<Player>();
        respawns = new ArrayList<LocationDirection>();

        WorldCreator creator = new WorldCreator("miniarena");
        //creator.type(WorldType.CUSTOMIZED);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator(new SpawnWorldGenerator());
        World w = Bukkit.createWorld(creator);
        w.setGameRule(GameRule.DO_FIRE_TICK, false);
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        w.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        w.setGameRule(GameRule.MOB_GRIEFING, false);
        w.setGameRule(GameRule.RANDOM_TICK_SPEED, 1000);
        w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        w.setTime(1000);
        this.fileName = Main.arenasPath+"miniarena"+"/spawns.yml";
        new File(Main.arenasPath + "miniarena/").mkdirs();
        File f = new File(fileName);
        if(!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.miniarena = w;

        try {
            loadSpawns();
            List<Location> ret = new ArrayList<>();
            for(LocationDirection rd : respawns) {
                ret.add(rd.getLocation(miniarena).clone().add(0,10,0));
            }
            //TODO swarm = new Swarm(ret, main, 50);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        for(Entity e : miniarena.getEntities()) {
            if(e instanceof Bat) {
                e.remove();
            }
        }
    }
    public Swarm getSwarm() {
        return swarm;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        if(contains(event.getPlayer())) {
            LocationDirection respawn = getFreeRespawnLocation(event.getPlayer());
            event.setRespawnLocation(respawn.getLocation(miniarena));
            event.getPlayer().getLocation().setDirection(respawn.getDirection());
            //KitHandler.getInstance().restockKit(event.getPlayer(), false, false, true);
            //Kits.getInstance(main).setKit(event.getPlayer(), false);
            Location target = respawn.getLocation(miniarena);
            Material old = target.getBlock().getType();
            target.getBlock().setType(Material.END_GATEWAY);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);
            event.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));
        }
    }
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if(event.getClickedBlock().getWorld().equals(miniarena)) {
                if (event.getClickedBlock().getType().equals(Material.DRAGON_EGG)) {
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        event.setCancelled(true);
                    } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
    private void loadSpawns() throws IOException, InvalidConfigurationException {
        try {
            respawns = new ArrayList<>();
            hitCounters = new ArrayList<>();
            YamlConfiguration config = new YamlConfiguration();
            File f = new File(fileName);
            config.load(f);
            List<Map<?, ?>> list = config.getMapList("spawns");

            for (Entity e : miniarena.getEntities()) {
                if(e instanceof ArmorStand) {
                    e.remove();
                }
            }

            if(list != null) {
                for (Map m : list) {
                    int type = m.get("type") == null ? 0 : (int) (double) m.get("type");
                    if(type == 0) {
                        double x = (double) m.get("x");
                        double y = (double) m.get("y");
                        double z = (double) m.get("z");
                        double dx = (double) m.get("dx");
                        double dy = (double) m.get("dy");
                        double dz = (double) m.get("dz");
                        respawns.add(new LocationDirection(x, y, z, dx, dy, dz));

                        ArmorStand a = (ArmorStand) miniarena.spawnEntity(new Location(miniarena, x, y, z).add(0.5, -0.25 + 1, 0.5), EntityType.ARMOR_STAND);
                        a.setCustomName(ChatColor.YELLOW + "sneak to return");
                        a.setCustomNameVisible(true);
                        a.setVisible(false);
                        a.setGravity(false);
                        a.setInvulnerable(true);

                    } else if(type == 1) {
                        double x = (double) m.get("x");
                        double y = (double) m.get("y");
                        double z = (double) m.get("z");
                        hitCounters.add(new HitCounter(new Location(miniarena, x, y, z)));
                    }
                }
            }

        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }
    public List<Location> getSpawns() {
        List<Location> locs = new ArrayList<>();
        for(LocationDirection l : respawns) {
            locs.add(l.getLocation(miniarena));
        }
        return locs;
    }
    public void addSpawn(Player player, int type) {
        try {
            Map<String, Double> map = new HashMap<>();
            map.put("x", player.getLocation().getX());
            map.put("y", player.getLocation().getY());
            map.put("z", player.getLocation().getZ());
            if(type == 0) {
                map.put("dx", player.getLocation().getDirection().getX());
                map.put("dy", player.getLocation().getDirection().getY());
                map.put("dz", player.getLocation().getDirection().getZ());
            }
            map.put("type", (double) type);
            YamlConfiguration config = new YamlConfiguration();
            File f = new File(fileName);
            config.load(f);
            List<Map<?, ?>> list = config.getMapList("spawns");
            list.add(map);
            config.set("spawns", list);
            config.save(f);
            loadSpawns();
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private LocationDirection getFreeRespawnLocation(Player player) {
        if(respawns.size() != 0) {
            hideSpawns();
            Map<LocationDirection, Integer> map = new HashMap<>();
            for (LocationDirection l : respawns) {
                map.put(l, 0);
                for (Player p : currentPlayers) {
                    if(p.getWorld().equals(miniarena)) {
                        if(!p.equals(player)) {
                            if (p.getLocation().distance(l.getLocation(miniarena)) < 5) {
                                Integer i = map.get(l);
                                map.put(l, i + 1);
                            }
                        }
                    }
                }
            }
            int count = Integer.MAX_VALUE;
            for (LocationDirection l : map.keySet()) {
                if (map.get(l) < count) {
                    count = map.get(l);
                }
            }
            List<LocationDirection> curr_smallest = new ArrayList<>();
            for(LocationDirection l : respawns) {
                if(map.get(l) == count) {
                    curr_smallest.add(l);
                }
            }
            return curr_smallest.get(new Random().nextInt(curr_smallest.size()));
        } else {
            return new LocationDirection(miniarena.getSpawnLocation());
        }
    }
    public boolean contains(Player player) {
        return currentPlayers.contains(player);
    }

    /**
     * Checks if from's distance to to's is smaller than maxDistance at one Location of to's x and z in every height
     * @param from
     * @param to
     */
    private double distanceAllY(Location from, Location to) {
        double smallest = Double.MAX_VALUE;
        if(to.getWorld().equals(from.getWorld())) {
            for (int i = 0; i < 255; i++) {
                double tmp = from.distance(new Location(to.getWorld(), to.getX(), (double) i, to.getZ()));
                if (tmp < smallest) {
                    smallest = tmp;
                }
            }
        }
        return smallest;
    }
    public boolean inInnerPvpRadius(Player player) {
        if(contains(player)) {
            return inInnerPvpRadius(player.getLocation());
            /*if(player.getLocation().distance(miniarena.getSpawnLocation()) < inner_fight_radius) {
                return true;
            } else {
                return false;
            }*/
        }
        return false;
    }
    public boolean inInnerPvpRadius(Location l) {
        return distanceAllY(l, miniarena.getSpawnLocation()) < inner_fight_radius;
        //return l.distance(miniarena.getSpawnLocation()) < inner_fight_radius;
    }
    public boolean inPvpRadius(Player player) {
        if(contains(player)) {
            /*if(player.getLocation().distance(miniarena.getSpawnLocation()) < inner_fight_radius ||
                    player.getLocation().distance(miniarena.getSpawnLocation()) > no_fight_radius) {
                return true;
            } else {
                return false;
            }*/
            return inPvpRadius(player.getLocation());
        }
        return false;
    }
    public boolean inBuildRadius(Player player) {
        if(contains(player))
            return inBuildRadius(player.getLocation());//return player.getLocation().distance(miniarena.getSpawnLocation()) > no_fight_radius;
        else
            return false;
    }
    public boolean inBuildRadius(Location l) {
        return distanceAllY(l, miniarena.getSpawnLocation()) > no_fight_radius;
        //return l.getWorld().equals(miniarena) && l.distance(miniarena.getSpawnLocation()) > no_fight_radius;
    }
    public boolean inPvpRadius(Location l) {
        /*if(l.getWorld().equals(miniarena)) {
            return l.distance(miniarena.getSpawnLocation()) > no_fight_radius || l.distance(miniarena.getSpawnLocation()) < inner_fight_radius;
        } else {
            return false;
        }*/
        return inBuildRadius(l) || inInnerPvpRadius(l);
    }
    private void hideSpawns() {
        for(LocationDirection l : respawns) {
            Location target = l.getLocation(miniarena);
            target.getBlock().setType(Material.AIR);
        }
    }
    public void showSpawns() {
        for(LocationDirection l : respawns) {
            Location target = l.getLocation(miniarena);
            Material old = target.getBlock().getType();
            target.getBlock().setType(Material.END_GATEWAY);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);
        }
    }
    private void sendTextLater(Player player, String text, int delay) {
        new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.GREEN + text);
            }
        }.runTaskLater(main, delay);
    }
    public void addPlayer(Player player) {
        System.out.println("adding player " + player.getName());
        if(!contains(player)) {
            LocationDirection l = getFreeRespawnLocation(player);
            player.teleport(l.getLocation(miniarena));
            player.sendTitle("Test Arena", "/leavetest to leave", 10, 70, 10);
            //player.setDisplayName( ChatColor.GRAY+ "[Mini-Arena] " + ChatColor.RESET + player.getName() + ChatColor.RESET);
            player.getLocation().setDirection(l.getDirection());
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendTextLater(player, "Hey you!", 100);
                    sendTextLater(player, "You can test every class here and practice pvp on a smaller map!", 120);
                    sendTextLater(player, "All kills and deaths wont affect your stats!", 140);
                    sendTextLater(player, "You get a block for each kill that you can place behind the no pvp zone!", 160);
                }
            }.runTaskLater(main, 100);
            currentPlayers.add(player);

            KitHandler.getInstance().restockKit(player, false, false, true);
            //Kits.getInstance(main).setKit(player, false);
            player.getPlayer().getInventory().setHelmet(new ItemStack(Material.AIR));

            Location target = l.getLocation(miniarena);
            Material old = target.getBlock().getType();
            target.getBlock().setType(Material.END_GATEWAY);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> target.getBlock().setType(old), 100);

        } else {
            player.sendMessage(ChatColor.GREEN + "You're already here...");
        }
    }
    public void removePlayer(Player player) {
        if(contains(player)) {
            currentPlayers.remove(player);
            player.teleport(main.getRoundHandler().getRound().getTeam(player).spawnLocation());
            player.sendMessage(ChatColor.GREEN + "Now leaving the test arena");
            KitHandler.getInstance().setKit(player, main.getRoundHandler().getRound().getTeam(player).getDefaultKit(), true, true, true);
            //Kits.getInstance(main).setKit(player, main.getRoundHandler().getRound().getTeam(player).getDefaultKit(), true, true, true);
        } else {
            player.sendMessage(ChatColor.GREEN + "You're not in the test arena...");
        }
    }
    public List<Player> getCurrentPlayers() {
        System.out.println(currentPlayers.size());
        return currentPlayers;
    }
    public ItemStack getBuildBlock() {
        Material material = null;
        Random random = new Random();
        while(material == null)
        {
            material = material.values()[random.nextInt(material.values().length)];
            if(!(material.isBlock()) || !(material.getMaxStackSize() > 16))
            {
                material = null;
            } else if(material.equals(Material.STRUCTURE_BLOCK) ||
                    material.equals(Material.STRUCTURE_VOID) ||
                    material.equals(Material.CAVE_AIR) ||
                    material.equals(Material.TNT) ||
                    material.equals(Material.END_CRYSTAL) ||
                    material.equals(Material.COMMAND_BLOCK) ||
                    material.equals(Material.CHAIN_COMMAND_BLOCK) ||
                    material.equals(Material.RESPAWN_ANCHOR) ||
                    material.equals(Material.REPEATING_COMMAND_BLOCK)) {
                material = null;
            } else if(!material.isItem()) {
                material = null;
            }
        }
        return new ItemStack(material, 1);
    }
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if(currentPlayers.contains(event.getPlayer())) {
            currentPlayers.remove(event.getPlayer());
        }
    }



    class LocationDirection {
        double x, y, z, dx, dy, dz;
        public LocationDirection(Location l) {
            this.x = l.getX();
            this.y = l.getY();
            this.z = l.getZ();
            this.dx = 0;
            this.dy = 0;
            this.dz = 0;
        }
        public LocationDirection(double x, double y,double z,double dx,double dy,double dz) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dx = dx;
            this.dy = dy;
            this.dz = dz;
        }

        public Location getLocation(World w) {
            return new Location(w, x, y, z);
        }
        public Vector getDirection() {
            return new Vector(dx, dy, dz);
        }
    }

    class HitCounter implements Listener {
        Location location;
        Player player;
        int counter;
        int delay = 10;

        HitCounter(Location location) {
            this.location = location;
            //miniarena.getBlockAt(location).setType(Material.HAY_BLOCK);
            main.getServer().getPluginManager().registerEvents(this, main);
        }

        @EventHandler
        public void onPlayerInteractEvent(PlayerInteractEvent event) {
            if (event.getClickedBlock() != null) {
                if(event.getClickedBlock().getWorld().equals(miniarena)) {
                    if(event.getClickedBlock().equals(miniarena.getBlockAt(location)) && event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        if(player == null) {
                            this.player = event.getPlayer();
                            this.counter = 1;
                            player.sendMessage(ChatColor.GREEN + "You have " + delay + " seconds! Click as fast as you can!");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    double d = (double)counter/delay;
                                    player.sendMessage(ChatColor.GREEN + "You hit me " + counter + " times (" + d + " clicks/second)");
                                    player = null;
                                }
                            }.runTaskLater(main, 20 * delay);
                        } else if(event.getPlayer().equals(player)){
                            counter++;
                        } else if(!event.getPlayer().equals(player)) {
                            event.getPlayer().sendMessage(ChatColor.GREEN + " relax human! Wait until " + player.getDisplayName() + " has finished!");
                        }
                    }
                }
            }
        }
    }
}
