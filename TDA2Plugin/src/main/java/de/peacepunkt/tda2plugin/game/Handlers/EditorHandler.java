package de.peacepunkt.tda2plugin.game.Handlers;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class EditorHandler {
    private static EditorHandler instance;
    private List<EditorNew> editors;
    private Map<UUID, PermissionAttachment> permissions;

    private EditorHandler() {
        editors = new ArrayList<>();
        permissions = new HashMap<>();
    }

    public static EditorHandler getInstance() {
        if(EditorHandler.instance == null) {
            instance = new EditorHandler();
        }
        return instance;
    }

    public World getPlayerEditWorld(Player player) {
        for(EditorNew e : editors) {
            if(e.getWorld().getPlayers().contains(player)) {
                return e.getWorld();
            }
        }
        return null;
    }

    public boolean isPlayerInEditMode(Player player) {
        return getPlayerEditWorld(player) != null;
    }

    public List<Player> getAllPlayersInEditor() {
        List<Player> ret =  new ArrayList<>();
        for(EditorNew e: editors) {
            ret.addAll(e.getWorld().getPlayers());
        }
        return ret;
    }

    public EditorNew getEditor(World world) {
        for(EditorNew e: editors) {
            if(e.getWorld().equals(world)) {
                return e;
            }
        }
        return null;
    }
    public void reloadEditor(String worldName) {
        for(EditorNew e: editors) {
            if(e.getWorld().getName().equals(worldName)) {
                e.reload();
                break;
            }
        }
    }
    public void addEditor(World world, Player player) {
        if(getPlayerEditWorld(player) == null) {
            boolean found = false;
            for (EditorNew e : editors) {
                if (e.getWorld().equals(world)) {
                    found = true;
                    e.addPlayer(player);
                    addBuilder(player);
                    break;
                }
            }
            if (!found) {
                editors.add(new EditorNew(world, player));
                addBuilder(player);
            }
        } else {
            player.sendMessage(ChatColor.GREEN + "Please do /editleave first");
        }
    }

    public void leaveEditor(Player player) {
        for(EditorNew e: editors) {
            if(e.getWorld().equals(player.getWorld())) {
                if(e.leave(player, false)) {
                    editors.remove(e);
                }
                removeBuilder(player);
                KitHandler.getInstance().setKitOnNewRound(player, MainHolder.main.getRoundHandler().getRound().getTeam(player).defaultKit, false, true);
                //KitHandler.getInstance().restockKit(player, false, false,true);
                break;
            }
        }
    }

    public void addBuilder(Player p) {
        UUID id = p.getUniqueId();
        if(p != null) {
            PermissionAttachment a = p.addAttachment(MainHolder.main, "builder", true);
            p.sendMessage(ChatColor.GREEN + "You're now a builder");
            permissions.put(id, a);
            p.updateCommands();
        } else {
            //TODO neu vlt dumm
            removeBuilder(p);
            p.updateCommands();
        }
    }

    public void removeBuilder(Player p) {
        UUID id = p.getUniqueId();
        if(p != null) {
            p.sendMessage(ChatColor.GREEN + "You're no longer a builder");
            p.setGameMode(GameMode.SURVIVAL);
        }
        if(permissions.containsKey(id)) {
            p.removeAttachment(permissions.get(id)); //throws error if p doesnt have that attachment
            permissions.remove(id);
            p.updateCommands();
        }
    }

    public void removeAllBuilders() {
        for(Player player: getAllPlayersInEditor()) {
            removeBuilder(player);
        }
    }
}
