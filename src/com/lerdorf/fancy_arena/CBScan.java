package com.lerdorf.fancy_arena;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.chat.*;

import net.md_5.bungee.api.ChatColor;

public class CBScan implements CommandExecutor  {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /cbscan <radius>");
            return true;
        }

        int radius;
        try {
            radius = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid radius value.");
            return true;
        }

        String contents = "";

        
        Player player = (Player) sender;
        if (args.length > 1) {
        	for (int i = 1; i < args.length; i++) {
        		contents += args[i];
        		if (i != args.length-1) {
        			contents += " ";
        		}
        	}
        	player.sendMessage("Scanning for substring: \"" + contents + "\"");
        }
        Location playerLoc = player.getLocation();

        List<Location> foundBlocks = new ArrayList<>();

        // Scan area for command blocks
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location checkLoc = playerLoc.clone().add(new Vector(x, y, z));
                    Block block = checkLoc.getBlock();

                    if (isCommandBlock(block, contents)) {
                        foundBlocks.add(checkLoc);
                    }
                }
            }
        }

        if (foundBlocks.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No command blocks found within radius " + radius + ".");
            return true;
        }

        player.sendMessage(ChatColor.GOLD + "Found command blocks:");

        for (Location loc : foundBlocks) {
            sendClickableMessage(player, loc);
        }

        return true;
		
	}
	
	private boolean isCommandBlock(Block block, String contents) {
        Material type = block.getType();
        boolean result = type == Material.COMMAND_BLOCK || 
               type == Material.CHAIN_COMMAND_BLOCK || 
               type == Material.REPEATING_COMMAND_BLOCK;
        
        if (result && contents.length() > 0) {
        	if (block.getState() instanceof CommandBlock cb) {
        		if (cb.getCommand().toLowerCase().contains(contents.toLowerCase())) {
        			return true;
        		} else {
        			return false;
        		}
        	}
        }
        
        return result;
    }

    private void sendClickableMessage(Player player, Location loc) {
        String locString = String.format("%d, %d, %d", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        
        TextComponent message = new TextComponent(ChatColor.AQUA + " - " + locString);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, 
            String.format("/teleport %s %d %d %d", player.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())));
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new ComponentBuilder("Click to teleport").create()));

        player.spigot().sendMessage(message);
    }
}
