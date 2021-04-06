package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import javax.persistence.Entity;

@Entity
public class KitExecutioner extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[6];
        ItemStack sword = new ItemStack(Material.IRON_AXE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + ".exe");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.BLACK);
        chest.setItemMeta(chestMeta);

        i[0] = chest;
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        legMeta.setColor(Color.BLACK);
        leg.setItemMeta(legMeta);
        i[1] = leg;
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.BLACK);
        boots.setItemMeta(bootsMeta);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_BEEF, 5);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Executioner");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String[]{
                "Black Leather armor",
                "4 Ladders (6)",
                "Iron axe (Sharp)",
                "10 Steaks",
                "Beheads enemies with 4 or less hearts instantly",
                "*makes a loud sound*"
        });

        ItemStack sword = new ItemStack(Material.IRON_AXE);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + ".exe");
        sword.setItemMeta(swordMeta);

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

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player whoWasHit = (Player) event.getEntity();
            Player whoHit = (Player) event.getDamager();
            if(hasMyKit(whoHit)) {
                //not in the same team or in MiniArena and both in pvpRadius
                if (!MainHolder.main.getRoundHandler().getRound().getTeam(whoHit).contains(whoWasHit) ||
                        (MainHolder.main.isPlayerInMiniArena(whoWasHit)
                                && MainHolder.main.getMiniPvPHandler().inPvpRadius(whoHit)
                                && MainHolder.main.getMiniPvPHandler().inPvpRadius(whoWasHit))) {

                    if (Math.round(whoWasHit.getHealth()) <= 9 && whoHit.getInventory().getItemInMainHand().getType().equals(Material.IRON_AXE)) {
                        event.setDamage(1000);
                        whoWasHit.getWorld().playSound(whoWasHit.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 1, 1);
                        whoWasHit.getWorld().dropItem(whoWasHit.getLocation(), new ItemStack(MainHolder.main.getRoundHandler().getRound().getTeam(whoWasHit).getTeamMaterial()));
                    }
                }
            }
        }
    }
}
