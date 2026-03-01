package com.lerdorf.fancy_arena;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FancyItem implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("disable")) {
				FancyArena.enableItems = false;
				sender.sendMessage("Disabling custom items");
				return true;
			} else if (args[0].equalsIgnoreCase("enable")) {
				FancyArena.enableItems = true;
				sender.sendMessage("Enabling custom items");
				return true;
			}
		}
		
		return false;
	}

}
