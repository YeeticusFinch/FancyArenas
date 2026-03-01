package com.lerdorf.fancy_arena;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Cutscene implements Serializable {

	public String arenaName;
	public ArrayList<String> locations = new ArrayList<String>();
	public ArrayList<Integer> timings = new ArrayList<Integer>();
	public ArrayList<String> messages = new ArrayList<String>();
	public float index = 0;
	
	public Cutscene() {
		
	}
	
	public Cutscene(String arenaName) {
		this.arenaName = arenaName;
	}
	
	public boolean Step(int timeStep) {
		if (timings.size() > index+1) {
			//if (index == 0) {
			//	FancyArena.instance.storedLoc.clear();
			//}
			float timing = timings.get((int)index+1);
			Location prevLoc = FancyLevel.stringToLoc(locations.get((int)index));
			Location nextLoc = FancyLevel.stringToLoc(locations.get((int)index+1));
			String msg = null;
			
			float percent = index - (int)index;
			if (percent == 0 || percent < ((float)timeStep) / timing) {
				if (messages.get((int)index) != null && messages.get((int)index).length() > 0) {
					msg = ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + messages.get((int)index);
					if (msg.toLowerCase().contains("pocket dimension") || msg.toLowerCase().contains("pocked dimension")) {
						Castle.cloneRegionAsync(FancyArena.instance);
					}
				}
			}
			
			Location tpLoc = new Location(prevLoc.getWorld(), prevLoc.getX() * (1-percent) + nextLoc.getX() * percent, prevLoc.getY() * (1-percent) + nextLoc.getY() * percent, prevLoc.getZ() * (1-percent) + nextLoc.getZ() * percent);
			
			float yawDiff = angleDiff(nextLoc.getYaw(), prevLoc.getYaw());
			//float pitchDiff = angleDiff(nextLoc.getPitch(), prevLoc.getPitch());
			
			tpLoc.setYaw(prevLoc.getYaw() + yawDiff * percent);
			tpLoc.setPitch(prevLoc.getPitch()*(1-percent) + nextLoc.getPitch() * percent);
			String tag = "Ready_" + arenaName;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getScoreboardTags().contains(tag)) {
					if (index == 0) {
						if (Arena.currentLevel.get(arenaName) > 0 && Arena.currentLevel.get(arenaName) < 35)
							FancyArena.instance.storedLoc.put(p, p.getLocation().clone());
						else 
							FancyArena.instance.storedLoc.put(p, Arena.getCurrentLevel(arenaName).getPlayerSpawnLoc());
					}
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(tpLoc);
					p.setVelocity(nextLoc.clone().toVector().subtract(prevLoc.clone().toVector()).multiply(((float)timeStep*0.05f)/timing));
					if (msg != null) {
						p.sendMessage(msg);
					}
				}
			}
			
			index += ((float)timeStep) / timing;
			return true;
		} else {
			// Done
			Location tpLoc = Arena.getCurrentLevel(arenaName).getPlayerSpawnLoc();
			String tag = "Ready_" + arenaName;
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getScoreboardTags().contains(tag)) {
					p.setGameMode(GameMode.SURVIVAL);
					if (FancyArena.instance.storedLoc.containsKey(p))
						p.teleport(FancyArena.instance.storedLoc.get(p));
					else
						p.teleport(tpLoc);
				}
			}
			return false;
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
	
	public void addLoc(Location loc, int delay, String msg) {
		locations.add(FancyLevel.locToString(loc));
		timings.add(delay);
		messages.add(msg);
	}
	
	public void reset() {
		index = 0;
	}
}
