package de.peacepunkt.tda2plugin.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.game.RoundNew;
import de.peacepunkt.tda2plugin.kits.gui.ExplainBook;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KitHandler {
    private static KitHandler instance;
    private RoundNew round;

    //sadly we need an instance of each subclass to process their events correctly
    private Map<Class<? extends AbstractKitSuperclass>, AbstractKitSuperclass> subclasses;

    private KitHandler() {
        subclasses = new HashMap<>();
        init();
    }

    public static KitHandler getInstance() {
        if(KitHandler.instance == null) {
            instance = new KitHandler();
        }
        return instance;
    }

    //sets ref to main and looks for subclasses of AbstractKitSuperclass
    private void init() {
        Reflections reflections = new Reflections("de.peacepunkt");

        Set<Class<? extends AbstractKitSuperclass>> kits = reflections.getSubTypesOf(AbstractKitSuperclass.class);
        for(Class<? extends AbstractKitSuperclass> kit : kits) {
            try {
                AbstractKitSuperclass k = (AbstractKitSuperclass) kit.getConstructors()[0].newInstance() ;
                register(k);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public Map<Class<? extends AbstractKitSuperclass>, AbstractKitSuperclass> getKitSubclassesMap() {
        return subclasses;
    }

    public void setRound(RoundNew round) {
        this.round = round;
    }

    private void register(AbstractKitSuperclass instance) {
        if(!subclasses.containsKey(instance.getClass())) {
            MainHolder.main.getServer().getPluginManager().registerEvents(instance,  MainHolder.main);
            subclasses.put(instance.getClass(), instance);
            System.out.println("registered " + instance.getClass());
        }
    }

    public AbstractKitSuperclass getKitByName(String name) {
        for(Class<? extends AbstractKitSuperclass> clazz : subclasses.keySet()) {
            if(subclasses.get(clazz).getKitDescription().getName().equals(name))
                return subclasses.get(clazz);
        }
        return null;
    }

    public void setKitOnNewRound(Player player, String mapDefaultKit, boolean checkIfUnlocked, boolean heal) {
        AbstractKitSuperclass kit = PlayerHandler.getInstance().getKit(player);
        boolean msg = false;
        if(kit == null || kit.isMapSpecificKit() || getKitByName(mapDefaultKit).isMapSpecificKit()) {
            kit = getKitByName(mapDefaultKit);
            msg = true;
        } else {
        }
        setKit(player, kit.getKitDescription().getName(), checkIfUnlocked, heal, msg);
    }
    public void restockKit(Player player, boolean food, boolean checkIfUnlocked, boolean heal) {
        AbstractKitSuperclass kit = PlayerHandler.getInstance().getKit(player);
        int a = player.getFoodLevel();
        if(kit != null) { //TODO circle? If player doesn't own class he previously had (test arena)
            setKit(player, kit.getKitDescription().getName(), checkIfUnlocked, heal, false);
        } else {
            setKit(player, round.getTeam(player).getDefaultKit(), false, heal, false);
        }

        if(food)
            player.setFoodLevel(a);
    }

    public void setKit(Player player, String kitName, boolean checkIfUnlocked, boolean heal, boolean msg) {
        System.out.println("setting kit of player " + player.getName() + " " + kitName);
        //check if players owns that kit already this is a test
        AbstractKitSuperclass kit = getKitByName(kitName);
        if(kit == null)
            return;

        AbstractKitSuperclassDaoImpl asdo = new AbstractKitSuperclassDaoImpl<>(kit.getClass());
        //System.out.println(asdo);
        ItemStack[] equipment;
        if (checkIfUnlocked) {
            if (asdo.hasKit(player)) {
                player.setFoodLevel(20);
                player.setFireTicks(0);
                equipment = getKitByName(kitName).setKit(player);
            } else {
                player.sendMessage("You did not unlock " + kit.getKitDescription().getName() + " yet. Do /buy to unlock it!");
                kit = PlayerHandler.getInstance().getKit(player);
                if (kit != null) //TODO circle? If player doesn't own class he previously had (test arena)
                    setKit(player, kit.getKitDescription().getName(), true, heal, false);
                else
                    setKit(player, round.getTeam(player).getDefaultKit(), false, heal, false);
                return;
            }
        } else {
            player.setFoodLevel(20);
            player.setFireTicks(0);
            equipment = getKitByName(kitName).setKit(player);
        }


        //ExtraHandler more hearts
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (ExtraHandler.getExtraHeart(player)) {
            healthAttribute.setBaseValue(22f);
            if(heal) {
                player.setHealth(22);
            }
        } else {
            healthAttribute.setBaseValue(20f);
            if(heal) {
                player.setHealth(20);
            }
        }

        //ExtraHandler displayName
        if (ExtraHandler.getExtraDisplayName(player)) {
            player.setDisplayName(round.getTeam(player).getChatColor() +""+ ChatColor.BOLD  + player.getName() + ChatColor.RESET);
        }

        //set Inventory items
        if(equipment != null) {
            Material head = round.getTeam(player).getTeamMaterial();
            ItemStack rest[] = new ItemStack[equipment.length - 3];
            for (int j = 3; j < equipment.length; j++) {
                rest[j - 3] = equipment[j];
            }
            player.getInventory().setContents(rest);
            player.getInventory().setHelmet(new ItemStack(head));
            player.getInventory().setChestplate(equipment[0]);
            player.getInventory().setLeggings(equipment[1]);
            player.getInventory().setBoots(equipment[2]);
            player.getInventory().addItem(new ExplainBook().getBook(player, round.getTeam(player)));
            player.updateInventory();
        }

        if(msg)
            player.sendMessage(ChatColor.GREEN + "Switched to " + kit.getKitDescription().getName());
    }


    @EventHandler
    public void onEntityToggleGlideEvent(EntityToggleGlideEvent event) {
        if(!event.isGliding()) {
            restockKit((Player) event.getEntity(), true, false, false);
            ((Player) event.getEntity()).sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "*Your Disposable wings are gone now!*");
        }
    }

    /*@EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }*/
}
