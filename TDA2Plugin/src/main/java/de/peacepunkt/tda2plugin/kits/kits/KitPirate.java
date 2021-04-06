package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.GrappleRunnable;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.persistence.Entity;
import java.util.Random;

@Entity
public class KitPirate extends AbstractKitSuperclass {

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[9];
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "saber");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        i[0] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        i[1] = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_COD, 10);

        ItemStack grapple = new ItemStack(Material.CROSSBOW);
        ItemMeta grappleMeta = grapple.getItemMeta();
        grappleMeta.setDisplayName(ChatColor.GREEN + "grappling hook");
        grapple.setItemMeta(grappleMeta);
        i[6] = grapple;
        i[7] = new ItemStack(Material.ARROW, ExtraHandler.getExtraArrows(player) ? 4 : 2);

        ItemStack rum = new ItemStack(Material.HONEY_BOTTLE);
        ItemMeta rumMeta = rum.getItemMeta();
        rumMeta.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "RUM");
        rum.setItemMeta(rumMeta);
        i[8] = rum;
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Pirate");
        kitDescription.setPrice(0);
        kitDescription.setDescription(new String[]{
                "Chainmail chest, iron leggings & boots",
                "Iron sword (Sharp)",
                "4 Ladders (6)",
                "10 cooked cod",
                "A grappling hook",
                "(max. 30 blocks far)",
                "can be used to easily cross rough terrain",
                "2 arrows (4)",
                "Replaces the swordsman class on some maps"
        });
        ItemStack grapple = new ItemStack(Material.CROSSBOW);
        ItemMeta grappleMeta = grapple.getItemMeta();
        grappleMeta.setDisplayName(ChatColor.GREEN + "grappling hook");
        grapple.setItemMeta(grappleMeta);
        kitDescription.setInventoryMaterial(grapple);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return true;
    }

    //If player is pirate add some ARRRs!
    @EventHandler(priority = EventPriority.LOW)
    public  void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        String[] split = event.getMessage().split(" ");
        StringBuilder newMessage = new StringBuilder();
        if(hasMyKit(event.getPlayer())) {
            for(String s : split) {
                if(new Random().nextInt(20) < 1) {
                    newMessage.append(ChatColor.BOLD + "ARRRR " + ChatColor.RESET);
                }
                newMessage.append(s + " ");
            }
            event.setMessage(newMessage.toString());
        }
    }

    @EventHandler
    public void onEntityShootBowEvent(EntityShootBowEvent event) {
        if (event.getProjectile() instanceof Arrow) {
            if (event.getEntity() instanceof Player) {
                Player pirate = (Player) event.getEntity();
                if (hasMyKit(pirate)) {
                    if(!PlayerHandler.getInstance().isAtSpawn(pirate)) {
                        GrappleRunnable gr = new GrappleRunnable((Arrow) event.getProjectile());
                        gr.runTaskTimer(MainHolder.main, 0, 1);
                    }
                }
            }
        }
    }
}
