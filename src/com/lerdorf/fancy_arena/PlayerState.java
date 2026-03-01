package com.lerdorf.fancy_arena;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerState {

	Location loc;
	double hp;
	//ItemStack[] inv;
	int fireticks;
	Collection<PotionEffect> effects;
	
	public PlayerState(Location loc, double hp, ItemStack[] inv, int fireticks, Collection<PotionEffect> effects) {
		this.loc = loc;
		this.hp = hp;
		//this.inv = inv;
		this.fireticks = fireticks;
		this.effects = effects;
	}
	
	public PlayerState(Player p) {
		setState(p);
	}
	
	public void copy(PlayerState state) {
		loc = state.loc;
		hp = state.hp;
		//inv = state.inv;
		fireticks = state.fireticks;
		effects = state.effects;
	}
	
	public void setState(Player p) {
		loc = p.getLocation();
		hp = p.getHealth();
		//inv = p.getInventory().getContents();
		fireticks = p.getFireTicks();
		effects = p.getActivePotionEffects();
	}
	
	public void activatePlayerState(Player p) {
		if (p.getPassengers().size() > 0)
			p.removePassenger(p.getPassengers().get(0));
		p.teleport(loc);
		p.setHealth(hp);
		//p.getInventory().setContents(inv);
		/*
		if (inv.length > 0) {
			p.getInventory().clear();
			for (ItemStack item : inv)
				
		}
		*/
		p.setFireTicks(fireticks);
		for (PotionEffect e : p.getActivePotionEffects())
			p.removePotionEffect(e.getType());
		p.addPotionEffects(effects);
		
		for (Player q : Bukkit.getOnlinePlayers()) {
			q.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
		}
		loc.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0);
	}
}
