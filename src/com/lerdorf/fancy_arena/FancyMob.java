package com.lerdorf.fancy_arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dropper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
//import org.bukkit.entity.Giant;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.yaml.snakeyaml.util.EnumUtils;

import de.tr7zw.nbtapi.NBT;
import net.md_5.bungee.api.ChatColor;

public class FancyMob implements CommandExecutor {

	public String helpString = "/fancymob template (creates a template at your position)";
	
	public boolean isNumeric(String s) {
		//boolean result = true;
		char[] nums = { '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ' '};
		for (char c : s.toCharArray()) {
			boolean isNumber = false;
			for (char n : nums) {
				if (n == c) {
					//return true;
					isNumber = true;
					break;
				}
			}
			if (!isNumber)
				return false;
		}
		return true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("fancymob")) {

			Player p = sender instanceof Player player ? player : null;
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("spawn") && args.length >= 2) {
					String entityName = "";
					int k = 0;
					float x = 0;
					float y = 0;
					float z = 0;
					float yaw = 0;
					float pitch = 0;
					try {
						for (int i = 1; i < args.length; i++) {
							if (isNumeric(args[i])) {
								//Bukkit.broadcastMessage("Found number: " + args[i]);
								if (k == 0)
									x = Float.parseFloat(args[i].strip());
								else if (k == 1)
									y = Float.parseFloat(args[i].strip());
								else if (k == 2)
									z = Float.parseFloat(args[i].strip());
								else if (k == 3)
									yaw = Float.parseFloat(args[i].strip());
								else if (k == 4)
									pitch = Float.parseFloat(args[i].strip());
								k++;
							} else {
								entityName += args[i];
								if (i < args.length-1)
									entityName += " ";
							}
						}
						//Bukkit.broadcastMessage("Name: " + entityName + " k: " + k);
						if (p == null || k >= 2) {
							if (p == null) {
								for (Player player : Bukkit.getOnlinePlayers()) {
									if (p == null)
										p = player;
									for (String tag : player.getScoreboardTags()) {
										if (tag.contains("Ready_")) {
											p = player;
											break;
										}
									}
								}
							}
							if (k >= 2) {
								//Bukkit.broadcastMessage("Spawning " + entityName + ChatColor.WHITE + " at " + x + " " + y + " " + z + " " + pitch + " " + yaw + " in world " + p.getWorld());
								Spawn(new Location(p.getWorld(), x, y, z, yaw, pitch), entityName);
							}
						}
						else 
							Spawn(p.getLocation(), entityName);
					} catch (Exception e) {
						if (p != null) {
							entityName = "";
							for (int i = 1; i < args.length; i++) {
									entityName += args[i];
									if (i < args.length-1)
										entityName += " ";
							}
							Spawn(p.getLocation(), entityName);
						}
					}
					return true;
				}
			
			if (!(sender instanceof Player)) {
	            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
	            return true;
	        }
			
			//Player p = (Player)sender;
			
			
				if (args[0].equalsIgnoreCase("template")) {
					Entity entity = p.getLocation().getBlock().getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.ARMOR_STAND);
					entity.getLocation().setDirection(p.getLocation().getDirection());
					
					if (entity instanceof ArmorStand armorStand) {
						
						armorStand.setArms(true);
						armorStand.setBasePlate(false);
						armorStand.setGravity(false);
						armorStand.addScoreboardTag("FancyMobTemplate");
					
						Block block = p.getLocation().add(0, -1, 0).getBlock();
						block.setType(Material.CHEST);
						Chest chest = (Chest) block.getState();
						Inventory inv = chest.getInventory();
						chest.setCustomName("Mob Attributes");
						chest.update();
						
						ItemStack egg = new ItemStack(Material.ZOMBIE_SPAWN_EGG);
						ItemMeta meta = egg.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						lore.add("Replace with a spawn egg for the monster type");
						meta.setLore(lore);
						egg.setItemMeta(meta);
						egg.setAmount(1);
						inv.addItem(egg);
						
						ItemStack nametag = new ItemStack(Material.NAME_TAG);
						meta = nametag.getItemMeta();
						lore.clear();
						lore.add("Rename to the desired name");
						lore.add("Names should be unique");
						meta.setLore(lore);
						meta.setDisplayName("Fancy Mob");
						nametag.setItemMeta(meta);
						nametag.setAmount(1);
						inv.addItem(nametag);
						
						ItemStack health = new ItemStack(Material.BEEF);
						meta = health.getItemMeta();
						meta.setDisplayName("Health");
						lore.clear();
						lore.add("The mob's health in full hearts");
						lore.add("(remove to use base health)");
						meta.setLore(lore);
						health.setItemMeta(meta);
						health.setAmount(10);
						inv.addItem(health);
						
						ItemStack speed = new ItemStack(Material.FEATHER);
						meta = speed.getItemMeta();
						meta.setDisplayName("Speed");
						lore.clear();
						lore.add("The mob's speed");
						lore.add("(10 is normal)");
						meta.setLore(lore);
						speed.setItemMeta(meta);
						speed.setAmount(10);
						inv.addItem(speed);
	
						ItemStack jumpHeight = new ItemStack(Material.RABBIT_HIDE);
						meta = jumpHeight.getItemMeta();
						meta.setDisplayName("Jump Height");
						lore.clear();
						lore.add("The mob's jump height");
						lore.add("(2 is normal)");
						meta.setLore(lore);
						jumpHeight.setItemMeta(meta);
						jumpHeight.setAmount(2);
						inv.addItem(jumpHeight);
	
						ItemStack attackDamage = new ItemStack(Material.PRISMARINE_SHARD);
						meta = attackDamage.getItemMeta();
						meta.setDisplayName("Attack Damage");
						lore.clear();
						lore.add("The mob's attack damage");
						lore.add("(remove to use equipment)");
						meta.setLore(lore);
						attackDamage.setItemMeta(meta);
						attackDamage.setAmount(3);
						inv.addItem(attackDamage);
	
						ItemStack armor = new ItemStack(Material.NETHERITE_SCRAP, 1);
						meta = armor.getItemMeta();
						meta.setDisplayName("Armor");
						lore.clear();
						lore.add("The mob's armor");
						lore.add("(remove to use equipped armor)");
						meta.setLore(lore);
						armor.setItemMeta(meta);
						armor.setAmount(10);
						inv.addItem(armor);
						
						ItemStack dropChance = new ItemStack(Material.DROPPER);
						meta = dropChance.getItemMeta();
						meta.setDisplayName("Drop Chance");
						lore.clear();
						lore.add("The mob's equipment drop chance");
						lore.add("(out of 20)");
						meta.setLore(lore);
						dropChance.setItemMeta(meta);
						dropChance.setAmount(5);
						inv.addItem(dropChance);
						
						ItemStack livingSound = new ItemStack(Material.PLAYER_HEAD);
						meta = livingSound.getItemMeta();
						meta.setDisplayName("entity.villager.ambient");
						lore.clear();
						lore.add("Set the name to the sound");
						lore.add("the mob should make when alive");
						meta.setLore(lore);
						livingSound.setItemMeta(meta);
						livingSound.setAmount(1);
						inv.addItem(livingSound);
						
						ItemStack hurtSound = new ItemStack(Material.ZOMBIE_HEAD);
						meta = hurtSound.getItemMeta();
						meta.setDisplayName("entity.villager.hurt");
						lore.clear();
						lore.add("Set the name to the sound");
						lore.add("the mob should make when hurt");
						meta.setLore(lore);
						hurtSound.setItemMeta(meta);
						hurtSound.setAmount(1);
						inv.addItem(hurtSound);
						
						ItemStack deadSound = new ItemStack(Material.SKELETON_SKULL);
						meta = deadSound.getItemMeta();
						meta.setDisplayName("entity.villager.death");
						lore.clear();
						lore.add("Set the name to the sound");
						lore.add("the mob should make when killed");
						meta.setLore(lore);
						deadSound.setItemMeta(meta);
						deadSound.setAmount(1);
						inv.addItem(deadSound);

						block = p.getLocation().add(0, -2, 0).getBlock();
						block.setType(Material.DROPPER);
						Dropper dropper = (Dropper) block.getState();
						inv = dropper.getInventory();
						dropper.setCustomName("Mob Loot");
						dropper.update();
						inv.addItem(new ItemStack(Material.GOLD_NUGGET, 1));
					}
				}
			}
		}
		
		return false;
	}
	
	public static ItemStack[] GetDrops(Location loc, String name) {
		//List<Entity> entities = templateWorld.getEntities();
		ArmorStand template = GetTemplate(name);
		
		if (template != null) {
			Block block = template.getLocation().clone().add(0, -2, 0).getBlock();
			if (block.getType() != Material.DROPPER) {
				Bukkit.broadcastMessage("Template for \"" + name + "\" is missing the dropper!");
				return null;
			}
			Dropper dropper = (Dropper) block.getState();
			return dropper.getInventory().getContents().clone();
		} else
			return null;
	}
	
	public static double GetDropChance(Location loc, String name) {
		//List<Entity> entities = templateWorld.getEntities();
		ArmorStand template = GetTemplate(name);
		
		if (template != null) {
			Block block = template.getLocation().clone().add(0, -1, 0).getBlock();
			if (block.getType() != Material.CHEST) {
				Bukkit.broadcastMessage("Template for \"" + name + "\" is missing the chest!");
				return 0;
			}
			Chest chest = (Chest) block.getState();
			for (ItemStack item : chest.getInventory().getContents()) {
				if (item != null) {
					if (item.getItemMeta().getDisplayName().contains("Drop Chance")) {
						return item.getAmount() * 0.05;
					}
				}
			}
		} else
			return 0;
		return 0;
	}
	
	public static World templateWorld = Bukkit.getWorld("aeons_lobby");
	
	public static ArmorStand GetTemplate(String name) {
		if (name == null || name.length() == 0) return null;

		ArmorStand template = null;
		
		if (Arena.fancyMobTemplates.containsKey(name)) {
			template = Arena.fancyMobTemplates.get(name);
		} else {
			for (String n : Arena.fancyMobTemplates.keySet()) {
				String strippedName = ChatColor.stripColor(n) != null ? ChatColor.stripColor(n).trim() : n.trim();
		    	String strippedName2 = ChatColor.stripColor(name) != null ? ChatColor.stripColor(name).trim() : name.trim();
		    	if (strippedName.equalsIgnoreCase(strippedName2)) {
		    		template = Arena.fancyMobTemplates.get(n);
			        break;
		    	}
			}
		}
		if (template == null) {
			//Bukkit.broadcastMessage("No template found by the name \"" + name + "\"");
			List<Entity> entities = templateWorld.getEntities();
			for (Entity e : entities) {
			    if (e.getScoreboardTags().contains("FancyMobTemplate") && e.getType() == EntityType.ARMOR_STAND && e.getCustomName() != null && e.getCustomName().length() > 0) {
			    	//Bukkit.broadcastMessage("Comparing '" +ChatColor.stripColor(e.getCustomName()).trim() + "' to '" + ChatColor.stripColor(name).trim() + "'");
			    	String strippedName = ChatColor.stripColor(e.getCustomName()) != null ? ChatColor.stripColor(e.getCustomName()).trim() : e.getCustomName().trim();
			    	String strippedName2 = ChatColor.stripColor(name) != null ? ChatColor.stripColor(name).trim() : name.trim();
			    	if (strippedName.equalsIgnoreCase(strippedName2)) {
			    		template = (ArmorStand) e;
				        break;
			    	}
			    }
			}
			if (template != null) {
				Arena.fancyMobTemplates.put(template.getCustomName(), template);
				return template;
			} else {
				return null;
			}
		} else {
			return template;
		}
	}
	
	public static void SwapWeapon(LivingEntity entity) {
		ArmorStand template = GetTemplate(entity.getCustomName());
		if (template == null)
			entity.addScoreboardTag("Minion");
		else {
			Block block = template.getLocation().clone().add(0, -1, 0).getBlock();
			if (block.getType() != Material.CHEST) {
				Bukkit.broadcastMessage("Template for \"" + entity.getCustomName() + "\" is missing the chest!");
				return;
			}
			Chest chest = (Chest) block.getState();
			Inventory inv = chest.getInventory();
			
			ArrayList<Integer> index = new ArrayList<Integer>();
			ItemStack[] contents = inv.getContents();
			for (int i = contents.length - 9; i < contents.length; i++) {
				if (contents[i] != null) {
					//Bukkit.broadcastMessage("Found weapon");
					index.add(i);
				}
			}
			
			//Bukkit.broadcastMessage("Choosing between " + (index.size() + 1) + " weapons");
			
			if (index.size() > 0) {
				int randomIndex = (int)(Math.random() * (index.size() + 1));
				//Bukkit.broadcastMessage("Random index = " + randomIndex);
				if (randomIndex >= index.size()) {
					SetHandItems(entity, template.getEquipment().getItemInMainHand(), template.getEquipment().getItemInOffHand());
				} else {
					
					ItemStack newItem = contents[index.get(randomIndex)];
					if (newItem.hasItemMeta() && newItem.getItemMeta().getDisplayName().contains("Swap Places")) {
						try {
							EntityType type = entity.getType();
							Entity swapEntity = null;
							for (Entity e : entity.getWorld().getNearbyEntities(entity.getLocation(), 20, 20, 20)) {
								if (e.getType() == type) {
									if (swapEntity == null || Math.random() < 0.4) {
										swapEntity = e;
									}
								}
							}
							if (swapEntity != null) {
								Location swapPos = swapEntity.getLocation().clone();
								Location ogPos = entity.getLocation().clone();
								FancyArena.instance.delayedTeleport.put((LivingEntity)swapEntity, ogPos);
								FancyArena.instance.delayedTeleport.put(entity, swapPos);
								for (Player p : Bukkit.getOnlinePlayers())
									p.playSound(swapPos, Sound.ENTITY_WITHER_SPAWN, 1, 2);
								swapPos.getWorld().spawnParticle(Particle.PORTAL, swapPos, 20);
								ogPos.getWorld().spawnParticle(Particle.PORTAL, ogPos, 20);
							}
						} catch (Exception e) {
							
						}
						return;
					}
					else if (newItem.hasItemMeta() && newItem.getItemMeta().getDisplayName().contains("Summon Echo")) {
						if (entity.getScoreboardTags().contains("Echo")) return;
						SpawnEcho(entity);
						if (Math.random() < 0.5f)
							SpawnEcho(entity);
						return;
					}
					else if (newItem.getType() == Material.POTION) {
						PotionMeta meta = (PotionMeta) newItem.getItemMeta();
						//Bukkit.broadcastMessage("Attempting to drink potion");
						if (meta != null) {
							try {
								//Bukkit.broadcastMessage("Drinking potion");
								//entity.addPotionEffects(meta.getCustomEffects());
								if (meta.hasBasePotionType()) {
									for (PotionEffect effect : meta.getBasePotionType().getPotionEffects()) {
										//Bukkit.broadcastMessage("Applying effect " + effect.getType().toString().toLowerCase());
										entity.addPotionEffect(effect);
									}
								}
								for (PotionEffect effect : meta.getCustomEffects()) {
									//Bukkit.broadcastMessage("Applying effect " + effect.getType().toString().toLowerCase());
									entity.addPotionEffect(effect);
									//effect.apply(entity);
									//FancyArena.addPotionEffect(entity, effect);
								}
								SFX.play(entity.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
							} catch (Exception e) {}
							return;
						}
					}
					else if (newItem.getType().toString().contains("SPAWN_EGG")) {
						//Bukkit.broadcastMessage("Attempting to spawn minions");
						SpawnEggMeta meta = ((SpawnEggMeta) newItem.getItemMeta());
						if (meta != null) {
							String arenaName = null;
							for (String tag : entity.getScoreboardTags()) {
								if (tag.contains("Arena_")) {
									arenaName = tag.substring(tag.indexOf("_")+1);
								}
							}
							String minionType = null;
							//Bukkit.broadcastMessage("Creating minions");
							for (int i = 0; i < newItem.getAmount(); i++) {
								//Bukkit.broadcastMessage("New minion");
								EntitySnapshot snapshot = meta.getSpawnedEntity();
								LivingEntity minion = null;
								EntityType eType = null;
								if (snapshot != null)
									eType = snapshot.getEntityType();
								else {
									String type = newItem.getType().toString().substring(0, newItem.getType().toString().indexOf('_'));
									eType = EntityType.valueOf(type.toUpperCase().strip());
								}
								if (eType != null) {
									if (FancyArena.instance.minions.containsKey(entity)) {
										int minionCount = 0;
										/*
										for (String tag : entity.getScoreboardTags()) {
											if (tag.contains("spawnedminion_")) {
												String tempMinionType = tag.substring(tag.indexOf('_')+1);
												if (tempMinionType.equals(eType.toString())) {
													// The minion has already been spawned
													return;
												}
											}
										}*/
										for (LivingEntity e : FancyArena.instance.minions.get(entity)) {
											if (e.getType() == eType)
											{
												minionCount++;
											}
										}
										//Bukkit.broadcastMessage("minionCount = " + minionCount + " newItem.getAmount() = " + newItem.getAmount());
										if (minionCount >= newItem.getAmount()) {
											return;
										}
									}
								}
								if (snapshot != null) {
									minionType = snapshot.getEntityType().toString();
									minion = (LivingEntity)snapshot.createEntity(entity.getLocation());
								}
								if (minion == null) {
									minion = (LivingEntity)entity.getWorld().spawnEntity(entity.getLocation(), eType);
								}
								minion.teleport(entity.getLocation());
								minion.setPersistent(true);
								minion.setRemoveWhenFarAway(false);
								minion.addScoreboardTag("FancyMob");
								Hostiler.activate(minion);
								if (!FancyArena.instance.minions.containsKey(entity)) {
									FancyArena.instance.minions.put(entity, new ArrayList<LivingEntity>());
								}
								FancyArena.instance.minions.get(entity).add(minion);
								if (arenaName != null) {
									minion.addScoreboardTag("ArenaMob");
									Arena.arenaEntities.get(arenaName).add(minion);
								}
							}
							if (minionType != null)
								entity.addScoreboardTag("spawnedminion_" + minionType);
							return;
						}
						
					}
					
					SetHandItems(entity, newItem.clone(), template.getEquipment().getItemInOffHand());
				}
			}
		}
	}
	
	private static void SpawnEcho(LivingEntity entity) {
		// TODO Auto-generated method stub
		String arenaName = null;
		for (String tag : entity.getScoreboardTags()) {
			if (tag.contains("Arena_")) {
				arenaName = tag.substring(tag.indexOf("_")+1);
			}
		}
		EntityType type = entity.getType();
		//Entity clone = Spawn(entity.getEyeLocation(), entity.getCustomName() + " Echo");
		LivingEntity clone = (LivingEntity) entity.getWorld().spawnEntity(entity.getLocation(), type);
		clone.teleport(entity.getEyeLocation());
		clone.setVelocity(entity.getLocation().getDirection());
		clone.setCustomName(entity.getCustomName());
		clone.addScoreboardTag("Echo");
		clone.setPersistent(true);
		clone.setRemoveWhenFarAway(false);
		clone.addScoreboardTag("FancyMob");
		clone.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(2);
		clone.setHealth(1);
		clone.getEquipment().setArmorContents(entity.getEquipment().getArmorContents());
		clone.getEquipment().setItemInMainHand(entity.getEquipment().getItemInMainHand());
		clone.getEquipment().setItemInOffHand(entity.getEquipment().getItemInOffHand());
		clone.getEquipment().setHelmetDropChance(0);
		clone.getEquipment().setChestplateDropChance(0);
		clone.getEquipment().setLeggingsDropChance(0);
		clone.getEquipment().setBootsDropChance(0);
		clone.getEquipment().setItemInMainHandDropChance(0);
		clone.getEquipment().setItemInOffHandDropChance(0);
		java.util.Collection<PotionEffect> effects = new ArrayList<PotionEffect>();
		effects.add(new PotionEffect(PotionEffectType.INVISIBILITY, 20000, 0, false, false));
		effects.add(new PotionEffect(PotionEffectType.SLOW_FALLING, 20000, 10, false, false));
		effects.add(new PotionEffect(PotionEffectType.JUMP_BOOST, 20000, 2, false, false));
		effects.add(new PotionEffect(PotionEffectType.SPEED, 20000, 1, false, false));
		
		clone.addPotionEffects(effects);
		Hostiler.activate(clone);
		
		if (!FancyArena.instance.minions.containsKey(entity)) {
			FancyArena.instance.minions.put(entity, new ArrayList<LivingEntity>());
		}
		FancyArena.instance.minions.get(entity).add(clone);
		if (arenaName != null) {
			clone.addScoreboardTag("ArenaMob");
			Arena.arenaEntities.get(arenaName).add(clone);
		}
	}

	public static void SetHandItems(LivingEntity entity, ItemStack mainHand, ItemStack offHand) {
		if (entity == null)
			return;
		Boolean[] yeet = new Boolean[] {false, false, false};
		if (mainHand != null && mainHand.hasItemMeta()) {
			
			String itemName = mainHand.getItemMeta().getDisplayName();
			if (!FancyArena.itemBtn.containsKey(itemName)) {
				yeet[0] = FancyArena.instance.useItem(entity, mainHand, true, 0);
				yeet[1] = FancyArena.instance.useItem(entity, mainHand, true, 1);
				yeet[2] = FancyArena.instance.useItem(entity, mainHand, true, 2);
				FancyArena.itemBtn.put(itemName, yeet);
			} else {
				yeet = FancyArena.itemBtn.get(itemName);
			}
		}
		boolean useInvisBow = false;
		
		if ((entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WITHER_SKELETON || entity.getType() == EntityType.BOGGED || entity.getType() == EntityType.STRAY) && (yeet[0] || yeet[1] || yeet[2])) {
			
			ItemStack invisBow = new ItemStack(Material.BOW);
			ItemMeta meta = invisBow.getItemMeta();
			meta.setCustomModelData(42);
			meta.setDisplayName("Invisible bow");
			invisBow.setItemMeta(meta);
			
			// Obtain data
			//boolean leftHanded = NBT.get(entity, nbt -> (boolean) nbt.getBoolean("LeftHanded"));
			// Modify data
			NBT.modify(entity, nbt -> {
			    nbt.setBoolean("LeftHanded", true);
			});
			
			//Bukkit.dispatchCommand(entity, "data merge entity @e[type=skeleton,tag {LeftHanded:1b}");
			entity.getEquipment().setItemInMainHand(invisBow);
			entity.getEquipment().setItemInOffHand(mainHand);
			useInvisBow = true;
		} else {
			if ((yeet[0] || yeet[1] || yeet[2])) {
				// If the creature isn't something that can use a bow, it should just use the ability without switching to equip it
				if (yeet[0] && !(Math.random() < 0.5 && (yeet[1] || yeet[2])))
					FancyArena.instance.useItem(entity, mainHand, false, 0);
				else if (yeet[1] && !(Math.random() < 0.5 && (yeet[2])))
					FancyArena.instance.useItem(entity, mainHand, false, 1);
				else if (yeet[2])
					FancyArena.instance.useItem(entity, mainHand, false, 2);
			} else {
				NBT.modify(entity, nbt -> {
				    nbt.setBoolean("LeftHanded", false);
				});
				entity.getEquipment().setItemInMainHand(mainHand);
				entity.getEquipment().setItemInOffHand(offHand);
				
				if (entity instanceof Giant) {
					Zombie stand = FancyArena.giants.get(entity);
					stand.getEquipment().setChestplate(mainHand);
				}
			}
		}
		
		/*
		if (entity.getScoreboardTags().contains("Giant")) {
			if (FancyArena.giants.containsKey(entity) && FancyArena.giants.get(entity) != null && FancyArena.giants.get(entity).x != null) {
				Giant giant = FancyArena.giants.get(entity).x;
				giant.getEquipment().setItemInMainHand(mainHand);
				giant.getEquipment().setItemInOffHand(offHand);
				entity.getEquipment().setItemInOffHand(null);
				if (!useInvisBow) {
					entity.getEquipment().setItemInMainHand(null);
				}
			}
		}
		*/
	}
	
	public static LivingEntity Spawn(Location loc, String name) {
		try {
			//List<Entity> entities = loc.getWorld().getEntities();
			ArmorStand template = GetTemplate(name);
			if (template == null) {
				Arena.findFancyMobTemplates();
				template = GetTemplate(name);
			}
			if (template != null ){
				//Bukkit.broadcastMessage("Found template");
				Block block = template.getLocation().clone().add(0, -1, 0).getBlock();
				if (block.getType() != Material.CHEST) {
					Bukkit.broadcastMessage("Template for \"" + name + "\" is missing the chest!");
					return null;
				}
				Chest chest = (Chest) block.getState();
				Inventory inv = chest.getInventory();
	
				EntitySnapshot eSnap = null;
				EntityType type = EntityType.ZOMBIE;
				boolean foundSpawnEgg = false;
				boolean isGiant = false;
				int hp = -1;
				String customName = name;
				double speed = -1;
				int armor = -1;
				int damage = -1;
				double jumpHeight = -1;
				float dropChance = 0;
				String livingSound = null;
				String hurtSound = null;
				String deadSound = null;

				int typeScore = 0;
				for (ItemStack item : inv.getContents()) {
					if (item == null)
						continue;
					try {
						ItemMeta meta = item.getItemMeta();
						/* else if (item.getItemMeta() instanceof SpawnEggMeta) {
							SpawnEggMeta spawnMeta = (SpawnEggMeta) item.getItemMeta();
							eSnap = spawnMeta.getSpawnedEntity();
							type = spawnMeta.getSpawnedEntity().getEntityType();
							Bukkit.broadcastMessage("Found SpawnEggMeta");
						}*/ 
						if (item.getType().toString().contains("SPAWN_EGG") && !foundSpawnEgg) {
							foundSpawnEgg = true;
							String entityName = item.getType().toString();
							
							if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
								if (item.getItemMeta().getDisplayName().toLowerCase().contains("giant")) {
									isGiant = true;
								} else if (item.getItemMeta().getDisplayName().toLowerCase().contains("ender dragon") || item.getItemMeta().getDisplayName().toLowerCase().contains("enderdragon")) {
									type = EntityType.ENDER_DRAGON;
									entityName = "ENDERDRAGON_SPAWN_EGG";
								}
							}
							
							entityName = entityName.substring(0, entityName.indexOf("_SPAWN_EGG"));
							//Bukkit.broadcastMessage("Spawning " + entityName);
							if (isGiant)
								type = EntityType.GIANT;
							else
								type = EntityType.valueOf(entityName);
							//type = EntityType.fromName(entityName);
							//Bukkit.broadcastMessage("Found SPAWN_EGG");
						} if (item.getType() == Material.BEEF && meta.getDisplayName().contains("Health")) {
							if (meta.getDisplayName().contains("x10"))
								hp = item.getAmount() * 20;
							else
								hp = item.getAmount() * 2;
						} else if (item.getType() == Material.NAME_TAG) {
							customName = item.getItemMeta().getDisplayName();
						} else if (item.getType() == Material.PRISMARINE_CRYSTALS) {
							try {
							typeScore = Integer.parseInt(meta.getDisplayName());
							} catch (Exception e) {}
						}
						else if (item.getType() == Material.FEATHER && meta.getDisplayName().contains("Speed")) {
							speed = item.getAmount() * 0.23 / 10;
						} else if (item.getType() == Material.NETHERITE_SCRAP && meta.getDisplayName().contains("Armor")) {
							armor = item.getAmount();
						} else if (item.getType() == Material.PRISMARINE_SHARD && meta.getDisplayName().contains("Attack Damage")) {
							damage = item.getAmount();
						} else if (item.getType() == Material.RABBIT_HIDE && meta.getDisplayName().contains("Jump Height")) {
							jumpHeight = item.getAmount() * 0.42 / 2;
						} else if (item.getType() == Material.DROPPER && meta.getDisplayName().contains("Drop Chance")) {
							dropChance = item.getAmount() * 0.05F;
						} else if (item.getType() == Material.PLAYER_HEAD) {
							livingSound = meta.getDisplayName();
						} else if (item.getType() == Material.ZOMBIE_HEAD) {
							hurtSound = meta.getDisplayName();
						} else if (item.getType() == Material.SKELETON_SKULL) {
							deadSound = meta.getDisplayName();
						}
					} catch (Exception e) {
						
					}
				}
				String mobTag = getMobTag(customName);
				LivingEntity entity = null;
				if (eSnap == null) {
					entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
				} else {
					entity = (LivingEntity) eSnap.createEntity(loc);
				}
				
				if (isGiant) {
					Zombie giantHead = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
					giantHead.setGravity(false);
					giantHead.setAI(false);
					giantHead.setSilent(true);
					giantHead.setInvulnerable(true);
					giantHead.addScoreboardTag("invulnerable");
					giantHead.addScoreboardTag("Invulnerable");
					giantHead.getEquipment().setHelmet(template.getEquipment().getHelmet());
					FancyArena.giants.put((Giant) entity, giantHead);
				}
				/*
				Giant giant = null;
				if (isGiant) {
					giant = (Giant) loc.getWorld().spawnEntity(loc, EntityType.GIANT);
					giant.addScoreboardTag("FancyMob");
					giant.getEquipment().setArmorContents(template.getEquipment().getArmorContents());
					giant.getEquipment().setHelmet(template.getEquipment().getHelmet());
					entity.addScoreboardTag("Giant");
					if (mobTag != null && mobTag.length() > 0)
						giant.addScoreboardTag(mobTag);
					FancyArena.giants.put(entity, new Tuple<Giant, Entity>(giant, null));
					
					giant.getEquipment().setHelmetDropChance(dropChance);
					giant.getEquipment().setChestplateDropChance(dropChance);
					giant.getEquipment().setLeggingsDropChance(dropChance);
					giant.getEquipment().setBootsDropChance(dropChance);
					giant.getEquipment().setItemInMainHandDropChance(dropChance);
					giant.getEquipment().setItemInOffHandDropChance(dropChance);
					
					giant.addPotionEffects(template.getActivePotionEffects());
					giant.setCustomName(customName);
					giant.setCustomNameVisible(true);
					
					giant.setPersistent(true);
					giant.setRemoveWhenFarAway(false);
					
					//entity.setInvulnerable(true);
					entity.setInvisible(true);
					entity.setCollidable(false);
					entity.getEquipment().setHelmet(new ItemStack(Material.BARRIER));
				}
				else*/
					entity.getEquipment().setArmorContents(template.getEquipment().getArmorContents());
				
				SetHandItems(entity, template.getEquipment().getItemInMainHand(), template.getEquipment().getItemInOffHand());
				
				/*
				if (entity.getType() == EntityType.SKELETON && FancyArena.instance.useItem(entity, template.getEquipment().getItemInMainHand(), true)) {
					ItemStack invisBow = new ItemStack(Material.BOW);
					ItemMeta meta = invisBow.getItemMeta();
					meta.setCustomModelData(42);
					invisBow.setItemMeta(meta);
					Bukkit.dispatchCommand(entity, "data merge entity @s {LeftHanded:1b}");
					entity.getEquipment().setItemInMainHand(invisBow);
					entity.getEquipment().setItemInOffHand(template.getEquipment().getItemInMainHand());
				} else {
					entity.getEquipment().setItemInMainHand(template.getEquipment().getItemInMainHand());
					entity.getEquipment().setItemInOffHand(template.getEquipment().getItemInOffHand());
				}*/
				try {
					if (typeScore != 0) {

						// Get the server scoreboard
					    Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

					    // Make sure the objective exists
					    Objective objective = scoreboard.getObjective("type");
					    if (objective == null) {
					        objective = scoreboard.registerNewObjective("type", "dummy", "type");
					    }
					   
					    // Set the score to 5
					    Score score = objective.getScore(entity.getCustomName().toString());
					    score.setScore(typeScore);
					}
					if (hp != -1) {
						/*
						if (isGiant) {
							hp = hp*7;
							giant.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
							giant.setHealth(hp);
						}
						*/
						entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(hp);
						//entity.setMaxHealth(hp);
						entity.setHealth(hp);
					}
					if (Math.abs(speed + 1) < 0.0001) {
						entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
						/*
						if (isGiant) {
							giant.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
						}
						*/
					}
					if (armor != -1) {
						/*
						if (isGiant) {
							giant.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
						}
						*/
						entity.getAttribute(Attribute.GENERIC_ARMOR).setBaseValue(armor);
					}
					if (damage != -1) {
						/*
						if (isGiant) {
							damage = (int)(damage * 2);
							giant.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
						}
						*/
						entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
					}
					if (jumpHeight != -1) {
						/*
						if (isGiant) {
							giant.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpHeight);
						}
						*/
						entity.getAttribute(Attribute.GENERIC_JUMP_STRENGTH).setBaseValue(jumpHeight);
					}
					
					entity.addScoreboardTag("FancyMob");
					if (mobTag != null && mobTag.length() > 0)
						entity.addScoreboardTag(mobTag);
					
					entity.getEquipment().setHelmetDropChance(dropChance);
					entity.getEquipment().setChestplateDropChance(dropChance);
					entity.getEquipment().setLeggingsDropChance(dropChance);
					entity.getEquipment().setBootsDropChance(dropChance);
					entity.getEquipment().setItemInMainHandDropChance(dropChance);
					entity.getEquipment().setItemInOffHandDropChance(dropChance);
				} catch (Exception e) {
					
				}
				
				entity.addPotionEffects(template.getActivePotionEffects());
				entity.setCustomName(customName);
				entity.setCustomNameVisible(true);
				
				entity.setPersistent(true);
				entity.setRemoveWhenFarAway(false);
				
				//if (entity.getType() == EntityType.IRON_GOLEM)
					Hostiler.activate(entity);
				
				if (livingSound != null) {
					entity.setMetadata("livingSound", new FixedMetadataValue(FancyArena.instance, livingSound));
					entity.setSilent(true);
					entity.addScoreboardTag("CustomLivingSound");
					FancyArena.customLivingSounds.add(entity);
				}
				if (hurtSound != null) {
					entity.setMetadata("hurtSound", new FixedMetadataValue(FancyArena.instance, hurtSound));
					entity.setSilent(true);
					entity.addScoreboardTag("CustomHurtSound");
				}
				if (deadSound != null) {
					entity.setMetadata("deadSound", new FixedMetadataValue(FancyArena.instance, deadSound));
					entity.setSilent(true);
					entity.addScoreboardTag("CustomDeathSound");
				}
				
				SwapWeapon(entity);
				
				if (entity.getType() == EntityType.PIGLIN) {
					Piglin piglin = (Piglin) entity;
					piglin.setImmuneToZombification(true);
				} else if (entity.getType() == EntityType.PIGLIN_BRUTE) {
					PiglinBrute piglin = (PiglinBrute) entity;
					piglin.setImmuneToZombification(true);
				}
				
				return entity;
				
			} else {
				Bukkit.broadcastMessage("No template found for " + name);
			}
		} catch (Exception e) {
			Bukkit.broadcastMessage("Failed to spawn " + name);
		}
		return null;
	}
	
	private static String getMobTag(String name) {
		if (name.contains("Statue") || name.contains("Aeormaton")) {
			return "Construct";
		} else if (name.contains("Simulacrum") || name.contains("Demon") || name.contains("Vampire") || name.contains("Undead")) {
			return "Unholy";
		} else if (name.contains("Chef") || name.contains("Crownsguard") || name.contains("Mage") || name.contains("Royal Knight") || name.contains("Chef")) {
			return "Hired";
		}
		return null;
	}
}
