package com.lerdorf.fancy_arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EntityKiller {

	public static List<EntityKiller> killers = new ArrayList<EntityKiller>();
	
	public int ticks = 10;
	public Entity entity;
	
	public boolean dead = false;
	
	public EntityKiller(Entity entity, int ticks) {
		this.ticks = ticks;
		this.entity = entity;
		killers.add(this);
	}
	
	public void update() {
		ticks--;
		if (ticks <= 0) {
			try {
				if (entity.getScoreboardTags().contains("Maskirovka")) {
					FancyArena.illusionDoubles.remove((LivingEntity) entity);
				}
			} catch (Exception e) {}
			entity.remove();
			dead = true;
		}
	}
	
}
