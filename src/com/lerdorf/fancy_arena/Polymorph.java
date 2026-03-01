package com.lerdorf.fancy_arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;

public class Polymorph {
	public int maxHP;
	
	public ItemStack[] storedInv;
	public Player player;
	
	public static final int DIRE_WOLF = 0;
	public static final int OWL = 1;
	public static final int QUETZAL = 2;
	public static final int BADGER = 3;
	public static final int TREX = 4;
	public static final int DEINONYCHUS = 5;
	
	public static final int NUM_MORPHS = 6;
	
	public boolean ended = false;
	
	public int id;
	
	boolean canFly = false;

	int frame = 0;
	private float hpMult = 1;
	
	public Polymorph(Player player, int id, float hpMult) {

		this.id = id;
		this.player = player;
		this.hpMult = hpMult;
		
		startPolymorph();
	}
	
	public static int getID(String name) {
		for (int i = 0; i < NUM_MORPHS; i++)
			if (name.equalsIgnoreCase(getName(i)) || name.contains(getName(i)))
				return i;
		
		return -1;
	}
	
	public void startPolymorph() {
		storedInv = player.getInventory().getContents();
		player.getInventory().clear();
		player.addScoreboardTag("Polymorph");
		
		addPotionEffects();
		
		if (getMaxHP() % 2 == 1)
			player.damage(2);
		
		if (id == OWL || id == QUETZAL)
			canFly = true;
		else if (id == BADGER)
			player.addScoreboardTag("NoSuffocation");

		player.getEquipment().setHelmet(getHead());
		ItemStack[] items = getItems();
		for (ItemStack i : items)
			player.getInventory().addItem(i);
		playSpawnSound();
	}
	
