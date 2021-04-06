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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class KitBerserker extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[7];
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);


        ItemStack berserkPotion = new ItemStack(Material.POTION, 1);
        PotionMeta berserkPotionMeta = (PotionMeta) berserkPotion.getItemMeta();
        berserkPotionMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "berserk potion");
        List<String> lore = new ArrayList<String>();
        lore.add("Speed");
        lore.add("Strength");
        lore.add("Nausea");
        lore.add("Duration: 20 seconds");
        berserkPotionMeta.setLore(lore);
        berserkPotionMeta.setColor(Color.fromRGB(255, 34, 34));
        berserkPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 20*20, 1), true);
        berserkPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*20, 0), true);
        berserkPotionMeta.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*20, 0), true);
        berserkPotion.setItemMeta(berserkPotionMeta);

        i[0] = new ItemStack(Material.AIR);
        i[1] = new ItemStack(Material.AIR);
        i[2] = new ItemStack(Material.AIR);
        i[3] = sword;
        i[4] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_BEEF, 5);
        i[6] = berserkPotion;
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Berserker");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String[]{
                "No Armor at all",
                "4 Ladders (6)",
                "Iron Sword (Sharp)",
                "5 Steaks",
                "Berserk Potion: Strength + Speed + Nausea for 20 secs."
        });

        ItemStack berserkPotion = new ItemStack(Material.POTION, 1);
        PotionMeta berserkPotionMeta = (PotionMeta) berserkPotion.getItemMeta();
        berserkPotionMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "berserk potion");
        berserkPotionMeta.setColor(Color.fromRGB(255, 34, 34));
        berserkPotion.setItemMeta(berserkPotionMeta);

        kitDescription.setInventoryMaterial(berserkPotion);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }
}
