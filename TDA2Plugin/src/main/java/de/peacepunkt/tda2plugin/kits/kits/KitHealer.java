package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class KitHealer extends AbstractKitSuperclass {
    private static Map<Block, Player> cakes = new HashMap<>();

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[7];
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.WHITE);
        chest.setItemMeta(chestMeta);

        i[0] = chest;
        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        legMeta.setColor(Color.WHITE);
        leg.setItemMeta(legMeta);
        i[1] = new ItemStack(leg);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.WHITE);
        boots.setItemMeta(bootsMeta);
        i[2] = new ItemStack(boots);
        i[3] = new ItemStack(Material.COOKED_BEEF, 5);

        ItemStack bandage = new ItemStack(Material.PAPER);
        ItemMeta milkMeta = bandage.getItemMeta();
        milkMeta.setDisplayName(ChatColor.DARK_RED + "bandage");
        bandage.setItemMeta(milkMeta);
        i[4] = bandage;

        ItemStack cake = new ItemStack(Material.CAKE, 16);
        i[5] = cake;

        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Medic");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String[]{
                "White Leather armor",
                "Bandage that heals team members (heal stat)",
                "16 cakes that heals team members",
                "but can be removed by enemies",
                "5 Steaks",
                "Speed and Resistance",
                "no weapon at all"
        });
        kitDescription.setInventoryMaterial(new ItemStack(Material.PAPER));
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*60*30, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*30, 0));
    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    @EventHandler
    public void onBlockPlacedEvent(BlockPlaceEvent event) {
        if (hasMyKit(event.getPlayer())) {
            if (event.getBlockPlaced().getType().equals(Material.CAKE)) {
                for (Block b : cakes.keySet()) {
                    if (cakes.get(b).equals(event.getPlayer())) { //only 1 cake at a time
                        b.setType(Material.AIR);
                        cakes.remove(b);
                        break;
                    }
                }
                cakes.put(event.getBlockPlaced(), event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType().equals(Material.CAKE)) {
                Player medic = cakes.get(event.getClickedBlock());
                Player clicker = event.getPlayer();
                AttributeInstance healthAttribute = clicker.getAttribute(Attribute.GENERIC_MAX_HEALTH);

                //if cake was set by an enemy
                if (medic != null && !MainHolder.main.getRoundHandler().getRound().getTeam(medic).equals(MainHolder.main.getRoundHandler().getRound().getTeam(event.getPlayer()))) {
                    medic.sendMessage(ChatColor.GRAY + "Your cake was removed by an enemy!");
                    event.getClickedBlock().setType(Material.AIR);
                    cakes.remove(event.getClickedBlock());
                    event.setCancelled(true);
                    return;
                }

                //if cake was not placed by a medic or if medic and target are in same team
                if (medic == null ||
                        MainHolder.main.getRoundHandler().getRound().getTeam(medic).equals(MainHolder.main.getRoundHandler().getRound().getTeam(event.getPlayer()))) {
                    if (clicker.getHealth() < healthAttribute.getBaseValue() - 1) {
                        //if not yet effected
                        for (PotionEffect p : clicker.getActivePotionEffects()) {
                            if (p.getType().equals(PotionEffectType.REGENERATION)) {
                                return;
                            }
                        }
                        //add heal stat if not null and not in Mini Arena
                        if (medic != null) {
                            String name = clicker.getUniqueId().equals(medic.getUniqueId()) ? "yourself" : clicker.getName();
                            medic.sendMessage(ChatColor.GRAY + "You healed " + name);

                            if (!MainHolder.main.isPlayerInMiniArena(clicker))
                                MainHolder.main.getRoundHandler().getRound().getTeam(medic).getRoundStats().addHeal(medic);
                        }

                        //make cake smaller
                        Cake cake = (Cake) event.getClickedBlock().getBlockData();
                        if (cake.getBites() < cake.getMaximumBites() - 1) {
                            cake.setBites(cake.getBites() + 1);
                            event.getClickedBlock().setBlockData(cake);
                            if (clicker.getHealth() < healthAttribute.getBaseValue() - 2) {
                                clicker.setHealth(clicker.getHealth() + 2);
                            } else {
                                clicker.setHealth(healthAttribute.getBaseValue());
                            }
                        } else {
                            event.getClickedBlock().setType(Material.AIR);
                        }
                        clicker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 0));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event)
    {
        if (event.getRightClicked().getType().equals(EntityType.PLAYER))
        {
            if (hasMyKit(event.getPlayer()) && event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                Player target = (Player) event.getRightClicked();
                AttributeInstance healthAttribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
                if(target.getHealth() < healthAttribute.getBaseValue()) {

                    //if target is in Mini Arena or same team
                    if (MainHolder.main.isPlayerInMiniArena(target) ||
                            MainHolder.main.getRoundHandler().getRound().getTeam(target).equals(MainHolder.main.getRoundHandler().getRound().getTeam(event.getPlayer()))) {

                        //if target is not yet effected
                        for (PotionEffect p : target.getActivePotionEffects()) {
                            if (p.getType().equals(PotionEffectType.REGENERATION)) {
                                return;
                            }
                        }
                        target.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 0));
                        event.getPlayer().sendMessage(ChatColor.GRAY + "You healed " + target.getName());

                        //add heal stat if target is not in Mini Arena
                        if (!MainHolder.main.isPlayerInMiniArena(target)) {
                            MainHolder.main.getRoundHandler().getRound().getTeam(event.getPlayer()).getRoundStats().addHeal(event.getPlayer());
                        }
                    }
                }
            }
        }
    }
}
