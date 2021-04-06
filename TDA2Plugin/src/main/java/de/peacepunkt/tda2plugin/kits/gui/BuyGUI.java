package de.peacepunkt.tda2plugin.kits.gui;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclassDaoImpl;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.kits.kits.*;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class BuyGUI implements Listener {
    private Inventory inv;
    Main main;
    Map<String, AbstractKitSuperclass> kitNameMap;

    public BuyGUI(Main main) {
        this.main = main;
        kitNameMap = new HashMap<>();
    }

    private ItemStack makeItemStack(AbstractKitSuperclass a, boolean unlocked) {
        String locked = ChatColor.DARK_RED + "locked";
        String _unlocked = ChatColor.GREEN + "unlocked";

        ItemStack item = a.getKitDescription().getInventoryMaterial();
        kitNameMap.put(a.getKitDescription().getName(), a);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(a.getKitDescription().getName());
        List<String> list = new ArrayList<>();
        if(a.getKitDescription().getDescription() != null) {
            if (a.getKitDescription().getDescription().length != 0) {
                list = new ArrayList<String>(Arrays.asList(a.getKitDescription().getDescription()));
            }
        }
        if(unlocked) {
            list.add(" ");
            list.add(_unlocked);
        } else {
            list.add(" ");
            list.add(locked + ChatColor.GREEN + " costs: " + ChatColor.DARK_RED + a.getKitDescription().getPrice() + ChatColor.GREEN + " xp");
        }
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }
    private void getKitsOfPlayer(Player player) {
        Map<Class<? extends AbstractKitSuperclass>, AbstractKitSuperclass> map = KitHandler.getInstance().getKitSubclassesMap();
        for(Class<? extends AbstractKitSuperclass> kit : KitHandler.getInstance().getKitSubclassesMap().keySet()) {
            AbstractKitSuperclassDaoImpl asdo = new AbstractKitSuperclassDaoImpl<>(kit);
            inv.addItem(makeItemStack(map.get(kit), asdo.hasKit(player)));
        }

        /*AbstractKitSuperclassDaoImpl asdo = new AbstractKitSuperclassDaoImpl<KitPirate>(KitPirate.class);
        inv.addItem(makeItemStack(new KitPirate(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitArcher>(KitArcher.class);
        inv.addItem(makeItemStack(new KitArcher(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitChaos>(KitChaos.class);
        inv.addItem(makeItemStack(new KitChaos(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitExecutioner>(KitExecutioner.class);
        inv.addItem(makeItemStack(new KitExecutioner(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitHealer>(KitHealer.class);
        inv.addItem(makeItemStack(new KitHealer(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitScout>(KitScout.class);
        inv.addItem(makeItemStack(new KitScout(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSense>(KitSense.class);
        inv.addItem(makeItemStack(new KitSense(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSpearman>(KitSpearman.class);
        inv.addItem(makeItemStack(new KitSpearman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSwordsman>(KitSwordsman.class);
        inv.addItem(makeItemStack(new KitSwordsman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitTrapper>(KitTrapper.class);
        inv.addItem(makeItemStack(new KitTrapper(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitWaterman>(KitWaterman.class);
        inv.addItem(makeItemStack(new KitWaterman(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitAcrobat>(KitAcrobat.class);
        inv.addItem(makeItemStack(new KitAcrobat(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitHalberdier>(KitHalberdier.class);
        inv.addItem(makeItemStack(new KitHalberdier(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitBerserker>(KitBerserker.class);
        inv.addItem(makeItemStack(new KitBerserker(), asdo.hasKit(player)));

        asdo = new AbstractKitSuperclassDaoImpl<KitSticker>(KitSticker.class);
        inv.addItem(makeItemStack(new KitSticker(), asdo.hasKit(player)));*/
    }
    // You can open the inventory with this
    public void openInventory(Player player) {
        int x = 0;
        Xp xp = new XpDaoImpl().get(player.getUniqueId().toString());
        if(xp != null) {
            x = xp.getXp();
        }

        inv = Bukkit.createInventory(null, 18, ChatColor.GREEN+"Buy Classes with your xp: " + ChatColor.DARK_RED + x);
        getKitsOfPlayer(player);
        player.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;
        e.setCancelled(true);

        ItemStack clickedItem = e.getCurrentItem();
        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        ItemMeta meta = clickedItem.getItemMeta();
        Player p = (Player) e.getWhoClicked();
        AbstractKitSuperclass a = kitNameMap.get(meta.getDisplayName());
        if(a != null) {
            AbstractKitSuperclassDaoImpl adao = new AbstractKitSuperclassDaoImpl<>(a.getClass());
            if(!adao.hasKit(p)) {
                Xp xp = new XpDaoImpl().get(p.getUniqueId().toString());
                if (xp.getXp() >= a.getKitDescription().getPrice()) {
                    AccceptGUI aa = new AccceptGUI(a, main);
                    main.getServer().getPluginManager().registerEvents(aa, main);
                    aa.openInventory(p);
                } else {
                    p.sendMessage(ChatColor.DARK_RED + "Not enough xp!" + ChatColor.GREEN + " Get xp while playing and get 50 xp for every /vote!");
                }
            } else {
                p.sendMessage(ChatColor.DARK_RED + "You already have that class!" + ChatColor.GREEN + " You dummy ^^");
            }
        }
        return;
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == inv) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if(event.getInventory().equals(inv)) {
            HandlerList.unregisterAll(this);
        }
    }
}
