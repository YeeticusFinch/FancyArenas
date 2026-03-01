package com.lerdorf.fancy_arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Merchant;

import net.md_5.bungee.api.ChatColor;

public class Shop implements CommandExecutor  {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0 && args[0].equalsIgnoreCase("set") && sender instanceof Player player) {
			Arena.shopLoc = FancyLevel.locToString(player.getLocation());
			player.sendMessage(ChatColor.GREEN + "Successfully set shop to " + Arena.shopLoc);
			return true;
		}
		else if (sender instanceof Player player) {
			if (Arena.runningArenas.size() > 0) {
				for (String tag : player.getScoreboardTags()) {
					if (tag.contains("Ready_")) {
						player.sendMessage(ChatColor.RED + "The shop is currently unavailable");
						return true;
					}
				}
			}
			 openVillagerShop(player);
			 return true;
		}
		
		return false;
		
	}
	
	public Villager getVillagerShop() {
		Location loc = FancyLevel.stringToLoc( Arena.shopLoc);
		
		for (Entity e : loc.getWorld().getNearbyEntities(loc, 1, 1, 1)) {
			if (e instanceof Villager v) {
				return v;
			}
		}
		return null;
	}
	
	public void openVillagerShop(Player player) {
		
		Villager v = getVillagerShop();
		
		if (v != null) {
			Merchant merchant = Bukkit.createMerchant("Shop");
	        merchant.setRecipes(v.getRecipes());
	        player.openMerchant(merchant, true);
		}
		else {
			player.sendMessage(ChatColor.RED + "No shop found");
		}
	}
	
}
