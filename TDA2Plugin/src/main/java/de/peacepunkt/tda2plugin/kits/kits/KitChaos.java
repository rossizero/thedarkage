package de.peacepunkt.tda2plugin.kits.kits;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.kits.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import javax.persistence.Entity;
import java.util.*;

@Entity
public class KitChaos extends AbstractKitSuperclass {
    public static String crowCaller = ChatColor.DARK_GRAY +  "crow caller";
    static List<Player> players;
    public static int MAX_FLOW_DISTANCE = 4;

    @Override
    public ItemStack[] getKitItemStacks(Player player) {
        if(players == null)
            players = new ArrayList<Player>();
        ItemStack[] i = new ItemStack[10];
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemMeta swordMeta = sword.getItemMeta();
        swordMeta.setDisplayName(ChatColor.GREEN + "sword");
        sword.setItemMeta(swordMeta);
        if(ExtraHandler.getExtraSharpness(player))
            sword.addEnchantment(Enchantment.DAMAGE_ALL, 1);

        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.BLACK);
        chest.setItemMeta(chestMeta);

        i[0] = chest;
        i[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);


        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.SILVER);
        boots.setItemMeta(bootsMeta);
        if(ExtraHandler.getExtraFeatherFalling(player))
            boots.addEnchantment(Enchantment.PROTECTION_FALL, 1);


        ItemStack crowCaller = new ItemStack(Material.PUMPKIN_SEEDS, 1);
        ItemMeta crowCallerMeta = crowCaller.getItemMeta();
        crowCallerMeta.setDisplayName(KitChaos.crowCaller);
        crowCaller.setItemMeta(crowCallerMeta);


        i[2] = boots;
        i[3] = sword;
        i[4] =  new ItemStack(Material.LADDER, ExtraHandler.getExtraLadders(player) ? 6 : 4);
        i[5] = new ItemStack(Material.COOKED_BEEF, 10);
        i[6] = new ItemStack(Material.LAVA_BUCKET, 1);
        i[7] = new ItemStack(Material.WATER_BUCKET, 1);
        i[8] = new ItemStack(Material.COBWEB, 5);
        //i[9] = crowCaller;
        i[9] = new ItemStack(Material.AIR);
        return i;
    }

    @Override
    public KitDescription getKitDescription() {
        KitDescription kitDescription = new KitDescription("Chaos");
        kitDescription.setPrice(6000);
        kitDescription.setDescription(new String[]{
                "Black Leather chest & boots and chainmail leggings",
                "4 Ladders (6)",
                "Stone Sword (Sharp)",
                "10 Steaks",
                "Lava and Water bucket",
                "5 cobwebs",
                "Invisible while sneaking (fireworks though)",
                "Random invisibility glitches"
        });
        kitDescription.setInventoryMaterial(new ItemStack(Material.LAVA_BUCKET));
        return kitDescription;
    }

    @Override
    protected void addEffects(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*60*30, 0));
        ItemStack boots = getKitItemStacks(player)[2];
        ItemStack chests = getKitItemStacks(player)[0];

        if(!players.contains(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!hasMyKit(player) || !player.isOnline()) { //remove inactive players
                        cancel();
                        players.remove(player);
                    } else {
                        Random r = new Random();
                        if (r.nextInt(4) < 1 || player.isSneaking()) {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 60 * 30, 1));
                            player.getInventory().setChestplate(new ItemStack(Material.AIR));
                            player.getInventory().setBoots(new ItemStack(Material.AIR));
                            player.getInventory().setHelmet(new ItemStack(Material.AIR));
                            player.getInventory().setLeggings(new ItemStack(Material.AIR));
                        } else {
                            player.removePotionEffect(PotionEffectType.INVISIBILITY);
                            player.getInventory().setChestplate(chests);
                            player.getInventory().setBoots(boots);
                            player.getInventory().setHelmet(new ItemStack(MainHolder.main.getRoundHandler().getRound().getTeam(player).getTeamMaterial()));
                            player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                        }
                        if (player.isSneaking()) {
                            if (r.nextInt(50) < 1) {
                                FireworkEffect effect = FireworkEffect.builder().withColor(Color.WHITE).withFade(Color.BLACK).with(FireworkEffect.Type.BURST).trail(true).flicker(true).build();
                                Firework fw = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                                FireworkMeta meta = fw.getFireworkMeta();
                                meta.addEffect(effect);
                                meta.setPower(1);
                                fw.setFireworkMeta(meta);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2*60, 1));
                            }
                        }
                    }
                }
            }.runTaskTimer(MainHolder.main, 2, 5);
            players.add(player);
        }
    }
    private static List<LavaFlowTree> whosLava = new ArrayList<>();

    private void limtLava(PlayerBucketEmptyEvent event) {
        if(event.getBucket().equals(Material.LAVA_BUCKET)) {
            LavaFlowTree lft = new LavaFlowTree(event.getBlock(), event.getPlayer());
            MainHolder.main.getServer().getPluginManager().registerEvents(lft,  MainHolder.main);
            whosLava.add(lft);
        }
    }

    private void placeBucket(PlayerBucketEmptyEvent event) {
        BlockData data = event.getBlock().getBlockData();
        limtLava(event);
        //event.getPlayer().getInventory().removeItem(event.getItemStack());
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setBlockData(data);
            }
        }.runTaskLater(MainHolder.main, event.getBucket().equals(Material.LAVA_BUCKET) ? 90 : 20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        if(event.getEntity().getLastDamageCause() != null) {
            EntityDamageEvent damageEvent = event.getEntity().getLastDamageCause();
            if(damageEvent.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
                LavaFlowTree t = getLavaFlowTree(event.getEntity().getLocation());
                if(t != null && t.getPlayer().isOnline()) {
                    t.getPlayer().sendMessage(event.getEntity().getDisplayName() + " died in your lava");
                }
            }
        }
    }
    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        if(hasMyKit(event.getPlayer()) && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            if(MainHolder.main.isPlayerInMiniArena(event.getPlayer())) {
                if(MainHolder.main.getMiniPvPHandler().inPvpRadius(event.getPlayer())) {
                    placeBucket(event);
                } else {
                    event.setCancelled(true);
                }
            } else {
                if(!PlayerHandler.getInstance().isAtSpawn(event.getPlayer())) {
                    placeBucket(event);
                } else {
                    event.setCancelled(true);
                }
            }
        } else if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @Override
    public boolean isMapSpecificKit() {
        return false;
    }

    private LavaFlowTree getLavaFlowTree(Location location) {
        for(LavaFlowTree t: whosLava) {
            if(t.isAt(location))
                return t;
        }
        return null;
    }

    private List<LavaFlowTree> getLavaTreesOfPlayer(Player player) {
        List<LavaFlowTree> ret = new ArrayList<>();
        for(LavaFlowTree t: whosLava) {
            if(t.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                ret.add(t);
            }
        }
        return ret;
    }

    private class LavaFlowTree implements Listener {
        Block source;
        List<Block> flows;
        Player player;

        public LavaFlowTree(Block source, Player player) {
            this.source = source;
            flows = new ArrayList<>();
            flows.add(source);
            this.player = player;
        }
        public Player getPlayer() {
            return player;
        }

        public boolean isAt(Location location) {
            for(Block b: flows) {
                if(b.getWorld().equals(location.getWorld()) && b.getLocation().distance(location) < 1)
                    return true;
            }
            return false;
        }

        @EventHandler
        public void onBlockFromTo(BlockFromToEvent event) {
            Block block = event.getToBlock();
            if (event.getBlock().getType().equals(Material.LAVA)) {
                if(flows.contains(event.getBlock())) {
                    Location src = source.getLocation().clone();
                    //src.setY(0);
                    Location blck = block.getLocation().clone();
                    //blck.setY(0);

                    if(blck.distance(src) < MAX_FLOW_DISTANCE) {
                        flows.add(block);
                    } else {
                        event.setCancelled(true);
                    }
                }
            } else {
                for(Block b: flows) {
                    if(b.getType().equals(Material.LAVA))
                        return;
                }
                HandlerList.unregisterAll(this);
                whosLava.remove(this);
            }
        }
    }

}
