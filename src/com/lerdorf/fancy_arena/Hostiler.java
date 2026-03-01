package com.lerdorf.fancy_arena;

import org.bukkit.craftbukkit.v1_21_R1.entity.CraftCat;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftFox;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftIronGolem;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftOcelot;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftParrot;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPolarBear;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftSalmon;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftWolf;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalCrossbowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityOcelot;
import net.minecraft.world.entity.animal.EntityParrot;
import net.minecraft.world.entity.animal.EntityPolarBear;
import net.minecraft.world.entity.animal.EntitySalmon;
import net.minecraft.world.entity.animal.EntityWolf;
import net.minecraft.world.entity.monster.EntitySkeleton;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.player.EntityHuman;

public class Hostiler {
	public static void activate(Entity entity) {
		try {
			if (entity != null && entity.isValid() && entity instanceof LivingEntity le) {
				if (le.getType() == EntityType.IRON_GOLEM && entity instanceof org.bukkit.entity.IronGolem) {
		        	//Bukkit.broadcastMessage("Creating hostile irongolem");
			        CraftIronGolem craftGolem = (CraftIronGolem) entity;
			        EntityIronGolem nmsGolem = (EntityIronGolem) craftGolem.getHandle();
			
			        // Add hostile goal to players
			        nmsGolem.bX.a(1, new PathfinderGoalNearestAttackableTarget<>(
			            nmsGolem,
			            EntityHuman.class,
			            true
			        ));
		        } else if (le.getType() == EntityType.CAT && entity instanceof org.bukkit.entity.Cat) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftCat craftCat = (CraftCat) entity;
		            EntityCat nmsCat = (EntityCat) craftCat.getHandle();
		
		            // Make it attack players
		            nmsCat.bW.a(1, new PathfinderGoalMeleeAttack(nmsCat, 2.0D, true)); // goalSelector
		            nmsCat.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            	nmsCat,
		                EntityHuman.class,
		                true
		            ));
		        } else if (le.getType() == EntityType.OCELOT && entity instanceof org.bukkit.entity.Ocelot) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftOcelot craftOcelot = (CraftOcelot) entity;
		            EntityOcelot nmsOcelot = (EntityOcelot) craftOcelot.getHandle();
		
		            // Make it attack players
		            nmsOcelot.bW.a(1, new PathfinderGoalMeleeAttack(nmsOcelot, 2.0D, true)); // goalSelector
		            nmsOcelot.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            		nmsOcelot,
		                EntityHuman.class,
		                true
		            ));
		        } else if (le.getType() == EntityType.FOX && entity instanceof org.bukkit.entity.Fox) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftFox craftFox = (CraftFox) entity;
		            EntityFox nmsFox = (EntityFox) craftFox.getHandle();
		
		            // Make it attack players
		            nmsFox.bW.a(1, new PathfinderGoalMeleeAttack(nmsFox, 1.8D, true)); // goalSelector
		            nmsFox.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            		nmsFox,
		                EntityHuman.class,
		                true
		            ));
		        } else if (le.getType() == EntityType.POLAR_BEAR && entity instanceof org.bukkit.entity.PolarBear) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftPolarBear craftPolarBear = (CraftPolarBear) entity;
		            EntityPolarBear nmsPolarBear = (EntityPolarBear) craftPolarBear.getHandle();
		
		            // Make it attack players
		            //nmsPolarBear.bW.a(1, new PathfinderGoalMeleeAttack(nmsPolarBear, 1.0D, true)); // goalSelector
		            nmsPolarBear.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            		nmsPolarBear,
		                EntityHuman.class,
		                true
		            ));
		        } else if (le.getType() == EntityType.WOLF && entity instanceof org.bukkit.entity.Wolf) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftWolf craftWolf = (CraftWolf) entity;
		            EntityWolf nmsWolf = (EntityWolf) craftWolf.getHandle();
		
		            // Make it attack players
		            //nmsWolf.bW.a(1, new PathfinderGoalMeleeAttack(nmsWolf, 1.0D, true)); // goalSelector
		            nmsWolf.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            		nmsWolf,
		                EntityHuman.class,
		                true
		            ));
		        } else if (le.getType() == EntityType.PARROT && entity instanceof org.bukkit.entity.Parrot) {
		            //Bukkit.broadcastMessage("Creating hostile cat");
		            CraftParrot craftParrot = (CraftParrot) entity;
		            EntityParrot nmsParrot = (EntityParrot) craftParrot.getHandle();
		
		            // Make it attack players
		            nmsParrot.bW.a(1, new PathfinderGoalMeleeAttack(nmsParrot, 1.0D, true)); // goalSelector
		            nmsParrot.bX.a(1, new PathfinderGoalNearestAttackableTarget<EntityHuman>(
		            		nmsParrot,
		                EntityHuman.class,
		                true
		            ));
		        }
			}
		}
		catch (Exception e) {
			
		}
	} 
}
