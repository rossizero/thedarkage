package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.Entity;

@Entity
public class KitHorseman extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[8];
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 1, false);
        bowMeta.setDisplayName(ChatColor.GREEN + "bow");
        bow.setItemMeta(bowMeta);
        i[0] = new ItemStack(Material.LEATHER_CHESTPLATE);
        i[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] = bow;
        i[5] = new ItemStack(Material.ARROW, ExtraHandler.getExtraArrows(player) ? 48 : 24);
        i[6] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[7] = new ItemStack(Material.COOKED_BEEF, 10);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Cavalry");
        kitDescription.setPrice(200000);
        kitDescription.setDescription(new String[]{
                "no desc."
        });
        kitDescription.setInventoryMaterial(new ItemStack(Material.HORSE_SPAWN_EGG));
        return kitDescription;
    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @Override
    protected void addEffects(Player player) {

    }
}
