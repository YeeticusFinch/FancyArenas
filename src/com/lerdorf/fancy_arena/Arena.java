package com.lerdorf.fancy_arena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Hangable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
//import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

//import com.lerdorf.fancy_plugin_1_19.Door;

/*
import com.google.gson.Gson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
*/

import net.md_5.bungee.api.ChatColor;

public class Arena implements CommandExecutor {
	
	//public static HashMap<String, ArmorStand> gm = null;
	public static HashMap<String, List<FancyLevel>> levels = new HashMap<String, List<FancyLevel>>();
	public static HashMap<String, Dialogs> dialogs = new HashMap<String, Dialogs>();
	public static HashMap<String, HashMap<Integer, Cutscene>> cutscenes = new HashMap<String, HashMap<Integer, Cutscene>>();
	
	public static HashMap<String, Integer> currentLevel = new HashMap<String, Integer>();;
	public static HashMap<String, List<LivingEntity>> arenaEntities = new HashMap<String, List<LivingEntity>>();
	public static HashMap<String, List<String>> lootedChests = new HashMap<String, List<String>>();
	public static HashMap<LivingEntity, BossBar> bossBars = new HashMap<LivingEntity, BossBar>();
	public static ArrayList<String> runningArenas = new ArrayList<String>();
	public static HashMap<String, Integer> levelStage = new HashMap<String, Integer>();
	public static HashMap<String, Long> lastCC = new HashMap<String, Long>();
	public static HashMap<String, Integer> lives = new HashMap<String, Integer>();
	public static HashMap<String, Integer> messageIndex = new HashMap<String, Integer>();
	public static HashMap<Player, Collection<PotionEffect>> playerEffects = new HashMap<Player, Collection<PotionEffect>>();
	public static HashMap<String, List<String>> finalCommands = new HashMap<String, List<String>>();
	public static HashMap<String, Integer> levelTicker = new HashMap<String, Integer>();

	public static int bankGold = 0;
	public static ArrayList<ItemStack> bankChestplates = new ArrayList<>();
	public static ArrayList<ItemStack> bankLeggings = new ArrayList<>();
	public static ArrayList<ItemStack> bankBoots = new ArrayList<>();
	public static ArrayList<ItemStack> bankRangedWeapons = new ArrayList<>();
	public static ArrayList<ItemStack> bankMeleeWeapons = new ArrayList<>();
	
	public static String shopLoc = null;
	
	public static String filepath = "plugins/FancyArena/";
	public static String filepath_backup = "plugins/FancyArenaBackup/";
	public static String filename = "FancyArenaLevels.json";
	public static String filename2 = "FancyArenaDialogs.json";
	public static String filename3 = "FancyArenaCommands.json";
	public static String filename4 = "FancyArenaCutscenes.json";
	public static String filename5 = "FancyArenaShop.json";
	
	public static HashMap<BukkitTask, Long> tasks = new HashMap<>();
	public static ArrayList<BukkitTask> bigTasks = new ArrayList<>();
	
	public static HashMap<String, Boolean> levelDone = new HashMap<String, Boolean>();
	
	public static HashMap<String, ArmorStand> fancyMobTemplates = new HashMap<>();
	
	public static Mercenary[] mercenaries = new Mercenary[] {
		new Mercenary("Supreme Mercinary", 9*9*6, false), // 6 gold blocks
		new Mercenary("Mercinary Brute", 9*9*4, true), // 4 gold blocks
		new Mercenary("Greater Mercinary", 9*9, false), // 1 gold block
		new Mercenary("Mercinary", 9*5, false), // 5 gold ingots
		new Mercenary("Lesser Mercinary", 10, false), // slightly over 1 gold ingot
	};
	
	public static void Init() {
		//gm = new HashMap<String, ArmorStand>();
		currentLevel = new HashMap<String, Integer>();
		levels = new HashMap<String, List<FancyLevel>>();
		arenaEntities = new HashMap<String, List<LivingEntity>>();
		
		load();
	}
	
	public static String[] commands = new String[] {
		"help",
		"shop <biome> <profession> <name>",
		"max_lives <number>",
		"min_boss_health <number>",
		"start <arena_name>",
		"list",
		"ready <arena_name>",
		"stop <arena_name>",
		"copy",
		"create <arena_name>",
		"info <arena_name>",
		"setup <arena_name>",
		"done",
		"clearfire <radius>",
		"max_monsters <number>",
		"name <text>",
		"next_delay <number>",
		"start_delay <number>",
		"add_begin_cmd <command>",
		"add_end_cmd <command>",
		"new_char <name> <name_color> <text_color>",
		"start_chat <number (ticks)> <name> <message>",
		"end_chat <number (ticks)> <name> <message>",
		"rand_chat <number (ticks)> <name> <message>",
		"clear_start_chat",
		"clear_rand_chat",
		"clear_end_chat",
		"clear_begin_cmd",
		"clear_end_cmd",
		"clear_monsters",
		"clear_monster_spawn",
		"clear_chest_loot",
	};
	
	/*
	public void spawnGiantSkeletonPair(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // Spawn the skeleton
        Skeleton skeleton = (Skeleton) world.spawnEntity(location, EntityType.SKELETON);
        skeleton.setInvisible(true);
        skeleton.setInvulnerable(true);

        // Spawn the giant
        Giant giant = (Giant) world.spawnEntity(location, EntityType.GIANT);
        giant.getEquipment().setArmorContents(skeleton.getEquipment().getArmorContents());
        giant.getEquipment().setItemInMainHand(skeleton.getEquipment().getItemInMainHand());

        // Synchronize movement and actions
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!skeleton.isValid() || !giant.isValid()) {
                    skeleton.remove();
                    giant.remove();
                    cancel();
                    return;
                }

                // Move giant to skeleton's position
                Location skeletonLocation = skeleton.getLocation();
                Location giantLocation = giant.getLocation();

                giant.teleport(new Location(
                    skeletonLocation.getWorld(),
                    skeletonLocation.getX(),
                    skeletonLocation.getY() - 10, // Adjust for giant's height
                    skeletonLocation.getZ(),
                    skeletonLocation.getYaw(),
                    skeletonLocation.getPitch()
                ));

                // Make the giant look where the skeleton looks
                giantLocation.setDirection(skeletonLocation.getDirection());
            }
        }.runTaskTimer(FancyArena.instance, 0L, 1L);
    }
    */
	
