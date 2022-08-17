package com.lerdorf.fancy_plugin_1_19;

import java.io.Serializable;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;

public class Door implements CommandExecutor, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7597479326871735203L;

	public static transient HashMap<String, Door> doors = new HashMap<String, Door>();
	
	int x1, y1, z1, x2, y2, z2;
	String name;
	String[] open;
	String[][] phases;
	String[] closed;
	
	boolean active = false;
	
	public Door() {}
	
	public Door(Block[] blocks, boolean open, int x1, int y1, int z1, int x2, int y2, int z2) {
		set(blocks, open);
		active = true;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("door")){
        	if (args.length == 3) {
        		Door door;
			ArrayList<Block> blocks = new ArrayList<Block>();
			try {
				Region r = Main.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer( Main.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
				sender.sendMessage("Successfully aquired WorldEdit selection");
				int i = 0;
				for (BlockVector3 e : r) {
					Block block = (new Location(sender.getLocation().getWorld(), e.getX(), e.getY(), e.getZ())).getBlock();
					if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR && block.getType() != Material.VOID_AIR) {
						blocks.add(block);
					}
					i++;
				}
				
				BlockVector3 min = r.getMinimumPoint();
				BlockVector3 max = r.getMaximumPoint();
				
				sender.sendMessage("Successfully created new door " + door.name));
			} catch (IncompleteRegionException e) {
				e.printStackTrace();
				sender.sendMessage("Failed to get WorldEdit selection");
			}
        		
        	} else {
			sender.sendMessage("Error: Expected 3 args (name, phase, delay)");
		}
        	return true;
        }
        return false;
	}
	
	public void set(Block[] blocks, boolean open) {
		if (open) {
			this.open = new String[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				this.open[i] = blocks[i].getBlockData().getAsString() + "|" + blocks[i].getX() + "|" + blocks[i].getY() + "|" + blocks[i].getZ();
			}
		} else {
			this.closed = new String[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				this.closed[i] = blocks[i].getBlockData().getAsString() + "|" + blocks[i].getX() + "|" + blocks[i].getY() + "|" + blocks[i].getZ();
			}
		} 
	}
	
	// loc.getBlock().setBlockData(Bukkit.createBlockData(datas[i-minX][j-minY][k-minZ]));
}
