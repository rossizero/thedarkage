package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class KitAcrobat extends AbstractKitSuperclass {
    private static List<AcrobatTimeout> acrobatsJumping = new ArrayList<>();

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
        bowMeta.addEnchant(Enchantment.ARROW_DAMAGE, 0, false);
        bowMeta.setDisplayName(ChatColor.GREEN + "bow");
        bow.setItemMeta(bowMeta);
        i[0] = new ItemStack(Material.LEATHER_CHESTPLATE);
        i[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);
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
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Acrobat");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String [] {
                "Leather chest & leggings and golden boots",
                "Bow & 24 Arrows (48)",
                "4 Ladders (6)",
                "Wooden Sword (Sharp.)",
                "10 Steaks",
                "No fall damage and a double jump every 15 seconds"
        });
        kitDescription.setInventoryMaterial(new ItemStack(Material.FEATHER));
        return kitDescription;
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) { //TODO make better
        //enables allowflight if player is acrobat and jumping upwards
        if (!EditorHandler.getInstance().isPlayerInEditMode(event.getPlayer())) {
            if (hasMyKit(event.getPlayer())) {
                AcrobatTimeout at = addAcrobatTimeout(event.getPlayer());
                if (at.canDoubleJump()) {
                    event.getPlayer().setAllowFlight(true);
                } else {
                    event.getPlayer().setAllowFlight(false);
                }
            } else if (event.getPlayer().getAllowFlight() && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                event.getPlayer().setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onDouble(PlayerToggleFlightEvent event) {
        if(hasMyKit(event.getPlayer())) {
            AcrobatTimeout at = getAcrobatTimeout(event.getPlayer());
            if (at != null) {
                if(at.canDoubleJump()) {
                    event.getPlayer().setAllowFlight(false);
                    event.setCancelled(true);
                    at.jump();
                }
            } else { //just to be safe
                event.getPlayer().setAllowFlight(false);
            }
        } else if(event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            //if someone changed kit and tries to fly
            event.getPlayer().setAllowFlight(false);
        }
    }

    /**
     * Adds a new Acrobat Timeout if one does not yet exist for player
     * @param player
     * @return new or existing Acrobat Timeout
     */
    private AcrobatTimeout addAcrobatTimeout(Player player) {
        AcrobatTimeout ret = getAcrobatTimeout(player);
        if(ret == null) {
            ret = new AcrobatTimeout(player);
            acrobatsJumping.add(ret);
        }
        return ret;
    }

    /**
     * returns Acrobat Timeout for  player if exists
     * @param player
     * @return Acrobat Timeout for  player if exists
     */
    private AcrobatTimeout getAcrobatTimeout(Player player) {
        for(AcrobatTimeout at : acrobatsJumping) {
            if (at.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return at;
            }
        }
        return null;
    }

    class AcrobatTimeout {
        Player player;
        boolean jumped = false;
        final int TIMEOUT = 20;
        AcrobatTimeout(Player player) {
            this.player = player;
        }

        boolean canDoubleJump() {
            return !jumped;
        }
        void jump() {
            jumped = true;
            player.setVelocity(player.getEyeLocation().getDirection().normalize().multiply(new Vector(0.83, 0, 0.83)).add(new Vector(0, 1.13, 0)));
            AcrobatTimeout self = this;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(PlayerHandler.getInstance().getKit(player).getKitDescription().getName().equals("Acrobat")) {
                        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.GRAY + "double jump is ready"));
                    }
                    acrobatsJumping.remove(self); //so list won't fill up to infinity eventually
                }
            }.runTaskLater(MainHolder.main, 20 * TIMEOUT);
        }
        public Player getPlayer() {
            return player;
        }
    }
}
