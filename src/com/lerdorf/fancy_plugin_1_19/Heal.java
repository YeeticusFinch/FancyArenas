package com.lerdorf.fancy_plugin_1_19;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Heal implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("heal") || cmd.getName().equalsIgnoreCase("h")) {


			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give " + player.getName() + " minecraft:saturation 3 255");
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give " + player.getName() + " minecraft:instant_health 3 255");
			player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 3, 200));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3, 200));
			player.getLocation().getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as " + player.getName() + " at @s run particle minecraft:end_rod ~ ~1 ~ 0.1 0.1 0.1 0.5 100 force");
			player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 2);
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as " + player.getName() + " at @s run playsound minecraft:block.end_portal.spawn master @a ~ ~ ~ 1 2");
			
			Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has been healed!");
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say " + player.getName() + " has been healed");
			return true;
		} else if (cmd.getName().equalsIgnoreCase("healall") || cmd.getName().equalsIgnoreCase("ha")) {
			
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give @a minecraft:saturation 3 255");
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give @a minecraft:instant_health 3 255");
			Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
            Iterator<? extends Player> iterator = players.iterator();
            while (iterator.hasNext()) {
            	Player p = iterator.next();
				p.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 3, 200));
				p.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 3, 200));
            }
			player.getLocation().getWorld().spawnParticle(Particle.END_ROD, player.getLocation(), 100);
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as @a at @s run particle minecraft:end_rod ~ ~1 ~ 0.1 0.1 0.1 0.5 100 force");
			player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 2);
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as @a at @s run playsound minecraft:block.end_portal.spawn master @s ~ ~ ~ 1 2");
			
			Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " has healed ALL players!");
			//Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say All players have been healed");
			return true;
			
		}
		return false;
	}

}
