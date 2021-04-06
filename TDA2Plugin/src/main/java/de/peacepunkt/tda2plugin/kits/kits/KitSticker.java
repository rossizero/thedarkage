package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.kits.AbstractKitSuperclass;
import de.peacepunkt.tda2plugin.kits.ExtraHandler;
import de.peacepunkt.tda2plugin.kits.KitDescription;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
public class KitSticker extends AbstractKitSuperclass {
    public static List<Block> honeyBlocks = new ArrayList<>();
    public static String honeyBow = ChatColor.YELLOW + "sticky bow";
    public static String stelzenBow = ChatColor.DARK_BLUE + "stelzen bow";
    public static Material stelzenMaterial = Material.CRYING_OBSIDIAN;
    public static Main main;

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        ItemStack[] i = new ItemStack[9];
        ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bow.getItemMeta();
        bowMeta.setDisplayName(honeyBow);
        bow.setItemMeta(bowMeta);

        ItemStack bow2 = new ItemStack(Material.BOW);
        ItemMeta bow2Meta = bow.getItemMeta();
        bow2Meta.setDisplayName(stelzenBow);
        bow2.setItemMeta(bow2Meta);
        i[0] = new ItemStack(Material.LEATHER_CHESTPLATE);
        i[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);
        i[2] = boots;
        i[3] = sword;
        i[4] = bow;
        i[5] = bow2;
        i[6] = new ItemStack(Material.ARROW, ExtraHandler.getExtraArrows(player) ? 48 : 24);
        i[7] = new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[8] = new ItemStack(Material.COOKED_BEEF, 10);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Sticker");
        kitDescription.setPrice(200000);
        kitDescription.setDescription(new String[]{
                "No description",
        });
        kitDescription.setInventoryMaterial(new ItemStack(Material.HONEY_BLOCK));
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
    public void onProjectileHitEvent(ProjectileHitEvent event) {
        if(event.getEntity().getShooter() instanceof Player) {
            if(hasMyKit((Player)event.getEntity().getShooter())) {
                if(event.getEntity().getMetadata("bowType") != null) {
                    if (event.getEntity().getMetadata("bowType").get(0).asString().equals(KitSticker.stelzenBow)) {
                        if (event.getHitEntity() != null && main.getMiniPvPHandler().inInnerPvpRadius(event.getHitEntity().getLocation())) {
                            Block block = event.getHitEntity().getLocation().getBlock();
                            ((Player) event.getHitEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 10*20, 1));
                            int stelzen = new Random().nextInt(6) + 5;
                            int count = 0;
                            for (int i = 0; i < stelzen; i++) {
                                if (block.getLocation().clone().add(0, i, 0).getBlock().getType().equals(Material.AIR)) {
                                    count++;
                                }
                            }
                            if (count > 2) {
                                count -= 2;
                                boolean doo = true;
								/*if (!block.getLocation().clone().add(0, -1, 0).getBlock().getType().equals(Material.AIR)) {
									if (block.getLocation().clone().add(0, -2, 0).getBlock().getType().equals(Material.AIR)) {
										count -= 1;
									} else {
										doo = false;
									}
								}*/
                                Block b = block.getLocation().getBlock();
                                int counter = 0;
                                while(b.getType().equals(Material.AIR)) {
                                    b.setType(KitSticker.stelzenMaterial);
                                    Block finalB = b;
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            finalB.setType(Material.AIR);
                                        }
                                    }.runTaskLater(main, (new Random().nextInt(40) + 20) * 20);
                                    counter++;
                                    b = block.getLocation().clone().add(0, -counter, 0).getBlock();
                                }
                                if (doo) {
                                    event.getHitEntity().teleport(event.getHitEntity().getLocation().clone().add(0, count, 0));
                                    for (int i = 0; i < count; i++) {
                                        Block block2 = block.getLocation().clone().add(0, i, 0).getBlock();
                                        block2.setType(KitSticker.stelzenMaterial);
                                        new BukkitRunnable() {
                                            @Override
                                            public void run() {
                                                block2.setType(Material.AIR);
                                            }
                                        }.runTaskLater(main, (new Random().nextInt(40) + 20) * 20);
                                    }
                                }
                            }
                        }
                    } else if (event.getEntity().getMetadata("bowType").get(0).asString().equals(KitSticker.honeyBow)) {
                        int radius = 2;
                        if (event.getHitBlock() != null) {
                            for (int i = -radius; i < radius; i++) {
                                for (int j = -radius; j < radius; j++) {
                                    for (int k = -radius; k < radius; k++) {
                                        Block block = event.getHitBlock().getLocation().clone().add(i, j, k).getBlock();
                                        if (!block.getType().equals(Material.AIR) && !KitSticker.honeyBlocks.contains(block)) {
                                            BlockData data = block.getBlockData();
                                            Material old = block.getType();
                                            if (!old.equals(Material.OAK_WALL_SIGN) &&
                                                    !old.equals(Material.ACACIA_SIGN) &&
                                                    !old.equals(Material.ACACIA_WALL_SIGN) &&
                                                    !old.equals(Material.BIRCH_SIGN) &&
                                                    !old.equals(Material.BIRCH_WALL_SIGN) &&
                                                    !old.equals(Material.CRIMSON_SIGN) &&
                                                    !old.equals(Material.CRIMSON_WALL_SIGN) &&
                                                    !old.equals(Material.DARK_OAK_SIGN) &&
                                                    !old.equals(Material.DARK_OAK_WALL_SIGN) &&
                                                    !old.equals(Material.JUNGLE_SIGN) &&
                                                    !old.equals(Material.JUNGLE_WALL_SIGN) &&
                                                    !old.equals(Material.OAK_SIGN) &&
                                                    !old.equals(Material.SPRUCE_SIGN) &&
                                                    !old.equals(Material.SPRUCE_WALL_SIGN) &&
                                                    !old.equals(Material.WARPED_SIGN) &&
                                                    !old.equals(Material.WARPED_WALL_SIGN) &&
                                                    !old.equals(KitSticker.stelzenMaterial) &&
                                                    main.getMiniPvPHandler().inInnerPvpRadius(block.getLocation()) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.ACACIA_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.BIRCH_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.CRIMSON_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.DARK_OAK_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.IRON_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.JUNGLE_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.OAK_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.SPRUCE_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.WARPED_TRAPDOOR) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.ACACIA_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.HEAVY_WEIGHTED_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.LIGHT_WEIGHTED_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.STONE_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.BIRCH_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.CRIMSON_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.DARK_OAK_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.OAK_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.SPRUCE_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.WARPED_PRESSURE_PLATE) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.LEVER) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.STONE_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.ACACIA_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.BIRCH_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.CRIMSON_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.DARK_OAK_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.JUNGLE_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.OAK_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.POLISHED_BLACKSTONE_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.SPRUCE_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.WARPED_BUTTON) &&
                                                    !block.getLocation().clone().add(0, 1, 0).getBlock().getType().equals(Material.JUNGLE_PRESSURE_PLATE)) {
                                                block.setType(Material.HONEY_BLOCK);
                                                KitSticker.honeyBlocks.add(block);
                                                new BukkitRunnable() {
                                                    @Override
                                                    public void run() {
                                                        block.setType(old);
                                                        block.setBlockData(data);
                                                        KitSticker.honeyBlocks.remove(block);
                                                    }
                                                }.runTaskLater(main, (new Random().nextInt(20) + 10) * 20);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    public static MetadataValue honeyBowMeta = new MetadataValue() {
        @Override
        public Object value() {
            return null;
        }

        @Override
        public int asInt() {
            return 0;
        }

        @Override
        public float asFloat() {
            return 0;
        }

        @Override
        public double asDouble() {
            return 0;
        }

        @Override
        public long asLong() {
            return 0;
        }

        @Override
        public short asShort() {
            return 0;
        }

        @Override
        public byte asByte() {
            return 0;
        }

        @Override
        public boolean asBoolean() {
            return false;
        }

        @Override
        public String asString() {
            return KitSticker.honeyBow;
        }

        @Override
        public Plugin getOwningPlugin() {
            return main;
        }

        @Override
        public void invalidate() {

        }
    };

    public static MetadataValue stelzenBowMeta = new MetadataValue() {
        @Override
        public Object value() {
            return null;
        }

        @Override
        public int asInt() {
            return 0;
        }

        @Override
        public float asFloat() {
            return 0;
        }

        @Override
        public double asDouble() {
            return 0;
        }

        @Override
        public long asLong() {
            return 0;
        }

        @Override
        public short asShort() {
            return 0;
        }

        @Override
        public byte asByte() {
            return 0;
        }

        @Override
        public boolean asBoolean() {
            return false;
        }

        @Override
        public String asString() {
            return KitSticker.stelzenBow;
        }

        @Override
        public Plugin getOwningPlugin() {
            return main;
        }

        @Override
        public void invalidate() {

        }
    };

}
