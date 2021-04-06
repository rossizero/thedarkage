package de.peacepunkt.tda2plugin.game;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.StructureHandler;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThing;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThingGameobject;
import de.peacepunkt.tda2plugin.structures.flag.Flag;
import de.peacepunkt.tda2plugin.structures.flag.FlagGameobject;
import de.peacepunkt.tda2plugin.structures.flag.FlagSpawnBlock;
import de.peacepunkt.tda2plugin.team.TeamHandler;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditorNew {
    World template;

    List<AbstractGameobject> objects;
    List<Teem> teams;
    List<Player> players;

    boolean isSpawnWorld = false;
    String path = null;

    /**
     * To edit arenas and their spawnworlds
     * @param template
     * @param player
     */
    public EditorNew(World template, Player player) {
        this.template = template;

        this.players = new ArrayList<Player>();
        this.players.add(player);
        Location loc = new Location(template, template.getSpawnLocation().getX(), template.getSpawnLocation().getY(), template.getSpawnLocation().getZ());
        player.teleport(loc);
        player.setGameMode(GameMode.CREATIVE);

        load();
    }

    private void load() {
        objects = new ArrayList<>();
        System.out.println("(re)loading editor for world " + template.getName());
        List<Class<? extends AbstractStructure>> subclasses = AbstractStructure.getAllSubclasses();
        if(getArenas() != null && getArenas().contains(template.getName())) { //is arena?
            isSpawnWorld = false;
            path = Main.arenasPath + template.getName();
            loadWorldSettings();
        } else if(template.getName().contains("Spawn")) {
            isSpawnWorld = true;
            path =  Main.arenasPath + template.getName().split("Spawn")[0];
        }

        //load all structures
        if(path != null) {
            //TODO remove eventually
            path += "/new/";
            for (Class clazz : subclasses) {
                StructureHandler structureHandler = new StructureHandler<>(clazz, path);
                List<AbstractStructure> structures = (List<AbstractStructure>)structureHandler.loadAll(clazz);
                if (structures != null) {
                    for(AbstractStructure structure: structures) {
                        objects.add(structure.getGameobject(structure, template, true, !isSpawnWorld));//new AbstractGameobject<>(structure, template, true));
                    }
                    System.out.println("Found " + structures.size() + " " + structureHandler.name + "'s");
                } else {
                    System.out.println("Found no " + structureHandler.name + "'s");
                }
            }
        }
        if(isSpawnWorld) {
            for(AbstractGameobject t: getAll(TopThing.class)) {
                TopThingGameobject topThing = (TopThingGameobject) t;
                topThing.draw();
            }
            for(AbstractGameobject t: getAll(Flag.class)) {
                FlagGameobject flag = (FlagGameobject) t;
                try {
                    int i = Integer.parseInt(template.getName().split("Spawn")[1]);
                    for(FlagSpawnBlock block: flag.getConnections()) {
                        if(block.teamId == i)
                            block.setWorld(template); //sets seedLocation -> can be drawn now
                    }
                    flag.drawConnections();
                } catch (NumberFormatException e) {
                    for(Player player: players) {
                        player.sendMessage("unable to load flag spawn blocks into this world");
                    }
                }
            }
        }
        TeamHandler teamHandler = new TeamHandler(path);
        teams = teamHandler.loadAll();
    }

    public List<? extends AbstractGameobject> getAll(Class<? extends AbstractStructure> subclass) {
        List<AbstractGameobject> ret = new ArrayList<>();
        for(AbstractGameobject object : objects) {
            if(object.getStructure().getClass().equals(subclass))
                ret.add(object);
        }
        return ret;
    }

    public AbstractGameobject getClosest(Class<? extends AbstractStructure> subclass, Player player) {
        AbstractGameobject closest = null;
        for(AbstractGameobject object : getAll(subclass)) {
            if(closest == null || player.getLocation().distance(object.getLocation()) < player.getLocation().distance(closest.getLocation())) {
                closest = object;
            }
        }
        if(closest.getLocation().distance(player.getLocation()) < 10.0) {
            return closest;
        }
        return null;
    }

    public boolean removeClosest(Class<? extends AbstractStructure> subclass, Player player) {
        AbstractGameobject toRemove = getClosest(subclass, player);
        if(toRemove != null) {
            objects.remove(toRemove);
            player.sendMessage("Removed closest " + subclass.getName());
            saveAll();
            load();
            return true;
        } else {
            player.sendMessage("No " + subclass.getName() + " close enough!");
            return false;
        }
    }

    public void addStructure(AbstractStructure structure) {
        objects.add(structure.getGameobject(structure, template, true, true));
        saveAll();
        load();
    }

    public void saveAll() {
        List<Class<? extends AbstractStructure>> subclasses = AbstractStructure.getAllSubclasses();
        if(path != null) {
            for (Class clazz : subclasses) {
                List<AbstractGameobject> subs = getAll(clazz);
                List<AbstractStructure> str = new ArrayList<>();
                for(AbstractGameobject sub: subs) {
                    str.add(sub.getStructure());
                }
                StructureHandler structureHandler = new StructureHandler<>(clazz, path);
                structureHandler.saveAll(str);
            }
        }
    }

    private List<String> getArenas() {
        YamlConfiguration config = new YamlConfiguration();
        File f = new File(Main.arenasPath+"/arenas.yml");

        try {
            config.load(f);
            List<String> ret = (List<String>) config.getList("arenas");
            return ret;
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
        return null;
    }


    public void removeNameTags() {
        for(Entity e: template.getEntities()) {
            if(e.getType().equals(EntityType.ARMOR_STAND)){
                //TODO check if invisble?
                e.remove();
            }
        }
    }


    private void loadWorldSettings() {
        String settingsFile = Main.arenasPath + template.getName() + "/settings.yml";
        try {
            YamlConfiguration config = new YamlConfiguration();
            File f = new File(settingsFile);
            config.load(f);
            config.addDefault("timeOfDay", "1000");
            config.addDefault("storm", "false");
            config.addDefault("thunder", "false");
            config.options().copyDefaults(true);
            template.setThundering(config.get("thunder").equals("true"));
            template.setStorm(config.get("storm").equals("true"));
            template.setTime(Long.parseLong((String) config.get("timeOfDay")));
        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private void saveWorldSettings() {
        String settingsFile = Main.arenasPath + template.getName() + "/settings.yml";
        try {
            YamlConfiguration config = new YamlConfiguration();
            File f = new File(settingsFile);
            config.load(f);
            config.set("timeOfDay", String.valueOf(template.getTime()));
            config.set("storm", String.valueOf(template.hasStorm()));
            config.set("thunder", String.valueOf(template.isThundering()));
            config.save(f);

        } catch (InvalidConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    public World getWorld() {
        return template;
    }

    public void reload() {
        load();
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        Location loc = new Location(template, template.getSpawnLocation().getX(), template.getSpawnLocation().getY(), template.getSpawnLocation().getZ());
        player.teleport(loc);
        player.setGameMode(GameMode.CREATIVE);
    }

    /**
     * returns true if editor was closed
     * @param player
     * @param force
     * @return
     */
    public boolean leave(Player player, boolean force) {
        if(force) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getWorld().equals(template)) {
                    p.setGameMode(GameMode.SURVIVAL);
                    p.teleport(MainHolder.main.getRoundHandler().getRound().getTeam(player).spawnLocation());
                }
            }
            if (!isSpawnWorld)
                saveWorldSettings();
            Bukkit.unloadWorld(template, true);
            System.out.println("Now closing editor of " + template.getName());
            return true;
        } else {
            if (players.contains(player)) {
                players.remove(player);
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(MainHolder.main.getRoundHandler().getRound().getTeam(player).spawnLocation());
            }
            if (players.size() == 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getWorld().equals(template)) {
                        p.setGameMode(GameMode.SURVIVAL);
                        p.teleport(MainHolder.main.getRoundHandler().getRound().getTeam(player).spawnLocation());
                    }
                }
                if (!isSpawnWorld)
                    saveWorldSettings();
                Bukkit.unloadWorld(template, true);
                System.out.println("Now closing editor of " + template.getName());
                return true;
            }
            return false;
        }
    }

    public List<Teem> getTeams() {
        return teams;
    }

    public boolean isSpawnWorld() {
        return isSpawnWorld;
    }
}
