package de.peacepunkt.tda2plugin.structures.Cannon;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public class CannonCommands implements Listener {
    Map<Player, Cannon> locked;
    Main main;

    public CannonCommands(Main main) {
        this.main = main;
        locked = new HashMap<>();
        main.getCommand("setCannon").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    String worldname = ((Player) commandSender).getWorld().getName();
                    Player player = (Player) commandSender;
                    if(EditorHandler.getInstance().isPlayerInEditMode(player)) {
                        Cannon c = new Cannon();
                        locked.put(player, c);
                        player.sendMessage("Click a button to finish cannon configuration");
                        return true;
                    }
                }
                return false;
            }
        });

        main.getCommand("removeCannon").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player p = (Player) commandSender;
                    EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
                    if(e != null) //if not an editable world but arena or so
                        e.removeClosest(Cannon.class, p);
                }
                return true;
            }
        });
    }
    @EventHandler
    public void onPlayerSetBlock(BlockPlaceEvent event) {
        if (event.getBlock().getType().equals(Material.STONE_BUTTON)) {
            if (locked.containsKey(event.getPlayer())) {
                finishCannon(event.getPlayer(), event.getPlayer().getLocation());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(event.getClickedBlock().getType().equals(Material.STONE_BUTTON)) {
                if (locked.containsKey(event.getPlayer())) {
                    finishCannon(event.getPlayer(), event.getClickedBlock().getLocation());
                }
            }
        }
    }

    private void finishCannon(Player player, Location location) {
        Cannon cannon = locked.get(player);
        if(cannon != null) {
            cannon.seed = Vector.vectorFromLocation(location);
            EditorHandler.getInstance().getEditor(player.getWorld()).addStructure(cannon);
            player.sendMessage("Cannon created!");
            locked.remove(player);
        }
    }
}

