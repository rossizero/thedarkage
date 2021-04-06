package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import javax.persistence.Entity;

@Entity
public class KitScout extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[6];
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        i[0] = new ItemStack(Material.IRON_CHESTPLATE);
        i[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] =  new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 8 : 6);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Scout");
        kitDescription.setPrice(0);
        kitDescription.setDescription(new String[]{
                "Iron chest, leather leggings, chainmail boots",
                "6 Ladders (8)",
                "Iron sword (Sharp)",
                "no Steaks",
                "No hunger and jump boost",
                "Speed if damaged"
        });

        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) potion.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.SPEED, true, false));
        pm.setDisplayName(ChatColor.LIGHT_PURPLE+"speed");
        potion.setItemMeta(pm);

        kitDescription.setInventoryMaterial(potion);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20*60*30, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20*60*30, 0));
    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @EventHandler
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(hasMyKit(player)) { //add speed if damaged
                if(player.getHealth() < 19) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*300, 1));
                }
            }
        }
    }
    @EventHandler
    public void onEntityRegainHealthEvent(EntityRegainHealthEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(hasMyKit(player)) {
                if(player.getHealth() >= 19) { //remove speed if fully healed
                    player.removePotionEffect(PotionEffectType.SPEED);
                } else {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*30, 1));
                }
            }
        }
    }
}
