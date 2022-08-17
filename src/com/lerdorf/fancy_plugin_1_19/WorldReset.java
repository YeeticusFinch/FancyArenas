package com.lerdorf.fancy_plugin_1_19;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.FileUtil;
import org.bukkit.entity.Player;

//import org.apache.commons.io.FileUtils;

public class WorldReset implements CommandExecutor {

	int p = 0;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("worldreset") || cmd.getName().equalsIgnoreCase("wr")){
            Player player = (Player) sender;
            Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say " + player.getName() + " has reset the world!");
            p = 0;
            try {
            	p = 1;
				//FileUtil.deleteDirectory(new File("/home/carl/minecraft/sky-battle-v2"));
            	(new File("/home/carl/minecraft/sky-battle-v2")).delete();
				p = 2;
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say deleted sky-battle-v2/");
				
				//FileUtil.copyDirectory(new File("/home/carl/minecraft/sky-battle-v2-backup"), new File("/home/carl/minecraft/sky-battle-v2"));
				copy(new File("/home/carl/minecraft/sky-battle-v2-backup"), new File("/home/carl/minecraft/sky-battle-v2"));
				
				p = 3;
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say restored backup from sky-battle-v2-backup/");
				
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "mvremove sky-battle-v2");
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say removed old multiverse configuration");

				p = 4;
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "mvimport sky-battle-v2 normal");
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say configured sky-battle-v2 with multiverse");
				
				p = 5;
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				String message = "";
				switch (p) {
				case 0:
					message = "You fucked up bigtime";
					break;
				case 1:
					message = "Failed to delete sky-battle-v2/";
					break;
				case 2:
					message = "Failed to copy sky-battle-v2-backup/";
					break;
				case 3:
					message = "Idk what the problem is, sucks to be you lmao";
					break;
				case 4:
					message = "Failed to import new multiverse world";
					break;
				case 5:
					message = "This should be working";
					break;
				}
				Bukkit.dispatchCommand(player.getServer().getConsoleSender(), "say error restoring from sky-battle-v2-backup: " + message);
				e.printStackTrace();
			}
            /*
            if(args[0].equalsIgnoreCase(" join ") && args[1].equalsIgnoreCase("skywell")){
                Bukkit.dispatchCommand(getServer().getConsoleSender(), "pg clear "+ player.getName());
            }*/
            return true;
        }
        return false;
    }
	
	public void copy(File t, File d)
    {
        if(!t.exists())
        {
            return;
        }
        if(!d.exists())
        {
            try
            {
                if(t.isDirectory())
                {
                    d.mkdir();
                }
                else
                {
                    if(!d.createNewFile())
                    {
                        throw new IOException();
                    }
                }
            }
            catch(IOException e)
            {
                // log.info("Failed to create destination directory/file");
                return;
            }
        }
        if(t.isFile())
        {
            File newfile = new File(d, t.getName());
            try 
            {
                newfile.createNewFile();
                // Copy part
            } 
            catch (IOException ex) 
            {
                // log.info("Failed to create file: '"+x.getPath()+"'");
                return;
            }
        }
        else
        {
            File newdir = new File(d, t.getName());
            try 
            {
                newdir.mkdirs();
                if(!newdir.exists())
                {
                    throw new IOException("Failed to create directory");
                }
            } 
            catch (IOException ex) 
            {
                // log.info("Failed to create directory: '"+x.getPath()+"'");
                return;
            }
            for(File f : d.listFiles())
            {
                copy(f, newdir);
            }
        }
    }
	
}
