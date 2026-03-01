package com.lerdorf.fancy_arena;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.sound.midi.InvalidMidiDataException;

import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dropper;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Candle;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftCat;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftFox;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftIronGolem;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftSalmon;
import org.bukkit.damage.DamageSource;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntitySalmon;
import net.minecraft.world.entity.player.EntityHuman;

import com.google.common.collect.Lists;
//import com.google.gson.Gson;

//import com.lerdorf.fancy_plugin;

public class FancyArena extends JavaPlugin implements Listener {
	
	public static boolean enableItems = true;
	
	public static FancyArena instance = null;
	
	public HashMap<AbstractArrow, Particle> arrowTrail = new HashMap<>();
	
	public HashMap<LivingEntity, ArrayList<LivingEntity>> minions = new HashMap<LivingEntity, ArrayList<LivingEntity>>();
	public static ArrayList<Polymorph> polymorphs = new ArrayList<Polymorph>();
	
	public HashMap<Player, PlayerState[]> playerStates = new HashMap<>();
	
	public HashMap<LivingEntity, LivingEntity> grapple = new HashMap<LivingEntity, LivingEntity>();
	
	public static HashMap<LivingEntity, Tuple<Location, Integer>> rooted = new HashMap<>();
	
	public HashMap<LivingEntity, Integer> dalekShield = new HashMap<>();
	public HashMap<LivingEntity, Integer> dalekThruster = new HashMap<>();
	public ArrayList<FallingBlock> meteors = new ArrayList<>();
	public HashMap<LivingEntity, Integer> arrowBarrage = new HashMap<LivingEntity, Integer>();
	public HashMap<LivingEntity, Integer> royalArrowBarrage = new HashMap<LivingEntity, Integer>();
	public HashMap<LivingEntity, Integer> supremeArrowBarrage = new HashMap<LivingEntity, Integer>();
	//public static HashMap<ShulkerBullet, Double> magicMissiles = new HashMap<ShulkerBullet, Double>();
	public HashMap<LivingEntity, Integer> firebreath = new HashMap<LivingEntity, Integer>();
	public HashMap<LivingEntity, Integer> dragonbreath = new HashMap<LivingEntity, Integer>();
	public HashMap<LivingEntity, Tuple<Integer, ItemStack>> rightClicks = new HashMap<LivingEntity, Tuple<Integer, ItemStack>>();
	public static HashMap<LivingEntity, Location> delayedTeleport = new HashMap<LivingEntity, Location>();
	public HashMap<Player, Inventory> delayedInventory = new HashMap<Player, Inventory>();
	public HashMap<LivingEntity, Integer> swarmingInsects = new HashMap<LivingEntity, Integer>();
	public static ArrayList<LivingEntity> customLivingSounds = new ArrayList<LivingEntity>();
	public static HashMap<LivingEntity, Skeleton> illusionDoubles = new HashMap<LivingEntity, Skeleton>();
	public static HashMap<String, Boolean[]> itemBtn = new HashMap<String, Boolean[]>();
	public static HashMap<LivingEntity, Tuple<Integer, Integer>> hammerSpin = new HashMap<LivingEntity, Tuple<Integer, Integer>>();
	public HashMap<LivingEntity, Integer> hammerBoost = new HashMap<LivingEntity, Integer>();
	public HashMap<LivingEntity, ArmorStand> sentryMode = new HashMap<LivingEntity, ArmorStand>();
	public HashMap<LivingEntity, ArmorStand> durrithWings = new HashMap<LivingEntity, ArmorStand>();
	public HashMap<LivingEntity, ArrayList<LivingEntity>> sentryTarget = new HashMap<LivingEntity, ArrayList<LivingEntity>>();
	public static HashMap<Tuple<Location, Integer>, Integer> acheronPortal = new HashMap<>();
	public static ArrayList<Inventory> bags = new ArrayList<>();
	public static HashMap<LivingEntity, Integer> freedomOfMovement = new HashMap<>();
	public static HashMap<LivingEntity, Integer> antilifeShell = new HashMap<>();
	//public static HashMap<Player, Integer> bagUpgrades = new HashMap<>();
	//public static HashMap<LivingEntity, Tuple<Giant, Entity>> giants = new HashMap<LivingEntity, Tuple<Giant, Entity>>();
	public static HashMap<LivingEntity, Zombie> giants = new HashMap<LivingEntity, Zombie>();
	
	public static HashMap<Player, MerchantRecipe> selectedRecipe = new HashMap<>();
	
	public HashMap<Player, Location> storedLoc = new HashMap<Player, Location>();
	
	public ArrayList<LivingEntity> repulsorFly = new ArrayList<LivingEntity>();
	public ArrayList<LivingEntity> repulsorFlying = new ArrayList<LivingEntity>();
	
	public HashMap<Fireball, Tuple<LivingEntity, Vector>> homing = new HashMap<Fireball, Tuple<LivingEntity, Vector>>();
	
	//public static Location gulagLoc;
	
	public HashMap<ArmorStand, Vector> thrownItems = new HashMap<ArmorStand, Vector>();
	public HashMap<ArmorStand, List<String>> thrownItemCommands = new HashMap<ArmorStand, List<String>>();
	
	public static HashMap<Location, Tuple<Integer, LivingEntity>> wallOfFire = new HashMap<Location, Tuple<Integer, LivingEntity>>();
	public HashMap<LivingEntity, Integer> wallOfFireSpawner = new HashMap<LivingEntity, Integer>();
	
	public static HashMap<LivingEntity, Location> gulag = new HashMap<LivingEntity, Location>();
	
	public static HashMap<Tuple<BoundingBox, World>, Integer> antimagic = new HashMap<Tuple<BoundingBox, World>, Integer>();
	public static HashMap<Triple<BoundingBox, World, LivingEntity>, Integer> hypnotic = new HashMap<Triple<BoundingBox, World, LivingEntity>, Integer>();
	
	public static HashMap<Tuple<LivingEntity, Integer>, Integer> sunbeam = new HashMap<>();
	public static HashMap<LivingEntity, Integer> primordialWard = new HashMap<>();
	public static HashMap<LivingEntity, Integer> elementalWard = new HashMap<>();
	
	public static HashMap<LivingEntity, LivingEntity> riding = new HashMap<>();
	
	public static int maxLives = 3;
	public static boolean arenaRunning = false;
	
	public static boolean pvp = false;
	public static boolean monsterPvp = false;
	
	// Maps to store deletion GUI data per player.
    public static HashMap<Player, Villager> deletionVillagerMap = new HashMap<>();
    public static HashMap<Player, Integer> deletionPageMap = new HashMap<>();

    // How many trades to show per page (one per row)
    public static final int TRADES_PER_PAGE = 5;
	
	/*
	public static MIDIPlayer midiPlayer;
	public static List<MIDIFile> midiFiles = new ArrayList<>();
	public static MIDIFile hypnoticMidi;
	*/
	
	long c = 0;
	private void pointArmorStandAtEntity(ArmorStand armorStand, Entity target) {
		
		/*
        // Get the direction vector from the armor stand to the target
        Location armorStandLocation = armorStand.getEyeLocation();
        Location targetLocation = target.getLocation().add(new Vector(0, 1, 0));

        Vector currentDirection = armorStandLocation.getDirection();
        Vector direction = targetLocation.toVector().subtract(armorStandLocation.toVector()).normalize();
        Vector diffDirection = direction.clone().subtract(currentDirection);

        // Calculate yaw and pitch from the difference in direction vectors
        float yaw = (float)Math.toDegrees(Math.atan2(-diffDirection.getX(), diffDirection.getZ()));
        float pitch = (float)Math.toDegrees(Math.asin(-direction.getY()));

        // Set the armor stand's head pose to point towards the target
        armorStand.setHeadPose(new EulerAngle(Math.toRadians(pitch), 0, 0));
        armorStand.setRotation(armorStand.getLocation().getYaw() + yaw, 0);
        */
		
		// Get the direction vector from the armor stand to the target
	    Location armorStandLocation = armorStand.getEyeLocation();
	    Location targetLocation = target.getLocation().add(0, target.getHeight() / 2, 0); // Adjust for entity height

	    // Calculate direction vector
	    Vector direction = targetLocation.toVector().subtract(armorStandLocation.toVector()).normalize();

	    Location yeet = armorStandLocation.clone();
	    yeet.setDirection(direction);
	    
	    // Calculate yaw and pitch
	    //float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())); // Minecraft yaw calculation
	    //float pitch = (float) Math.toDegrees(Math.asin(-direction.getY())); // Correct pitch calculation

	    float yaw = yeet.getYaw();
	    float pitch = yeet.getPitch();
	    
	    // Update the armor stand's head pose (head tilt up/down)
	    armorStand.setHeadPose(new EulerAngle(Math.toRadians(pitch), 0, 0));

	    // Update the armor stand's body rotation (yaw)
	    armorStand.setRotation(yaw, 0);
    }

	/*
	MIDIInstrument saxophone = new MIDIInstrument(Instrument.DIDGERIDOO, 1);
	
	public void initMidiPlayer() {
		
		MIDIPlayer.init(this);
		midiPlayer = new MIDIPlayer();
		MIDIBlasterAPICore.SetDebugMode(false);
		
		// Creates a new File instance by converting the given pathname string
        // into an abstract pathname
        File f = new File("plugins/FancyArena/midi");

        // Populates the array with names of files and directories
        String[] pathnames = f.list();

        // For each pathname in the pathnames array
        for (String pathname : pathnames) {
            try {
            	MIDIFile newFile = MIDIDecoder.Decode("plugins/FancyArena/midi/" + pathname);
            	for (MIDINote note : newFile.GetNotes()) {
            		note.SetMillisecondsTick(note.GetMillisecondsTick()/2);
            		//if (note.GetChannel() == 0) {
            			note.SetInstrument(saxophone);
            		//}
            	}
            	midiFiles.add(newFile);
				//Bukkit.broadcastMessage("Loaded " + pathname);
				if (pathname.contains("Hypnotic")) {
					hypnoticMidi = newFile;
					//Bukkit.broadcastMessage("Found Hypnotic!");
				}
			} catch (InvalidMidiDataException e) {
				Bukkit.broadcastMessage(ChatColor.RED + "InvalidMidiDataException Failed to load " + pathname + e.getLocalizedMessage());
				//e.printStackTrace();
			} catch (IOException e) {
				Bukkit.broadcastMessage(ChatColor.RED + "IOException Failed to load " + pathname + e.getLocalizedMessage());
				//e.printStackTrace();
			}
        }
	}
	*/
	
	public static void prepare_teleport(LivingEntity le) {
		if (le.getPassengers().size() > 0)
			le.removePassenger(le.getPassengers().get(0));
		if (le.getScoreboardTags().contains("Grapple")) {
			le.removeScoreboardTag("Grapple");
		}
		if (rooted.containsKey(le))
			rooted.remove(le);
		if (hammerSpin.containsKey(le))
			hammerSpin.remove(le);
	}
	
	public static void strong_teleport(LivingEntity le, Entity dest) {
		strong_teleport(le, dest.getLocation());
	}
	
	public static void strong_teleport(LivingEntity le, Location loc) {
		prepare_teleport(le);
		
		le.teleport(loc);
	}
	
	public static void super_strong_teleport(LivingEntity le, Entity dest) {
		super_strong_teleport(le, dest.getLocation());
	}
	
	public static void super_strong_teleport(LivingEntity le, Location loc) {
		prepare_teleport(le);
		
		le.teleport(loc);
		delayedTeleport.put(le, loc);
	}
	
