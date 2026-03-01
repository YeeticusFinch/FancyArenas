package com.lerdorf.fancy_arena;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.attribute.Attribute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Hangable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.royawesome.jlibnoise.MathHelper;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Bullet {

	public static List<Bullet> bullets = new ArrayList<Bullet>();
	
	public Vector velocity;
	public Location location;
	public boolean hitscan;
	public Particle particle;
	public int range;
	public boolean[] dead = new boolean[] {false};
	public float size;
	public int hitEffect;
	public LivingEntity owner;
	public Plugin plugin;
	public int damage;
	public int projectiles;
	public float spreadX;
	public float spreadY;
	
	public static final int PHYSICAL = 0;
	public static final int ELEMENTAL = 1;
	public static final int PSYCHIC = 2;
	public static final int NECROTIC = 3;
	public static final int DIVINE = 4;
	public static final int FORCE = 5;
	
	public boolean magic = false;
	
	public boolean monsterBullet = false;
	
	int initialRange = 100;
	
	public Vector up = null;
	public Vector right = null;
	
	public int[] magicBullets = new int[] {0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 21, 22, 23, 24, 26, 27, 28, 29, 30};
	
	public LivingEntity target;
	
	public static boolean LineOfSight(Vector dir, Location loc, int range, float size, LivingEntity owner, LivingEntity target, Plugin plugin) {
		for (int i = 0; i < range; i++) {
			loc.add(dir);
			boolean hitEntity = false;
			Collection<Entity> hitEntities = loc.getWorld().getNearbyEntities(loc, size*4, size*4, size*4);
			if (hitEntities.size() > 0) {
				Vector minVector = loc.toVector().clone().subtract(new Vector(size*0.6, size*0.6, size*0.6));
				Vector maxVector = loc.toVector().clone().add(new Vector(size*0.6, size*0.6, size*0.6));
				Vector minVector2 = loc.toVector().clone().add(dir.clone().multiply(0.5)).subtract(new Vector(size*0.6, size*0.6, size*0.6));
				Vector maxVector2 = loc.toVector().clone().add(dir.clone().multiply(0.5)).add(new Vector(size*0.6, size*0.6, size*0.6));
				
				for (Entity e : hitEntities) {
					if (e.getType() == EntityType.ARMOR_STAND)
						continue;
					
					if (e instanceof Damageable de && !e.equals(owner) && distance(e.getLocation(), owner.getLocation()) > 0.6) {
						BoundingBox box = e.getBoundingBox();
						//boolean cancelled = false;
						if (box.overlaps(minVector, maxVector) || box.overlaps(minVector2, maxVector2)) {
							hitEntity = true;
							if (e.equals(target))
								return true;
							break;
						}
					}
				}
			}
			if ((intersectsBlock(loc, size*0.3) || hitEntity)) {
				if (loc.distance(target.getLocation()) < 1 || loc.distance(target.getEyeLocation()) < 1)
					return true;
				return false;
			}
		}
		return true;
	}
	
	public Bullet(Vector velocity, Location location, boolean hitscan, Particle particle, int range, float size, int hitEffect, LivingEntity owner, Plugin plugin) {
		Construct(velocity, location, hitscan, particle, range, size, hitEffect, owner, plugin, -1, 1, 0, 0);
	}
	
	void Construct(Vector velocity, Location location, boolean hitscan, Particle particle, int range, float size, int hitEffect, LivingEntity owner, Plugin plugin, int damage, int projectiles, float spreadX, float spreadY) {		
		this.location = location;
		this.velocity = velocity;
		this.hitscan = hitscan;
		this.range = range;
		this.hitEffect = hitEffect;
		this.particle = particle;
		this.owner = owner;
		this.plugin = plugin;
		this.damage = damage;
		this.projectiles = projectiles;
		this.spreadX = spreadX;
		this.spreadY = spreadY;
		this.size = size;
		initialRange = range;
		bullets.add(this);
		
		for (int m : magicBullets) {
			if (hitEffect == m) {
				magic = true;
				break;
			}
		}
		
		if (owner != null && owner.getScoreboardTags().contains("ArenaMob") || owner.getScoreboardTags().contains("FancyMob"))
			monsterBullet = true;
		
		right = velocity.clone().crossProduct(new Vector(0, 1, 0)).normalize();
		up = right.clone().crossProduct(velocity).normalize();
		
		dead = new boolean[projectiles];
		for (int i = 0; i < projectiles; i++)
			dead[i] = false;
		
		if (hitscan) {
			while (hitscan && !isDead()) {
				bulletStep();
			}
		}
	}
	
	public Bullet(Vector velocity, Location location, boolean hitscan, Particle particle, int range, float size, int hitEffect, LivingEntity owner, Plugin plugin, int damage) {
		Construct(velocity, location, hitscan, particle, range, size, hitEffect, owner, plugin, damage, 1, 0, 0);
	}
	
	public Bullet(Vector velocity, Location location, boolean hitscan, Particle particle, int range, float size, int hitEffect, LivingEntity owner, Plugin plugin, int damage, int projectiles, float spreadX, float spreadY) {
		Construct(velocity, location, hitscan, particle, range, size, hitEffect, owner, plugin, damage, projectiles, spreadX, spreadY);
	}
	
	public boolean isDead() {
		for (boolean b : dead) {
			if (!b)
				return false;
		}
		return true;
	}
	
	public void update() {
		if (!hitscan)
		{
			bulletStep();
		}
	}
	
	int cc = 0;
	public void bulletStep() {
		range -= 1;
		
		
		for (int i = 0; i < projectiles; i++) {
			if (dead[i])
				continue;
			else {
				Location loc = location.clone();
				
				if (i >= 1 && projectiles > 1 && spreadX + spreadY > 0)
					loc = loc.add(right.clone().multiply(Math.cos((i-1) * 2.0*Math.PI / (projectiles-1)) * spreadX * cc)).add(up.clone().multiply(Math.sin((i-1) * 2.0*Math.PI / (projectiles-1)) * spreadY * cc));
				
				if (range < 0) {
					hit(null, null, i, location);
				}
				if (particle != null) {
					loc.getWorld().spawnParticle(particle, loc, 1, 0, 0, 0, 0);
					if (hitEffect == 6) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
						location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 1, 0, 0, 0, 0);
					} else if (hitEffect == 9) {
						Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
						FireworkMeta meta = fw.getFireworkMeta();
						meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.YELLOW).withColor(Color.WHITE).withColor(Color.AQUA).withColor(Color.GREEN).withColor(Color.RED).withColor(Color.PURPLE).with(Type.BURST).build());
						fw.setFireworkMeta(meta);
						fw.detonate();
					} else if (hitEffect == 10) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
					} else if (hitEffect == 15) {
						if (target == null) {
							
						} else {
							double speed = velocity.length();
							Vector targetVel = target.getEyeLocation().toVector().add(target.getVelocity()).subtract(location.toVector().add(velocity)).multiply(speed);
							velocity = velocity.add(targetVel.subtract(velocity).normalize().multiply(0.4*speed)).normalize().multiply(speed);
						}
					} else if (hitEffect == 16) {
						location.getWorld().spawnParticle(Particle.BLOCK, location, 1, 0.1, 0, 0, 0, Material.OAK_LEAVES.createBlockData());
					} else if (hitEffect == 19) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
						location.getWorld().spawnParticle(Particle.SONIC_BOOM, location, 1, 0, 0, 0, 0);
						//location.getWorld().spawnParticle(Particle.SMALL_GUST, location, 1, 0, 0, 0, 0);
					} else if (hitEffect == 20) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
						location.getWorld().spawnParticle(Particle.SQUID_INK, location, 1, 0, 0, 0, 0);
					} else if (hitEffect == 23) {
						location.getWorld().spawnParticle(Particle.BLOCK, location, 1, 0.1, 0, 0, Material.REDSTONE_BLOCK.createBlockData());
					} else if (hitEffect == 25) {
						double speed = velocity.length();
						Vector targetVel = target.getEyeLocation().toVector().add(target.getVelocity()).subtract(location.toVector().add(velocity)).multiply(speed);
						velocity = velocity.add(targetVel.subtract(velocity).normalize().multiply(0.4*speed)).normalize().multiply(speed);
						loc.getWorld().spawnParticle(particle, loc.clone().subtract(velocity.clone().multiply(0.5)), 1, 0, 0, 0, 0);
					} else if (hitEffect == 26) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
						location.getWorld().spawnParticle(Particle.GUST, location, 1, 0, 0, 0, 0);
					} else if (hitEffect == 30) {
						location.getWorld().spawnParticle(Particle.FLASH, location, 1, 0, 0, 0, 0);
					}
				}
				if (magic)
					CheckAntimagic(loc, i);
				CheckEntities(loc, i);
				if (hitEffect != 25 && hitEffect != 15)
					CheckLocation(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ()), i);
				if (dead[i]) return;
			}
			
		}

		if (!isDead()) {
			location = location.add(velocity);
			//range -= 1;
			
			if (range <= 0) {
				for (int i = 0; i < projectiles; i++)
					if (!dead[i])
						hit(null, null, i, location);
			}
		}

		if (cc > 300 || cc > initialRange) {
			range = -1;
			for (int i = 0; i < projectiles; i++)
				dead[i] = true;
		}
		
		cc++;
	}
	
	Vector proj(Vector a, Vector b) { // Returns a vector along b
		return b.multiply(a.dot(b) / b.lengthSquared());
	}
	
	Vector gramSchmidt(Vector a, Vector b) { // Returns a vector perpendicular to a in the direction of b
		return b.subtract(proj(b, a));
	}
	
	Vector normalize(Vector v) {
		return v.multiply(1/v.length());
	}
	
	public static void damage(Damageable entity, float damage, Entity source, int type) {
		
		if (entity.getScoreboardTags().contains("ElementalWard")) {
			if (type == ELEMENTAL)
				damage /= 2;
		} else if (entity.getScoreboardTags().contains("PrimordialWard")) {
			if (type == ELEMENTAL || type == NECROTIC || type == DIVINE)
				damage *= 0.2f;
		}
		if (entity.getScoreboardTags().contains("Unholy")) {
			if (type == NECROTIC)
				damage /= 2;
			else if (type == DIVINE)
				damage *= 2;
		}
		else if (entity.getScoreboardTags().contains("Construct")) {
			if (type == PSYCHIC)
				damage *= 0.1;
			else if (type == ELEMENTAL)
				damage *= 0.5;
			else if (type == PHYSICAL || type == FORCE)
				damage *= 2;
		}
		else if (entity.getScoreboardTags().contains("Hired")) {
			if (type == PSYCHIC)
				damage *= 2;
		}
		
		entity.damage(damage, source);
	}
	
	public void hit(Block block, Damageable entity, int k, Location location) {
		if (block == null && entity == null) {
			dead[k] = true; // Range expired — always kill, even piercing bullets
		} else if ((hitEffect == 17) && (block != null || entity != null)) // Go through entities or blocks
			dead[k] = dead[k]; // Don't stop the bullet
		else if (block == null && (hitEffect == 6 || hitEffect == 7 || hitEffect == 9 || hitEffect == 19 || hitEffect == 26)) // Pierce through entities
			dead[k] = dead[k]; // Don't stop the bullet
		else
			dead[k] = true; // Stop the bullet
		
		switch (hitEffect) {
			case 0: // Shield
				int radius = 4;
				Vector right = velocity.clone().normalize().rotateAroundY(Math.PI/2).normalize();
				location = location.getBlock().getLocation();
				for (float i = 0f; i < Math.PI; i += Math.PI/8) {
					Vector dir = right.clone().rotateAroundNonUnitAxis(velocity, i).normalize();
					for (int j = -radius; j < radius; j++) {
						Location l = location.clone().add(dir.clone().multiply(j));
						if (l.getBlock().isEmpty())
							l.getBlock().setType(Material.LIGHT_BLUE_STAINED_GLASS);
					}
				}
				break;
			case 1: // Bullet
				if (entity != null && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 9, owner, PHYSICAL);
					//entity.damage(damage != -1 ? damage : 9, owner);
				}
				
					SFX.play(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 10f, 0.5f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0);
				//location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 1, 0, 0, 0, 0);
				
				break;
			case 2: // fire breath
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 8, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 8, owner);
					entity.setFireTicks(60);
				}
				
					SFX.play(location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.2f);
				location.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location, 1, 0, 0, 0, 0);
				Block blockAbove = location.clone().add(0, 1, 0).getBlock();
				if (blockAbove.getType() == Material.AIR)
					blockAbove.setType(Material.FIRE);
				break;
			case 3: // Lightning
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 4, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 4, owner);
					entity.setFireTicks(30);
				}
				
				LightningStrike lightning = location.getWorld().strikeLightning(location);
	            lightning.setMetadata("lightningOwner", new FixedMetadataValue(plugin, owner));
	            
				break;
			case 4: // Thunderstep
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 3, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 3, owner);
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.4f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FIREWORK, location, 15, 1, 0.1, 0.1, 0.1);
				
				Location tpLoc = location.clone().setDirection(owner.getLocation().getDirection());
				Vector sub = velocity.clone().multiply(0.5);
				boolean intersects = intersectsBlock(tpLoc, 0.8);
				int c = 0;
				while (intersects) {
					tpLoc = tpLoc.subtract(sub);
					intersects = intersectsBlock(tpLoc, 0.8);
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(0, 0.5, 0)), 0.8);
					}
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(0, -0.5, 0)), 0.8);
					}
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(0.5, 0, 0)), 0.8);
					}
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(-0.5, 0, 0)), 0.8);
					}
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(0, 0, 0.5)), 0.8);
					}
					if (intersects) {
						intersects = intersectsBlock(tpLoc.clone().add(new Vector(0, 0, -0.5)), 0.8);
					}
					if (c > initialRange)
						break;
					c++;
				}
				if (!intersects) {
					if (owner.getPassengers().size() > 0)
						owner.removePassenger(owner.getPassengers().get(0));
					if (owner.getScoreboardTags().contains("Grapple")) {
						owner.removeScoreboardTag("Grapple");
						//FancyArena.instance.grapple.remove(entity);
					}
					owner.teleport(tpLoc.setDirection(owner.getLocation().getDirection()));
				}
				
				break;
			case 5: // Repulsor
			{
				double knockback = damage != -1 ? damage/14.0 : 1;
				if (entity != null && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 7, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 7, owner);
					if (!entity.getScoreboardTags().contains("invulnerable") && !entity.getScoreboardTags().contains("fom"))
						entity.setVelocity(entity.getVelocity().add(velocity.clone().multiply(knockback)));
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.4f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.1, 0.1, 0.1);
				
				Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5);
				for (Entity e : nearbyEntities) {
					if (e instanceof LivingEntity le && !(le instanceof ArmorStand) && !(le instanceof Hangable) && le != entity && Arena.validTarget(le)) {
						Vector diff = le.getLocation().toVector().clone().subtract(location.toVector());

						if (!le.getScoreboardTags().contains("invulnerable") && !le.getScoreboardTags().contains("fom"))
							le.setVelocity(le.getVelocity().add(diff.normalize().multiply(knockback)));
					}
				}
	            
				break;
			}
			case 6: // Disintegrate
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 26, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 26, owner);
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_GENERIC_EXPLODE, 10f, 1.3f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.1, 0.1, 0.1);
	            
				break;

			case 7: // Chain Lightning
			{
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 19, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 19, owner);
					entity.setFireTicks(5);
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10f, 1.3f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.1, 0.1, 0.1);
				
				Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 20, 20, 20);
				
				boolean foundEntity = false;
				
				for (Entity e : entities) {
					if (e instanceof Damageable le && le != entity && le != owner && le.getType() != EntityType.ARMOR_STAND && !(le instanceof Hangable) && Arena.validTarget(le)) {
						if (!foundEntity || Math.random() > 0.4) {
							Vector diff = new Vector(e.getLocation().getX() - location.getX(), e.getLocation().getY() - location.getY(), e.getLocation().getZ() - location.getZ());
							velocity = diff.normalize();
							foundEntity = true;
						} else if (Math.random() < 0.1)
							return;
					}
				}
				
				dead[k] = !foundEntity;
				//velocity
	            
				break;
			}
			case 8: // Enervation
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 12, owner, NECROTIC);
					//entity.damage(damage != -1 ? damage : 12, owner);
					if (entity instanceof LivingEntity le && !le.getScoreboardTags().contains("fom")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s wither 5 2");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slowness 8 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s darkness 8 10");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s weakness 4 10");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slow_falling 8 200");
						//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s glowing 8 100");
						//le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5*20, 2));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 8*20, 100));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 8*20, 10));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 4*20, 10));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 8*20, 200));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 8*20, 200));
					}
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.BLOCK_ANVIL_PLACE, 10f, 0.5f);
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, location, 15, 1, 0.5, 0.5, 0.5);
	            
				break;

			case 9: // Prismatic
			{
				double knockback = damage != -1 ? damage/22.0 : 1;
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 21, owner, DIVINE);
					//entity.damage(damage != -1 ? damage : 18, owner);
					if (!entity.getScoreboardTags().contains("invulnerable"))
						entity.setVelocity(entity.getVelocity().add(velocity.clone().multiply(knockback)));
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 10f, 2f);
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.5, 0.5, 0.5);
				
				Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(location, 1.5, 1.5, 1.5);
				for (Entity e : nearbyEntities) {
					if (e instanceof LivingEntity le && !(le instanceof ArmorStand) && !(le instanceof Hangable) && le != entity) {
						Vector diff = le.getLocation().toVector().clone().subtract(location.toVector());

						if (!le.getScoreboardTags().contains("invulnerable"))
							le.setVelocity(le.getVelocity().add(diff.normalize().multiply(knockback)));
					}
				}
	            
				break;
			}
			case 10: // Shooting Stars
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 19, owner, DIVINE);
					//entity.damage(damage != -1 ? damage : 16, owner);
					entity.setFireTicks(15);
					
					//entity.setFireTicks(30);
				}
				if (entity != null || block != null) {
					Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
					FireworkMeta meta = fw.getFireworkMeta();
					meta.addEffect(FireworkEffect.builder().flicker(true).trail(true).withColor(Color.YELLOW).withColor(Color.WHITE).with(Type.STAR).build());
					fw.setFireworkMeta(meta);
					fw.detonate();
				}
				SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1);
				/*
				
					SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1.1f);
					*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, location, 15, 1, 1, 1, 1);
				
				dead[k] = true;
	            
				break;
			case 11: // Gulag
				if (entity != null && entity instanceof LivingEntity le && !(le instanceof Hangable) && le.getType() != EntityType.ARMOR_STAND && le.getType() !=  EntityType.CHEST_MINECART && Arena.validTarget(le)) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s minecraft:slowness 30 200");
					FancyArena.gulag.put(le, le.getLocation().clone());
					Location gulagLoc = new Location(le.getWorld(), 30, 250, 30);
					
					FancyArena.Fill(gulagLoc.clone().add(new Vector(-4, -4, -4)), gulagLoc.clone().add(new Vector(4, 4, 4)), Material.BARRIER);
					FancyArena.Fill(gulagLoc.clone().add(new Vector(-3, -3, -3)), gulagLoc.clone().add(new Vector(3, 3, 3)), Material.POWDER_SNOW);
					
					/*
					gulagLoc.clone().subtract(new Vector(0, 1, 0)).getBlock().setType(Material.POWDER_SNOW);
					gulagLoc.clone().subtract(new Vector(0, 2, 0)).getBlock().setType(Material.POWDER_SNOW);
					gulagLoc.clone().subtract(new Vector(0, 3, 0)).getBlock().setType(Material.SNOW_BLOCK);
					
					gulagLoc.clone().subtract(new Vector(1, 1, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(-1, 1, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 1, 1)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 1, -1)).getBlock().setType(Material.BARRIER);

					gulagLoc.clone().subtract(new Vector(1, 2, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(-1, 2, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 2, 1)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 2, -1)).getBlock().setType(Material.BARRIER);
					
					gulagLoc.clone().subtract(new Vector(1, 3, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(-1, 3, 0)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 3, 1)).getBlock().setType(Material.BARRIER);
					gulagLoc.clone().subtract(new Vector(0, 3, -1)).getBlock().setType(Material.BARRIER);
					*/
					if (le.getPassengers().size() > 0)
						le.removePassenger(le.getPassengers().get(0));
					if (le.getScoreboardTags().contains("Grapple")) {
						le.removeScoreboardTag("Grapple");
					}
					le.teleport(gulagLoc.clone().add(1, -1, 1));
					FancyArena.root(le, gulagLoc.clone().add(1, -1, 1), 20*25);
					
					if (le instanceof Player player) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute as " + player.getName() + " at @s run playsound welcome_to_gulag hostile @a ~ ~ ~");
						player.sendTitle(ChatColor.RED + "Welcome to the Gulag", ChatColor.GOLD + "Good Luck!", 1, 40, 5);
					}
				} else {
					//if (owner instanceof Player p)
					//	p.setCooldown(Material.GOLDEN_HOE, 10);
				}
				SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1.1f);/*
				
					SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1.1f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, location, 15, 1, 1, 1, 1);
				
				dead[k] = true;
	            
				break;
			case 12: // Maskirovka
				
					Skeleton mas = (Skeleton)owner.getWorld().spawnEntity(location.clone().add(0, 1f, 0), EntityType.SKELETON);
					mas.getEquipment().setArmorContents(owner.getEquipment().getArmorContents().clone());
					ItemStack staff = new ItemStack(Material.GOLDEN_HOE);
					ItemMeta meta = staff.getItemMeta();
					meta.setCustomModelData(110);
					staff.setItemMeta(meta);
					mas.getEquipment().setItemInMainHand(staff);
					mas.getEquipment().setItemInOffHand(owner.getEquipment().getItemInOffHand().clone());
					if (mas.getEquipment().getHelmet() == null || mas.getEquipment().getHelmet().getType() == Material.AIR) {
						ItemStack head = new ItemStack(Material.PLAYER_HEAD);
						SkullMeta smeta = (SkullMeta)head.getItemMeta();
						smeta.setOwner(owner.getName());
						mas.getEquipment().setHelmet(head);
					}
					mas.setInvulnerable(true);
					//mas.setGravity(true);
					mas.setAI(false);
					mas.setCustomNameVisible(true);
					String name = owner.getCustomName() == null ? owner.getName() : owner.getCustomName();
					mas.setCustomName(name);
					mas.addScoreboardTag("Maskirovka");
					mas.addScoreboardTag("invulnerable");
					mas.setMetadata("owner", new FixedMetadataValue(FancyArena.instance, owner));
					mas.setSilent(true);
					mas.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(200);
					//entity.setMaxHealth(hp);
					mas.setHealth(200);
					FancyArena.illusionDoubles.put(owner, mas);
					EntityKiller killer = new EntityKiller(mas, 600);
				
				SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1.1f);/*I
				
					SFX.play(location, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 10f, 1.1f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.ENCHANT, location, 15, 0.5, 1, 1, 1);
				
				dead[k] = true;
	            
				break;
			case 13: // Guiding bolt
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 11, owner, DIVINE);
					//entity.damage(damage != -1 ? damage : 11, owner);
					entity.setFireTicks(10);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s glowing 20 1");
					entity.addScoreboardTag("GuidingBolt");
					//entity.setFireTicks(30);
				}
				SFX.play(location, Sound.BLOCK_ANVIL_HIT, 1f, 1.6f);/*
				
					SFX.play(location, Sound.BLOCK_ANVIL_HIT, 1f, 1.6f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 15, 0.5, 1, 1, 1);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 0.5, 1, 1, 1);
				
				break;
			case 14: // Levitate
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(entity) && !entity.getScoreboardTags().contains("fom")) {
					damage(entity, damage != -1 ? damage : 1, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 1, owner);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				SFX.play(location, Sound.ENTITY_SHULKER_BULLET_HIT, 1f, 1f);/*
				
					SFX.play(location, Sound.ENTITY_SHULKER_BULLET_HIT, 1f, 1f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, location, 15, 0.5, 1, 1, 1);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 0.5, 1, 1, 1);
				
				break;
			case 15: // Heal
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					FancyArena.HealSpell(le, damage);
					/*
					le.setHealth(Math.min(le.getHealth() + damage, le.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
					if (damage >= 6) {
						le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20*2, 3));
						le.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20*2, 0));
						le.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20*2, 0));
					}
					*/
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				/*
				
					SFX.play(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1f, 1f);
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 15, 0.5, 1, 1, 1);
				location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				*/
				
				break;
			case 16: // Thornwhip
				double pull = damage != -1 ? damage/10 : 0.9f;
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(le)) {
					damage(entity, damage != -1 ? damage : 4, owner, PHYSICAL);
					//entity.damage(damage != -1 ? damage : 4, owner);
					if (!entity.getScoreboardTags().contains("invulnerable") && !le.getScoreboardTags().contains("fom"))
						entity.setVelocity(entity.getVelocity().add(velocity.clone().multiply(-pull)));
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				SFX.play(location, Sound.BLOCK_AZALEA_LEAVES_BREAK, 2f, 1f);/*
				
					SFX.play(location, Sound.BLOCK_AZALEA_LEAVES_BREAK, 2f, 1f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.BLOCK, location, 30, 0.5, 1, 1, 1, Material.OAK_LEAVES.createBlockData());
				//location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				
				break;
			case 17: // Thunderwave
			{
				double knockback = damage != -1 ? damage/8 : 1.4f;
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND && Arena.validTarget(le)) {
					damage(entity, damage != -1 ? damage : 8, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 8, owner);
					if (!entity.getScoreboardTags().contains("invulnerable") && !le.getScoreboardTags().contains("fom"))
						entity.setVelocity(entity.getVelocity().add(velocity.clone().multiply(knockback)));
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				SFX.play(location, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2f, 1.3f);/*
				
					SFX.play(location, Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, 2f, 1.3f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0.5, 0.5, 0.5, 0.5);
				//location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				
				break;
			}
			case 18: // Rocket Hammer Spin
			{
				double knockback = damage != -1 ? damage/10 : 1f;
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 12, owner, PHYSICAL);
					//entity.damage(damage != -1 ? damage : 12, owner);
					if (!entity.getScoreboardTags().contains("invulnerable") && !le.getScoreboardTags().contains("fom")) {
						entity.setVelocity(entity.getVelocity().add(velocity.clone().add(this.right.clone()).multiply(knockback)));
					}
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				if (entity != null) {
					location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0.01, 0.01, 0.01, 0.01);
					SFX.play(location, Sound.BLOCK_ANVIL_LAND, 1f, 1.4f);/*
					
						SFX.play(location, Sound.BLOCK_ANVIL_LAND, 1f, 1.4f);*/
				} else {
					SFX.play(location, Sound.ENTITY_BREEZE_LAND, 1f, 1.2f);/*
					
						SFX.play(location, Sound.ENTITY_BREEZE_LAND, 1f, 1.2f);*/
				}
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0.01, 0.01, 0.01, 0.01);
				//location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				
				break;
			}
			case 19: // Antimatter Rifle
			{
				//double knockback = damage != -1 ? damage/10 : 1f;
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 24, owner, NECROTIC);
					//entity.damage(damage != -1 ? damage : 24, owner);
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s wither 4 3");
					/*if (!entity.getScoreboardTags().contains("invulnerable")) {
						entity.setVelocity(entity.getVelocity().add(velocity.clone().add(this.right.clone()).multiply(knockback)));
					}*/
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				if (entity != null || block != null) {
					location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0.01, 0.01, 0.01, 0.01);
					SFX.play(location, Sound.ENTITY_ENDERMAN_DEATH, 1f, 1.4f);/*
					
						SFX.play(location, Sound.ENTITY_ENDERMAN_DEATH, 1f, 1.4f);*/
				}
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				//location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0.01, 0.01, 0.01, 0.01);
				//location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				
				break;
			}
			case 20: // Antimatter Core
			{
				double knockback = damage != -1 ? damage/21 : 1f;
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 30, owner, NECROTIC);
					//entity.damage(damage != -1 ? damage : 30, owner);
					
					/*if (!entity.getScoreboardTags().contains("invulnerable")) {
						entity.setVelocity(entity.getVelocity().add(velocity.clone().add(this.right.clone()).multiply(knockback)));
					}*/
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + location.getX() + " " + location.getY() + " " + location.getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=10,sort=nearest,distance=..9] run effect give @s wither 4 3");
				for (Entity e : location.getWorld().getNearbyEntities(location, 9, 9, 9)) {
					if (e != null && e instanceof LivingEntity le && !le.getScoreboardTags().contains("fom") && Arena.validTarget(le) && le.getType() != EntityType.ARMOR_STAND && le.getType() != EntityType.ITEM_FRAME && le.getType() != EntityType.GLOW_ITEM_FRAME && !le.getScoreboardTags().contains("invulnerable")) {
						//float dist = (float)Math.sqrt(Math.pow(location.getX() - le.getLocation().getX(), 2) + Math.pow(location.getY() - le.getLocation().getY(), 2) + Math.pow(location.getZ() - le.getLocation().getZ(), 2));
						Vector diff = location.toVector().clone().subtract(le.getLocation().toVector().clone());
						float dist = (float)diff.length();
						le.damage(damage != -1 ? damage : 30/(Math.max(1, dist/3)), owner);
						le.setVelocity(le.getVelocity().add(diff.clone().normalize().multiply(1.5)));
					}
				}
				//if (entity != null || block != null) {
				location.getWorld().spawnParticle(Particle.LAVA, location, 30, 0.8, 0.8, 0.8, 0.8);
				location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 2, 0.1, 0.1, 0.1, 0.1);
				//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + " run summon creeper ~ ~ ~ {Fuse:0,ExplosionRadius:8}");
				//location.getWorld().createExplosion(location, 100, false, false);
				/*
				Creeper creeper = (Creeper) location.getWorld().spawnEntity(location, EntityType.CREEPER);
				creeper.setExplosionRadius(8);
				creeper.setFuseTicks(0);
				creeper.explode();
				*/
				SFX.play(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 0.6f);/*
				
					SFX.play(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 1f, 0.6f);*/
				//}
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				//location.getWorld().spawnParticle(Particle.FLAME, location, 2, 0.01, 0.01, 0.01, 0.01);
				//location.getWorld().spawnParticle(Particle.HEART, location, 15, 0.5, 1, 1, 1);
				
				break;
			}
			case 21: // Wall of Fire
			{
				FancyArena.wallOfFire.put(location.clone(), new Tuple<Integer, LivingEntity>(10 * 30, (LivingEntity)owner));
				SFX.play(location, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f);/*
				
					SFX.play(location, Sound.BLOCK_LAVA_EXTINGUISH, 1f, 1f);*/
				break;
			}
			case 22: // Vine balls
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 16, owner, PHYSICAL);
					//entity.damage(damage != -1 ? damage : 16, owner);
					if (entity instanceof LivingEntity le && !le.getScoreboardTags().contains("fom")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s poison 10 4");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slowness 10 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s weakness 6 10");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slow_falling 10 200");
						//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s glowing 8 100");

					}
					//entity.setFireTicks(30);
				}
				
				SFX.play(location, Sound.BLOCK_AZALEA_LEAVES_BREAK, 2f, 1f);/*
				
					SFX.play(location, Sound.BLOCK_AZALEA_LEAVES_BREAK, 2f, 1f);*/
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, location, 15, 1, 0.5, 0.5, 0.5);
				location.getWorld().spawnParticle(Particle.BLOCK, location, 30, 0.5, 1, 1, 1, Material.OAK_LEAVES.createBlockData());
	            
				break;
			case 23: // Vampire bolt
				if (Arena.validTarget(entity)) {
					int damage = this.damage != -1 ? this.damage : 20;
					float healing = (float)Math.min(entity.getHealth(), damage);
					damage(entity, damage, owner, NECROTIC);
					//entity.damage(damage);
					owner.setHealth(Math.min(owner.getHealth() + healing, owner.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
				}
				
				SFX.play(location, Sound.ENTITY_WITHER_SHOOT, 1f, 0.7f);/*
				
					SFX.play(location, Sound.ENTITY_WITHER_SHOOT, 1f, 0.7f);*/
				location.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR,  location, 15, 1, 0.5, 0.5, 0.5);
				
				break;
			case 24: // Super Chain Lightning
			{
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 25, owner, ELEMENTAL);
					//entity.damage(damage != -1 ? damage : 25, owner);
					entity.setFireTicks(30);
					//entity.setFireTicks(30);
				}
				
				LightningStrike bolt = location.getWorld().strikeLightning(location);
				bolt.setMetadata("lightningOwner", new FixedMetadataValue(plugin, owner));
				
				//
				//	SFX.play(location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10f, 1.3f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.1, 0.1, 0.1);
				
				Collection<Entity> entities = location.getWorld().getNearbyEntities(location, 20, 20, 20);
				
				boolean foundEntity = false;
				
				for (Entity e : entities) {
					if (e instanceof Damageable le && le != entity && le != owner && le.getType() != EntityType.ARMOR_STAND && !(le instanceof Hangable)) {
						if (!foundEntity || Math.random() > 0.4) {
							Vector diff = new Vector(e.getLocation().getX() - location.getX(), e.getLocation().getY() - location.getY(), e.getLocation().getZ() - location.getZ());
							velocity = diff.normalize();
							foundEntity = true;
						} else if (Math.random() < 0.1)
							return;
					}
				}
				
				dead[k] = !foundEntity;
				//velocity
	            
				
				
				
				break;
			}
			case 25: // Magic Missiles
			{
				if (entity != null && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 3, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 3, owner);
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.ENTITY_SHULKER_BULLET_HIT, 10f, 1.3f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				//location.getWorld().spawnParticle(Particle.FLASH, location, 15, 1, 0.1, 0.1, 0.1);
	            
				break;
			}
			case 26: // Sunbeam
			{
				if (entity != null && Arena.validTarget(entity)) {
					damage(entity, damage != -1 ? damage : 22, owner, DIVINE);
					//entity.damage(damage != -1 ? damage : 20, owner);
					entity.setFireTicks(50);
					if (entity instanceof LivingEntity le && !le.getScoreboardTags().contains("fom")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s blindness 10 4");
					}
				}
				
					SFX.play(location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1.2f);
				location.getWorld().spawnParticle(Particle.EXPLOSION, location, 1, 0, 0, 0, 0);
				break;
			}
			case 27: // Sleep
			{
				if (entity != null && Arena.validTarget(entity) && !entity.getScoreboardTags().contains("fom")) {
					if (entity instanceof LivingEntity le) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s blindness 10 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slowness 10 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s darkness 10 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s weakness 10 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s mining_fatigue 10 100");
						FancyArena.root(le, le.getLocation(), 10*20);
					}
				}
				break;
			}
			case 28: // Jail
			{
				Arena.prison(entity);
				break;
			}
			case 29: // Hypnotic Pattern
			{
				FancyArena.instance.hypnoticPattern(location, owner);
				break;
			}
			case 30: // Acheron's Cube
			{
				FancyArena.SpawnAcheronPortal(location.add(new Vector(0, 0.5f, 0)));
				break;
			}
			case 31: // Supreme Enervation
				if (entity != null && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 24, owner, NECROTIC);
					//entity.damage(damage != -1 ? damage : 12, owner);
					if (entity instanceof LivingEntity le && !le.getScoreboardTags().contains("fom")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s wither 10 4");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slowness 16 100");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s darkness 16 10");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s weakness 16 10");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s slow_falling 16 200");
						//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s glowing 8 100");
						//le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 5*20, 2));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 8*20, 100));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 8*20, 10));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 4*20, 10));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 8*20, 200));
						//le.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 8*20, 200));
					}
					//entity.setFireTicks(30);
				}
				
				
					SFX.play(location, Sound.BLOCK_ANVIL_PLACE, 10f, 0.5f);
				//location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0, 0, 0, 0);
				location.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, location, 15, 1, 0.5, 0.5, 0.5);
	            
				break;
			case 32: // Dalek Gunstick
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 24, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 24, owner);
					location.getWorld().spawnParticle(Particle.EXPLOSION, location, 2, 0.01, 0.01, 0.01, 0.01);
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run playsound dalek_shoot_hit hostile @a ~ ~ ~");
					/*if (!entity.getScoreboardTags().contains("invulnerable")) {
						entity.setVelocity(entity.getVelocity().add(velocity.clone().add(this.right.clone()).multiply(knockback)));
					}*/
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				break;
			case 33: // Explosive Gunstick
				if (entity != null && entity instanceof LivingEntity le && entity.getType() != EntityType.ARMOR_STAND) {
					damage(entity, damage != -1 ? damage : 40, owner, FORCE);
					//entity.damage(damage != -1 ? damage : 24, owner);
					
					
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute in " + location.getWorld().getName().strip().toLowerCase() + " positioned " + le.getLocation().getX() + " " + le.getLocation().getY() + " " + le.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run playsound dalek_shoot_hit hostile @a ~ ~ ~");
					
					/*if (!entity.getScoreboardTags().contains("invulnerable")) {
						entity.setVelocity(entity.getVelocity().add(velocity.clone().add(this.right.clone()).multiply(knockback)));
					}*/
					//Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute positioned " + entity.getLocation().getX() + " " + entity.getLocation().getY() + " " + entity.getLocation().getZ() + " as @e[type=!item,type=!armor_stand,type=!painting,type=!item_frame,type=!glow_item_frame,limit=1,sort=nearest] run effect give @s levitation 8 4");
					//entity.setFireTicks(30);
				}
				location.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, location, 5, 0.2, 0.2, 0.2, 0.2);
				FancyArena.instance.explode(location, 4, 40, false, owner);
				break;
		}
	}
	
	public static boolean intersectsBlock(Location loc, double radius) {
		if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
			return false;
		if (loc.getBlock() != null && !loc.getBlock().isPassable() && !loc.getBlock().isLiquid()) {
			Vector minVector = loc.toVector().clone().subtract(new Vector(radius, radius, radius));
			Vector maxVector = loc.toVector().clone().add(new Vector(radius, radius, radius));
			BoundingBox box = loc.getBlock().getBoundingBox();
			if (box.overlaps(minVector, maxVector))
				return true;
		}
		return false;
	}
	
	public void CheckLocation(Location loc, int i) {
		if (intersectsBlock(loc, size*0.3))
				hit(loc.getBlock(), null, i, loc);
		
	}
	
	public void CheckAntimagic(Location loc, int i) {
		if (FancyArena.antimagic.size() > 0) {
			for (Tuple<BoundingBox, World> bbox : FancyArena.antimagic.keySet()) {
				if (loc.getWorld() != bbox.y) {
					break;
				} else {
					Vector minVector = loc.toVector().clone().subtract(new Vector(size*0.6, size*0.6, size*0.6));
					Vector maxVector = loc.toVector().clone().add(new Vector(size*0.6, size*0.6, size*0.6));
					if (bbox.x.overlaps(minVector, maxVector)) {
						dead[i] = true;
						
							SFX.play(loc, Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
						loc.getWorld().spawnParticle(Particle.LARGE_SMOKE,loc, 3);
					}
				}
			}
		}
	}
	
	public void CheckEntities(Location loc, int i) {
		Collection<Entity> hitEntities = loc.getWorld().getNearbyEntities(loc, size*4, size*4, size*4);
		if (hitEntities.size() > 0) {
			Vector minVector = loc.toVector().clone().subtract(new Vector(size*0.6, size*0.6, size*0.6));
			Vector maxVector = loc.toVector().clone().add(new Vector(size*0.6, size*0.6, size*0.6));
			Vector minVector2 = loc.toVector().clone().add(velocity.clone().multiply(0.5)).subtract(new Vector(size*0.6, size*0.6, size*0.6));
			Vector maxVector2 = loc.toVector().clone().add(velocity.clone().multiply(0.5)).add(new Vector(size*0.6, size*0.6, size*0.6));
			
			for (Entity e : hitEntities) {
				if (e.getType() == EntityType.ARMOR_STAND)
					continue;
				if (hitEffect != 15 && !FancyArena.monsterPvp && e.getScoreboardTags().contains("ArenaMob") && monsterBullet) {
					return;
				} else if (hitEffect != 15 && !FancyArena.pvp && e instanceof Player player && !monsterBullet) {
					if (Arena.runningArenas.size() > 0) {
						for (String tag : player.getScoreboardTags()) {
							if (tag.contains("Ready_"))
								return;
						}
					}
				}
				if (e instanceof Damageable de && !e.equals(owner) && distance(e.getLocation(), owner.getLocation()) > 0.6) {
					BoundingBox box = e.getBoundingBox();
					boolean cancelled = false;
					if (box.overlaps(minVector, maxVector) || box.overlaps(minVector2, maxVector2)) {
						
						if (e instanceof LivingEntity le && owner != le) {
							if (le.getEquipment().getItemInOffHand() != null && le.getEquipment().getItemInOffHand().hasItemMeta() && le.getEquipment().getItemInOffHand().getItemMeta().hasDisplayName() && le.getEquipment().getItemInOffHand().getType() == Material.GUNPOWDER && le.getEquipment().getItemInOffHand().getItemMeta().getDisplayName().contains("Ring of Arcane Antithesis")) {
								if (le instanceof Player player) {
									if (!player.hasCooldown(Material.GUNPOWDER)) {
										FancyArena.instance.counterspell(player, velocity.clone().multiply(-1).normalize().multiply(0.5).add( loc.toVector().clone().subtract(le.getLocation().toVector()) ));
										player.setCooldown(Material.GUNPOWDER, 80);
										
										
											SFX.play(e.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1, 1.6f);
										e.getWorld().spawnParticle(Particle.LARGE_SMOKE, e.getLocation(), 3);
										
										dead[i] = true;
										cancelled = true;
									}
								} else if (Math.random() < 0.4) {
									FancyArena.instance.counterspell(le, velocity.clone().multiply(-1).normalize().multiply(0.5).add( loc.toVector().clone().subtract(le.getLocation().toVector()) ));
									
									dead[i] = true;
									cancelled = true;
								}
							}
							if (FancyArena.instance.dalekShield.containsKey(le)) {
								Vector diff = le.getLocation().subtract(location).toVector();
								//Bukkit.broadcastMessage("Deflected");
								location.getWorld().spawnParticle(Particle.GUST, location, 1, 0, 0, 0, 0);
								//Vector proj = diff.multiply(velocity.dot(diff) / diff.dot(diff));
								velocity = (diff.normalize().multiply(-velocity.length()));
								cancelled = true;
							}
						}
						if (!cancelled)
							hit(null, de, i, loc);
					}
				}
			}
		}
	}
	
	public static double distance(Location a, Location b) {
		return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY(), 2) + Math.pow(a.getZ() - b.getZ(), 2));
	}
	
	public static List<Entity> getEntitiesAroundPoint(Location location, double radius) {
	    List<Entity> entities = new ArrayList<Entity>();
	    World world = location.getWorld();

	    // To find chunks we use chunk coordinates (not block coordinates!)
	    int smallX = MathHelper.floor((location.getX() - radius) / 16.0D);
	    int bigX = MathHelper.floor((location.getX() + radius) / 16.0D);
	    int smallZ = MathHelper.floor((location.getZ() - radius) / 16.0D);
	    int bigZ = MathHelper.floor((location.getZ() + radius) / 16.0D);

	    for (int x = smallX; x <= bigX; x++) {
	        for (int z = smallZ; z <= bigZ; z++) {
	            if (world.isChunkLoaded(x, z)) {
	                entities.addAll(Arrays.asList(world.getChunkAt(x, z).getEntities())); // Add all entities from this chunk to the list
	            }
	        }
	    }

	    // Remove the entities that are within the box above but not actually in the sphere we defined with the radius and location
	    // This code below could probably be replaced in Java 8 with a stream -> filter
	    Iterator<Entity> entityIterator = entities.iterator(); // Create an iterator so we can loop through the list while removing entries
	    while (entityIterator.hasNext()) {
	        if (entityIterator.next().getLocation().distanceSquared(location) > radius * radius) { // If the entity is outside of the sphere...
	            entityIterator.remove(); // Remove it
	        }
	    }
	    return entities;
	}
}
