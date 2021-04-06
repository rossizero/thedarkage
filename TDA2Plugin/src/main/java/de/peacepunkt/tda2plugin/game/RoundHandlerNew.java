package de.peacepunkt.tda2plugin.game;

import java.time.LocalDateTime;
import java.util.*;

import de.peacepunkt.tda2plugin.MainHolder;
import de.peacepunkt.tda2plugin.game.Handlers.EditorHandler;
import de.peacepunkt.tda2plugin.game.Handlers.PlayerHandler;
import de.peacepunkt.tda2plugin.kits.KitHandler;
import de.peacepunkt.tda2plugin.kits.kits.*;
import de.peacepunkt.tda2plugin.persistence.*;
import de.peacepunkt.tda2plugin.persistence.xp.Xp;
import de.peacepunkt.tda2plugin.persistence.xp.XpDaoImpl;
import de.peacepunkt.tda2plugin.stats.PlayerRoundStats;
import de.peacepunkt.tda2plugin.stats.RoundStats;
import de.peacepunkt.tda2plugin.structures.TopThing.TopThing;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.block.data.type.Dispenser;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.block.data.type.Grindstone;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;

import de.peacepunkt.tda2plugin.Main;
import de.peacepunkt.tda2plugin.team.Teem;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class RoundHandlerNew implements Listener{
	RoundNew currentRound;

	List<String> allRoundNames;
	AssistHandler assistHandler;
	Main main;
	PressurePlateActions ppa;
	boolean voteable;
	Map<OfflinePlayer, Integer> voteMap;

	/**
	 * JavaPlugin needed to pass down to flags and catapults to enable their events
	 * @param main
	 */
	public RoundHandlerNew(Main main) {
		this.main = main;
        voteMap = new HashMap<>();
		//main.getServer().getPluginManager().registerEvents(Kits.getInstance(main), main);
		allRoundNames = RoundUtils.getStagedArenas();
		shuffle();
		ppa = new PressurePlateActions(main);
	}

	private void shuffle() {
		Collections.shuffle(allRoundNames);
		if(currentRound != null && allRoundNames.get(0).equals(currentRound.getName())) {
			allRoundNames.remove(0);
			allRoundNames.add(currentRound.getName());
		}
	}

	public String getNextRoundName() {
		if (allRoundNames == null || allRoundNames.size() == 0)
			return null;

		if(currentRound == null || currentRound.getName() == null) { //first round
			return allRoundNames.get(0);
		} else {
			int index = allRoundNames.indexOf(currentRound.getName()) + 1;
			index %= allRoundNames.size();
			return allRoundNames.get(index);
		}
	}

	public List<String> getNextVotingRoundNames() {
		int index = allRoundNames.indexOf(currentRound.getName()) + 1;
		List<String> ret = new ArrayList<>();
		for(int i = 0; i < Main.numVoteMaps; i++) {
			index %= allRoundNames.size();
			ret.add(RoundUtils.getDisplayNameOfMap(allRoundNames.get(index)));
            index ++;
		}
		return ret;
	}

	public void next() {
		if (allRoundNames != null && allRoundNames.size() > 0) {
			assistHandler = new AssistHandler();
			RoundNew old = currentRound;

			String name = getVotedMapName();
			currentRound = new RoundNew(name, this, allRoundNames.indexOf(name));

			if (ppa != null) {
				HandlerList.unregisterAll(ppa);
			}
			ppa = new PressurePlateActions(main);

			if (!currentRound.start()) {
				next();
			}

			if(old != null)
				old.kill();
		}
	}

	public void start() {
		next();
	}

	public void enableVotes() {
		voteable = true;
		voteMap = new HashMap<>();
	}

	public void disableVotes() {
		voteable = false;
		voteMap = new HashMap<>();
	}

	public void vote(OfflinePlayer player, int id) {
		if(voteable && voteMap != null && id < Main.numVoteMaps) {
			if(!voteMap.containsKey(player)) {
				voteMap.put(player, id);
				if(player.getPlayer() != null)
					player.getPlayer().sendMessage(ChatColor.YELLOW + "You voted for: " + ChatColor.GREEN + getNextVotingRoundNames().get(id));
			}
		}
	}

	private String getVotedMapName() {
		int totalVotes = voteMap.size();
		int[] votes = new int[Main.numVoteMaps];
		for(int i : voteMap.values()) {
			votes[i]++;
		}
		int max = 0;
		int maxI = 0;

		for(int i = 0; i < Main.numVoteMaps; i++) {
			if(votes[i] > max) {
				maxI = i;
				max = votes[i];
			}
		}
		int index = 1;
		if(currentRound != null)
            index = allRoundNames.indexOf(currentRound.getName()) + maxI + 1;
		index %= allRoundNames.size();
		int iiii = -1;
		if(currentRound != null)
		    iiii = allRoundNames.indexOf(currentRound.getName());
		System.out.println("Most votes for: " + maxI + " " + "  " + iiii + " " +  index + " " + RoundUtils.getDisplayNameOfMap(allRoundNames.get(index)));
		for(String s : allRoundNames) {
		    System.out.println(s + " " + allRoundNames.indexOf(s));
        }
		return allRoundNames.get(index);
	}

	public void onFinish(RoundNew finisher, boolean save) {
	    //cancel all task to make garbage collection of unloaded world possible
		//TODO kann das wirklich raus?
        //Bukkit.getScheduler().cancelTasks(main);
		//save stuff
		if(save) {
			handleMVPs();
			saveStats();
		} else {
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Round skipped -> Stats won't be saved, sorry guys");
		}
		next();
	}

	private void saveStats() {
		for(Teem t: currentRound.getTeams()) {
			RoundStats rs = t.getRoundStats();
			rs.save();
		}
		//refresh miniarencounter
		MiniarenaCounter.getInstance().refresh();
	}

	private void handleMVPs() {
		for(Player player: Bukkit.getOnlinePlayers()) {
			RoundUtils.printMVPofPlayer(player, main);
		}

		for(Teem t : currentRound.getTeams()) {
			RoundStats rs = t.getRoundStats();
			PlayerRoundStats ps = rs.getCurrentMVP();

			if(ps != null) {
				if (ps.getScore() > 0) {
					OfflinePlayer player = ps.getPlayer();
					Xp xp = new XpDaoImpl().get(player.getUniqueId().toString());
					xp.addXp(50, false);
					if (player.getPlayer() != null)
						player.getPlayer().sendMessage(ChatColor.DARK_RED + "" + 50 + ChatColor.GREEN + " xp has been added to your account for being mvp!");
					new XpDaoImpl().update(xp);
				}
			}
		}
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage("");
	}

	public void skip() {
		onFinish(null, true);
	}

	public void skipTo(String mapName) {
		if(allRoundNames.contains(mapName)) {
			int index = allRoundNames.indexOf(mapName);
			int nextIndex = allRoundNames.indexOf(getNextRoundName());
			Collections.swap(allRoundNames, index, nextIndex);
			onFinish(null, true);
		}
	}

	public RoundNew getRound() {
		return currentRound;
	}


	public AssistHandler getAssistHandler() {
		return assistHandler;
	}
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
    	EditorHandler.getInstance().removeBuilder(event.getPlayer());
		if(currentRound != null) {
			Teem t = currentRound.joinRandomTeam(event.getPlayer(), true);
		}
    	//say hi join team
    	//database stuff
		new BukkitRunnable() {
			@Override
			public void run() {
				event.getPlayer().sendTitle("Welcome " + event.getPlayer().getName(), "Join the discord for bigger rounds.", 10, 70, 10);

				if(Main.warning != null) {
					event.getPlayer().sendMessage(Main.warning);
				}

				PlayerRoundStats rejoined = null;
				PlayerStats tmp = new PlayerStatsDaoImpl().getMyStats(event.getPlayer().getUniqueId().toString());
				//first time welcome message
				if(tmp == null) {
					tmp = new PlayerStats(event.getPlayer());
					new PlayerStatsDaoImpl().add(tmp);
					Bukkit.broadcastMessage("Welcome newbie "  + ChatColor.GREEN + event.getPlayer().getName() + ChatColor.WHITE + " on the server!");
					event.getPlayer().sendMessage("Click a wool block of your team's color to spawn into the arena (You don't have to be in range of the block to click it).");
				}
				Xp xp = new XpDaoImpl().get(event.getPlayer().getUniqueId().toString());
				if(xp != null) {
					event.getPlayer().sendMessage(ChatColor.GREEN + "You have " + ChatColor.DARK_RED + xp.getXp() + ChatColor.GREEN + " Xp.");
				}
			}
		}.runTaskAsynchronously(main);

		new BukkitRunnable() {
            @Override
            public void run() {
                if(event.getPlayer() != null) {
                    event.getPlayer().sendMessage(ChatColor.GREEN + "Join our discord to find out when bigger rounds start: " + Main.discordLink);
                }
            }
        }.runTaskLater(main, 20*55);
	}

	@EventHandler
	public void onEntityPickupItemEvent(EntityPickupItemEvent event) {
		//prevent item pickups other than arrows
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if(!player.getGameMode().equals(GameMode.CREATIVE)) {
				if (!event.getItem().getItemStack().getType().equals(Material.ARROW)) {
					event.setCancelled(true);
					event.getItem().remove();
				}
			}
		}
	}

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
		if(!main.isPlayerInMiniArena(event.getPlayer())) {
			Player p = (Player) event.getPlayer();
			event.setRespawnLocation(currentRound.getTeam(p).spawnLocation());
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				KitHandler.getInstance().restockKit(event.getPlayer(), false, true, true);
			}
		}.runTask(main);
    }

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player whoWasHit = (Player) e.getEntity();
			Player whoHit = (Player) e.getDamager();

			//not in the same team or in MiniArena and both in pvpRadius
			if (!MainHolder.main.getRoundHandler().getRound().getTeam(whoHit).contains(whoWasHit) ||
					(MainHolder.main.isPlayerInMiniArena(whoWasHit)
							&& MainHolder.main.getMiniPvPHandler().inPvpRadius(whoHit)
							&& MainHolder.main.getMiniPvPHandler().inPvpRadius(whoWasHit))) {
				assistHandler.add(whoWasHit, whoHit);
			} else {
				e.setCancelled(true);
			}
		} else if (e.getEntity() instanceof Player && e.getDamager() instanceof Arrow) {
			Arrow a = (Arrow) e.getDamager();
			if(a.getShooter() instanceof Player) {
				Player shooter = (Player) a.getShooter();
				Player hit = (Player) e.getEntity();
				if(main.isPlayerInMiniArena(shooter) && !main.getMiniPvPHandler().inPvpRadius(shooter)) {
					e.setCancelled(true);
					return;
				}
				if(currentRound.getTeam(shooter).contains(hit)) {
                    if(!main.isPlayerInMiniArena(hit)) {
                        e.setCancelled(true); //cancel event if same team
                    } else if(!main.getMiniPvPHandler().inPvpRadius(hit)) {
                        e.setCancelled(true);
                    }
					//e.setCancelled(true); //cancel event if same team
				} else {
                    if(main.isPlayerInMiniArena(hit) && !main.getMiniPvPHandler().inPvpRadius(hit)) {
                        e.setCancelled(true);
                    } else {
                        assistHandler.add(hit, shooter);
						List<MetadataValue> tmp = e.getDamager().getMetadata("autogun");
						MetadataValue value = null;
						if(tmp.size() > 0) {
							value = e.getDamager().getMetadata("autogun").get(0);
						}
						if(value != null) {
							if(value.asString().equals("true")) {
								if(!hit.equals(shooter)) {
									shooter.sendMessage(ChatColor.GREEN + "Hit! ( " + currentRound.getTeam(hit).getChatColor() + hit.getName() + ChatColor.GREEN + " with an autogun arrow)");
								}
							}
						} else {
							long dist = Math.round(hit.getLocation().distance(shooter.getLocation()));
							shooter.sendMessage(ChatColor.GREEN + "Hit " + currentRound.getTeam(hit).getChatColor() + hit.getName() + ChatColor.GREEN + "! (from " + dist + " blocks)");
						}
                    }
				}

			}
		} else if(e.getEntity() instanceof Player && e.getDamager() instanceof AreaEffectCloud){
			AreaEffectCloud cloud = (AreaEffectCloud) e.getDamager();
			if(cloud.getSource() instanceof Player) {
				Player source = (Player) cloud.getSource();
				if(main.isPlayerInMiniArena(source) && (!main.getMiniPvPHandler().inPvpRadius(cloud.getLocation()) || !main.getMiniPvPHandler().inPvpRadius(source))) {
					e.setCancelled(true);
					return;
				} else if (main.isPlayerInMiniArena(source) && main.getMiniPvPHandler().inPvpRadius(cloud.getLocation())) {
					if(e.getEntity().equals(source)) {
						e.setCancelled(true);
					}
				}
				if(main.isPlayerInMiniArena((Player) e.getEntity()) && !main.getMiniPvPHandler().inPvpRadius((Player) e.getEntity())) {
					e.setCancelled(true);
				}

				if(currentRound.getTeam(source).equals(currentRound.getTeam((Player) e.getEntity()))) {
                    if(!main.isPlayerInMiniArena((Player) e.getEntity())) {
                        e.setCancelled(true); //cancel event if same team
                    } else if(!main.getMiniPvPHandler().inPvpRadius((Player) e.getEntity())) {
                        e.setCancelled(true);
                    }
				} else {
                    if(main.isPlayerInMiniArena((Player) e.getEntity()) && !main.getMiniPvPHandler().inPvpRadius((Player) e.getEntity())) {
                        e.setCancelled(true);
                    } else {
                        assistHandler.add((Player) e.getEntity(), source);
                    }
				}
			}
			e.getEntity();
		}
		//prevent armorstand kills
		if(e.getEntity() instanceof ArmorStand) {
			if(e.getDamager() instanceof Player) {
				Player whoHit = (Player) e.getDamager();
				if(!whoHit.isOp()) {
					if(!whoHit.getGameMode().equals(GameMode.CREATIVE)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	List<Player> windplaying = Collections.synchronizedList(new ArrayList<Player>());
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        playWind(event.getPlayer());
        //TODO move in minipvphandler
		if (event.getPlayer().isSneaking() && event.getPlayer().getWorld().getName().equals("miniarena")) {
			for (Location t : main.getMiniPvPHandler().getSpawns()) {
				if (t.getWorld().equals(event.getPlayer().getWorld())) {
					if (t.distance(event.getPlayer().getLocation()) <= 1.3) {
						main.getMiniPvPHandler().removePlayer(event.getPlayer());
					}
				}
			}
		}
    }
    private void playWind(Player player) {
        if(!windplaying.contains(player) && player.getWorld().getName().equals("arena") && !player.isFlying()) {
            if (player.getLocation().getY() > currentRound.windHeight) {
                Block block = player.getLocation().getBlock();
                if (block.getWorld().getHighestBlockYAt(block.getLocation()) <= block.getY()) {
                    player.playSound(player.getLocation(), Sound.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT,  0.4f, 0);
                    windplaying.add(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            windplaying.remove(player);
                            playWind(player); //in case he stands still on a mountain or tower
                        }
                    }.runTaskLater(main, 10 * 20);
                } else {
                    player.stopSound(Sound.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT);
                }
            } else {
                player.stopSound(Sound.ITEM_ELYTRA_FLYING, SoundCategory.AMBIENT);
            }

        }
        //TODO mehr Partikel! -> sowas wie über feuern, asche auf vulkanmap
        player.getWorld().spawnParticle(Particle.WHITE_ASH, player.getLocation(), 20, 20, 20 ,20 );
        player.getWorld().spawnParticle(Particle.ASH, player.getLocation(), 20, 20, 20 ,20);
    }
	//reduce fall damage by 1/3
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof ItemFrame) {
			e.setCancelled(true);
		}
    	if(e.getEntity() instanceof Player && e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
    	    //if(Kits.getKit((Player)e.getEntity()).equals(new KitAcrobat().getName())) {
    	    if(PlayerHandler.getInstance().getKit((Player)e.getEntity()).getKitDescription().getName().equals("Acrobat")) {
    	        e.setCancelled(true);
            }
    		if(ppa.contains((Player)e.getEntity())) {
    			ppa.remove((Player) e.getEntity());
    			e.setCancelled(true);
			} else if(e.getDamage() <= 2.0){
				e.setCancelled(true);
			} else {
				e.setDamage(e.getDamage() - e.getDamage() / 3);
			}
		}
	}

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
    	//prevent farmland destruction
		if(e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null && e.getClickedBlock().getType().equals(Material.FARMLAND)) {
			e.setCancelled(true);
		}

		if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getItem() != null) {
				if (e.getItem().getType().equals(Material.WRITTEN_BOOK)) {
					BookMeta book = (BookMeta) e.getItem().getItemMeta();
					if (book.getAuthor().equals(e.getPlayer().getName())) {
						e.getPlayer().sendMessage(ChatColor.RED + "-");
						e.getPlayer().sendMessage(ChatColor.GREEN + "Good human! You've looked into the book. Not every human does that. Now, human, go out there and pvp the hell out of other humans!");
						e.getPlayer().sendMessage(ChatColor.RED + "-");
						System.out.println(e.getPlayer().getName() + " read the info book! <--------------------------------------");
					}
				}
			}
		}

        if(e.getClickedBlock() != null) {
            Block block = e.getClickedBlock();
            if(block != null) {
                if(block.getState() instanceof Sign) {
                    Player player = e.getPlayer();
                    Sign sign = (Sign) block.getState();
                    if(sign.getLine(0).equals("CLASS")) {
                        KitHandler.getInstance().setKit(player, sign.getLine(2), true, true, true);
                    }
					if(sign.getLine(0).equals("TESTCLASS")) {
						KitHandler.getInstance().setKit(player, sign.getLine(2), false, true, false);
						player.getInventory().setHelmet(new ItemStack(Material.AIR));
					}
                }
            }
			if (e.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
				if(main.isPlayerInMiniArena(e.getPlayer())) {
					//can be used
				} else {
					KitHandler.getInstance().restockKit(e.getPlayer(), true,false, false);
					if (!e.getPlayer().isOp())
						e.setCancelled(true);
				}
			} else if(e.getClickedBlock().getState() instanceof Container //    Barrel, BlastFurnace, BrewingStand, Chest, Dispenser, Dropper, Furnace, Hopper, ShulkerBox, Smoker
					|| e.getClickedBlock().getState() instanceof Jigsaw
					|| e.getClickedBlock().getState() instanceof Grindstone
					|| e.getClickedBlock().getState() instanceof Beacon
					|| e.getClickedBlock().getState() instanceof EnchantingTable
					|| e.getClickedBlock().getState() instanceof EnderChest
					|| e.getClickedBlock().getState() instanceof RespawnAnchor
					|| e.getClickedBlock().getState() instanceof BlockInventoryHolder //    Barrel, BlastFurnace, BrewingStand, Chest, Container, Dispenser, Dropper, Furnace, Hopper, Lectern, ShulkerBox, Smoker
			) {
				e.setCancelled(true);
				if (e.getPlayer().isOp()) {
					e.setCancelled(false);
				} else if (EditorHandler.getInstance().isPlayerInEditMode(e.getPlayer())) {
					if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						e.setCancelled(false);
					}
				}
			}
        }

    }
	@EventHandler
	public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
		if(event.getRightClicked() instanceof ArmorStand) {
			if(!event.getPlayer().isOp()) {
				if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
					event.setCancelled(true);
				}
			}
		}
		if(event.getRightClicked() instanceof Player) {
			Player target = (Player) event.getRightClicked();
			if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {
				if(event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(KitChaos.crowCaller)) {

					if(main.isPlayerInMiniArena(event.getPlayer())) {
						event.getPlayer().getInventory().remove(event.getPlayer().getInventory().getItemInMainHand());
						if(main.getMiniPvPHandler().getSwarm() != null) {
							main.getMiniPvPHandler().getSwarm().setTarget(target, event.getPlayer());
							assistHandler.add(target, event.getPlayer());
							event.getPlayer().updateInventory();
							event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "swarm is on his way..."));
						}
					} else {
						if (currentRound.getSwarm() != null) {
							if(!getRound().getTeam(target).equals(getRound().getTeam(event.getPlayer()))) {
								event.getPlayer().getInventory().remove(event.getPlayer().getInventory().getItemInMainHand());
								main.getRoundHandler().getRound().callSwarm(target, event.getPlayer());
								event.getPlayer().updateInventory();
								assistHandler.add(target, event.getPlayer());
								event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "swarm is on his way..."));
							}
						} else {
							event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_GRAY + "no swarm on this map..."));
						}
					}
				}
			}
		}
	}
	@EventHandler
	public void onBlockFromToEvent(BlockFormEvent event) {
		//prevents Obsidian, Stone, Cobble creation (because kit Chaos has buckets)
		if(event.getBlock().getType().equals(Material.LAVA) || event.getBlock().getType().equals(Material.WATER)) {
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            //prevent block breaking except ladders
            if(!(event.getBlock().getType().equals(Material.LADDER)) && !(event.getBlock().getType().equals(Material.COBWEB)) && !(event.getBlock().getType().equals(Material.FIRE))) {
                event.setCancelled(true);
            } else {
            	if(main.isPlayerInMiniArena(event.getPlayer())) {
					//if (main.getMiniPvPHandler().inBuildRadius(event.getPlayer()) || main.getMiniPvPHandler().inInnerPvpRadius(event.getPlayer())) {
					if (main.getMiniPvPHandler().inPvpRadius(event.getPlayer())) {
						//allow block break
					} else {
						event.setCancelled(true);
					}
				} else {
            		//allow block break
				}
			}
        }
        event.setDropItems(false);
    }
    private boolean isKitPlaceableBlock(Material m) {
	    return m.equals(Material.LADDER) ||
                m.equals(Material.LEVER) ||
                m.equals(Material.ACACIA_PRESSURE_PLATE) ||
                m.equals(Material.COBWEB) ||
				m.equals(Material.CAKE);
    }
	@EventHandler
	public void onBlockPlacedEvent(BlockPlaceEvent event) {
	    if (main.isPlayerInMiniArena(event.getPlayer())) {
	    	if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
	    		if(!main.getMiniPvPHandler().inBuildRadius(event.getBlock().getLocation())) {
	    			if(main.getMiniPvPHandler().inInnerPvpRadius(event.getBlock().getLocation())) {
						if(isKitPlaceableBlock(event.getBlock().getType())) {
							new BukkitRunnable() {

								@Override
								public void run() {
									event.getBlock().setType(Material.AIR);
								}
							}.runTaskLater(main, 30 * 20);
						} else {
							event.setCancelled(true);
						}
					} else {
	    				event.setCancelled(true);
					}
				} else {
					if(isKitPlaceableBlock(event.getBlock().getType())) {
						new BukkitRunnable() {

							@Override
							public void run() {
								event.getBlock().setType(Material.AIR);
							}
						}.runTaskLater(main, 30 * 20);
					} else {
						//if normal block was placed in miniarena
						List<MiniArenaBlocks> ret = new MiniArenaBlocksDaoImpl().get(event.getBlock().getX(), event.getBlock().getZ());
						if(ret != null) {
							if(ret.size() > 0) {
								Player player = Bukkit.getPlayer(UUID.fromString(ret.get(0).getUuid()));
								if(player != null) {
									if (!player.equals(event.getPlayer())) {
										event.getPlayer().sendMessage(ChatColor.GREEN + "This is " + player.getName() + "'s spot...");
										event.setCancelled(true);
									} else {
										new MiniArenaBlocksDaoImpl().add(new MiniArenaBlocks(event.getPlayer(), event.getBlock()));
									}
								} else {
									new MiniArenaBlocksDaoImpl().add(new MiniArenaBlocks(event.getPlayer(), event.getBlock()));
								}
							} else {
								new MiniArenaBlocksDaoImpl().add(new MiniArenaBlocks(event.getPlayer(), event.getBlock()));
							}
						} else {
							new MiniArenaBlocksDaoImpl().add(new MiniArenaBlocks(event.getPlayer(), event.getBlock()));
						}
					}
				}
			}
        } else if(event.getBlock().getWorld().getName().contains("Spawn") && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true); //prevent drop item
    }

    @EventHandler
    public void onInventoryClickEvent (InventoryClickEvent event) {
        if(event.getSlot() == 39) {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(ChatColor.GREEN + "Please continue hiding your face!");
        }
		if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
			if(event.getWhoClicked() instanceof Player) {
				if (event.getCurrentItem() != null) {
					ItemStack kitItems[] = PlayerHandler.getInstance().getKit((Player) event.getWhoClicked()).getKitItemStacks((Player) event.getWhoClicked());// Kits.getKit((Player) event.getWhoClicked(), Kits.getKit((Player) event.getWhoClicked()));
					for(ItemStack tmp : kitItems) {
						if(tmp.getType().equals(event.getCurrentItem().getType())) {

							System.out.println(event.getCurrentItem() + " " + tmp.getType());
							event.setCancelled(true);
							break;
						}
					}
				}
			}
		}
    }

	@EventHandler
	public void onBlockDamageEvent(BlockDamageEvent event) {
    	if(event.getBlock().getType().equals(Material.COBWEB) && !event.getInstaBreak()) {
    		new BukkitRunnable() {
				@Override
				public void run() {
					event.getBlock().setType(Material.AIR);
				}
			}.runTaskLater(main, 16);
		}
	}

    //add a death if players leaves the game out of the arena
    @EventHandler
    public void onPlayerLeaveEvent(PlayerQuitEvent event) {
        if(event.getPlayer().getWorld().getName().equals("arena")) {
			List<Player> assisters = assistHandler.get(event.getPlayer());
			Teem killedTeem = currentRound.getTeam(event.getPlayer());
			Player lastPlayer = null;
			Teem lastPlayerTeem = null;

			if (assisters != null) {
				if(assisters.size() != 0) { //if he has been it recently and rage quited
					for (Player p : assisters) {
						//last player in assist gets the kill
						Teem pTeem = currentRound.getTeam(p);
						if (!(assisters.indexOf(p) == assisters.size() - 1)) {
							pTeem.getRoundStats().addAssist(p, PlayerHandler.getInstance().getKit(event.getPlayer()).getKitDescription().getName());
							if(!main.isPlayerInMiniArena(event.getPlayer())) {
								p.sendMessage("You assisted killing " + killedTeem.getChatColor() + event.getPlayer().getName());
							} else {
								p.sendMessage(ChatColor.GRAY + "[Mini-Arena] You assisted killing " + event.getPlayer().getName());
							}

						} else {
							lastPlayer = p;
							lastPlayerTeem = pTeem;
							pTeem.getRoundStats().addKill(p, PlayerHandler.getInstance().getKit(event.getPlayer()).getKitDescription().getName());
						}
					}
					killedTeem.getRoundStats().addDeath(event.getPlayer(), PlayerHandler.getInstance().getKit(event.getPlayer()).getKitDescription().getName());
					if (lastPlayer != null) {
						Bukkit.broadcastMessage(lastPlayerTeem.getChatColor() + lastPlayer.getName() + ChatColor.WHITE + " killed " + killedTeem.getChatColor() + event.getPlayer().getName() + ChatColor.WHITE + " because he rage quited!");
					}
				}
			}
        } else {
        	//close editor in case player is in editmode
			//TODO new could be stupid
        	EditorHandler.getInstance().leaveEditor(event.getPlayer());
		}

		currentRound.leave(event.getPlayer());
    }
	@EventHandler
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		if(!event.getPlayer().isOp()) {
			event.setCanCreatePortal(false);
			event.setCancelled(true);
		}
	}

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        event.getDrops().clear();
        event.setDeathMessage(null);

        Player killer = event.getEntity().getKiller();
        Player killed = event.getEntity();
        boolean edit = false;
        if(EditorHandler.getInstance().isPlayerInEditMode(killed)) {
			EditorHandler.getInstance().leaveEditor(killed);
		} else if(main.isPlayerInMiniArena(killed)) {
			MiniarenaCounter.getInstance().addKill();
            if(killer != null) {
                killer.sendMessage("You killed " + killed.getName());
                killed.sendMessage("You were killed by " + killer.getName());
				checkForBubiKill(event, killer);
                //e.setDeathMessage(killerTeem.getChatColor() + killer.getName() + ChatColor.WHITE + " killed " +  ChatColor.WHITE + killedTeem.getChatColor() + killed.getName());
            } else { //death for other reason
                List<Player> assisters = assistHandler.get(killed);
                Player lastPlayer = null;

                if (assisters != null) {
                    for (Player p : assisters) {
                        Teem pTeem = currentRound.getTeam(p);
                        if (!(assisters.indexOf(p) == assisters.size() - 1)) {
                        } else {
                            lastPlayer = p;
                        }
                    }
                }

                if (lastPlayer != null) {
                    lastPlayer.sendMessage("You killed " + killed.getName());
					checkForBubiKill(event, lastPlayer);
                    killed.sendMessage("You were killed by " + lastPlayer.getName());
                    killer = lastPlayer;
                }
            }

            if(killer != null) {
            	ItemStack b = main.getMiniPvPHandler().getBuildBlock();
				HashMap<Integer,ItemStack> map = killer.getEnderChest().addItem(b);
				System.out.println(killer.getName() + " got following block: " + b.getType());
				if(map.size() == 0) {
					killer.sendMessage(ChatColor.GRAY + "You got following block in your enderchest: " + b.getType());
				} else {
					for(Integer is : map.keySet()) {
						killer.sendMessage(ChatColor.GRAY + "Your enderchest was full, so you got following block in your inventory: " + b.getType());
						killer.getInventory().addItem(map.get(is));
					}
				}
                killer.updateInventory();
            }
        } else {
            Teem killerTeem = currentRound.getTeam(killer);
            Teem killedTeem = currentRound.getTeam(killed);

            List<Player> assisters = assistHandler.get(killed);
            if(killer != null) {
                int currKills = killerTeem.getRoundStats().addKill(killer, PlayerHandler.getInstance().getKit(killer).getKitDescription().getName());//AbstractKitSuperclass.subclasses.get(Kits.getKit(killer)));
                killedTeem.getRoundStats().addDeath(killed, PlayerHandler.getInstance().getKit(killed).getKitDescription().getName());
				checkForBubiKill(event, killer);
                killer.sendMessage("You killed " + killedTeem.getChatColor() + killed.getName() +  ChatColor.RESET +  " (" + currKills + ")");
                killed.sendMessage("You were killed by " + killerTeem.getChatColor() + killer.getName());
                //e.setDeathMessage(killerTeem.getChatColor() + killer.getName() + ChatColor.WHITE + " killed " +  ChatColor.WHITE + killedTeem.getChatColor() + killed.getName());


                if (assisters != null) {
                    for (Player p : assisters) {
                        if (!p.equals(killer)) {
                            Teem pTeem = currentRound.getTeam(p);
                            //NPE???!!!!TODO
                            pTeem.getRoundStats().addAssist(p, PlayerHandler.getInstance().getKit(p).getKitDescription().getName());
                            p.sendMessage("You assisted killing " + killedTeem.getChatColor() + killed.getName());
                        }
                    }
                }
            } else { //death for other reason
                killedTeem.getRoundStats().addDeath(killed, PlayerHandler.getInstance().getKit(killed).getKitDescription().getName());

                Player lastPlayer = null;
                Teem lastPlayerTeem = null;
                if (assisters != null) {
                	if(assisters.size() > 1) {
						for (Player p : assisters) {
							//last player in assist gets the kill
							Teem pTeem = currentRound.getTeam(p);
							if (!(assisters.indexOf(p) == assisters.size() - 1)) {
								pTeem.getRoundStats().addAssist(p, PlayerHandler.getInstance().getKit(p).getKitDescription().getName());
								p.sendMessage("You assisted killing " + killedTeem.getChatColor() + killed.getName());
							} else {
								lastPlayer = p;
								lastPlayerTeem = pTeem;
							}
						}
					} else if(assisters.size() == 1) {
                		lastPlayer = assisters.get(0);
                		lastPlayerTeem = currentRound.getTeam(assisters.get(0));
					}
                }
                if (lastPlayer != null) {
					int currKills = lastPlayerTeem.getRoundStats().addKill(lastPlayer, PlayerHandler.getInstance().getKit(lastPlayer).getKitDescription().getName());
					checkForBubiKill(event, lastPlayer);
                    lastPlayer.sendMessage("You killed " + killedTeem.getChatColor() + killed.getName() + ChatColor.RESET +  " (" + currKills + ")");
                    killed.sendMessage("You were killed by " + lastPlayerTeem.getChatColor() + lastPlayer.getName());
                }
            }
        }

        assistHandler.remove(killed);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable()
        {
            public void run()
            {
                event.getEntity().spigot().respawn();
            }

        }, (3));

        if(killed != null) {
        	deathParticle(killed);
		}
    }

	private void checkForBubiKill(PlayerDeathEvent event, Player killer) {
    	if(event.getEntity().getUniqueId().equals(UUID.fromString("6d751c22-45b4-4e92-82fc-f0723fac7939"))) {
    	//if(event.getEntity().getUniqueId().equals(UUID.fromString("c3f79bf5-6018-46f3-bc79-c0a3a157e9a7"))) {
    		if(killer != null) {
    			new BukkitRunnable() {
					@Override
					public void run() {
						new BubiCounterDaoImpl().increase(killer);
					}
				}.runTaskAsynchronously(main);
			}
		}
	}
    private void deathParticle(Player player) {
		Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
		player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 555, 0.3, 0.7, 0.3, 0.2, dustOptions);
		player.getWorld().spawnParticle(Particle.LANDING_LAVA, player.getLocation(), 100, 0.5, 0.5, 0.5);
		if(player.isOp()) {
			dustOptions = new Particle.DustOptions(Color.LIME, 1);
			player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 555, 0.2, 0.5, 0.2, 0.1, dustOptions);
		}
	}

	@EventHandler
	public void onEntityShootBowEvent(EntityShootBowEvent event) {
		if(event.getProjectile() instanceof Arrow) {
			if (event.getEntity() instanceof Player) {
				if(event.getBow().getItemMeta() != null) {
					if(event.getBow().getItemMeta().getDisplayName().equals(KitSticker.honeyBow)) {
						event.getProjectile().setMetadata("bowType", KitSticker.honeyBowMeta);
						System.out.println(event.getProjectile().getMetadata("bowType"));
					} else if (event.getBow().getItemMeta().getDisplayName().equals(KitSticker.stelzenBow)) {
						event.getProjectile().setMetadata("bowType", KitSticker.stelzenBowMeta);
					}
				}
			}
		}
	}
	@EventHandler
	public void onProjectileHitEvent(ProjectileLaunchEvent event) {
		if(event.getEntity().getWorld().getName().contains("Spawn")) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		event.getDrops().clear();//no drops
		LivingEntity mob = event.getEntity();
		if(!(mob instanceof Player) && !(mob instanceof Bat)) {
			if (mob.getKiller() instanceof Player) {
				Player player = (Player) mob.getKiller();
				if(player.getWorld().equals(Bukkit.getWorld("arena"))) {
					Teem pTeem = currentRound.getTeam(player);
					pTeem.getRoundStats().addUselessKill(player);
					/*for (int i = 0; i < 10; i++) {
						pTeem.getRoundStats().addDeath(player, AbstractKitSuperclass.subclasses.get(Kits.getKit(player)));
					}*/
					player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "This was a useless act of violence!");
					BukkitRunnable r = new BukkitRunnable() {
						@Override
						public void run() {
							player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "Lightning in...");
							player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "3");
							player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "2");
							player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "1");
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
							player.getWorld().strikeLightning(player.getLocation());
						}
					};
					r.runTaskLater(main, 44);
				} else if (player.getWorld().equals(Bukkit.getWorld("miniarena"))) {
					if(event.getEntity() instanceof Pig) {
						MiniarenaCounter.getInstance().addPig();
					}
				}
			}
		}
	}

	@EventHandler
	public void explode(EntityExplodeEvent e) {
		//System.out.println(e.getEntity().getName());
		e.setYield(0); //no drops
		if(e.getEntity() instanceof ItemFrame) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void explode(BlockExplodeEvent e) {
		e.setYield(0);//no drops
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onHangingBreak(HangingBreakEvent event) {
		if (event.getCause() == HangingBreakEvent.RemoveCause.EXPLOSION) {
			if(event.getEntity().getType().equals(EntityType.ITEM_FRAME)) {
				event.setCancelled(true);
			}
		}
	}
	public void onEntityExplodeEvent(EntityExplodeEvent event)
	{
		if (event.getEntity() instanceof WitherSkull || event.getEntity() instanceof Wither)
		{
			event.setCancelled(true);
		}
	}

}
