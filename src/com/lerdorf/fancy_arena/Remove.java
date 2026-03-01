package com.lerdorf.fancy_arena;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public class Remove implements CommandExecutor {
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check command name
        if (command.getName().equalsIgnoreCase("rm")) {
            if (args.length == 0) {
                sender.sendMessage("Usage: /rm <selector>");
                return true;
            }

            // Join arguments in case the selector contains spaces
            StringBuilder sb = new StringBuilder();
            for (String arg : args) {
                sb.append(arg).append(" ");
            }
            String selector = sb.toString().trim();

            // Use Bukkit's built-in entity selector
            List<Entity> entities;
            try {
                entities = Bukkit.selectEntities(sender, selector);
            } catch (IllegalArgumentException e) {
                sender.sendMessage("Invalid selector: " + e.getMessage());
                return true;
            }

            if (entities.isEmpty()) {
                sender.sendMessage("No entities found with selector: " + selector);
                return true;
            }

            // Remove each entity
            int count = 0;
            for (Entity entity : entities) {
                // Remove the entity from the world
                entity.remove();
                count++;
            }
            sender.sendMessage("Removed " + count + " entities.");
            return true;
        }
        return false;
    }
}
