package com.lerdorf.fancy_arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

public class SFX {
	public static void play(Location loc, Sound sound, float volume, float pitch) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.getWorld() == loc.getWorld() && p.getLocation().distance(loc) < 20 * volume)
				p.playSound(p, sound, volume, pitch);
	}
	
	public static void play(Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
		for (Player p : Bukkit.getOnlinePlayers())
			if (p.getWorld() == loc.getWorld() && p.getLocation().distance(loc) < 20 * volume)
				p.playSound(p, sound, category, volume, pitch);
	}
}
