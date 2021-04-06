package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class KitHalberdier extends AbstractKitSuperclass {
    public static final int REGENTIME = 8; //secs

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[7];
        ItemStack sword = new ItemStack(Material.IRON_AXE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "halberd");
        sword.setItemMeta(swordMeta);
        sword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
        i[0] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        i[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        return i;
    }

   @Override
   public KitDescription getKitDescription() {
       KitDescription kitDescription = new KitDescription("Halberdier");
       kitDescription.setPrice(6000);
       kitDescription.setDescription(new String[]{
               "Diamond chest, chainmail leggings and iron boots",
               "4 Ladders (6)",
               "Iron axe",
               "Strong but slow and hungry",
               "Heals after being not damaged for 8 seconds",
               "can't break ladders for some reason"
       });
       kitDescription.setInventoryMaterial(new ItemStack(Material.DIAMOND_CHESTPLATE));
       return kitDescription;
   }

    @Override
    protected void addEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*60*30, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*30, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20*60*30, 1));
        player.setFoodLevel(6);
    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @EventHandler
    public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if(hasMyKit(player)) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player hit = (Player) event.getEntity();
            if (hasMyKit(hit)) {
                hit.removePotionEffect(PotionEffectType.REGENERATION);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (hit != null) {
                            LocalDateTime time = MainHolder.main.getRoundHandler().getAssistHandler().getTimeStampOf(hit);
                            if (time != null && hasMyKit(hit)) {
                                long seconds = time.until(LocalDateTime.now(), ChronoUnit.SECONDS);
                                if (seconds >= KitHalberdier.REGENTIME - 1) {
                                    hit.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1));
                                }
                            } else {
                                cancel();
                            }
                        } else {
                            cancel();
                        }
                    }
                }.runTaskLater(MainHolder.main, 20 * KitHalberdier.REGENTIME);
            }
        }
    }
}
