package com.lerdorf.fancy_plugin_1_19;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SurvivalGames implements CommandExecutor {

	int[][] spawnpoints = {
			{-1218, 21, 604},
			{-1332, 29, 506},
			{-1158, 37, 283},
			{-1027, 24, 475},
			{-1261, 24, 513},
			{-1202, 54, 398},
	};
	
	boolean[] usedSpawnpoint;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("survivalgames") || cmd.getName().equalsIgnoreCase("sg")){
        	usedSpawnpoint = new boolean[spawnpoints.length];
        	for (int i = 0; i < spawnpoints.length; i++) {
        		usedSpawnpoint[i] = false;
        	}
            Player player = (Player) sender;
            Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say " + player.getName() + " sending players to spawnpoints");
            Collection<? extends Player> players = sender.getServer().getOnlinePlayers();
            Iterator<? extends Player> iterator = players.iterator();
            if (players.size() > spawnpoints.length) {
            	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say " + player.getName() + " there are more players than spawnpoints you fucking donkey!");
            	return false;
            }
            while (iterator.hasNext()) {
            	Player e = iterator.next();
            	int i = (int)(Math.random()*spawnpoints.length);
            	while (usedSpawnpoint[i]) i = (int)(Math.random()*spawnpoints.length);
            	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute in minecraft:wuhan-island-resort-v.1.4 run tp " + e.getName() + " " + spawnpoints[i][0] + " " + spawnpoints[i][1] + " " + spawnpoints[i][2]);

            	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "title " + e.getName() + " times 10 30 10");

            	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "title " + e.getName() + " subtitle {\"text\":\"Dropped at: " + spawnpoints[i][0] + " " + spawnpoints[i][1] + " " + spawnpoints[i][2] + "\"}");

            }

            String[] cmds = {
            		"say Resetting worldborder",
            		"execute as " + player.getName() + " at @s run worldborder set 610",
            		"say Resetting saturation",
            		"effect give @a minecraft:saturation 10 255",
            		"say Healing all players",
            		"effect give @a minecraft:instant_health 1 10",
            		"say Clearing inventories",
            		"clear @a",
            		"say Setting all players to adventure mode",
            		"gamemode adventure @a",
            		"say Giving all players xp levels",
            		"xp set @a 696969 levels",
            		"say Resetting chests",
            		"function cl:reset",
            		"execute as @a at @s run playsound minecraft:entity.player.levelup master @a ~ ~ ~",
            		"say Starting worldborder shrinkage",
            		"execute as " + player.getName() + " at @s run worldborder set 50 600",
            		"say DONE! Ready to start!"
            		
            };
            
            try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            for (int i = 0; i < cmds.length; i++) {
            	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), cmds[i]);
            	try {
            		if (i == cmds.length-3)
        				TimeUnit.SECONDS.sleep(2);
            		else
            			TimeUnit.MILLISECONDS.sleep(69);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
            
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "title @a title [\"\",{\"text\":\"-\",\"obfuscated\":true,\"color\":\"light_purple\"},{\"text\":\"G\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"a\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"m\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"e \",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"S\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"t\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"a\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"r\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"t\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"e\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"d\",\"bold\":true,\"color\":\"yellow\"},{\"text\":\"!\",\"bold\":true,\"color\":\"dark_red\"},{\"text\":\"-\",\"obfuscated\":true,\"color\":\"light_purple\"}]");

            
          
            /*
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Resetting worldborder");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as " + player.getName() + " as @s run worldborder set 610");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Starting worldborder shrinkage");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as " + player.getName() + " as @s run worldborder set 50 600");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Setting all players to adventure mode");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "gamemode adventure @a");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Clearing inventories");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "clear @a");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Resetting saturation");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give @a minecraft:saturation 10 255");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Healing all players");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "effect give @a minecraft:instant_health 1 10");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say Resetting chests");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "function cl:reset");
        	Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "execute as @a at @s run playsound minecraft:entity.player.levelup master @a ~ ~ ~");
            */
        	return true;
        }
        return false;
	}
	
}
