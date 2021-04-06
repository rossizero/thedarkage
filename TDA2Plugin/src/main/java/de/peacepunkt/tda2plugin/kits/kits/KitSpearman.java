package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.Entity;

@Entity
public class KitSpearman extends AbstractKitSuperclass {
    public static String name = ChatColor.GREEN + "spear";

    @Override
    public  ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[6];
        ItemStack sword = new ItemStack(Material.STICK, 5);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(name);
        sword.setItemMeta(swordMeta);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 9); //1 + 0.5 * ([9]-1)+1 = 6 like iron sword
        i[0] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        i[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        i[2] = boots;
        i[3] = sword;
        i[4] =  new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_BEEF, 10);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Spearman");
        kitDescription.setPrice(0);
        kitDescription.setDescription(new String[]{
                "Chainmail armor",
                "4 Ladders (6)",
                "5 spears",
                "can be thrown at enemies",
                "can only be thrown if hunger is full",
                "gets hungry after throwing a spear",
                "10 Steaks",
                "removes complete rows of ladders if broke with a spear"
        });

        ItemStack sword = new ItemStack(Material.STICK, 1);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(name);
        sword.setItemMeta(swordMeta);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 9);

        kitDescription.setInventoryMaterial(sword);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        //Spearman ladder break
        if(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
                if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(KitSpearman.name)) {
                    if (event.getBlock().getType().equals(Material.LADDER)) {
                        Location l = event.getBlock().getLocation().clone();
                        while (l.add(0, 1, 0).getBlock().getType().equals(Material.LADDER)) {
                            l.getBlock().setType(Material.AIR);
                        }
                        l = event.getBlock().getLocation().clone();
                        while (l.add(0, -1, 0).getBlock().getType().equals(Material.LADDER)) {
                            l.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            if (event.getHitEntity() != null) {
                if (hasMyKit((Player) event.getEntity().getShooter())) {
                    org.bukkit.entity.Entity targetE = event.getHitEntity();
                    if (targetE instanceof Player) {
                        Player target = (Player) targetE;
                        //target.damage(2, (Entity) event.getEntity().getShooter()); //pipapo 8, wie diamond sword
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (event.getItem() != null && event.getItem().getItemMeta() != null) {
                if (event.getItem().getItemMeta().getDisplayName().equals(KitSpearman.name)) {
                    Player player = event.getPlayer();
                    if (player.getFoodLevel() == 20) {
                        event.getItem().setAmount(event.getItem().getAmount() - 1);
                        player.updateInventory();
                        Arrow arrow = player.launchProjectile(Arrow.class);
                        arrow.setDamage(4);
                        player.setFoodLevel(14);
                    }
                }
            }
        }
    }
}
