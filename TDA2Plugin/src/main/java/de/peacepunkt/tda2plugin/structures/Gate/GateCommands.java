package de.peacepunkt.tda2plugin.structures.Gate;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.structures.StructureUtils;
import de.peacepunkt.tda2plugin.structures.Vector;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GateCommands implements Listener {
    private Map<Player, Gate> locked;

    public GateCommands(Main main) {
        locked = new HashMap<>();
        main.getCommand("setGate").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    //int width, int height, boolean hideCompletely, boolean isDrawbridge, Material material
                    if(strings.length == 5) {
                        try {
                            Player player = (Player) commandSender;
                            int width = Integer.parseInt(strings[0]);
                            int height = Integer.parseInt(strings[1]);
                            boolean hideCompletely = strings[2].equals("true");
                            boolean isDrawbridge = strings[3].equals("true");
                            Material material = Material.matchMaterial(strings[4]);
                            player.sendMessage("Using default Material: iron_bars");

                            Gate gate = new Gate();
                            gate.seed = Vector.vectorFromLocation(player.getLocation());
                            gate.dir = StructureUtils.getDir(player);
                            gate.width = width;
                            gate.height = height;
                            gate.hideCompletely = hideCompletely;
                            gate.drawBridge = isDrawbridge;
                            gate.material = material;
                            player.sendMessage(ChatColor.RED + "The next lever you place will be linked with this gate!");
                            player.setItemOnCursor(new ItemStack(Material.LEVER, 1));
                            player.updateInventory();
                            locked.put(player, gate);
                            return true;
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                }
                return false;
            }
        });

        main.getCommand("removeGate").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player p = (Player) commandSender;
                    EditorNew e = EditorHandler.getInstance().getEditor(p.getWorld());
                    if(e != null) {
                        e.removeClosest(Gate.class, p);
                    }
                }
                return true;
            }
        });
    }

    @EventHandler
    public void onPlayerSetBlock(BlockPlaceEvent event) {
        if(event.getBlock().getType().equals(Material.LEVER)) {
            if(locked.containsKey(event.getPlayer())) {
                Player player = event.getPlayer();
                Gate gate = locked.get(player);
                gate.lever = Vector.vectorFromLocation(event.getBlock().getLocation());
                locked.remove(event.getPlayer());
                EditorNew e = EditorHandler.getInstance().getEditor(player.getWorld());
                if (e != null) {
                    e.addStructure(gate);
                }
                player.sendMessage("Gate created.");
                player.setItemOnCursor(null);
                player.updateInventory();
            }
        }
    }
}
