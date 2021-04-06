package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

@Entity
public class KitTrapper extends AbstractKitSuperclass {
    private static Map<Block, Player> traps = new HashMap<>();

    public static Material trapMaterial = Material.ACACIA_PRESSURE_PLATE;

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[10];
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.SILVER);
        chest.setItemMeta(chestMeta);

        ItemStack molotov = new ItemStack(Material.LINGERING_POTION, 4);
        PotionMeta  molotovMeta = (PotionMeta) molotov.getItemMeta();
        molotovMeta.setColor(Color.fromRGB(226, 88, 34));
        molotovMeta.setBasePotionData(new PotionData(PotionType.INSTANT_DAMAGE));
        molotovMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "molotov cocktail");
        molotov.setItemMeta(molotovMeta);
        molotov.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 5);

        ItemStack timeBomb = new ItemStack(Material.LEVER, 2);
        ItemMeta timeBombMeta = timeBomb.getItemMeta();
        timeBombMeta.setDisplayName(ChatColor.LIGHT_PURPLE+"tick tack");
        timeBomb.setItemMeta(timeBombMeta);
        timeBomb.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 8);

        ItemStack trap = new ItemStack(Material.ACACIA_PRESSURE_PLATE, 5);
        ItemMeta trapMeta = trap.getItemMeta();
        trapMeta.setDisplayName(ChatColor.DARK_RED +"Trap");
        trap.setItemMeta(trapMeta);

        ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leg.getItemMeta();
        legMeta.setColor(Color.SILVER);
        leg.setItemMeta(legMeta);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.SILVER);
        boots.setItemMeta(bootsMeta);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);

        i[0] = chest;
        i[1] = leg;
        i[2] = boots;
        i[3] = sword;
        i[4] = molotov;
        i[5] = timeBomb;
        i[6] = trap;
        i[7] = new ItemStack(Material.COBWEB, 5);
        i[8] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[9] = new ItemStack(Material.COOKED_BEEF, 5);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Trapper");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String[]{
                "Leather armor",
                "4 Ladders (6)",
                "Stone sword (Sharp)",
                "5 Steaks",
                "5 cobwebs & 5 Traps",
                "4 molotov cocktails",
                "2 time bombs, that harm every player",
                "effects near teammates with regeneration after explosion"
        });
        ItemStack timeBomb = new ItemStack(Material.LEVER, 1);
        ItemMeta timeBombMeta = timeBomb.getItemMeta();
        timeBombMeta.setDisplayName(ChatColor.LIGHT_PURPLE+"tick tack");
        timeBomb.setItemMeta(timeBombMeta);
        timeBomb.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 8);

        kitDescription.setInventoryMaterial(timeBomb);
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
    public void onBlockPlacedEvent(BlockPlaceEvent event) {
        if(hasMyKit(event.getPlayer())) {
            if(event.getBlockPlaced().getType().equals(KitTrapper.trapMaterial)) {
                traps.put(event.getBlockPlaced(), event.getPlayer());
                if(!event.getBlockPlaced().getWorld().getName().equals("miniarena")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (traps.containsKey(event.getBlockPlaced())) {
                                traps.remove(event.getBlockPlaced());
                                event.getBlockPlaced().setType(Material.AIR);
                            }
                        }
                    }.runTaskLater(MainHolder.main, 60 * 20);
                }
            } else if(event.getBlockPlaced().getType().equals(Material.LEVER)) {
                new BukkitRunnable() {
                    Player from = event.getPlayer();
                    Block lever = event.getBlock();
                    boolean activated = false;
                    boolean exploded = false;
                    @Override
                    public void run() {
                        if (!activated && lever.getBlockPower() >= 1) {
                            lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 10);
                            activated = true;
                        } else if(activated && lever.getBlockPower() >=1) {
                            //ticktack
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    try {
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(1000);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(800);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(600);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(500);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(350);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(200);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(100);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(80);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(60);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(40);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(20);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(20);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(20);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(20);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                        Thread.sleep(20);
                                        lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 4, 15);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.runTaskAsynchronously(MainHolder.main);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (Player other : Bukkit.getOnlinePlayers()) {
                                        if(other.getWorld().equals(lever.getWorld())) {
                                            if (other.getLocation().distance(lever.getLocation()) < 2 * 4) {
                                                if(!MainHolder.main.getRoundHandler().getRound().getTeam(from).equals(MainHolder.main.getRoundHandler().getRound().getTeam(other))) {
                                                    other.damage(0.1, from);
                                                } else {
                                                    other.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, (int) ((10-other.getLocation().distance(lever.getLocation()))*50), 0));
                                                }
                                            }
                                        }
                                    }
                                    lever.getWorld().playSound(lever.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 0);
                                    lever.getWorld().createExplosion(lever.getLocation(), 4, false, false);
                                    lever.setType(Material.AIR);
                                    exploded = true;
                                }
                            }.runTaskLater(MainHolder.main, 4*20);

                            cancel();
                        } else if(activated && lever.getBlockPower() < 1) {
                            //got deactivated
                            lever.getWorld().playSound(lever.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 4, 0);
                            lever.setType(Material.AIR);
                            from.sendMessage(ChatColor.GRAY + "Your bomb has been defused");
                            cancel();
                        }
                    }
                }.runTaskTimer(MainHolder.main, 0, 20);

            }
        }
    }

    //TODO rework
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            if (event.getClickedBlock().getType().equals(KitTrapper.trapMaterial)) {
                Player trapper = traps.get(event.getClickedBlock());
                if (trapper != null) {
                    if (MainHolder.main.getMiniPvPHandler().inPvpRadius(event.getClickedBlock().getLocation())) {
                        if (!event.getPlayer().equals(trapper)) {
                            event.getPlayer().damage(9, trapper);
                            event.getPlayer().sendMessage(ChatColor.GRAY + "You stepped into " + trapper.getName() + ChatColor.GRAY + "'s trap!");
                            trapper.sendMessage(ChatColor.GRAY + "" + event.getPlayer().getName() + ChatColor.GRAY + " stepped into your trap!");
                            event.getClickedBlock().setType(Material.AIR);
                            traps.remove(event.getClickedBlock());
                            event.setCancelled(true);
                        }
                    } else {
                        if (!MainHolder.main.getRoundHandler().getRound().getTeam(trapper).equals(MainHolder.main.getRoundHandler().getRound().getTeam(event.getPlayer()))) {
                            event.getPlayer().damage(9, trapper);
                            event.getPlayer().sendMessage(ChatColor.GRAY + "You stepped into " + trapper.getDisplayName() + ChatColor.GRAY + "'s trap!");
                            trapper.sendMessage(ChatColor.GRAY + "" + event.getPlayer().getDisplayName() + ChatColor.GRAY + " stepped into your trap!");
                            event.getClickedBlock().setType(Material.AIR);
                            traps.remove(event.getClickedBlock());
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}
