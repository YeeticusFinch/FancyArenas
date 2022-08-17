package com.lerdorf.fancy_plugin_1_19;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public class FancyPlugin extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		System.out.println("Starting FancyPlugin");
		
		getServer().getPluginManager().registerEvents(this, this);
		
		// Register our command "kit" (set an instance of your command class as executor)
        this.getCommand("wr").setExecutor(new WorldReset());
        this.getCommand("worldreset").setExecutor(new WorldReset());
        this.getCommand("sg").setExecutor(new SurvivalGames());
        this.getCommand("survivalgames").setExecutor(new SurvivalGames());
        this.getCommand("heal").setExecutor(new Heal());
        this.getCommand("h").setExecutor(new Heal());
        this.getCommand("healall").setExecutor(new Heal());
        this.getCommand("ha").setExecutor(new Heal());
        this.getCommand("door").setExecutor(new Door());
	}
	
	@Override
	public void onDisable() {
		System.out.println("Shutting down Fancy Plugin...");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		System.out.println("Player joined");
		event.getPlayer().sendMessage( "Welcome to the Yeet Squad Minecraft Server!\nEnjoy your stay!" );
	}

}
