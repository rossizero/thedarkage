package de.peacepunkt.tda2plugin.kits.gui;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclassDaoImpl;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
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

import java.util.Arrays;

public class AccceptGUI implements Listener {
    private  Inventory inv;
    AbstractKitSuperclass classname;
    Main main;
    public AccceptGUI(AbstractKitSuperclass classname, Main main) {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "Buy " + classname.getKitDescription().getName() + "?");
        // Put the items into the inventory
        this.classname = classname;
        initializeItems();
        this.main = main;
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        String yes = ChatColor.GREEN + "yes";
        String no = ChatColor.RED + "no";

        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE, yes, "   "));
        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE, yes, "  "));
        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE, yes, " "));
        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE, yes, ""));
        inv.addItem(createGuiItem(Material.BEDROCK, "click yes or no"));
        inv.addItem(createGuiItem(Material.RED_STAINED_GLASS_PANE, no, ""));
        inv.addItem(createGuiItem(Material.RED_STAINED_GLASS_PANE, no, " "));
        inv.addItem(createGuiItem(Material.RED_STAINED_GLASS_PANE, no, "  "));
        inv.addItem(createGuiItem(Material.RED_STAINED_GLASS_PANE, no, "   "));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        Player p = (Player) e.getWhoClicked();
        if(e.getRawSlot() < 4) {
            Xp xp = new XpDaoImpl().get(p.getUniqueId().toString());
            xp.addXp(classname.getKitDescription().getPrice() * -1, false);
            new XpDaoImpl().update(xp);
            AbstractKitSuperclassDaoImpl dao = new AbstractKitSuperclassDaoImpl<>(classname.getClass());
            classname.setUuid(p.getUniqueId().toString());
            dao.add(classname);
            p.sendMessage(ChatColor.GREEN + "You just unlocked " + classname.getKitDescription().getName() + " for " + ChatColor.DARK_RED + classname.getKitDescription().getPrice() + ChatColor.GREEN + " xp!");
            Bukkit.broadcastMessage(ChatColor.GREEN + p.getDisplayName() + ChatColor.GREEN + " just unlocked " + classname.getKitDescription().getName() + ". Have fun!");
            p.closeInventory();
            KitHandler.getInstance().setKit(p, classname.getKitDescription().getName(), true, true, false);
        } else if(e.getRawSlot() > 4){
            p.sendMessage(ChatColor.DARK_RED + "You canceled buying " + classname.getKitDescription().getName() + "!");
            p.closeInventory();
        }
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
