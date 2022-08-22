package com.lerdorf.fancy_plugin_1_19;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class FancyPlugin extends JavaPlugin implements Listener {
	
	public static ArrayList<String> mhProps;
	
	public static FancyPlugin instance = null;
	
	@Override
	public void onEnable() {
		System.out.println("Starting FancyPlugin");
		
		getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		instance = this;
		
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
		this.getCommand("mh").setExecutor(new Manhunt());
		this.getCommand("yeet").setExecutor(new Manhunt());
		loadSaves();
		
		try {
			mhProps = FileIO.read("../mc-manhunt/server.properties");
		} catch (IOException e1) {
			System.out.println("Error reading from mc-manhunt's properties");
			e1.printStackTrace();
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			  @Override
			  public void run() {
				  try {
						doorUpdate();
						//System.out.println("Main loop");
			    	} catch (Exception e) {
			    		e.printStackTrace();
			    	}
			  }
			}, 0L, 1L);
	}
	
	 public static void sendPlayerToServer(Player player, String server) {
         try {
           ByteArrayOutputStream b = new ByteArrayOutputStream();
           DataOutputStream out = new DataOutputStream(b);
           out.writeUTF("Connect");
           out.writeUTF(server);
           player.sendPluginMessage(FancyPlugin.instance, "BungeeCord", b.toByteArray());
           b.close();
           out.close();
         }
         catch (Exception e) {
           player.sendMessage(ChatColor.RED+"Error when trying to connect to "+server);
         }
       }
	
	@Override
	public void onDisable() {
		System.out.println("Shutting down Fancy Plugin...");
		for (Door d : Door.doors.values())
			d.save();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		System.out.println("Player joined");
		event.getPlayer().sendMessage( "Welcome to the Yeet Squad Minecraft Server!\nEnjoy your stay!" );
	}

	@EventHandler
	public void onUseEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
		} else if (event.getAction() == Action.RIGHT_CLICK_AIR) {
		} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location loc = event.getClickedBlock().getLocation();
			//Location loc = player.getTargetBlock(null, 6).getLocation();
			//player.sendMessage("Right clicked block at " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
			for (Door d : Door.doors.values()) {
				//player.sendMessage("Lever at " + d.leverX + " " + d.leverY + " " + d.leverZ);
				if ((d.leverX == loc.getBlockX() && d.leverY == loc.getBlockY() && d.leverZ == loc.getBlockZ()) || d.lever2X == loc.getBlockX() && d.lever2Y == loc.getBlockY() && d.lever2Z == loc.getBlockZ()) {
					d.moveIt();
					break;
				}
			}
		}
	}
	
	public static WorldEditPlugin getWorldEdit() {
		Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		p = Bukkit.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
		if (p instanceof WorldEditPlugin) return ((WorldEditPlugin) p);
		return null;
	}
	
	public void loadSaves() {
		System.out.println("Attempting to load saved files for FancyPlugin");
		File f = new File("plugins/.."); // How else do you get the current directory?
		if (f != null) {
			String[] list = f.list();
			System.out.println("list = " + list);
			if (list != null) {
				for (String e : list) {
					//System.out.println("File: " + e);
					File g = new File(e + "/FancyPlugin/");
					if (g.exists() && !e.equalsIgnoreCase("plugins")) {
						System.out.println("Found " + e + "/FancyPlugin/");
						String[] list2 = g.list();
						for (String e2 : list2) {
							System.out.println("File: " + e2 + " " + e2.substring(0, 4));
							if (e.length() > 4 && e2.substring(0, 4).equals("door")) {
								System.out.println("Loading " + e + "/FancyPlugin/" + e2);
								String name = e2.substring(5, e2.indexOf('.'));
								//while (Door.doors.size() <= n) Door.doors.add(null);
								Door.doors.put(name, new Door(e + "/FancyPlugin/" + e2));
							}
						}
					}
				}
			}
		}
	}
	
	public void doorUpdate() {
		if (Door.doors == null)
			return;
		for (Door d : Door.doors.values()) {
			if (d != null)
				d.update();
		}
	}
	
}
