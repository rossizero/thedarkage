package de.peacepunkt.tda2plugin.game.command;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.EditorNew;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.team.TeemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EditCommands {
    public EditCommands() {
        MainHolder.main.getCommand("edit").setTabCompleter(new TabCompleter() {
            final String[] COMMANDS = { "enter", "leave", "reload", "add_builder", "remove_builder", "status", "tools"};
            @Override
            public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
                final List<String> completions = new ArrayList<>();
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (EditorHandler.getInstance().isPlayerInEditMode(player)) {
                        if (strings.length == 1) {
                            StringUtil.copyPartialMatches(strings[0], Arrays.asList(COMMANDS), completions);
                            Collections.sort(completions);
                        } else {
                            if (strings[0].equals("add_builder")) {
                                for (Player p : Bukkit.getOnlinePlayers()) {
                                    if (strings[1] == null || strings[1].equals("") || p.getName().startsWith(strings[1])) {
                                        completions.add(p.getName());
                                    }
                                }
                            } else if (strings[0].equals("remove_builder")) {
                                if (commandSender instanceof Player) {
                                    List<String> names = getBuilders((Player) commandSender);
                                    if (names != null) {
                                        for (String name : names) {
                                            if (strings[1] == null || strings[1].equals("") || name.startsWith(strings[1])) {
                                                completions.add(name);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if(strings.length < 2)
                            StringUtil.copyPartialMatches(strings[0], Collections.singletonList("enter"), completions);
                    }
                }
                return completions;
            }
        });

        MainHolder.main.getCommand("edit").setDescription("Editors allow you to create your own playable maps! Ask an admin to get one yourself :) (only if you're really really motivated and gonna finish it)\n" +
                "It's also possible to upload existing maps to be used as an editor!");
        MainHolder.main.getCommand("edit").setUsage(ChatColor.GREEN +"enter <name> " + ChatColor.GRAY + "- to enter one of your editors.\n\n" +
                ChatColor.GREEN + "leave " + ChatColor.GRAY + "- to leave the editor and return to the game.\n\n" +
                ChatColor.GREEN + "reload " + ChatColor.GRAY + "- to reload the editor you're in (This will re-render flags and stuff).\n\n" +
                ChatColor.GREEN + "add_builder <player>" + ChatColor.GRAY + "- to add an builder to your current editor. You have to be its owner to do that.\n\n" +
                ChatColor.GREEN + "remove_builder <player>" + ChatColor.GRAY + "- to remove an builder from your current editor. You have to be its owner to do that.\n\n" +
                ChatColor.GREEN + "tools" + ChatColor.GRAY + "- to open an inventory with special blocks and usefull items\n\n" +
                ChatColor.GREEN + "status " + ChatColor.GRAY + "- to see some basic information about the current editor.\n\n");

        MainHolder.main.getCommand("edit").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (strings.length > 0) {
                        switch (strings[0]) {
                            default: //check if there's a map with that name
                                return true;
                            case "enter":
                                if(strings.length > 1) {
                                    return edit(player, strings[1]);
                                } else {
                                    return false;
                                }
                            case "leave":
                                leave(player);
                                return true;
                            case "reload":
                                return reload(player);
                            case "add_builder":
                                if(strings.length > 1) {
                                    addBuilder(player, strings[1]);
                                    return true;
                                } else {
                                    return false;
                                }
                            case "remove_builder":
                                if(strings.length > 1) {
                                    removeBuilder(player, strings[1]);
                                    return true;
                                } else {
                                    return false;
                                }
                            case "status":
                                status(player);
                                return true;
                            case "tools":
                                tools(player);
                                return true;
                        }
                    }
                    return false;
                }
                return false;
            }
        });

        MainHolder.main.getCommand("setOwner").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
                if(commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    if (strings.length > 0) {
                        String nameOfOther = strings[0];
                        Player other = Bukkit.getPlayer(nameOfOther);
                        World w = EditorHandler.getInstance().getPlayerEditWorld(player);
                        if(w != null) {
                            String settingsFile = Main.arenasPath + w.getName() + "/settings.yml";
                            YamlConfiguration config = new YamlConfiguration();
                            File ff = new File(settingsFile);

                            try {
                                config.load(ff);
                                config.addDefault("owner", other.getUniqueId().toString());
                                config.addDefault("helpers", new ArrayList<String>());
                                config.options().copyDefaults(true);
                                config.save(ff);
                                other.sendMessage(ChatColor.GREEN + "You're now owner of " + w.getName());
                                player.sendMessage(ChatColor.GREEN + "You made " + other.getName() + " owner of " + w.getName());
                            } catch (IOException | InvalidConfigurationException e) {
                                e.printStackTrace();
                            }
                        } else {
                            player.sendMessage("Not in edit mode or so");
                        }
                    }
                }
                return true;
            }
        });
    }

    private boolean edit(Player p, String worldName) {
        if(!p.getWorld().getName().equals("arena") && !MainHolder.main.isPlayerInMiniArena(p) && !EditorHandler.getInstance().isPlayerInEditMode(p)) {
            if (!worldName.contains("Spawn")) {
                boolean done = false;
                //just fetch arenas not all worlds
                File[] directories = Bukkit.getWorldContainer().listFiles(File::isDirectory);
                File worldFolder = new File(Bukkit.getWorldContainer() + "/" + worldName);
                for (File f : directories) {
                    if (f.getName().equals(worldName)) {
                        System.out.println(f.getName() + " " + worldName);
                        String settingsFile = Main.arenasPath + worldName + "/settings.yml";
                        YamlConfiguration config = new YamlConfiguration();
                        File ff = new File(settingsFile);
                        try {
                            boolean continu = false;
                            if (!p.isOp()) {
                                config.load(ff);
                                config.addDefault("owner", "1");
                                config.addDefault("helpers", new ArrayList<String>());
                                config.options().copyDefaults(true);
                                String uuidOwner = (String) config.getString("owner");
                                if (uuidOwner.equals(p.getUniqueId().toString())) {
                                    continu = true;
                                } else {
                                    if (config.getList("helpers") != null) {
                                        for (String help : (List<String>) config.getList("helpers")) {
                                            if (help.equals(p.getUniqueId().toString())) {
                                                continu = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                continu = true;
                            }
                            if (continu) {
                                World target = TeemUtils.getWorld(worldName);
                                EditorHandler.getInstance().addEditor(target, p);
                                done = true;
                                return true;
                            }
                        } catch (IOException | InvalidConfigurationException e) {
                            e.printStackTrace();
                            p.sendMessage("nope.");
                        }
                        break;
                    }
                }
                if (!done) {
                    p.sendMessage("World " + worldName + " does not exist.");
                    return false;
                }
            } else {
                if(p.isOp()) {
                    File[] directories = Bukkit.getWorldContainer().listFiles(File::isDirectory);
                    File worldFolder = new File(Bukkit.getWorldContainer() + "/" + worldName);
                    for (File f : directories) {
                        if (f.getName().equals(worldName)) {
                            World target = TeemUtils.getWorld(worldName);
                            EditorHandler.getInstance().addEditor(target, p);
                            break;
                        }
                    }
                }
            }
        } else {
            p.sendMessage(ChatColor.GREEN + "You can only enter an editor from your teams spawn!");
            return true;
        }
        return false;
    }
    private void addBuilder(Player player, String nameOfTarget) {
        Player other = Bukkit.getPlayer(nameOfTarget);
        World w = EditorHandler.getInstance().getPlayerEditWorld(player);
        if(w != null && other != null) {
            String settingsFile = Main.arenasPath + w.getName() + "/settings.yml";
            YamlConfiguration config = new YamlConfiguration();
            File ff = new File(settingsFile);

            try {
                config.load(ff);
                config.addDefault("owner", "1");
                config.addDefault("helpers", new ArrayList<String>());
                config.options().copyDefaults(true);
                String uuidOwner = (String) config.getString("owner");
                if (uuidOwner.equals(player.getUniqueId().toString()) || player.isOp()) {
                    List<String> uuids = (List<String>) config.getList("helpers");
                    if(uuids == null)
                        uuids = new ArrayList<>();
                    String newUuid = other.getUniqueId().toString();
                    if(!uuids.contains(newUuid)) {
                        uuids.add(newUuid);
                        other.sendMessage(ChatColor.GREEN +"You're now helper for map " + w.getName() + " with owner " +  Bukkit.getPlayer(UUID.fromString(uuidOwner)).getName());
                        player.sendMessage(ChatColor.GREEN +"You added " + other.getName() + " as helper for map " + w.getName());
                        config.set("helpers", uuids);
                        config.save(ff);
                    }
                }
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getBuilders(Player player) {
        World w = EditorHandler.getInstance().getPlayerEditWorld(player);
        if(w != null) {
            String settingsFile = Main.arenasPath + w.getName() + "/settings.yml";
            YamlConfiguration config = new YamlConfiguration();
            File ff = new File(settingsFile);
            try {
                List<String> ret = new ArrayList<>();
                config.load(ff);
                config.addDefault("helpers", new ArrayList<String>());
                List<String> uuids = (List<String>) config.getList("helpers");
                for(String s: uuids) {
                    ret.add(Bukkit.getOfflinePlayer(UUID.fromString(s)).getName());
                }
                return ret;
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void removeBuilder(Player player, String nameOfTarget) {
        World w = EditorHandler.getInstance().getPlayerEditWorld(player);
        if(w != null) {
            String settingsFile = Main.arenasPath + w.getName() + "/settings.yml";
            YamlConfiguration config = new YamlConfiguration();
            File ff = new File(settingsFile);

            try {
                config.load(ff);
                config.addDefault("owner", "1");
                config.addDefault("helpers", new ArrayList<String>());
                config.options().copyDefaults(true);
                String uuidOwner = (String) config.getString("owner");
                if (uuidOwner.equals(player.getUniqueId().toString()) || player.isOp()) {
                    List<String> uuids = (List<String>) config.getList("helpers");
                    for(String uuid : uuids) {
                        if(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName().equals(nameOfTarget)) {
                            uuids.remove(uuid);
                            if(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer() != null) {
                                EditorHandler.getInstance().leaveEditor(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer());
                                Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getPlayer().sendMessage(ChatColor.GREEN + "You're no longer helper for map " + w.getName() + " with owner " + Bukkit.getPlayer(UUID.fromString(uuidOwner)).getName());
                            }
                            player.sendMessage(ChatColor.GREEN +"You removed " + Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName() + " as helper for map " + w.getName());
                            config.set("helpers", uuids);
                            config.save(ff);
                            return;
                        }
                    }
                }
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private void status(Player player) {
        World w = EditorHandler.getInstance().getPlayerEditWorld(player);
        if (w != null) {
            String settingsFile = Main.arenasPath + w.getName() + "/settings.yml";
            YamlConfiguration config = new YamlConfiguration();
            File ff = new File(settingsFile);
            try {
                config.load(ff);
                player.sendMessage(ChatColor.DARK_PURPLE +"-");
                player.sendMessage(ChatColor.GREEN + "Status of map " + w.getName() +":");
                player.sendMessage(ChatColor.GREEN + "Staged: " + (config.get("staged") != null ? String.valueOf(config.getBoolean("staged")) : "we don't know"));
                String tmp = (String) config.getString("owner");
                String name = tmp != null ? Bukkit.getOfflinePlayer(UUID.fromString(tmp)).getName(): "no one";
                player.sendMessage(ChatColor.GREEN + "Owner: " + ChatColor.DARK_RED + name);
                player.sendMessage(ChatColor.GREEN + "Helpers: ");
                if (config.getList("helpers") != null) {
                    for (String help : (List<String>) config.getList("helpers")) {
                        player.sendMessage(ChatColor.GREEN + "    - " + Bukkit.getOfflinePlayer(UUID.fromString(help)).getName());
                    }
                }
                player.sendMessage(ChatColor.DARK_PURPLE +"-");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    private void tools(Player player) {
        if(EditorHandler.getInstance().isPlayerInEditMode(player)) {
            Inventory inventory = Bukkit.createInventory(player, 18, ChatColor.GREEN+"Tools 'n stuff");
            inventory.addItem(new ItemStack(Material.DEBUG_STICK));
            inventory.addItem(new ItemStack(Material.BARRIER));
            player.openInventory(inventory);
        }
    }

    private void leave(Player player) {
        EditorHandler.getInstance().leaveEditor(player);
    }

    private boolean reload(Player player) {
        EditorNew editor = EditorHandler.getInstance().getEditor(player.getWorld());
        if (editor != null) {
            editor.saveAll();
            editor.reload();
            return true;
        }
        return false;
    }
}
