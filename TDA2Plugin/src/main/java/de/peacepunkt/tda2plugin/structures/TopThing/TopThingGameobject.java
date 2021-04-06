package de.peacepunkt.tda2plugin.structures.TopThing;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.persistence.PlayerStats;
import de.peacepunkt.tda2plugin.persistence.PlayerStatsDaoImpl;
import de.peacepunkt.tda2plugin.structures.AbstractGameobject;
import de.peacepunkt.tda2plugin.structures.AbstractStructure;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class TopThingGameobject extends AbstractGameobject {
    private Location seedLocation;
    private List<ArmorStand> stands;

    private int type;
    private String worldname;

    public TopThingGameobject(TopThing structure, World world, boolean noFunc, boolean draw) {
        super(structure, world, noFunc, draw);
    }

    private ArmorStand makeStand(String name, int i) {
        ArmorStand a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(0.5, -0.25*i + 2, 0.5), EntityType.ARMOR_STAND);
        a.setCustomName(name);
        a.setCustomNameVisible(true);
        a.setVisible(false);
        a.setGravity(false);
        a.setInvulnerable(true);
        return a;
    }

    @Override
    protected void setup(AbstractStructure s, World world, boolean noFunc, boolean draw) {
        TopThing structure = (TopThing) s;
        type = structure.type;
        worldname = structure.worldname;
        seedLocation = Vector.locationFromVector(world, structure.seed);
        stands = new ArrayList<>();

        if(draw)
            draw();
    }
    public void draw() {
        //only if we are in the correct world
        if(worldname.equals(seedLocation.getWorld().getName())) {
            if (type == 0) {
                stands = new ArrayList<ArmorStand>();
                String name = ChatColor.GREEN + "Top 10 Players and their" + ChatColor.BOLD + " insane " + ChatColor.RESET + ChatColor.GREEN + "KD:";
                stands.add(makeStand(name, 0));
                int count = 1;
                for (PlayerStats ps : new PlayerStatsDaoImpl().getTopList(0)) {
                    name = ChatColor.YELLOW + "" + count + " " + ChatColor.LIGHT_PURPLE + ps.getUsername() + ChatColor.WHITE + " | " + ps.getScore() + " | " + ps.getKd();
                    stands.add(makeStand(name, count));
                    count++;
                }
            } else {
                seedLocation.getBlock().setType(Material.NETHER_QUARTZ_ORE);
                ArmorStand a = (ArmorStand) seedLocation.getWorld().spawnEntity(seedLocation.clone().add(0.5, -0.25 + 1, 0.5), EntityType.ARMOR_STAND);
                a.setCustomName(ChatColor.RED + "sneak to test classes");
                a.setCustomNameVisible(true);
                a.setVisible(false);
                a.setGravity(false);
                a.setInvulnerable(true);
                stands = new ArrayList<ArmorStand>();
                stands.add(a);
            }
        }
    }

    public void hide() {
        for(ArmorStand a : stands)
            a.remove();
        if(type == 1)
            seedLocation.getBlock().setType(Material.AIR);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if(this.type == 1) {
            if (event.getPlayer().isSneaking() && event.getPlayer().getWorld().getName().contains("Spawn")) {
                if (seedLocation.getWorld().equals(event.getPlayer().getWorld())) {
                    if (seedLocation.distance(event.getPlayer().getLocation()) <= 1.3) {
                        if (!(event.getPlayer().getWorld().getName().equals("arena"))) {
                            if (!EditorHandler.getInstance().isPlayerInEditMode(event.getPlayer())) {
                                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 1, 1);
                                event.getPlayer().getWorld().spawnParticle(Particle.ASH, event.getPlayer().getLocation(), 100, 1, 2, 1);
                                event.getPlayer().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, event.getPlayer().getLocation(), 10, 1, 2, 1);
                                MainHolder.main.getMiniPvPHandler().addPlayer(event.getPlayer());
                            } else {
                                event.getPlayer().sendMessage(ChatColor.GREEN + "Do /editleave first!");
                            }
                        }
                    }
                }
            }
        }
    }
}
