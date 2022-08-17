package com.lerdorf.fancy_plugin_1_19;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Door implements CommandExecutor, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7597479326871735203L;

	String[] open;
	String[][] phases;
	String[] closed;
	
	boolean active = false;
	
	public Door() {}
	
	public Door(Block[] blocks, boolean open) {
		set(blocks, open);
		active = true;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("door")){
        	
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
