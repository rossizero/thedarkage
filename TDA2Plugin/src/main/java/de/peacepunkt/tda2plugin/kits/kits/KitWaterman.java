package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.persistence.Entity;

@Entity
public class KitWaterman extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[7];
        ItemStack sword = new ItemStack(Material.TRIDENT);
        sword.addEnchantment(Enchantment.RIPTIDE, 2);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "trident");
        sword.setItemMeta(swordMeta);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.AQUA);
        chest.setItemMeta(chestMeta);

        i[0] = chest;
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        legMeta.setColor(Color.AQUA);
        leg.setItemMeta(legMeta);
        i[1] = new ItemStack(leg);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 3);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.AQUA);
        boots.setItemMeta(bootsMeta);
        i[2] = new ItemStack(boots);
        i[3] = sword;
        i[4] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_SALMON, 10);

        ItemStack milk = new ItemStack(Material.MILK_BUCKET);
        ItemMeta milkMeta = milk.getItemMeta();
        milkMeta.setDisplayName(ChatColor.BLUE + "calcium");
        milk.setItemMeta(milkMeta);
        i[6] = milk;
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Aquatic");
        kitDescription.setPrice(0);
        kitDescription.setDescription(new String[]{
                "Leather armor with fall damage 3",
                "4 Ladders (6)",
                "Trident with riptide 2",
                "catapults you out of water sources if thrown",
                "10 Cooked Salmon",
                "Special class: available on few maps only"
        });
        ItemStack sword = new ItemStack(Material.TRIDENT);
        sword.addEnchantment(Enchantment.RIPTIDE, 2);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "trident");
        sword.setItemMeta(swordMeta);

        kitDescription.setInventoryMaterial(sword);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return true;
    }
}
