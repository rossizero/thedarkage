package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
public class KitSense extends AbstractKitSuperclass {
    public static String name = ChatColor.GREEN + "kilij";
    //sharpness = baseDamage + 0.5 * (Sharp_level - 1 ) + 1
    //iron sword = 6 --> iron armor
    //trident = 9 --> much lower armor
    //golden_hoe = 1 --> add sharp and golden armor 1 + 0.5 * (9-1)+1 = 6

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[7];
        ItemStack sense = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta swordMeta = sense.getItemMeta();
        swordMeta.setDisplayName(name);
        sense.setItemMeta(swordMeta);
        sense.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7); //1 + 0.5 * ([9]-1)+1 = 6 like iron sword
        sense.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack knife = new ItemStack(Material.FLINT);
        ItemMeta knifeMeta = sense.getItemMeta();
        knifeMeta.setDisplayName(ChatColor.DARK_RED + "knife");
        knife.setItemMeta(knifeMeta);
        knife.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1); //1 + 0.5 * ([5]-1)+1 = 6 like iron sword


        ItemStack chest = new ItemStack(Material.GOLDEN_CHESTPLATE);
        ItemMeta chestMeta = chest.getItemMeta();
        chestMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        chest.setItemMeta(chestMeta);
        chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        i[0] = chest;
        ItemStack leg = new ItemStack(Material.GOLDEN_LEGGINGS);
        leg.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        leg.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        i[1] = leg;
        ItemStack boots = new ItemStack(Material.GOLDEN_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        i[2] = boots;
        i[3] = sense;
        i[4] = knife;
        i[5] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[6] = new ItemStack(Material.BAKED_POTATO, 13);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Templar");
        kitDescription.setPrice(0);
        kitDescription.setDescription(new String[]{
                "Golden armor",
                "4 Ladders (6)",
                "Golden hoe",
                "can be right clicked 3 times to push enemies away",
                "a weak knife one can use if the golden hoe broke",
                "13 Baked Potatoes",
                "Special class: available on few maps only"
        });

        ItemStack sense = new ItemStack(Material.GOLDEN_HOE);
        ItemMeta swordMeta = sense.getItemMeta();
        swordMeta.setDisplayName(name);
        sense.setItemMeta(swordMeta);
        sense.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7); //1 + 0.5 * ([9]-1)+1 = 6 like iron sword
        sense.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        kitDescription.setInventoryMaterial(sense);
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {

    }

    @Override
    public boolean isMapSpecificKit() {
        return true;
    }

    private double normalizeAngleDegree(double angle) {
        double myaw = angle % 360;
        double nyaw = myaw < 0 ? 360 + myaw: myaw;
        return nyaw;
    }

    //TODO rework
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getItem() != null) {
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (event.getItem().getItemMeta().getDisplayName().equals(KitSense.name)) {
                    //if not out of pvp radius in Mini Arena
                    if (!MainHolder.main.getMiniPvPHandler().inPvpRadius(event.getPlayer())) {
                        ItemStack item = event.getItem();
                        Player player = event.getPlayer();
                        org.bukkit.inventory.meta.Damageable damageable = (org.bukkit.inventory.meta.Damageable) item.getItemMeta();
                        damageable.setDamage(damageable.getDamage() + Material.GOLDEN_HOE.getMaxDurability() / 3 + 1);
                        if (damageable.getDamage() > Material.GOLDEN_HOE.getMaxDurability()) {
                            player.getLocation().getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
                            player.getLocation().getWorld().spawnParticle(Particle.ITEM_CRACK, player.getLocation().add(player.getLocation().getDirection()), 10, 0.3, 0.5, 0.3, 0, new ItemStack(Material.GOLDEN_HOE));
                            item.setAmount(0);
                        } else {
                            item.setItemMeta((ItemMeta) damageable);
                        }
                        player.updateInventory();
                        Teem t = MainHolder.main.getRoundHandler().getRound().getTeam(player);
                        List<org.bukkit.entity.Entity> pushed = new ArrayList<>();
                        for (org.bukkit.entity.Entity e : player.getNearbyEntities(5, 5, 5)) {
                            if (e instanceof Player) {
                                Player ee = (Player) e;
                                if (!MainHolder.main.getRoundHandler().getRound().getTeam(ee).equals(t) || (MainHolder.main.isPlayerInMiniArena(ee) && MainHolder.main.getMiniPvPHandler().inPvpRadius(ee))) {
                                    pushed.add(ee);
                                }
                            } else {
                                pushed.add(e);
                            }
                        }
                        //direction player is looking
                        //Vector dir = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw() + 90)), 0, Math.sin(Math.toRadians(player.getLocation().getYaw() + 90)));
                        double lower = normalizeAngleDegree((player.getLocation().getYaw() + 90)) - 45;
                        double upper = normalizeAngleDegree((player.getLocation().getYaw() + 90)) + 45;
                        //Vector lower = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw() + 90 - 45)), 0, Math.sin(Math.toRadians(player.getLocation().getYaw() + 90 - 45)));
                        //Vector upper = new Vector(Math.cos(Math.toRadians(player.getLocation().getYaw() + 90 + 45)), 0, Math.sin(Math.toRadians(player.getLocation().getYaw() + 90 + 45)));
                        for (org.bukkit.entity.Entity p : pushed) {
                            double dX = p.getLocation().getX() - player.getLocation().getX();
                            //double dY = player.getLocation().getY() - p.getLocation().getY();
                            double dZ = p.getLocation().getZ() - player.getLocation().getZ();
                            double yaw = normalizeAngleDegree(Math.atan2(dZ, dX) * 180 / Math.PI);
                            if (yaw < upper && yaw > lower) {
                                p.setVelocity(new Vector(Math.cos(Math.toRadians(yaw)), 0.2, Math.sin(Math.toRadians(yaw))).multiply(2));
                                if (p instanceof Player) {
                                    EntityDamageEvent.DamageCause cause = EntityDamageEvent.DamageCause.MAGIC;
                                    Player target = (Player) p;
                                    EntityDamageEvent ede = new EntityDamageByEntityEvent(p, target, cause, 0.1);
                                    target.setLastDamageCause(ede);
                                    Bukkit.getServer().getPluginManager().callEvent(ede);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