	public static boolean within(Location l, Location p1, Location p2) {
		
		double minX = Math.min(p1.getX(), p2.getX());
		double minY = Math.min(p1.getY(), p2.getY());
		double minZ = Math.min(p1.getZ(), p2.getZ());

		double maxX = Math.max(p1.getX(), p2.getX());
		double maxY = Math.max(p1.getY(), p2.getY());
		double maxZ = Math.max(p1.getZ(), p2.getZ());
		
		if (l.getWorld().getName().equals(p1.getWorld().getName())) {
			if (l.getX() > minX && l.getX() < maxX) {
				if (l.getY() > minY && l.getY() < maxY) {
					if (l.getZ() > minZ && l.getZ() < maxZ) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	static void upgradeGear(LivingEntity le, boolean meleeOnly) {
		// Weapon
		boolean chose = false;
		ItemStack chosenItem = le.getEquipment().getItemInMainHand();
		if (chosenItem.hasItemMeta() && chosenItem.getItemMeta().hasDisplayName() && chosenItem.getItemMeta().getDisplayName().contains("Invisible bow")) {
			chosenItem = le.getEquipment().getItemInOffHand();
		}
		if (meleeOnly || Math.random() > 0.5f) {
			for (ItemStack item : bankMeleeWeapons) {
				if (ItemHelper.isBetterMeleeWeapon(item, chosenItem)) {
					chosenItem = item;
					chose = true;
				}
				if (chose) {
					bankMeleeWeapons.remove(bankMeleeWeapons.indexOf(chosenItem));
				}
			}
		} else {
			for (ItemStack item : bankRangedWeapons) {
				if (ItemHelper.isBetterRangedWeapon(item, chosenItem)) {
					chosenItem = item;
					chose = true;
				}
			}
			if (chose) {
				bankRangedWeapons.remove(bankRangedWeapons.indexOf(chosenItem));
			}
		}
		if (chose) {
			FancyMob.SetHandItems(le, chosenItem, null);
		}
		
		// Chestplate
		chosenItem = le.getEquipment().getChestplate();
		chose = false;
		for (ItemStack item : bankChestplates) {
			if (ItemHelper.isBetterArmor(item, chosenItem)) {
				chosenItem = item;
				chose = true;
			}
		}
		if (chose) {
			bankChestplates.remove(bankChestplates.indexOf(chosenItem));
			le.getEquipment().setChestplate(chosenItem);
		}
		
		// Leggings
		chosenItem = le.getEquipment().getLeggings();
		chose = false;
		for (ItemStack item : bankLeggings) {
			if (ItemHelper.isBetterArmor(item, chosenItem)) {
				chosenItem = item;
				chose = true;
			}
		}
		if (chose) {
			bankLeggings.remove(bankLeggings.indexOf(chosenItem));
			le.getEquipment().setLeggings(chosenItem);
		}
		
		// Boots
		chosenItem = le.getEquipment().getBoots();
		chose = false;
		for (ItemStack item : bankBoots) {
			if (ItemHelper.isBetterArmor(item, chosenItem)) {
				chosenItem = item;
				chose = true;
			}
		}
		if (chose) {
			bankBoots.remove(bankBoots.indexOf(chosenItem));
			le.getEquipment().setBoots(chosenItem);
		}
	}
	
	static boolean cmdRan = false;
	static long cc = 0;
	public static void arenaTick() {
		if (cc % 2 == 0) {
			if (runningArenas.size() > 0) {
				for (String arenaName : runningArenas) {
					if (levelStage.get(arenaName) == 0) {
						if (cutscenes.containsKey(arenaName) && cutscenes.get(arenaName).containsKey(currentLevel.get(arenaName))) {
							boolean cutscene = cutscenes.get(arenaName).get(currentLevel.get(arenaName)).Step(1);
							if (!cutscene) {
								cutscenes.get(arenaName).get(currentLevel.get(arenaName)).reset();
								lastCC.put(arenaName, -1l);
								levelStage.put(arenaName, levelStage.get(arenaName)+1);
							}
						} else {
							lastCC.put(arenaName, -1l);
							levelStage.put(arenaName, levelStage.get(arenaName)+1);
						}
						break;
					}
				}
			}
		}
		if (cc % 10 == 0) {
			if (cc % 100 == 0) {
				if (tasks.size() > 0) {
					ArrayList<BukkitTask> remove = new ArrayList<>();
					for (BukkitTask task : tasks.keySet()) {
						if (System.currentTimeMillis() - tasks.get(task) > 1000*30) {
							remove.add(task);
						}
					}
					for (BukkitTask task : remove) {
						if (task != null) {
							if (!task.isCancelled()) {
								Bukkit.broadcastMessage(ChatColor.GRAY + "Canceling hanging task");
								task.cancel();
							}
						}
						Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
				        	if (task == null || task.isCancelled()) {
				        		Bukkit.broadcastMessage(ChatColor.GRAY + "Deleting hanging task");
				        		tasks.remove(task);
				        	}
				        }, 5);
					}
				}
			}
			if (bossBars.size() > 0) {
				ArrayList<LivingEntity> deadBosses = new ArrayList<LivingEntity>();
				for (LivingEntity le : bossBars.keySet()) {
					BossBar bossBar = bossBars.get(le);
					if (!le.isDead()) {
				        bossBar.setProgress(le.getHealth() / le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
				    } else {
				    	deadBosses.add(le);
				        List<Player> players = bossBar.getPlayers();
				        for (Player player : players) {
				           bossBar.removePlayer(player);
				        }
				        bossBar.setVisible(false);
				    }
				}
				for (LivingEntity le : deadBosses) {
					bossBars.remove(le);
				}
			}
			
			//Bukkit.broadcastMessage("Arena tick");
			if (runningArenas.size() > 0) {
				for (String arenaName : runningArenas) {

					//Bukkit.broadcastMessage("Ticking " + arenaName);
					if (!levelStage.containsKey(arenaName)) {
						levelStage.put(arenaName, 0);
					}
					if (!lastCC.containsKey(arenaName)) {
						lastCC.put(arenaName, -1l);
					}
					

					//Bukkit.broadcastMessage("Level Stage: " + levelStage.get(arenaName));

					switch (levelStage.get(arenaName)) {
						case 0: // Cutscenee
						{
							if (currentLevel.get(arenaName) == 0 && !cmdRan) { // Run the commands here if it's level 0
								cmdRan = true; // just so it doesnt run the commandsagain during cutscene
								levelDone.put(arenaName, false);
								FancyLevel lvl = getCurrentLevel(arenaName);
								try {
									if (lvl.getBeginCommands() != null && lvl.getBeginCommands().size() > 0) {
										Player p = null;
										for (Player player : Bukkit.getOnlinePlayers()) {
											if (player.getScoreboardTags().contains("Ready_" + arenaName)) {
												p = player;
												break;
											}
										}
										for (String cmd : lvl.getBeginCommands()) {
											if (cmd != null && cmd.length() > 0)
												arenaCMD(cmd, p);
												//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + p.getName() + " at @s run " + cmd);
										}
									}
								} catch (Exception e) {
									Bukkit.broadcastMessage(ChatColor.RED + "Failed to execute start commands");
								}

							}
							break;
						}
						case 1: // Wait for wave start delay
							levelDone.put(arenaName, false);
							if (lastCC.get(arenaName) < 0) { // Timer not started
								lastCC.put(arenaName, cc);
								Bukkit.broadcastMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Level starting in " + getCurrentLevel(arenaName).getWaveStartDelay()/20 + " seconds");
							} else if (cc-lastCC.get(arenaName) > getCurrentLevel(arenaName).getWaveStartDelay()) {
								lastCC.put(arenaName, -1l);
								levelStage.put(arenaName, levelStage.get(arenaName)+1);
							}
							break;
						case 2: // Check for all players in area
						{
							cmdRan = false;
							levelDone.put(arenaName, false);
							FancyLevel lvl = getCurrentLevel(arenaName);
							boolean allPlayersHere = true;
							for (World w : Bukkit.getWorlds()) {
								for (Player p : w.getPlayers()) {
									if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
										if (within(p.getLocation(), lvl.getMinPosLoc(), lvl.getMaxPosLoc())) {
											
										} else {
											allPlayersHere = false;
											if (Math.random() < 0.01)
												p.sendMessage(ChatColor.YELLOW + "" + ChatColor.ITALIC + "You must make your way to the new location before the next wave can start");
										}
									}
								}
							}
							if (allPlayersHere) {

								levelStage.put(arenaName, levelStage.get(arenaName)+1);
							} else if (Math.random() < 0.01) {
								Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Waiting to proceed to next level, not all players are in the level's area");
							}
							break;
						}
						case 3: // Begin Commands
						{
							if (currentLevel.get(arenaName) > 0) { // Don't run the commands here if it's level 0
								levelDone.put(arenaName, false);
								FancyLevel lvl = getCurrentLevel(arenaName);
								try {
									if (lvl.getBeginCommands() != null && lvl.getBeginCommands().size() > 0) {
										Player p = null;
										for (Player player : Bukkit.getOnlinePlayers()) {
											if (player.getScoreboardTags().contains("Ready_" + arenaName)) {
												p = player;
												break;
											}
										}
										for (String cmd : lvl.getBeginCommands()) {
											if (cmd != null && cmd.length() > 0)
												arenaCMD(cmd, p);
												//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + p.getName() + " at @s run " + cmd);
										}
									}
								} catch (Exception e) {
									Bukkit.broadcastMessage(ChatColor.RED + "Failed to execute start commands");
								}

							}
							levelStage.put(arenaName, levelStage.get(arenaName)+1);
							messageIndex.put(arenaName, 0);
							break;
						}
						case 4: // Start Chat Messages
						{
							levelDone.put(arenaName, false);
							if (!dialogs.containsKey(arenaName))
							{
								lastCC.put(arenaName, -1l);
								levelStage.put(arenaName, levelStage.get(arenaName)+1);
								messageIndex.put(arenaName, 0);
							}
							else if (lastCC.get(arenaName) < 0 || lastCC.get(arenaName) < cc) { // Timer not started
								ChatMessage msg = dialogs.get(arenaName).getStartMessage(currentLevel.get(arenaName), messageIndex.get(arenaName));
								messageIndex.put(arenaName, messageIndex.get(arenaName)+1);
								if (msg == null) {
									lastCC.put(arenaName, -1l);
									levelStage.put(arenaName, levelStage.get(arenaName)+1);
									messageIndex.put(arenaName, 0);
								} else {
									Player player = null;
										for (Player p : Bukkit.getOnlinePlayers()) {
											if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
												if (player == null)
													player = p;
												else if (Math.random() > 0.3)
													player = p;
											}
										}
									
									
									String str = dialogs.get(arenaName).constructMessage(msg, player.getName(), player.getName());
									
									Bukkit.broadcastMessage(str);
									
									lastCC.put(arenaName, cc + msg.tickDuration);
								}
								//Bukkit.broadcastMessage("Level starting in " + getCurrentLevel(arenaName).getWaveStartDelay()/20 + " seconds");
							}
							break;
						}
						case 5: // Spawn Monsters
						{
							levelDone.put(arenaName, false);
							FancyLevel lvl = getCurrentLevel(arenaName);
							int lvlNumber = currentLevel.get(arenaName);
							levelStage.put(arenaName, levelStage.get(arenaName)+1);
							levelTicker.put(arenaName, 0); // Reset level ticker
							Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + "Starting Level " + lvlNumber + ": " + ChatColor.LIGHT_PURPLE + lvl.getLevelName());
							
							for (World w : Bukkit.getWorlds()) {
								for (Player p : w.getPlayers()) {
									if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
										p.sendTitle(ChatColor.LIGHT_PURPLE + lvl.getLevelName(), arenaName + " level " + lvlNumber + ", " + lives.get(p.getName()) + " lives left", 1, 40, 5);
									}
								}
							}
							
							lvl.spawnWave();
							
							try {
								if (mercenariesAllowed(lvlNumber)) {
									int minPrice = 9;
									int numMercenaries = 0;
									int cc = 0;
									while (bankGold >= minPrice) {
										cc++;
										if (cc > 100)
											break;
										for (Mercenary m : mercenaries) {
											try {
												if (m.price < bankGold && Math.random() < 0.6f) {
													// Spawn this mercenary, prioritizing the most expensive ones
													bankGold -= m.price;
													String location = lvl.getMonsterSpawn().get((int)(Math.random()*lvl.getMonsterSpawn().size()));
													Location loc = FancyLevel.stringToLoc(location);
													Entity entity = FancyMob.Spawn(loc, m.name);
													if (entity == null) {
														EntityType entityType = EntityType.valueOf(m.name.toUpperCase());
														
														entity = loc.getWorld().spawnEntity(loc, entityType);
													}
													if (entity != null && entity instanceof LivingEntity le) {
														if (Arena.arenaEntities.get(arenaName) == null) {
															Arena.arenaEntities.put(arenaName, new ArrayList<LivingEntity>());
														}
														entity.addScoreboardTag("Arena_" + arenaName);
														entity.addScoreboardTag("ArenaMob");
														Arena.arenaEntities.get(arenaName).add(le);
														upgradeGear(le, m.meleeOnly);
													}
													numMercenaries++;
													break;
												}
											} catch (Exception e) {
												
											}
										}
										if (Math.random() < 0.05f)
											break;
									}
									if (numMercenaries > 0) {
										Bukkit.broadcastMessage(ChatColor.YELLOW + "The empire hired " + numMercenaries + " mercenaries to kill you!");
									}
								}
							} catch (Exception e) {
								
							}
							
							
							break;
						}
						case 6: // Wait for monsters to die (or for players to die)
						{
							levelTicker.put(arenaName, levelTicker.get(arenaName)+1); // This increases twice per second
							FancyLevel lvl = getCurrentLevel(arenaName);
							int aliveEntities = 0;
							if (arenaEntities != null && arenaEntities.get(arenaName) != null) {
								for (int i = 0; i < arenaEntities.get(arenaName).size(); i++) {
									LivingEntity le = arenaEntities.get(arenaName).get(i);
									boolean isBoss = le.getScoreboardTags().contains("FancyBoss");
									if (le != null && !within(le.getLocation(), lvl.getMinPosLoc(), lvl.getMaxPosLoc())) {
										if (le.isSwimming() || le.isInWater() || le.getLocation().getBlock().getType() == Material.WATER) {
											le.damage(5);
											if (Math.random() < 0.1)
												le.damage(500);
										}
										if (levelTicker.get(arenaName) > 300 && Math.random() < 0.4) {
											// After 5 minutes, the arena will start teleporting monsters back into the arena area
											levelTicker.put(arenaName, 0);
											Location loc = lvl.getMonsterSpawnLoc((int)(Math.random() * lvl.getMonsterSpawn().size()));
											/*if (le.getPassengers().size() > 0)
												le.removePassenger(le.getPassengers().get(0));
												*/
											FancyArena.prepare_teleport(le);
											le.teleport(loc);
										}
									}
									if (le == null || le.getHealth() <= 0.0001 || le.isDead()) {
										arenaEntities.get(arenaName).remove(i);
									} else if (lvl.getMinBossHealth() > 0 && isBoss && le.getHealth() <= lvl.getMinBossHealth()+1) {
										// This boss is below minimum health
										levelDone.put(arenaName, true);
										le.remove();
									} else {
										// Entity is still alive
										aliveEntities++;
										if (isBoss) {
											//Bukkit.broadcastMessage("Boss is alive");
											aliveEntities += 10;
											if (Math.random() < 0.05) {
												//Bukkit.broadcastMessage("Trying to chat");
												if (dialogs.containsKey(arenaName) && dialogs.get(arenaName).randomMessages.containsKey(currentLevel.get(arenaName))) {
													String bossName = ChatColor.stripColor(le.getCustomName().replace(' ', '_'));
													String chatName = "";
													for (String name : dialogs.get(arenaName).randomMessages.get(currentLevel.get(arenaName)).keySet()) {
														if (similarity(bossName, name) > 0.5 && similarity(bossName, name) > similarity(bossName, chatName)) {
															chatName = bossName;
														}
													}
													//Bukkit.broadcastMessage("Searching for message of name " + chatName);
													ChatMessage msg = dialogs.get(arenaName).getRandomMessage(currentLevel.get(arenaName), chatName);
													if (msg == null) {
														msg = dialogs.get(arenaName).getRandomMessage(currentLevel.get(arenaName), ChatColor.stripColor(le.getCustomName().replace(" ", "")));
													}
													if (msg != null) {
														double dist = 100000000;
														Player target = null;
														Player player = null;
															for (Player p : Bukkit.getOnlinePlayers()) {
																if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
																	if (player == null)
																		player = p;
																	else if (Math.random() > 0.3)
																		player = p;
																	double newDist = Math.pow(p.getLocation().getX() - le.getLocation().getX(), 2) + Math.pow(p.getLocation().getY() - le.getLocation().getY(), 2) + Math.pow(p.getLocation().getZ() - le.getLocation().getZ(), 2);
																	if (newDist < dist || target == null) {
																		dist = newDist;
																		target = p;
																	}
																}
															}
														Bukkit.broadcastMessage(dialogs.get(arenaName).constructMessage(msg, player.getName(), target.getName()));
													}
												}
											}
										}
									}
								}
							}
							if (levelDone.get(arenaName) || (aliveEntities <= lvl.getMaxMonstersAlive() && arenaEntities.get(arenaName).size() <= lvl.getMaxMonstersAlive())) {
								Player prevPlayer = null;
								boolean bossAlive = false;
								for (Player p : Bukkit.getOnlinePlayers()) {
									if (prevPlayer != null && prevPlayer.getWorld() == p.getWorld())
										continue;
									for (Entity e : p.getWorld().getEntities()) {
										if (e instanceof LivingEntity le && le.getScoreboardTags().contains("FancyBoss")) {
											bossAlive = true;
											break;
										}
									}
									prevPlayer = p;
									if (bossAlive)
										break;
								}
								if (!bossAlive) {
									//Bukkit.broadcastMessage("Alive Entities: " + aliveEntities + ", maxMonsters: " + lvl.getMaxMonstersAlive());
									if (levelDone.get(arenaName)) {
										clearMobs(arenaName);
									}
									levelStage.put(arenaName, levelStage.get(arenaName)+1);
									messageIndex.put(arenaName, 0);
									lastCC.put(arenaName, -1l);
	
									int lvlNumber = currentLevel.get(arenaName);
								}
								//Bukkit.broadcastMessage(ChatColor.GREEN + "Cleared Level " + lvlNumber + ": " + ChatColor.LIGHT_PURPLE + lvl.getLevelName());
							} else {
								int alivePlayers = 0;
								for (int l : lives.values()) {
									if (l >= 0)
										alivePlayers++;
								}
								/*
								for (World w : Bukkit.getWorlds()) {
									for (Player p : w.getPlayers()) {
										if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
											alivePlayers++;
										}
									}
								}
								*/
								if (alivePlayers == 0) {
									// Defeat
									Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "DEFEAT! All players were eliminated from arena " + arenaName + "!");
									
									//Location minPos = lvl.getMinPosLoc();
									//Location maxPos = lvl.getMaxPosLoc();
									//ClearFire(minPos.getWorld(), minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ(), maxPos.getBlockX(), maxPos.getBlockY(), maxPos.getBlockZ());
									
									stopArena(arenaName);
								}
							}
							
							break;
						}
						case 7: // End Chat Messages
						{
							if (!dialogs.containsKey(arenaName))
							{
								lastCC.put(arenaName, -1l);
								levelStage.put(arenaName, levelStage.get(arenaName)+1);
								messageIndex.put(arenaName, 0);
							}
							else if (lastCC.get(arenaName) < 0 || lastCC.get(arenaName) < cc) { // Timer not started
								FancyLevel lvl = getCurrentLevel(arenaName);
								Location minPos = lvl.getMinPosLoc();
								Location maxPos = lvl.getMaxPosLoc();
								ClearFire(minPos.getWorld(), minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ(), maxPos.getBlockX(), maxPos.getBlockY(), maxPos.getBlockZ());
								
								ChatMessage msg = dialogs.get(arenaName).getEndMessage(currentLevel.get(arenaName), messageIndex.get(arenaName));
								messageIndex.put(arenaName, messageIndex.get(arenaName)+1);
								if (msg == null) {
									lastCC.put(arenaName, -1l);
									levelStage.put(arenaName, levelStage.get(arenaName)+1);
									messageIndex.put(arenaName, 0);
								} else {
									Player player = null;
									boolean playerDefined = false;
										for (Player p : Bukkit.getOnlinePlayers()) {
											if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
												if (!playerDefined)
													player = p;
												else if (Math.random() > 0.5)
													player = p;
											}
										}
									
									
									String str = dialogs.get(arenaName).constructMessage(msg, player.getName(), player.getName());
									
									Bukkit.broadcastMessage(str);
									
									lastCC.put(arenaName, cc + msg.tickDuration);
								}
								//Bukkit.broadcastMessage("Level starting in " + getCurrentLevel(arenaName).getWaveStartDelay()/20 + " seconds");
							}
							break;
						}
						case 8: // Run end commands
						{
							FancyLevel lvl = getCurrentLevel(arenaName);
							try {
								if (lvl.getEndCommands() != null && lvl.getEndCommands().size() > 0) {
									Player p = null;
									for (Player player : Bukkit.getOnlinePlayers()) {
										if (player.getScoreboardTags().contains("Ready_" + arenaName)) {
											p = player;
											break;
										}
									}
									for (String cmd : lvl.getEndCommands()) {
										if (cmd != null && cmd.length() > 0)
											arenaCMD(cmd, p);
											//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + p.getName() + " at @s run " + cmd);
									}
								}
							} catch (Exception e) {
								Bukkit.broadcastMessage(ChatColor.RED + "Failed to execute end commands");
							}

							levelStage.put(arenaName, levelStage.get(arenaName)+1);
							break;
						}
						case 9: // Next wave delay
							if (lastCC.get(arenaName) < 0) { // Timer not started
								lastCC.put(arenaName, cc);
								Bukkit.broadcastMessage(ChatColor.GRAY +""+ ChatColor.ITALIC + getCurrentLevel(arenaName).getWaveStartDelay()/20 + " seconds until next level");
							} else if (cc-lastCC.get(arenaName) > getCurrentLevel(arenaName).getNextWaveDelay()) {
								lastCC.put(arenaName, -1l);
								levelStage.put(arenaName, levelStage.get(arenaName)+1);
							}
							break;
						case 10: // Start next level
							int lvlNumber = currentLevel.get(arenaName);
							FancyLevel lvl = getCurrentLevel(arenaName);
							lvl.setRunning(false);
							
							if (levels.get(arenaName).size() > lvlNumber+1) {
								// Still more levels
								FancyLevel nextLvl = levels.get(arenaName).get(lvlNumber + 1);
								nextLvl.setRunning(true);
								levelStage.put(arenaName, 0);
								currentLevel.put(arenaName, lvlNumber + 1);
							} else {
								// All levels complete
								for (World w : Bukkit.getWorlds()) {
									for (Player p : w.getPlayers()) {
										if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
											p.sendTitle(ChatColor.GREEN + "Victory", "Completed arena " + arenaName, 1, 60, 5);
											p.getInventory().clear();
										}
									}
								}

								stopArena(arenaName);
							}
							break;
					}
				}
			}
		}
		
		
		cc++;
	}
	
	private static void arenaCMD(String cmd, Player player) {
		// TODO Auto-generated method stub
		try {
			if (cmd.contains("tp") && (cmd.contains("@a") || cmd.contains("@p"))) {
				for (Player p : Bukkit.getOnlinePlayers()) {
					FancyArena.prepare_teleport(p);
					/*
					if (p.getPassengers().size() > 0)
						p.removePassenger(p.getPassengers().get(0));
					if (p.getScoreboardTags().contains("Grapple")) {
						p.removeScoreboardTag("Grapple");
					}
					*/
				}
			}
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getName() + " at @s run " + cmd);
		} catch (Exception e) {
			
		}
	}

	private static boolean mercenariesAllowed(int lvlNumber) {
		switch (lvlNumber) {
			case 0:
			case 1:
			case 15:
			case 16:
			case 36:
			case 37:
			case 38:
			case 39:
			case 40:
			case 41:
			case 42:
				return false;
		}
		return true;
	}
	
	public static void prison(Entity entity) {
		Location jailLoc = new Location(FancyMob.templateWorld, -14, -48, 11);
		if (entity != null && entity instanceof LivingEntity le && Arena.validTarget(le) && le.getLocation().distance(jailLoc) > 20) {
			
			if (le instanceof Player p) {
				
				for (ItemStack item : p.getInventory().getContents()) {
					if (item != null && (item.getType() == Material.TRIAL_KEY || item.getType() == Material.TOTEM_OF_UNDYING)) {
						if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains("Jail Key")) {
							//for (Player player : p.getWorld().getPlayers()) {
								SFX.play(p.getLocation(), Sound.ITEM_TOTEM_USE, 2, 2);
							//}
							p.getWorld().spawnParticle(Particle.END_ROD, p.getLocation(), 20, 0, 0, 0, 0);
							return;
						}
					}
				}
				
				FancyArena.instance.storeInventoryInMinecarts(p);
				
				if (FancyArena.instance.sentryMode.containsKey(p)) {
					FancyArena.instance.sentryMode.get(p).remove();
					FancyArena.instance.sentryMode.remove(p);
				}
				
				if (FancyArena.instance.sentryTarget.containsKey(p)) {
					FancyArena.instance.sentryTarget.remove(p);
				}
			}
			
			/*if (entity.getPassengers().size() > 0)
				entity.removePassenger(entity.getPassengers().get(0));
			entity.teleport(new Location(entity.getWorld(), -14, -48, 11));*/
			FancyArena.super_strong_teleport(le,  jailLoc);
		}
	}
	
	/**
	 * Calculates the similarity (a number within 0 and 1) between two strings.
	 */
	public static double similarity(String s1, String s2) {
	  String longer = s1, shorter = s2;
	  if (s1.length() < s2.length()) { // longer should always have greater length
	    longer = s2; shorter = s1;
	  }
	  int longerLength = longer.length();
	  if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	  return (longerLength - editDistance(longer, shorter)) / (double) longerLength;
	}
	
	// Example implementation of the Levenshtein Edit Distance
	  // See http://rosettacode.org/wiki/Levenshtein_distance#Java
	  public static int editDistance(String s1, String s2) {
	    s1 = s1.toLowerCase();
	    s2 = s2.toLowerCase();

	    int[] costs = new int[s2.length() + 1];
	    for (int i = 0; i <= s1.length(); i++) {
	      int lastValue = i;
	      for (int j = 0; j <= s2.length(); j++) {
	        if (i == 0)
	          costs[j] = j;
	        else {
	          if (j > 0) {
	            int newValue = costs[j - 1];
	            if (s1.charAt(i - 1) != s2.charAt(j - 1))
	              newValue = Math.min(Math.min(newValue, lastValue),
	                  costs[j]) + 1;
	            costs[j - 1] = lastValue;
	            lastValue = newValue;
	          }
	        }
	      }
	      if (i > 0)
	        costs[s2.length()] = lastValue;
	    }
	    return costs[s2.length()];
	  }
	
	public static void removeTags(Player player, String str) {
		ArrayList<String> tagsToRemove = new ArrayList<String>();

		// Collect tags to remove
		for (String tag : player.getScoreboardTags()) {
		    if (tag.contains(str)) {
		        tagsToRemove.add(tag);
		    }
		}

		// Remove collected tags
		for (String tag : tagsToRemove) {
		    player.removeScoreboardTag(tag);
		}
	}
	
	public static void save(String filepath) {
		try {
			//Bukkit.broadcastMessage(ChatColor.YELLOW + "Attempting to save FancyArenas");
			(new File(filepath)).mkdirs();
			(new File(filepath + filename)).createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath + filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(levels);
			
			System.out.println("Writing to " + filepath + filename + " with save()");
			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully saved FancyArenas to " + filepath + filename);
		} catch (IOException ex) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to save FancyArenas");
			ex.printStackTrace();
		}
		try {
			//Bukkit.broadcastMessage(ChatColor.YELLOW + "Attempting to save FancyArena Dialogs");
			//(new File(filepath)).mkdirs();
			(new File(filepath + filename2)).createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath + filename2);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(dialogs);
			
			System.out.println("Writing to " + filepath + filename2 + " with save()");
			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully saved FancyArena Dialogs to " + filepath + filename2);
		} catch (IOException ex) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to save FancyArena Dialogs");
			ex.printStackTrace();
		}
		try {
			//Bukkit.broadcastMessage(ChatColor.YELLOW + "Attempting to save FancyArena Dialogs");
			//(new File(filepath)).mkdirs();
			(new File(filepath + filename3)).createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath + filename3);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(finalCommands);
			
			System.out.println("Writing to " + filepath + filename3 + " with save()");
			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully saved FancyArena Dialogs to " + filepath + filename2);
		} catch (IOException ex) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to save FancyArena Commands");
			ex.printStackTrace();
		}

		try {
			//Bukkit.broadcastMessage(ChatColor.YELLOW + "Attempting to save FancyArena Dialogs");
			//(new File(filepath)).mkdirs();
			(new File(filepath + filename4)).createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath + filename4);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(cutscenes);
			
			System.out.println("Writing to " + filepath + filename4 + " with save()");
			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully saved FancyArena Dialogs to " + filepath + filename2);
		} catch (IOException ex) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to save FancyArena Cutscenes");
			ex.printStackTrace();
		}

		try {
			//Bukkit.broadcastMessage(ChatColor.YELLOW + "Attempting to save FancyArena Dialogs");
			//(new File(filepath)).mkdirs();
			(new File(filepath + filename5)).createNewFile();
			FileOutputStream fos = new FileOutputStream(filepath + filename5);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(shopLoc);
			
			System.out.println("Writing to " + filepath + filename5 + " with save()");
			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully saved FancyArena Dialogs to " + filepath + filename2);
		} catch (IOException ex) {
			Bukkit.broadcastMessage(ChatColor.RED + "Failed to save FancyArena Shop");
			ex.printStackTrace();
		}
	}

	public static void load() {
		try (FileInputStream fis = new FileInputStream(filepath + filename); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			HashMap<String, List<FancyLevel>> yeet = (HashMap<String, List<FancyLevel>>) ois.readObject();
			
			levels = yeet;

			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully loaded FancyArenas from " + filepath + filename);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try (FileInputStream fis = new FileInputStream(filepath + filename2); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			HashMap<String, Dialogs> yeet = (HashMap<String, Dialogs>) ois.readObject();
			
			dialogs = yeet;
			

			//Bukkit.broadcastMessage(ChatColor.GREEN + "Successfully loaded FancyArena Dialogs from " + filepath + filename2);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (FileInputStream fis = new FileInputStream(filepath + filename3); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			HashMap<String, List<String>> yeet = (HashMap<String, List<String>>) ois.readObject();
			
			finalCommands = yeet;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (FileInputStream fis = new FileInputStream(filepath + filename4); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			HashMap<String, HashMap<Integer, Cutscene>> yeet = (HashMap<String, HashMap<Integer, Cutscene>>) ois.readObject();
			
			cutscenes = yeet;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (FileInputStream fis = new FileInputStream(filepath + filename5); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			String yeet = (String) ois.readObject();
			
			shopLoc = yeet;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ClearFire(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		ClearFire(world, x1, y1, z1, x2, y2, z2, null);
	}
	
	public static void ClearFire(World world, int x1, int y1, int z1, int x2, int y2, int z2, Player player) {
		if (player != null)
			player.sendMessage(ChatColor.GRAY + "Clearing fire...");
		if (x1 > x2) {
			int temp = x2;
			x2 = x1;
			x1 = temp;
		}
		if (y1 > y2) {
			int temp = y2;
			y2 = y1;
			y1 = temp;
		}
		if (z1 > z2) {
			int temp = z2;
			z2 = z1;
			z1 = temp;
		}
		
		if (Math.abs(x2-x1) < 30 || Math.abs(y2-y1) < 30 || Math.abs(z2-z1) < 30) {
			if (player != null)
				player.sendMessage(ChatColor.GRAY + "Clearing fire in a small zone, using synchronus approach");
			for (int i = x1; i <= x2; i++) {
				for (int j = y1; j <= y2; j++) {
					for (int k = z1; k <= z2; k++) {
						Location loc = new Location(world, i, j, k);
						//if (player != null)
						//	player.sendMessage(ChatColor.GRAY + "Clearing fire at " + i + " " + j + " " + k);
		                //loc.getWorld().spawnParticle(Particle.POOF, loc, 1, 0, 0, 0, 0);
						if (loc.getBlock().getType() == Material.FIRE) {
							Location under = loc.clone().add(new Vector(0, -1, 0));
							if (under.getBlock().getType() == Material.SOUL_SAND || under.getBlock().getType() == Material.NETHERRACK || under.getBlock().getType() == Material.END_STONE) {
								// Don't put out fire if it's on soul sand or netherrack or endstone
							} else {
								loc.getBlock().setType(Material.AIR);
							}
						}
					}
				}
			}
			if (player != null)
				player.sendMessage(ChatColor.GRAY + "Done clearing fire");
		}
		else
		{
			if (player != null)
				player.sendMessage(ChatColor.GRAY + "Clearing fire in a large zone, using asynchronus approach");
			int xMin = x1;
			int yMin = y1;
			int zMin = z1;
			int xMax = x2;
			int yMax = y2;
			int zMax = z2;
			
			BukkitTask task = new BukkitRunnable() {
	        	
	        	int i = 0;
	        	int k = 0;
	        	
	        	public void removeFire(int x, int z) {
	        		// Clear a column of fire
	                for (int j = yMin; j <= yMax; j++) {
		                // Clear the fire  at this location
		                Location loc = new Location(world, x, j, z);
		                //loc.getWorld().spawnParticle(Particle.POOF, loc, 1, 0, 0, 0, 0);
						if (loc.getBlock().getType() == Material.FIRE) {
							Location under = loc.clone().add(new Vector(0, -1, 0));
							if (under.getBlock().getType() == Material.SOUL_SAND || under.getBlock().getType() == Material.NETHERRACK || under.getBlock().getType() == Material.END_STONE) {
								// Don't put out fire if it's on soul sand or netherrack or endstone
							} else {
								loc.getBlock().setType(Material.AIR);
							}
						}
	                }
	        	}
	        	
	            @Override
	            public void run() {
	            	//if (player != null)
	        		//	player.sendMessage(ChatColor.GRAY + "i=" + i + " k=" + k + " (xMax-xMin)/2" + (xMax-xMin)/2 + " (zMax-zMin)/2" + (zMax-zMin)/2);
	                if (i >= (xMax-xMin)/2 && k >= (zMax-zMin)/2) {
	                	//Bukkit.broadcastMessage("done clearing fire");
	                	//if (player != null)
	            		//	player.sendMessage(ChatColor.GRAY + "Exiting runnable");
	                	if (player != null)
	            			player.sendMessage(ChatColor.GRAY + "Done clearing fire");
	                    cancel();
	                    return;
	                }
	                //Bukkit.broadcastMessage("Clearing fire at " + (i+xMin) + ", " + (k+zMin));
	                
	                for (int di = 0; di < 10; di++) {
	                	for (int dk = 0; dk < 10; dk++) {
	                		removeFire(i+xMin+di, k+zMin+dk);
	                		removeFire(xMax-i-di, zMax-k-dk);
	                	}
	                }
					
					// Select next  location
					i+=10;
					if (i > (xMax-xMin)/2) {
						i = 0;
						k+=10;
					}
					
	            }
	        }.runTaskTimer(FancyArena.instance, 0L, 1L);
	        
			tasks.put(task, System.currentTimeMillis());
		}

		// clear all fire within a cube defined by x1:x2, y1:y1, z1:z2
        /*
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				for (int k = z1; k <= z2; k++) {
					Location loc = new Location(world, i, j, k);
					if (loc.getBlock().getType() == Material.FIRE) {
						Location under = loc.clone().add(new Vector(0, -1, 0));
						if (under.getBlock().getType() == Material.SOUL_SAND || under.getBlock().getType() == Material.NETHERRACK || under.getBlock().getType() == Material.END_STONE) {
							// Don't put out fire if it's on soul sand or netherrack
						} else {
							loc.getBlock().setType(Material.AIR);
						}
					}
				}
			}
		}
		*/
	}
	
	public static FancyLevel getCurrentLevel(String arenaName) {
		return levels.get(arenaName).get(currentLevel.get(arenaName));
		/*
		if (levels.containsKey(arenaName) && currentLevel.containsKey(arenaName) && levels.get(arenaName).contains(currentLevel.get(arenaName)))
			return levels.get(arenaName).get(currentLevel.get(arenaName));
		else
			return null;
			*/
	}
	
	public static Location getPlayerSpawn(String arenaName) {
		FancyLevel lvl = getCurrentLevel(arenaName);
		return lvl != null ? lvl.getPlayerSpawnLoc() : (levels.containsKey(arenaName) ? levels.get(arenaName).get(0).getPlayerSpawnLoc() : null);
		
	}
	
	public static void clearMobs(String arenaName) {
		if (arenaEntities.containsKey(arenaName)) {
			for (LivingEntity e : arenaEntities.get(arenaName)) {
				try {
					if (bossBars.containsKey(e)) {
						BossBar bossBar = bossBars.get(e);
						List<Player> players = bossBar.getPlayers();
				        for (Player player : players) {
				           bossBar.removePlayer(player);
				        }
				        bossBar.setVisible(false);
						bossBars.remove(e);
					}
				} catch (Exception f) {
				}
				e.remove();
			}
			bossBars.clear();
			arenaEntities.clear();
		}
	}
	
	public static void resetBossBars() {
		for (BossBar bb : bossBars.values()) {
			bb.setVisible(false);
			bb.removeAll();
		}
		bossBars.clear();
		
		Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        	Bukkit.broadcastMessage(ChatColor.GRAY + "Fixing Boss Bars");
        	boolean fixed = false;
        	for (String arenaName : arenaEntities.keySet()) {
        		for (LivingEntity le : arenaEntities.get(arenaName)) {
        			if (le.getScoreboardTags().contains("FancyBoss") ) {
        				fixed = true;
			        	BossBar bossBar = Bukkit.getServer().createBossBar(le.getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							bossBar.addPlayer(p);
						}
						Arena.bossBars.put(le, bossBar);
        			}
        		}
        	}
        	if (!fixed) {
        		for (Player p : Bukkit.getOnlinePlayers()) {
        			for (Entity e : p.getWorld().getEntities()) {
        				if (e instanceof LivingEntity le && le.getScoreboardTags().contains("FancyBoss")) {
        					fixed = true;
    			        	BossBar bossBar = Bukkit.getServer().createBossBar(le.getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
    						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
    							bossBar.addPlayer(player);
    						}
    						Arena.bossBars.put(le, bossBar);
        				}
        			}
        		}
        	}
        }, 5);
	}
	
	public static void killBigTasks() {
		if (bigTasks.size() > 0) {
			//ArrayList<BukkitTask> remove = new ArrayList<>();
			for (BukkitTask task : bigTasks) {
				if (task != null) {
					if (!task.isCancelled()) {
						Bukkit.broadcastMessage(ChatColor.GRAY + "Canceling hanging task");
						task.cancel();
					}
				}
				Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
		        	if (task == null || task.isCancelled()) {
		        		Bukkit.broadcastMessage(ChatColor.GRAY + "Deleting hanging task");
		        		bigTasks.remove(task);
		        	}
		        }, 5);
			}
		}
	}
	
	public static void stopArena(String arenaName) {
		Bukkit.broadcastMessage(ChatColor.ITALIC + "Stopping arena " + arenaName);
		
		killBigTasks();
		
		FancyArena.instance.brutalCrit.clear();
		
		Castle.deleteStructureAsync(FancyArena.instance, 2);
		
		FancyArena.customLivingSounds.clear();
		playerEffects.clear();
		
		if (FancyArena.instance.sentryMode.size() > 0) {
			for (LivingEntity le : FancyArena.instance.sentryMode.keySet()) {
				if (FancyArena.instance.sentryMode.get(le) != null && FancyArena.instance.sentryMode.get(le).isValid())
					FancyArena.instance.sentryMode.get(le).remove();
			}
		}
		if (FancyArena.instance.durrithWings.size() > 0) {
			for (LivingEntity le : FancyArena.instance.durrithWings.keySet()) {
				if (FancyArena.instance.durrithWings.get(le) != null && FancyArena.instance.durrithWings.get(le).isValid()) {
					if (le instanceof Player p)
						p.setAllowFlight(false);
					FancyArena.instance.durrithWings.get(le).remove();
				}
			}
		}
		FancyArena.instance.durrithWings.clear();
		if (FancyArena.instance.sentryTarget.size() > 0)
			FancyArena.instance.sentryTarget.clear();
		
		if (FancyArena.instance.repulsorFly.size() > 0) {
			try {
				//LivingEntity[] keys = (LivingEntity[]) FancyArena.instance.repulsorFly.toArray();
				ArrayList<LivingEntity> temp = FancyArena.instance.repulsorFly;
				for (LivingEntity le : temp) {
					FancyArena.instance.disableRepulsorFly(le);
				}
				FancyArena.instance.repulsorFly.clear();
			}
			catch (Exception e) {}
		}
		
		for (FancyLevel lvl : levels.get(arenaName)) {
			try {
				Location minPos = lvl.getMinPosLoc();
				Location maxPos = lvl.getMaxPosLoc();
				ClearFire(minPos.getWorld(), minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ(), maxPos.getBlockX(), maxPos.getBlockY(), maxPos.getBlockZ());
				Collection<Entity> entities = minPos.getWorld().getNearbyEntities(new BoundingBox(minPos.getBlockX(), minPos.getBlockY(), minPos.getBlockZ(), maxPos.getBlockX(), maxPos.getBlockY(), maxPos.getBlockZ()));
				for (Entity e : entities) {
					if (e.getType() == EntityType.ARROW || e.getType() == EntityType.SPECTRAL_ARROW || e.getType() == EntityType.ITEM || e.getScoreboardTags().contains("FancyBossLoot")) {
						e.remove();
					}
				}
			}
			catch (Exception e) {
				
			}
		}
		
		if (cutscenes.containsKey(arenaName)) {
			for (Cutscene cs : cutscenes.get(arenaName).values()) {
				try {
					if (cs != null)
						cs.reset();
				} catch (Exception e) {
					
				}
			}
		}
		
		if (finalCommands.containsKey(arenaName)) {
			for (String cmd : finalCommands.get(arenaName)) {
				try {
					Player p = null;
					for (Player player : Bukkit.getOnlinePlayers()) {
						if (player.getScoreboardTags().contains("Ready_" + arenaName)) {
							p = player;
							break;
						}
					}
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + p.getName() + " at @s run " + cmd);
				} catch (Exception e) {
					
				}
			}
		}
		
		clearMobs(arenaName);
		if (lootedChests.containsKey(arenaName))
			lootedChests.get(arenaName).clear();
		currentLevel.put(arenaName, 0);
		
		lastCC.put(arenaName, -1l);
		levelStage.put(arenaName, 0);
		
		if (runningArenas.contains(arenaName))
			runningArenas.remove(arenaName);
		
		for (World w : Bukkit.getWorlds()) {
			for (Player p : w.getPlayers()) {
				if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
					FancyArena.instance.resetShit(p);
					Heal(p);
					p.removeScoreboardTag("Ready_" + arenaName);
				}
			}
		}
		
		for (FancyLevel l : levels.get(arenaName)) {
			l.setRunning(false);
		}
		

		Bukkit.broadcastMessage(ChatColor.ITALIC + "Successfully stopped arena " + arenaName);
		
	}
	
	public static void Heal(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 3, 200));
		player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3, 200));
		player.getLocation().getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
		SFX.play(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 2);
		
		//Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has been healed!");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("prison")) {
				for (Player p : Bukkit.getOnlinePlayers())
					prison(p);
				return true;
			}
			else if (args[0].equals("show_characters") || args[0].equals("show_names")) {
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				int lvlNum = levels.get(arenaName).indexOf(lvl);
				
				if (dialogs.containsKey(arenaName)) {
					ArrayList<String> characters = dialogs.get(arenaName).characters;
					
					player.sendMessage(ChatColor.GOLD + "Listing Characters:");
					for (String name : characters)
						player.sendMessage(name);
				}
				return true;
			}
			else if (args[0].equals("show_start_message") || args[0].equals("show_start_messages") || args[0].equals("show_start_chat")) {
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				int lvlNum = levels.get(arenaName).indexOf(lvl);
				
				if (dialogs.containsKey(arenaName)) {
					int num = dialogs.get(arenaName).startMessages.size();
					for (int i = 0; i < num; i++) {
						ChatMessage msg = dialogs.get(arenaName).getStartMessage(lvlNum, i);
						//ChatMessage msg = dialogs.get(arenaName).getStartMessage(currentLevel.get(arenaName), messageIndex.get(arenaName));
						//messageIndex.put(arenaName, messageIndex.get(arenaName)+1);
						if (msg == null) {
							
						} else {
							
							String str = dialogs.get(arenaName).constructMessage(msg, player.getName(), player.getName());
							
							Bukkit.broadcastMessage(str);
							
						}
					}
				}
				return true;
			}
			else if (args[0].equals("debug") && sender instanceof Player p) {
				FancyArena.instance.openDebugInventory(p);
				return true;
			}
			else if (args[0].equals("show_start_cmd") || args[0].equals("show_begin_cmd")) {
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				int lvlNum = levels.get(arenaName).indexOf(lvl);
				
				for (String m : lvl.getBeginCommands())
					player.sendMessage(m);
				return true;
			}
			else if (args[0].equals("show_end_cmd")) {
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				int lvlNum = levels.get(arenaName).indexOf(lvl);
				
				for (String m : lvl.getEndCommands())
					player.sendMessage(m);
				return true;
			}
			else if (args[0].equals("show_end_message") || args[0].equals("show_end_messages") || args[0].equals("show_end_chat")) {
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				int lvlNum = levels.get(arenaName).indexOf(lvl);
				
				if (dialogs.containsKey(arenaName)) {
					int num = dialogs.get(arenaName).endMessages.size();
					for (int i = 0; i < num; i++) {
						ChatMessage msg = dialogs.get(arenaName).getEndMessage(lvlNum, i);
						//ChatMessage msg = dialogs.get(arenaName).getStartMessage(currentLevel.get(arenaName), messageIndex.get(arenaName));
						//messageIndex.put(arenaName, messageIndex.get(arenaName)+1);
						if (msg == null) {
							//lastCC.put(arenaName, -1l);
							//levelStage.put(arenaName, levelStage.get(arenaName)+1);
							//messageIndex.put(arenaName, 0);
						} else {
							
							String str = dialogs.get(arenaName).constructMessage(msg, player.getName(), player.getName());
							
							Bukkit.broadcastMessage(str);
							
							//lastCC.put(arenaName, cc + msg.tickDuration);
						}
					}
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.GOLD + "FancyArena Commands:");
				for (String str : commands) {
					sender.sendMessage("/fancyarena " + str);
				}
				return true;
			} else if (args[0].equalsIgnoreCase("backup")) {
				try {

					Arena.save(Arena.filepath_backup);
					
					sender.sendMessage(ChatColor.GREEN + "Successfully created backup");
					
					return true;
				} catch (Exception e) {
					sender.sendMessage("Usage /fancyarena cutscene <delay> <message>");
					return false;
				}
			} 
			else if (args[0].equalsIgnoreCase("cutscene")) {
				try {
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					FancyLevel lvl = getSelectedLevel(player);
					String arenaName = lvl.getArenaName();
					int lvlNum = levels.get(arenaName).indexOf(lvl);
					
					int delay = Integer.parseInt(args[1]);
					String msg = "";
					Location loc = player.getLocation().clone();
					for (int i = 2; i < args.length; i++) {
						if (msg.length() > 0) {
							msg += " ";
						}
						msg += args[i];
					}
					
					if (!cutscenes.containsKey(arenaName)) {
						cutscenes.put(arenaName, new HashMap<Integer, Cutscene>());
					}
					if (!cutscenes.get(arenaName).containsKey(lvlNum)) {
						cutscenes.get(arenaName).put(lvlNum, new Cutscene(arenaName));
					}
					cutscenes.get(arenaName).get(lvlNum).addLoc(loc, delay, msg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added current location to cutscene");
					
					return true;
				} catch (Exception e) {
					sender.sendMessage("Usage /fancyarena cutscene <delay> <message>");
					return false;
				}
			} else if (args[0].equalsIgnoreCase("clear_cutscene")) {
				try {
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					FancyLevel lvl = getSelectedLevel(player);
					String arenaName = lvl.getArenaName();
					int lvlNum = levels.get(arenaName).indexOf(lvl);
					
					if (!cutscenes.containsKey(arenaName)) {
						cutscenes.put(arenaName, new HashMap<Integer, Cutscene>());
					}
					if (cutscenes.get(arenaName).containsKey(lvlNum)) {
						cutscenes.get(arenaName).remove(lvlNum);
					}
					sender.sendMessage(ChatColor.GREEN + "Successfully removed cutscene from current level");
					
					return true;
				} catch (Exception e) {
					sender.sendMessage("Usage /fancyarena cutscene <delay> <message>");
					return false;
				}
			}  else if (args[0].equalsIgnoreCase("max_lives")) {
				if (args.length > 1) {
					try {
						FancyArena.maxLives = Integer.parseInt(args[1]);
						sender.sendMessage(ChatColor.GREEN + "Set max lives to " + ChatColor.WHITE + args[1]);
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Failed to set max lives");
						sender.sendMessage("Usage /fancyarena max_lives <number>");
					}
					return true;
				} else {
					sender.sendMessage("Usage /fancyarena max_lives <number>");
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("shop")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				
				String name = args[3];
				for (int i = 4; i < args.length; i++) {
					name += " " + args[i];
				}
				
				Villager villager = (Villager)player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
				
				villager.setBreed(false);
				villager.setAI(false);
				villager.addScoreboardTag("FancyShop");
				villager.addScoreboardTag("invulnerable");
				villager.setVillagerType(Villager.Type.valueOf(args[1]));
				villager.setProfession(Profession.valueOf(args[2]));
				
				villager.setCustomName(ChatColor.GREEN + name);
				villager.setCustomNameVisible(true);
				villager.setInvulnerable(true);
				villager.setCanPickupItems(false);
				villager.setPersistent(true);
				villager.setAdult();
				villager.setSilent(true);
				//villager.pro
				
				List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());
				recipes.clear();

				MerchantRecipe recipe = new MerchantRecipe(new ItemStack(Material.GOLD_INGOT), 0, Integer.MAX_VALUE, false);
				List<ItemStack> ingredients = new ArrayList<ItemStack>();
				ingredients.add(new ItemStack(Material.GOLD_NUGGET, 9));
				recipe.setIngredients(ingredients);
		        recipes.add(recipe);
		        
		        MerchantRecipe recipe2 = new MerchantRecipe(new ItemStack(Material.GOLD_BLOCK), 0, Integer.MAX_VALUE, false);
				List<ItemStack> ingredients2 = new ArrayList<ItemStack>();
				ingredients2.add(new ItemStack(Material.GOLD_INGOT, 9));
				recipe2.setIngredients(ingredients2);
		        recipes.add(recipe2);

		        villager.setRecipes(recipes);
		        
		        //Inventory fancyInv = Bukkit.createInventory(null, InventoryType.HOPPER, "Select Price");
		        
		        //player.openInventory(fancyInv);
		        
		        return true;
			} else if (args[0].equalsIgnoreCase("mod_shop")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				
				
				Player player = (Player) sender;
				
				Villager villager = null;
	        	for (Entity e : player.getNearbyEntities(2, 2, 2)) {
	        		if (e.getType() == EntityType.VILLAGER && e.getScoreboardTags().contains("FancyShop")) {
	        			villager = (Villager)e;
	        		}
	        	}
	        	villager.setAI(false);
				
	        	if (args.length > 1 && args[1].equalsIgnoreCase("remove")) {
	            	
	            	List<MerchantRecipe> recipes =  Lists.newArrayList(villager.getRecipes());
	            	
	            	recipes.remove(recipes.size()-1);
	            	
	            	villager.setRecipes(recipes);
	    			//villager.setAI(true);
	            	player.sendMessage(ChatColor.YELLOW + "Shop " + villager.getCustomName() + ChatColor.YELLOW + " successfully modified");
	        	} else {
	        	
					Inventory fancyInv = Bukkit.createInventory(null, InventoryType.HOPPER, "Select Price");
			        
			        player.openInventory(fancyInv);
	        	}
		        return true;
			}
			else if (args[0].equalsIgnoreCase("del_trade")) {
	            if (!(sender instanceof Player)) {
	                sender.sendMessage(ChatColor.RED + "Only players can use this command!");
	                return true;
	            }
	            Player player = (Player) sender;
	            // Locate a nearby villager with the "FancyShop" tag.
	            Villager villager = null;
	            for (Entity e : player.getNearbyEntities(2, 2, 2)) {
	                if (e.getType() == EntityType.VILLAGER && e.getScoreboardTags().contains("FancyShop")) {
	                    villager = (Villager) e;
	                    break;
	                }
	            }
	            if (villager == null) {
	                player.sendMessage(ChatColor.RED + "No FancyShop villager found nearby!");
	                return true;
	            }
	            // Store the villager and set the starting page.
	            FancyArena.deletionVillagerMap.put(player, villager);
	            FancyArena.deletionPageMap.put(player, 0);
	            FancyArena.openDeletionInventory(player, 0, villager);
	            return true;
	        }
			else if (args[0].equalsIgnoreCase("tp")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				
				World world = Bukkit.getWorld(args[1]);
				Location loc = world.getSpawnLocation();
				
				if (args.length > 2) {
					int x = Integer.parseInt(args[2]);
					int y = Integer.parseInt(args[3]);
					int z = Integer.parseInt(args[4]);
					loc = new Location(world, x, y, z);
				}
				
				ItemStack[] armor = player.getInventory().getArmorContents().clone();
				ItemStack[] inv = player.getInventory().getContents().clone();
				
				Collection<PotionEffect> effects = player.getActivePotionEffects();
				
				/*if (player.getPassengers().size() > 0)
					player.removePassenger(player.getPassengers().get(0));*/
				FancyArena.prepare_teleport(player);
				player.teleport(loc);
				
				player.getInventory().setArmorContents(armor);
				player.getInventory().setContents(inv);
				

				Collection<PotionEffect> effects2 = player.getActivePotionEffects();
				for (PotionEffect e : effects2) {
					player.removePotionEffect(e.getType());
				}
				
				player.addPotionEffects(effects);
				
				SFX.play(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 1.5F, 1.5F);
				player.spawnParticle(Particle.CHERRY_LEAVES, player.getLocation(), 50, 1F, 1.5F, 1F);
				return true;
			}
			else if (args[0].equalsIgnoreCase("start")) {
				if (args.length > 1) {
					findFancyMobTemplates();
					String arenaName = args[1];
					
					int lvlNum = 0;
					if (args.length > 2)
						lvlNum = Integer.parseInt(args[2]);
					
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					
					if (!levels.containsKey(arenaName)) {
						sender.sendMessage(ChatColor.RED + arenaName + " doesn't exist, have you created the arena with '/fancyarena create'?");
						return true;
					}
					
					if (getCurrentLevel(arenaName).getRunning()) {
						sender.sendMessage(ChatColor.YELLOW + arenaName + " is already running, please do '/fancyarena stop' to end it before starting a new game.");
						return true;
					}
					
					removeTags(player, "Ready_");
					
					player.addScoreboardTag("Ready_" + arenaName);
					lives.put(player.getName(), FancyArena.maxLives);
					
					currentLevel.put(arenaName, lvlNum);
					
					FancyLevel level = getCurrentLevel(arenaName);
					
					level.setRunning(true);
					if (!runningArenas.contains(arenaName))
						runningArenas.add(arenaName);
					
					levelStage.put(arenaName, 0);
					
					lootedChests.put(arenaName, new ArrayList<String>());
					
					for (Player p : player.getWorld().getPlayers()) {
						if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
							/*if (p.getPassengers().size() > 0)
								p.removePassenger(p.getPassengers().get(0));*/
							FancyArena.prepare_teleport(p);
							p.teleport(getPlayerSpawn(arenaName));
							p.setGameMode(GameMode.SURVIVAL);
							p.getEnderChest().clear();
							playerEffects.put(player, player.getActivePotionEffects());
							Heal(p);
							
							if (lvlNum > 0) {
								for (int i = 0; i < lvlNum; i++) {
									p.getInventory().addItem(new ItemStack(Material.GOLD_INGOT));
									Inventory loot = levels.get(arenaName).get(i).getRandomChestLoot();
									
									for (int j = 0; j < loot.getSize(); j++) {
							            ItemStack item = loot.getItem(j);
							            if (item != null) {
							                p.getInventory().addItem(item.clone());
							            }
							        }
								}
							}
						}
					}
					
					bankGold = 0;
					bankChestplates.clear();
					bankLeggings.clear();
					bankBoots.clear();
					bankMeleeWeapons.clear();
					bankRangedWeapons.clear();
					
					Bukkit.broadcastMessage("Starting arena " + arenaName);
					
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("make_castle")) {
				Castle.cloneRegionAsync(FancyArena.instance);
			}
			else if (args[0].equalsIgnoreCase("eat_castle") || args[0].equalsIgnoreCase("break_castle") || args[0].equalsIgnoreCase("destroy_castle")) {
				Castle.deleteStructureAsync(FancyArena.instance, 5);
			}
			else if (args[0].equalsIgnoreCase("eat_castle_slow")) {
				Castle.deleteStructureAsync(FancyArena.instance, 40);
			}
			else if (args[0].equalsIgnoreCase("stop_tasks")) {
				killBigTasks();
			}
			else if (args[0].equalsIgnoreCase("list")) {
				sender.sendMessage("Listing Arenas:");
				for (String str : levels.keySet()) {
					sender.sendMessage(str);
				}
				return true;
			}
			else if (args[0].equalsIgnoreCase("ready")) {
				if (args.length > 1) {
					
					String arenaName = args[1];
					
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					
					if (!levels.containsKey(arenaName)) {
						sender.sendMessage(ChatColor.RED + arenaName + " doesn't exist, have you created the arena with '/fancyarena create'?");
						return true;
					}
					
					if (!currentLevel.containsKey(arenaName))
						currentLevel.put(arenaName, 0);
					
					if (getCurrentLevel(arenaName).getRunning()) {
						//sender.sendMessage(ChatColor.YELLOW + arenaName + " is already running, please do '/fancyarena stop' to end it before starting a new game.");
						sender.sendMessage(ChatColor.GREEN + "Sending you to " + arenaName);
						Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + player.getDisplayName() +  ChatColor.LIGHT_PURPLE + " joined arena " + arenaName + "!");
						/*if (player.getPassengers().size() > 0)
							player.removePassenger(player.getPassengers().get(0));*/
						FancyArena.prepare_teleport(player);
						player.teleport(getPlayerSpawn(arenaName));
						//return true;
					}
					
					removeTags(player, "Ready_");
					player.addScoreboardTag("Ready_" + arenaName);
					lives.put(player.getName(), FancyArena.maxLives);
					
					player.sendMessage(ChatColor.GREEN + "Ready!");

					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("stop")) {

				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				
				String arenaName = null;
				for (String tag : player.getScoreboardTags()) {
					if (tag.contains("Ready_")) {
						arenaName = tag.substring(tag.indexOf('_') + 1);
					}
				}
				if (args.length > 1) {
					arenaName = args[1];
				}
				if (arenaName != null) {

					stopArena(arenaName);

					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("insert")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				
				int index = levels.get(arenaName).indexOf(lvl);
				
				FancyLevel newLevel = new FancyLevel();
				newLevel.setArenaName(arenaName);
				
				levels.get(arenaName).add(index, newLevel);
				if (dialogs.containsKey(arenaName))
					dialogs.get(arenaName).insertLevel(index);
				player.sendMessage("Inserted a new level before the current level");
				
				return true;
			}
			else if (args[0].equalsIgnoreCase("copy") || args[0].equalsIgnoreCase("clone")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				String arenaName = lvl.getArenaName();
				
				int index = levels.get(arenaName).indexOf(lvl);
				
				if (index == 0) {
					sender.sendMessage(ChatColor.RED + "Can't use this command on Level 0");
					return true;
				}
				
				FancyLevel plvl = levels.get(arenaName).get(index-1);
				
				lvl.setChestLoots(plvl.getChestLoots());
				lvl.setMaxPos(plvl.getMaxPos());
				lvl.setMinPos(plvl.getMinPos());
				lvl.setMonsterSpawn(plvl.getMonsterSpawn());
				lvl.setPlayerSpawn(plvl.getPlayerSpawn());
				lvl.setMaxMonstersAlive(plvl.getMaxMonstersAlive());
				lvl.setNextWaveDelay(plvl.getNextWaveDelay());
				lvl.setWaveStartDelay(plvl.getWaveStartDelay());
				sender.sendMessage(ChatColor.GREEN + "Successfully copied over settings from " + plvl.getLevelName() + " to " + lvl.getLevelName());
				return true;
			} else if (args[0].equalsIgnoreCase("max_monsters")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = args[1];
					lvl.setMaxMonstersAlive(Integer.parseInt(arg));
					sender.sendMessage(ChatColor.GREEN + "Successfully set max_monsters to " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to set max_monsters");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("min_boss_health")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = args[1];
					lvl.setMinBossHealth(Integer.parseInt(arg));
					sender.sendMessage(ChatColor.GREEN + "Successfully set min_boss_health to " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to set max_monsters");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("name")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = "";
					for (int i = 1; i < args.length; i++) {
						arg += args[i] + " ";
					}
					lvl.setLevelName(arg);
					sender.sendMessage(ChatColor.GREEN + "Successfully set name to " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to set name");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("next_delay")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = args[1];
					lvl.setNextWaveDelay(Integer.parseInt(arg));
					sender.sendMessage(ChatColor.GREEN + "Successfully set next_delay to " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to set next_delay");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("start_delay")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = args[1];
					lvl.setWaveStartDelay(Integer.parseInt(arg));
					sender.sendMessage(ChatColor.GREEN + "Successfully set start_delay to " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to set start_delay");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("new_char")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying chat for level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String name = args[1];
					String nameColor = args[2];
					String textColor = args[3];
					
					if (!dialogs.containsKey(arenaName))
						dialogs.put(arenaName, new Dialogs(arenaName));
					Dialogs d = dialogs.get(arenaName);

					
					ChatMessage msg = new ChatMessage(name, "Hello there @p, my name is @s.", 0);
					
					d.addCharacter(name, nameColor, textColor);
					
					sender.sendMessage(ChatColor.GREEN + "Successfully added a new character to " + arenaName + ". Here is an example:");
					sender.sendMessage(d.constructMessage(msg, sender.getName(), sender.getName()));
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add new character");
				}
				return true;
			}else if (args[0].equalsIgnoreCase("start_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying chat for level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String ticks = args[1];
					String name = args[2];
					String message = args[3];
					int index = levels.get(arenaName).indexOf(lvl);
					for (int i = 4; i < args.length; i++) {
						message += " " + args[i];
					}
					if (!dialogs.containsKey(arenaName))
						dialogs.put(arenaName, new Dialogs(arenaName));
					Dialogs d = dialogs.get(arenaName);

					if (!d.startMessages.containsKey(index))
						d.startMessages.put(index, new ArrayList<ChatMessage>());
					
					ChatMessage msg = new ChatMessage(name, message, Integer.parseInt(ticks));
					d.startMessages.get(index).add(msg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added to start_chat, here's how it will look:");
					sender.sendMessage(d.constructMessage(msg, sender.getName(), sender.getName()));
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add to start_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("end_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying chat for level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String ticks = args[1];
					String name = args[2];
					String message = args[3];
					int index = levels.get(arenaName).indexOf(lvl);
					for (int i = 4; i < args.length; i++) {
						message += " " + args[i];
					}
					if (!dialogs.containsKey(arenaName))
						dialogs.put(arenaName, new Dialogs(arenaName));
					Dialogs d = dialogs.get(arenaName);

					if (!d.endMessages.containsKey(index))
						d.endMessages.put(index, new ArrayList<ChatMessage>());
					
					ChatMessage msg = new ChatMessage(name, message, Integer.parseInt(ticks));
					d.endMessages.get(index).add(msg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added to end_chat, here's how it will look:");
					sender.sendMessage(d.constructMessage(msg, sender.getName(), sender.getName()));
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add to end_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("rand_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying chat for level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String ticks = args[1];
					String name = args[2];
					String message = args[3];
					int index = levels.get(arenaName).indexOf(lvl);
					for (int i = 4; i < args.length; i++) {
						message += " " + args[i];
					}
					if (!dialogs.containsKey(arenaName))
						dialogs.put(arenaName, new Dialogs(arenaName));
					Dialogs d = dialogs.get(arenaName);

					if (!d.randomMessages.containsKey(index))
						d.randomMessages.put(index, new HashMap<String, ArrayList<ChatMessage>>());
					
					if (!d.randomMessages.get(index).containsKey(name))
						d.randomMessages.get(index).put(name, new ArrayList<ChatMessage>());
					
					ChatMessage msg = new ChatMessage(name, message, Integer.parseInt(ticks));
					d.randomMessages.get(index).get(name).add(msg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added to rand_chat, here's how it will look:");
					sender.sendMessage(d.constructMessage(msg, sender.getName(), sender.getName()));
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add to rand_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_start_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					Dialogs d = dialogs.get(arenaName);
					int index = levels.get(arenaName).indexOf(lvl);
					d.startMessages.get(index).clear();
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared start_chat");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear start_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_end_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					Dialogs d = dialogs.get(arenaName);
					int index = levels.get(arenaName).indexOf(lvl);
					d.endMessages.get(index).clear();
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared end_chat");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear end_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_rand_chat")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					Dialogs d = dialogs.get(arenaName);
					int index = levels.get(arenaName).indexOf(lvl);
					d.randomMessages.get(index).clear();
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared rand_chat");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear rand_chat");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_begin_cmd")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					lvl.setBeginCommands(null);
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared begin_cmd");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear begin_cmd");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_end_cmd")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					lvl.setEndCommands(null);
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared end_cmd");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear end_cmd");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_monsters")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					lvl.setMonsters(null);
					lvl.setBossMonsters(null);
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared monsters");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear monsters");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_monster_spawn")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					lvl.setMonsterSpawn(null);
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared monster_spawn");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear monster_spawn");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("clear_chest_loot")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					//String arg = args[1];
					lvl.setChestLoots(null);
					sender.sendMessage(ChatColor.GREEN + "Successfully cleared chest_loot");
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to clear chest_loot");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("add_begin_cmd")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = "";
					for (int i = 1; i < args.length; i++) {
						arg += args[i] + " ";
					}
					lvl.addBeginCommand(arg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added begin command " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add begin command");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("add_end_cmd")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				FancyLevel lvl = getSelectedLevel(player);
				if (lvl == null)
				{
					sender.sendMessage(ChatColor.RED + "Must be in setup mode to use this command");
					return true;
				}
				String arenaName = lvl.getArenaName();
				try {
					sender.sendMessage(ChatColor.YELLOW + "Modifying Level " + levels.get(arenaName).indexOf(lvl) + ": " + lvl.getLevelName());
					String arg = "";
					boolean finalCommand = false;
					for (int i = 1; i < args.length; i++) {
						if (i == 1 && args[i].equalsIgnoreCase("-f")) {
							finalCommand = true;
						} else
							arg += args[i] + " ";
					}
					if (finalCommand) {
						if (!finalCommands.containsKey(arenaName)) {
							finalCommands.put(arenaName, new ArrayList<String>());
						}
						finalCommands.get(arenaName).add(arg);
					}
					lvl.addEndCommand(arg);
					sender.sendMessage(ChatColor.GREEN + "Successfully added end command " + arg);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to add end command");
				}
				return true;
			} 
			else if (args[0].equalsIgnoreCase("info")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				
				String arenaName = null;
				
				FancyLevel sellvl = getSelectedLevel(player);
				if (sellvl != null)
				{
					arenaName = sellvl.getArenaName();
					
					sender.sendMessage(ChatColor.BOLD + sellvl.getLevelName());
					sender.sendMessage(ChatColor.YELLOW + "Player Spawn: " + ChatColor.WHITE + sellvl.getPlayerSpawn());
					int sumMonsters = 0;
					if (sellvl.getMonsters() != null)
						for (int i : sellvl.getMonsters().values())
							sumMonsters += i;
					sender.sendMessage(ChatColor.YELLOW + "Monsters: " + ChatColor.WHITE + sumMonsters);
					sumMonsters = 0;
					if (sellvl.getBossMonsters() != null)
						for (int i : sellvl.getBossMonsters().values())
							sumMonsters += i;
					sender.sendMessage(ChatColor.YELLOW + "Boss Monsters: " + ChatColor.WHITE + sumMonsters);
					sender.sendMessage(ChatColor.YELLOW + "Monster Spawnpoints: " + ChatColor.WHITE + (sellvl.getMonsterSpawn() == null ? 0 : sellvl.getMonsterSpawn().size()));
					sender.sendMessage(ChatColor.YELLOW + "Chest Loots: " + ChatColor.WHITE + (sellvl.getChestLoots() == null ? 0 : sellvl.getChestLoots().size()));
					sender.sendMessage(ChatColor.YELLOW + "Level Min Pos: " + ChatColor.WHITE + sellvl.getMinPos());
					sender.sendMessage(ChatColor.YELLOW + "Level Max Pos: " + ChatColor.WHITE + sellvl.getMaxPos());
					sender.sendMessage(ChatColor.YELLOW + "Begin Commands: " + ChatColor.WHITE + (sellvl.getBeginCommands() == null ? 0 : sellvl.getBeginCommands().size()));
					sender.sendMessage(ChatColor.YELLOW + "End Commands: " + ChatColor.WHITE + (sellvl.getEndCommands() == null ? 0 : sellvl.getEndCommands().size()));
					sender.sendMessage(ChatColor.YELLOW + "Next Wave Delay: " + ChatColor.WHITE + sellvl.getNextWaveDelay());
					sender.sendMessage(ChatColor.YELLOW + "Wave Start Delay: " + ChatColor.WHITE + sellvl.getWaveStartDelay());
					sender.sendMessage(ChatColor.YELLOW + "Max Monsters Alive: " + ChatColor.WHITE + sellvl.getMaxMonstersAlive());
					
					
					return true;
				}

				if (args.length > 1) {
					arenaName = args[1];
				}
				if (arenaName == null) {
					for (String tag : player.getScoreboardTags()) {
						if (tag.contains("Ready_")) {
							arenaName = tag.substring(tag.indexOf('_') + 1);
							break;
						} else if (tag.contains("Edit_")) {
							arenaName = tag.substring(tag.indexOf('_') + 1, tag.indexOf("--"));
							break;
						}
					}
				}
				if (arenaName == null) {
					sender.sendMessage(ChatColor.RED + "Must provide a valid arena name");
					return false;
				} else {
					
					sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Info for arena " + arenaName + ":");
					
					if (!currentLevel.containsKey(arenaName))
						currentLevel.put(arenaName, 0);
					sender.sendMessage("Current Level: " + currentLevel.get(arenaName));
					sender.sendMessage("Running: " + getCurrentLevel(arenaName).getRunning());
					
					int c = 0;
					for (FancyLevel lvl : levels.get(arenaName)) {
						sender.sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Level " + c + ": " + ChatColor.WHITE + lvl.getLevelName());
						/*
						sender.sendMessage(ChatColor.YELLOW + "Player Spawn: " + ChatColor.WHITE + lvl.getPlayerSpawn());
						int sumMonsters = 0;
						if (lvl.getMonsters() != null)
							for (int i : lvl.getMonsters().values())
								sumMonsters += i;
						sender.sendMessage(ChatColor.YELLOW + "Monsters: " + ChatColor.WHITE + sumMonsters);
						sumMonsters = 0;
						if (lvl.getBossMonsters() != null)
							for (int i : lvl.getBossMonsters().values())
								sumMonsters += i;
						sender.sendMessage(ChatColor.YELLOW + "Boss Monsters: " + ChatColor.WHITE + sumMonsters);
						sender.sendMessage(ChatColor.YELLOW + "Monster Spawnpoints: " + ChatColor.WHITE + (lvl.getMonsterSpawn() == null ? 0 : lvl.getMonsterSpawn().size()));
						sender.sendMessage(ChatColor.YELLOW + "Chest Loots: " + ChatColor.WHITE + (lvl.getChestLoots() == null ? 0 : lvl.getChestLoots().size()));
						sender.sendMessage(ChatColor.YELLOW + "Level Min Pos: " + ChatColor.WHITE + lvl.getMinPos());
						sender.sendMessage(ChatColor.YELLOW + "Level Max Pos: " + ChatColor.WHITE + lvl.getMaxPos());
						sender.sendMessage(ChatColor.YELLOW + "Begin Commands: " + ChatColor.WHITE + (lvl.getBeginCommands() == null ? 0 : lvl.getBeginCommands().size()));
						sender.sendMessage(ChatColor.YELLOW + "End Commands: " + ChatColor.WHITE + (lvl.getEndCommands() == null ? 0 : lvl.getEndCommands().size()));
						sender.sendMessage(ChatColor.YELLOW + "Next Wave Delay: " + ChatColor.WHITE + lvl.getNextWaveDelay());
						sender.sendMessage(ChatColor.YELLOW + "Wave Start Delay: " + ChatColor.WHITE + lvl.getWaveStartDelay());
						sender.sendMessage(ChatColor.YELLOW + "Max Monsters Alive: " + ChatColor.WHITE + lvl.getMaxMonstersAlive());
						*/
						c++;
					}
					
					return true;
				}
			}
			else if (args[0].equalsIgnoreCase("create")) {
				if (args.length > 1) {
					String arenaName = args[1];
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					
					/*
					ArmorStand manager = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
					manager.setGravity(false);
					manager.setMarker(true);
					manager.setInvulnerable(true);
					manager.setInvisible(true);
					manager.setCustomName(arenaName);
					manager.setCustomNameVisible(false);
					manager.addScoreboardTag("FancyArena");
					manager.addScoreboardTag("FancyLevel0");
					
					
					gm.put(arenaName, manager);
					player.getWorld().setChunkForceLoaded(player.getLocation().getBlockX(), player.getLocation().getBlockZ(), true);
					*/
					
					levels.put(arenaName, new ArrayList<FancyLevel>());
					
					FancyLevel lvl = new FancyLevel();
					lvl.setArenaName(arenaName);
					lvl.setPlayerSpawn(FancyLevel.locToString(player.getLocation()));
					levels.get(arenaName).add(lvl);
					
					currentLevel.put(arenaName, 0);
					
					sender.sendMessage(ChatColor.GREEN + "Successfully created arena: " + arenaName);
					sender.sendMessage(ChatColor.GREEN + "Set 1st spawnpoint of " + arenaName + " to " + player.getLocation().getBlockX() + " " + player.getLocation().getBlockY() + " " + player.getLocation().getBlockZ());
					sender.sendMessage(ChatColor.YELLOW + "To configure spawnpoints, use '/fancyarena setup <arena name>'");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Usage: /fancyarena create <name>");
					return false;
				}
			} else if (args[0].equalsIgnoreCase("setup")) {
				String arenaName = args[1];
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				
				Player player = (Player) sender;
				
				ItemStack playerSpawnTool = new ItemStack(Material.GOLDEN_AXE);
				ItemMeta meta = playerSpawnTool.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Player Spawnpoint");
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("Right click on a block");
				lore.add("to set player spawnpoint");
				lore.add("for the selected level");
				meta.setLore(lore);
				playerSpawnTool.setItemMeta(meta);
				player.getInventory().addItem(playerSpawnTool);
				
				ItemStack mobSpawnTool = new ItemStack(Material.GOLDEN_PICKAXE);
				meta = mobSpawnTool.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Monster Spawnpoints");
				lore.clear();
				lore.add("Right click on a block");
				lore.add("to set monster spawnpoint");
				lore.add("for the selected level");
				meta.setLore(lore);
				mobSpawnTool.setItemMeta(meta);
				player.getInventory().addItem(mobSpawnTool);
				
				ItemStack mobSelectTool = new ItemStack(Material.GOLDEN_SWORD);
				meta = mobSelectTool.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Monster Selection");
				lore.clear();
				lore.add("Right click to select");
				lore.add("which monsters spawn");
				lore.add("for the selected level");
				meta.setLore(lore);
				mobSelectTool.setItemMeta(meta);
				player.getInventory().addItem(mobSelectTool);
				
				ItemStack bossSelectTool = new ItemStack(Material.GOLD_BLOCK);
				meta = bossSelectTool.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Boss Selection");
				lore.clear();
				lore.add("Right click to select");
				lore.add("which bosses to spawn");
				lore.add("for the selected level");
				meta.setLore(lore);
				bossSelectTool.setItemMeta(meta);
				player.getInventory().addItem(bossSelectTool);
				
				ItemStack rewardSelectTool = new ItemStack(Material.CHEST);
				meta = rewardSelectTool.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Add Loot");
				lore.clear();
				lore.add("Left click on a chest");
				lore.add("containing loot to");
				lore.add("set it as a loot template ");
				lore.add("for selected level");
				meta.setLore(lore);
				rewardSelectTool.setItemMeta(meta);
				player.getInventory().addItem(rewardSelectTool);
				
				ItemStack nextButton = new ItemStack(Material.SPECTRAL_ARROW);
				meta = nextButton.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Next Level");
				lore.clear();
				lore.add("Right click to move");
				lore.add("to next level");
				meta.setLore(lore);
				nextButton.setItemMeta(meta);
				player.getInventory().addItem(nextButton);
				
				ItemStack previousButton = new ItemStack(Material.ARROW);
				meta = previousButton.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Previous Level");
				lore.clear();
				lore.add("Right click to move");
				lore.add("to previous level");
				meta.setLore(lore);
				previousButton.setItemMeta(meta);
				player.getInventory().addItem(previousButton);
				

				ItemStack pos1 = new ItemStack(Material.COPPER_INGOT);
				meta = pos1.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Set Pos 1");
				lore.clear();
				lore.add("Right click to set");
				lore.add("level bound position");
				meta.setLore(lore);
				pos1.setItemMeta(meta);
				player.getInventory().addItem(pos1);
				
				ItemStack pos2 = new ItemStack(Material.GOLD_INGOT);
				meta = pos2.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW + "---Set Pos 2");
				lore.clear();
				lore.add("Right click to set");
				lore.add("level bound position");
				meta.setLore(lore);
				pos2.setItemMeta(meta);
				player.getInventory().addItem(pos2);
				
				player.addScoreboardTag("Edit_" + arenaName + "--0");
				FancyLevel lvl = levels.get(arenaName).get(0);
				/*if (player.getPassengers().size() > 0)
					player.removePassenger(player.getPassengers().get(0));*/
				FancyArena.prepare_teleport(player);
				player.teleport(lvl.getPlayerSpawnLoc());
				player.sendMessage(ChatColor.YELLOW + "Now editing " + arenaName + ", do " + ChatColor.WHITE + ChatColor.BOLD + "/fancyarena done" + ChatColor.RESET + ChatColor.YELLOW + " to exit editing mode.");
				
				return true;
				
			} else if (args[0].equalsIgnoreCase("done")) {
				if (!(sender instanceof Player)) {
		            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
		            return true;
		        }
				Player player = (Player) sender;
				player.sendMessage("Exiting edit mode");
				
				
				removeTags(player, "Edit_");
				player.sendMessage("Removed tags");
				
				ArrayList<ItemStack> removeItems = new ArrayList<ItemStack>();
				
				for (ItemStack yeet : player.getInventory().getContents()) {
					if (yeet != null && yeet.getItemMeta().getDisplayName().contains("---")) {
						//yeet.setAmount(0);
						//player.getInventory().remove(yeet);
						removeItems.add(yeet);
						//removeItems.add(yeet);
					}
				}
				player.sendMessage("Finding items to remove");
				
				for (ItemStack yeet : removeItems) {
					player.getInventory().remove(yeet);
				}
				
				player.sendMessage(ChatColor.YELLOW + "Successfully exited editing mode");
				return true;
				
			}
			else if (args[0].equalsIgnoreCase("clearfire")) {
				
				if (args.length == 2) {
					if (!(sender instanceof Player)) {
			            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
			            return true;
			        }
					Player player = (Player) sender;
					try {
						int dist = Integer.parseInt(args[1]);
						org.bukkit.Location l = player.getLocation();
						ClearFire(l.getWorld(), l.getBlockX()-dist, l.getBlockY()-dist, l.getBlockZ()-dist, l.getBlockX()+dist, l.getBlockY()+dist, l.getBlockZ()+dist, player);
						
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /fancyarena clearfire <radius>\nOr: /fancyarena clearfire <x1> <y1> <z1> <x2> <y2> <z2>");
					}
				} else if (args.length > 6) {
					try {
						World world = null;
						if (sender instanceof Player player) {
							world = player.getWorld();
						} else {
							for (World w : Bukkit.getWorlds()) {
								if (w.getPlayers().size() > 0) {
									world = w;
									break;
								}
							}
						}
						if (world != null)
						ClearFire(world, Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]));
					} catch (Exception e) {
						sender.sendMessage(ChatColor.RED + "Usage: /fancyarena clearfire <radius>\nOr: /fancyarena clearfire <x1> <y1> <z1> <x2> <y2> <z2>");
					}
				}
				
				return true;
			}
		}
		
		
		return false;
	}
	
	public static FancyLevel getSelectedLevel(Player player) {
		for (String tag : player.getScoreboardTags()) {
			if (tag.contains("Edit_")) {
				String arena = tag.substring(tag.indexOf('_')+1, tag.indexOf("--"));
				int levelNumber = Integer.parseInt(tag.substring(tag.indexOf("--") + 2));
				
				return getLevel(arena, levelNumber);
			}
		}
		return null;
	}
	
	public static FancyLevel getLevel(String arena, int levelNumber) {
		while (levels.get(arena).size()-1 < levelNumber) {
			FancyLevel lvl = new FancyLevel();
			lvl.setArenaName(arena);
			levels.get(arena).add(lvl);
			Bukkit.broadcastMessage(ChatColor.BLUE + "Creating level #" + levels.get(arena).size() + " for arena " + arena);
		}
		
		return levels.get(arena).get(levelNumber);
	}
	
	public static void AddMonster(Player player, String monster) {
		getSelectedLevel(player).addMonster(monster);
	}
	
	public static void AddBoss(Player player, String monster) {
		getSelectedLevel(player).addBossMonster(monster);
	}
	
	public static void findFancyMobTemplates() {
		//ArrayList<ArmorStand> result = new ArrayList<ArmorStand>();
		fancyMobTemplates.clear();
		Chunk c1 = FancyMob.templateWorld.getChunkAt(new Location(FancyMob.templateWorld, 6972, 88, 7016));
		Chunk c2 = FancyMob.templateWorld.getChunkAt(new Location(FancyMob.templateWorld, 6972, 88, 7030));
		Chunk c3 = FancyMob.templateWorld.getChunkAt(new Location(FancyMob.templateWorld, 6972, 88, 7044));
		c1.setForceLoaded(true);
		c2.setForceLoaded(true);
		c3.setForceLoaded(true);
		for (World w : Bukkit.getWorlds()) {
			for (LivingEntity e : w.getLivingEntities()) {
				if (e instanceof ArmorStand stand) {
					if (stand.getScoreboardTags().contains("FancyMobTemplate")) {
						//result.add((ArmorStand) e);
						fancyMobTemplates.put(e.getCustomName(), stand);
					}
				}
			}
		}
		//return result;
	}
	
	/*
	public static ArrayList<ArmorStand> getFancyMobTemplates() {
		if (fancyMobTemplates == null || fancyMobTemplates.size() == 0) {
			findFancyMobTemplates();
		}
		return fancyMobTemplates.values();
	}*/
	
	public static boolean validTarget(Entity entity) {
		if (entity != null && (entity.getScoreboardTags().contains("Maskirovka") || entity instanceof Player || (entity instanceof LivingEntity le && le.getType() != EntityType.ARMOR_STAND && le.getType() != EntityType.ITEM && !(entity instanceof Hangable) && !entity.getScoreboardTags().contains("invulnerable")))) {
			return true;
		}
		return false;
	}
	
	public static void ArenaEditItem(Player player, ItemStack item, Block block) {
		ItemMeta meta = item.getItemMeta();
		String itemName = meta.getDisplayName();
		
		if (player.getCooldown(item.getType()) > 0) {
			return;
		}
		
		if (block.getType() == Material.AIR || block.getType() == Material.WATER) {
			block = null;
		}
		
		if (itemName.contains("Add Loot")) {
			if (block == null) {
				player.sendMessage("Must click on a block");
				return;
			} else {
				if (block.getType() == Material.BARREL) {
					getSelectedLevel(player).addChestLoot(FancyLevel.locToString(block.getLocation()));
					player.sendMessage("Added barrel to chest loots");
				} else {
					player.sendMessage("Must click on a block with storage, you clicked on " + block.getType());
					return;
				}
			}
		} else if (itemName.contains("Monster Selection") || itemName.contains("Boss Selection")) {
			/*Location loc = player.getLocation();
			if (block != null) {
				loc = block.getLocation();
			}*/
			/*
			boolean boss = (itemName.contains("Boss"));
			player.sendMessage(ChatColor.BLUE + "Opening " + (boss ? "boss" : "monster") + " selector");
			
			Inventory fancyInv = Bukkit.createInventory(null, 54, boss ? "Select Bosses" : "Select Monsters");
			// Add all spawn eggs to the inventory

	        for (ArmorStand stand : getFancyMobTemplates()) {
	        	if (stand.getCustomName().contains("Mercinary")) continue;
	        	ItemStack headItem = stand.getEquipment().getHelmet().clone();
	        	if (headItem == null) {
	        		headItem = new ItemStack(Material.PLAYER_HEAD);
	        	}
	        	ItemMeta headMeta = headItem.getItemMeta();
	        	headMeta.removeEnchantments();
	        	headMeta.removeAttributeModifier(EquipmentSlot.BODY);
	        	headMeta.removeAttributeModifier(EquipmentSlot.CHEST);
	        	headMeta.removeAttributeModifier(EquipmentSlot.HEAD);
	        	headMeta.removeAttributeModifier(EquipmentSlot.LEGS);
	        	headMeta.removeAttributeModifier(EquipmentSlot.FEET);
	        	headMeta.removeAttributeModifier(EquipmentSlot.HAND);
	        	headMeta.removeAttributeModifier(EquipmentSlot.OFF_HAND);
	        	headMeta.setDisplayName(stand.getCustomName());
	        	headItem.setItemMeta(headMeta);
	        	
	        	fancyInv.addItem(headItem);
	        }

			player.openInventory(fancyInv);
			*/
			
			 boolean boss = itemName.contains("Boss");
			 player.sendMessage(ChatColor.BLUE + "Opening " + (boss ? "boss" : "monster") + " selector");
			 openSelectionInventory(player, boss, 1); // Open page 1
			
		} else if (itemName.contains("Monster Spawnpoints")) {
			Location loc = player.getLocation();
			if (block != null) {
				loc = block.getLocation().clone().add(0, 1, 0);
			}

			String locStr = FancyLevel.locToString(loc.getBlock().getLocation());
			getSelectedLevel(player).addMonsterSpawnLoc(loc.getBlock().getLocation());
			
			player.sendMessage(ChatColor.BLUE + "Added monster spawn at " + locStr);
			
		} else if (itemName.contains("Player Spawnpoint")) {
			Location loc = player.getLocation();
			if (block != null) {
				loc = block.getLocation().clone().add(0, 1, 0);
			}
			
			String locStr = FancyLevel.locToString(loc.getBlock().getLocation());
			getSelectedLevel(player).setPlayerSpawn(locStr);
			
			player.sendMessage(ChatColor.BLUE + "Set player spawn to " + locStr);
			
		} else if (itemName.contains("Next Level")) {
			String tagToRemove = "";
			for (String tag : player.getScoreboardTags()) {
				if (tag.contains("Edit_")) {
					String arena = tag.substring(tag.indexOf('_')+1, tag.indexOf("--"));
					int levelNumber = Integer.parseInt(tag.substring(tag.indexOf("--") + 2));
					
					levelNumber++;
					
					tagToRemove = tag;
					player.addScoreboardTag("Edit_" + arena + "--" + levelNumber);
					
					player.sendMessage(ChatColor.GREEN + "Now editing level " + levelNumber);
					
					FancyLevel lvl = getLevel(arena, levelNumber);
					
					if (lvl.getPlayerSpawn() != null && lvl.getPlayerSpawn().length() > 1) {
						player.teleport(lvl.getPlayerSpawnLoc());
					}
					
					break;
				}				
			}

			player.removeScoreboardTag(tagToRemove);
		} else if (itemName.contains("Previous Level")) {
			String tagToRemove = "";
			for (String tag : player.getScoreboardTags()) {
				if (tag.contains("Edit_")) {
					String arena = tag.substring(tag.indexOf('_')+1, tag.indexOf("--"));
					int levelNumber = Integer.parseInt(tag.substring(tag.indexOf("--") + 2));
					
					levelNumber--;
					if (levelNumber < 0)
						levelNumber = levels.get(arena).size()-1;
					tagToRemove = tag;
					//player.removeScoreboardTag(tag);
					player.addScoreboardTag("Edit_" + arena + "--" + levelNumber);

					FancyLevel lvl = getLevel(arena, levelNumber);
					
					player.sendMessage(ChatColor.GREEN + "Now editing level " + levelNumber + ": " + lvl.getLevelName());
					
					if (lvl.getPlayerSpawn() != null && lvl.getPlayerSpawn().length() > 1) {
						player.teleport(lvl.getPlayerSpawnLoc());
					}
					
					break;
				}				
			}
			player.removeScoreboardTag(tagToRemove);
		} else if (itemName.contains("Set Pos 1")) {
			Location loc = player.getLocation();
			if (block != null) {
				loc = block.getLocation();
			}
			FancyLevel lvl = getSelectedLevel(player);
			lvl.setMinPos(FancyLevel.locToString(loc.getBlock().getLocation()));
			
			/*
			if (lvl.getMaxPos() != null && lvl.getMaxPos().length() > 0) {
				Location maxP = lvl.getMaxPosLoc();
				Location minP = lvl.getMinPosLoc();
				
				if (maxP.getX() < minP.getX()) {
					double temp = maxP.getX();
					maxP.setX(minP.getX());
					minP.setX(temp);
				}

				if (maxP.getY() < minP.getY()) {
					double temp = maxP.getY();
					maxP.setY(minP.getY());
					minP.setY(temp);
				}

				if (maxP.getZ() < minP.getZ()) {
					double temp = maxP.getZ();
					maxP.setZ(minP.getZ());
					minP.setZ(temp);
				}
			}*/
			
			player.sendMessage(ChatColor.BLUE + "Set level min pos to " + lvl.getMinPos());
			
		} else if (itemName.contains("Set Pos 2")) {
			Location loc = player.getLocation();
			if (block != null) {
				loc = block.getLocation();
			}
			FancyLevel lvl = getSelectedLevel(player);
			lvl.setMaxPos(FancyLevel.locToString(loc.getBlock().getLocation()));
			
			/*
			if (lvl.getMinPos() != null && lvl.getMinPos().length() > 0) {
				Location maxP = lvl.getMaxPosLoc();
				Location minP = lvl.getMinPosLoc();
				
				if (maxP.getX() < minP.getX()) {
					double temp = maxP.getX();
					maxP.setX(minP.getX());
					minP.setX(temp);
				}

				if (maxP.getY() < minP.getY()) {
					double temp = maxP.getY();
					maxP.setY(minP.getY());
					minP.setY(temp);
				}

				if (maxP.getZ() < minP.getZ()) {
					double temp = maxP.getZ();
					maxP.setZ(minP.getZ());
					minP.setZ(temp);
				}
			} */

			player.sendMessage(ChatColor.BLUE + "Set level max pos to " + lvl.getMaxPos());
		}
		
		player.setCooldown(item.getType(), 5);
	}
	
	// Helper method to open the selection inventory with pagination
	public static void openSelectionInventory(Player player, boolean boss, int page) {
	    int itemsPerPage = 45; // Slots 0 to 44 for monster items
	    String title = (boss ? "Select Bosses" : "Select Monsters") + " - Page " + page;
	    Inventory inv = Bukkit.createInventory(null, 54, title);

	    // Build list of item stacks from your fancy mob templates (excluding Mercinary)
	    findFancyMobTemplates();
	    List<ItemStack> monsterItems = new ArrayList<>();
	    for (ArmorStand stand : fancyMobTemplates.values()) {
	        //if (stand.getCustomName().contains("Mercinary")) {
	        //    continue;
	        //}
	        ItemStack headItem = stand.getEquipment().getHelmet();
	        if (headItem == null) {
	            headItem = new ItemStack(Material.PLAYER_HEAD);
	        } else {
	            headItem = headItem.clone();
	        }
	        ItemMeta headMeta = headItem.getItemMeta();
	        headMeta.removeEnchantments();
	        headMeta.removeAttributeModifier(EquipmentSlot.BODY);
	        headMeta.removeAttributeModifier(EquipmentSlot.CHEST);
	        headMeta.removeAttributeModifier(EquipmentSlot.HEAD);
	        headMeta.removeAttributeModifier(EquipmentSlot.LEGS);
	        headMeta.removeAttributeModifier(EquipmentSlot.FEET);
	        headMeta.removeAttributeModifier(EquipmentSlot.HAND);
	        headMeta.removeAttributeModifier(EquipmentSlot.OFF_HAND);
	        headMeta.setDisplayName(stand.getCustomName());
	        headItem.setItemMeta(headMeta);
	        monsterItems.add(headItem);
	    }
	    
	    // Calculate start and end indices for this page
	    int start = (page - 1) * itemsPerPage;
	    int end = Math.min(start + itemsPerPage, monsterItems.size());
	    // Add the subset of items into the inventory
	    for (int i = start; i < end; i++) {
	        inv.addItem(monsterItems.get(i));
	    }
	    
	    // Add navigation arrows if needed
	    if (page > 1) {
	        ItemStack prev = new ItemStack(Material.ARROW);
	        ItemMeta prevMeta = prev.getItemMeta();
	        prevMeta.setDisplayName("Previous Page");
	        prev.setItemMeta(prevMeta);
	        inv.setItem(45, prev); // Slot 45 reserved for previous page
	    }
	    if (end < monsterItems.size()) {
	        ItemStack next = new ItemStack(Material.ARROW);
	        ItemMeta nextMeta = next.getItemMeta();
	        nextMeta.setDisplayName("Next Page");
	        next.setItemMeta(nextMeta);
	        inv.setItem(53, next); // Slot 53 reserved for next page
	    }
	    
	    player.openInventory(inv);
	}


	private static boolean isMonster(String monsterName) {
		// TODO Auto-generated method stub
		
		String[] monsters = new String[] {
				"ZOMBIE",
				"SKELETON",
				"CREEPER",
				"WOLF",
				"BLAZE",
				"BOGGED",
				"BREEZE",
				"CAVE_SPIDER",
				"DROWNED",
				"ELDER_GUARDIAN",
				"ENDERMAN",
				"EVOKER",
				"GHAST",
				"GOAT",
				"GUARDIAN",
				"HOGLIN",
				"HUSK",
				"IRON_GOLEM",
				"LLAMA",
				"MAGMA_CUBE",
				"SLIME",
				"PHANTOM",
				"PIGLIN",
				"PIGLIN_BRUTE",
				"PILLAGER",
				"POLAR_BEAR",
				"RAVAGER",
				"SILVERFISH",
				"SPIDER",
				"STRAY",
				"VEX",
				"VINDICATOR",
				"WARDEN",
				"WITCH",
				"WITHER_SKELETON",
				"WOLF",
				"ZOGLIN",
				"ZOMBIE_VILLAGER",
				"ZOMBIFIED_PIGLIN"
		};
		
		for (String m : monsters) {
			if (monsterName.equalsIgnoreCase(m))
				return true;
		}
		
		return false;
	}

}
