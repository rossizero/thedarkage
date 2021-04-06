package de.peacepunkt.tda2plugin.structures.flag;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.RoundHandlerNew;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import de.peacepunkt.tda2plugin.structures.Vector;
import de.peacepunkt.tda2plugin.structures.flag.FlagPresets.FlagTypeInterface;
import de.peacepunkt.tda2plugin.team.Teem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlagGameobject extends AbstractGameobject {
    private int status;
    private Teem current;
    private BukkitScheduler scheduler;
    private boolean beeingCaptured = false;
    private int taskId;
    private Location seedLocation;
    private Location cappingLocation;
    private double cappingRadius;
    private double cappingHeight;
    private Location spawnLocation;

    private String name;
    private int direction;
    private int type; //0: flag, 1: banner, 2 : 3x3 flat
    private boolean conquerable;
    private boolean flyOnSpawn;
    private boolean birds;
    private List<FlagSpawnBlock> connections;
    private List<FlagCustomBlock> customBlocks;
    private boolean noFunc;

    private FlagTypeInterface flagType;

    public FlagGameobject(Flag structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw);
    }

    @Override
    protected void setup(AbstractStructure s, World world, boolean noFunc, boolean draw) {
        Flag structure = (Flag) s;
        this.name = structure.name;
        this.seedLocation = Vector.locationFromVector(world, structure.seed);
        this.spawnLocation = Vector.locationFromVector(world, structure.spawn);
        this.cappingLocation = (structure.cappingZone != null) ? Vector.locationFromVector(world, structure.cappingZone) : this.seedLocation;
        this.cappingRadius = structure.cappingRadius;
        this.cappingHeight = structure.cappingHeight;

        spawnLocation.setYaw(StructureUtils.getYaw(structure.spawnRotation));

        this.direction = structure.direction;
        this.current = null; //TODO
        this.type = structure.type;
        this.birds = structure.birds;
        this.flyOnSpawn = structure.flyOnSpawn;

        this.noFunc = noFunc;

        this.conquerable = structure.conquerable;
        this.connections = structure.connections;
        this.customBlocks = structure.customBlocks;

        //check what team owns the flag
        for(Teem team : MainHolder.main.getRoundHandler().getRound().getTeams()) {
            if(team.getId() == structure.teamId) {
                this.current = team;
                break;
            }
        }

        if(this.current == null) {
            this.status = 0; //anfangs nicht erobert
        } else {
            this.status = Flag.max; //anfangs erobert
        }

        flagType = FlagTypeInterface.getFlagTypeById(type, this);

        if(!noFunc) {
            registerSpawnBlocks();

            if(conquerable) {
                scheduler = MainHolder.main.getServer().getScheduler();
                taskId = scheduler.scheduleSyncRepeatingTask(MainHolder.main, new Runnable() {
                    @Override
                    public void run() {
                        timerCallback();
                    }
                }, 0L, Main.flagCaptureSpeed);
            }
        }

        if(draw)
            drawOnce();
    }
    private void timerCallback() {
        RoundHandlerNew rh = MainHolder.main.getRoundHandler();
        beeingCaptured = false;
        if(rh.getRound() != null) {
            List<Player> membersOfBiggestTeam = getCapturingPlayers(rh);
            if(membersOfBiggestTeam == null) {
                //tie
            } else {
                Teem tmp = rh.getRound().getTeam(membersOfBiggestTeam.get(0));
                if(current != null) { //if flag was not neutralized
                    if(tmp.equals(current)) { //if most players are in same team as current
                        if(status < Flag.max) {
                            status++;
                            if(status == Flag.max) {
                                fullycapturedMsg(membersOfBiggestTeam);
                            } else {
                                captureMsg(membersOfBiggestTeam);
                            }
                        }
                    } else {
                        beeingCaptured = true;
                        if(status == 1) {
                            current = null;
                            status = 0;
                            onNeutralized();
                            captureMsg(membersOfBiggestTeam);
                        } else {
                            status--;
                            captureMsg(membersOfBiggestTeam);
                        }
                    }
                } else { //if flag had been neutral
                    status++;
                    current = tmp;
                    newCaptureMsg(tmp, membersOfBiggestTeam);
                }
            }

        }
    }
    private void check() {
        RoundHandlerNew rh = MainHolder.main.getRoundHandler();
        beeingCaptured = false;
        if(rh.getRound() != null) {
            List<Player> membersOfBiggestTeam = getCapturingPlayers(rh);
            if(membersOfBiggestTeam != null) {
                Teem tmp = rh.getRound().getTeam(membersOfBiggestTeam.get(0));
                if (current != null) { //if flag was not neutralized
                    if (!tmp.equals(current)) { //if most players are in same team as current
                        beeingCaptured = true;
                    }
                }
            }
        }
    }
    public Location getSeedLocation() {
        return seedLocation.clone();
    }
    public Location getCappingLocation() {return cappingLocation.clone();}
    public int getDirection() {
        return direction;
    }

    public void draw() {
        flagType.draw(status);
    }
    public List<FlagCustomBlock> getCustomBlocks() {
        return customBlocks;
    }
    private void newCaptureMsg(Teem cap, List<Player> players) {
        String mes = cap.getChatColor() + "The " + cap.getName() + " have captured the " + name+"!";
        Bukkit.broadcastMessage(mes);
        captureMsg(players);
        MainHolder.main.getRoundHandler().getRound().redoScoreboard();
    }

    private void fullycapturedMsg(List<Player> players) {
        for(Player p : players)  {
            MainHolder.main.getRoundHandler().getRound().getTeam(p).getRoundStats().addCapture(p);
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 4, 1);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.YELLOW + "fully captured"));
        }
        draw();
        drawConnections();
    }

    private void captureMsg(List<Player> players) {
        for(Player p : players)  {
            MainHolder.main.getRoundHandler().getRound().getTeam(p).getRoundStats().addCapture(p);
            p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 4, 0);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.LIGHT_PURPLE + "+1 capping point ("+status +"/"+Flag.max+")"));
        }
        draw();
        drawConnections();
    }

    public void onNeutralized() {
        String mes = ChatColor.GRAY+ "The " + name + " has been neutralized!";
        Bukkit.broadcastMessage(mes);
        MainHolder.main.getRoundHandler().getRound().redoScoreboard();
        draw();
        drawConnections();
    }


    public void unregister() {
        super.unregister();
        for(FlagSpawnBlock block : connections) {
            HandlerList.unregisterAll(block);
        }
        cancelTasks();
    }

    private void cancelTasks() {
        if(taskId != -1 && scheduler != null) {
            scheduler.cancelTask(taskId);
        }
    }
    private void registerSpawnBlocks() {
        for(FlagSpawnBlock block : connections) {
            for(Teem t: MainHolder.main.getRoundHandler().getRound().getTeams()) {
                if(t.getId() == block.teamId) {
                    block.setWorld(t.getSpawnWorld());
                    break;
                }
            }
            MainHolder.main.getServer().getPluginManager().registerEvents(block, MainHolder.main);
        }
        drawConnections();
    }
    private void drawOnce() {
        draw();
        drawConnections();
    }

    public void drawConnections() {
        if(connections != null) {
            for(FlagSpawnBlock fsb: connections) {
                fsb.setTargetFlag(this);
                fsb.draw();
            }
        }
    }

    private boolean inReach(Location target) {
        Location origin = cappingLocation.clone();
        Location upper = cappingLocation.clone().add(0, this.cappingHeight, 0);
        Location lower = cappingLocation.clone().add(0, -1, 0);
        if(cappingLocation.getWorld().equals(target.getWorld())) {
            for (int i = 0; i < Flag.max; i++) {
                origin.add(0, 1, 0);
                Location radiusLocation = target.clone();
                radiusLocation.setY(0);
                Location radiusLocation2 = origin.clone();
                radiusLocation2.setY(0);
                if(radiusLocation.distance(radiusLocation2) <= cappingRadius && (target.getY() >= lower.getY() && target.getY() <= upper.getY())) {
                    return true;
                }
            }
        }
        return false;
    }
    public List<FlagSpawnBlock> getConnections() {
        return connections;
    }
    private ArrayList<Player> getClosePlayers() {
        ArrayList<Player> playersClose = new ArrayList<>();
        for(Player p: Bukkit.getOnlinePlayers()) {
            if(inReach(p.getLocation())) {
                playersClose.add(p);
            }
        }
        return playersClose;
    }

    public Teem getCapturingTeam() {
        List<Player> most = getCapturingPlayers(MainHolder.main.getRoundHandler());
        if(most != null && most.size() > 0) {
            return MainHolder.main.getRoundHandler().getRound().getTeam(most.get(0));
        }
        return null;
    }

    private List<Player> getCapturingPlayers(RoundHandlerNew rh) {
        List<Player> ret = new ArrayList<>();
        Map<Teem, Integer> count = new HashMap<>();
        List<Player> closePlayers = getClosePlayers();
        //count all players and sort them by their team
        for(Player p: closePlayers) {
            for (Teem t : rh.getRound().getTeams()) {
                if (t.contains(p)) {
                    Integer old = count.get(t);
                    if (old == null) {
                        old = 0;
                    }
                    count.put(t, old + 1);
                }
            }
        }
        int maxCount = -1;
        Teem most = null;
        for(Teem t: rh.getRound().getTeams()) {
            if(count.get(t) != null) { //no member of team t there
                if(count.get(t) == maxCount) {
                    most = null; //there are two max teams
                }
                if(count.get(t) > maxCount) { //team t has more members around
                    maxCount = count.get(t);
                    most = t;
                }
            }
        }
        if(most != null) {
            for (Player p : closePlayers) {
                if (most.contains(p)) {
                    ret.add(p);
                }
            }
            return ret;
        }
        return null;
    }

    void spawnAtFlag(Player p) {
        if(current != null) {
            if(current.contains(p)) {
                check();
                if(!beeingCaptured) {
                    if (flyOnSpawn) {
                        p.getInventory().setChestplate(new ItemStack(Material.ELYTRA, 1));
                        p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "*Equipped with disposable wings!*");
                    }
                    p.teleport(spawnLocation);
                } else {
                    p.sendMessage("This flag is under attack. You can't spawn there right now.");
                }
            } else {
                p.sendMessage("You can't spawn at enemy or neutral flags!");
            }
        } else {
            p.sendMessage("You can't spawn at enemy or neutral flags!");
        }
    }

    public String getName() {
        return name;
    }

    int getStatus() {
        return status;
    }

    public Teem getCurrent() {
        return current;
    }

    public boolean isNoFunc() {
        return noFunc;
    }
}