	public static void Fill(Location l1, Location l2, Material mat) {
		int minX = Math.min(l1.getBlockX(), l2.getBlockX());
		int minY = Math.min(l1.getBlockY(), l2.getBlockY());
		int minZ = Math.min(l1.getBlockZ(), l2.getBlockZ());
		int maxX = Math.max(l1.getBlockX(), l2.getBlockX());
		int maxY = Math.max(l1.getBlockY(), l2.getBlockY());
		int maxZ = Math.max(l1.getBlockZ(), l2.getBlockZ());
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				for (int z = minZ; z < maxZ; z++) {
					l1.getWorld().getBlockAt(x, y, z).setType(mat);
				}
			}
		}
	}
	
	@Override
	public void onEnable() {
		System.out.println("Starting FancyArena");
		
		//gulagLoc = new Location(Bukkit.getWorld("Aeons_Lobby"), 6997, 66, 6848, -180, 0);
		
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		instance = this;
		
		// Register our command "kit" (set an instance of your command class as executor)
		this.getCommand("moditem").setExecutor(new ModItem());
		this.getCommand("fancymob").setExecutor(new FancyMob());
		this.getCommand("fancyitem").setExecutor(new FancyItem());
		Arena arena = new Arena();
		this.getCommand("fancyarena").setExecutor(arena);
		this.getCommand("fa").setExecutor(arena);
		this.getCommand("shop").setExecutor(new Shop());
		this.getCommand("cbscan").setExecutor(new CBScan());
		this.getCommand("rm").setExecutor(new Remove());
		//loadSaves();

		Arena.findFancyMobTemplates();
		Arena.Init();
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			  @Override
			  public void run() {
				  try {
					  if (Bukkit.getOnlinePlayers().size() == 0) return;
					  c++;
					  
					  /*
					  if (c == 1) {
							initMidiPlayer();
					  }
					  */
					  
					  /*
					  if (sentryMode.size() > 0) {
						  for (LivingEntity le : sentryMode.keySet()) {
							  Vector dir = le.getLocation().getDirection();
							  Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
							  Vector diff = new Vector(le.getLocation().getDirection().getX(), 0, le.getLocation().getDirection().getZ()).normalize().multiply(-0.4).add(right.multiply(0.4).add(new Vector(0, 0.7, 0)));
							  sentryMode.get(le).teleport(le.getLocation().add(diff).add(le.getVelocity()).setDirection(sentryMode.get(le).getLocation().getDirection()));
						  }
					  }
					  */
					  	if (polymorphs.size() > 0) {
					  		for (int i = 0; i < polymorphs.size(); i++) {
					  			while (polymorphs.size() > i && polymorphs.get(i).ended) 
					  				polymorphs.remove(i);
					  			if (i < polymorphs.size()) {
					  				polymorphs.get(i).tick();
					  			}
					  		}
					  	}
					  	if (antilifeShell.size() > 0) {
					  		ArrayList<LivingEntity> remove = new ArrayList<>();
					  		for (LivingEntity le : antilifeShell.keySet()) {
					  			if (le == null || !le.isValid() || antilifeShell.get(le) <= 0) {
					  				if (le != null && le.isValid())
					  					le.removeScoreboardTag("antilife");
					  				remove.add(le);
					  			} else {
					  				antilifeShell.put(le, antilifeShell.get(le)-1);
					  				for (float i = 0; i < 2*Math.PI; i += 0.05) {
										le.getWorld().spawnParticle(Particle.ASH, le.getLocation().add(new Vector(5*Math.sin(i), 1.8f, 5*Math.cos(i))), 1, 0, 0, 0, 0);
									}
					  				for (Entity e : le.getNearbyEntities(4, 4, 4)) {
					  					if (e != null && e.isValid() && Arena.validTarget(e) && e instanceof LivingEntity le2 && (le instanceof Player) != (le2 instanceof Player)) {
					  						Vector diff = le2.getLocation().toVector().subtract(le.getLocation().toVector());
					  						diff = new Vector(diff.getX(), 0, diff.getZ()).normalize();
					  						le2.setVelocity(diff);
					  						//strong_teleport(le2, le2.getLocation().add(diff));
					  					}
					  				}
					  			}
					  		}
					  	}
					  	if (wallRunning.size() > 0) {
					  		for (Player p : wallRunning) {
					  			if (p.isSprinting() && isNextToWall(p, true)) {
						  			p.setVelocity(p.getLocation().getDirection().multiply(0.5).add(new Vector(0, 0.1, 0)));
					                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_SLIME_BLOCK_STEP, 1f, 1.2f);
					  			}
					  		}
					  	}
					  	if (freedomOfMovement.size() > 0) {
					  		ArrayList<LivingEntity> remove = new ArrayList<>();
					  		for (LivingEntity le : freedomOfMovement.keySet()) {
					  			if (le == null || !le.isValid() || freedomOfMovement.get(le) <= 0) {
					  				if (le != null && le.isValid())
					  					le.removeScoreboardTag("fom");
					  				remove.add(le);
					  			} else {
					  				freedomOfMovement.put(le, freedomOfMovement.get(le)-1);
					  				if (le.hasPotionEffect(PotionEffectType.SLOWNESS))
					  					le.removePotionEffect(PotionEffectType.SLOWNESS);
					  				if (le.hasPotionEffect(PotionEffectType.SLOW_FALLING))
					  					le.removePotionEffect(PotionEffectType.SLOW_FALLING);
					  				int fem = 0;
					  				if (le instanceof Player p)
					  					if (p.isSneaking())
					  						fem += 11;
					  				if (le.getLocation().getBlock().getType() == Material.COBWEB || le.getLocation().add(0, 1, 0).getBlock().getType() == Material.COBWEB || le.getLocation().add(0, -1, 0).getBlock().getType() == Material.COBWEB) {
					  					fem += 30;
					  					if (le.getLocation().add(0, -0.1f, 0).getBlock().getType() == Material.COBWEB)
					  						le.teleport(le.getLocation().add(0, -0.1f, 0));
					  					if (le.getLocation().add(0, -0.02f, 0).getBlock().getType() == Material.COBWEB)
					  						le.teleport(le.getLocation().add(0, -0.02f, 0));
					  				}
					  				if (le.getLocation().getBlock().getType() == Material.WATER || le.getLocation().add(0, 1, 0).getBlock().getType() == Material.WATER || le.getLocation().add(0, -1, 0).getBlock().getType() == Material.WATER) {
					  					le.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20, 2));
					  				}
					  				if (le.getLocation().getBlock().getType() == Material.SOUL_SAND)
					  					fem += 2;
					  				else if (le.getLocation().getBlock().getType() == Material.HONEY_BLOCK)
					  					fem += 4;
					  				switch (le.getLocation().add(0, -1, 0).getBlock().getType()) {
						  				case SOUL_SAND:
						  					fem += 2;
						  					break;
						  				case SLIME_BLOCK:
						  					fem += 8;
						  					break;
						  				case HONEY_BLOCK:
						  					fem += 4;
						  					break;
						  				case ICE:
						  					fem += 1;
						  					break;
						  				case BLUE_ICE:
						  					fem += 1;
						  					break;
						  				case PACKED_ICE:
						  					fem += 1;
						  					break;
						  				case SWEET_BERRY_BUSH:
						  					fem += 6;
						  					break;
					  				}
					  				int speed = fem <= 30 ? fem : 30 + 2*(fem-30);
					  				if (fem == 34) speed = 42;
					  				else if (fem == 35) speed = 50;
					  				else if (fem == 36) speed = 58;
					  				else if (fem == 37) speed = 63;
					  				else if (fem == 38) speed = 68;
					  				else if (fem == 39) speed = 77;
					  				else if (fem == 40) speed = 87;
					  				else if (fem == 41) speed = 98;
					  				else if (fem == 42) speed = 109;
					  				else if (fem == 43) speed = 120;
					  				else if (fem == 44) speed = 130;
					  				le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, speed));
					  			}
					  		}
					  	}
						bulletUpdate();
						entityKillerUpdate();
						fancyMobUpdate();
						Arena.arenaTick();
						if (dalekShield.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<>();
							for (LivingEntity e : dalekShield.keySet()) {
								if (e == null || !e.isValid() || e.isDead() || dalekShield.get(e) <= 0) {
									remove.add(e);
								} else {
									dalekShield.put(e, dalekShield.get(e)-1);
									spawnParticleSphere(e, 1.5, 200, Particle.ELECTRIC_SPARK);
									for (Entity f : e.getNearbyEntities(2, 2, 2)) {
										if (f instanceof Projectile p) {
											if (p.getShooter() instanceof LivingEntity le && (le.equals(e) || le == e)) {	
											} else {
												Vector diff = e.getLocation().subtract(f.getLocation()).toVector();
												if (f.getVelocity().dot(diff) > 0) {
													//Bukkit.broadcastMessage("Deflected");
													f.getWorld().spawnParticle(Particle.GUST, f.getLocation(), 1, 0, 0, 0, 0);
													//Vector proj = diff.multiply(f.getVelocity().dot(diff) / diff.dot(diff));
													f.setVelocity(f.getVelocity().add(diff.normalize().multiply(-2)));
												}
											}
										}
									}
								}
							}
							for (LivingEntity e : remove) {
								dalekShield.remove(e);
							}
						}
						if (dalekThruster.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<>();
							for (LivingEntity e : dalekThruster.keySet()) {
								if (e == null || !e.isValid() || e.isDead() || dalekThruster.get(e) <= 0) {
									remove.add(e);
								} else {
									dalekThruster.put(e, dalekThruster.get(e)-1);
									e.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, e.getLocation(), 5, 0.02, 0, 0.02, 0);
									e.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2, 1));
									e.setVelocity(e.getVelocity().add(e.getLocation().getDirection().multiply(0.06f)));
								}
							}
							for (LivingEntity e : remove) {
								dalekThruster.remove(e);
							}
						}
						if (meteors.size() > 0) {
							ArrayList<FallingBlock> remove = new ArrayList<>();
							for (FallingBlock m : meteors) {
								if (m == null || !m.isValid())
									remove.add(m);
								else {
									Vector velocity = m.getVelocity();
									Location loc = m.getLocation();
									loc.getWorld().spawnParticle(Particle.LAVA, loc, 5, 0, 0, 0);
									LivingEntity shooter = (LivingEntity)m.getMetadata("Shooter").get(0).value();
									Vector minVector = loc.toVector().clone().subtract(new Vector(1, 1, 1));
									Vector maxVector = loc.toVector().clone().add(new Vector(1, 1, 1));
									Vector minVector2 = loc.toVector().clone().add(velocity.clone().multiply(0.5)).subtract(new Vector(1, 1, 1));
									Vector maxVector2 = loc.toVector().clone().add(velocity.clone().multiply(0.5)).add(new Vector(1, 1, 1));
									for (Entity e : m.getWorld().getNearbyEntities(m.getLocation(), 5, 5, 5)) {
										if (e instanceof LivingEntity le && le != shooter && Arena.validTarget(le)) {
											BoundingBox box = le.getBoundingBox();
											//boolean cancelled = false;
											if (box.overlaps(minVector, maxVector) || box.overlaps(minVector2, maxVector2)) {
												remove.add(m);
												explode(m.getLocation(), 5, 45, true, shooter);
												break;
											}
										}
									}
								}
							}
							for (FallingBlock m : remove) {
								meteors.remove(m);
								if (m != null)
									m.remove();
							}
						}
						if (arrowTrail.size() > 0) {
							ArrayList<AbstractArrow> remove = new ArrayList<>();
							for (AbstractArrow arrow : arrowTrail.keySet()) {
								if (arrow.isOnGround())
									remove.add(arrow);
								else {
									arrow.getLocation().getWorld().spawnParticle(arrowTrail.get(arrow), arrow.getLocation(), 1, 0, 0, 0, 0);
									arrow.getLocation().getWorld().spawnParticle(arrowTrail.get(arrow), arrow.getLocation().subtract(arrow.getVelocity().clone().multiply(0.25f)), 1, 0, 0, 0, 0);
									arrow.getLocation().getWorld().spawnParticle(arrowTrail.get(arrow), arrow.getLocation().subtract(arrow.getVelocity().clone().multiply(0.5f)), 1, 0, 0, 0, 0);
									arrow.getLocation().getWorld().spawnParticle(arrowTrail.get(arrow), arrow.getLocation().subtract(arrow.getVelocity().clone().multiply(0.75f)), 1, 0, 0, 0, 0);
								}
							}
						}
						if (acheronPortal.size() > 0) {
							ArrayList<Tuple<Location, Integer>> remove = new ArrayList<>();
							for (Tuple<Location, Integer> portal : acheronPortal.keySet()) {
								int portalTick = acheronPortal.get(portal);
								if (portalTick < 0) {
									remove.add(portal);
								} else {
									AcheronPortal(portal.x, portal.y, portalTick);
									acheronPortal.put(portal, portalTick-1);
								}
							}
							for (Tuple<Location, Integer> portal : remove) {
								acheronPortal.remove(portal);
							}
						}
						if (riding.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<>();
							for (LivingEntity le : riding.keySet()) {
								LivingEntity ve = riding.get(le);
								if ((le == null || !le.isValid()) || (ve == null || !ve.isValid())) {
									remove.add(le);
								} else {
									if (!le.isInsideVehicle()) {
										le.teleport(ve.getEyeLocation());
									}
									if ((ve.getScoreboardTags().contains("Grapple") || (ve instanceof Player p && p.getGameMode() == GameMode.SPECTATOR)) || rooted.containsKey(ve)) {
										if (ve.getPassengers().size() > 0)
											ve.removePassenger(le);
									} else if (!le.isInsideVehicle()) {
										ve.addPassenger(le);
									}
								}
							}
							for (LivingEntity le : remove)
								riding.remove(le);
						}
						if (sunbeam.size() > 0) {
							ArrayList<Tuple<LivingEntity, Integer>> remove = new ArrayList<>();
							for (Tuple<LivingEntity, Integer> pair : sunbeam.keySet()) {
								LivingEntity le = pair.x;
								if (pair == null || le == null || !le.isValid() || le.isDead() || sunbeam.get(pair) <= 0) {
									remove.add(pair);
								} else {
									sunbeam.put(pair, sunbeam.get(pair)-1);
									Location loc = le.getEyeLocation();
									Vector dir = loc.getDirection();
									
									switch (pair.y) {
										case 0:
										case 1:
										{
											Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.FLAME, 60, 0.8f, 26, le, FancyArena.instance);
											break;
										}
										case 2:
										{
											Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.FLAME, 80, 0.8f, 26, le, FancyArena.instance, 32);
											break;
										}
										case 3:
										{
											Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.FLAME, 100, 0.8f, 26, le, FancyArena.instance, 45);
											break;
										}
									}
								}
							}
							for (Tuple<LivingEntity, Integer> pair : remove)
								sunbeam.remove(pair);
						}
						if (grapple.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
							for (LivingEntity le : grapple.keySet()) {
								if (!le.isValid() || !grapple.get(le).isValid() || !grapple.get(le).getScoreboardTags().contains("Grapple")) {
									remove.add(le);
								} else {
									LivingEntity gr = grapple.get(le);
									Location loc = le.getEyeLocation().add(le.getEyeLocation().getDirection().multiply(2)).setDirection(gr.getEyeLocation().getDirection());
									/*if (loc.distance(le.getLocation()) < 2) {
										if (gr.isValid())
											gr.removeScoreboardTag("Grapple");
										//grapple.remove(le);
										remove.add(le);
									}*/
									if (!loc.getBlock().isPassable()) {
										Location loc2 = loc.clone().add(new Vector(0, 1, 0));
										if (!loc2.getBlock().isPassable()) {
											loc2 = loc.clone().add(new Vector(0, 0, 1));
											if (!loc2.getBlock().isPassable()) {
												loc2 = loc.clone().add(new Vector(0, 0, -1));
												if (!loc2.getBlock().isPassable()) {
													loc2 = loc.clone().add(new Vector(1, 0, 0));
													if (!loc2.getBlock().isPassable()) {
														loc2 = loc.clone().add(new Vector(-1, 0, 0));
													}
												}
											}
										}
										if (loc2.getBlock().isPassable()) {
											loc = loc2;
										} else if (Math.random() < 0.2) {
											remove.add(le);
										}
									}
									//teleport(gr, loc);
									
									if (gr.getPassengers().size() > 0)
										gr.removePassenger(gr.getPassengers().get(0));
									gr.teleport(loc);
									
									if (c % 5 == 0) {
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + gr.getLocation().getX() + " " + gr.getLocation().getY() + " " + gr.getLocation().getZ() + " as @e[tag=Grapple,limit=1,sort=nearest] run effect give @s minecraft:slow_falling 1 0 true");
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + gr.getLocation().getX() + " " + gr.getLocation().getY() + " " + gr.getLocation().getZ() + " as @e[tag=Grapple,limit=1,sort=nearest] run effect give @s minecraft:mining_fatigue 1 5 true");
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + gr.getLocation().getX() + " " + gr.getLocation().getY() + " " + gr.getLocation().getZ() + " as @e[tag=Grapple,limit=1,sort=nearest] run effect give @s minecraft:slowness 1 10 true");
									}
								}
							}
							for (LivingEntity le : remove) {
								if (grapple.get(le).isValid())
									grapple.get(le).removeScoreboardTag("Grapple");
								grapple.remove(le);
							}
						}
						
						if (repulsorFlying.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
							for (LivingEntity le : repulsorFlying) {
								if (le.isOnGround() || (le instanceof Player p && p.isSneaking())) {
									remove.add(le);
								}
								le.setGliding(true);
								Vector addVel = le.getVelocity().dot(le.getLocation().getDirection()) < 0.25 ? le.getLocation().getDirection().multiply(0.55f) : new Vector(0, 0, 0);
								if (le.getVelocity().getY() < 0.15 && le.getEyeLocation().getPitch() < 50)
									addVel.add(new Vector(0, 0.08f, 0));
								if (addVel.length() > 0.01f)
									le.setVelocity(le.getVelocity().add(addVel));
								//if (le.getVelocity().dot(le.getLocation().getDirection()) < 0.1) {
									//le.setVelocity(le.getVelocity().add(le.getLocation().getDirection().multiply(0.5f)).add(new Vector(0, 0.4f, 0)));
								//}
								le.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, le.getLocation().add(le.getVelocity()), 0);
							}
							for (LivingEntity le : remove) {
								repulsorFlying.remove(le);
								le.removePotionEffect(PotionEffectType.LEVITATION);
								le.removePotionEffect(PotionEffectType.SLOW_FALLING);
							}
						}
						if (hammerSpin.size() > 0) {
							ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
							for (LivingEntity le : hammerSpin.keySet()) {
								if (hammerSpin.get(le).y <= 2) {
									ItemStack mace = null;
									if (le.getEquipment().getItemInMainHand().getType() == Material.MACE) {
										mace = le.getEquipment().getItemInMainHand();
									} else if (le.getEquipment().getItemInOffHand().getType() == Material.MACE) {
										mace = le.getEquipment().getItemInOffHand();
									}
									if (mace != null) {
										ItemMeta meta = mace.getItemMeta();
										meta.setCustomModelData(3);
										mace.setItemMeta(meta);
									}
								}
								Location loc = le.getLocation();
								loc.setPitch(0);
								loc.setYaw(hammerSpin.get(le).x + hammerSpin.get(le).y);
								if (le.getPassengers().size() > 0)
									le.removePassenger(le.getPassengers().get(0));
								le.teleport(loc);
								//teleport(le, loc);
								if (hammerSpin.get(le).y % 2 == 0) {
									Bullet b = new Bullet(loc.getDirection().clone(), loc.clone().add(0, 1.55, 0), true, Particle.ASH, 11, 1, 18, le, instance);
								}
								if (hammerSpin.get(le).y >= 360) {
									remove.add(le);
									
									ItemStack mace = null;
									if (le.getEquipment().getItemInMainHand().getType() == Material.MACE) {
										mace = le.getEquipment().getItemInMainHand();
									} else if (le.getEquipment().getItemInOffHand().getType() == Material.MACE) {
										mace = le.getEquipment().getItemInOffHand();
									}
									if (mace != null) {
										ItemMeta meta = mace.getItemMeta();
										meta.setCustomModelData(1);
										mace.setItemMeta(meta);
									}
								}
								hammerSpin.put(le, new Tuple<Integer, Integer>(hammerSpin.get(le).x, hammerSpin.get(le).y + 10));
							}
							for (LivingEntity le : remove) {
								hammerSpin.remove(le);
							}
						}
						if (c % 4 == 0) {
							if (primordialWard.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<>();
								for (LivingEntity le : primordialWard.keySet()) {
									if (le == null || !le.isValid() || le.isDead() || primordialWard.get(le) <= 0) {
										remove.add(le);
										if (le != null && le.isValid() && !le.isDead()) {
											le.removeScoreboardTag("PrimordialWard");
										}
									} else {
										primordialWard.put(le, primordialWard.get(le)-1);
										if (le.getFireTicks() > 11)
											le.setFireTicks(10);
									}
								}
								for (LivingEntity le : remove)
									primordialWard.remove(le);
							}
							if (elementalWard.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<>();
								for (LivingEntity le : elementalWard.keySet()) {
									if (le == null || !le.isValid() || le.isDead() || elementalWard.get(le) <= 0) {
										remove.add(le);
										if (le != null && le.isValid() && !le.isDead()) {
											le.removeScoreboardTag("ElementalWard");
										}
									} else {
										elementalWard.put(le, elementalWard.get(le)-1);
										if (le.getFireTicks() > 11)
											le.setFireTicks(10);
									}
								}
								for (LivingEntity le : remove)
									elementalWard.remove(le);
							}
							if (repulsorFly.size() > 0) {
								for (LivingEntity le : repulsorFly) {
									if (le.getScoreboardTags().contains("RepulsorHover")) {
										le.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, le.getLocation(), 0);
										if (le.isOnGround())
											le.removeScoreboardTag("RepulsorHover");
									}
								}
							}
							
							if (rooted.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<>();
								for (LivingEntity root : rooted.keySet()) {
									if (root == null || !root.isValid() || rooted.get(root).y <= 0) {
										remove.add(root);
									} else {
										rooted.put(root, new Tuple<>(rooted.get(root).x, rooted.get(root).y-4));
										root.teleport(rooted.get(root).x);
									}
								}
								for (LivingEntity root : remove) {
									rooted.remove(root);
								}
							}
							/*
							if (magicMissiles.size() > 0) {
								ArrayList<ShulkerBullet> remove = new ArrayList<ShulkerBullet>();
								for (ShulkerBullet missile : magicMissiles.keySet()) {
									if (missile == null || !missile.isValid()) {
										remove.add(missile);
									} else {
										double targetSpeed = 1;
										if (missile.getVelocity().length() < targetSpeed) {
											missile.setVelocity(missile.getVelocity().normalize().multiply(targetSpeed));
										}
									}
								}
							}
							*/
							if (wallOfFireSpawner.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
								for (LivingEntity le : wallOfFireSpawner.keySet()) {
									if (wallOfFireSpawner.get(le) <= 0) {
										remove.add(le);
									} else {
										wallOfFireSpawner.put(le, wallOfFireSpawner.get(le) - 1);
										Bullet b = new Bullet(le.getLocation().getDirection(), le.getEyeLocation(), true, Particle.SOUL_FIRE_FLAME, 20, 0.8f, 21, le, instance);
									}
								}
							}

							  if (sentryMode.size() > 0) {
								  ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
								  for (LivingEntity le : sentryMode.keySet()) {
									  if (le == null || !le.isValid())
										  remove.add(le);
									  else {
										  ArmorStand gun = sentryMode.get(le);
										  Player player = le instanceof Player p ? p : null;
										  ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
										  if (sentryTarget.containsKey(le) && (player == null || (i != null && i.getAmount() > 0))) {
											  LivingEntity target = null;
											  float dist = 0;
											  ArrayList<LivingEntity> remove2 = new ArrayList<LivingEntity>();
											  for (LivingEntity t : sentryTarget.get(le)) {
												  if (t == null || !t.isValid()) {
													  remove2.add(t);
													  continue;
												  }
												  if (illusionDoubles.containsKey(t) && illusionDoubles.get(t) != null && illusionDoubles.get(t).isValid()) {
													  LivingEntity it = illusionDoubles.get(t);
													  float newDist = (float)it.getLocation().distance(gun.getEyeLocation());
													  if ((target == null || newDist < dist) && checkMinigunLineOfSight(gun, it.getLocation().toVector().add(new Vector(0, 1, 0)).subtract(gun.getLocation().toVector()), le, it)) {
														  dist = newDist;
														  target = t;
														  if (dist < 15)
															  break;
													  }
												  }
												  float newDist = (float)t.getLocation().distance(gun.getEyeLocation());
												  if ((target == null || newDist < dist) && checkMinigunLineOfSight(gun, t.getLocation().toVector().add(new Vector(0, 1, 0)).subtract(gun.getLocation().toVector()), le, t)) {
													  dist = newDist;
													  target = t;
													  if (dist < 15)
														  break;
												  }
											  }
											  for (LivingEntity t : remove2)
												  sentryTarget.get(le).remove(t);
											  if (target != null && dist < 30) {
												  pointArmorStandAtEntity(gun, target);
												  fireMinigunBullet(gun, target.getLocation().toVector().add(new Vector(0, 1, 0)).subtract(gun.getLocation().toVector()), le);
												  if (player != null && Math.random() < 0.4f)
													  i.setAmount(i.getAmount()-1);
											  }
										  } else {
											  sentryTarget.put(le, new ArrayList<LivingEntity>());
										  }
									  }
								  }
								  for (LivingEntity le : remove)
									  sentryMode.remove(le);
								  
							  }
							  
						}
						if (c % 2 == 0) {
							if (antimagic.size() > 0) {
								ArrayList<Tuple<BoundingBox, World>> remove = new ArrayList<Tuple<BoundingBox, World>>();
								for (Tuple<BoundingBox, World> bbox : antimagic.keySet()) {
									if (antimagic.get(bbox) <= 0)
										remove.add(bbox);
									else {
										if (c % 6 == 0) {
											for (double x = bbox.x.getMinX(); x < bbox.x.getMaxX(); x += 0.5) {
												bbox.y.spawnParticle(Particle.END_ROD, x, bbox.x.getMinY(), bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, x, bbox.x.getMinY(), bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, x, bbox.x.getMaxY(), bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, x, bbox.x.getMaxY(), bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
											}

											for (double y = bbox.x.getMinY(); y < bbox.x.getMaxY(); y += 0.5) {
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMinX(), y, bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMinX(), y, bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMaxX(), y, bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMaxX(), y, bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
											}

											for (double z = bbox.x.getMinZ(); z < bbox.x.getMaxZ(); z += 0.5) {
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMinX(), bbox.x.getMinY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMinX(), bbox.x.getMaxY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMaxX(), bbox.x.getMinY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.END_ROD, bbox.x.getMaxX(), bbox.x.getMaxY(), z, 1, 0, 0, 0, 0);
											}
										}
										for (Entity e : bbox.y.getNearbyEntities(bbox.x)) {
											if (e instanceof Projectile) {
												if (e.getType() == EntityType.FIREBALL || e.getType() == EntityType.DRAGON_FIREBALL || e.getType() == EntityType.SMALL_FIREBALL || e.getType() == EntityType.WIND_CHARGE || e.getType() == EntityType.BREEZE_WIND_CHARGE || e.getType() == EntityType.WITHER_SKULL) {
													//for (Player p : bbox.y.getPlayers())
														SFX.play(e.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
													bbox.y.spawnParticle(Particle.LARGE_SMOKE, e.getLocation(), 3);
													e.remove();
												}
											}
										}
										antimagic.put(bbox, antimagic.get(bbox)-1);
									}
								}
								for (Tuple<BoundingBox, World> bbox : remove)
									antimagic.remove(bbox);
							}
							if (hypnotic.size() > 0) {
								ArrayList<Triple<BoundingBox, World, LivingEntity>> remove = new ArrayList<Triple<BoundingBox, World, LivingEntity>>();
								for (Triple<BoundingBox, World, LivingEntity> bbox : hypnotic.keySet()) {
									if (hypnotic.get(bbox) <= 0)
										remove.add(bbox);
									else {
										if (c % 6 == 0) {
											for (double x = bbox.x.getMinX(); x < bbox.x.getMaxX(); x += 0.5) {
												bbox.y.spawnParticle(Particle.NOTE, x, bbox.x.getMinY(), bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, x, bbox.x.getMinY(), bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, x, bbox.x.getMaxY(), bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, x, bbox.x.getMaxY(), bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
											}

											for (double y = bbox.x.getMinY(); y < bbox.x.getMaxY(); y += 0.5) {
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMinX(), y, bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMinX(), y, bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMaxX(), y, bbox.x.getMinZ(), 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMaxX(), y, bbox.x.getMaxZ(), 1, 0, 0, 0, 0);
											}

											for (double z = bbox.x.getMinZ(); z < bbox.x.getMaxZ(); z += 0.5) {
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMinX(), bbox.x.getMinY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMinX(), bbox.x.getMaxY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMaxX(), bbox.x.getMinY(), z, 1, 0, 0, 0, 0);
												bbox.y.spawnParticle(Particle.NOTE, bbox.x.getMaxX(), bbox.x.getMaxY(), z, 1, 0, 0, 0, 0);
											}
										}
										for (Entity e : bbox.y.getNearbyEntities(bbox.x)) {
											if (e instanceof LivingEntity le && Arena.validTarget(le) && !le.equals(bbox.z) && !le.getScoreboardTags().contains("fom")) {
												root(le, le.getLocation(), 20*20);
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s nausea 30 100");
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slowness 30 100");
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s darkness 30 100");
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s weakness 30 100");
												//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slow_falling 30 100");
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s mining_fatigue 30 100");
											}
										}
										hypnotic.put(bbox, hypnotic.get(bbox)-1);
									}
								}
								for (Triple<BoundingBox, World, LivingEntity> bbox : remove)
									hypnotic.remove(bbox);
							}
							if (wallOfFire.size() > 0) {
								ArrayList<Location> remove = new ArrayList<Location>();
								for (Location loc : wallOfFire.keySet()) {
									int t = wallOfFire.get(loc).x;
									LivingEntity owner = wallOfFire.get(loc).y;
									
									for (Tuple<BoundingBox, World> bbox : FancyArena.antimagic.keySet()) {
										if (loc.getWorld() != bbox.y) {
											break;
										} else {
											if (bbox.x.contains(loc.toVector())) {
												
												//for (Player p : Bukkit.getOnlinePlayers())
													SFX.play(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
												loc.getWorld().spawnParticle(Particle.LARGE_SMOKE,loc, 3);
												t = -1;
												break;
											}
										}
										if (t <= 0)
											break;
									}
									
									if (t <= 0) {
										remove.add(loc);
									} else {
										t--;
										wallOfFire.put(loc, new Tuple<Integer, LivingEntity>(t, owner));
										loc.getWorld().spawnParticle(Particle.FLAME, loc.clone().add(0, 2, 0), 10, 0.5, 1, 0.5, 0.1);
										loc.getWorld().spawnParticle(Particle.LAVA, loc.clone().add(0, 2, 0), 10, 0.5, 1, 0.5, 0.1);
										loc.getWorld().spawnParticle(Particle.DUST_PILLAR, loc.clone().add(0, 2, 0), 10, 0.5, 1, 0.5, 0.1, Material.ORANGE_WOOL.createBlockData());
										if (Math.random() < 0.2) {
											//for (Player p : Bukkit.getOnlinePlayers()) {
												SFX.play(loc, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
											//}
										}
										
										for (Entity e : loc.getWorld().getNearbyEntities(loc, 1.5f, 1.5f, 1.5f)) {
											if (e instanceof LivingEntity le && !le.getScoreboardTags().contains("fom")) {
												if (le.getType() != EntityType.ARMOR_STAND && le.getType() != EntityType.PAINTING && le.getType() != EntityType.ITEM_FRAME && le.getType() != EntityType.GLOW_ITEM_FRAME) {
													Bullet.damage(le, 15, owner, Bullet.ELEMENTAL);
													//le.damage(16, owner);
													le.setFireTicks(30);
													loc.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, le.getLocation(), 1);
													//for (Player p : Bukkit.getOnlinePlayers()) {
														SFX.play(loc, Sound.ENTITY_GENERIC_BURN, 1, 0.9f);
													//}
													try {
														Vector diff = le.getLocation().toVector().clone().subtract(loc.toVector().clone()).normalize();
														diff.checkFinite();
														le.setVelocity(le.getVelocity().add(diff.multiply(0.6f)));
													} catch (Exception f) {}
												}
											}
										}
									}
								}
								for (Location loc : remove) {
									wallOfFire.remove(loc);
								}
							}
							if (homing.size() > 0) {
								ArrayList<Fireball> remove = new ArrayList<Fireball>();
								for (Fireball fb : homing.keySet()) {
									if (fb == null || !fb.isValid()) {
										remove.add(fb);
									} else {
										Vector ogDir = homing.get(fb).y.clone();
										LivingEntity owner = homing.get(fb).x;
										Vector newDir = fb.getDirection().add(owner.getLocation().getDirection().clone().subtract(ogDir));
										fb.setDirection(newDir);
									}
								}
								for (Fireball fb : remove) {
									homing.remove(fb);
								}
							}
							if (hammerBoost.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
								for (LivingEntity le : hammerBoost.keySet()) {
									if (hammerBoost.get(le) >= 9) {
										ItemStack mace = null;
										if (le.getEquipment().getItemInMainHand().getType() == Material.MACE) {
											mace = le.getEquipment().getItemInMainHand();
										} else if (le.getEquipment().getItemInOffHand().getType() == Material.MACE) {
											mace = le.getEquipment().getItemInOffHand();
										}
										if (mace != null) {
											ItemMeta meta = mace.getItemMeta();
											meta.setCustomModelData(2);
											mace.setItemMeta(meta);
										}
									}
									
									le.setVelocity(le.getLocation().getDirection().multiply(1.5));
									le.getLocation().getWorld().spawnParticle(Particle.EXPLOSION, le.getLocation(), 1);
									le.getLocation().getWorld().spawnParticle(Particle.FLAME, le.getLocation(), 1);
									//for (Player p : Bukkit.getOnlinePlayers())
										SFX.play(le.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 0.8f);
									hammerBoost.put(le, hammerBoost.get(le)-1);
									if (hammerBoost.get(le) <= 0) {
										remove.add(le);
										ItemStack mace = null;
										if (le.getEquipment().getItemInMainHand().getType() == Material.MACE) {
											mace = le.getEquipment().getItemInMainHand();
										} else if (le.getEquipment().getItemInOffHand().getType() == Material.MACE) {
											mace = le.getEquipment().getItemInOffHand();
										}
										if (mace != null) {
											ItemMeta meta = mace.getItemMeta();
											meta.setCustomModelData(1);
											mace.setItemMeta(meta);
										}
									}
									for (Entity e : le.getLocation().getWorld().getNearbyEntities(le.getLocation(), 1, 1, 1)) {
										if (e instanceof LivingEntity e2) {
											if (e2 != le && e2.getType() != EntityType.ARMOR_STAND && e2.getType() != EntityType.ITEM_FRAME && e2.getType() != EntityType.GLOW_ITEM_FRAME && e2.getType() != EntityType.ITEM_DISPLAY && e2.getType() != EntityType.ITEM && !e2.getScoreboardTags().contains("invulnerable")) {
												e2.damage(8, le);
												e2.setFireTicks(15);
											}
										}
									}
								}
								for (LivingEntity le : remove) {
									hammerBoost.remove(le);
								}
							}
							if (swarmingInsects.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
								for (LivingEntity le : swarmingInsects.keySet()) {
									if (swarmingInsects.get(le) >= 20) {
										le.setVisibleByDefault(false);
										le.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0, true));
										for (Player p : Bukkit.getOnlinePlayers()) {
											p.hideEntity(instance, le);
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " run playsound swarm");
										}
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s invisibility infinite 0 true");
										
									} else if (swarmingInsects.get(le) <= 1) {
										le.removePotionEffect(PotionEffectType.INVISIBILITY);
										le.setVisibleByDefault(true);
										for (Player p : Bukkit.getOnlinePlayers())
											p.showEntity(instance, le);
										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect clear @s invisibility");
										remove.add(le);
									}
									swarmingInsects.put(le, swarmingInsects.get(le)-1);
									le.getWorld().spawnParticle(Particle.SQUID_INK, le.getLocation(), 100, 6, 3, 6);
									for (Entity e : le.getWorld().getNearbyEntities(le.getLocation(), 6, 4, 6)) {
										if (e instanceof LivingEntity le2 && le2 != le) {
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le2.getLocation().getX() + " " + le2.getLocation().getY() + " " + le2.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest,distance=..1] run effect give @s darkness 2 2 true");
											Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le2.getLocation().getX() + " " + le2.getLocation().getY() + " " + le2.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest,distance=..1] run effect give @s blindness 2 2 true");
										}
									}
								}
								for (LivingEntity le : remove)
									swarmingInsects.remove(le);
								
							}
							if (delayedTeleport.size() > 0) {
								for (LivingEntity le : delayedTeleport.keySet()) {
									/*
									if (le.getPassengers().size() > 0)
										le.removePassenger(le.getPassengers().get(0));
									le.teleport(delayedTeleport.get(le));
									*/
									strong_teleport(le, delayedTeleport.get(le));
								}
								delayedTeleport.clear();
							}
							if (delayedInventory.size() > 0) {
								for (Player p : delayedInventory.keySet()) {
									p.openInventory(delayedInventory.get(p));
								}
								delayedInventory.clear();
							}
							if (rightClicks.size() > 0) {
								ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
								for (LivingEntity le : rightClicks.keySet()) {
									int uses = rightClicks.get(le).x;
									ItemStack item = rightClicks.get(le).y;
									if (uses > 0) {
										rightClicks.put(le, new Tuple<Integer, ItemStack>(uses-1, item));
										useItem(le, item, false, 0);
									} else {
										remove.add(le);
									}
								}
								for (LivingEntity le : remove) {
									rightClicks.remove(le);
								}
							}
							if (c % 10 == 0) {
								if (customLivingSounds.size() > 0) {
									for (int i = 0; i < customLivingSounds.size(); i++) {
										LivingEntity e = customLivingSounds.get(i);
										if (e == null || e.getHealth() < 0.0001 || !e.isValid()) {
											customLivingSounds.remove(i);
										} else if (Math.random() < 0.09) {
											if (e.getScoreboardTags().contains("CustomLivingSound")) {
												Location loc = e.getLocation();
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound " + e.getMetadata("livingSound").get(0).value() + " hostile @a ~ ~ ~ 0.9 1");
											}
										}
									}
								}
								if (c % 20 == 0) {
									if (playerStates.size() > 0) {
										for (Player p : playerStates.keySet()) {
											if (p.isOnline()) {
												playerStates.get(p)[4].copy(playerStates.get(p)[3]);
												playerStates.get(p)[3].copy(playerStates.get(p)[2]);
												playerStates.get(p)[2].copy(playerStates.get(p)[1]);
												playerStates.get(p)[1].copy(playerStates.get(p)[0]);
												playerStates.get(p)[0].setState(p);
											}
										}
									}
								}
							}
							if (gulag.size() > 0) {
								List<LivingEntity> remove = new ArrayList<LivingEntity>();
								for (LivingEntity le : gulag.keySet()) {
									if (le.getHealth() <= 0.00001f) {
										String name = le.getCustomName() == null ? le.getName() : le.getCustomName();
										Bukkit.broadcastMessage(name + " froze to death in Gulag");
										remove.add(le);
									} else {
										boolean gulagActive = false;
										for (PotionEffect effect : le.getActivePotionEffects()) {
											if (effect.getType() == PotionEffectType.SLOWNESS && effect.getDuration() > 0 && effect.getAmplifier() > 100) {
												// Gulag still active
												gulagActive = true;
												break;
											}
										}
										if (gulagActive) {
											//le.teleport(gulagLoc);
										} else {
											/*
											delayedTeleport.put(le, gulag.get(le).clone());
											if (le.getPassengers().size() > 0)
												le.removePassenger(le.getPassengers().get(0));
											le.teleport(gulag.get(le));*/
											super_strong_teleport(le, gulag.get(le).clone());
											remove.add(le);
											//for (Player p : Bukkit.getOnlinePlayers()) {
												SFX.play(gulag.get(le), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, SoundCategory.HOSTILE, 1, 1);
											//}
											gulag.get(le).getWorld().spawnParticle(Particle.CAMPFIRE_SIGNAL_SMOKE, gulag.get(le), 50, 0.1, 0.5, 0.5, 0.5);
											if (le.getPassengers().size() > 0)
												le.removePassenger(le.getPassengers().get(0));
											le.teleport(gulag.get(le));
										}
									}
								}
								if (remove.size() > 0) {
									for (LivingEntity le : remove)
										gulag.remove(le);
								}
							}
						}
						//System.out.println("Main loop");
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    	}
			  }
		
			}, 0L, 1L);
	}
	
	private static void spawnParticleSphere(LivingEntity e, double radius, int density, Particle particle) {
		// TODO Auto-generated method stub
		Location center = e.getLocation().add(0, 1, 0); // Center at chest height

	    for (int i = 0; i < density; i++) {
	        double theta = Math.random() * 2 * Math.PI;  // Angle around the Y axis
	        double phi = Math.acos(2 * Math.random() - 1); // Angle from the vertical axis

	        double x = radius * Math.sin(phi) * Math.cos(theta);
	        double y = radius * Math.cos(phi);
	        double z = radius * Math.sin(phi) * Math.sin(theta);

	        Location particleLocation = center.clone().add(x, y, z);
	        e.getWorld().spawnParticle(particle, particleLocation, 0);
	    }
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (event.getMessage() != null && event.getMessage().trim().equalsIgnoreCase("lag")) {
			LocalDateTime now = LocalDateTime.now();
	        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        String filepath = "plugins/FancyArena/logs/laglog.csv";
	        
	        try {
	            // Ensure parent directories exist
	            java.io.File file = new java.io.File(filepath);
	            file.getParentFile().mkdirs();

	            // Append to CSV file
	            try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
	                writer.println(timestamp);
	            }
	            event.getPlayer().sendMessage(ChatColor.AQUA + "Logged laggy event");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		} else if (event.getMessage() != null && event.getMessage().trim().equalsIgnoreCase("bossbar")) {
			Arena.resetBossBars();
		} /*else if (event.getMessage() != null && event.getMessage().trim().equalsIgnoreCase("debug")) {
			openDebugInventory(event.getPlayer());
		}*/
	}
	
	@EventHandler
	public void onPlayerDisconnect(PlayerQuitEvent event) {
		if (Arena.runningArenas.size() > 0) {
			for (String arenaName : Arena.runningArenas) {
				boolean onlinePlayer = false;
				for (Player p : Bukkit.getOnlinePlayers())
					if (p.getScoreboardTags().contains("Ready_" + arenaName))
						onlinePlayer = true;
				if (onlinePlayer == false) {
					Arena.stopArena(arenaName);
					return;
				}
			}
		}
	}
	
	public float angleDiff(float a, float b) {
		float diff = a-b;
		if (Math.abs(diff) > Math.abs(a-b-360))
			diff = a-b-360;
		if (Math.abs(diff) > Math.abs(a-b+360))
			diff = a-b + 360;
		return diff;
	}
	
	public Vector pitchYawToDirection(float pitch, float yaw) {
	    float pitchRad = (float) ((pitch + 90) * Math.PI / 180);
	    float yawRad = (float) ((yaw + 90) * Math.PI / 180);
	    float x = (float) (Math.sin(pitchRad) * Math.cos(yawRad));
	    float y = (float) (Math.sin(pitchRad) * Math.sin(yawRad));
	    float z = (float) Math.cos(pitchRad);
	    return new Vector(x, y, z);
	}
	
	public static void fancyMobUpdate() {
		
		if (giants.size() > 0) {
			ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
			for (LivingEntity le : giants.keySet()) {
				if (le == null || !le.isValid() || le.isDead()) {
					remove.add(le);
				} else {
					Zombie stand = giants.get(le);
					stand.teleport(le.getEyeLocation());
					
					
					if (Math.random() < 0.01) {
						if (Math.random() < 0.8) {
							for (Player p : le.getWorld().getPlayers()) {
								if (p.getLocation().distance(le.getEyeLocation()) < 60) {
									Location newLoc = le.getLocation().setDirection(p.getLocation().subtract(le.getEyeLocation()).toVector().normalize());
									le.setRotation(newLoc.getYaw(), newLoc.getPitch());
									//le.teleport(newLoc);
									break;
								}
							}
						}
							ItemStack item = le.getEquipment().getItemInMainHand();
							int var = 0;
							Boolean[] yeet = new Boolean[] {false, false, false};
							if (item != null && item.hasItemMeta()) { // This is to figure out which mouse button to use, it just uses a random available one, so either left click, right click, or Q
								String itemName2 = item.getItemMeta().getDisplayName();
								if (itemBtn.containsKey(itemName2)) {
									yeet = FancyArena.itemBtn.get(itemName2);
									
									if (yeet[0] || yeet[1] || yeet[2]) {
										do {
											var = (int)(Math.random()*3);
										} while (yeet[var] == false);
										instance.useItem(le, item, false, var);
									}
								}
							}
							//boolean cancelEvent = useItem(giant == null ? entity : giant, item, false, var);							
					}
					
				}
			}
			for (LivingEntity le : remove) {
				giants.get(le).remove();
				giants.remove(le);
			}
		} 
		/*if (giants.size() > 0) {
			ArrayList<LivingEntity> remove = new ArrayList<LivingEntity>();
			for (LivingEntity le : giants.keySet()) {
				if (le == null) {
					remove.add(le);
				} else {
					Giant giant = giants.get(le).x;
					Entity target = giants.get(le).y;
					
					if (!le.isValid() || giant == null || !giant.isValid() || le.getHealth() <= 0.01 || giant.getHealth() <= 0.01) {
						if (giant != null) {
							giant.setHealth(0);
						}
						//le.remove();
						remove.add(le);
					} else {
					
						Location loc = target != null ? le.getLocation().clone().setDirection(target.getLocation().subtract(le.getLocation()).toVector()) : le.getLocation();
						giant.teleport(loc);
					}
				}
			}
			for (LivingEntity le : remove) {
				giants.remove(le);
				if (le != null)
					le.remove();
			}
		}*/
		if (Math.random() < 0.06) {
			boolean skipWeirdos = false;
			if (Arena.runningArenas.size() > 0)
				if (Arena.currentLevel.get(Arena.runningArenas.get(0)) > 35 && Arena.currentLevel.get(Arena.runningArenas.get(0)) < 42) // subject to change
					skipWeirdos = true;
			for (World world : Bukkit.getWorlds()) {
				if (world.getPlayers().size() > 0) {
					for (Entity entity : world.getEntities()) {
						if (entity.getScoreboardTags().contains("FancyMob")) {
							if (entity instanceof LivingEntity le && le.hasAI()) {
								if ((!skipWeirdos && Math.random() < 0.2) || ((le.getType() == EntityType.ENDER_DRAGON || le.getType() == EntityType.WARDEN || le.getType() == EntityType.WITHER || le.getType() == EntityType.IRON_GOLEM || le.getType() == EntityType.ELDER_GUARDIAN) && Math.random() < 0.5))
									FancyMob.SwapWeapon(le);
							}
						}
					}
				}
			}
		}
		if (Math.random() < 0.1) {
			
		}
	}
	
	public static void addPotionEffect(LivingEntity le, PotionEffect effect) {
		Location location = le.getLocation();
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s " + effect.getType().toString().toLowerCase() + " " + effect.getDuration()/20 + " " + effect.getAmplifier());
	}
	
	public void openDebugInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Debug Menu");
		
		ItemStack item = new ItemStack(Material.DRAGON_HEAD);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "Reset Bossbars");
		item.setItemMeta(meta);
		inv.addItem(item);

		item = new ItemStack(Material.REDSTONE_BLOCK);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.RED + "See Healthbar");
		List<String> lore = new ArrayList<String>();
		lore.add("Adds healthbar to nearest monster");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.RAW_COPPER);
		meta = item.getItemMeta();
		meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.YELLOW + "Reset Sentry Mode");
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.RAW_COPPER);
		meta = item.getItemMeta();
		meta.setCustomModelData(14);
		meta.setDisplayName(ChatColor.YELLOW + "Reset Durrith Wings");
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.SKELETON_SKULL);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.YELLOW + "Clear Arena Monsters");
		lore = new ArrayList<String>();
		lore.add("Clears all non-boss monsters");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.WITHER_SKELETON_SKULL);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.YELLOW + "Clear Arena Bosses");
		lore = new ArrayList<String>();
		lore.add("Clears all boss monsters");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.EMERALD);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.GREEN + "Fix Shops");
		//lore = new ArrayList<String>();
		//lore.add("Clears all boss monsters");
		//meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.CHEST_MINECART);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.YELLOW + "Retrieve Loot");
		//meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Retrieves all nearby chest minecarts");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);

		item = new ItemStack(Material.CHEST_MINECART);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.YELLOW + "Retrieve All Loot");
		meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Retrieves all chest minecarts in the multiverse");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.NETHER_STAR);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.GOLD + "Gain Extra Life");
		meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Gives you another life");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);

		item = new ItemStack(Material.BEACON);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.AQUA + "Level Spawn");
		meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Teleport to level's spawn");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		item = new ItemStack(Material.DROPPER);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.GREEN + "Retrieve Lost Item");
		meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Retrieve a commonly-lost item");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);

		item = new ItemStack(Material.NAME_TAG);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.RED + "Remove Excess Tags");
		//meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Removes tags that may have gotten stuck");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);

		item = new ItemStack(Material.FIRE_CHARGE);
		meta = item.getItemMeta();
		//meta.setCustomModelData(4);
		meta.setDisplayName(ChatColor.RED + "Clear Fire");
		//meta.setEnchantmentGlintOverride(true);
		lore = new ArrayList<String>();
		lore.add("Removes nearby fire");
		meta.setLore(lore);
		item.setItemMeta(meta);
		inv.addItem(item);
		
		player.openInventory(inv);
	}
	
	public void openLostAndFound(Player player) {
		Chest chest = (Chest)((new Location(FancyMob.templateWorld, 6964, 98, 6987).getBlock()).getState());
		ItemStack[] stack = chest.getInventory().getContents();
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_GREEN + "Lost and Found");
		inv.setContents(stack);
		
		player.openInventory(inv);
	}
	
	public void openWildshapeInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Select Wildshape");
		inv.addItem(Polymorph.getHead(Polymorph.DIRE_WOLF));
		inv.addItem(Polymorph.getHead(Polymorph.BADGER));
		inv.addItem(Polymorph.getHead(Polymorph.OWL));
		inv.addItem(Polymorph.getHead(Polymorph.DEINONYCHUS));
		player.openInventory(inv);
	}
	
	public void openSuperiorWildshapeInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Select Wildshape (Rugged)");
		inv.addItem(Polymorph.getHead(Polymorph.DIRE_WOLF));
		inv.addItem(Polymorph.getHead(Polymorph.BADGER));
		inv.addItem(Polymorph.getHead(Polymorph.OWL));
		inv.addItem(Polymorph.getHead(Polymorph.DEINONYCHUS));
		player.openInventory(inv);
	}
	
	public void openPolymorphInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Select Polymorph");
		inv.addItem(Polymorph.getHead(Polymorph.QUETZAL));
		inv.addItem(Polymorph.getHead(Polymorph.TREX));
		player.openInventory(inv);
	}
	
	public void openSupremeWildshapeInventory(Player player) {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Select Wildshape (Archdruid)");
		inv.addItem(Polymorph.getHead(Polymorph.DIRE_WOLF));
		inv.addItem(Polymorph.getHead(Polymorph.BADGER));
		inv.addItem(	Polymorph.getHead(Polymorph.OWL));
		inv.addItem(Polymorph.getHead(Polymorph.DEINONYCHUS));
		inv.addItem(Polymorph.getHead(Polymorph.QUETZAL));
		inv.addItem(Polymorph.getHead(Polymorph.TREX));
		player.openInventory(inv);
	}
	
    static boolean bouncingInventory = false;
	
	/**
     * Opens the deletion GUI for a given player, page, and villager.
     */
    public static void openDeletionInventory(Player player, int page, Villager villager) {
    	bouncingInventory = true;
        // Create a double-chest inventory (54 slots) titled "Delete Trade".
        Inventory inv = Bukkit.createInventory(null, 54, "Delete Trade");
        List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());
        int totalTrades = recipes.size();
        int startIndex = page * TRADES_PER_PAGE;
        int endIndex = Math.min(startIndex + TRADES_PER_PAGE, totalTrades);

        // For each trade on this page, build a row with our layout.
        for (int i = startIndex; i < endIndex; i++) {
            int row = i - startIndex;  // row number on this page (0-based)
            MerchantRecipe recipe = recipes.get(i);
            int baseSlot = row * 9;

            // --- Price Ingredients ---
            // We'll show the first price ingredient in slot (baseSlot+1) and the second (if available) in slot (baseSlot+2).
            if (!recipe.getIngredients().isEmpty()) {
                ItemStack price1 = recipe.getIngredients().get(0).clone();
                ItemMeta meta1 = price1.getItemMeta();
                if (meta1 != null) {
                    //meta1.setDisplayName(ChatColor.GOLD + "Price 1");
                    //List<String> lore = new ArrayList<>();
                    //lore.add(ChatColor.GRAY + "Trade index: " + i);
                    //meta1.setLore(lore);
                    //price1.setItemMeta(meta1);
                }
                inv.setItem(baseSlot + 1, price1);
            }
            if (recipe.getIngredients().size() >= 2) {
                ItemStack price2 = recipe.getIngredients().get(1).clone();
                ItemMeta meta2 = price2.getItemMeta();
                if (meta2 != null) {
                	/*
                    meta2.setDisplayName(ChatColor.GOLD + "Price 2");
                    List<String> lore = new ArrayList<>();
                    lore.add(ChatColor.GRAY + "Trade index: " + i);
                    meta2.setLore(lore);
                    price2.setItemMeta(meta2);
                    */
                }
                inv.setItem(baseSlot + 2, price2);
            }

            // --- Delete Button ---
            // The delete button is placed in slot (baseSlot+4).
            ItemStack deleteItem = new ItemStack(Material.BARRIER);
            ItemMeta deleteMeta = deleteItem.getItemMeta();
            if (deleteMeta != null) {
                deleteMeta.setDisplayName(ChatColor.RED + "Delete Trade");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Click to delete trade index: " + i);
                deleteMeta.setLore(lore);
                deleteItem.setItemMeta(deleteMeta);
            }
            inv.setItem(baseSlot + 4, deleteItem);

            // --- Reward Item ---
            // The reward is shown in slot (baseSlot+6).
            ItemStack rewardItem = recipe.getResult().clone();
            ItemMeta rewardMeta = rewardItem.getItemMeta();
            if (rewardMeta != null) {
            	/*
                rewardMeta.setDisplayName(ChatColor.GREEN + "Reward");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Trade index: " + i);
                rewardMeta.setLore(lore);
                rewardItem.setItemMeta(rewardMeta);
                */
            }
            inv.setItem(baseSlot + 6, rewardItem);
        }

        // --- Navigation Arrows ---
        // Place a "Previous Page" arrow in slot 45 if there is a previous page.
        if (page > 0) {
            ItemStack prevArrow = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevArrow.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                prevArrow.setItemMeta(prevMeta);
            }
            inv.setItem(45, prevArrow);
        }
        // Place a "Next Page" arrow in slot 53 if there are more trades.
        if (endIndex < totalTrades) {
            ItemStack nextArrow = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextArrow.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                nextArrow.setItemMeta(nextMeta);
            }
            inv.setItem(53, nextArrow);
        }

        // Optionally, fill any remaining slots with a filler item.
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(" ");
            filler.setItemMeta(fillerMeta);
        }
        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (inv.getItem(slot) == null) {
                inv.setItem(slot, filler);
            }
        }

        player.openInventory(inv);
        
        Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        	bouncingInventory = false;
        }, 2); // Delay of 0.1 seconds
    }
	
    public boolean itemCompare(ItemStack a, ItemStack b) {
    	if (a == null || b == null) return false;
    	boolean result = false;
    	if (a.getType() == b.getType()) {
    		result = true;
    		if (a.hasItemMeta() && b.hasItemMeta()) {
    			if (a.getItemMeta().hasDisplayName() && b.getItemMeta().hasDisplayName()) {
    				if (a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName())) {
    					return true;
    				} else {
    					return false;
    				}
    			}
    			if (a.getItemMeta().hasDisplayName() != b.getItemMeta().hasDisplayName()) return false;
    		}
    		
    		if (a.hasItemMeta() != b.hasItemMeta()) return false;
    	}
    	return result;
    }
    
    public static void SpawnAcheronPortal(Location loc) {
    	int type = (int)(Math.random()*5);
    	acheronPortal.put(new Tuple<Location, Integer>(loc, type), 20*12);
    	//for (Player p : Bukkit.getOnlinePlayers())
    		SFX.play(loc, Sound.ENTITY_WITHER_SPAWN, 2, 0.8f);
    }
    
    public static void AcheronPortal(Location loc, int type, int c) {
    	World world = loc.getWorld();
    	//Location[] spiralPoints = new Location[100];
    	int numPoints = 40;
    	float dustSize = 1f;
    	DustOptions dust = new DustOptions(Color.WHITE, 0.5f);
    	switch (type) {
	    	case 0: // Shadowbreaker's world
	    		if (c % 2 == 0) {
	    			dust = new DustOptions(Color.BLACK, dustSize);
	    		} else {
	    			dust = new DustOptions(Color.PURPLE, dustSize);
	    		}
	    		break;
	    	case 1: // Acheron
	    		if (c % 2 == 0) {
	    			dust = new DustOptions(Color.ORANGE, dustSize);
	    		} else {
	    			dust = new DustOptions(Color.GRAY, dustSize);
	    		}
	    		break;
	    	case 2: // Abyss
	    		if (c % 2 == 0) {
	    			dust = new DustOptions(Color.BLACK, dustSize);
	    		} else {
	    			dust = new DustOptions(Color.RED, dustSize);
	    		}
	    		break;
	    	case 3: // Bytopia
	    		if (c % 2 == 0) {
	    			dust = new DustOptions(Color.GREEN, dustSize);
	    		} else {
	    			dust = new DustOptions(Color.YELLOW, dustSize);
	    		}
	    		break;
	    	case 4: // plane of water
	    		if (c % 2 == 0) {
	    			dust = new DustOptions(Color.AQUA, dustSize);
	    		} else {
	    			dust = new DustOptions(Color.BLUE, dustSize);
	    		}
	    		break;
    	}
    	for (int i = 0; i < numPoints; i++) {
    		double[] points = generateSpiralPoint(360 * i/numPoints, c*6);
			world.spawnParticle(Particle.DUST, new Location(world, loc.getX() + points[0], loc.getY(), loc.getZ() + points[1]), 0, 0.5f, 0, 0, dust);
			world.spawnParticle(Particle.DUST, new Location(world, loc.getX() - points[0], loc.getY(), loc.getZ() - points[1]), 0, 0.5f, 0, 0, dust);
			//points = generateSpiralPoint(360 * i/numPoints, c*6 + 90);
			world.spawnParticle(Particle.DUST, new Location(world, loc.getX() + points[0]/2, loc.getY(), loc.getZ() + points[1]/2), 0, 0.5f, 0, 0, dust);
			world.spawnParticle(Particle.DUST, new Location(world, loc.getX() - points[0]/2, loc.getY(), loc.getZ() - points[1]/2), 0, 0.5f, 0, 0, dust);
    	}
    	if (c % 6 == 0) {
	    	for (Entity e : world.getNearbyEntities(loc, 5, 2, 5)) {
	    		if (e instanceof LivingEntity le && Arena.validTarget(le)) {
	    			Bullet.damage(le, 11, null, Bullet.FORCE);
	    		}
	    	}
    	}
    }
    
    /**
     * Returns the (x, y) coordinates on a Fibonacci (golden) spiral.
     *
     * @param tDegrees the parameter along the spiral in degrees (controls how far along the spiral the point is)
     * @param rotationDegrees the amount in degrees to rotate the spiral
     * @return an array of two doubles: {x, y}
     */
    public static double[] generateSpiralPoint(double tDegrees, double rotationDegrees) {
        // Convert the parameter and rotation offset from degrees to radians.
        double t = Math.toRadians(tDegrees);
        double rotation = Math.toRadians(rotationDegrees);

        // Define the golden ratio (phi) and compute b such that after a quarter turn (pi/2), r increases by phi.
        double phi = (1 + Math.sqrt(5)) / 2;
        double b = (2 * Math.log(phi)) / Math.PI;  // because e^(b*(pi/2)) = phi

        // Compute the radial distance along the spiral.
        double r = Math.exp(b * t);

        // Apply rotation offset to the parameter.
        double angle = t + rotation;

        // Compute the x and y coordinates.
        double x = r * Math.cos(angle);
        double y = r * Math.sin(angle);

        return new double[] { x, y };
    }
    
    private HashMap<UUID, Inventory> brewingInventories = new HashMap<>();
    
    // Handle drag events (if the player drags a DECORATED_POT into a bag inventory)
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        InventoryView view = event.getView();
        if (view.getTitle().toLowerCase().contains("bag")) {
            // Check all items being placed by the drag event
            for (ItemStack item : event.getNewItems().values()) {
                if (item != null && item.getType() == Material.DECORATED_POT) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot place a bag inside another bag!");
                    break;
                }
                if (item != null && item.getType() == Material.BARRIER) {
                    event.setCancelled(true); // Prevent movement
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot move locked slots!");
                    break;
                }
            }
        }
    }
    
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTitle().contains("Debug Menu")) {
			event.setCancelled(true);
			if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
			String name = event.getCurrentItem().getItemMeta().getDisplayName();
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();
			SFX.play(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0.6f);
			if (name.contains("Reset Bossbars")) {
				Arena.resetBossBars();

				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Resetting bossbars");
			} 
			else if (name.contains("See Healthbar")) {
				LivingEntity target = null;
				for (Entity e : player.getWorld().getEntities()) {
					if (e instanceof LivingEntity le && !(e instanceof Player) && Arena.validTarget(le)) {
						if (target == null || target.getLocation().distance(player.getLocation()) > le.getLocation().distance(player.getLocation())) {
							target = le;
						}
					}
				}
				if (target != null) {
					String n = target.getCustomName();
					if (n == null)
						n = target.getName();
					BossBar bossBar = Bukkit.getServer().createBossBar(n, BarColor.YELLOW, BarStyle.SOLID, BarFlag.CREATE_FOG);
					for (Player p : Bukkit.getServer().getOnlinePlayers()) {
						bossBar.addPlayer(p);
					}
					Arena.bossBars.put(target, bossBar);
					Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Adding bossbar to " + n);
				}
				else
					Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " No monsters");
			}
			else if (name.contains("Reset Sentry Mode")) {
				for (LivingEntity le : sentryMode.keySet()) {
					ArmorStand g = sentryMode.get(le);
					if (g != null && g.isValid()) {
						g.remove();
					}
				}
				sentryMode.clear();
				sentryTarget.clear();
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntitiesByClass(ArmorStand.class)) {
						if (e.getScoreboardTags().contains("SentryMode")) {
							e.remove();
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Resetting Sentry Mode");
			} else if (name.contains("Reset Durrith Wings")) {
				for (LivingEntity le : durrithWings.keySet()) {
					ArmorStand g = durrithWings.get(le);
					if (g != null && g.isValid()) {
						g.remove();
					}
				}
				durrithWings.clear();
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntitiesByClass(ArmorStand.class)) {
						if (e.getScoreboardTags().contains("DurrithWings")) {
							e.remove();
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Resetting Durrith's Wings");
			} else if (name.contains("Clear Arena Monsters")) {
				int i = 0;
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof LivingEntity le && e.getScoreboardTags().contains("FancyMob") && !e.getScoreboardTags().contains("FancyBoss")) {
							i++;
							e.remove();
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Deleting " + i + " arena monsters");
			} else if (name.contains("Clear Arena Monsters")) {
				int i = 0;
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof LivingEntity le && e.getScoreboardTags().contains("FancyMob") && !e.getScoreboardTags().contains("FancyBoss")) {
							i++;
							e.remove();
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Deleting " + i + " arena monsters");
			} else if (name.contains("Clear Arena Bosses")) {
				int i = 0;
				for (World w : Bukkit.getWorlds()) {
					for (Entity e : w.getEntities()) {
						if (e instanceof LivingEntity le && e.getScoreboardTags().contains("FancyBoss")) {
							i++;
							e.remove();
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Deleting " + i + " arena bosses");
			} else if (name.contains("Fix Shops")) {
				tempPriceItems = null;
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Fixing shops");
			} else if (name.contains("Retrieve Loot")) {
				//tempPriceItems = null;
				for (StorageMinecart e : player.getWorld().getEntitiesByClass(StorageMinecart.class)) {
					if (e.getScoreboardTags().contains("FancyBossLoot") && e.getLocation().distance(player.getLocation()) < 200) {
						e.teleport(player);
						e.setVelocity(new Vector(Math.random()*4-2, Math.random()*2, Math.random()*4-2));
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Retrieving nearby loot");
			} else if (name.contains("Retrieve All Loot")) {
				//tempPriceItems = null;
				for (World w : Bukkit.getWorlds()) {
					for (StorageMinecart e : w.getEntitiesByClass(StorageMinecart.class)) {
						if (e.getScoreboardTags().contains("FancyBossLoot")) {
							e.teleport(player);
							e.setVelocity(new Vector(Math.random()*4-2, Math.random()*2, Math.random()*4-2));
						}
					}
				}
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Retrieving all loot");
			} else if (name.contains("Gain Extra Life")) {
				//tempPriceItems = null;
				SFX.play(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1.6f);
				
				player.getLocation().getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 10, 1, 0.5, 0.5, 0.5);
				
				Arena.lives.put(player.getName(), Arena.lives.get(player.getName()) + 1);
				
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Giving " + player.getName() + ChatColor.WHITE + " an extra life");
				player.sendMessage(ChatColor.GREEN + "You gained another life! You now have " + Arena.lives.get(player.getName()) + " lives.");
			} else if (name.contains("Level Spawn")) {
				
				for (String arenaName : Arena.runningArenas) {
					if (player.getScoreboardTags().contains("Ready_" + arenaName)) {
						/*if (p.getPassengers().size() > 0)
							p.removePassenger(p.getPassengers().get(0));*/
						FancyArena.prepare_teleport(player);
						player.teleport(Arena.getPlayerSpawn(arenaName));
						player.setGameMode(GameMode.SURVIVAL);
						Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " Teleporting " + player.getName() + ChatColor.WHITE + " to level spawn");
					}
				}
			} else if (name.contains("Retrieve Lost Item")) {
				
				SFX.play(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
				
				openLostAndFound(player);
				
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " " + player.getName() + ChatColor.WHITE + " opened the lost and found");
			} else if (name.contains("Remove Excess Tags")) {
				
				//SFX.play(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1f, 1f);
				
				//openLostAndFound(player);
				
				Player p = player;
					if (p.getScoreboardTags().contains("ElementalWard"))
						p.removeScoreboardTag("ElementalWard");
					if (p.getScoreboardTags().contains("PrimordialWard"))
						p.removeScoreboardTag("PrimordialWard");
					if (p.getScoreboardTags().contains("PrimordialWard"))
						p.removeScoreboardTag("PrimordialWard");
					if (p.getScoreboardTags().contains("Moving"))
						p.removeScoreboardTag("Moving");
					if (p.getScoreboardTags().contains("Polymorph"))
						p.removeScoreboardTag("Polymorph");
					if (p.getScoreboardTags().contains("Shield"))
						p.removeScoreboardTag("Shield");
					if (p.getScoreboardTags().contains("shield"))
						p.removeScoreboardTag("shield");
					if (p.getScoreboardTags().contains("fireball"))
						p.removeScoreboardTag("fireball");
					if (p.getScoreboardTags().contains("BagUpgrade"))
						p.removeScoreboardTag("BagUpgrade");
					if (p.getScoreboardTags().contains("BagOfHoldingUpgrade"))
						p.removeScoreboardTag("BagOfHoldingUpgrade");
					if (p.getScoreboardTags().contains("ConvergentFuture"))
						p.removeScoreboardTag("ConvergentFuture");
					if (p.getScoreboardTags().contains("RelentlessRage"))
						p.removeScoreboardTag("RelentlessRage");
					if (p.getScoreboardTags().contains("Grapple"))
						p.removeScoreboardTag("Grapple");
					if (p.getScoreboardTags().contains("RepulsorHover"))
						p.removeScoreboardTag("RepulsorHover");
					if (p.getScoreboardTags().contains("Meteor"))
						p.removeScoreboardTag("Meteor");
					if (p.getScoreboardTags().contains("DurrithWings"))
						p.removeScoreboardTag("DurrithWings");
					if (p.getScoreboardTags().contains("invulnerable"))
						p.removeScoreboardTag("invulnerable");
					if (p.getScoreboardTags().contains("SentryMode"))
						p.removeScoreboardTag("SentryMode");
					if (p.getScoreboardTags().contains("fom"))
						p.removeScoreboardTag("fom");
					if (p.getScoreboardTags().contains("antilife"))
						p.removeScoreboardTag("antilife");
				
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " " + player.getName() + ChatColor.WHITE + " removed their excess tags");
			} else if (name.contains("Clear Fire")) {
				SFX.play(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
				Arena.ClearFire(player.getWorld(), player.getLocation().getBlockX()-30, player.getLocation().getBlockY()-6, player.getLocation().getBlockZ()-30, player.getLocation().getBlockX()+30, player.getLocation().getBlockY()+6, player.getLocation().getBlockZ()+30);
				Bukkit.broadcastMessage(ChatColor.RED + "[DEBUG]" + ChatColor.WHITE + " " + player.getName() + ChatColor.WHITE + " cleared fire");
			}
		} 
		else if (event.getView().getTitle().contains("Select Polymorph") || event.getView().getTitle().contains("Select Wildshape")) {
			event.setCancelled(true);
			if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
			String name = event.getCurrentItem().getItemMeta().getDisplayName();
			Player player = (Player) event.getWhoClicked();
			player.closeInventory();
			int morphID = Polymorph.getID(name);
			if (morphID >= 0) {
				Polymorph morph = new Polymorph(player, morphID, event.getView().getTitle().contains("Rugged") ? 1.5f : (event.getView().getTitle().contains("Archdruid") ? 3 : 1));
				polymorphs.add(morph);
			}
		} else if (event.getView().getType() == InventoryType.ANVIL) {
			if (event.getSlot() == 2 && event.getInventory().getItem(2) != null && ((event.getInventory().getItem(0) != null && !event.getInventory().getItem(0).getItemMeta().getDisplayName().equals(event.getInventory().getItem(3).getItemMeta().getDisplayName())) || (event.getInventory().getItem(1) != null && !event.getInventory().getItem(1).getItemMeta().getDisplayName().equals(event.getInventory().getItem(3).getItemMeta().getDisplayName())))) {
				if (event.getWhoClicked() instanceof Player p && (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE)) {
					boolean inArena = false;
					for (String tag : p.getScoreboardTags()) {
						if (tag.contains("Ready_")) {
							inArena = true;
							break;
						}
					}
					if (inArena) {
						p.sendMessage(ChatColor.RED + "Renaming items is forbidden!");
						event.setCancelled(true);
						return;
					}
				}
			}
		}
		else if (event.getView().getTitle().toLowerCase().contains("bag")) {
            // Check for shift-click actions where the current item is being moved
            if (event.isShiftClick()) {
                ItemStack currentItem = event.getCurrentItem();
                if (currentItem != null && currentItem.getType() == Material.DECORATED_POT) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot place a bag inside another bag!");
                }
                if (currentItem != null && currentItem.getType() == Material.BARRIER) {
                    event.setCancelled(true); // Prevent movement
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot move locked slots!");
                }
            } else {
                // For normal click actions, check the item on the cursor
                ItemStack cursorItem = event.getCurrentItem();
                if (cursorItem != null && cursorItem.getType() == Material.DECORATED_POT) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot place a bag inside another bag!");
                }
                if (cursorItem != null && cursorItem.getType() == Material.BARRIER) {
                    event.setCancelled(true); // Prevent movement
                    event.getWhoClicked().sendMessage(ChatColor.RED + "You cannot move locked slots!");
                }
            }
        }
		else if (event.getView().getTitle().contains("Select Monsters") || event.getView().getTitle().contains("Select Bosses")) {
		    event.setCancelled(true); // Prevent item movement

		    if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
		        return;
		    }
		    
		    Player player = (Player) event.getWhoClicked();
		    String title = event.getView().getTitle();
		    boolean boss = title.contains("Bosses");
		    
		    // Check if the clicked item is a navigation arrow
		    if (event.getCurrentItem().getType() == Material.ARROW) {
		        String displayName = event.getCurrentItem().getItemMeta().getDisplayName();
		        int currentPage = 1;
		        // Expecting title format like "Select Monsters - Page 2"
		        if (title.contains(" - Page ")) {
		            try {
		                currentPage = Integer.parseInt(title.split(" - Page ")[1]);
		            } catch (NumberFormatException e) {
		                currentPage = 1;
		            }
		        }
		        if (displayName.equals("Next Page")) {
		            Arena.openSelectionInventory(player, boss, currentPage + 1);
		        } else if (displayName.equals("Previous Page")) {
		            Arena.openSelectionInventory(player, boss, currentPage - 1);
		        }
		        return;
		    }

		    // Process item selection as before
		    Material clickedMaterial = event.getCurrentItem().getType();
		    String name = event.getCurrentItem().getItemMeta().getDisplayName();
		    if (clickedMaterial.name().endsWith("_SPAWN_EGG")) {
		        if (boss)
		            Arena.AddBoss(player, clickedMaterial.name().substring(0, name.indexOf("_SPAWN_EGG")));
		        else
		            Arena.AddMonster(player, clickedMaterial.name().substring(0, name.indexOf("_SPAWN_EGG")));
		    } else {
		        if (boss)
		            Arena.AddBoss(player, name);
		        else
		            Arena.AddMonster(player, name);
		        player.sendMessage("Adding " + name);
		        SFX.play(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
		    }
		}
		else if (event.getView().getTitle().equals("Fabricate")) {
            event.setCancelled(true);  // Cancel the click in the menu

            if (event.getCurrentItem() == null) {
                return;
            }

            Player player = (Player) event.getWhoClicked();
            Material clickedType = event.getCurrentItem().getType();

            switch (clickedType) {
                case CRAFTING_TABLE:
                    // Open the crafting table workbench interface.
                    // Passing null uses the default location.
                    player.openWorkbench(null, true);
                    break;
                case ANVIL:
                    // Open an anvil inventory.
                    player.openInventory(Bukkit.createInventory(null, InventoryType.ANVIL, "Anvil"));
                    break;
                case ENCHANTING_TABLE:
                    // Open an enchanting table inventory.
                    Inventory enchantingInv = Bukkit.createInventory(null, InventoryType.ENCHANTING, "Enchanting Table");
                    enchantingInv.setItem(1, new ItemStack(Material.LAPIS_LAZULI, 64));
                    player.openInventory(enchantingInv);
                    break;
                case SMITHING_TABLE:
                    // Open a smithing table inventory.
                    player.openInventory(Bukkit.createInventory(null, InventoryType.SMITHING, "Smithing Table"));
                    break;
                case LOOM:
                    // Open a loom inventory.
                    player.openInventory(Bukkit.createInventory(null, InventoryType.LOOM, "Loom"));
                    break;
                case GRINDSTONE:
                    // Open a grindstone inventory.
                    player.openInventory(Bukkit.createInventory(null, InventoryType.GRINDSTONE, "Grindstone"));
                    break;
                case STONECUTTER:
                    // Open a stonecutter inventory.
                    player.openInventory(Bukkit.createInventory(null, InventoryType.STONECUTTER, "Stonecutter"));
                    break;
                case BREWING_STAND:
                    Location loc = new Location(player.getWorld(), 0, 319, 0);
                    Block block = player.getWorld().getBlockAt(loc);
                    // If the block is not already a brewing stand, set it.
                    if (block.getType() != Material.BREWING_STAND) {
                        block.setType(Material.BREWING_STAND);
                    }
                    // Get the inventory from the block state if it implements InventoryHolder.
                    BlockState state = block.getState();
                    if (state instanceof InventoryHolder holder) {
                        Inventory brewingInv = holder.getInventory();
                        player.openInventory(brewingInv);
                    }
                    break;
                case ENDER_CHEST:
                    // Open the player's ender chest.
                    player.openInventory(player.getEnderChest());
                    break;
                default:
                    break;
            }
        }
		else if (event.getView().getTitle().equals("Delete Trade")) {
			if (event.getRawSlot() >= event.getInventory().getSize()) return; // Click in player's inventory; ignore.
	        
			//boolean cancelEvent = true;
	        Player player = (Player) event.getWhoClicked();
	        int slot = event.getRawSlot();
	        
	        //player.sendMessage("Slot: " + slot);

	        // Make sure we have deletion data for this player.
	        if (!deletionVillagerMap.containsKey(player) || !deletionPageMap.containsKey(player)) {
	        	player.sendMessage("No deletion data");
	            return;
	        }
	        Villager villager = deletionVillagerMap.get(player);
	        int currentPage = deletionPageMap.get(player);
	        final int currentPageFinal = currentPage;
	        List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());
	        int totalTrades = recipes.size();
	
		     // --- Editing of Trades ---
		     // Check if the clicked slot is for Price 1 (column 1), Price 2 (column 2), or Reward (column 6)
		     if (slot % 9 == 1 || slot % 9 == 2 || slot % 9 == 6) {
		    	 Bukkit.getScheduler().runTaskLater(this, () -> {
		    		// Check if the player is placing a new item (i.e. holding something on their cursor).
			         ItemStack cursorItem = event.getInventory().getItem(slot);
			    	 //ItemStack cursorItem = event.getCurrentItem();
			         if (cursorItem != null && cursorItem.getType() != Material.AIR) {
			             int row = slot / 9; // Determine the row of the trade
			             int tradeIndex = currentPageFinal * TRADES_PER_PAGE + row;
			             if (tradeIndex < totalTrades) {
			                 MerchantRecipe recipe = recipes.get(tradeIndex);
			                 
			                 // Update the trade based on which column was clicked:
			                 if (slot % 9 == 1) {
			                     // Update Price 1 (first ingredient) if it exists
			                     if (recipe.getIngredients().size() >= 1) {
			                         // Replace ingredient at index 0 with a clone of the cursor item.
			                    	 List<ItemStack> ingredients = recipe.getIngredients();
			                         ingredients.set(0, cursorItem.clone());
			                         recipe.setIngredients(ingredients);
			                         player.sendMessage(ChatColor.GREEN + "Price 1 for trade index " + tradeIndex + " updated.");
			                     }
			                 } else if (slot % 9 == 2) {
			                     // Update Price 2 (second ingredient) if it exists
			                     if (recipe.getIngredients().size() >= 2) {
			                    	 List<ItemStack> ingredients = recipe.getIngredients();
			                         ingredients.set(1, cursorItem.clone());
			                         recipe.setIngredients(ingredients);
			                         player.sendMessage(ChatColor.GREEN + "Price 2 for trade index " + tradeIndex + " updated.");
			                     }
			                 } else if (slot % 9 == 6) {
			                     // Update the Reward item
			                	 List<ItemStack> ingredients = recipe.getIngredients();
			                	 recipe = new MerchantRecipe(cursorItem.clone(), 0, Integer.MAX_VALUE, true);
			                	 recipe.setIngredients(ingredients);
			                     //recipe.setResult(cursorItem.clone());
			                	 recipes.set(tradeIndex, recipe);
			                     player.sendMessage(ChatColor.GREEN + "Reward for trade index " + tradeIndex + " updated.");
			                 }
			                 
			                 // Save the updated list of recipes back to the villager.
			                 villager.setRecipes(recipes);
			                 // Refresh the deletion menu.
			                 bouncingInventory = true;
			                 openDeletionInventory(player, currentPageFinal, villager);
			             }
			         }
		    	 }, 5); 
			     return;
	       	}
	        
	        // --- Navigation Arrows ---
	        if (slot == 45) { // Previous Page arrow.
	            if (currentPage > 0) {
	                currentPage--;
	                deletionPageMap.put(player, currentPage);
	                bouncingInventory = true;
	                openDeletionInventory(player, currentPage, villager);
	            }

	   	     event.setCancelled(true);
	            return;
	        }
	        if (slot == 53) { // Next Page arrow.
	            if ((currentPage + 1) * TRADES_PER_PAGE < totalTrades) {
	                currentPage++;
	                deletionPageMap.put(player, currentPage);
	                bouncingInventory = true;
	                openDeletionInventory(player, currentPage, villager);
	            }

	   	     event.setCancelled(true);
	            return;
	        }

	        // --- Delete Button ---
	        // Our delete button is placed at column 4 in each row.
	        if (slot % 9 == 4) {
	   	     event.setCancelled(true);
	            int row = slot / 9; // row in the top inventory (0-based)
	            int tradeIndex = currentPage * TRADES_PER_PAGE + row;
	            if (tradeIndex < totalTrades) {
	                recipes.remove(tradeIndex);
	                villager.setRecipes(recipes);
	                player.sendMessage(ChatColor.YELLOW + "Trade at index " + tradeIndex + " deleted.");
	                // Adjust the page if necessary.
	                if (currentPage > 0 && currentPage * TRADES_PER_PAGE >= recipes.size()) {
	                    currentPage--;
	                    deletionPageMap.put(player, currentPage);
	                }
	                bouncingInventory = true;
	                openDeletionInventory(player, currentPage, villager);
	            }
	        }
		} else if (event.getInventory().getHolder() instanceof Villager) {
		     //event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            if ((event.getSlot() != 0 && event.getSlot() != 1 && event.getSlot() != 2)) {
            	ItemStack price0 = event.getInventory().getItem(0);
            	ItemStack price1 = event.getInventory().getItem(1);
            	ItemStack price2 = event.getCurrentItem();
            	Merchant merchant = (Merchant) event.getInventory().getHolder();
            	for (MerchantRecipe recipe : merchant.getRecipes()) {
            		if (recipe == null) continue;
            		boolean failed = false;
            		for (ItemStack i : recipe.getIngredients()) {
            			if (i == null) continue;
            			if (itemCompare(price0, i) || itemCompare(price1, i) || itemCompare(price2, i)) {
            				
            			} else {
            				failed = true;
            				break;
            			}
            		}
            		if (!failed) {
            			selectedRecipe.put(player, recipe);
            			//player.sendMessage("Found recipe from ingredient");
            			break;
            		}
            	}
            }
            if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.RESULT) {
       	     //event.setCancelled(true);
                Merchant merchant = (Merchant) event.getInventory().getHolder();
                
                ItemStack result = event.getInventory().getItem(event.getSlot());
                
                //player.sendMessage("Result = " + result.getItemMeta().getDisplayName());

                MerchantRecipe selected = null;
                if (selectedRecipe.containsKey(player) && selectedRecipe.get(player).getResult() != null && selectedRecipe.get(player).getResult().hasItemMeta() && selectedRecipe.get(player).getResult().getItemMeta().getDisplayName().equals(result.getItemMeta().getDisplayName())) {
                	selected = selectedRecipe.get(player);
                	//player.sendMessage("Received from SelectedRecipe");
                } else {
                	for (MerchantRecipe recipe : merchant.getRecipes()) {
                    	if (recipe == null || result == null) continue;
                    	if (recipe.getResult().getType() == result.getType()) {
                    		if (result.hasItemMeta() == recipe.getResult().hasItemMeta()) {
                    			if ((result.hasItemMeta() && result.getItemMeta().getDisplayName() == recipe.getResult().getItemMeta().getDisplayName()) || !result.hasItemMeta()) {
                    				selected = recipe;
                    				break;
                    			}
                    		}
                    	}
                    }
                }
                
                if (selected != null) {
                	//player.sendMessage("Handling trade");
                	ItemStack[] itemsGiven = selected.getIngredients().toArray(new ItemStack[0]);
                    handleTrade(player, itemsGiven, selected.getResult());
                } else {
                	//player.sendMessage("No trade to handle");
                }
            }
        }
    }
	
	@EventHandler
	private void onTradeSelect(TradeSelectEvent event) {
		if (event.getWhoClicked() instanceof Player player) {
			selectedRecipe.put(player, event.getMerchant().getRecipe(event.getIndex()));
			//player.sendMessage("Selected recipe: " + event.getMerchant().getRecipe(event.getIndex()).getResult().getItemMeta().getDisplayName());
		}
	}
	
	private void handleTrade(Player player, ItemStack[] itemsGiven, ItemStack itemSold) {
		if (Arena.runningArenas == null || Arena.runningArenas.size() == 0) return;
        String message = ChatColor.AQUA + "Traded";
        for (ItemStack item : itemsGiven) {
            if (item != null && item.getType() != Material.AIR) {
            	String itemName = item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name().substring(0, 1).toUpperCase() + item.getType().name().substring(1).toLowerCase().replace("_", " ");
                message += (" - " + item.getAmount() + "x " + itemName);
            }
        }
        String itemName = itemSold.hasItemMeta() && itemSold.getItemMeta().hasDisplayName() ? itemSold.getItemMeta().getDisplayName() : itemSold.getType().name().substring(0, 1).toUpperCase() + itemSold.getType().name().substring(1).toLowerCase().replace("_", " ");
        message += (" for " + itemSold.getAmount() + "x " + itemName);
        player.sendMessage(message);
        
        // Custom logic goes here
        
        int countNull = 0;
        int goldCount = 0;
        boolean isGoldConversion = false;
        for (ItemStack item : itemsGiven) {
        	if (item == null)
        		countNull++;
        	else if (isGold(item)) {
        		goldCount+=getValue(item);
        	}
        	else if (isChestplate(item)) {
        		Arena.bankChestplates.add(item);
        	} else if (isLeggings(item)) {
        		Arena.bankLeggings.add(item);
        	} else if (isBoots(item)) {
        		Arena.bankBoots.add(item);
        	} else if (isMeleeWeapon(item)) {
        		Arena.bankMeleeWeapons.add(item);
        	} else if (isRangedWeapon(item)) {
        		Arena.bankRangedWeapons.add(item);
        	}
        }
        if (goldCount > 0 && isGold(itemSold))
        	isGoldConversion = true;
        else {
        	Arena.bankGold += goldCount;
        }
    }
	
	public boolean isChestplate(ItemStack item) {
		if (item == null) return false;
		return (item.getType().name().toLowerCase().contains("chestplate"));
	}
	
	public boolean isLeggings(ItemStack item) {
		if (item == null) return false;
		return (item.getType().name().toLowerCase().contains("leggings"));
	}
	
	public boolean isBoots(ItemStack item) {
		if (item == null) return false;
		return item.getType().name().toLowerCase().contains("boots");
	}
	
	public boolean isGold(ItemStack item) {
		if (item == null) return false;
		return item.getType() == Material.GOLD_NUGGET || item.getType() == Material.GOLD_INGOT || item.getType() == Material.GOLD_BLOCK;
	}
	
	public int getValue(ItemStack item) {
		if (item == null) return 0;
		if (item.getType() == Material.GOLD_NUGGET)
			return item.getAmount();
		else if (item.getType() == Material.GOLD_INGOT)
			return 9 * item.getAmount();
		else if (item.getType() == Material.GOLD_BLOCK)
			return 9*9*item.getAmount();
		return 0;
	}
	
	public boolean isMeleeWeapon(ItemStack item) {
		if (item.getType().name().toLowerCase().contains("sword") || item.getType().name().toLowerCase().contains("_axe")) {
			return true;
		}
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			String name = item.getItemMeta().getDisplayName().toLowerCase();
			if (name.contains("sword") || name.contains("halberd") || name.contains("shiv") || name.contains("shank") || name.contains("dagger")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRangedWeapon(ItemStack item) {
		if (item.getType().name().toLowerCase().contains("bow")) {
			return true;
		}
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
			String name = item.getItemMeta().getDisplayName().toLowerCase();
			if (name.contains("wand") || name.contains("staff") || name.contains("rifle") || name.contains("musket") || name.contains("gun") || name.contains("dart") || name.contains("fireball") || name.contains("wither skull") || name.contains("witherskull")) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onDisable() {
		System.out.println("Shutting down Fancy Arenas...");
		try {
			for (LivingEntity le : sentryMode.keySet()) {
				sentryMode.get(le).remove();
			}
			if (FancyArena.instance.repulsorFly.size() > 0) {
				LivingEntity[] keys = (LivingEntity[]) FancyArena.instance.repulsorFly.toArray();
				for (LivingEntity le : keys) {
					FancyArena.instance.disableRepulsorFly(le);
				}
				FancyArena.instance.repulsorFly.clear();
			}
		} catch (Exception e) {
			
		}
		Arena.save(Arena.filepath);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		//System.out.println("Player joined");
		//event.getPlayer().sendMessage( "Welcome to the Fancy Yeet Squad Minecraft Server!\nEnjoy your stay!" );
	}

	@EventHandler
	public void inventoryOpen(InventoryOpenEvent event) {
		Inventory inventory = event.getInventory();
		//event.getPlayer().sendMessage("Opened Inventory");
		if (inventory.getLocation() != null && (inventory.getLocation().getBlock().getState().getType() == Material.CHEST || inventory.getLocation().getBlock().getState().getType() == Material.BARREL)) {

			//event.getPlayer().sendMessage("Chest or barrel");
			
			Location loc = inventory.getLocation();
			if (Arena.runningArenas.size() > 0) {

				//event.getPlayer().sendMessage("Arena running");
				
				for (String arenaName : Arena.runningArenas) {
					int lvlNum = Arena.currentLevel.get(arenaName);
					//FancyLevel[] lvls = {Arena.levels.get(arenaName).get(lvlNum), Arena.levels.get(arenaName).get(lvlNum-1)};
					
					
					
					for (int i = lvlNum; i >= 0; i--) {
						FancyLevel lvl = Arena.levels.get(arenaName).get(i);
						//event.getPlayer().sendMessage("Checking level " + lvl.getLevelName());
						if (Arena.within(loc, lvl.getMinPosLoc(), lvl.getMaxPosLoc())) {
							//event.getPlayer().sendMessage("Container within " + lvl.getLevelName());
							if (Arena.lootedChests.containsKey(arenaName) && Arena.lootedChests.get(arenaName).contains(FancyLevel.locToString(loc.getBlock().getLocation()))) {
								//if (Arena.lootedChests.get(arenaName).contains(FancyLevel.locToString(loc.getBlock().getLocation()))) {
									// Already looted
									//event.getPlayer().sendMessage("Container already looted");
									return;
								//}
							} else {
								if (!Arena.lootedChests.containsKey(arenaName))
									Arena.lootedChests.put(arenaName, new ArrayList<String>());
								//event.getPlayer().sendMessage("Looting container");
							
								int lootMultiplier = 1;
								if (scavengersKit.contains(event.getPlayer().getName())) {
									lootMultiplier = 2;
								}
								
								Inventory loot = lvl.getRandomChestLoot();
								Inventory loot2 = lvl.getRandomChestLoot();
								
								inventory.clear();
								
								for (int j = 0; j < loot.getSize(); j++) {
						            ItemStack item = loot.getItem(j);
						            ItemStack item2 = loot2.getItem(j);
						            if (item != null) {
						                inventory.setItem(j, item.clone());
						            } else if (item2 != null){
						            	if (Math.random() * lootMultiplier > 1) {
						            		inventory.setItem(j, item2.clone());
						            	}
						            }
						        }
								
								Arena.lootedChests.get(arenaName).add(FancyLevel.locToString(loc.getBlock().getLocation()));
								//event.getPlayer().sendMessage("Spawning loot");
								return;
							} 
						}else {
							//event.getPlayer().sendMessage("Container not found");
						}
					}
					
				}
			}
		} 
	}
	
	public static ItemStack[] tempPriceItems = null;
	
	@EventHandler
	public void onEntityTransform(EntityTransformEvent event) {
		if (event.getEntity().getScoreboardTags().contains("invulnerable") || event.getTransformedEntity().getScoreboardTags().contains("invulnerable")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent event) {
		// Check if the inventory closed is a chest
        Inventory inventory = event.getInventory();
        
        //event.getPlayer().sendMessage("Closing Inventory " + event.getView().getTitle());
        
        if (inventory.getHolder() instanceof StorageMinecart minecart && minecart.getScoreboardTags().contains("FancyBossLoot")) {
        	boolean empty = true;
        	for (ItemStack item : inventory.getContents()) {
        		if (item != null && item.getType() != Material.AIR) {
        			empty = false;
        			break;
        		}
        	}
        	if (empty)
        		minecart.remove();
        }
        else if (event.getView().getTitle().equals("Select Price") && tempPriceItems == null) {
        	
        	tempPriceItems = inventory.getContents();
        	boolean hasItems = false;
        	if (tempPriceItems.length == 0) {
        		
        	} else {
        		for (int i = 0; i < tempPriceItems.length; i++) {
        			if (tempPriceItems[i] != null) {
        				hasItems = true;
        				break;
        			}
        		}
        	}
        	if (!hasItems) {
        		event.getPlayer().closeInventory();
        		event.getPlayer().sendMessage(ChatColor.YELLOW + "No items selected, cancelling process");
        		return;
        	}

        	event.getPlayer().closeInventory();
        	Inventory fancyInv = Bukkit.createInventory(null, InventoryType.DISPENSER, "Select Reward");
        	//event.getPlayer().openInventory(fancyInv);
        	event.getPlayer().sendMessage(ChatColor.YELLOW + "Set price");
        	delayedInventory.put((Player) event.getPlayer(), fancyInv);
        } else if (event.getView().getTitle().equals("Select Reward") && tempPriceItems != null) {
        	
        	event.getPlayer().sendMessage(ChatColor.YELLOW + "Attempting to modify shop");
        	
        	Villager villager = null;
        	for (Entity e : event.getPlayer().getNearbyEntities(1, 1, 1)) {
        		if (e.getType() == EntityType.VILLAGER && e.getScoreboardTags().contains("FancyShop")) {
        			villager = (Villager)e;
        		}
        	}
        	
        	List<MerchantRecipe> recipes =  Lists.newArrayList(villager.getRecipes());
        	
        	ItemStack reward = inventory.getContents()[0];
        	for (ItemStack item : inventory.getContents()) {
        		if (item != null) {
        			reward = item;
        			break;
        		}
        	}
        	
        	MerchantRecipe recipe = new MerchantRecipe(reward, 0, Integer.MAX_VALUE, true);
        	List<ItemStack> ingredients = new ArrayList<ItemStack>();
        	for (ItemStack item : tempPriceItems) {
        		if (item != null) {
        			ingredients.add(item);
        		}
        	}
        	recipe.setIngredients(ingredients);
        	recipes.add(recipe);
        	
        	villager.setRecipes(recipes);
			//villager.setAI(true);
			tempPriceItems = null;
        	event.getPlayer().sendMessage(ChatColor.YELLOW + "Shop " + villager.getCustomName() + ChatColor.YELLOW + " successfully modified");
        	
        } else if (inventory.getLocation() != null && inventory.getLocation().getBlock().getType() == Material.DROPPER) {
        	Location dropperLocation = inventory.getLocation();
        	// Look for an armor stand above the dispenser
            Location armorStandLocation = dropperLocation.clone().add(0.5, 2.0, 0.5); // 1 block above dispenser
            ArmorStand armorStand = null;
            for (Entity entity : dropperLocation.getWorld().getNearbyEntities(armorStandLocation, 0.5, 0.5, 0.5)) {
                if (entity instanceof ArmorStand && entity.getScoreboardTags().contains("FancyMobTemplate")) {
                    armorStand = (ArmorStand) entity;
                    break;
                }
            }

            if (armorStand == null) {
                return; // No valid armor stand found
            }
            
            Dropper dropper = (Dropper) dropperLocation.getBlock().getState();
            dropper.setCustomName("Mob Loot");
            dropper.update();
            
            event.getPlayer().sendMessage(armorStand.getCustomName() + "'s loot successfully modified.");
        } 
        else if (event.getView().getTitle().equals("Delete Trade")) {
        	if (!bouncingInventory) {
	            Player player = (Player) event.getPlayer();
	            deletionVillagerMap.remove(player);
	            deletionPageMap.remove(player);
        	}
        }
        if (inventory.getLocation() != null && inventory.getLocation().getBlock().getType() == Material.CHEST) {

            // Get the chest's location
            Location chestLocation = inventory.getLocation();
            
        	// Look for an armor stand above the dispenser
            Location armorStandLocation = chestLocation.clone().add(0.5, 1.0, 0.5); // 1 block above dispenser
            ArmorStand armorStand = null;
            for (Entity entity : chestLocation.getWorld().getNearbyEntities(armorStandLocation, 0.5, 0.5, 0.5)) {
                if (entity instanceof ArmorStand && entity.getScoreboardTags().contains("FancyMobTemplate")) {
                    armorStand = (ArmorStand) entity;
                    break;
                }
            }

            if (armorStand == null) {
                return; // No valid armor stand found
            }
            

            // Check for a nametag itemstack in the chest
            ItemStack nameTag = null;
            for (ItemStack item : inventory.getContents()) {
                if (item != null && item.getType() == Material.NAME_TAG) {
                    nameTag = item;
                    break;
                }
            }

            if (nameTag == null || !nameTag.hasItemMeta()) {
                return; // No valid nametag found
            }
            

            // Get the custom name from the nametag
            ItemMeta meta = nameTag.getItemMeta();
            String customName = meta != null && meta.hasDisplayName() ? meta.getDisplayName() : null;

            if (customName == null) {
                return; // Nametag has no custom name
            }
            
            Chest chest = (Chest) chestLocation.getBlock().getState();
            chest.setCustomName("Mob Attributes");
            chest.update();

			armorStand.setArms(true);
			armorStand.setBasePlate(false);
			armorStand.setGravity(false);

            // Set the name of the armor stand and make it visible
            armorStand.setCustomName(customName);
            armorStand.setCustomNameVisible(true);
            
            /*
            ItemStack weapon = armorStand.getEquipment().getItemInMainHand();
            if (weapon != null) {
                boolean hasWeapon = false;
	            for (ItemStack item : inventory.getContents()) {
	                if (item != null && item.getType() == weapon.getType() && item.getItemMeta().getDisplayName() == weapon.getItemMeta().getDisplayName()) {
	                    hasWeapon = true;
	                    break;
	                }
	            }
	            if (!hasWeapon) {
	            	inventory.addItem(weapon);
	            }
            } */

            //Bukkit.getLogger().info("Armor stand name updated to: " + customName);
            event.getPlayer().sendMessage(customName + " successfully modified.");
        }
	}
	
	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		if (event.getVehicle().getType() == EntityType.CHEST_MINECART && (event.getVehicle().getScoreboardTags().contains("FancyBossLoot") || Arena.runningArenas.size() > 0)) {
			if (event.getAttacker() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
				
			} else {
				event.setCancelled(true);
			}
			return;
		}
	}
	
	List<String> brutalCrit = new ArrayList<String>();
	List<String> wallRun = new ArrayList<String>();
	List<String> wallJump = new ArrayList<String>();
	List<String> doubleJump = new ArrayList<String>();
	List<String> scavengersKit = new ArrayList<String>();
	List<String> adrenalineRush = new ArrayList<String>();
	List<String> noPainNoGain = new ArrayList<String>();

	public static boolean supremeDragonBreath = false;
	
	static final PotionEffectType[] elementalEffectBan = { PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS };
	static final PotionEffectType[] primordialEffectBan = { PotionEffectType.POISON, PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS, PotionEffectType.INSTANT_DAMAGE, PotionEffectType.MINING_FATIGUE, PotionEffectType.WITHER, PotionEffectType.HUNGER };
	
	
	@EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (event.getDamager() instanceof Player p && p.getFallDistance() >= 1.5f && heavyFists.contains(p.getName()) && p.getEquipment().getItemInMainHand().getType() == Material.AIR) {
			event.setDamage(p.getFallDistance() * 1.5f);
			p.setVelocity(p.getVelocity().setY(0.3f));
			p.setFallDistance(0);
			SFX.play(event.getEntity().getLocation(), Sound.ITEM_MACE_SMASH_GROUND, 1, 1.8f);
            p.getWorld().spawnParticle(Particle.BLOCK, event.getEntity().getLocation().add(new Vector(0, 1, 0)), 30, 0.03, 0.06, 0.03, Material.REDSTONE_BLOCK.createBlockData());
		}
		if (event.getDamager() instanceof LivingEntity le && le.getFallDistance() >= 1.5f && isWindburstMace(le.getEquipment().getItemInMainHand())) {
			//Bukkit.broadcastMessage("Windbursting");
            windburst(event.getEntity().getLocation().getBlock(), 6);
        }
		
		if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("PrimordialWard")) {
        	//event.setCancelled(true);
        } else if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("ElementalWard")) {
        	//event.setDamage(event.getDamage()/2);
        }
		
		if (event.getDamager() instanceof Player p && brutalCrit.contains(p.getName())) {
			//p.sendMessage("Checking for brutal crit");
			 boolean flag = p.getFallDistance() > 0.0F && !p.isOnGround() && !p.isClimbing() && !p.isSwimming() && !p.hasPotionEffect(PotionEffectType.BLINDNESS) && !p.isInsideVehicle() && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK;

             if (flag && event.getDamage() > 0.0F) {
                 SFX.play(event.getEntity().getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1, 0.8f);
                 p.getWorld().spawnParticle(Particle.BLOCK, event.getEntity().getLocation().add(new Vector(0, 1, 0)), 30, 0.03, 0.06, 0.03, Material.REDSTONE_BLOCK.createBlockData());
                 event.setDamage(event.getDamage()*1.5f);
             }
			
		}
		
		if (event.getEntity().getType() == EntityType.CHEST_MINECART && (event.getEntity().getScoreboardTags().contains("FancyBossLoot") || Arena.runningArenas.size() > 0)) {
			if (event.getDamager() instanceof Player player && player.getGameMode() == GameMode.CREATIVE) {
				
			} else {
				event.setCancelled(true);
			}
			return;
		}
		// Check if the damage is caused by a fireball
        if (event.getDamager() instanceof Fireball) {
            Fireball fireball = (Fireball) event.getDamager();

            // Check if the fireball's shooter is a player
            if (fireball.getShooter() instanceof Player) {
                Player shooter = (Player) fireball.getShooter();
                
                if (!pvp && event.getEntity() instanceof Player player) {
                	if (Arena.runningArenas.size() > 0) {
						for (String tag : player.getScoreboardTags()) {
							if (tag.contains("Ready_")) {
			                	event.setCancelled(true);
			                	break;
							}
						}
					}
                }

                // If the damaged entity is the shooter and they're immune, cancel the damage
                if (event.getEntity().getUniqueId().equals(shooter.getUniqueId())) {
                    event.setDamage(event.getDamage()/2);
                	//event.setCancelled(true);
                    //immunePlayers.remove(shooter.getUniqueId()); // Remove immunity after one explosion
                }
            }
        }
        
		Entity damager = event.getDamager();
		if (damager instanceof Projectile a) {
			if (a.getShooter() instanceof Entity e) {
				damager = e;
			}
			//Bukkit.broadcastMessage("Arrow shooter: " + damager.getName());
		}
		if (!pvp && Arena.runningArenas.size() > 0 && damager instanceof Player p1 && event.getEntity() instanceof Player p2) {
			boolean arenaPlayer = false;
			if (p1 == p2) {
				return;
			} else {
				for (String s : p1.getScoreboardTags()) {
					if (s.contains("Ready_")) {
						arenaPlayer = true;
						break;
					}
				}
				if (!arenaPlayer) {
					for (String s : p2.getScoreboardTags()) {
						if (s.contains("Ready_")) {
							arenaPlayer = true;
							break;
						}
					}
				}
			}
			if (arenaPlayer)
				event.setCancelled(true);
			return;
		} else if (!monsterPvp && Arena.runningArenas.size() > 0 && damager.getScoreboardTags().contains("ArenaMob") && event.getEntity().getScoreboardTags().contains("ArenaMob")) {
			event.setCancelled(true);
			return;
		}
		
		if (damager instanceof LivingEntity dmgr && event.getEntity() instanceof LivingEntity victim) {
			if (sentryMode.containsKey(dmgr)) {
				if (!sentryTarget.containsKey(dmgr))
					sentryTarget.put(dmgr, new ArrayList<LivingEntity>());
				if (!sentryTarget.get(dmgr).contains(victim))
					sentryTarget.get(dmgr).add(victim);
			}
			if (sentryMode.containsKey(victim)) {
				if (!sentryTarget.containsKey(victim))
					sentryTarget.put(victim, new ArrayList<LivingEntity>());
				if (!sentryTarget.get(victim).contains(dmgr))
					sentryTarget.get(victim).add(dmgr);
			}
		}
		
		// Set head item of armorstand
		 if (event.getEntity() instanceof ArmorStand || event.getEntity() instanceof ItemFrame || event.getEntity() instanceof GlowItemFrame) {
	        	if (event.getDamager() instanceof Player player) {
		        	if (player.getGameMode() == GameMode.CREATIVE && player.getEquipment().getItemInMainHand() != null && player.getEquipment().getItemInMainHand().getType() != Material.AIR) {
			            event.setCancelled(true);
			            ArmorStand stand = (ArmorStand) event.getEntity();
			            if (player.isSneaking()) {
				            stand.getEquipment().setItemInOffHand(player.getEquipment().getItemInMainHand());
			            } else {
				            stand.getEquipment().setHelmet(player.getEquipment().getItemInMainHand());
			            }
		        	}
	        	} 
	        }
    }
	
	public boolean isFriendlyFire(LivingEntity le1, LivingEntity le2) {
		if (le1 == le2)
			return false;
		if (le1 instanceof Player p1 && le2 instanceof Player p2 && pvp) {
			boolean arenaPlayer = false;
			for (String s : p1.getScoreboardTags()) {
				if (s.contains("Ready_")) {
					arenaPlayer = true;
					break;
				}
			}
			if (!arenaPlayer) {
				for (String s : p2.getScoreboardTags()) {
					if (s.contains("Ready_")) {
						arenaPlayer = true;
						break;
					}
				}
			}
			return arenaPlayer;
		}
		else if (le1.getScoreboardTags().contains("ArenaMob") && le2.getScoreboardTags().contains("ArenaMob")) {
			return true;
		}
		return false;
	}
	
	public void ConvergentFuture(Player player) {
		prepare_teleport(player);
		//if (player.getPassengers().size() > 0)
		//	player.removePassenger(player.getPassengers().get(0));
		playerStates.get(player)[1].activatePlayerState(player);
		final Player p = player;
		player.sendMessage(ChatColor.LIGHT_PURPLE + "You performed a convergent future");
		Bukkit.getScheduler().runTaskLater(this, () -> {
			double maxHP = p.getHealth();
			PlayerState chosenState = null;
			for (PlayerState state : playerStates.get(p)) {
				if (state.hp > maxHP) {
					maxHP = state.hp;
					chosenState = state;
				}
			}
			if (chosenState != null)
				chosenState.activatePlayerState(p);
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 2));
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 2));
        }, 3); // Delay of 0.15 seconds
	}
	
	ArrayList<Player> wallRunning = new ArrayList<>();
	ArrayList<String> heavyFists = new ArrayList<>();
	
	@EventHandler
	public void onGameModeChange(PlayerGameModeChangeEvent event) {
		if ((event.getNewGameMode() == GameMode.SURVIVAL || event.getNewGameMode() == GameMode.ADVENTURE) && (repulsorFly.contains(event.getPlayer()) || doubleJump.contains(event.getPlayer().getName()))) {
			event.getPlayer().setAllowFlight(true);
		}
	}
	
	ArrayList<Player> canDoubleJump = new ArrayList<>();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
	    if (event.getPlayer().getScoreboardTags().contains("Polymorph")) {
	    	if (!event.getPlayer().getScoreboardTags().contains("Moving")) {
	    		event.getPlayer().addScoreboardTag("Moving");
	    	}
	    }
	    if (doubleJump.contains(event.getPlayer().getName()) && !event.getPlayer().getAllowFlight() && event.getPlayer().isOnGround()) {
	    	Bukkit.getScheduler().runTaskLater(this, () -> {
		    	canDoubleJump.add(event.getPlayer());
		    	event.getPlayer().setAllowFlight(true);
		    	event.getPlayer().sendMessage("Double Jump Refreshed");
	        }, 2);
	    } 
	    if (wallJump.contains(event.getPlayer().getName())) {
	    	boolean doubleJumpEnabled = canDoubleJump.contains(event.getPlayer());
	    	if (!doubleJumpEnabled) {
		    	boolean nextToWall = isNextToWall(event.getPlayer(), false);
		    	event.getPlayer().setAllowFlight(nextToWall);
		    	if (nextToWall)
		    		event.getPlayer().sendMessage("Wall jump refreshed");
	    	}
	    }
	    if (wallRun.contains(event.getPlayer().getName())) {
	    	
	    	boolean nextToWall = isNextToWall(event.getPlayer(), false);
	    	if (!nextToWall || event.getPlayer().isOnGround() || !event.getPlayer().isSprinting()) {
	            wallRunning.remove(event.getPlayer());
	            return;
	        }

	        if (nextToWall && event.getPlayer().getVelocity().getY() < 0.1) {
	            if (!wallRunning.contains(event.getPlayer())) {
	                wallRunning.add(event.getPlayer());
	                event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.5).setY(0.3));
	                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_SLIME_BLOCK_STEP, 1f, 1.2f);
	            }
	        }
	    }
	    
	}
	
	private boolean isNextToWall(Player player, boolean useParticles) {
        Location loc = player.getLocation().add(0, 1, 0);
        World world = loc.getWorld();
        if (world == null) return false;

        // Check 4 cardinal directions for wall blocks
        BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

        for (BlockFace face : directions) {
            Block block = loc.getBlock().getRelative(face);
            if (!block.isPassable()) {
            	if (useParticles) world.spawnParticle(Particle.GUST, loc.clone().add(face.getDirection()), 1);
                return true;
            }
        }

        return false;
    }
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.SUFFOCATION && event.getEntity().getScoreboardTags().contains("NoSuffocation")) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity().getScoreboardTags().contains("RelentlessRage") && event.getEntity() instanceof LivingEntity le && le.hasPotionEffect(PotionEffectType.RESISTANCE) && le.hasPotionEffect(PotionEffectType.STRENGTH)) {
			//if (le instanceof Player p)
			//	p.sendMessage("Took damage while raging");
			if (le.getHealth() - event.getFinalDamage() < 0.1 && Math.random() > 0.1) {
				//if (le instanceof Player p)
				//	p.sendMessage("Cancelled Damage");
				event.setDamage(le.getHealth()-2);
				//for (Player p : Bukkit.getOnlinePlayers()) {
					SFX.play(le.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
				//}
				le.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, le.getLocation().add(new Vector(0, 1, 0)), 20);
				return;
			}
		} else if (event.getEntity().getScoreboardTags().contains("ConvergentFuture") && event.getEntity() instanceof Player p) {
			if (p.getHealth() - event.getFinalDamage() < 0.1 && Math.random() > 0.05 && !p.hasCooldown(Material.CLOCK)) {
				event.setDamage(0);
				//for (Player player : Bukkit.getOnlinePlayers()) {
					SFX.play(p.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
				//}
				p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation().add(new Vector(0, 1, 0)), 20);
				ConvergentFuture(p);
				p.setCooldown(Material.CLOCK, 15*20);
			}
		}
		try {
			if (event.getEntity() instanceof LivingEntity le && le.getHealth() - event.getFinalDamage() < 0.1) {
				if (le.getScoreboardTags().contains("CustomDeathSound") && event.getEntity().hasMetadata("deathSound")) {
					Location loc = le.getLocation();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound " + le.getMetadata("deathSound").get(0).value() + " hostile @a ~ ~ ~ 2 1");
				}
			}
			else if (event.getEntity().getScoreboardTags().contains("CustomHurtSound") && event.getEntity().hasMetadata("hurtSound")) {
				Location loc = event.getEntity().getLocation();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound " + event.getEntity().getMetadata("hurtSound").get(0).value() + " hostile @a ~ ~ ~ 2 1");
			}
		} catch (Exception ex) {
			
		}
		if (event.getEntity().getScoreboardTags().contains("GuidingBolt")) {
			if (((LivingEntity)event.getEntity()).hasPotionEffect(PotionEffectType.GLOWING))
			{
				event.setDamage(event.getDamage() * 1.5);
			} else
				event.getEntity().removeScoreboardTag("GuidingBolt");
		}
		if (event.getEntity().getScoreboardTags().contains("FancyBossLoot")) {
			event.setCancelled(true);
			return;
		}
		if (event.getEntity().getScoreboardTags().contains("FancyShop")) {
			event.setCancelled(true);
			return;
		}
		/*if (event.getEntity().getScoreboardTags().contains("Giant")) {
			event.setCancelled(true);
		}*/
		if (event.getEntity().getScoreboardTags().contains("FancyBoss")) {
        	for (String arenaName : Arena.runningArenas) {
        		if (event.getEntity().getScoreboardTags().contains("Arena_" + arenaName)) {
        			int minBossHealth = Arena.getCurrentLevel(arenaName).getMinBossHealth();
        			if (minBossHealth > 0) {
	        			if (((LivingEntity)event.getEntity()).getHealth() - event.getFinalDamage() < minBossHealth) {
	        				event.setCancelled(true);
	        				((LivingEntity)event.getEntity()).setHealth(Math.max(((LivingEntity)event.getEntity()).getHealth(), minBossHealth));
	        				Arena.levelDone.put(arenaName, true);
	        				event.getEntity().getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, event.getEntity().getLocation(), 10);
	        				event.getEntity().remove();
	        			}
        			}
        		}
        	}
        }
		/*
		if (event.getEntity().getType() == EntityType.GIANT) {
			Giant giant = (Giant) event.getEntity();
			LivingEntity puppet = null;
			for (Entity e : giant.getLocation().getWorld().getNearbyEntities(giant.getLocation(), 5, 5, 5)) {
				if (e instanceof LivingEntity le && e.getScoreboardTags().contains("Giant") && giants.get(e).x == giant) {
					puppet = le;
				}
			}
			if (puppet != null) {
				puppet.setHealth(giant.getHealth());
			}
		}
		*/
	}
	
	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event) {
		if (event.getNewEffect() != null) {
			if (event.getEntity().getScoreboardTags().contains("ElementalWard")) {
				for (PotionEffectType type : elementalEffectBan)
				{
					if (type == event.getNewEffect().getType()) {
						event.setCancelled(true);
						break;
					}
				}
			}
			else if (event.getEntity().getScoreboardTags().contains("PrimordialWard")) {
				for (PotionEffectType type : primordialEffectBan)
				{
					if (type == event.getNewEffect().getType()) {
						event.setCancelled(true);
						break;
					}
				}
			}
		
			if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("FancyMob") && (le.getType() == EntityType.ZOMBIE || le.getType() == EntityType.SKELETON || le.getType() == EntityType.WITHER_SKELETON || le.getType() == EntityType.GIANT)) {
				//if (event.getNewEffect() != null) {
					if (event.getNewEffect().getType() == PotionEffectType.INSTANT_DAMAGE) {
						le.damage((event.getNewEffect().getAmplifier()+1)*6, le.getLastDamageCause() != null ? le.getLastDamageCause().getEntity() : null);
						event.setCancelled(true);
					} else if (event.getNewEffect().getType() == PotionEffectType.INSTANT_HEALTH) {
						heal(le, (event.getNewEffect().getAmplifier()+1)*4);
						event.setCancelled(true);
					} else if (event.getNewEffect().getType() == PotionEffectType.POISON) {
						Location loc = le.getLocation();
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..16] run effect give @s minecraft:wither " + event.getNewEffect().getDuration()/20 + " " + event.getNewEffect().getAmplifier());
						event.setCancelled(true);
					}
					
					
						
				//}
			}
		}
	}
	
	public static void heal(LivingEntity entity, double healAmount) {
		entity.setHealth(Math.min(entity.getHealth() + healAmount, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
	    Block placedBlock = event.getBlockPlaced();
	    Player player = event.getPlayer();

	    boolean cancelEvent = rightClick(player);
	    if (cancelEvent)
	    	event.setCancelled(true);
	    
	    // Player placed a block
	    //player.sendMessage("You placed a " + placedBlock.getType().toString());
	}
	
	public Entity getEntityPlayerIsLookingAt(Player player, double range) {
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();
        Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(eyeLocation, range, range, range);

        for (Entity entity : nearbyEntities) {
            if (entity.equals(player) || entity.getType() != EntityType.ARMOR_STAND) continue; // Ignore the player themselves
            
            // Check if the entity is in the player's line of sight
            if (isLookingAtEntity(eyeLocation, direction, entity, range)) {
                return entity;
            }
        }

        return null; // No entity found
    }
	
	private static boolean isLookingAtEntity(Location eyeLocation, Vector direction, Entity entity, double range) {
        // Entity bounding box for more precise checks
        Location entityLocation = entity.getLocation();
        Vector entityPosition = entityLocation.toVector();

        // Project entity position onto the player's line of sight
        Vector difference = entityPosition.clone().subtract(eyeLocation.toVector());
        double dotProduct = difference.dot(direction);

        // Check if the entity is in front of the player
        if (dotProduct <= 0) return false;

        // Check if within the specified range
        if (difference.lengthSquared() > range * range) return false;

        // Check if the direction to the entity matches the player's line of sight
        double distanceToLine = difference.clone().subtract(direction.clone().multiply(dotProduct)).length();
        return distanceToLine < 1.5; // Adjust threshold for precision
    }
	
	public boolean useItem(LivingEntity entity, ItemStack item) {
		return useItem(entity, item, false, 0);
	}
	
	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event) {
		if (event.getItem().getType().toString().toLowerCase().contains("potion")) {
			event.getPlayer().setCooldown(Material.GLASS_BOTTLE, 8);
		} else if (event.getItem().getType() == Material.BEETROOT && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getDisplayName().contains("Demon Heart")) {
			
			ItemStack antimatterShell = new ItemStack(Material.NETHER_WART);
			ItemMeta meta = antimatterShell.getItemMeta();
			meta.setDisplayName(ChatColor.DARK_RED + "Antimatter Shell");
			meta.setEnchantmentGlintOverride(true);
			antimatterShell.setItemMeta(meta);
			antimatterShell.setAmount(20 + (int)(Math.random() * 40));
			event.getPlayer().getInventory().addItem(antimatterShell);
			//for (Player p : Bukkit.getOnlinePlayers()) {
				SFX.play(event.getPlayer().getLocation(), Sound.ENTITY_WITHER_SPAWN, 0.8f, 0.6f);
			//}
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 2));
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 120, 2));
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 140, 2));
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 140, 2));
		}
	}
	
	public static int getBagSize(String itemName) {
		int size = 8;
		if (itemName.contains("Holding")) {
			size = 54;
		} else if (itemName.contains("Tiny")) {
			size = 4;
		} else if (itemName.contains("Small")) {
			size = 8;
		} else if (itemName.contains("Medium")) {
			size = 12;
		} else if (itemName.contains("Extra Large")) {
			size = 27;
		} else if (itemName.contains("Large")) {
			size = 18;
		}
		return size;
	}
	
	public static Inventory getCustomInventory(String name, int size, int correctedSize) {
		// Round up to the nearest multiple of 9
        int correctedBagSize = ((size + 8) / 9) * 9;
        //player.sendMessage(ChatColor.YELLOW + "Creating new inventory for bag, size=" + correctedBagSize + " name=" + itemName);

        // Create the inventory
        Inventory inv = Bukkit.createInventory(null, correctedBagSize, name);

        // Fill the remaining slots with barriers
        for (int i = size; i < correctedBagSize; i++) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta meta = barrier.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Locked Slot");
            barrier.setItemMeta(meta);
            inv.setItem(i, barrier);
        }
        return inv;
	}
	
	public ItemStack getHolyAvenger() {
		ItemStack newitem = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta meta = newitem.getItemMeta();
		meta.setCustomModelData(10000100);
		ModItem.changeAttribute(meta, 11, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
		ModItem.changeAttribute(meta, -3.1f, Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
		meta.addEnchant(Enchantment.SHARPNESS, 1, true);
		meta.addEnchant(Enchantment.SMITE, 6, true);
		meta.setDisplayName(ChatColor.YELLOW + "Holy Avenger");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("Right click to throw,");
		lore.add("Press Q to create aura");
		meta.setLore(lore);
		meta.setUnbreakable(true);
		newitem.setItemMeta(meta);
		return newitem;
	}

	public ItemStack getNapoleonsVengeance() {
		ItemStack newitem = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta meta = newitem.getItemMeta();
		meta.setCustomModelData(10000100);
		ModItem.changeAttribute(meta, 14, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
		ModItem.changeAttribute(meta, -2.7f, Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
		meta.addEnchant(Enchantment.SWEEPING_EDGE, 2, true);
		meta.addEnchant(Enchantment.SHARPNESS, 4, true);
		meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
		meta.setDisplayName(ChatColor.DARK_PURPLE + "Napoleon's Vengeance");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Right click to throw");
		meta.setLore(lore);
		meta.setUnbreakable(true);
		newitem.setItemMeta(meta);
		return newitem;
	}
	
	public void tauntMobs(Player player) {
	    Location loc = player.getLocation();
	    World world = loc.getWorld();
	    if (world == null) return;

	    double radius = 20.0;

	    for (Entity entity : world.getNearbyEntities(loc, radius, radius, radius)) {
	        if (entity instanceof Mob) {
	            Mob mob = (Mob) entity;

	            // Force the mob to target the player
	            mob.setTarget(player);

	            // Optional: Visual cue (angry particles)
	            mob.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, mob.getLocation().add(0, 1, 0), 5, 0.2, 0.2, 0.2, 0);
	        }
	    }

	    // Optional: Sound and feedback
	    player.getWorld().playSound(loc, Sound.ENTITY_WITHER_SPAWN, 1f, 1.2f);
	    player.getWorld().playSound(loc, Sound.ENTITY_WOLF_GROWL, 1f, 0.6f);
	    //player.sendMessage(ChatColor.RED + "You let out a furious shout! All nearby enemies turn toward you.");
	}
	
	public boolean useItem(LivingEntity entity, ItemStack item, boolean test, int var) {
		boolean cancelEvent = false;
		boolean jammable = false;
		Vector dir = entity.getLocation().getDirection();
		if (entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.WARDEN || entity.getType() == EntityType.WITHER)
		{
			float dist = 100000;
			LivingEntity target = null;
			for (Player p : entity.getWorld().getPlayers()) {
				if (p.isOnline()) {
					float newDist = (float) entity.getEyeLocation().distance(p.getLocation());
					if (target == null || newDist < dist) {
						dist = newDist;
						target = p;
						if (illusionDoubles.containsKey(p)) {
							newDist = (float) entity.getEyeLocation().distance(illusionDoubles.get(p).getLocation());
							if (newDist < dist)
							{
								dist = newDist;
								target = illusionDoubles.get(p);
							}
						}
					}
				}
			}
			if (target != null) {
				dir = (target.getLocation().toVector().add(target.getVelocity()).subtract(entity.getEyeLocation().toVector())).normalize();
			}
		}
		//Vector offset = entity instanceof Giant ? entity.getEyeLocation().getDirection().multiply(8) : new Vector(0, 0, 0);
		Vector offset = entity instanceof Giant ? new Vector(entity.getEyeLocation().getDirection().getX(), 0, entity.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Player player = null;
		if (entity.getType() == EntityType.PLAYER) {
			player = (Player) entity;
		}
		
		if (item != null && item.getItemMeta() != null) {
			String itemName = item.getItemMeta().getDisplayName();
			boolean upgrade = false;
			Material mat = item.getType();
			
			if (mat == Material.POPPED_CHORUS_FRUIT && itemName.contains("Wildshape") && var == 0) {
				if (player != null && !player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
					cancelEvent = true;
					player.setCooldown(Material.POPPED_CHORUS_FRUIT, 20*60);
					if (itemName.contains("Archdruid"))
						openSupremeWildshapeInventory(player);
					else if (itemName.contains("Rugged"))
						openSuperiorWildshapeInventory(player);
					else
						openWildshapeInventory(player);
				}
			} else if (mat == Material.SHELTER_POTTERY_SHERD && itemName.contains("Bag Upgrade") && var == 0 && player != null && !player.hasCooldown(Material.SHELTER_POTTERY_SHERD)) {
				if (player.getScoreboardTags().contains("BagUpgrade") || player.getScoreboardTags().contains("BagOfHoldingUpgrade")) {
					player.sendMessage(ChatColor.YELLOW + "Bag upgrade already in progress, open a bag to apply it");
				} else {
					player.addScoreboardTag("BagUpgrade");
					player.sendMessage(ChatColor.GREEN + "Open a bag to apply the upgrade");
					item.setAmount(item.getAmount()-1);
				}
				player.setCooldown(Material.SHELTER_POTTERY_SHERD, 20);
			} else if (mat == Material.FLOW_POTTERY_SHERD && itemName.contains("Bag of Holding Upgrade") && var == 0 && player != null && !player.hasCooldown(Material.SHELTER_POTTERY_SHERD)) {
				if (player.getScoreboardTags().contains("BagUpgrade") || player.getScoreboardTags().contains("BagOfHoldingUpgrade")) {
					player.sendMessage(ChatColor.YELLOW + "Bag upgrade already in progress, open a bag to apply it");
				} else {
					player.addScoreboardTag("BagOfHoldingUpgrade");
					player.sendMessage(ChatColor.GREEN + "Open a bag to apply the upgrade");
					item.setAmount(item.getAmount()-1);
				}
				player.setCooldown(Material.FLOW_POTTERY_SHERD, 20);
			} else if (mat == Material.DECORATED_POT && itemName.contains("Bag") && var == 0 && player != null && !player.hasCooldown(Material.DECORATED_POT)) {
				ItemMeta meta = item.getItemMeta();
				List<String> lore = meta.hasLore() ? meta.getLore() : null;
				if (player.getScoreboardTags().contains("BagUpgrade")) {
					boolean failed = false;
					if (itemName.contains("Tiny")) {
						itemName = ChatColor.WHITE + "Small Bag";
					} else if (itemName.contains("Small")) {
						itemName = ChatColor.GREEN + "Medium Bag";
					} else if (itemName.contains("Medium")) {
						itemName = ChatColor.AQUA + "Large Bag";
					} else if (itemName.contains("Large")) {
						itemName = ChatColor.YELLOW + "Extra Large Bag";
					} else {
						failed = true;
						player.sendMessage(ChatColor.RED + "Failed to upgrade bag");
					}
					if (!failed) {
						player.sendMessage(ChatColor.GREEN + "Upgrading to " + itemName);
						meta.setDisplayName(itemName);
						item.setItemMeta(meta);
						upgrade = true;
					}
					player.removeScoreboardTag("BagUpgrade");
				} else if (player.getScoreboardTags().contains("BagOfHoldingUpgrade")) {
					if (itemName.contains("Extra Large")) {
						player.sendMessage(ChatColor.GREEN + "Upgrading to " + itemName);
						itemName = ChatColor.LIGHT_PURPLE + "Bag of Holding";
						meta.setDisplayName(itemName);
						meta.setEnchantmentGlintOverride(true);
						item.setItemMeta(meta);
						upgrade = true;
					} else {
						player.sendMessage(ChatColor.RED + "Only an " + ChatColor.YELLOW + "Extra Large Bag" + ChatColor.RED + " can be converted into a" + ChatColor.LIGHT_PURPLE + " Bag of Holding");
					}
					player.removeScoreboardTag("BagOfHoldingUpgrade");
				}
				int bagSize = getBagSize(itemName);
				int correctedBagSize = ((bagSize + 8) / 9) * 9;
				boolean success = false;
				if (lore != null && lore.size() > 0) {
					try {
						int id = Integer.parseInt(lore.get(0));
						if (id >= 0 && id < bags.size()) {
							Inventory inv = bags.get(id);
							if (upgrade) {
								ItemStack[] items = inv.getContents();
								//inv = Bukkit.createInventory(null, bagSize, itemName);
								inv = getCustomInventory(itemName, bagSize, correctedBagSize);
								for (int i = 0; i < items.length; i++) {
									if (items[i] != null && items[i].getType() != Material.BARRIER) {
										inv.setItem(i, items[i]);
									}
								}
								//inv.setContents(items);
								player.sendMessage(ChatColor.GREEN + "Bag successfully upgraded");
								bags.set(id, inv);
							}
							player.openInventory(inv);
							success = true;
						}
					} catch (Exception e) {}
				}
				if (!success) {
					// Create new inventory for this bag
					//player.sendMessage(ChatColor.YELLOW + "Creating new inventory for bag, size=" + bagSize + " name=" + itemName);
					//Inventory inv = Bukkit.createInventory(null, bagSize, itemName);
					Inventory inv = getCustomInventory(itemName, bagSize, correctedBagSize);
					//player.sendMessage("Created inventory");
					lore = new ArrayList<String>();
					//player.sendMessage("Created lore array");
					lore.add("" + bags.size());
					//player.sendMessage("Added to lore");
					meta.setLore(lore);
					//player.sendMessage("Set lore");
					item.setItemMeta(meta);
					//player.sendMessage("Set meta");
					bags.add(inv);
					//player.sendMessage("Stored inventory");
					player.openInventory(inv);
					//player.sendMessage("Opened Inventory");
				}
				player.setCooldown(Material.DECORATED_POT, 40);
			} else if (mat == Material.ENDER_CHEST && itemName.contains("Portable Ender Chest") && var == 0) {
				if (player != null && !player.hasCooldown(Material.ENDER_CHEST)) {
					cancelEvent = true;
					player.openInventory(player.getEnderChest());
					//for (Player p : Bukkit.getOnlinePlayers())
						SFX.play(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
					player.setCooldown(Material.ENDER_CHEST, 200);
				}
			} else if (mat == Material.CHEST_MINECART && itemName.contains("Summon Portable Chest") && var == 0) {
				if (player != null && !player.hasCooldown(Material.CHEST_MINECART)) {
					cancelEvent = true;
					int count = 0;
					for (Entity e : player.getWorld().getEntitiesByClasses(EntityType.CHEST_MINECART.getClass())) {
						if (e instanceof StorageMinecart crate && crate.getScoreboardTags().contains("ArenaCrate")) {
							crate.teleport(player.getLocation());
							crate.setVelocity(new Vector(Math.random()-0.5f, Math.random()-0.5, Math.random()-0.5));
							count++;
						}
					}
					
					player.setCooldown(Material.CHEST_MINECART, 20*60);
					if (count == 0) {
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1.3f);
						player.sendMessage(ChatColor.YELLOW + "Failed to find any portable chests");
					} else {
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_SHULKER_TELEPORT, 1, 1);
						player.sendMessage(ChatColor.AQUA + "Summoning " + count + " chests");
					}
				}
			}
			else if (mat == Material.POPPED_CHORUS_FRUIT && itemName.contains("Polymorph")) {
				if (player != null && !player.hasCooldown(Material.POPPED_CHORUS_FRUIT)) {
					cancelEvent = true;
					if (var == 0) {
						player.setCooldown(Material.POPPED_CHORUS_FRUIT, 20*60);
						
						openPolymorphInventory(player);
					}
				}
			}
			else if (mat == Material.DIAMOND_SWORD && itemName.contains("Holy Avenger Upgrade")) {
				cancelEvent = true;
				if (player != null && !player.hasCooldown(Material.DIAMOND_SWORD)) {
					
					item.setAmount(item.getAmount()-1);
					player.getInventory().addItem(getHolyAvenger());
					Location loc = player.getLocation();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run give @p minecraft:player_head[minecraft:custom_name='{\"text\":\"Lost My Holy Avenger\",\"color\":\"light_purple\",\"underlined\":false,\"bold\":false,\"italic\":false}',minecraft:lore=['{\"text\":\"Custom Head ID: 90892\",\"color\":\"gray\",\"italic\":false}'],profile={id:[I;-579692594,1522223452,-1104386849,-610360288],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZjYWM2OTI4ZTBjMDlmMjIwNjMxNjZjZjFlYmNhODMwMjE1NzY4OGM3YjA4OGY3ZTQyNGU5M2M5ZTRlODQwYyJ9fX0=\"}]}] 1");
					ArrayList<ItemStack> remove = new ArrayList<ItemStack>();
					for (ItemStack i : player.getInventory().getContents()) {
						if (i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().contains("Zehir")) {
							remove.add(i);
						}
					}
					for (ItemStack i : remove)
						player.getInventory().remove(i);
					player.updateInventory();
					player.setCooldown(Material.DIAMOND_SWORD, 30);
				}
				
				return true;
			} else if (mat == Material.NETHERITE_SWORD && itemName.contains("Napoleon's Vengeance Upgrade")) {
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.NETHERITE_SWORD)) {
						
						item.setAmount(item.getAmount()-1);
						player.getInventory().addItem(getNapoleonsVengeance());
						Location loc = player.getLocation();
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run give @p minecraft:player_head[minecraft:custom_name='{\"text\":\"Lost My Napoleon's Vengeance\",\"color\":\"light_purple\",\"underlined\":false,\"bold\":false,\"italic\":false}',minecraft:lore=['{\"text\":\"Custom Head ID: 90892\",\"color\":\"gray\",\"italic\":false}'],profile={id:[I;-579692594,1522223452,-1104386849,-610360288],properties:[{name:\"textures\",value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZjYWM2OTI4ZTBjMDlmMjIwNjMxNjZjZjFlYmNhODMwMjE1NzY4OGM3YjA4OGY3ZTQyNGU5M2M5ZTRlODQwYyJ9fX0=\"}]}] 1");
						ArrayList<ItemStack> remove = new ArrayList<ItemStack>();
						for (ItemStack i : player.getInventory().getContents()) {
							if (i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && (i.getItemMeta().getDisplayName().contains("Zehir") || i.getItemMeta().getDisplayName().contains("Holy Avenger"))) {
								remove.add(i);
							}
						}
						for (ItemStack i : remove)
							player.getInventory().remove(i);
						player.updateInventory();
						player.setCooldown(Material.NETHERITE_SWORD, 30);
					}
					
					return true;
				}
			if (var == 2) { // Press Q
				if (itemName.contains("Wyvernbone Rocket Hammer")) {
					// Spin!
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MACE)) {
						hammerSpin.put(entity, new Tuple<Integer, Integer>((int)entity.getLocation().getYaw(), 0));
						if (player != null) {
							player.setCooldown(Material.MACE, 80);
						}
					}
				}
				
				 else if (itemName.contains("Antimatter Gattling Gun")) {
					 if (test)
						 return true;
					 cancelEvent = true;
						if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
							ItemStack i = player != null ? findInventoryItem(player, Material.NETHER_WART) : null;
							if (player == null || (i != null && i.getAmount() >= 4)) {
								if (i != null)
									i.setAmount(i.getAmount()-4);
								fireAntimatterCore(entity);

								if (player != null)
								{
									player.setCooldown(Material.IRON_HOE, 20*10);
								}
							} else {
								if (player != null)
									player.sendMessage("No ammo (requires 4 antimatter shells)");
								//for (Player p : Bukkit.getServer().getOnlinePlayers())
									SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
							}
						}
					}
				else if (itemName.contains("Staff of Swarming Insects")) {
					if (test)
						return true;
					cancelEvent = true;
					swarmingInsects.put(entity, 200);
				}
				else if (itemName.contains("Staff of the Proletariat")) {
					if (test)
						return true;
					cancelEvent = true;
					if (player.hasCooldown(Material.GOLDEN_HOE)) {
						player.sendMessage(ChatColor.RED + "You must wait for the cooldown to end before using this ability");
					} else {
						player.setCooldown(Material.GOLDEN_HOE, 8);
						if (illusionDoubles.containsKey((LivingEntity) entity) && illusionDoubles.get((LivingEntity) entity) != null && illusionDoubles.get((LivingEntity) entity).isValid()) {
							Location playerLoc = entity.getLocation().clone();
							Location masLoc = illusionDoubles.get((LivingEntity) entity).getLocation().clone().setDirection(dir);
							
							delayedTeleport.put(entity, masLoc);
							delayedTeleport.put(illusionDoubles.get((LivingEntity) entity), playerLoc);
							
							if (entity.getScoreboardTags().contains("Grapple")) {
								entity.removeScoreboardTag("Grapple");
								//grapple.remove(entity);
							}
							
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1, 2f);
							jammable = true;
						} else {
							Bullet b = new Bullet(dir, entity.getLocation().clone().add(0, 1.55, 0), true, Particle.ENCHANT, 20, 1.5f, 12, entity, this); // Maskirovka
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1, 1.3f);
						}
					}
				}
			}
			else {
				if (itemName.contains("---") && var == 0) {
					Arena.ArenaEditItem(player, item, player.getTargetBlock((Set<Material>) null, 4));
					cancelEvent = true;
				} else if (mat == Material.SHULKER_SHELL && var == 0) {
					cancelEvent = true;
					ItemStack headItem = entity.getEquipment().getHelmet();
					if (headItem != null)
						headItem = headItem.clone();
					entity.getEquipment().setHelmet(item);
					entity.getEquipment().setItemInMainHand(headItem);
				}
				else if (mat == Material.NETHERITE_SWORD && itemName.contains("Napoleon's Vengeance") && var == 0) {
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.NETHERITE_SWORD)) {
						
						openFabricateMenu(player);
						
						player.setCooldown(Material.NETHERITE_SWORD, 20);
					}
					
					return true;
				}
				else if (item.getType() == Material.CLOCK && itemName.contains("Chronal Shift")) {
					cancelEvent = true;
					if (test)
						return true;
					if (player == null || !player.hasCooldown(Material.CLOCK)) {
						if (player != null) {
							if (playerStates.containsKey(player)) {
								prepare_teleport(player);
								//if (player.getPassengers().size() > 0)
								//	player.removePassenger(player.getPassengers().get(0));
								playerStates.get(player)[1].activatePlayerState(player);
								final Player p = player;
								player.sendMessage(ChatColor.LIGHT_PURPLE + "You performed a chronal shift");
								Bukkit.getScheduler().runTaskLater(this, () -> {
			                    	playerStates.get(p)[2].activatePlayerState(p);
						        }, 3); // Delay of 0.15 seconds
							} else {
								playerStates.put(player, new PlayerState[] {new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player)});
								player.sendMessage(ChatColor.GREEN + "Began tracking your player-state for future chronal shifts");
							}
							player.setCooldown(Material.CLOCK, 15*20);
						}
					}
				} else if (item.getType() == Material.CLOCK && itemName.contains("Convergent Future")) {
					cancelEvent = true;
					if (test)
						return true;
					if (player != null && !player.hasCooldown(Material.CLOCK)) {
						player.addScoreboardTag("ConvergentFuture");
						if (playerStates.containsKey(player)) {
							ConvergentFuture(player);
						} else {
							playerStates.put(player, new PlayerState[] {new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player), new PlayerState(player)});
							player.sendMessage(ChatColor.GREEN + "Began tracking your player-state for future convergent futures (ironic)");
						}
						player.setCooldown(Material.CLOCK, 15*20);
					}
				}
				else if (item.getType() == Material.HEAVY_CORE && itemName.contains("Repulsor Flight Upgrade")) {
					cancelEvent = true;
					if (test)
						return true;
					if (player == null || !player.hasCooldown(Material.HEAVY_CORE)) {
						
						boolean success = enableRepulsorFly(entity);
						
						if (player != null) {
							ItemStack newItem = new ItemStack(Material.HEAVY_CORE);
							ItemMeta meta = newItem.getItemMeta();
							meta.setDisplayName(ChatColor.AQUA + "Disable Repulsor Flight");
							newItem.setItemMeta(meta);
							player.getInventory().addItem(newItem);
							player.setCooldown(Material.HEAVY_CORE, 20);
							if (success) {
								item.setAmount(item.getAmount()-1);
								player.sendMessage(ChatColor.GREEN + "Enabling repulsor fly");
							} else {
								player.sendMessage(ChatColor.RED + "Repulsor fly is already enabled");
							}
						} else {
							FancyMob.SwapWeapon(entity);
						}
						
					}
				} else if (item.getType() == Material.HEAVY_CORE && itemName.contains("Disable Repulsor Flight")) {
					cancelEvent = true;
					if (test)
						return true;
					if (player == null || !player.hasCooldown(Material.HEAVY_CORE)) {
						
						boolean success = disableRepulsorFly(entity);
						
						if (player != null) {
							player.setCooldown(Material.HEAVY_CORE, 20);
							ItemStack newItem = new ItemStack(Material.HEAVY_CORE);
							ItemMeta meta = newItem.getItemMeta();
							meta.setDisplayName(ChatColor.AQUA + "Repulsor Flight Upgrade");
							newItem.setItemMeta(meta);
							player.getInventory().addItem(newItem);
							if (success) {
								item.setAmount(item.getAmount()-1);
								player.sendMessage(ChatColor.GREEN + "Disabling repulsor fly");
							} else {
								player.sendMessage(ChatColor.RED + "Repulsor fly is already disabled");
							}
						} else {
							FancyMob.SwapWeapon(entity);
						}
						
					}
				}
				else if (item.getType() == Material.BARRIER && itemName.contains("Deactivate Sentry Mode") && var == 0) {
					cancelEvent = true;
					if (sentryMode.containsKey(entity)) {
						sentryMode.get(entity).remove();
						sentryMode.remove(entity);
					}
					if (sentryTarget.containsKey(entity)) {
						sentryTarget.remove(entity);
					}
					if (player != null && !player.hasCooldown(Material.BARRIER)) {
						player.setCooldown(Material.RAW_COPPER, 20);
						player.setCooldown(Material.BARRIER, 20);
						item.setAmount(item.getAmount()-1);
						ItemStack newItem = new ItemStack(Material.RAW_COPPER);
						ItemMeta meta = newItem.getItemMeta();
						meta.setCustomModelData(4);
						meta.setDisplayName(ChatColor.YELLOW + "Sentry Mode");
						newItem.setItemMeta(meta);
						player.getInventory().addItem(newItem);
					}
				}
				else if (item.getType() == Material.BARRIER && itemName.contains("Deactivate Wings") && var == 0) {
					cancelEvent = true;
					if (durrithWings.containsKey(entity)) {
						durrithWings.get(entity).remove();
						durrithWings.remove(entity);
					}
					if (player != null && !player.hasCooldown(Material.BARRIER)) {
						player.setAllowFlight(false);
						player.setCooldown(Material.RAW_COPPER, 20);
						player.setCooldown(Material.BARRIER, 20);
						item.setAmount(item.getAmount()-1);
						ItemStack newItem = new ItemStack(Material.RAW_COPPER);
						ItemMeta meta = newItem.getItemMeta();
						meta.setCustomModelData(14);
						meta.setDisplayName(ChatColor.GOLD + "Wing Upgrade");
						newItem.setItemMeta(meta);
						player.getInventory().addItem(newItem);
					}
				}
				else if (item.getType() == Material.RAW_COPPER && itemName.contains("Sentry Mode") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.BARRIER)) {
						boolean success = spawnSentryMode(entity);
						if (player != null) {
							player.setCooldown(Material.RAW_COPPER, 20);
							player.setCooldown(Material.BARRIER, 20);
							if (success) {
								item.setAmount(item.getAmount()-1);
								ItemStack newItem = new ItemStack(Material.BARRIER);
								ItemMeta meta = newItem.getItemMeta();
								meta.setDisplayName(ChatColor.RED + "Deactivate Sentry Mode");
								newItem.setItemMeta(meta);
								player.getInventory().addItem(newItem);
								player.sendMessage(ChatColor.GREEN + "Activating sentry mode");
							} else {
								player.sendMessage(ChatColor.RED + "Sentry mode is already active");
							}
						} else {
							FancyMob.SwapWeapon(entity);
						}
					}
				}
				else if (item.getType() == Material.RAW_COPPER && itemName.contains("Wing Upgrade") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.BARRIER)) {
						boolean success = spawnDurrithWings(entity);
						if (player != null) {
							player.setCooldown(Material.RAW_COPPER, 20);
							player.setCooldown(Material.BARRIER, 20);
							if (success) {
								item.setAmount(item.getAmount()-1);
								ItemStack newItem = new ItemStack(Material.BARRIER);
								ItemMeta meta = newItem.getItemMeta();
								meta.setDisplayName(ChatColor.RED + "Deactivate Wings");
								newItem.setItemMeta(meta);
								player.getInventory().addItem(newItem);
								player.sendMessage(ChatColor.GREEN + "Growing wings");
							} else {
								player.sendMessage(ChatColor.RED + "You already have wings");
							}
						} else {
							FancyMob.SwapWeapon(entity);
						}
					}
				}
				else if (mat == Material.CROSSBOW && itemName.contains("Barrage") && var == 0 && player == null) {
					//arrowBarrage.put(giant == null ? entity : giant, 10);
					cancelEvent = true;
					if (test)
						return true;
					if (itemName.contains("Royal"))
						royalArrowBarrage.put(entity, 12);
					else if (itemName.contains("Supreme"))
						royalArrowBarrage.put(entity, 15);
					else
						arrowBarrage.put(entity, 10);
		        }
				else if (itemName.contains("Wand of the Jailer") && (item.getType() == Material.CHAIN || mat == Material.TOTEM_OF_UNDYING) && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(mat)) {
						jammable = true;
						
						Location loc = entity.getEyeLocation();
						//Vector dir = dir;

						//for (Player p : Bukkit.getOnlinePlayers()) {
							SFX.play(loc, Sound.ENTITY_BLAZE_DEATH, 1, 0.5f);
						//}
						
						Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.ANGRY_VILLAGER, 60, 0.8f, 28, entity, this, 5, 4, 0.3f, 0.1f);
						
						if (player != null) {
							player.setCooldown(mat, 20*120);
						}
					}
				}
				else if (itemName.contains("Wall of Fire") && item.getType() == Material.BLAZE_ROD) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.BLAZE_ROD)) {
						jammable = true;
						wallOfFireSpawner.put(entity, 10);
						
						if (player != null) {
							player.setCooldown(Material.BLAZE_ROD, 20*45);
						}
					}
				}
				else if (itemName.contains("Brutal Critical Upgrade") && item.getType() == Material.FERMENTED_SPIDER_EYE) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.DARK_RED + "Enabling Brutal Critical!");
						brutalCrit.add(player.getName());
					}
				}
				else if (itemName.contains("Wall Run Upgrade") && item.getType() == Material.LADDER) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.GREEN + "Enabling Wall Run!");
						wallRun.add(player.getName());
					}
				}
				else if (itemName.contains("Scavenger's Kit Upgrade") && item.getType() == Material.NETHERITE_SCRAP) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.GREEN + "Enabling Scavenger's Kit!");
						scavengersKit.add(player.getName());
					}
				}
				else if (itemName.contains("Adrenaline Rush Upgrade") && item.getType() == Material.HONEYCOMB) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.GREEN + "Enabling Adrenaline Rush!");
						adrenalineRush.add(player.getName());
					}
				}
				else if (itemName.contains("No Pain No Gain Upgrade") && item.getType() == Material.HONEYCOMB) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.GREEN + "Enabling No Pain No Gain!");
						noPainNoGain.add(player.getName());
					}
				}
				else if (itemName.contains("Wall Jump Upgrade") && item.getType() == Material.RABBIT_FOOT) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.GREEN + "Enabling Wall Jump!");
						wallJump.add(player.getName());
					}
				}
				else if (itemName.contains("Double Jump Upgrade") && item.getType() == Material.FEATHER) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.AQUA + "Enabling Double Jump!");
						doubleJump.add(player.getName());
					}
				}
				else if (itemName.contains("Heavy Fists Upgrade") && item.getType() == Material.HEAVY_CORE) {
					cancelEvent = true;
					if (player != null) {
						item.setAmount(item.getAmount()-1);
						player.sendMessage(ChatColor.AQUA + "Enabling Heavy Fists!");
						heavyFists.add(player.getName());
					}
				}
				else if (itemName.contains("Taunt") && item.getType() == Material.TORCHFLOWER) {
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.TORCHFLOWER)) {
						tauntMobs(player);

					    player.setCooldown(Material.TORCHFLOWER, 20);
					}
				}
				 else if (itemName.contains("Antimatter Gattling Gun")) {
						if (test)
							return true;
						cancelEvent = true;
						if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
							jammable = true;
							ItemStack i = player != null ? findInventoryItem(player, Material.NETHER_WART) : null;
							if (player == null || (i != null && i.getAmount() >= 0)) {
								if (i != null)
									i.setAmount(i.getAmount()-1);
								fireSmallAntimatter(entity);

								if (player != null)
								{
									player.setCooldown(Material.IRON_HOE, 8);
								}
							} else {
								if (player != null)
									player.sendMessage("No ammo (requires antimatter shells)");
								//for (Player p : Bukkit.getServer().getOnlinePlayers())
									SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
							}
							
						}
					}
					else if (itemName.contains("Antimatter Rifle") && var == 0) {
						if (test)
							return true;
						cancelEvent = true;
						if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
							jammable = true;
							ItemStack i = player != null ? findInventoryItem(player, Material.NETHER_WART) : null;
							if (player == null || (i != null && i.getAmount() >= 0)) {
								if (i != null)
									i.setAmount(i.getAmount()-1);
								fireAntimatter(entity);

								if (player != null)
								{
									player.setCooldown(Material.IRON_HOE, 35);
								}
							} else {
								if (player != null)
									player.sendMessage("No ammo (requires antimatter shells)");
								//for (Player p : Bukkit.getServer().getOnlinePlayers())
									SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
							}
							
						}
					} else if (itemName.contains("Wyvernbone Rocket Hammer") && var == 0) {
					// FLYYYYY
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MACE)) {
						if (entity.getScoreboardTags().contains("Grapple")) {
							entity.removeScoreboardTag("Grapple");
							//grapple.remove(entity);
						}
						hammerBoost.put(entity, 10);
						if (player != null) {
							player.setCooldown(Material.MACE, 60);
						}
					}
				}
				else if (itemName.contains("Lost My Charm of Zehir") && var == 0) {
					if (player != null) {
						ItemStack newitem = new ItemStack(Material.STONE_SWORD);
						ItemMeta meta = newitem.getItemMeta();
						meta.setCustomModelData(10000102);
						meta.setDisplayName("Charm of Broken Zehirs");
						meta.setUnbreakable(true);
						newitem.setItemMeta(meta);
						
						for (ItemStack invItem : player.getInventory().getContents()) {
							if (invItem != null && invItem.getItemMeta() != null && invItem.getItemMeta().getDisplayName().contains("Broken Zehirs")) {
								player.sendMessage("Your item is in your inventory you lying cheat!");
								return true;
							}
						}
						
						ArmorStand projectile = null;
						for (ArmorStand proj : thrownItems.keySet()) {
							if (proj.getEquipment().getHelmet() != null) {
								if (proj.getEquipment().getHelmet().getItemMeta().getDisplayName().contains("Blade of Broken Zehirs")) {
									projectile = proj;
								}
							}
						}
						if (projectile != null) {
							newitem = projectile.getEquipment().getHelmet().clone();
							thrownItems.remove(projectile);
							player.sendMessage("Returning " + newitem.getItemMeta().getDisplayName());
							projectile.remove();
						} else {
							player.sendMessage("Couldn't fine item, creating a new one");
						}
						player.getInventory().addItem(newitem);
					}
				}
				else if (itemName.contains("Lost My Holy Avenger") && var == 0) {
					if (player != null) {
						ItemStack newitem = getHolyAvenger();
						
						for (ItemStack invItem : player.getInventory().getContents()) {
							if (invItem != null && invItem.getType() == Material.DIAMOND_SWORD && invItem.getItemMeta() != null && invItem.getItemMeta().getDisplayName().contains("Holy Avenger")) {
								player.sendMessage("Your item is in your inventory you lying cheat!");
								return true;
							}
						}
						
						ArmorStand projectile = null;
						for (ArmorStand proj : thrownItems.keySet()) {
							if (proj.getEquipment().getHelmet() != null) {
								if (proj.getEquipment().getHelmet().getItemMeta().getDisplayName().contains("Holy Avenger")) {
									projectile = proj;
								}
							}
						}
						if (projectile != null) {
							newitem = projectile.getEquipment().getHelmet().clone();
							thrownItems.remove(projectile);
							player.sendMessage("Returning " + newitem.getItemMeta().getDisplayName());
							projectile.remove();
						} else {
							player.sendMessage("Couldn't fine item, creating a new one");
						}
						player.getInventory().addItem(newitem);
					}
				}
				else if (mat == Material.FIRE_CHARGE && itemName.contains("Deadly Fireball") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.FIRE_CHARGE)) {
						Fireball fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), Fireball.class);
						boolean superDeadly = itemName.contains("Super");
						fb.setFireTicks(superDeadly ? 40 : 20);
						fb.setIsIncendiary(true);
						fb.setYield(superDeadly ? 6 : 3);
						fb.setShooter(entity);
						fb.setVelocity(dir.multiply(superDeadly ? 3.5f : 2.5f));
						EntityKiller k = new EntityKiller(fb, 80);
						item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
	
						if (player != null)
							player.setCooldown(Material.FIRE_CHARGE, superDeadly ? 40 : 30); // 1 second cooldown
					}
				}
				else if (mat == Material.MUSIC_DISC_PIGSTEP && itemName.contains("Parasite Roar") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MUSIC_DISC_PIGSTEP)) {
						Location loc = entity.getLocation();
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound nighthunter_howl hostile @a ~ ~ ~ 2 1");
						for (Entity e : entity.getNearbyEntities(20, 20, 20)) {
							if (e instanceof LivingEntity le && Arena.validTarget(le) && entity != le) {
								if (!isFriendlyFire(entity, le)) {
									le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 2, true));
									le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 5, true));
									le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 5, true));
									le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 5, true));
									le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 100, 5, true));
									le.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, le.getEyeLocation(), 5);
								}
							}
						}
						if (player != null)
							player.setCooldown(Material.MUSIC_DISC_PIGSTEP, 20*18);
					}
				}
				else if ((itemName.contains("Sunbeam") || itemName.contains("Pelor's Wrath")) && var == 0 && item.getType() == Material.SUNFLOWER) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.SUNFLOWER)) {
						jammable = true;
						int yeet = itemName.contains("Pelor") ? 3 : (itemName.contains("Superior") ? 2 : 1);
						sunbeam.put(new Tuple<LivingEntity, Integer>(entity, yeet), 15 + 5 * yeet);
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_GHAST_HURT, 1, 1);
						//}
						
						//Location loc = entity.getEyeLocation();
						//Vector dir = loc.getDirection();
						
						//Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.FLAME, 60, 0.8f, 26, entity, this);
						
						if (player != null)
							player.setCooldown(Material.SUNFLOWER, 20*8);
					}
				}
				else if (itemName.contains("Dragon Breath") && var == 0 && item.getType() == Material.CAMPFIRE) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.CAMPFIRE)) {
						jammable = true;
						float mult = 1;
						if (itemName.contains("Supreme")) {
							mult = 2;
							supremeDragonBreath = true;
						} else {

							supremeDragonBreath = false;
						}
						dragonbreath.put(entity, (int)(30 * mult));
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
						//}
						
						Location loc = entity.getEyeLocation();
						//Vector dir = loc.getDirection();
						
						Bullet b = new Bullet(dir.clone(), loc.clone().add(dir), false, Particle.LAVA, 100, 0.9f, 2, entity, this, (int)(22 * mult + 0.5f), 5, 0.1f, 0.1f);
						
						if (player != null)
							player.setCooldown(Material.CAMPFIRE, 20*8);
					}
				}
				else if (itemName.contains("Hypnotic Pattern") && var == 0 && item.getType() == Material.MUSIC_DISC_WARD) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MUSIC_DISC_WARD)) {
						jammable = true;
						
						Location loc = entity.getEyeLocation();
						//Vector dir = loc.getDirection();
						
						Bullet b = new Bullet(dir.clone(), loc.clone(), true, Particle.NOTE, 60, 0.8f, 29, entity, this);
						
						if (player != null) {
							player.setCooldown(Material.MUSIC_DISC_WARD, 20*40);
						}

					}
				}
				else if ((itemName.contains("Primordial Ward")) && var == 0 && item.getType() == Material.HEART_OF_THE_SEA) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.HEART_OF_THE_SEA)) {
						jammable = true;
						primordialWard.put(entity, 120*5);
						if (player != null)
							player.sendMessage(ChatColor.AQUA + "Activated primordial ward");
						for (float i = 0; i < 2*Math.PI; i += 0.05) {
							entity.getWorld().spawnParticle(Particle.SCULK_SOUL, entity.getLocation().add(new Vector(5*Math.sin(i), 1, 5*Math.cos(i))), 1, 0, 0, 0, 0);
						}
						for (Entity e : entity.getWorld().getNearbyEntities(entity.getLocation(), 5, 5, 5)) {
							if (e instanceof LivingEntity le && ((player != null && le instanceof Player) || (player == null && !(le instanceof Player)))) {
								if (le instanceof Player p)
									p.sendMessage(ChatColor.AQUA + "You recieved a primordial ward from " + entity.getName());
								primordialWard.put(le, 120*5);
								le.addScoreboardTag("PrimordialWard");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s fire_resistance 120 0 true");
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s resistance 120 0 true");
							}
						}
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.6f);
						//}
						
						if (player != null)
							player.setCooldown(Material.HEART_OF_THE_SEA, 20*120);
					}
				}
				else if (itemName.contains("Antilife Shell") && var == 0 && item.getType() == Material.NAUTILUS_SHELL) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.NAUTILUS_SHELL)) {
						entity.addScoreboardTag("antilife");
						antilifeShell.put(entity, 20*40);
						SFX.play(entity.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1, 0.6f);
						if (player != null)
							player.setCooldown(Material.NAUTILUS_SHELL, 20*45);
					}
				}
				else if (itemName.contains("Freedom of Movement") && var == 0 && item.getType() == Material.RABBIT_HIDE) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RABBIT_HIDE)) {
						if (player != null)
							player.sendMessage(ChatColor.GREEN + "You activated Freedom of Movement");
						entity.addScoreboardTag("fom");
						freedomOfMovement.put(entity, 20*45);
						for (Entity e : entity.getNearbyEntities(5, 5, 5))
						{
							if (e instanceof LivingEntity le && (entity instanceof Player) == (le instanceof Player)) {
								if (le instanceof Player p)
									p.sendMessage(ChatColor.GREEN + "You received Freedom of Movement from " + (entity.getCustomName() != null ? entity.getCustomName() : entity.getName()));
								le.addScoreboardTag("fom");
								freedomOfMovement.put(le, 20*20);
							}
						}
						for (float i = 0; i < 2*Math.PI; i += 0.05) {
							entity.getWorld().spawnParticle(Particle.GLOW, entity.getLocation().add(new Vector(5*Math.sin(i), 1, 5*Math.cos(i))), 1, 0, 0, 0, 0);
						}
						SFX.play(entity.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1, 1.8f);
						if (player != null)
							player.setCooldown(Material.RABBIT_HIDE, 20*55);
					}
				}
				else if ((itemName.contains("Elemental Ward")) && var == 0 && item.getType() == Material.HEART_OF_THE_SEA) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.HEART_OF_THE_SEA)) {
						jammable = true;
						elementalWard.put(entity, 10);
						if (player != null)
							player.sendMessage(ChatColor.AQUA + "Activated elemental ward");
						for (float i = 0; i < 2*Math.PI; i += 0.05) {
							entity.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, entity.getLocation().add(new Vector(5*Math.sin(i), 1, 5*Math.cos(i))), 1, 0, 0, 0, 0);
						}
						for (Entity e : entity.getWorld().getNearbyEntities(entity.getLocation(), 5, 5, 5)) {
							if (e instanceof LivingEntity le && ((player != null && le instanceof Player) || (player == null && !(le instanceof Player)))) {
								if (le instanceof Player p)
									p.sendMessage(ChatColor.AQUA + "You recieved an elemental ward from " + entity.getName());
								elementalWard.put(le, 60*5);
								le.addScoreboardTag("ElementalWard");
								//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s fire_resistance 60 0 true");
							}
						}
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 0.6f);
						//}
						
						if (player != null)
							player.setCooldown(Material.HEART_OF_THE_SEA, 20*60);
					}
				}
				else if (mat == Material.STICK && itemName.contains("Vampire Staff")) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || (!player.hasCooldown(Material.STICK))) {
						jammable = true;
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_DEATH, 1f, 1.8f);
						//}
						
						Location loc = entity.getLocation();
						
						//Vector dir = loc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						Location shootLoc = entity.getEyeLocation().add(offset);
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						Bullet b = new Bullet(dir.clone().multiply(1.5f), shootLoc.clone(), false, Particle.SMOKE, 60, 0.8f, 23, entity, this);
						
						if (player != null) {
							player.setCooldown(Material.STICK, 30);
						}
					}
				}
				else if (mat == Material.STICK && itemName.contains("Staff of Wither Skulls")) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || (!player.hasCooldown(Material.STICK))) {
						WitherSkull fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), WitherSkull.class);
						//fb.setFireTicks(5);
						//fb.setIsIncendiary(true);
						fb.setShooter(entity);
						if (itemName.contains("Super Deadly"))
						{
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..16] minecraft:wither 10 8");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:5,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~ 2 2 2 0 6");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(10);
							fb.setVelocity(dir.multiply(1.8f));
						}
						else if (itemName.contains("Deadly")) {
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..9] minecraft:wither 10 4");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:3,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~ 1 1 1 0 3");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(8);
							fb.setVelocity(dir.multiply(1.6f));
						}
						else {
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..5] minecraft:wither 10 2");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:1,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(6);
							fb.setVelocity(dir.multiply(1.5f));
						}
						EntityKiller k = new EntityKiller(fb, 80);
						homing.put(fb, new Tuple<LivingEntity, Vector>(entity, dir));
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1f);
						
						if (player != null) {
							player.setCooldown(Material.STICK, 25);
						}
					}
				}
				else if ((mat == Material.WITHER_SKELETON_SKULL || mat == Material.PLAYER_HEAD) && itemName.contains("Wither Skull") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || (!player.hasCooldown(Material.WITHER_SKELETON_SKULL) && !player.hasCooldown(Material.PLAYER_HEAD))) {
						WitherSkull fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), WitherSkull.class);
						//fb.setFireTicks(5);
						//fb.setIsIncendiary(true);
						fb.setShooter(entity);
						if (itemName.contains("Super Deadly"))
						{
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..16] minecraft:wither 10 8");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:5,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~ 2 2 2 0 6");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(10);
							fb.setVelocity(dir.multiply(1.8f));
						}
						else if (itemName.contains("Deadly")) {
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..9] minecraft:wither 10 4");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:3,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~ 1 1 1 0 3");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(8);
							fb.setVelocity(dir.multiply(1.6f));
						}
						else {
							List<String> commands = new ArrayList<String>();
							commands.add("effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..5] minecraft:wither 10 2");
							commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:1,CustomName:[{text:Witherskull}]}");
							commands.add("particle explosion_emitter ~ ~ ~");
							fb.setMetadata("commands", new FixedMetadataValue(instance, commands));
							fb.setYield(6);
							fb.setVelocity(dir.multiply(1.5f));
						}
						EntityKiller k = new EntityKiller(fb, 80);
						item.setAmount(item.getAmount()-1);
						homing.put(fb, new Tuple<LivingEntity, Vector>(entity, dir));
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1f);
						
						if (player != null) {
							player.setCooldown(Material.WITHER_SKELETON_SKULL, 30);
							player.setCooldown(Material.PLAYER_HEAD, 30);
						}
					}
				}
				else if (mat == Material.FIRE_CHARGE && itemName.contains("Fireball") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.FIRE_CHARGE)) {
						Fireball fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), Fireball.class);
						fb.setFireTicks(5);
						fb.setIsIncendiary(true);
						fb.setYield(2);
						fb.setShooter(entity);
						fb.setVelocity(dir.multiply(1.5f));
						EntityKiller k = new EntityKiller(fb, 80);
						item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
						
						if (player != null)
							player.setCooldown(Material.FIRE_CHARGE, 20); // 1 second cooldown
					}
				} else if (mat == Material.PISTON && itemName.contains("Rocket Jump") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.PISTON)) {
						entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 5));
						entity.setVelocity(entity.getVelocity().add(new Vector(0, 1.5f, 0)).add(dir.multiply(0.5f)));
						Location loc = entity.getLocation();
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:2,CustomName:[{text:RocketJump}]}");
						if (player != null) {
							player.setCooldown(Material.PISTON, 60);
						}
					}
				}
				else if (mat == Material.STICK && itemName.contains("Repulsor") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						if (Math.random() < 0.4f)
							jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.9f, 2f);
							SFX.play(entity.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1.5f);
							SFX.play(entity.getLocation(), Sound.ENTITY_BREEZE_DEATH, 1f, 1.2f);
							SFX.play(entity.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 2f);
						//}
						
						Location loc = entity.getLocation();

						Location shootLoc = entity.getEyeLocation().add(offset);
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Vector dir = shootLoc.getDirection();
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						if (itemName.contains("Cold")) {
							Bullet b = new Bullet(dir.multiply(1.5f), shootLoc, true, Particle.GLOW_SQUID_INK, 60, 0.85f, 5, entity, this, 15);
						}
						else if (itemName.contains("Omega")) {
							Bullet b = new Bullet(dir.multiply(1.5f), shootLoc, true, Particle.GLOW_SQUID_INK, 70, 0.9f, 5, entity, this, 20);
						}
						else
						{
							Bullet b = new Bullet(dir.multiply(1.5f), shootLoc, true, Particle.GLOW_SQUID_INK, 60, 0.8f, 5, entity, this);
						}
						
						if (player != null) {
						
							player.setCooldown(Material.STICK, 12);
						}
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("Acheron's Cube") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_COPPER)) {
						
						Bullet b = new Bullet(entity.getEyeLocation().getDirection().multiply(1.1f), entity.getEyeLocation(), true, Particle.SONIC_BOOM, 60, 0.9f, 30, entity, this);
						
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(entity.getEyeLocation(), Sound.ENTITY_ELDER_GUARDIAN_DEATH, 10, 1.5f);
						
						if (player != null) {
							player.setCooldown(Material.RAW_COPPER, 20*30);
						}
					}
				}
				else if (mat == Material.REDSTONE_TORCH && itemName.contains("Wand of Healing") && (var == 1 || var == 0)) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.REDSTONE_TORCH)) {
						jammable = true;
						
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							//SFX.play(entity.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
							SFX.play(entity.getLocation(), Sound.ENTITY_CAT_HISS, 1f, 1.6f);
						//}
						
						//Location loc = entity.getLocation();
						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						//Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.5D)));
	
						//shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
	
						if (player != null) {
							player.setCooldown(Material.REDSTONE_TORCH, 30);
						}
						
						int healAmount = 2;
						
						if (itemName.contains("Supreme")) {
							healAmount = 6;
						} else if (itemName.contains("Superior")) {
							healAmount = 4;
						}
						LivingEntity target = null;
						int range = 16;
						float speed = 0.5f;
						double minAngle = 30;
						if (var == 1) {
							Collection<Entity> nearbyEntities = shootLoc.getWorld().getNearbyEntities(shootLoc.clone().add(dir.clone().multiply(range/2)), range, range, range);
							for (Entity nearbyEntity : nearbyEntities) {
						        if (nearbyEntity instanceof LivingEntity le && le != entity && Arena.validTarget(le)) {
						        	
						            // Get vector from the shooter to the target
						            Vector toTarget = le.getLocation().toVector().subtract(shootLoc.toVector()).normalize();
					
						            // Calculate the angle between the direction and the target
						            double angle = Math.toDegrees(dir.angle(toTarget));
					
						            // Check if the angle is within 10 degrees (converted to radians)
						            if (angle <= minAngle) {
						                //targets.add(le);
						                target = le;
						                minAngle = angle;
						                if (target.getType() == entity.getType())
						                	break;
						            }
						        }
							}
							Bullet b = new Bullet(entity.getEyeLocation().getDirection().multiply(speed), shootLoc, false, Particle.HEART, (int)(range/speed), 0.5f, 15, entity, this, healAmount);
							b.target = target;
						}
						else if (var == 0) {
							HealSpell(entity, healAmount);
							//entity.setHealth(Math.min(entity.getHealth() + healAmount, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
							//entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*2, 3));
							//entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*2, 0));
							//entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*2, 0));
						}
						
					}
				}
				else if (mat == Material.GUNPOWDER && itemName.contains("Ring of Arcane Antithesis") && var == 0) {
					if (player == null || !player.hasCooldown(Material.GUNPOWDER)) {
						
						counterspell(entity, entity.getEyeLocation().getDirection());
						
						if (player != null)
							player.setCooldown(Material.GUNPOWDER, 80);
						
					}
				}
				else if (mat == Material.GUNPOWDER && itemName.contains("Ring of Warding") && var == 0) {
					if (player == null || !player.hasCooldown(Material.GUNPOWDER)) {
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s resistance 30 200 true");
						//for (Player p : Bukkit.getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1, 0.5f);
						//}
						
						if (player != null) {
							player.setCooldown(Material.GUNPOWDER, 20*60);
						}
					}
				}
				else if (mat == Material.STICK && itemName.contains("Staff of Swarming Insects") && (var == 1 || var == 0)) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						
						jammable = true;
						
						Location loc = entity.getLocation();
						
						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.5D)));
	
						if (var == 0) {
							//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
								SFX.play(loc, Sound.ENTITY_CAT_HISS, 1f, 2f);
								SFX.play(loc, Sound.ENTITY_ARROW_SHOOT, 1, 0.8f);
							//}
							shootLoc.getWorld().spawnParticle(Particle.BLOCK, shootLoc, 15, 0.2, 0.1, 0.1, 0.1, Material.OAK_LEAVES.createBlockData());
							if (itemName.contains("Supreme")) {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.HAPPY_VILLAGER, 15, 0.6f, 22, entity, this, 20, 3, 0.5f, 0.1f);
							} else if (itemName.contains("Superior")) {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.HAPPY_VILLAGER, 15, 0.6f, 16, entity, this, 14, 3, 0.5f, 0.1f);
							} else {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.HAPPY_VILLAGER, 15, 0.6f, 16, entity, this);
							}
						} else if (var == 1) {
							//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
								SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1.1f);
								SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1, 1.5f);
							//}
							shootLoc.getWorld().spawnParticle(Particle.EXPLOSION, shootLoc, 15, 0.2, 0.1, 0.1, 0.1);
							if (itemName.contains("Supreme")) {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.GUST, 7, 0.9f, 17, entity, this, 19, 9, 0.6f, 0.35f);
							} else if (itemName.contains("Superior")) {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.GUST, 7, 0.9f, 17, entity, this, 13, 7, 0.6f, 0.35f);
							} else {
								Bullet b = new Bullet(dir.multiply(1.6), shootLoc, false, Particle.GUST, 7, 0.9f, 17, entity, this, 9, 6, 0.6f, 0.35f);
							}
						}
						
	
						if (player != null) {
						
							player.setCooldown(Material.STICK, 20);
						}
						
					}
				} else if (mat == Material.STICK && itemName.contains("Staff of Enervation") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 1.1f);
							SFX.play(entity.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 0.8f);
						//}
						
						boolean supreme = false;
						if (itemName.contains("Supreme"))
							supreme = true;
						
						Location loc = entity.getLocation();
						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						Bullet b = new Bullet(dir.multiply(2), shootLoc, true, Particle.SQUID_INK, 120, 0.8f, supreme ? 31 : 8, entity, this);
						
						if (player != null) {
						
							player.setCooldown(Material.STICK, 30);
						}
					}
				} else if (mat == Material.STICK && itemName.contains("Prismatic Wand") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1.6f);
						//}
						
						Location loc = entity.getLocation();
						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						Bullet b = new Bullet(dir.multiply(1), shootLoc, false, Particle.FIREWORK, 60, 0.8f, 9, entity, this);
						
						if (player != null) {
						
							player.setCooldown(Material.STICK, 40);
						}
					}
				} else if (mat == Material.MAGMA_CREAM && itemName.contains("Rage") && var == 0) {				
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MAGMA_CREAM)) {
						
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 0.7f, 1.8f);
						//}
						
						if (itemName.contains("Relentless")) {
							entity.addScoreboardTag("RelentlessRage");
						}
						
						entity.getLocation().getWorld().spawnParticle(Particle.ANGRY_VILLAGER, entity.getLocation(), 10, 1, 0.5, 0.5, 1.5);					
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s speed 30 0 true");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s strength 30 1 true");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s resistance 30 2 true");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s haste 30 1 true");
						
						if (player != null) {
							player.sendMessage(ChatColor.GOLD + "You are now raging!");
							player.setCooldown(Material.MAGMA_CREAM, 50 * 20);
						}
					}
				} else if (mat == Material.NETHER_STAR && itemName.contains("Extra Life") && var == 0) {
					cancelEvent = true;
					if (!player.hasCooldown(Material.NETHER_STAR) && Arena.lives.containsKey(player.getName())) {
						
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1f, 1.6f);
						//}
						
						player.getLocation().getWorld().spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation(), 10, 1, 0.5, 0.5, 0.5);
						
						Arena.lives.put(player.getName(), Arena.lives.get(player.getName()) + 1);
						
						player.sendMessage(ChatColor.GREEN + "You gained another life! You now have " + Arena.lives.get(player.getName()) + " lives.");
						
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.NETHER_STAR, 40);
						}
					}
				} else if (mat == Material.STICK && itemName.contains("Wand of Shooting Stars") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(4, item));
							} 
						}
						
						//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							SFX.play(entity.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1.2f);
							SFX.play(entity.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 1.6f);
						//}
						
						Location loc = entity.getLocation();
						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						Bullet b = new Bullet(dir.multiply(1.2f), shootLoc, false, Particle.END_ROD, 80, 0.6f, 10, entity, this);
						
						if (player != null) {
						
							player.setCooldown(Material.STICK, 8);
						}
					}
				} else if (mat == Material.GOLDEN_HOE && itemName.contains("Staff of the Proletariat") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GOLDEN_HOE)) {
						
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(4, item));
							} 
						}
						
						//GULAG
						
						Location loc = entity.getLocation();
	
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound gulag hostile @a ~ ~ ~ 2 1");

						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						//shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						Bullet b = new Bullet(dir.multiply(1.2f), shootLoc, false, Particle.ANGRY_VILLAGER, 80, 0.7f, 11, entity, this);
						
						if (player != null) {
							player.setCooldown(Material.GOLDEN_HOE, 20 * 40);
						}
					}
				} else if (mat == Material.STICK && itemName.contains("Wand of Disintegration") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						
						
						Location loc = entity.getLocation();
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound laser_sonic hostile @a ~ ~ ~ 2 1");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName() + " positioned " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " run playsound entity.generic.explode hostile @a ~ ~ ~ 10 2");

						Location shootLoc = entity.getEyeLocation().add(offset);
						
						//Vector dir = shootLoc.getDirection();
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						
						//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(right.multiply(0.1D).add(dir.clone().multiply(0.3D)));
	
						//shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1);
						shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1);
						
						if (itemName.contains("Supreme"))
						{
							Bullet b = new Bullet(dir, shootLoc, true, Particle.EXPLOSION, 100, 0.8f, 6, entity, this, 45, 3, 0.1f, 0.1f);
						}
						else if (itemName.contains("Superior")) {
							Bullet b = new Bullet(dir, shootLoc, true, Particle.EXPLOSION, 100, 0.8f, 6, entity, this, 38);
						} else {
							Bullet b = new Bullet(dir, shootLoc, true, Particle.EXPLOSION, 100, 0.8f, 6, entity, this);
						}
						
						if (player != null) {
						
							player.setCooldown(Material.STICK, 40);
						}
					}
				} else if (mat == Material.STONE_SWORD && itemName.contains("Blade of Broken Zehirs") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STONE_SWORD)) {
						List<String> commands = new ArrayList<String>();
						commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem(entity, item, true, 7, 0.2f, commands, Sound.ITEM_TRIDENT_RETURN, Particle.SMOKE);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.STONE_SWORD, 18);
						}
					}
					
				} else if (mat == Material.RAW_IRON && itemName.contains("Grenade") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_IRON)) {
						List<String> commands = new ArrayList<String>();
						commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:4,CustomName:[{text:Grenade}]}");
						//commands.add("summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:4}");
						//commands.add("say Hit!!");
						//throwItem(entity, item, false, 10, 0.2f, commands, Sound.ENTITY_EGG_THROW, Particle.SMOKE);
						throwItem2(entity, item, 10, 1, commands, Sound.ENTITY_GENERIC_EXPLODE, Particle.SMOKE);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.RAW_IRON, 20);
						}
					}
					
				} else if (mat == Material.DIAMOND_SWORD && itemName.contains("Holy Avenger") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.DIAMOND_SWORD)) {
						List<String> commands = new ArrayList<String>();
						commands.add("effect give @s minecraft:weakness 5 1");
						//commands.add("say Hit!!");
						throwItem(entity, item, true, 7, 0.2f, commands, Sound.ITEM_TRIDENT_RETURN, Particle.END_ROD);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.DIAMOND_SWORD, 30);
						}
					}
					
				} else if (mat == Material.NETHERITE_HOE && itemName.contains("Scythe of Death") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.NETHERITE_HOE)) {
						List<String> commands = new ArrayList<String>();
						commands.add("execute at @s run effect give @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,tag=!invulnerable,distance=..5] minecraft:wither 5 45");
						//commands.add("execute at @s run summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:5,CustomName:[{text:DeathScythe}]}");
						//commands.add("effect give @s wither 5 30");
						//commands.add("say Hit!!");
						SFX.play(entity.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 0.5f);
						throwItem(entity, item, true, 52, 1.2f, commands, Sound.ITEM_TRIDENT_RETURN, Particle.EXPLOSION_EMITTER, true);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.NETHERITE_HOE, 60);
						}
					}
					
				}
				else if (mat == Material.LEATHER_HORSE_ARMOR && itemName.contains("Gingerbread")) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.LEATHER_HORSE_ARMOR)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						//List<String> commands = new ArrayList<String>();
						//commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 6, 0.4f, null, Sound.BLOCK_HANGING_ROOTS_BREAK, Particle.CRIT);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.LEATHER_HORSE_ARMOR, 10);
						}
					}
					
					
				} else if (mat == Material.GREEN_DYE && itemName.contains("Spinach") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GREEN_DYE)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(3, item));
							} 
						}
						//List<String> commands = new ArrayList<String>();
						//commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 5, 0.3f, null, Sound.ENTITY_COD_FLOP, Particle.SPLASH);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.GREEN_DYE, 10);
						}
					}
					
					
				} else if ((mat == Material.TRIAL_KEY || mat == Material.TOTEM_OF_UNDYING) && itemName.contains("Jail Key") && var == 0) {
					if (player != null && !player.hasCooldown(mat)) {
						Block block = player.getTargetBlock(null, 5);
						Block block2 = null;
						
						if (block.getType() == Material.IRON_DOOR) {

							if (block.getLocation().clone().subtract(0, 1, 0).getBlock().getType() == Material.IRON_DOOR)
								block2 = block.getLocation().clone().subtract(0, 1, 0).getBlock();
							else if (block.getLocation().clone().add(0, 1, 0).getBlock().getType() == Material.IRON_DOOR)
								block2 = block.getLocation().clone().add(0, 1, 0).getBlock();
							
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(block.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 1, 1);
							
							Openable openable = (Openable) block.getBlockData();
							Openable openable3 = (Openable) block2.getBlockData();
							boolean ogOpen = openable.isOpen();
							boolean ogOpen2 = openable3.isOpen();
		                    openable.setOpen(!ogOpen);
		                    openable3.setOpen(!ogOpen2);
		                    block.setBlockData(openable, false);
		                    block2.setBlockData(openable3, false);
		                    
		                    final Block block3 = block2;
		                    
		                    Bukkit.getScheduler().runTaskLater(this, () -> {
		                    	
		                    	Openable openable2 = (Openable) block.getBlockData();
		                    	Openable openable4 = (Openable) block3.getBlockData();
			                    openable2.setOpen(ogOpen);
			                    openable4.setOpen(ogOpen2);
			                    block.setBlockData(openable2, false);
			                    block3.setBlockData(openable4, false);
			                    
								//for (Player p : Bukkit.getOnlinePlayers())
									SFX.play(block.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1, 1);
					        }, 20*5); // Delay of 5 seconds
							//item.setAmount(item.getAmount()-1);
							player.setCooldown(mat, 30);
						}
					}
				}
				else if ((mat == Material.GOAT_HORN || (mat == Material.TOTEM_OF_UNDYING && itemName.contains("Goat Horn"))) && var == 0) {
					if (player == null || !player.hasCooldown(mat)) {
						
						if (itemName.contains("Wind")) {
							if (test)
								return true;
							//entity.getWorld().spawnEntity(entity.getEyeLocation().add(entity.getEyeLocation().getDirection()), )
							WindCharge fb = entity.getWorld().spawn(entity.getEyeLocation().add(dir), WindCharge.class);
							//fb.setFireTicks(5);
							//fb.setIsIncendiary(true);
							//fb.setYield(80);
							fb.setMetadata("Power", new FixedMetadataValue(this, 10));
							fb.setShooter(entity);
							fb.setVelocity(dir.multiply(2.5f));
							EntityKiller k = new EntityKiller(fb, 80);
							//item.setAmount(item.getAmount()-1);
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.ENTITY_WIND_CHARGE_THROW, 1f, 1f);
						} else if (itemName.contains("Seek")) {
							if (test)
								return true;
							LivingEntity nearest = null;
							double dist = 1000;
							for (Entity e : entity.getNearbyEntities(100, 100, 100)) {
								if (e instanceof LivingEntity le && le.getScoreboardTags().contains("FancyMob")) {
									if (nearest == null || le.getLocation().distance(entity.getLocation()) < dist)
									{
										nearest = le;
										dist = le.getLocation().distance(entity.getLocation());
										if (dist < 20) {
											break;
										}
									}
								}
							}
							if (nearest != null) {
								Vector dir2 = nearest.getLocation().subtract(entity.getLocation()).toVector().normalize();
								Location newLoc = entity.getLocation();
								newLoc.setDirection(dir2);
								prepare_teleport(entity);
								//if (entity.getPassengers().size() > 0)
								//	entity.removePassenger(entity.getPassengers().get(0));
								entity.teleport(newLoc);
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + nearest.getWorld().getName().strip().toLowerCase() + " positioned " + nearest.getLocation().getX() + " " + nearest.getLocation().getY() + " " + nearest.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s glowing 5 1");
								
							} else if (player != null) {
								player.sendMessage(ChatColor.RED + "No nearby entities found");
							}
						} else if (itemName.contains("Dream")) {
							if (test)
								return true;
							Bullet b = new Bullet(dir.multiply(1.2f), entity.getEyeLocation(), false, Particle.NOTE, 80, 0.7f, 27, entity, this, 0, 6, 0.8f, 0.3f);
							
						} else if (itemName.contains("Call")) {
							for (Entity e : entity.getNearbyEntities(20, 20, 20)) {
								if (e instanceof LivingEntity le && le.getScoreboardTags().contains("FancyMob")) {
									le.setVelocity(entity.getLocation().subtract(le.getLocation()).toVector());
								}
							}
						}
						
						if (player != null)
							player.setCooldown(mat, 5*20);
					}
				}
				else if (mat == Material.RAW_COPPER && itemName.contains("Badger Claw") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						boolean success = false;
						if (entity.getEyeLocation().getBlock().isPassable()) {
							if (!entity.getEyeLocation().add(entity.getEyeLocation().multiply(1.5)).getBlock().isPassable()) {
								entity.teleport(entity.getLocation().add(entity.getEyeLocation().getDirection().multiply(1.5)));
								success = true;
							} else if (!entity.getLocation().add(new Vector(0, -1, 0)).getBlock().isPassable()) {
								entity.teleport(entity.getLocation().add(new Vector(0, -1, 0)));
								success = true;
							} 
						} else {
							entity.teleport(entity.getLocation().add(entity.getEyeLocation().getDirection().multiply(1.5)));
							success = true;
						}
						if (success) {
							//for (Player p : Bukkit.getOnlinePlayers()) {
								SFX.play(entity.getLocation(), Sound.BLOCK_ROOTED_DIRT_BREAK, 1, 1);
							//}
							entity.getWorld().spawnParticle(Particle.BLOCK, entity.getLocation(), 30, 0.5, 1, 1, 1, Material.DIRT.createBlockData());
							player.setCooldown(Material.RAW_COPPER, 5);
						}
					}
				}
				else if (mat == Material.RAW_COPPER && itemName.contains("Owl Fly") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0, true, false));
						if (player.getVelocity().dot(player.getLocation().getDirection()) < 0.2) {
							player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(0.5)));
						}
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2, 2);
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 7);
					}
				}
				else if (mat == Material.RAW_COPPER && itemName.contains("Owl Fly") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 0, true, false));
						if (player.getVelocity().dot(player.getLocation().getDirection()) < 0.2) {
							player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(0.5)));
						}
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2, 2);
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 7);
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("Quetzal Fly") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 14, 0, true, false));
						if (player.getVelocity().dot(player.getLocation().getDirection()) < 0.4) {
							player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection()));
						}
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.2f, 1.2f);
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 10);
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("Deinonychus Bite") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						//player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 14, 0, true, false));
						
						player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(3).add(new Vector(0, 0.7f, 0))));
						
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_CAT_HISS, 0.9f, 0.9f);
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 30);
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("Dire Wolf Bite") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						//player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 14, 0, true, false));
						
						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*5, 0, true, false));
						player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*5, 2, true, false));
						player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20*5, 4, true, false));
						player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20*5, 0, true, false));
						player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20*5, 1, true, false));
						//player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(3).add(new Vector(0, 0.7f, 0))));
						
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(player.getLocation(), Sound.ENTITY_WOLF_HOWL, 0.9f, 0.9f);
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 20*10);
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("T-Rex Tail") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						for (Polymorph p : polymorphs) {
							if (p.player == player) {
								p.attack2 = true;
								break;
							}
						}
						//player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 14, 0, true, false));
						
						Location loc = player.getEyeLocation().subtract(player.getEyeLocation().getDirection().multiply(3));
						for (Entity e : loc.getWorld().getNearbyEntities(loc, 2, 2, 2)) {
							if (e instanceof LivingEntity le && Arena.validTarget(le)) {
								le.damage(10, player);
							}
						}
						
						//player.setVelocity(player.getVelocity().add(player.getEyeLocation().getDirection().multiply(3).add(new Vector(0, 0.7f, 0))));
						
						//for (Player p : Bukkit.getOnlinePlayers())
							SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.3f, 1.3f);
						//player.setVelocity(player.getVeloci);
						player.getWorld().spawnParticle(Particle.EXPLOSION, loc, 1);
						player.setCooldown(Material.RAW_COPPER, 25);
					}
				} else if ((mat == Material.RAW_COPPER && itemName.contains("Quetzal Bite") || itemName.contains("T-Rex Bite")) && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player != null && !player.hasCooldown(Material.RAW_COPPER)) {
						if (grapple.containsKey(entity)) {
							if (grapple.get(entity).isValid())
								grapple.get(entity).removeScoreboardTag("Grapple");
							grapple.remove(entity);
						}
						else {
							for (Polymorph p : polymorphs) {
								if (p.player == player) {
									p.attack = true;
									break;
								}
							}
							Location biteLoc = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(3.5f));
							
							for (Entity e : biteLoc.getWorld().getNearbyEntities(biteLoc, 2, 2, 2)) {
								if (e instanceof LivingEntity le && Arena.validTarget(le) && entity != le && entity.getLocation().distance(le.getLocation()) > 0.5f) {
									le.addScoreboardTag("Grapple");
									grapple.put(entity, le);
									break;
								}
							}
							
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 0.9f, 0.9f);
						}
						//player.setVelocity(player.getVeloci);
						player.setCooldown(Material.RAW_COPPER, 14);
					}
				}
				else if (mat == Material.WHEAT && itemName.contains("Fries") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.WHEAT)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(5, item));
							} 
						}
						//List<String> commands = new ArrayList<String>();
						//commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 4, 0.3f, null, Sound.BLOCK_GRASS_BREAK, Particle.CRIT);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.WHEAT, 5);
						}
					}
					
					
				}else if (mat == Material.SUNFLOWER && itemName.contains("Sugar Cookie") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.SUNFLOWER)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(5, item));
							} 
						}
						List<String> commands = new ArrayList<String>();
						commands.add("effect give @s levitation 5 3");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 4, 0.3f, commands, Sound.BLOCK_HANGING_ROOTS_BREAK, Particle.CRIT);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.SUNFLOWER, 5);
						}
					}
					
					
				}else if (mat == Material.NAUTILUS_SHELL && itemName.contains("Escargot") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.NAUTILUS_SHELL)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(1, item));
							} 
						}
						List<String> commands = new ArrayList<String>();
						commands.add("effect give @s slowness 6 6");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 5, 0.3f, commands, Sound.ENTITY_GUARDIAN_FLOP, Particle.SPLASH);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.NAUTILUS_SHELL, 15);
						}
					}
					
					
				}else if (mat == Material.LIGHT_GRAY_CANDLE && itemName.contains("Beer Can") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.LIGHT_GRAY_CANDLE)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(5, item));
							} 
						}
						//List<String> commands = new ArrayList<String>();
						//commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 6, 0.3f, null, Sound.BLOCK_ANVIL_LAND, Particle.CRIT);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.LIGHT_GRAY_CANDLE, 8);
						}
					}
					
					
				}else if (mat == Material.SUGAR_CANE && itemName.contains("Frog Legs") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.SUGAR_CANE)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(5, item));
							} 
						}
						//List<String> commands = new ArrayList<String>();
						//commands.add("effect give @s minecraft:wither 5 1");
						//commands.add("say Hit!!");
						throwItem2(entity, item, 4, 0.3f, null, Sound.BLOCK_GRASS_BREAK, Particle.CRIT);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.SUGAR_CANE, 5);
						}
					}
					
					
				}
				else if (mat == Material.STICK && itemName.contains("Wand of Firebolts") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(4, item));
							} 
						}
						
						SmallFireball sfb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), SmallFireball.class);
						sfb.setFireTicks(20);
						sfb.setShooter(entity);
						sfb.setVelocity(dir.multiply(3.5f));
						EntityKiller k = new EntityKiller(sfb, 50);
						//item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.9f, 1.4f);
	
						if (player != null)
							player.setCooldown(Material.STICK, 3); // 0.15 second cooldown
					}
				} else if (mat == Material.STICK && itemName.contains("Staff of Fireballs") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(4, item));
							} 
						}
						
						Fireball fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), Fireball.class);
						fb.setFireTicks(5);
						fb.setIsIncendiary(true);
						fb.setYield(2);
						fb.setShooter(entity);
						fb.setVelocity(dir.multiply(1.5f));
						EntityKiller k = new EntityKiller(fb, 80);
						//item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.9f, 1f);
	
						if (player != null)
							player.setCooldown(Material.STICK, 6);
					}
				} else if (mat == Material.STICK && itemName.contains("Staff of Deadly Fireballs") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						
						Fireball fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), Fireball.class);
						fb.setFireTicks(20);
						fb.setIsIncendiary(true);
						fb.setYield(3);
						fb.setShooter(entity);
						fb.setVelocity(dir.multiply(1.5f));
						EntityKiller k = new EntityKiller(fb, 80);
						//item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 0.9f, 1f);
	
						if (player != null)
							player.setCooldown(Material.STICK, 10);
					}
				} else if (mat == Material.STICK && itemName.contains("Staff of Super Deadly Fireballs") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
						for (int i = 0; i < 3; i++) {
							Fireball fb = entity.getWorld().spawn(entity.getEyeLocation().add(dir.clone().add(right.clone().multiply(1-i))), Fireball.class);
							fb.setFireTicks(40);
							fb.setIsIncendiary(true);
							fb.setYield(6);
							fb.setShooter(entity);
							fb.setVelocity(dir.clone().add(right.clone().multiply(0.2f-i*0.2f)).normalize().multiply(3.5f));
							EntityKiller k = new EntityKiller(fb, 80);
						}
						//item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
	
						if (player != null)
							player.setCooldown(Material.STICK, 10);
					}
				} else if (mat == Material.BLAZE_POWDER && itemName.contains("Fire Breath") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.BLAZE_POWDER)) {
						//fireBreath(player);
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(3, item));
							} 
						}
						//firebreath.put(entity, 10);
						if (itemName.contains("Supreme")) {
							firebreath4(entity);
						}
						else if (itemName.contains("Superior"))
							firebreath3(entity);
						else
							firebreath2(entity);
						if (player != null)
							player.setCooldown(Material.BLAZE_POWDER, 10); // 0.5 second cooldown
					}
					//item.setAmount(item.getAmount()-1);
				} else if (mat == Material.GOLDEN_HOE && itemName.contains("Saxophone") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GOLDEN_HOE)) {
						//fireBreath(player);
						jammable = true;
						//firebreath.put(entity, 10);
						int saxLvl = 0;
						if (itemName.contains("Supreme"))
							saxLvl = 2;
						else if (itemName.contains("Superior"))
							saxLvl = 1;
						saxophone(entity, saxLvl);
						if (player != null)
							player.setCooldown(Material.GOLDEN_HOE, 30); // 0.5 second cooldown
					}
					//item.setAmount(item.getAmount()-1);
				}
				else if (mat == Material.SLIME_BALL && itemName.equals("Shield") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.SLIME_BALL)) {
						Bullet b = new Bullet(player.getLocation().getDirection().multiply(2), player.getLocation().add(new Vector(0.0D, 2D, 0.0D)), false, Particle.FIREWORK, 50, 1, 0, player, this);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1f, 1f);
						if (player != null) {
							item.setAmount(item.getAmount()-1);
						
							player.setCooldown(Material.SLIME_BALL, 10); // 0.5 second cooldown
						}
					}
				} else if (mat == Material.STONE_HOE && itemName.contains("Royal Pistol") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STONE_HOE)) {
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireRoyalPistolBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.STONE_HOE, 9); // 0.5 second cooldown
					}
				} else if (mat == Material.GOLDEN_HOE && itemName.contains("Dalek Gunstick") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GOLDEN_HOE)) {
						ItemStack i = player != null ? findInventoryItem(player, Material.NETHER_WART) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							if (itemName.contains("Explosive"))
								fireExplosiveDalekBullet(entity, itemName.contains("Ludicrous") ? 45 : 35);
							else if (itemName.contains("Ludicrous")) {
								fireDalekBullet(entity, 45);
							} else {
								fireDalekBullet(entity, 29);
							}
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires antimatter shells)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.GOLDEN_HOE, 20); 
					}
				} else if (mat == Material.GUNPOWDER && itemName.contains("Dalek Shield") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GUNPOWDER)) {
						dalekShield.put(entity, 200);
						entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 200));
						if (player != null)
							player.setCooldown(Material.GUNPOWDER, 20*20); 
					}
				} else if (mat == Material.RAW_COPPER && itemName.contains("Dalek Thruster") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_COPPER)) {
						dalekThruster.put(entity, 80);
						if (player != null)
							player.setCooldown(Material.RAW_COPPER, 10*20); 
					}
				}
				else if (mat == Material.STONE_HOE && itemName.contains("Pistol") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.STONE_HOE)) {
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							jammable = true;
							if (i != null)
								i.setAmount(i.getAmount()-1);
							firePistolBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.STONE_HOE, 10); // 0.5 second cooldown
					}
				} else if (mat == Material.IRON_HOE && itemName.contains("Super Heavy Musket") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							jammable = true;
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireSuperHeavyBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.IRON_HOE, 60); // 3 second cooldown
					}
				} else if (mat == Material.IRON_HOE && itemName.contains("Heavy Musket") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							jammable = true;
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireHeavyBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.IRON_HOE, 60); // 3 second cooldown
					}
				} else if ((mat == Material.TOTEM_OF_UNDYING || mat == Material.BREEZE_ROD) && itemName.contains("Magic Missiles")) {
					int level = 0;
					if (itemName.contains("Superior")) {
						level = 3;
					} else if (itemName.contains("Supreme")) {
						level = 5;
					} else if (itemName.contains("Ultra")) {
						level = 14;
					}
					else if (itemName.contains("Staff")) {
						level = 1;
					}
					if (var == 0) {
						if (test)
							return true;
						cancelEvent = true;
						if (player == null || !player.hasCooldown(mat)) {
							magicMissile(entity, level, false);
							if (player != null)
								player.setCooldown(mat, 50);
						}
					} else if (var == 1) {
						if (test)
							return true;
						cancelEvent = true;
						if (player == null || !player.hasCooldown(mat)) {
							jammable = true;
							magicMissile(entity, level, true);
							if (player != null)
								player.setCooldown(mat, 50);
						}
					}
				}
				else if (mat == Material.MAGMA_BLOCK && itemName.contains("Meteor Swarm") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.MAGMA_BLOCK)) {
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(2, item));
							} 
						}
						
						launchMeteor(entity);
						
						if (player != null) {
							item.setAmount(item.getAmount()-1);
							player.setCooldown(Material.MAGMA_BLOCK, 100);
						}
					}
				}
				else if (mat == Material.RAW_IRON && itemName.contains("Shotgun") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_IRON)) {
						jammable = true;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireShotgun(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.RAW_IRON, 60); // 3 second cooldown
					}
				} else if (mat == Material.IRON_HOE && itemName.contains("Musket") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
						jammable = true;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireRifleBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.IRON_HOE, 30); // 1.5 second cooldown
					}
				} else if (mat == Material.RAW_IRON && itemName.contains("9mm Pistol") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_IRON)) {
						jammable = true;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fire9mmBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.RAW_IRON, 8);
					}
				} else if (mat == Material.RAW_IRON && itemName.contains("Revolver") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.RAW_IRON)) {
						jammable = true;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (player == null || i != null) {
							if (i != null)
								i.setAmount(i.getAmount()-1);
							fireRevolverBullet(entity);
						} else {
							if (player != null)
								player.sendMessage("No ammo (requires iron nuggets)");
							//for (Player p : Bukkit.getServer().getOnlinePlayers())
								SFX.play(entity.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.RAW_IRON, 12);
					}
				} else if (mat == Material.GLASS_BOTTLE && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.GLASS_BOTTLE)) {
						if (player != null) item.setAmount(item.getAmount()-1);
						
						throwBottle(entity);
					}
				}
				else if (mat == Material.CARROT_ON_A_STICK && itemName.contains("47") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.CARROT_ON_A_STICK)) {
						boolean canShoot = false;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (i != null) {
							jammable = true;
							canShoot = true;
							i.setAmount(i.getAmount()-1);
						}
						if (player == null || canShoot) {
							//player.addScoreboardTag("fire_bullet");
							//player.sendMessage("BAM!");
							fireBullet(entity);
						} else if (player != null) {
							player.sendMessage("No ammo (requires iron nuggets)");
							SFX.play(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						if (player != null)
							player.setCooldown(Material.CARROT_ON_A_STICK, 1); // 0.05 second cooldown
					}
				} else if (mat == Material.IRON_HOE && itemName.contains("47") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.IRON_HOE)) {
						boolean canShoot = false;
						ItemStack i = player != null ? findInventoryItem(player, Material.IRON_NUGGET) : null;
						if (i != null) {
							jammable = true;
							canShoot = true;
							i.setAmount(i.getAmount()-1);
						}
						if (player == null || canShoot) {
							//player.addScoreboardTag("fire_bullet");
							//player.sendMessage("BAM!");
							fireBullet(entity);
						} else if (player != null) {
							player.sendMessage("No ammo (requires iron nuggets)");
							SFX.play(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1f, 1f);
						}
						
						if (player != null)
							player.setCooldown(Material.IRON_HOE, 1); // 0.05 second cooldown
					}
				} else if (mat == Material.LIGHTNING_ROD && itemName.contains("Wand of Storms") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.LIGHTNING_ROD)) {
						jammable = true;
						Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 80, 1, 3, entity, this);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1f);
						
						if (player != null)
							player.setCooldown(Material.LIGHTNING_ROD, 60); // 3 second cooldown
					}
				} else if (mat == Material.LIGHTNING_ROD && itemName.contains("Lightning Wand") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.LIGHTNING_ROD)) {
						jammable = true;
						Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 120, 0.2f, 3, entity, this, 10);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1f);
						
						if (player != null)
							player.setCooldown(Material.LIGHTNING_ROD, 40); // 2 second cooldown
					}
				} else if (mat == Material.LIGHTNING_ROD && (itemName.contains("Wand of Chain Lightning") || itemName.contains("Wrath of the Stormlord")) && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.LIGHTNING_ROD)) {
						jammable = true;
						if (itemName.contains("Stormlord")) {
							Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 180, 0.2f, 24, entity, this, 50, 3, 0.5f, 0.1f);
						}
						else if (itemName.contains("Supreme")) {
							Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 160, 0.2f, 24, entity, this, 32, 3, 0.5f, 0.1f);
						}
						else if (itemName.contains("Superior")) {
							Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 160, 0.2f, 24, entity, this);
						} else {
							Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 160, 0.2f, 7, entity, this);
						}
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1f);
						
						if (player != null)
							player.setCooldown(Material.LIGHTNING_ROD, 40); // 2 second cooldown
					}
				} else if (mat == Material.IRON_SWORD && itemName.contains("Dagger of Pouncing") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.IRON_SWORD)) {
						entity.setVelocity(entity.getEyeLocation().getDirection().multiply(3).add(new Vector(0, 0.6f, 0)));
						entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 10));
						if (player != null)
							player.setCooldown(Material.IRON_SWORD, 60); // 2 second cooldown
					}
				}
				else if (mat == Material.STICK && itemName.contains("Wand of Thunderstep") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.STICK)) {
						jammable = true;
						Bullet b = new Bullet(dir.multiply(1), entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)), true, Particle.FIREWORK, 80, 0.6f, 4, entity, this, 7);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1f, 1.1f);
						
						if (player != null)
							player.setCooldown(Material.STICK, 60); // 3 second cooldown
					}
				} else if (mat == Material.PRISMARINE_SHARD && itemName.contains("Throwing Dart") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					
					if (player == null || !player.hasCooldown(Material.PRISMARINE_SHARD)) {
						jammable = true;
						if (player == null) {
							if (!rightClicks.containsKey(entity)) {
								rightClicks.put(entity, new Tuple<Integer, ItemStack>(3, item));
							} 
						}
						
						boolean supreme = itemName.contains("Supreme");
						
						Arrow fb = entity.getWorld().spawn(entity.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(dir), Arrow.class);
						//fb.setFireTicks(20);
						//fb.setIsIncendiary(true);
						//fb.setYield(3);
						fb.setBasePotionType(PotionType.STRONG_POISON);
						if (supreme) {
							fb.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, 10, 10), true);
							fb.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 10), true);
							fb.addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 10), true);
							fb.addCustomEffect(new PotionEffect(PotionEffectType.DARKNESS, 10, 10), true);
						}
						fb.setShooter(entity);
						fb.setVelocity(dir.multiply(supreme ? 7f : 4f));
						fb.setPierceLevel(supreme ? 4 : 2);
						fb.setPickupStatus(PickupStatus.DISALLOWED);
						fb.setDamage(supreme ? 10 : 4);
						
						EntityKiller k = new EntityKiller(fb, 80);
						item.setAmount(item.getAmount()-1);
						//for (Player p : Bukkit.getServer().getOnlinePlayers())
							SFX.play(entity.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1f, 1f);
						if (player != null)
							player.setCooldown(Material.PRISMARINE_SHARD, 5); // 0.25 second cooldown
					}
					
				} else if (mat == Material.SPECTRAL_ARROW && itemName.contains("Arrow Barrage") && var == 0) {
					if (test)
						return true;
					cancelEvent = true;
					if (player == null || !player.hasCooldown(Material.SPECTRAL_ARROW)) {
						if (itemName.contains("Supreme"))
							supremeArrowBarrage.put(entity, 15);
						else
							arrowBarrage.put(entity, 10);
						jammable = true;
						if (player != null)
							player.setCooldown(Material.SPECTRAL_ARROW, 60); // 3 second cooldown;
					}
				}
			}
		}
		
		if (jammable && Math.random() < 0.06 && player != null && (item.getType() == Material.STICK || item.getType() == Material.BREEZE_ROD || item.getType() == Material.BLAZE_ROD) && player.hasCooldown(item.getType()) && player.getCooldown(item.getType()) < 40 * 20) {
			player.setCooldown(item.getType(), Math.max(player.getCooldown(item.getType()) * 2, Math.max(player.getCooldown(item.getType()), 30*20)));
			//for (Player p : Bukkit.getOnlinePlayers()) {
				SFX.play(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.4f, 1.4f);
			//}
		}
		
		return cancelEvent;
	}


	public static void HealSpell(LivingEntity le, int amount) {
		le.setHealth(Math.min(le.getHealth() + amount, le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
		if (amount >= 6) {
			le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*2, 2));
			le.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*2, 0));
			le.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*2, 0));
		} else if (amount >= 4) {
			le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*2, 0));
		}
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers())
			SFX.play(le.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);
		//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
		le.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, le.getEyeLocation(), 15, 0.5, 1, 1, 1);
		le.getWorld().spawnParticle(Particle.HEART, le.getEyeLocation(), 15, 0.5, 1, 1, 1);
	}
	
	private void magicMissile(LivingEntity entity, int level, boolean melee) {
		ArrayList<LivingEntity> targets = new ArrayList<LivingEntity>();
		
		double range = 40;
	    int numMissiles = level + 3;
		
		// Get the direction the entity is looking at
	    Vector direction = entity.getEyeLocation().getDirection().normalize();

	    // Loop through all nearby entities
	    if (melee) {
	    	Collection<Entity> nearbyEntities = entity.getWorld().getNearbyEntities(entity.getLocation(), range/2, range/2, range/2);
	    	for (Entity nearbyEntity : nearbyEntities) { // Adjust radius as needed
		        if (nearbyEntity instanceof LivingEntity le && le != entity && Arena.validTarget(le)) {
		        	
		                targets.add(le);
	
		                // Limit to a maximum of 5 targets
		                if (targets.size() >= numMissiles) {
		                    break;
		                }
		            
		        }
		    }
	    } else {
		    Collection<Entity> nearbyEntities = entity.getWorld().getNearbyEntities(entity.getLocation().add(direction.clone().multiply(range/2)), range, range, range);
		    for (Entity nearbyEntity : nearbyEntities) { // Adjust radius as needed
		        if (nearbyEntity instanceof LivingEntity le && le != entity && Arena.validTarget(le)) {
		        	
		            // Get vector from the entity to the target
		            Vector toTarget = le.getLocation().toVector().subtract(entity.getLocation().toVector()).normalize();
	
		            // Calculate the angle between the direction and the target
		            double angle = direction.angle(toTarget);
	
		            // Check if the angle is within 10 degrees (converted to radians)
		            if (Math.toDegrees(angle) <= 10) {
		                targets.add(le);
	
		                // Limit to a maximum of 5 targets
		                if (targets.size() >= numMissiles) {
		                    break;
		                }
		            }
		        }
		    }
	    }
		
	    if (targets.size() > 0) {
	    	double damage = ((level*level)/1.4+1.3)*3.3;
	    	for (int i = 0; i < numMissiles; i++) {
	    		Vector randDir = (new Vector(Math.random()-0.5, Math.random()-0.5, Math.random()-0.5)).normalize();
	    		Bullet missile = new Bullet(randDir.multiply(Math.random()+0.5), entity.getEyeLocation().add(randDir), false, Particle.END_ROD, 50, 0.2f, 25, entity, this, (int)damage);
	    		missile.target = targets.get(i%targets.size());
	    		/*
	    		ShulkerBullet missile = (ShulkerBullet) entity.getWorld().spawn(entity.getEyeLocation().add(direction), ShulkerBullet.class);
	    		missile.setInvulnerable(true);
	    		missile.setShooter(entity);
	    		missile.setTarget(targets.get(i%targets.size()));
	    		magicMissiles.put(missile, damage);
	    		*/
	    	}
	    	//for (Player player : Bukkit.getOnlinePlayers())
	    		SFX.play(entity.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 1.5f, 11.5f);
	    } else if (entity instanceof Player player){
	    	player.sendMessage(ChatColor.RED + "Please aim at a target to cast Magic Missile");
	    }
	}
	
	public void repulsorLaunch(LivingEntity le) {
		Location loc = le.getLocation();
		if (!le.getScoreboardTags().contains("RepulsorHover")) { // Hover Case
			if (repulsorFlying.contains(le))
				repulsorFlying.remove(le);
			le.addScoreboardTag("RepulsorHover");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " as @e[limit=1,sort=nearest,type=!item_frame,type=!glow_item_frame,type=!painting,type=!armor_stand] run effect give @s levitation 3 0 true");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " as @e[limit=1,sort=nearest,type=!item_frame,type=!glow_item_frame,type=!painting,type=!armor_stand] run effect give @s slow_falling 8 10 true");
			
			for (Player pl : le.getWorld().getPlayers())
	    		pl.playSound(le.getLocation(), Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1, 0.8f);
		} else {
			// Fly case
			le.removeScoreboardTag("RepulsorHover");
			le.setGliding(true);
			repulsorFlying.add(le);
			le.setVelocity(le.getVelocity().add(loc.getDirection().multiply(2)));
		}
	}
	
	public boolean enableRepulsorFly(LivingEntity le) {
		if (!repulsorFly.contains(le)) {
			repulsorFly.add(le);
			if (le instanceof Player p)
				p.setAllowFlight(true);
			return true;
		}
		return false;
	}
	
	void explode(Location location, float radius, float damage, boolean fire, LivingEntity source) {
	    World world = location.getWorld();
	    world.createExplosion(location, radius, fire, false); // Strong explosion, no fire, no block damage

	    location.getWorld().spawnParticle(Particle.LAVA, location, 50, radius/2, radius/2, radius/2);
	    location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 2, 0, 0, 0);
	    
	    for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
	        if (entity instanceof LivingEntity le && Arena.validTarget(le)) {
	        	Bullet.damage(le, damage, source, Bullet.ELEMENTAL);
	            //((LivingEntity) entity).damage(damage); // High damage
	        }
	    }
	}
	
	public void launchMeteor(LivingEntity le) {
		
	    World world = le.getWorld();
	    Location launchLocation = le.getEyeLocation().add(le.getLocation().getDirection().multiply(3.5));
	    
	    // Spawn the falling block
	    FallingBlock magmaBomb = world.spawnFallingBlock(launchLocation, Material.MAGMA_BLOCK.createBlockData());
	    magmaBomb.setDropItem(false);
	    magmaBomb.setHurtEntities(true);
	    magmaBomb.setGravity(true);
	    magmaBomb.setVelocity(le.getLocation().getDirection().multiply(1.5));

	    // Tag it so we can identify it later
	    magmaBomb.setMetadata("Shooter", new FixedMetadataValue(this, le));
	    magmaBomb.addScoreboardTag("Meteor");

	    // Prevent placement
	    magmaBomb.setTicksLived(1); // Prevent block from placing when it hits ground
	    
	    meteors.add(magmaBomb);
	}
	
	@EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity().getScoreboardTags().contains("Meteor")) {
            event.setCancelled(true); // Prevent placing magma block
            explode(event.getEntity().getLocation(), 5, 45, true, (LivingEntity)event.getEntity().getMetadata("Shooter").get(0).value());
        }
    }
	
	public boolean disableRepulsorFly(LivingEntity le) {
		if (repulsorFly.contains(le)) {
			repulsorFly.remove(le);
			if (le instanceof Player p)
				p.setAllowFlight(false);
			return true;
		}
		return false;
	}
	
    @EventHandler
    public void onPlayerDoubleTap(PlayerToggleFlightEvent event){
        Player p = event.getPlayer();
 
        //if (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
            //em.launchPlayer(2, pl);
        
        //}
 
        if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {
	        if (repulsorFly.contains(p)) {
	        	
	        	repulsorLaunch(p);
	        	
	            event.setCancelled(true);
	        } else if (durrithWings.containsKey(p)) {
	        	p.setVelocity(p.getVelocity().add(p.getEyeLocation().getDirection().multiply(0.6)));
	        	p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10, 1, true, false));
	        	p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6*20, 0, true, false));
	        	for (Player a : Bukkit.getOnlinePlayers())
	        		a.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.1f, 1.1f);
	        	event.setCancelled(true);
	        } else if (doubleJump.contains(p.getName()) || (wallJump.contains(p.getName()) && isNextToWall(p, true))) {
	        	if (canDoubleJump.contains(p))
	        		canDoubleJump.remove(p);
	        	event.setCancelled(true);
	            p.setAllowFlight(false);
	            //doubleJumped.add(player.getUniqueId());

	            // Launch the player upward and slightly forward
	            Vector jump = p.getLocation().getDirection().multiply(0.5);
	            p.setVelocity(p.getVelocity().add(jump).setY(0.9f));
	            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 2f);
	        }
        }
    }
	public void counterspell(LivingEntity entity, Vector direction) {
		// TODO Auto-generated method stub
		Location loc = entity.getEyeLocation().clone().add(direction.normalize());
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run playsound counterspell hostile @a ~ ~ ~ 1 1");
		
		Vector vel = direction.clone().normalize().multiply(3);
		Vector sizer = new Vector(3, 3, 3);
		Vector minLoc = loc.toVector().clone().subtract(sizer);
		Vector maxLoc = loc.toVector().clone().add(sizer);
		
		for (int i = 0; i < 5; i++) {
			antimagic.put(new Tuple<BoundingBox, World>(new BoundingBox(minLoc.getX(), minLoc.getY(), minLoc.getZ(), maxLoc.getX(), maxLoc.getY(), maxLoc.getZ()), loc.getWorld()), 15);
			minLoc = minLoc.add(vel);
			maxLoc = maxLoc.add(vel);
		}
	}
	
	public void hypnoticPattern(Location loc, LivingEntity owner) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run playsound hypnotic hostile @a ~ ~ ~ 1 1");
		
		Vector sizer = new Vector(5, 5, 5);
		Vector minLoc = loc.toVector().clone().subtract(sizer);
		Vector maxLoc = loc.toVector().clone().add(sizer);
		
		hypnotic.put(new Triple<BoundingBox, World, LivingEntity>(new BoundingBox(minLoc.getX(), minLoc.getY(), minLoc.getZ(), maxLoc.getX(), maxLoc.getY(), maxLoc.getZ()), loc.getWorld(), owner), 60);
	}
	
	public boolean spawnDurrithWings(LivingEntity entity) {
		if (durrithWings.containsKey(entity) && durrithWings.get(entity).isValid()) {
			return false;
		}
		
		if (entity instanceof Player p) {
			p.sendMessage("Flight enabled");
			p.setAllowFlight(true);
		}
		
		ArmorStand wings = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
		
		wings.setVisible(false);
		wings.setInvulnerable(true);
		wings.setMarker(true);
		wings.setGravity(false);
		wings.addScoreboardTag("DurrithWings");
		wings.addScoreboardTag("invulnerable");
		wings.setArms(true);
		wings.setRightArmPose(new EulerAngle(0, 0, 0));
		wings.setSmall(true);
		
		ItemStack wingModel = new ItemStack(Material.RAW_COPPER);
		ItemMeta meta = wingModel.getItemMeta();
		meta.setCustomModelData(14);
		wingModel.setItemMeta(meta);
		wings.getEquipment().setHelmet(wingModel, true);
		
		riding.put(wings, entity);
		entity.addPassenger(wings);
		
		durrithWings.put(entity, wings);
		
		return true;
	}
	
	public boolean spawnSentryMode(LivingEntity entity) {
		if (sentryMode.containsKey(entity)) {
			Entity g = sentryMode.get(entity);
			sentryMode.remove(entity);
			if (g != null && g.isValid())
				g.remove();
		}
		
		ArmorStand gun = (ArmorStand) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
		
		gun.setVisible(false);
		gun.setInvulnerable(true);
		gun.setMarker(true);
		gun.setGravity(false);
		gun.addScoreboardTag("SentryMode");
		gun.addScoreboardTag("invulnerable");
		gun.setArms(true);
		gun.setRightArmPose(new EulerAngle(0, 0, 0));
		gun.setSmall(true);
		gun.setMetadata("owner", new FixedMetadataValue(this, entity));
		
		ItemStack cannon = new ItemStack(Material.RAW_COPPER);
		ItemStack stand = new ItemStack(Material.RAW_COPPER);
		ItemMeta meta = cannon.getItemMeta();
		meta.setCustomModelData(4);
		cannon.setItemMeta(meta);
		meta = stand.getItemMeta();
		meta.setCustomModelData(5);
		stand.setItemMeta(meta);
		gun.getEquipment().setHelmet(cannon, true);
		gun.getEquipment().setItemInMainHand(stand);
		
		riding.put(gun, entity);
		entity.addPassenger(gun);
		
		sentryMode.put(entity, gun);
		
		return true;
	}
	
	public boolean isInate(ItemStack item) {
		String itemName = "";
		if (item != null && item.getAmount() > 0 && item.hasItemMeta() && item.getItemMeta().hasDisplayName())
		{
			itemName = item.getItemMeta().getDisplayName();
			if (itemName.contains("Stalin's Head") || itemName.contains("MetalMythic Head") || itemName.contains("Cold Helmet") || itemName.contains("Omega Helmet") || itemName.contains("Durrith Head")  || itemName.contains("Knope Head") || (itemName.contains("Stalin") && itemName.contains("Head")))
				return true;
			if (itemName.contains("Chronal Shift") || itemName.contains("Convergent Future") || itemName.contains("Rage") || itemName.contains("Fire Breath") || itemName.contains("Wildshape") || itemName.contains("Wings") || itemName.contains("Repulsor Flight"))
				return true;
		}
		return false;
	}

	public void storeInventoryInMinecarts(Player player) {
        Location loc = player.getLocation();

        // Spawn two minecart chests
        StorageMinecart chest1 = (StorageMinecart) loc.getWorld().spawn(loc, StorageMinecart.class);
        StorageMinecart chest2 = (StorageMinecart) loc.getWorld().spawn(loc.add(0, 1, 0), StorageMinecart.class);

        // Get inventories of the minecart chests
        Inventory inv1 = chest1.getInventory();
        Inventory inv2 = chest2.getInventory();

        // Player inventory
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        ItemStack[] hotbarContents = new ItemStack[9];
        ItemStack[] storageContents = new ItemStack[27];

        // Separate hotbar and main storage inventory
        for (int i = 0; i < 9; i++) {
            hotbarContents[i] = player.getInventory().getItem(i);
        }
        System.arraycopy(player.getInventory().getStorageContents(), 9, storageContents, 0, 27);
        
        ArrayList<ItemStack> innateItems = new ArrayList<>();
        
        // Store armor and hotbar in the first chest
        for (ItemStack item : armorContents) {
            if (item != null && item.getType() != Material.AIR && !isInate(item)) {
            	if (isInate(item))
                    innateItems.add(item.clone());
            	else
            		inv1.addItem(item);
            }
        }
        for (ItemStack item : hotbarContents) {
            if (item != null && item.getType() != Material.AIR) {
            	if (isInate(item))
                    innateItems.add(item.clone());
            	else
            		inv1.addItem(item);
            }
        }

        // Store the rest of the inventory in the second chest
        for (ItemStack item : storageContents) {
            if (item != null && item.getType() != Material.AIR) {
            	if (isInate(item))
                    innateItems.add(item.clone());
            	else
            		inv2.addItem(item);
                //player.getInventory().remove(item);
            }
        }

        // Clear player's inventory after transferring
        player.getInventory().clear();
        player.sendMessage("Your inventory has been taken!");
        
        if (innateItems.size() > 0) {
        	for (ItemStack item : innateItems)
        		player.getInventory().addItem(item);
        }
        
        chest1.setCustomName("Loot " + player.getName());
        chest1.addScoreboardTag("FancyBossLoot");
        chest1.addScoreboardTag("PlayerLoot");
        chest2.setCustomName("Loot " + player.getName());
        chest2.addScoreboardTag("PlayerLoot");
    }
	
	@EventHandler
	public void onEntityResurrect(EntityResurrectEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			if (event.getHand() == EquipmentSlot.HAND) {
				if (Math.random() < 0.4)
					event.setCancelled(true);
			}
			if (!event.isCancelled() && event.getEntity().getScoreboardTags().contains("FancyMob")) {
				ArmorStand template = FancyMob.GetTemplate(event.getEntity().getCustomName());
				event.getEntity().addPotionEffects(template.getActivePotionEffects());
			}
		}
	}
	
	public static void root(LivingEntity le, Location loc, int ticks) {
		prepare_teleport(le);/*
		if (le.getPassengers().size() > 0)
			le.removePassenger(le.getPassengers().get(0));*/
		rooted.put(le, new Tuple<>(loc, ticks));
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		//Bukkit.broadcastMessage("Projectile hit");
		/*
		if (event.getEntityType() == EntityType.SHULKER_BULLET) {
			ShulkerBullet missile = (ShulkerBullet) event.getEntity();
			if (magicMissiles.containsKey(missile)) {
				double damage = magicMissiles.get(missile);
				event.setCancelled(true);
				Entity hitEntity = event.getHitEntity();
				if (hitEntity != null && hitEntity instanceof LivingEntity le && Arena.validTarget(le))
				{
					le.damage(damage); // Hit by magic missile
					if (missile.getShooter() instanceof Player player)
						player.sendMessage("Missile hit entity");
				}
				else if (event.getHitBlock() != null) {
					if (missile.getShooter() instanceof Player player)
						player.sendMessage("Missile hit block");
				}
			}
		}
		else*/ if (isWindCharge(event.getEntity())) {
            windburst(event.getHitBlock() != null ? event.getHitBlock() : event.getEntity().getLocation().getBlock(), 3);
            Location loc = event.getEntity().getLocation();
            if (event.getEntity().hasMetadata("Power")) {
            	int power = (int) event.getEntity().getMetadata("Power").get(0).value();
            	List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(3, 3, 3);
                
                for (Entity entity : nearbyEntities) {
                    if (entity instanceof LivingEntity le && Arena.validTarget(le)) {
                        Vector knockback = entity.getLocation().toVector().subtract(event.getEntity().getLocation().toVector()).normalize().multiply(((float)power)/2);
                        knockback.setY(((float)power)/5); // Increase vertical knockback
                        entity.setVelocity(entity.getVelocity().add(knockback));
                    }
                }
            }
        } else if (event.getEntity().hasMetadata("commands")) {
			//Bukkit.broadcastMessage("Running Commands");
			Location loc = event.getEntity().getLocation();
			boolean cancelled = false;
			if (event.getHitEntity() != null) {
				Entity e = event.getHitEntity();
				if (e instanceof LivingEntity le && event.getEntity().getShooter() != le && le.getEquipment().getItemInOffHand() != null && le.getEquipment().getItemInOffHand().hasItemMeta() && le.getEquipment().getItemInOffHand().getItemMeta().hasDisplayName() && le.getEquipment().getItemInOffHand().getType() == Material.GUNPOWDER && le.getEquipment().getItemInOffHand().getItemMeta().getDisplayName().contains("Ring of Arcane Antithesis")) {
					if (le instanceof Player player) {
						if (!player.hasCooldown(Material.GUNPOWDER)) {
							counterspell(player, event.getEntity().getVelocity().clone().multiply(-1).normalize().multiply(0.5).add( event.getEntity().getLocation().toVector().clone().subtract(le.getLocation().toVector()) ));
							player.setCooldown(Material.GUNPOWDER, 80);
							event.setCancelled(true);
							
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(e.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
							e.getWorld().spawnParticle(Particle.LARGE_SMOKE, e.getLocation(), 3);
							
							event.getEntity().remove();;
							cancelled = true;
						}
					} else if (Math.random() < 0.4) {
						counterspell(le, event.getEntity().getVelocity().clone().multiply(-1).normalize().multiply(0.5).add( event.getEntity().getLocation().toVector().clone().subtract(le.getLocation().toVector()) ));
						event.setCancelled(true);
						event.getEntity().remove();
						cancelled = true;
					}
				}
			}
			for (Entity e : loc.getWorld().getNearbyEntities(loc, 5, 5, 5)) {
				if (e instanceof LivingEntity le && event.getEntity().getShooter() != le && le.getEquipment().getItemInOffHand() != null && le.getEquipment().getItemInOffHand().hasItemMeta() && le.getEquipment().getItemInOffHand().getItemMeta().hasDisplayName() && le.getEquipment().getItemInOffHand().getType() == Material.GUNPOWDER && le.getEquipment().getItemInOffHand().getItemMeta().getDisplayName().contains("Ring of Arcane Antithesis")) {
					if (le instanceof Player player) {
						if (!player.hasCooldown(Material.GUNPOWDER)) {
							counterspell(player, event.getEntity().getVelocity().clone().multiply(-1).normalize().multiply(0.5).add( event.getEntity().getLocation().toVector().clone().subtract(le.getLocation().toVector()) ));
							player.setCooldown(Material.GUNPOWDER, 80);
							event.setCancelled(true);
							
							//for (Player p : Bukkit.getOnlinePlayers())
								SFX.play(e.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
							e.getWorld().spawnParticle(Particle.LARGE_SMOKE, e.getLocation(), 3);
							
							event.getEntity().remove();;
							cancelled = true;
						}
					} else if (Math.random() < 0.4) {
						counterspell(le, event.getEntity().getVelocity().clone().multiply(-1).normalize().multiply(0.5).add( event.getEntity().getLocation().toVector().clone().subtract(le.getLocation().toVector()) ));
						event.setCancelled(true);
						event.getEntity().remove();
						cancelled = true;
					}
				}
			}
			
			if (!cancelled) {
				List<String> commands = (List<String>)event.getEntity().getMetadata("commands").get(0).value();
				if (commands != null && commands.size() > 0) {
					for (String cmd : commands) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run " + cmd.strip());
					}
				}
			}
		}
	}
	
	 private HashMap<Block, Boolean> trackedDoors = new HashMap<>();
	 private HashMap<Block, Boolean> trackedCandles = new HashMap<>();
	 private HashMap<Block, BlockData> trackedBlockData = new HashMap<>();
	
	private boolean isWindCharge(Projectile projectile) {
        return projectile.getType().name().equals("WIND_CHARGE"); // Modify based on actual wind charge identifier
    }
	
	public void windburst(Block centerBlock, int r) {
        if (centerBlock == null) return;
        trackedDoors.clear();
        trackDoors(centerBlock, r);
        
        
        centerBlock.getWorld().getNearbyEntities(centerBlock.getLocation(), r, r, r).forEach(entity -> {
            /*if (entity.getLocation().distanceSquared(centerBlock.getLocation()) <= r*r) {
                trackDoors(centerBlock, r);
            }*/
        	if (!Arena.validTarget(entity)) {
        		entity.setVelocity(new Vector(0, 0, 0));
        	}
        });
        
    }

	private boolean isWindburstMace(ItemStack item) {
		//Bukkit.broadcastMessage("Checking for windburst: " + (item != null ? "true" : "false") + " " + (item.getType() == Material.MACE ? "true" : "false") + " " + (item.containsEnchantment(Enchantment.WIND_BURST) ? "true" : "false"));
        return item != null && item.getType() == Material.MACE && item.containsEnchantment(Enchantment.WIND_BURST);
    }
	
	private void trackDoors(Block centerBlock, int r) {
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Block block = centerBlock.getRelative(x, y, z);
                    if (isDoorOrTrapdoor(block)) {
                        trackedDoors.put(block, ((Openable) block.getBlockData()).isOpen());
                    } else if (isCandle(block)) {
                        trackedCandles.put(block, ((Candle) block.getBlockData()).isLit());
                    } else if (block.getType() == Material.LEVER) {
                    	trackedBlockData.put(block, block.getBlockData());
                    }
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (HashMap.Entry<Block, Boolean> entry : trackedDoors.entrySet()) {
                    Block block = entry.getKey();
                    boolean wasOpen = entry.getValue();
                    Openable openable = (Openable) block.getBlockData();
                    if (openable.isOpen() != wasOpen) {
                        openable.setOpen(wasOpen);
                        block.setBlockData(openable, false);
                    }
                }
                for (HashMap.Entry<Block, Boolean> entry : trackedCandles.entrySet()) {
                    Block block = entry.getKey();
                    boolean wasLit = entry.getValue();
                    Candle candle = (Candle) block.getBlockData();
                    if (candle.isLit() != wasLit) {
                        candle.setLit(wasLit);
                        block.setBlockData(candle, false);
                    }
                }
                for (HashMap.Entry<Block, BlockData> entry : trackedBlockData.entrySet()) {
                	Block block = entry.getKey();
                	BlockData data = entry.getValue();
                	block.setBlockData(data);
                }
            }
        }.runTaskLater(this, 2);
    }
	
	private boolean isCandle(Block block) {
        return block.getType().name().endsWith("_CANDLE");
    }
	
    private boolean isDoorOrTrapdoor(Block block) {
        Material material = block.getType();
        return material.name().endsWith("_DOOR") || material.name().endsWith("_TRAPDOOR");
    }
	
	@EventHandler
    public void onEntityInteract(EntityInteractEvent event) {
        Block block = event.getBlock();
        if (isDoorOrTrapdoor(block)) {
            event.setCancelled(true);
        }
    }
	
	public void throwItem2(LivingEntity e, ItemStack item, int damage, float knockback, List<String> commands, Sound hitSound, Particle hitParticle) {
		Location loc = e.getLocation().clone().add(0, 1.6F, 0);
		Vector dir = loc.getDirection();
		
		ThrownPotion thrownItem = (ThrownPotion)loc.getWorld().spawnEntity(loc, EntityType.POTION);
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_EGG_THROW, 1, 1.1F);
		
		thrownItem.setShooter(e);
		thrownItem.setVelocity(dir.clone().multiply(1.5));
		thrownItem.setItem(item);
		thrownItem.setMetadata("particle", new FixedMetadataValue(this, hitParticle));
		thrownItem.setMetadata("sound", new FixedMetadataValue(this, hitSound));
		thrownItem.setMetadata("damage", new FixedMetadataValue(this, damage));
		thrownItem.setMetadata("knockback", new FixedMetadataValue(this, knockback));
		thrownItem.setMetadata("item", new FixedMetadataValue(this, item.clone()));
		thrownItem.setMetadata("commands", new FixedMetadataValue(this, commands));
		thrownItem.addScoreboardTag("ThrownItem2");
	}
	

	private void throwItem(LivingEntity entity, ItemStack item, boolean returning, int damage, float knockback, List<String> commands, Sound hitSound, Particle hitParticle, boolean spin) {
		
		// TODO Auto-generated method stub
				Location loc = entity.getLocation().clone();
				//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					SFX.play(loc, Sound.ITEM_TRIDENT_THROW, 1, 1);
				//}
				
				Vector dir = loc.getDirection();
				
				ArmorStand proj = (ArmorStand)loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
				
				//proj.setVelocity(dir.clone().multiply(1.5));
				proj.getEquipment().setHelmet(item.clone());
				proj.setMarker(true);
				proj.setInvisible(true);
				proj.addScoreboardTag("ThrownItem");
				
				proj.setMetadata("damage", new FixedMetadataValue(this, damage));
				proj.setMetadata("knockback", new FixedMetadataValue(this, knockback));
				proj.setMetadata("returning", new FixedMetadataValue(this, returning));
				proj.setMetadata("returned", new FixedMetadataValue(this, false));
				proj.setMetadata("item", new FixedMetadataValue(this, item));
				proj.setMetadata("owner", new FixedMetadataValue(this, entity));
				//proj.setMetadata("effect", new FixedMetadataValue(this, effect));
				proj.setMetadata("sound", new FixedMetadataValue(this, hitSound));
				proj.setMetadata("particle", new FixedMetadataValue(this, hitParticle));
				proj.setMetadata("spin", new FixedMetadataValue(this, spin));
				thrownItemCommands.put(proj, commands);
				//proj.addPotionEffects(effects);
				//proj.setMetadata("velocity", new FixedMetadataValue(this, dir.clone().multiply(1.5)));
				thrownItems.put(proj, dir.clone().multiply(1.5));
	}
	
	private void throwItem(LivingEntity entity, ItemStack item, boolean returning, int damage, float knockback, List<String> commands, Sound hitSound, Particle hitParticle) {
		throwItem(entity, item, returning, damage, knockback, commands, hitSound, hitParticle, false);
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		ItemStack item = event.getItemDrop().getItemStack().clone();
		String itemName = item.getItemMeta().getDisplayName();
		
		boolean cancel = useItem(event.getPlayer(), item, false, 2);
		if (cancel)
			event.setCancelled(true);
		else if (itemName.contains("Charm of Broken Zehirs")) {
			String newItemName = ChatColor.DARK_PURPLE + "Blade of Broken Zehirs";
			int newDamage = 5;
			int newCustomModelData = 10000103;
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click to throw");
			lore.add(ChatColor.GRAY + "Press Q to close");
			
			Location loc = event.getPlayer().getLocation();
			Vector dir = loc.getDirection();
			Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector up = right.clone().crossProduct(dir).normalize();
			Location itemLoc = loc.clone().add(dir.clone().multiply(0.4)).add(up.clone().multiply(1.5)).add(right.clone().multiply(0.5));

			event.getItemDrop().remove();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(newItemName);
			meta.setCustomModelData(newCustomModelData);
			meta.setLore(lore);
			meta = ModItem.setAttribute(meta, newDamage, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run playsound charm player @a ~ ~ ~ 2 1");
			event.getPlayer().getLocation().getWorld().spawnParticle(Particle.BLOCK, itemLoc, 30, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
			item.setItemMeta(meta);
			event.getPlayer().getInventory().addItem(item);
		}
		else if (itemName.contains("Holy Avenger")) {
			Player player = event.getPlayer();
			Location loc = player.getLocation();
			//for (Player p : player.getWorld().getPlayers())
				SFX.play(loc, Sound.BLOCK_BELL_USE, 1, 1.2f);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run effect give @e[type=!armor_stand,type=!item,type=!painting,type=!item_frame,type=!glow_item_frame,distance=..3] resistance 30 0");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run effect give @e[type=!armor_stand,type=!item,type=!painting,type=!item_frame,type=!glow_item_frame,distance=..3] strength 30 0");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run effect give @e[type=!armor_stand,type=!item,type=!painting,type=!item_frame,type=!glow_item_frame,distance=..3] luck 30 0");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run effect give @e[type=!armor_stand,type=!item,type=!painting,type=!item_frame,type=!glow_item_frame,distance=..3] night_vision 30 0");
			player.getWorld().spawnParticle(Particle.END_ROD, loc, 20, 3, 3, 3, 0);
			event.setCancelled(true);
		}
		else if (itemName.contains("Blade of Broken Zehirs")) {
			String newItemName = ChatColor.DARK_PURPLE + "Charm of Broken Zehirs";
			int newDamage = 0;
			int newCustomModelData = 10000102;
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Press Q to open");
			
			Location loc = event.getPlayer().getLocation();
			Vector dir = loc.getDirection();
			Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector up = right.clone().crossProduct(dir).normalize();
			Location itemLoc = loc.clone().add(dir.clone().multiply(0.2)).add(up.clone().multiply(1.5)).add(right.clone().multiply(0.5));

			event.getItemDrop().remove();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(newItemName);
			meta.setLore(lore);
			meta.setCustomModelData(newCustomModelData);
			meta = ModItem.setAttribute(meta, newDamage, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run playsound minecraft:item.trident.riptide_1 player @a ~ ~ ~ 1 0.6");
			event.getPlayer().getLocation().getWorld().spawnParticle(Particle.BLOCK, itemLoc, 30, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
			item.setItemMeta(meta);
			event.getPlayer().getInventory().addItem(item);
		}
		
		/*
		if (itemName.contains("Staff of Swarming Insects")) {
			event.setCancelled(true);
			Player p = event.getPlayer();
			swarmingInsects.put(p, 200);
		}
		else if (itemName.contains("Staff of the Proletariat")) {
			event.setCancelled(true);
			Player p = event.getPlayer();
			if (p.hasCooldown(Material.GOLDEN_HOE)) {
				p.sendMessage(ChatColor.RED + "You must wait for the cooldown to end before using this ability");
			} else {
				p.setCooldown(Material.GOLDEN_HOE, 8);
				if (illusionDoubles.containsKey((LivingEntity) p) && illusionDoubles.get((LivingEntity) p) != null && illusionDoubles.get((LivingEntity) p).isValid()) {
					Location playerLoc = p.getLocation().clone();
					Location masLoc = illusionDoubles.get((LivingEntity) p).getLocation().clone().setDirection(p.getLocation().getDirection());
					
					delayedTeleport.put(p, masLoc);
					delayedTeleport.put(illusionDoubles.get((LivingEntity) p), playerLoc);
					
					for (Player player : Bukkit.getOnlinePlayers())
						SFX.play(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.PLAYERS, 1, 2f);
				} else {
					Bullet b = new Bullet(p.getLocation().getDirection(), p.getLocation().clone().add(0, 1.55, 0), true, Particle.ENCHANT, 20, 1.5f, 12, p, this); // Maskirovka
					for (Player player : Bukkit.getOnlinePlayers())
						SFX.play(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, SoundCategory.PLAYERS, 1, 1.3f);
				}
			}
		}
		else if (itemName.contains("Charm of Broken Zehirs")) {
			String newItemName = ChatColor.DARK_PURPLE + "Blade of Broken Zehirs";
			int newDamage = 5;
			int newCustomModelData = 10000103;
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click to throw");
			lore.add(ChatColor.GRAY + "Press Q to close");
			
			Location loc = event.getPlayer().getLocation();
			Vector dir = loc.getDirection();
			Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector up = right.clone().crossProduct(dir).normalize();
			Location itemLoc = loc.clone().add(dir.clone().multiply(0.4)).add(up.clone().multiply(1.5)).add(right.clone().multiply(0.5));

			event.getItemDrop().remove();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(newItemName);
			meta.setCustomModelData(newCustomModelData);
			meta.setLore(lore);
			meta = ModItem.setAttribute(meta, newDamage, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run playsound charm player @a ~ ~ ~ 2 1");
			event.getPlayer().getLocation().getWorld().spawnParticle(Particle.BLOCK, itemLoc, 30, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
			item.setItemMeta(meta);
			event.getPlayer().getInventory().addItem(item);
		} else if (itemName.contains("Blade of Broken Zehirs")) {
			String newItemName = ChatColor.DARK_PURPLE + "Charm of Broken Zehirs";
			int newDamage = 0;
			int newCustomModelData = 10000102;
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Press Q to open");
			
			Location loc = event.getPlayer().getLocation();
			Vector dir = loc.getDirection();
			Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
			Vector up = right.clone().crossProduct(dir).normalize();
			Location itemLoc = loc.clone().add(dir.clone().multiply(0.2)).add(up.clone().multiply(1.5)).add(right.clone().multiply(0.5));

			event.getItemDrop().remove();
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(newItemName);
			meta.setLore(lore);
			meta.setCustomModelData(newCustomModelData);
			meta = ModItem.setAttribute(meta, newDamage, Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + event.getPlayer().getName() + " at @s run playsound minecraft:item.trident.riptide_1 player @a ~ ~ ~ 1 0.6");
			event.getPlayer().getLocation().getWorld().spawnParticle(Particle.BLOCK, itemLoc, 30, 0, 0, 0, 0, Material.BLACKSTONE.createBlockData());
			item.setItemMeta(meta);
			event.getPlayer().getInventory().addItem(item);
		}
		*/
	}
	
	public void thrownItemHit(Entity proj, Block hitBlock, Entity hitEntity) {
		
		boolean kill = true;
		Location loc = proj.getLocation();
		
		if (proj.getScoreboardTags().contains("ThrownItem")) {

			LivingEntity owner = (LivingEntity)proj.getMetadata("owner").get(0).value();
			
			if (hitEntity != null && hitEntity instanceof LivingEntity le) {
				//Bukkit.broadcastMessage("Knocked entity");
				if (thrownItemCommands.containsKey(proj) && thrownItemCommands.get(proj) != null) {
					//Bukkit.broadcastMessage("Attempting to add potion effects");
					//le.addPotionEffects(thrownItemCommands.get(proj));
					//Bukkit.broadcastMessage("Added potion effects to " + le.getName());
					for (String cmd : thrownItemCommands.get(proj)) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!armor_stand,limit=1,sort=nearest] run " + cmd);
						//Bukkit.dispatchCommand(le, "effect give @s minecraft:wither 5 1");
						//Bukkit.broadcastMessage(effect.getType().toString() + ", amplifyer: " + effect.getAmplifier() + ", duration: " + effect.getDuration());
						//le.addPotionEffect(effect);
					}
				}

				le.damage((int)proj.getMetadata("damage").get(0).value(), owner);
				//Bukkit.broadcastMessage("Damaged entity");
				le.setVelocity(le.getVelocity().add(loc.getDirection().clone().multiply((float)proj.getMetadata("knockback").get(0).value())));
				//Bukkit.broadcastMessage("Done with collision");
			}

			if ((boolean)proj.getMetadata("returning").get(0).value()) {
				proj.setVelocity(proj.getVelocity().multiply(-1));
				proj.removeScoreboardTag("ThrownItem");
				proj.addScoreboardTag("ReturningItem");
				kill = false;
				//returnItem(loc.clone().setDirection(loc.getDirection().multiply(-1)).subtract(loc.getDirection()), owner, (ItemStack)proj.getMetadata("item").get(0).value());
			}
			//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				SFX.play(loc, (Sound)proj.getMetadata("sound").get(0).value(), 1, 1);
			//}
			loc.getWorld().spawnParticle((Particle)proj.getMetadata("particle").get(0).value(), loc, 10, 0.4f, 0.1f, 0.1f, 0.1f);
			
		}
		if (kill)
		{
			Item itemEntity = (Item)loc.getWorld().spawnEntity(loc, EntityType.ITEM);
			itemEntity.setItemStack((ItemStack)proj.getMetadata("effects").get(0).value());
			thrownItems.remove(proj);
			if (thrownItemCommands.containsKey(proj)) {
				thrownItemCommands.remove(proj);
			}
			proj.remove();
		}
	}
	
	public boolean leftClick(Player player) {
		if (!enableItems) {
			return false;
		}
		
		ItemStack item = player.getEquipment().getItemInMainHand();
		if (item != null && item.getItemMeta() != null)
			return useItem(player, item, false, 1);
		return false;
	}

	public boolean rightClick(Player player) {
		if (!enableItems) {
			return false;
		}
		//player.sendMessage("Right Click");
		Entity target = getEntityPlayerIsLookingAt(player, 3);
		if (target != null) {
			boolean cancel = interactEntity(player, target);
			if (cancel) {
				return true;
			}
		}
		
		// Get the used itemStack
		org.bukkit.inventory.ItemStack item = player.getInventory().getItemInMainHand() != null ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
		if (item == null) {
			return false;
		}
		boolean cancelEvent = false;
		cancelEvent = useItem(player, item);
		return cancelEvent;
	}
	
	@EventHandler
    public void PickupItem(PlayerPickupItemEvent e) {
		boolean success = false;
		 if (e.getItem() != null && e.getItem().getItemStack() != null) {
			 if (e.getPlayer().getScoreboardTags().contains("Polymorph")) {
				 for (Polymorph p : polymorphs) {
					 if (p.player == e.getPlayer()) {
						 for (ItemStack stack : p.storedInv) {
							 if (stack == null) {
								 stack = e.getItem().getItemStack().clone();
								 e.getItem().getItemStack().setAmount(0);
								 e.getItem().remove();
								 success = true;
								 break;
							 }
						 }
						 
						 if (success) {
							 // Successfully picked up item
						 } else {
							 // No inventory space
							 e.setCancelled(true);
						 }
						 
						 break;
					 }
				 }
			 }
		 }
	}
	
	/**
     * Opens the Fabricate Menu for the given player.
     * The menu is a chest inventory containing nine items.
     */
    public void openFabricateMenu(Player player) {
        // Create a chest inventory with 9 slots
        Inventory menu = Bukkit.createInventory(null, 9, "Fabricate");

        // Create items with appropriate material types.
        menu.setItem(0, createMenuItem(Material.CRAFTING_TABLE, ChatColor.GOLD + "Crafting Table"));
        menu.setItem(1, createMenuItem(Material.ANVIL, ChatColor.GOLD + "Anvil"));
        menu.setItem(2, createMenuItem(Material.ENCHANTING_TABLE, ChatColor.GOLD + "Enchanting Table"));
        menu.setItem(3, createMenuItem(Material.SMITHING_TABLE, ChatColor.GOLD + "Smithing Table"));
        menu.setItem(4, createMenuItem(Material.LOOM, ChatColor.GOLD + "Loom"));
        menu.setItem(5, createMenuItem(Material.GRINDSTONE, ChatColor.GOLD + "Grindstone"));
        menu.setItem(6, createMenuItem(Material.STONECUTTER, ChatColor.GOLD + "Stonecutter"));
        menu.setItem(7, createMenuItem(Material.BREWING_STAND, ChatColor.GOLD + "Brewing Stand"));
        menu.setItem(8, createMenuItem(Material.ENDER_CHEST, ChatColor.GOLD + "Ender Chest"));

        // Open the menu for the player
        player.openInventory(menu);
    }

    /**
     * Helper method to create an ItemStack with a display name.
     */
    private ItemStack createMenuItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }
	
    
    
	@EventHandler
    public void onShootArrow(EntityShootBowEvent e) {
		Material mat = e.getBow().getType();
		String itemName = e.getBow().getItemMeta().getDisplayName();
		
		LivingEntity entity = e.getEntity();
		//Giant giant = null;
		Entity target = null;
		/*
		if (entity.getScoreboardTags().contains("Giant")) {
			giant = giants.get(entity).x;
			target = giants.get(entity).y;
			if (target != null) {
				giant.getLocation().setDirection(target.getLocation().subtract(giant.getLocation()).toVector());
			} else {
				giant.teleport(entity.getLocation());
			}
		}
		*/
		
		AbstractArrow arrow = (AbstractArrow)e.getProjectile();
		
		if (mat == Material.CROSSBOW && itemName.contains("Barrage")) {
			//arrowBarrage.put(giant == null ? entity : giant, 10);
			if (itemName.contains("Royal"))
				royalArrowBarrage.put(entity, 10);
			else if (itemName.contains("Supreme"))
				supremeArrowBarrage.put(entity, 15);
			else
				arrowBarrage.put(entity, 10);
        } else if (mat == Material.BOW && itemName.contains("The Moonbow")) {
        	//Bukkit.broadcastMessage("Shooting longbow");
        	//Arrow arrow = (Arrow)e.getProjectile();
        	arrow.setVelocity(arrow.getVelocity().clone().multiply(2.2f));
        	arrow.setDamage(arrow.getDamage()*1.6);
        	arrow.setPierceLevel(4);
        	
        	arrowTrail.put(arrow, Particle.FLASH);
        	
        	final ProjectileSource shooter = arrow.getShooter();
        	final Location loc = arrow.getLocation();
        	final int fireTicks = arrow.getFireTicks();
        	final double damage = arrow.getDamage();
        	final int pierceLevel = arrow.getPierceLevel();
        	final Vector vel = arrow.getVelocity();
        	
        	Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        		Arrow fb = loc.getWorld().spawn(loc, Arrow.class);
				fb.setFireTicks(fireTicks);
				fb.setShooter(shooter);
				fb.setDamage(damage);
				fb.setVelocity(vel.add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
				fb.setPierceLevel(pierceLevel-1);
				fb.setPickupStatus(PickupStatus.DISALLOWED);
	        	arrowTrail.put(fb, Particle.FLASH);
            }, 2); // Delay of 0.1 seconds
        	
        	Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        		Arrow fb = loc.getWorld().spawn(loc, Arrow.class);
				fb.setFireTicks(fireTicks);
				fb.setShooter(shooter);
				fb.setDamage(damage);
				fb.setVelocity(vel.add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
				fb.setPierceLevel(pierceLevel-1);
				fb.setPickupStatus(PickupStatus.DISALLOWED);
	        	arrowTrail.put(fb, Particle.FLASH);
            }, 4); // Delay of 0.1 seconds
        	
        	for (Player p2 : Bukkit.getServer().getOnlinePlayers())
				p2.playSound(p2.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1f, 1.2f);
        }
		else if (mat == Material.BOW && itemName.contains("Supreme Longbow")) {
        	//Bukkit.broadcastMessage("Shooting longbow");
        	//Arrow arrow = (Arrow)e.getProjectile();
        	arrow.setVelocity(arrow.getVelocity().clone().multiply(2));
        	arrow.setDamage(arrow.getDamage()*1.5);
        	arrow.setPierceLevel(4);
        	
        	arrowTrail.put(arrow, Particle.END_ROD);
        	
        	final ProjectileSource shooter = arrow.getShooter();
        	final Location loc = arrow.getLocation();
        	final int fireTicks = arrow.getFireTicks();
        	final double damage = arrow.getDamage();
        	final int pierceLevel = arrow.getPierceLevel();
        	final Vector vel = arrow.getVelocity();
        	
        	Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        		Arrow fb = loc.getWorld().spawn(loc, Arrow.class);
				fb.setFireTicks(fireTicks);
				fb.setShooter(shooter);
				fb.setDamage(damage);
				fb.setVelocity(vel.add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
				fb.setPierceLevel(pierceLevel-1);
				fb.setPickupStatus(PickupStatus.DISALLOWED);
	        	arrowTrail.put(fb, Particle.END_ROD);
            }, 2); // Delay of 0.1 seconds
        	
        	Bukkit.getScheduler().runTaskLater(FancyArena.instance, () -> {
        		Arrow fb = loc.getWorld().spawn(loc, Arrow.class);
				fb.setFireTicks(fireTicks);
				fb.setShooter(shooter);
				fb.setDamage(damage);
				fb.setVelocity(vel.add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
				fb.setPierceLevel(pierceLevel-1);
				fb.setPickupStatus(PickupStatus.DISALLOWED);
	        	arrowTrail.put(fb, Particle.END_ROD);
            }, 4); // Delay of 0.1 seconds
        	
        	for (Player p2 : Bukkit.getServer().getOnlinePlayers())
				p2.playSound(p2.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0.8f);
        } 
		else if (mat == Material.BOW && itemName.contains("Longbow")) {
        	//Bukkit.broadcastMessage("Shooting longbow");
        	//Arrow arrow = (Arrow)e.getProjectile();
        	arrow.setVelocity(arrow.getVelocity().clone().multiply(1.8));
        	arrow.setDamage(arrow.getDamage()*1.5);
        	arrow.setPierceLevel(2);
        	
        	for (Player p2 : Bukkit.getServer().getOnlinePlayers())
				p2.playSound(p2.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 0.8f);
        }
		
		if ((entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WITHER_SKELETON) || entity.getType() == EntityType.STRAY || entity.getType() == EntityType.BOGGED) {			
			ItemStack item = entity.getEquipment().getItemInOffHand();
			//if (giant != null)
			//	item = giant.getEquipment().getItemInMainHand();
			int var = 0;
			Boolean[] yeet = new Boolean[] {false, false, false};
			if (item != null && item.hasItemMeta()) { // This is to figure out which mouse button to use, it just uses a random available one, so either left click, right click, or Q
				String itemName2 = item.getItemMeta().getDisplayName();
				if (itemBtn.containsKey(itemName2)) {
					yeet = FancyArena.itemBtn.get(itemName2);
					do {
						var = (int)(Math.random()*3);
					} while (yeet[var] == false);
				}
			}
			
			//boolean cancelEvent = useItem(giant == null ? entity : giant, item, false, var);
			boolean cancelEvent = useItem(entity, item, false, var);
			if (cancelEvent) {
				e.setCancelled(cancelEvent);
				return;
			}
		}
		
		/*if (giant != null) {
    		arrow.teleport(giant.getEyeLocation().add(giant.getEyeLocation().getDirection().multiply(3)));
    		arrow.setVelocity(giant.getEyeLocation().getDirection().multiply(arrow.getVelocity().length()));
    	}*/
		
        
    }
	
	
	@EventHandler
	public void onUseEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {

			// Ensure the player is using the main hand
	        //if (event.getHand() == EquipmentSlot.HAND) {

        		boolean cancelEvent = rightClick(player);
	    	    if (cancelEvent)
	    	    	event.setCancelled(true);
	        //}
		} else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			boolean cancelEvent = leftClick(player);
			if (cancelEvent)
				event.setCancelled(true);
		}
	}
	
	EntityType[] undroppable = new EntityType[] {
			EntityType.WITHER_SKELETON,
	};
	
	public boolean invalidDrop(ItemStack item) {
		if (item == null)
			return true;
		return item.getType() == Material.ROTTEN_FLESH || item.getType() == Material.COAL || item.getType() == Material.BONE || (item.getType() == Material.BOW && item.hasItemMeta() && item.getItemMeta().getCustomModelData() == 42);
	}
	
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntity() instanceof EnderDragon dragon) {
			dragon.setPhase(Phase.CIRCLING);
		}
	}
	
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
    	if (event.getEntity().getScoreboardTags().contains("Hostile")) {
	        Hostiler.activate(event.getEntity());
    	}
    }
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		if (e.getDamageSource() instanceof Player p && adrenalineRush.contains(p.getName())) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 2, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 30, 2, true));
			p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 30, 5, true));
			p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
		}
		if (e.getEntity().getScoreboardTags().contains("FancyMob") && !e.getEntity().getScoreboardTags().contains("Echo")) {
			if (minions.containsKey(e.getEntity())) {
				for (LivingEntity le : minions.get(e.getEntity())) {
					le.remove();
				}
				minions.remove(e.getEntity());
			}
			//Bukkit.broadcastMessage(e.getEntity().getCustomName() + " just died!");
			for (ItemStack item : e.getDrops()) {
				if (item != null && invalidDrop(item)) {
					item.setAmount(0);
				}
			}
			
			float lootMultiplier = 1;
			if (e.getDamageSource() instanceof Player p && scavengersKit.contains(p.getName())) {
				lootMultiplier = 2;
			}

			ItemStack[] drops = FancyMob.GetDrops(e.getEntity().getLocation(), e.getEntity().getCustomName());
			
			for (ItemStack i : drops) {
				i.setAmount((int)(Math.random() * lootMultiplier-0.1f) + i.getAmount());
			}
			
			double dropChance = lootMultiplier * FancyMob.GetDropChance(e.getEntity().getLocation(), e.getEntity().getCustomName());
			
			if (Arrays.asList(undroppable).contains(e.getEntity().getType()) && !e.getEntity().getScoreboardTags().contains("FancyBoss")) {
				for (ItemStack item : e.getEntity().getEquipment().getArmorContents()) {
					if (Math.random() < dropChance && !invalidDrop(item)) {
						e.getDrops().add(item.clone());
					}
				}
				if (Math.random() < dropChance && !invalidDrop(e.getEntity().getEquipment().getItemInMainHand()))
					e.getEntity().getEquipment().getItemInMainHand().clone();
				if (Math.random() < dropChance && !invalidDrop(e.getEntity().getEquipment().getItemInOffHand()))
					e.getEntity().getEquipment().getItemInOffHand().clone();
			}
			//Bukkit.broadcastMessage("Drop chance = " + dropChance);
			if (drops != null) {
				for (int i = 0; i < drops.length; i++) {
					if (Math.random() < dropChance*1.5F && drops[i] != null) {
						e.getDrops().add(drops[i].clone());
					}
				}
			}
			
			if (e.getEntity().getScoreboardTags().contains("FancyBoss")) {
				StorageMinecart crate = (StorageMinecart)e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), EntityType.CHEST_MINECART);
				crate.addScoreboardTag("invulnerable");
				if (Bullet.intersectsBlock(crate.getLocation(), 0.5)) {
					String arenaName = null;
					for (String tag : e.getEntity().getScoreboardTags()) {
						if (tag.contains("Arena_")) {
							arenaName = tag.substring(tag.indexOf('_') + 1);
							break;
						}
					}
					if (arenaName != null) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (p.getScoreboardTags().contains("Ready_" + arenaName)) {
								crate.teleport(p);
								break;
							}
						}
					}
				}
				crate.setCustomName("Loot " + e.getEntity().getCustomName());
				crate.addScoreboardTag("FancyBossLoot");
				crate.addScoreboardTag("ArenaCrate");
				for (ItemStack drop : e.getDrops())
					crate.getInventory().addItem(drop);
				e.getDrops().clear();
			}
			
		}
	}
	
	public String getDeathMessage(Player victim, Entity murderer, ItemStack item) {
		String victimName = victim.getDisplayName();
		String murdererName = murderer.getCustomName();
		if (murdererName == null)
			murdererName = murderer.getName();
		if (murdererName == null)
			murdererName = murderer.getType().toString().toLowerCase();
		if (murderer.getType() == EntityType.CREEPER) {
			if (murderer.getCustomName() != null && murderer.getCustomName().length() > 0) {
				return victimName + ChatColor.YELLOW + " was blown up by " + murderer.getCustomName() + ChatColor.YELLOW + "!";
			}
		}
		String itemName = item == null || !item.hasItemMeta() ? "" : item.getItemMeta().getDisplayName();
		Material mat = item == null ? null : item.getType();
		if (mat == Material.FIRE_CHARGE && itemName.contains("Super Deadly Fireball")) {
			return victimName + ChatColor.YELLOW + " was obliterated by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.FIRE_CHARGE && itemName.contains("Deadly Fireball")) {
			return victimName + ChatColor.YELLOW + " was vaporized by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.FIRE_CHARGE && itemName.contains("Fireball")) {
			return victimName + ChatColor.YELLOW + " was incinerated by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.STICK && itemName.contains("Wand of Firebolts")) {
			return victimName + ChatColor.YELLOW + " was torched by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.BLAZE_POWDER && itemName.equals("Fire Breath")) {
			return murdererName + ChatColor.YELLOW + " torched " + victimName + ChatColor.YELLOW + " with " + itemName + "!";
		} else if (mat == Material.STONE_HOE && itemName.contains("Pistol")) {
			return victimName + ChatColor.YELLOW + " was shot by " + murdererName + ChatColor.YELLOW +  " using " + itemName + "!";
		} else if (mat == Material.IRON_HOE && itemName.contains("Heavy Musket")) {
			return victimName + ChatColor.YELLOW +  " was no-scoped by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.IRON_HOE && itemName.contains("Musket")) {
			return victimName + ChatColor.YELLOW + " was shot through the heart by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.IRON_HOE && itemName.contains("Shotgun")) {
			return victimName + ChatColor.YELLOW + " was blasted to smitherines by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.CARROT_ON_A_STICK && itemName.contains("47")) {
			return victimName + ChatColor.YELLOW + " was gunned down by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.IRON_HOE && itemName.contains("47")) {
			return victimName + ChatColor.YELLOW + " was gunned down by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.LIGHTNING_ROD) {
			return victimName + ChatColor.YELLOW + " was electrocuted by " + murdererName + ChatColor.YELLOW + " using " + itemName + "!";
		} else if (mat == Material.PRISMARINE_SHARD && itemName.contains("Throwing Dart")) {
			return murdererName + ChatColor.YELLOW + " used " + victimName + ChatColor.YELLOW + " as a dart board!";
		} else if (itemName.contains("Arrow Barrage")) {
			return victimName + ChatColor.YELLOW + " was sent to the firing squad by " + murdererName + "!";
		} else if (itemName.contains("Staff of the Proletariat")) {
			return victimName + ChatColor.YELLOW + " was gulaged by " + murdererName + "!";
		} else if (itemName.contains("Disintegrate") || itemName.contains("Disintegration")) {
			return victimName + ChatColor.YELLOW + " was disintegrated by " + murdererName + ChatColor.YELLOW + " using magic!";
		} else if (itemName.contains("Wand") || itemName.contains("Staff")) {
			return victimName + ChatColor.YELLOW + " was killed by " + murdererName + ChatColor.YELLOW + " using magic!";
		} else if (itemName.contains("Frog Legs") || itemName.contains("Beer") || itemName.contains("Sugar") || itemName.contains("Escargot") || itemName.contains("Spinach") || itemName.contains("Gingerbread") || itemName.contains("Fries")) {
			return victimName + ChatColor.YELLOW + " lost a foodfight to " + murdererName + ChatColor.YELLOW + " who killed " + victimName + ChatColor.YELLOW + " with " + itemName;
		}
		
		return victimName + ChatColor.YELLOW + " was slaughtered by " + murdererName + ChatColor.YELLOW + " using " + itemName;
	}
	
	public ItemStack getWeapon(LivingEntity entity) {
		
		if ((entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.WITHER_SKELETON)) {
			ItemStack mainHand = entity.getEquipment().getItemInMainHand();
			
			if (mainHand.getType() == Material.BOW) {
				ItemMeta meta = mainHand.getItemMeta();
				if (meta.getCustomModelData() == 42) {
					// Invisible bow

					ItemStack offHand = entity.getEquipment().getItemInOffHand();
					
					if (offHand != null)
						return offHand;
				}
			}
			
		}
		
		return entity.getEquipment().getItemInMainHand();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e){
		
	     Entity entity = e.getDamageSource().getCausingEntity();
	     if (entity instanceof LivingEntity le) {
	    	 String message = getDeathMessage(e.getEntity(), le, getWeapon(le));
	    	 if (message != null) {
	    		 e.setDeathMessage(message);
	    	 }
	     }
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;
		/*if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("Giant")) {
			if (giants.containsKey(le)) {
				giants.put(le, new Tuple<Giant, Entity>(giants.get(le).x, event.getTarget()));
			}
		}*/
		if (!monsterPvp) {
			if (event.getEntity().getScoreboardTags().contains("ArenaMob") && event.getTarget().getScoreboardTags().contains("ArenaMob")) {
				event.setCancelled(true);
			}
		}
		if (illusionDoubles.containsKey((LivingEntity)event.getTarget()) && event.getEntity().getWorld().equals(illusionDoubles.get((LivingEntity)event.getTarget()).getLocation()) && event.getEntity().getLocation().distance(event.getTarget().getLocation()) > event.getEntity().getLocation().distance(illusionDoubles.get((LivingEntity)event.getTarget()).getLocation())) {
			event.setTarget(illusionDoubles.get((LivingEntity)event.getTarget()));
		}
	}

	 @EventHandler
	 public void onBlockBreak(BlockBreakEvent event) {
		 if (event.getPlayer() != null) {
			 if (event.getPlayer().getEquipment().getItemInMainHand() != null && event.getPlayer().getEquipment().getItemInMainHand().hasItemMeta() && event.getPlayer().getEquipment().getItemInMainHand().getItemMeta().getDisplayName().contains("---")) {
				 Player player = event.getPlayer();
				 Arena.ArenaEditItem(player, player.getEquipment().getItemInMainHand(), event.getBlock());
				 event.setCancelled(true);
			 }
		 }
	 }
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();
		if (repulsorFly.contains((LivingEntity) p)) {
			p.setAllowFlight(true);
		}
		//p.sendMessage("You are " + p.getName());
		//p.sendMessage("You just respawned");
		//if (p.getScoreboardTags().contains("Ready_")) {
		//	p.sendMessage("You are in an arena");
			if (Arena.lives.containsKey(p.getName())) {
				//p.sendMessage("You have lives");
				String arenaName = null;
				for (String tag : p.getScoreboardTags()) {
					if (tag.contains("Ready_")) {
						arenaName = tag.substring(tag.indexOf('_') + 1);
						break;
					}
				}
				Arena.lives.put(p.getName(), Arena.lives.get(p.getName())-1);
				SFX.play(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
				
				Location playerSpawn = Arena.getPlayerSpawn(arenaName);
				/*if (p.getPassengers().size() > 0)
					p.removePassenger(p.getPassengers().get(0));
				if (p.getScoreboardTags().contains("Grapple")) {
					p.removeScoreboardTag("Grapple");
					//grapple.remove(p);
				}
				p.teleport(playerSpawn);
				delayedTeleport.put(p, playerSpawn);*/
				super_strong_teleport(p, playerSpawn);
				
				if (Arena.lives.get(p.getName()) < 0) {
					Arena.removeTags(p, "Ready_");
					p.getInventory().clear();
					p.sendTitle(ChatColor.RED + "ELIMINATED", "Byeeeee", 1, 60, 5);
					//p.setGameMode(GameMode.SPECTATOR);
				} else if (Arena.lives.get(p.getName()) == 0) {
					p.sendTitle(ChatColor.RED + "Last Life", "Make it count!", 1, 20, 5);
				} else {
					p.sendTitle(ChatColor.RED + "" + ((Arena.lives.get(p.getName()) + 1)) + " Lives Left", "Stop dying!!!", 1, 40, 5);
				}

				if (Arena.playerEffects.containsKey(p))
					p.addPotionEffects(Arena.playerEffects.get(p));

			}
		//}
	}
	
	public void resetShit(Player player) {
		player.getInventory().clear();
		if (player.getActivePotionEffects().size() > 0) {
            for (PotionEffect e : player.getActivePotionEffects()) {
            	player.removePotionEffect(e.getType());;
            }
        }
		if (playerStates.containsKey(player)) {
			playerStates.remove(player);
		}
		if (repulsorFly.contains((LivingEntity) player))
			repulsorFly.remove((LivingEntity)player);
		if (repulsorFlying.contains((LivingEntity)player))
			repulsorFlying.remove((LivingEntity)player);
		player.removeScoreboardTag("RepulsorHover");
		player.removeScoreboardTag("RelentlessRage");
		player.removeScoreboardTag("ConvergentFuture");
		if (brutalCrit.contains(player.getName()))
			brutalCrit.remove(player.getName());
		if (doubleJump.contains(player.getName()))
			doubleJump.remove(player.getName());
		if (scavengersKit.contains(player.getName()))
			scavengersKit.remove(player.getName());
		if (noPainNoGain.contains(player.getName()))
			noPainNoGain.remove(player.getName());
		if (adrenalineRush.contains(player.getName()))
			adrenalineRush.remove(player.getName());
		if (canDoubleJump.contains(player.getName()))
			canDoubleJump.remove(player.getName());
		if (wallRun.contains(player.getName()))
			wallRun.remove(player.getName());
		if (wallJump.contains(player.getName()))
			wallJump.remove(player.getName());
		if (wallRunning.contains(player))
			wallRunning.remove(player);
		if (heavyFists.contains(player.getName()))
			heavyFists.remove(player.getName());
		
		if (sentryMode.containsKey(player)) {
			sentryMode.get(player).remove();
			sentryMode.remove(player);
		}
		
		if (sentryTarget.containsKey(player)) {
			sentryTarget.remove(player);
		}
		
		if (durrithWings.containsKey(player)) {
			durrithWings.get(player).remove();
			durrithWings.remove(player);
		}
		
		player.setAllowFlight(false);
	}
	
	public boolean interactEntity(Player player, Entity entity) {
		boolean cancelEvent = false;
		
		// Check if the clicked entity is an Armor Stand and has the "FancyKit" tag
        if (entity instanceof ArmorStand armorStand && armorStand.getScoreboardTags().contains("FancyKit")) {
        	
        	resetShit(player);
        	
        	cancelEvent = true; // Cancel the right-click event
        	
           player.getInventory().clear();

            // Copy armor and hand slots from the Armor Stand to the player
            player.getInventory().setHelmet(armorStand.getEquipment().getHelmet());
            player.getInventory().setChestplate(armorStand.getEquipment().getChestplate());
            player.getInventory().setLeggings(armorStand.getEquipment().getLeggings());
            player.getInventory().setBoots(armorStand.getEquipment().getBoots());
            player.getInventory().setItemInMainHand(armorStand.getEquipment().getItemInMainHand());
            player.getInventory().setItemInOffHand(armorStand.getEquipment().getItemInOffHand());            
            
            if (armorStand.getActivePotionEffects().size() > 0) {
	            for (PotionEffect e : armorStand.getActivePotionEffects()) {
	            	player.addPotionEffect(e);
	            }
            }
            
            // Get the block the Armor Stand is standing on
            Block blockBelow = armorStand.getLocation().clone().add(0, -1, 0).getBlock();

            // Check if the block below is a Barrel
            if (blockBelow.getType() == Material.BARREL) {
                Barrel barrel = (Barrel) blockBelow.getState();
                Inventory barrelInventory = barrel.getInventory();

                // Copy items from the barrel to the player's inventory
                for (ItemStack item : barrelInventory.getContents()) {
                    if (item != null) {
                        player.getInventory().addItem(item);
                    }
                }
            } else {
            	Block blockAbove = armorStand.getLocation().clone().add(0, 3, 0).getBlock();
            	if (blockAbove.getType() == Material.BARREL) {
                    Barrel barrel = (Barrel) blockAbove.getState();
                    Inventory barrelInventory = barrel.getInventory();

                    // Copy items from the barrel to the player's inventory
                    for (ItemStack item : barrelInventory.getContents()) {
                        if (item != null) {
                            player.getInventory().addItem(item);
                        }
                    }
                }
            }
        }
        
        return cancelEvent;
	}
	
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
    	Player player = event.getPlayer();
    	
    	//player.sendMessage("Right Clicked Entity");
    	
        Entity entity = event.getRightClicked();

        boolean cancelEvent = interactEntity(player, entity);
        
        if (cancelEvent)
        	event.setCancelled(cancelEvent);
        else {
        	cancelEvent = rightClick(player);
		    if (cancelEvent)
		    	event.setCancelled(true);
        }
        
    }
    
	@EventHandler
	public void onSplashPotion(PotionSplashEvent event) {
		if (event.getEntity().getScoreboardTags().contains("ThrownItem2")) {
			Entity hitEntity = event.getHitEntity();
			/*
		thrownItem.setMetadata("particle", new FixedMetadataValue(this, hitParticle)); //
		thrownItem.setMetadata("sound", new FixedMetadataValue(this, hitSound)); //
		thrownItem.setMetadata("damage", new FixedMetadataValue(this, damage)); //
		thrownItem.setMetadata("knockback", new FixedMetadataValue(this, knockback)); //
		thrownItem.setMetadata("item", new FixedMetadataValue(this, item.clone()));
		thrownItem.setMetadata("commands", new FixedMetadataValue(this, commands)); //
			 */
			Location loc = event.getEntity().getLocation();
			
			if (hitEntity != null && hitEntity instanceof LivingEntity le && event.getEntity().hasMetadata("damage") && !hitEntity.getScoreboardTags().contains("invulnerable")) {
				le.damage((int)event.getEntity().getMetadata("damage").get(0).value(), (Entity)event.getEntity().getShooter());
				
				float knockback = (float) event.getEntity().getMetadata("knockback").get(0).value();
				if (knockback > 0) {
					hitEntity.setVelocity(hitEntity.getVelocity().add(event.getEntity().getVelocity().clone().multiply(knockback)));
				}
			}
			
			if (event.getEntity().hasMetadata("commands")) {
				
				List<String> commands = (List<String>)event.getEntity().getMetadata("commands").get(0).value();
				if (commands != null && commands.size() > 0) {
					for (String cmd : commands) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + loc.getX() + " " + loc.getY() + " " + loc + " as @e[type=!armor_stand,type=!item_frame,type=!painting,type=!glow_item_frame,limit=1,sort=nearest] run " + cmd);
					}
				}
			}
			
			//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				SFX.play(loc, (Sound)event.getEntity().getMetadata("sound").get(0).value(), 1, 1);
			//}
			
			loc.getWorld().spawnParticle((Particle)event.getEntity().getMetadata("particle").get(0).value(), loc, 20, 0.1, 0.1, 0.1);
			
			event.setCancelled(true);
		}
		else if (event.getEntity().getScoreboardTags().contains("ThrownBottle") || event.getEntity().getItem().getType() == Material.GLASS_BOTTLE) {
			
			Entity hitEntity = event.getHitEntity();
			//Block hitBlock = event.getHitBlock();
			
			Location loc = event.getEntity().getLocation();
			
			if (hitEntity != null && hitEntity instanceof LivingEntity le) {
				le.damage(1, (Entity)event.getEntity().getShooter());
			}
			
			//for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				SFX.play(loc, Sound.BLOCK_GLASS_BREAK, 1, 1.1f);
				
			//}
			
			loc.getWorld().spawnParticle(Particle.BLOCK, loc, 20, 0.1, 0.1, 0.1, 0.1, Material.GLASS.createBlockData());
			
			event.setCancelled(true);
		}
	}
	
	public static WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		p = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		return null;
	}
	
	public ItemStack findInventoryItem(Player player, Material mat) {
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null && item.getType() == mat)
				return item;
		}
		return null;
	}
	
	public void bulletUpdate() {
		if (thrownItems != null) {
			for (ArmorStand proj : thrownItems.keySet()) {
				Vector vel = thrownItems.get(proj);
				proj.teleport(proj.getLocation().add(vel.clone()));
				
				boolean spin = (boolean) proj.getMetadata("spin").get(0).value();
				
				if (spin) {
					proj.setHeadPose(proj.getHeadPose().add(0, -7, 0));
				}
				
				if (proj.getScoreboardTags().contains("ThrownItem")) {

					vel = vel.add(new Vector(0, -0.02, 0));
					thrownItems.put(proj, vel);
					
					/*
					 	proj.setMetadata("damage", new FixedMetadataValue(this, damage));
						proj.setMetadata("knockback", new FixedMetadataValue(this, knockback));
						proj.setMetadata("returning", new FixedMetadataValue(this, returning));
						proj.setMetadata("returned", new FixedMetadataValue(this, false));
						proj.setMetadata("item", new FixedMetadataValue(this, item));
						proj.setMetadata("owner", new FixedMetadataValue(this, entity));
						proj.setMetadata("effects", new FixedMetadataValue(this, effects));
						proj.setMetadata("sound", new FixedMetadataValue(this, hitSound));
						proj.setMetadata("particle", new FixedMetadataValue(this, hitParticle));
					 */
					LivingEntity owner = (LivingEntity)proj.getMetadata("owner").get(0).value();
					Location loc = proj.getLocation().clone().add(0, 1.55f, 0);
					Collection<Entity> entities = loc.getWorld().getNearbyEntities(loc, 1f, 1f, 1f);
					boolean hit = false;
					for (Entity e : entities) {
						//Bukkit.broadcastMessage("Checking " + e.getName());
						if (e instanceof LivingEntity le && !(le instanceof ArmorStand)) {
							//Bukkit.broadcastMessage("Living entity " + le.getName() + " owner " + owner.getName());
							if (le != owner) {
								//Bukkit.broadcastMessage("Hit entity" + le.getName());
								thrownItemHit(proj, null, le);
								//Bukkit.broadcastMessage("Success!!!");
								hit = true;
								break;
							}
						}
					}
					if (!hit) {
						if (!loc.getBlock().isPassable()) {
							thrownItemHit(proj, loc.getBlock(), null);
							//Bukkit.broadcastMessage("Hit block" + loc.getBlock().getType().toString());
							hit = true;
						}
					}
					
				} else if (proj.getScoreboardTags().contains("ReturningItem")) {
					LivingEntity owner = (LivingEntity)proj.getMetadata("owner").get(0).value();
					Vector diff = owner.getLocation().toVector().clone().subtract(proj.getLocation().toVector());
					//proj.setVelocity(diff.normalize());
					thrownItems.put(proj, diff.clone().normalize().multiply(1.5));
					if (Math.sqrt(Math.pow(diff.getX(), 2) + Math.pow(diff.getY(), 2) + Math.pow(diff.getZ(), 2)) < 2) {
						if (owner instanceof Player player) {
							ItemStack item = proj.getEquipment().getHelmet();
							player.getInventory().addItem(item);
							//Bukkit.broadcastMessage("Giving " + item.getItemMeta().getDisplayName() + " to " + player.getName());
						}
						thrownItems.remove(proj);
						proj.remove();
					}
				}
			}
		}
		if (Bullet.bullets == null)
			return;
		ArrayList<Bullet> removeBullets = new ArrayList<Bullet>();
		for (Iterator<Bullet> ib = Bullet.bullets.iterator(); ib.hasNext(); ) {
			Bullet b = ib.next();
			b.update();
			if (b.isDead())
				removeBullets.add(b);
		}
		for (Bullet b : removeBullets) {
			Bullet.bullets.remove(b);
		}
		/*
		if (Bukkit.getServer().getOnlinePlayers().size() > 0) {
			List<Entity> shootingEntities = Bukkit.selectEntities(Bukkit.getServer().getOnlinePlayers().iterator().next(), "@e[tag=fire_bullet]");
			for (Entity e : shootingEntities) {
				if (e instanceof LivingEntity le)
					fireBullet(le);
				//item.setAmount(item.getAmount()-1);
				e.removeScoreboardTag("fire_bullet");
			}
		}*/
		if (arrowBarrage.size() > 0) {
			for (LivingEntity p : arrowBarrage.keySet()) {
				if (arrowBarrage.get(p) > 0) {
					arrowBarrage.put(p, arrowBarrage.get(p)-1);
					
					Arrow fb = p.getWorld().spawn(p.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(p.getLocation().getDirection()), Arrow.class);
					//fb.setFireTicks(20);
					//fb.setIsIncendiary(true);
					//fb.setYield(3);
					//fb.setBasePotionType(PotionType.STRONG_POISON);
					fb.setShooter((ProjectileSource) p);
					fb.setVelocity(p.getLocation().getDirection().multiply(Math.random()*3 + 2).add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
					fb.setPierceLevel(1);
					fb.setPickupStatus(PickupStatus.DISALLOWED);
					
					EntityKiller k = new EntityKiller(fb, 80);
					for (Player p2 : Bukkit.getServer().getOnlinePlayers())
						p2.playSound(p2.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1f, 1f);
				}
				if (arrowBarrage.get(p) <= 0) {
					arrowBarrage.remove(p);
				}
			}
		}
		if (royalArrowBarrage.size() > 0) {
			for (LivingEntity p : royalArrowBarrage.keySet()) {
				if (royalArrowBarrage.get(p) > 0) {
					royalArrowBarrage.put(p, royalArrowBarrage.get(p)-1);
					
					Arrow fb = p.getWorld().spawn(p.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(p.getLocation().getDirection()), Arrow.class);
					//fb.setFireTicks(20);
					//fb.setIsIncendiary(true);
					//fb.setYield(3);
					//fb.setBasePotionType(PotionType.STRONG_POISON);
					fb.setShooter((ProjectileSource) p);
					fb.setVelocity(p.getLocation().getDirection().multiply(Math.random()*3 + 2).add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
					fb.setPierceLevel(2);
					fb.setDamage(fb.getDamage() + 2);
					fb.setFireTicks(20);
					fb.setPickupStatus(PickupStatus.DISALLOWED);
					arrowTrail.put(fb, Particle.CHERRY_LEAVES);
					
					EntityKiller k = new EntityKiller(fb, 80);
					for (Player p2 : Bukkit.getServer().getOnlinePlayers())
						p2.playSound(p2.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1f, 1.5f);
				}
				if (royalArrowBarrage.get(p) <= 0) {
					royalArrowBarrage.remove(p);
				}
			}
		}
		if (supremeArrowBarrage.size() > 0) {
			for (LivingEntity p : supremeArrowBarrage.keySet()) {
				if (supremeArrowBarrage.get(p) > 0) {
					supremeArrowBarrage.put(p, supremeArrowBarrage.get(p)-1);
					
					Arrow fb = p.getWorld().spawn(p.getLocation().add(new Vector(0.0D, 1.5D, 0.0D)).add(p.getLocation().getDirection()), Arrow.class);
					//fb.setFireTicks(20);
					//fb.setIsIncendiary(true);
					//fb.setYield(3);
					//fb.setBasePotionType(PotionType.STRONG_POISON);
					fb.setShooter((ProjectileSource) p);
					fb.setVelocity(p.getLocation().getDirection().multiply(Math.random()*4 + 4).add(new Vector((Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5, (Math.random()-0.5)*1.5)));
					fb.setPierceLevel(3);
					fb.setDamage(fb.getDamage() + 4);
					fb.setFireTicks(40);
					fb.setPickupStatus(PickupStatus.DISALLOWED);
					arrowTrail.put(fb, Particle.END_ROD);
					
					EntityKiller k = new EntityKiller(fb, 80);
					for (Player p2 : Bukkit.getServer().getOnlinePlayers())
						p2.playSound(p2.getLocation(), Sound.ENTITY_SKELETON_SHOOT, 1f, 1.5f);
				}
				if (supremeArrowBarrage.get(p) <= 0) {
					supremeArrowBarrage.remove(p);
				}
			}
		}
		if (firebreath.size() > 0) {
			for (LivingEntity p : firebreath.keySet()) {
				if (firebreath.get(p) > 0) {
					fireBreath(p, firebreath.get(p));
					firebreath.put(p, firebreath.get(p)-1);
				}
				
				if (firebreath.get(p) <= 0) {
					firebreath.remove(p);
				}
			}
		}

		if (dragonbreath.size() > 0) {
			for (LivingEntity p : dragonbreath.keySet()) {
				if (dragonbreath.get(p) > 0) {
					dragonBreath(p);
					dragonbreath.put(p, dragonbreath.get(p)-1);
				}
				
				if (dragonbreath.get(p) <= 0) {
					dragonbreath.remove(p);
				}
			}
		}
	}
	
	public void saxophone(LivingEntity e, int lvl) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Location shootLoc = e.getEyeLocation().add(offset);
		
		Vector dir = shootLoc.getDirection();
		//shootLoc.getWorld().spawnParticle(Particle.NOTE, shootLoc, 6, 0.4, 0.1, 0.1, 0.1);
		
		switch ((int)(Math.random()*6)) {
			case 0:
			{
				if (lvl == 0) {
					// Three music note beams
					Bullet b = new Bullet(dir.clone().multiply(1.2), shootLoc, false, Particle.NOTE, 20, 0.8f, 1, e, this, 5, 3, 0.7f, 0.1f);
				} else if (lvl == 1) {
					// 5 music note beams
					Bullet b = new Bullet(dir.clone().multiply(1.3), shootLoc, false, Particle.NOTE, 20, 0.8f, 1, e, this, 11, 5, 0.9f, 0.2f);
				} else if (lvl == 2) {
					// 7 music note beams
					Bullet b = new Bullet(dir.clone().multiply(1.6), shootLoc, false, Particle.NOTE, 20, 0.8f, 1, e, this, 18, 7, 0.95f, 0.3f);
				}
				break;
			}
			case 1:
			{
				// Guiding bolt
				Bullet b = null;
				if (lvl == 0)
					b = new Bullet(dir.clone().multiply(1.5), shootLoc, false, Particle.GUST, 80, 0.6f, 13, e, this);
				else if (lvl == 1)
					b = new Bullet(dir.clone().multiply(1.7), shootLoc, false, Particle.GUST, 80, 0.6f, 13, e, this, 20);
				else if (lvl == 2)
					b = new Bullet(dir.clone().multiply(1.9), shootLoc, false, Particle.GUST, 80, 0.6f, 13, e, this, 29);
				break;
			}
			case 2:
			{
				// Levitate
				Bullet b = new Bullet(dir.clone().multiply(1.2), shootLoc, false, Particle.NOTE, 80, 0.6f, 14, e, this);
				break;
			}
			case 3:
			{
				// Enervation
				Bullet b = null;
				if (lvl == 0)
					b = new Bullet(dir.clone().multiply(1.2), shootLoc, false, Particle.SMOKE, 80, 0.6f, 8, e, this, 6);
				else if (lvl == 1)
					b = new Bullet(dir.clone().multiply(1), shootLoc, true, Particle.SMOKE, 80, 0.6f, 8, e, this, 12);
				else if (lvl == 2)
					b = new Bullet(dir.clone().multiply(1), shootLoc, true, Particle.SMOKE, 80, 0.6f, 31, e, this, 18);
				break;
			}
			case 4:
			{
				// Prismatic
				Bullet b = null;
				if (lvl == 0)
					b = new Bullet(dir.clone().multiply(0.9), shootLoc, false, Particle.SMOKE, 80, 0.6f, 9, e, this, 9);
				else if (lvl == 1)
					b = new Bullet(dir.clone().multiply(1.2), shootLoc, false, Particle.GLOW_SQUID_INK, 80, 0.6f, 9, e, this, 15);
				else if (lvl == 2)
					b = new Bullet(dir.clone().multiply(1), shootLoc, true, Particle.GUST, 80, 0.6f, 9, e, this, 31);
				break;
			}
			case 5:
			case 6:
			{
				Bullet b = null;
				if (lvl == 0)
					b = new Bullet(dir.clone(), shootLoc, true, Particle.FIREWORK, 120, 0.2f, 3, e, this, 10);
				else if (lvl == 1)
					b = new Bullet(dir.clone(), shootLoc, true, Particle.FIREWORK, 120, 0.2f, 7, e, this, 16);
				else if (lvl == 2)
					launchMeteor(e);
				break;
			}
		}
		//Bullet b = new Bullet(dir, shootLoc, false, Particle.LAVA, 6, 0.9f, 2, e, this, 6, 10, 0.35f, 0.25f);
		
		float pitch = 0.5f + (float)(Math.random()*1.5f);
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 2f, pitch);
	}
	
	public void fireBreath(LivingEntity e, int n) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getEyeLocation().add(offset);
		Vector dir = loc.getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		Vector up = right.clone().crossProduct(dir).normalize();

		double i = Math.sin(n * 2*Math.PI / 10) * 0.4;
		double j = Math.sin(n * 4*Math.PI / 10) * 0.4;
		
		Bullet b = new Bullet((dir.clone().add(right.clone().multiply(i)).add(up.clone().multiply(j)).normalize().multiply(1D) ), loc.clone(), false, Particle.FLAME, 10, 0.3f, 2, e, this);
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1.6f);
		
		
		//for (double i = -0.5; i <= 0.5; i += 0.5) {
		//	for (double j = -0.5; j <= 0.5; j += 0.5) {
				
		//	}
		//}
	}

	public void dragonBreath(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Location shootLoc = e.getEyeLocation().add(offset);
		
		Vector dir = shootLoc.getDirection();
		if (e.getType() == EntityType.ENDER_DRAGON || e.getType() == EntityType.WARDEN || e.getType() == EntityType.WITHER || e.getType() == EntityType.IRON_GOLEM)
		{
			float dist = 100000;
			LivingEntity target = null;
			for (Player p : e.getWorld().getPlayers()) {
				if (p.isOnline()) {
					if (e.getWorld().equals(p.getWorld())) {
						float newDist = (float) e.getEyeLocation().distance(p.getLocation());
						if (target == null || newDist < dist) {
							dist = newDist;
							target = p;
							if (illusionDoubles.containsKey(p)) {
								if (e.getWorld().equals(illusionDoubles.get(p).getWorld())) {
									newDist = (float) e.getEyeLocation().distance(illusionDoubles.get(p).getLocation());
									if (newDist < dist)
									{
										dist = newDist;
										target = illusionDoubles.get(p);
									}
								}
							}
						}
					}
				}
			}
			if (target != null) {
				dir = (target.getLocation().toVector().add(target.getVelocity()).subtract(e.getEyeLocation().toVector())).normalize();
			}
		}
		
		//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(dir.clone().multiply(0.3D));
		shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1, 0, 0, 0, 0);
		
		Bullet b = new Bullet(dir.multiply(1.5f), shootLoc, false, Particle.LAVA, 100, 0.9f, 2, e, this, supremeDragonBreath ? 50 : 22, (int)(Math.random()*9+1), 0.15f, 0.1f);
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1.1f);
	}
	
	public void firebreath4(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Location shootLoc = e.getEyeLocation().add(offset);
		
		Vector dir = shootLoc.getDirection();
		
		//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(dir.clone().multiply(0.3D));
		shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1, 0, 0, 0, 0);
		
		Bullet b = new Bullet(dir, shootLoc, false, Particle.LAVA, 12, 0.9f, 2, e, this, 18, 12, 0.55f, 0.4f);
		
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1.1f);
	}
	
	public void firebreath3(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Location shootLoc = e.getEyeLocation().add(offset);
		
		Vector dir = shootLoc.getDirection();
		
		//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(dir.clone().multiply(0.3D));
		shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1, 0, 0, 0, 0);
		
		Bullet b = new Bullet(dir, shootLoc, false, Particle.LAVA, 9, 0.9f, 2, e, this, 16, 10, 0.5f, 0.3f);
		
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1.4f);
	}
	
	public void firebreath2(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Location shootLoc = e.getEyeLocation().add(offset);
		
		Vector dir = shootLoc.getDirection();
		
		//Location shootLoc = loc.add(new Vector(0.0D, 1.55D, 0.0D)).add(dir.clone().multiply(0.3D));
		shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1, 0, 0, 0, 0);
		
		Bullet b = new Bullet(dir, shootLoc, false, Particle.LAVA, 6, 0.9f, 2, e, this, 6, 10, 0.35f, 0.25f);
		
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_BLAZE_SHOOT, 0.7f, 1.6f);
	}
	
	public void fireShotgun(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		

		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 10f, 1.1f);
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.9f);
			//SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.2f);
		//}
		
		Bullet b = new Bullet(dir, shootLoc, true, Particle.SMOKE, 40, 0.4f, 1, e, this, 18, 6, 0.45f, 0.35f);
		
	}
	
	public void fireAntimatterCore(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc  =e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		//shootLoc.getWorld().spawnParticle(Particle.LAVA, shootLoc, 1, 0, 0, 0, 0);
		shootLoc.getWorld().spawnParticle(Particle.EXPLOSION, shootLoc, 1, 0, 0, 0, 0);

		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_ENDER_PEARL_THROW, 10f, 0.7f);
			SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.4f);
		//}
		
		Bullet b = new Bullet(dir, shootLoc, false, Particle.LAVA, 90, 0.5f, 20, e, this);
		
	}

	public void fireSmallAntimatter(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		
		ItemStack gattlingGun = null;
		if (e.getEquipment().getItemInMainHand().getType() == Material.IRON_HOE) {
			gattlingGun = e.getEquipment().getItemInMainHand();
		} else if (e.getEquipment().getItemInOffHand().getType() == Material.IRON_HOE) {
			gattlingGun = e.getEquipment().getItemInOffHand();
		}
		if (gattlingGun != null) {
			ItemMeta meta = gattlingGun.getItemMeta();
			if (meta.getCustomModelData() == 106)
				meta.setCustomModelData(107);
			else if (meta.getCustomModelData() == 107)
				meta.setCustomModelData(108);
			else if (meta.getCustomModelData() == 108)
				meta.setCustomModelData(106);
			gattlingGun.setItemMeta(meta);
		}
		
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_ENDERMAN_SCREAM, 10f, 1.6f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.5f);
			SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.2f);
		//}
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.LAVA, shootLoc, 1, 0, 0, 0, 0);
		shootLoc.getWorld().spawnParticle(Particle.EXPLOSION, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.DRIPPING_LAVA, 90, 0.8f, 19, e, this, 20);
		
	}
	

	
	public void fireAntimatter(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1.3f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.8f);
			SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1.6f);
		//}
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.LAVA, shootLoc, 1, 0, 0, 0, 0);
		shootLoc.getWorld().spawnParticle(Particle.EXPLOSION, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.DRIPPING_LAVA, 90, 0.8f, 19, e, this);
	}
	
	public void fireSuperHeavyBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		shootLoc.getWorld().spawnParticle(Particle.GUST, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.FIREWORK, 110, 0.3f, 1, e, this, 26);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.8f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 0.9f);
			SFX.play(loc, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.6f);
		//}
	}
	
	public void fireHeavyBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 100, 0.2f, 1, e, this, 17);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.9f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.2f);
		//}
	}

	public void fireRifleBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 80, 0.2f, 1, e, this, 12);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.1f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.4f);
		//}
	}

	private void fireDalekBullet(LivingEntity e, int dmg) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir.clone().multiply(0.2f), shootLoc, true, Particle.END_ROD, 100+dmg*2, 0.2f, 32, e, this, dmg);
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run playsound dalek_shoot hostile @a ~ ~ ~");
			//SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.2f);
			//SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.8f);
	}
	
	private void fireExplosiveDalekBullet(LivingEntity e, int dmg) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLASH, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir.clone().multiply(0.2f), shootLoc, true, Particle.END_ROD, 100+dmg*2, 0.3f, 33, e, this, dmg);
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + loc.getWorld().getName().strip().toLowerCase() + " positioned " + loc.getX() + " " + loc.getY() + " " + loc.getZ() + " run playsound dalek_shoot_big hostile @a ~ ~ ~");
			//SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.2f);
			//SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.8f);
	}
	
	public void fireRoyalPistolBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.LAVA, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.DRIPPING_LAVA, 40, 0.2f, 1, e, this, 16);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.2f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.8f);
		//}
	}
	
	public void firePistolBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 20, 0.2f, 1, e, this, 5);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.2f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.8f);
		//}
	}
	
	public void fire9mmBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 20, 0.2f, 1, e, this, 5);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.4f);
			//SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.8f);
		//}
	}
	
	public void fireRevolverBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 20, 0.2f, 1, e, this, 6);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 1.2f);
			SFX.play(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10f, 1.9f);
		//}
	}
	
	public boolean checkMinigunLineOfSight(ArmorStand gun, Vector dir, LivingEntity owner, LivingEntity target) {
		dir = dir.normalize();
		Location loc = gun.getEyeLocation();
		
		//Vector dir = loc.getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = loc.add(new Vector(0, 0.85, 0)).add(right.multiply(0.12).add(dir.clone().multiply(1)));
		
		return Bullet.LineOfSight(dir, shootLoc, 20, 0.2f, owner, target, this);
	}
	
	public void fireMinigunBullet(ArmorStand gun, Vector dir, LivingEntity owner) {
		//Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		// TODO Auto-generated method stub
		dir = dir.normalize();
		Location loc = gun.getEyeLocation();
		
		//Vector dir = loc.getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = loc.add(new Vector(0, 0.85, 0)).add(right.multiply(0.12).add(dir.clone().multiply(1)));
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 20, 0.2f, 1, owner, this, 11);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.2f);
		//}
	}
	
	public void throwBottle(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getEyeLocation().add(offset);
		Vector dir = loc.getDirection();
		
		ThrownPotion bottle = (ThrownPotion)loc.getWorld().spawnEntity(loc, EntityType.POTION);
		
		//for (Player p : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_EGG_THROW, 1, 1.1F);
		
		bottle.setShooter(e);
		bottle.setVelocity(dir.clone().multiply(1.5));
		bottle.setItem(new ItemStack(Material.GLASS_BOTTLE));
		bottle.addScoreboardTag("ThrownBottle");
	}
	
	public void fireBullet(LivingEntity e) {
		Vector offset = e instanceof Giant ? new Vector(e.getEyeLocation().getDirection().getX(), 0, e.getEyeLocation().getDirection().getZ()).normalize().multiply(8) : new Vector(0, 0, 0);
		Location loc = e.getLocation();
		Vector dir = e.getEyeLocation().getDirection();
		Vector right = dir.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		
		Location shootLoc = e.getEyeLocation().add(right.multiply(0.1D).add(dir.clone().multiply(0.3D))).add(offset);
		shootLoc.getWorld().spawnParticle(Particle.FLAME, shootLoc, 1, 0, 0, 0, 0);
		Bullet b = new Bullet(dir, shootLoc, true, Particle.CRIT, 50, 0.3f, 1, e, this);
		
		
		//for (Player player : Bukkit.getServer().getOnlinePlayers())
			SFX.play(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.9f);
	}
	

	
	public void entityKillerUpdate() {
		if (EntityKiller.killers == null)
			return;
		for (Iterator<EntityKiller> ik = EntityKiller.killers.iterator(); ik.hasNext(); ) {
			EntityKiller k = ik.next();
			k.update();
			if (k.dead)
				Bullet.bullets.remove(k);
		}
	}
	
	@EventHandler
    public void onEntityDamagedByLightning(EntityDamageByEntityEvent event) {
        // Check if the damager is a lightning strike
        if (event.getDamager() instanceof LightningStrike) {
            LightningStrike lightning = (LightningStrike) event.getDamager();
            
            if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("PrimordialWard")) {
            	event.setCancelled(true);
            } else if (event.getEntity() instanceof LivingEntity le && le.getScoreboardTags().contains("ElementalWard")) {
            	event.setDamage(event.getDamage()/2);
            }

            // Check if the lightning has an owner
            if (lightning.hasMetadata("lightningOwner") && lightning.getMetadata("lightningOwner").get(0).value() instanceof LivingEntity owner && event.getEntity() instanceof Damageable de) {
                //LivingEntity owner = (LivingEntity) lightning.getMetadata("lightningOwner").get(0).value();

                //event.setDamage(event.getDamage(), owner);
                de.damage(event.getFinalDamage(), owner);
                
                event.setCancelled(true);
            }
        }
    }
	
}