	public void endPolymorph() {
		player.removeScoreboardTag("Polymorph");
		player.getInventory().clear();
		if (player.getScoreboardTags().contains("NoSuffocation")) {
			player.removeScoreboardTag("NoSuffocation");
		}
		if (FancyArena.instance.grapple.size() > 0) {
			if (FancyArena.instance.grapple.containsKey(player)) {
				FancyArena.instance.grapple.get(player).removeScoreboardTag("Grapple");
				FancyArena.instance.grapple.remove(player);
			}
		}
		player.getInventory().setContents(storedInv);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.removePotionEffect(PotionEffectType.ABSORPTION);
		if (id == OWL) {
			player.removePotionEffect(PotionEffectType.SLOW_FALLING);
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
		}
		if (Arena.playerEffects.containsKey(player))
			player.addPotionEffects(Arena.playerEffects.get(player));
		for (Player p : Bukkit.getOnlinePlayers())
			p.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1);
		player.getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, player.getLocation().add(new Vector(0, 1, 0)), 50, 0.01, 2, 0.01, 0.1);
		ended = true;
		player.setCooldown(Material.POPPED_CHORUS_FRUIT, 60*20);
	}
	
	public boolean attack = false;
	public boolean attack2 = false;
	
	int c = 0;
	public void tick() {
		if (c % 2 == 0) {
			if (attack) {
				if (!startedAttackAnim) {
					frame = 0;
				} else if (frame == 0) {
					attack = false;
				}
				if (attack) {
					ItemStack head = player.getEquipment().getHelmet();
					ItemMeta meta = head.getItemMeta();
					int md = attackMD();
					if (md == -1) {
						attack = false;
					} else {
						meta.setCustomModelData(md);
						head.setItemMeta(meta);
						//player.sendMessage("Attack " + frame);
						frame++;
					}
				}
			} else if (attack2) {
				if (!startedAttack2Anim) {
					frame = 0;
				} else if (frame == 0) {
					attack2 = false;
				}
				if (attack2) {
					ItemStack head = player.getEquipment().getHelmet();
					ItemMeta meta = head.getItemMeta();
					int md = attack2MD();
					if (md == -1) {
						attack = false;
					} else {
						meta.setCustomModelData(md);
						head.setItemMeta(meta);
						//player.sendMessage("Attack2 " + frame);
						frame++;
					}
				}
			}
			else if (player.getVelocity().length() > 0.1f || player.getScoreboardTags().contains("Moving")) {
				//player.sendMessage("Moving");
				if (canFly && !player.isOnGround()) {
					//player.sendMessage("Flying " + frame);
					ItemStack head = player.getEquipment().getHelmet();
					ItemMeta meta = head.getItemMeta();
					meta.setCustomModelData(flyMD());
					head.setItemMeta(meta);
					frame++;
				} else {
					ItemStack head = player.getEquipment().getHelmet();
					ItemMeta meta = head.getItemMeta();
					meta.setCustomModelData(walkMD());
					//player.sendMessage("Walking " + frame);
					head.setItemMeta(meta);
					frame++;
				}
			}

			if (c % 4 == 0) {
				
				if (!player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
					endPolymorph();
				}
				if (Math.random() < 0.05)
					playSound();
				else if (Math.random() < 0.02)
					playSpawnSound();
			}
		}
		c++;
		if (player.getScoreboardTags().contains("Moving")) {
    		player.removeScoreboardTag("Moving");
    	}
	}
	
	public void addPotionEffects() {
		
		if (hpMult > 1.4f && hpMult < 2.5f) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*60, 1, true, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20*60, 0, true, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*60, 0, true, false));
		} else if (hpMult > 2.5f) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*60, 3, true, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20*60, 2, true, false));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*60, 2, true, false));
		}
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60*20, 0, true, false));
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60*20, getMaxHP()/2, true, false));
		
		switch (id) {
			case DIRE_WOLF:
				break;
			case OWL:
				player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 60*20, 0, true, false));
				player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 60*20, 0, true, false));
				break;
			case QUETZAL:
				break;
			case BADGER:
				break;
			case TREX:
				break;
			case DEINONYCHUS:
				break;
		}
	}
	
	private void playSpawnSound() {
		switch (id) {
			case DIRE_WOLF:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:entity.wolf.howl player @a ~ ~ ~ 1 1");
				break;
			case OWL:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:owl player @a ~ ~ ~ 1 1");
				break;
			case QUETZAL:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:entity.phantom.ambient player @a ~ ~ ~ 1 1");
				break;
			case BADGER:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:badger_song player @a ~ ~ ~ 1 1");
				break;
			case TREX:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:trex_roar player @a ~ ~ ~ 1 1");
				break;
			case DEINONYCHUS:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound trex_roar player @a ~ ~ ~ 1 1.7");
				break;
		}
	}
		
	private void playSound() {
		switch (id) {
			case DIRE_WOLF:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:entity.wolf.howl player @a ~ ~ ~ 1 1");
				break;
			case OWL:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:owl player @a ~ ~ ~ 1 1");
				break;
			case QUETZAL:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:entity.phantom.ambient player @a ~ ~ ~ 1 1");
				break;
			case BADGER:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:badger player @a ~ ~ ~ 1 1");
				break;
			case TREX:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound minecraft:trex_roar player @a ~ ~ ~ 1 1");
				break;
			case DEINONYCHUS:
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute at " +  player.getName() + " run playsound trex_roar player @a ~ ~ ~ 1 1.7");
				break;
		}
	}
	
	public static ItemStack getHead(int id) {
		return ModItem.makeItem(getName(id), walkMD(id, 0), Material.HONEYCOMB, getAttackSpeed(id), getSpeed(id), getArmor(id), getJumpHeight(id), new EquipmentSlot[] {EquipmentSlot.HEAD});
	}
	
	private ItemStack getHead() {
		return getHead(id);
	}
	
	public String getName() {
		return getName(id);
	}
	
	public static String getName(int id) {
		switch (id) {
			case DIRE_WOLF:
				return "Dire Wolf";
			case OWL:
				return "Owl";
			case QUETZAL:
				return "Quetzalcoatlus";
			case BADGER:
				return "Giant Badger";
			case TREX:
				return "T-Rex";
			case DEINONYCHUS:
				return "Deinonychus";
		}
		return "Polymorph";
	}
	
	private int getMaxHP() {
		return (int) Math.ceil(getMaxHP(id) * hpMult);
	}
	
	public static int getMaxHP(int id) {
		switch (id) {
			case DIRE_WOLF:
				return 9;
			case OWL:
				return 1;
			case QUETZAL:
				return 7;
			case BADGER:
				return 3;
			case TREX:
				return 34;
			case DEINONYCHUS:
				return 7;
		}
		return -1;
	}
	
	private int getArmor() {
		return getArmor(id);
	}
	
	public static int getArmor(int id) {
		switch (id) {
			case DIRE_WOLF:
				return 14;
			case OWL:
				return 12;
			case QUETZAL:
				return 13;
			case BADGER:
				return 10;
			case TREX:
				return 13;
			case DEINONYCHUS:
				return 13;
		}
		return -1;
	}
	public static double getAttackSpeed(int id) {
		switch (id) {
			case DIRE_WOLF:
				return -2.5;
			case OWL:
				return 0;
			case QUETZAL:
				return -1.7;
			case BADGER:
				return -1.3;
			case TREX:
				return -2.6;
			case DEINONYCHUS:
				return -1.4;
		}
		return 0;
	}
	
	public static double getSpeed(int id) {
		switch (id) {
			case DIRE_WOLF:
				return 0.1;
			case OWL:
				return 0;
			case QUETZAL:
				return 0.05;
			case BADGER:
				return 0.02;
			case TREX:
				return 0.11;
			case DEINONYCHUS:
				return 0.2;
		}
		return 0;
	}
	
	public static double getJumpHeight(int id) {
		switch (id) {
			case DIRE_WOLF:
				return 0.1;
			case OWL:
				return 0.2;
			case QUETZAL:
				return 0.2;
			case BADGER:
				return 0;
			case TREX:
				return 0;
			case DEINONYCHUS:
				return 0.2;
		}
		return 0;
	}
	
	public ItemStack[] getItems() {
		switch (id) {
			case DIRE_WOLF:
			{
				ItemStack item = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item.getItemMeta();
				meta.setCustomModelData(13);
				meta.setDisplayName(ChatColor.GREEN + "Dire Wolf Bite");
				ModItem.setAttribute(meta, 12, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
				ModItem.setAttribute(meta, -0.12,  Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
				//List<String> lore = new ArrayList<String>();
				//lore.add(ChatColor.YELLOW + "Right click to burrow");
				//meta.setLore(lore);
				item.setItemMeta(meta);
				return new ItemStack[] { item };
			}
			case OWL:
			{
				ItemStack item = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item.getItemMeta();
				meta.setCustomModelData(7);
				meta.setDisplayName(ChatColor.GREEN + "Owl Fly");
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to fly");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return new ItemStack[] { item };
			}
			case QUETZAL:
			{
				ItemStack item1 = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item1.getItemMeta();
				meta.setCustomModelData(8);
				meta.setDisplayName(ChatColor.GREEN + "Quetzal Fly");
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to fly");
				meta.setLore(lore);
				item1.setItemMeta(meta);
				ItemStack item2 = new ItemStack(Material.RAW_COPPER);
				meta = item2.getItemMeta();
				meta.setCustomModelData(9);
				meta.setDisplayName(ChatColor.GREEN + "Quetzal Bite");
				ModItem.setAttribute(meta, 12, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
				ModItem.setAttribute(meta, -0.16,  Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
				lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to grapple");
				meta.setLore(lore);
				item2.setItemMeta(meta);
				return new ItemStack[] { item1, item2 };
			}
			case BADGER:
			{
				ItemStack item = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item.getItemMeta();
				meta.setCustomModelData(6);
				meta.setDisplayName(ChatColor.GREEN + "Badger Claw");
				ModItem.setAttribute(meta, 5, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
				ModItem.setAttribute(meta, -0.09,  Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to burrow");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return new ItemStack[] { item };
			}
			case TREX:
			{
				ItemStack item1 = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item1.getItemMeta();
				meta.setCustomModelData(11);
				meta.setDisplayName(ChatColor.GREEN + "T-Rex Tail");
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to tail whip");
				meta.setLore(lore);
				item1.setItemMeta(meta);
				ItemStack item2 = new ItemStack(Material.RAW_COPPER);
				meta = item2.getItemMeta();
				meta.setCustomModelData(12);
				meta.setDisplayName(ChatColor.GREEN + "T-Rex Bite");
				ModItem.setAttribute(meta, 19, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
				ModItem.setAttribute(meta, -0.18,  Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
				lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to grapple");
				meta.setLore(lore);
				item2.setItemMeta(meta);
				return new ItemStack[] { item1, item2 };
			}
			case DEINONYCHUS:
			{
				ItemStack item = new ItemStack(Material.RAW_COPPER);
				ItemMeta meta = item.getItemMeta();
				meta.setCustomModelData(10);
				meta.setDisplayName(ChatColor.GREEN + "Deinonychus Bite");
				ModItem.setAttribute(meta, 9, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
				ModItem.setAttribute(meta, -0.1,  Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
				List<String> lore = new ArrayList<String>();
				lore.add(ChatColor.YELLOW + "Right click to pounce");
				meta.setLore(lore);
				item.setItemMeta(meta);
				return new ItemStack[] { item };
			}
		}
		return null;
	}
	
	public int walkMD() {
		return walkMD(id, frame);
	}
	
	public static int walkMD(int id, int frame) {
		switch (id) {
			case DIRE_WOLF:
				frame %= 7;
				return 51 + frame;
			case OWL:
				frame %= 3;
				return 27 + frame;
			case QUETZAL:
				frame %= 8;
				return 30 + frame;
			case BADGER:
				frame %= 4;
				return 47 + frame;
			case TREX:
				frame %= 9;
				return 7 + frame;
			case DEINONYCHUS:
				frame %= 7;
				return 20 + frame;
		}
		return -1;
	}
	
	public int flyMD() {
		switch (id) {
			case OWL:
				frame %= 3;
				return 27 + frame;
			case QUETZAL:
				frame %= 9;
				return 38 + frame;
		}
		return -1;
	}
	
	boolean startedAttackAnim = false;
	
	public int attackMD() {
		startedAttackAnim = true;
		switch (id) {
			case TREX:
				if (frame >= 6) {
					frame = -1;
					return -1;
				}
				return 1 + frame;
				
		}
		return -1;
	}
	
	boolean startedAttack2Anim = false;
	
	public int attack2MD() {
		startedAttack2Anim = true;
		switch (id) {
			case TREX:
				if (frame >= 4) {
					frame = -1;
					return -1;
				}
				return 16 + frame;
				
		}
		return -1;
	}
}
