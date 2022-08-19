package com.lerdorf.fancy_plugin_1_19;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
	
	public static final int OPENING = 1, OPEN = -1;
	public static final int STATIONARY = 0;
	public static final int CLOSING = -1, CLOSED = -2;

	public int x1, y1, z1, x2, y2, z2, leverX, leverY, leverZ;
	public String world;
	public String name;
	public String[] open;
	public String[][] phases = new String[0][];
	public String[] closed;
	public int[][] xPoses;
	public int[][] yPoses;
	public int[][] zPoses;
	public int ticks;
	public int currentPos;
	public int dir = 0;

	public boolean active = false;
	
	public String filepath;

	public Door() {
	}

	public Door(Block[] blocks, boolean open, int x1, int y1, int z1, int x2, int y2, int z2, int ticks, String world, String name) {
		set(blocks, open);
		active = true;
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
		this.ticks = ticks;
		this.name = name;
		this.world = world;
		save();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("door")) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					sender.sendMessage("Listing doors:");
						for (String k : doors.keySet()) {
							Door d = doors.get(k);
							sender.sendMessage(d.name + " key:" + k + " open_def:" + (d.open != null) + " close_def:" + (d.closed != null) + " phases:" + d.phases.length + " current:" + currentPos);
						}
					
				}
			}
			else if (args.length >= 2) {
				if (args[1].equalsIgnoreCase("lever")) {
					if (doors.containsKey(args[0])) {
						Block b = player.getTargetBlock(null, 100);
						if (b == null)
							sender.sendMessage("Error: you must be looking at a block");
						else {
							doors.get(args[0]).leverX = b.getLocation().getBlockX();
							doors.get(args[0]).leverY = b.getLocation().getBlockY();
							doors.get(args[0]).leverZ = b.getLocation().getBlockZ();
							sender.sendMessage("Successfully added lever to door " + args[0]+ " at " + doors.get(args[0]).leverX + " " + doors.get(args[0]).leverY + " " + doors.get(args[0]).leverZ);
						}
					} else {
						sender.sendMessage("Could not find door by name " + args[0]);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("delete")) {
					if (doors.containsKey(args[1])) {
						doors.get(args[1]).delete();
						doors.remove(args[1]);
						sender.sendMessage("Successfully deleted door " + args[1]);
					} else {
						sender.sendMessage("Could not find door by name " + args[1]);
					}
					return true;
				}
				Door door;
				ArrayList<Block> blocks = new ArrayList<Block>();
				try {
					Region r = FancyPlugin.getWorldEdit().getWorldEdit().getSessionManager().get(new BukkitPlayer(FancyPlugin.getWorldEdit(), player)).getSelection(new BukkitWorld(player.getWorld()));
					sender.sendMessage("Successfully aquired WorldEdit selection");
					int i = 0;
					for (BlockVector3 e : r) {
						Block block = (new Location(player.getLocation().getWorld(), e.getX(), e.getY(), e.getZ())).getBlock();
						if (block.getType() != Material.AIR && block.getType() != Material.CAVE_AIR && block.getType() != Material.VOID_AIR) {
							//sender.sendMessage("Added block to door");
							blocks.add(block);
							i++;
						}
					}
					sender.sendMessage("Added " + i + " blocks to door");

					BlockVector3 min = r.getMinimumPoint();
					BlockVector3 max = r.getMaximumPoint();

					int phase = 0;
					
					if (args[1].equalsIgnoreCase("open") || args[1].equalsIgnoreCase("opened")) {
						phase = OPEN;
					} else if (args[1].equalsIgnoreCase("close") || args[1].equalsIgnoreCase("closed")) {
						phase = CLOSED;
					} else
						phase = Integer.parseInt(args[1]); 
					sender.sendMessage("Set phase to " + phase);
					
					if (doors.containsKey(args[0])) { // Edit existing Door
						door = doors.get(args[0]);
						sender.sendMessage("Door object acquired");
						if (args.length > 2) door.ticks = Integer.parseInt(args[2]);
						if (phase < 0)
							door.set(blocks.toArray(new Block[blocks.size()]), phase == OPEN);
						door.set(blocks.toArray(new Block[blocks.size()]), phase);
					} else { // Create new door
						door = new Door(blocks.toArray(new Block[blocks.size()]), phase == OPEN, min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ(), args.length > 2 ? Integer.parseInt(args[2]) : 20, player.getLocation().getWorld().getName(), args[0]);
						sender.sendMessage("Door object created");
						doors.put(door.name, door);
					}
					
					//door.world = player.getLocation().getWorld().getName();
					
					//door.name = args[0];
					
					door.save();
					
					sender.sendMessage("Successfully added to door " + door.name);
				} catch (Exception e) {
					e.printStackTrace();
					sender.sendMessage("Failed to create door: " + e.getMessage());
					//sender.sendMessage(e.getCause());
				}

			} else {
				sender.sendMessage("Error: Expected 3 args (name, phase, delay)");
			}
			return true;
		}
		return false;
	}
	
	public Door(String filepath) {
		load(filepath);
	}
	
	private long lastUpdateCall = 0;
		
	public void update() { // there are 50 milliseconds in 1 tick;  ticks * 50 = milliseconds
		if (System.currentTimeMillis() - lastUpdateCall >= ticks * 50) {
			lastUpdateCall = System.currentTimeMillis();
			if (dir != 0) {
				if (currentPos == -2)
					currentPos = phases.length;
				updateBlocks(currentPos, Math.min(phases.length, Math.max(-1, currentPos+dir)));
				if (currentPos == phases.length)
					currentPos = -2;
			}
		}
	}
	
	public void moveIt() {
		if (currentPos == CLOSED)
			dir = -1;
		else if (currentPos == OPEN)
			dir = 1;
		else if (currentPos > phases.length/2)
			dir = 1;
		else
			dir = 1;
	}
	
	public void updateBlocks(int curr, int next) {
		if (curr == next) {
			dir = 0;
			return;
		}
		System.out.println("door_" + name + " " + curr + " -> " + next);
		String[] currBlocks = getBlocks(curr);
		String[] nextBlocks = getBlocks(next);
		
		if (currBlocks == null) {
			if (curr < phases.length/2)
				currBlocks = open;
			else
				currBlocks = closed;
		}
		
		if (nextBlocks == null) {
			if (next < phases.length/2)
				nextBlocks = open;
			else
				nextBlocks = closed;
		}
		
		int currPosIndex = curr+2;
		if (curr == -1 || curr == OPEN)
			currPosIndex = 0;
		else if (curr == phases.length || curr == CLOSED) {
			currPosIndex = 1;
		}
		
		int nextPosIndex = next+2;
		if (next == -1 || next == OPEN)
			nextPosIndex = 0;
		else if (next == phases.length || next == CLOSED) {
			nextPosIndex = 1;
		}
		
		//boolean[] changed = new boolean[currBlocks.length]
		
		for (int i = 0; i < currBlocks.length; i++) {
			Block b = (new Location(Bukkit.getWorld(world), xPoses[currPosIndex][i], yPoses[currPosIndex][i], zPoses[currPosIndex][i])).getBlock();
			b.setType(Material.CAVE_AIR);
		}
		
		for (int i = 0; i < nextBlocks.length; i++) {
			Block b = (new Location(Bukkit.getWorld(world), xPoses[nextPosIndex][i], yPoses[nextPosIndex][i], zPoses[nextPosIndex][i])).getBlock();
			b.setBlockData(Bukkit.createBlockData(nextBlocks[i]));
		}
		currentPos = next;
	}
	
	public String[] getBlocks(int phase) {
		if (phase == OPEN)
			return open;
		else if (phase == CLOSED || phase == phases.length)
			return closed;
		return phases[phase];
	}
	
	public void set(Block[] blocks, boolean open) {
		if (open) {
			currentPos = OPEN;
			this.open = new String[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				this.open[i] = blocks[i].getBlockData().getAsString();
				addXPos(0, i, blocks[i].getX());
				addYPos(0, i, blocks[i].getY());
				addZPos(0, i, blocks[i].getZ());
			}
		} else {
			currentPos = CLOSED;
			this.closed = new String[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				this.closed[i] = blocks[i].getBlockData().getAsString();
				addXPos(1, i, blocks[i].getX());
				addYPos(1, i, blocks[i].getY());
				addZPos(1, i, blocks[i].getZ());
			}
		}
	}
	
	public void set(Block[] blocks, int phase) {
		currentPos = phase;
		if (phase <= 0)
			set(blocks, phase == 0);
		else {
			if (phases == null)
				phases = new String[phase+1][];
			else if (phase >= phases.length) {
				String[][] temp = phases; 
				phases = new String[phase+1][];
				for (int i = 0; i < temp.length; i++)
					phases[i] = temp[i];
			}
			phases[phase] = new String[blocks.length];
			for (int i = 0; i < blocks.length; i++) {
				phases[phase][i] = blocks[i].getBlockData().getAsString();
				addXPos(phase+2, i, blocks[i].getX());
				addYPos(phase+2, i, blocks[i].getY());
				addZPos(phase+2, i, blocks[i].getZ());
			}
		}
	}
	
	public void addXPos(int a, int b, int x) {
		if (xPoses == null) 
			xPoses = new int[a+1][];
		else if (a >= xPoses.length) {
			int[][] temp = xPoses;
			xPoses = new int[a+1][];
			for (int i = 0; i < temp.length; i++)
				xPoses[i] = temp[i];
		}
		if (xPoses[a] == null)
			xPoses[a] = new int[b+1];
		else if (b >= xPoses[a].length) {
			int[] temp = xPoses[a];
			xPoses[a] = new int[b+1];
			for (int i = 0; i < temp.length; i++)
				xPoses[a][i] = temp[i];
		}
		xPoses[a][b] = x;
	}
	
	public void addYPos(int a, int b, int y) {
		if (yPoses == null) 
			yPoses = new int[a+1][];
		else if (a >= yPoses.length) {
			int[][] temp = yPoses;
			yPoses = new int[a+1][];
			for (int i = 0; i < temp.length; i++)
				yPoses[i] = temp[i];
		}
		if (yPoses[a] == null)
			yPoses[a] = new int[b+1];
		else if (b >= yPoses[a].length) {
			int[] temp = yPoses[a];
			yPoses[a] = new int[b+1];
			for (int i = 0; i < temp.length; i++)
				yPoses[a][i] = temp[i];
		}
		yPoses[a][b] = y;
	}
	
	public void addZPos(int a, int b, int z) {
		if (zPoses == null) 
			zPoses = new int[a+1][];
		else if (a >= zPoses.length) {
			int[][] temp = zPoses;
			zPoses = new int[a+1][];
			for (int i = 0; i < temp.length; i++)
				zPoses[i] = temp[i];
		}
		if (zPoses[a] == null)
			zPoses[a] = new int[b+1];
		else if (b >= zPoses[a].length) {
			int[] temp = zPoses[a];
			zPoses[a] = new int[b+1];
			for (int i = 0; i < temp.length; i++)
				zPoses[a][i] = temp[i];
		}
		zPoses[a][b] = z;
	}
	
	public void delete() {
		(new File(world + "/FancyPlugin/door_" + name + ".dat")).delete();
	}
	
	public void save() {
		// TODO Auto-generated method stub
		try {
			if (this.filepath == null)
				this.filepath = world + "/FancyPlugin/door_" + name + ".dat";
			(new File(world + "/FancyPlugin")).mkdirs();
			(new File(this.filepath)).createNewFile();
			FileOutputStream fos = new FileOutputStream(this.filepath);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			// write object to file
			oos.writeObject(this);
			//this.filepath = world+"/FancyPlugin/" + filename;
			System.out.println("Writing to " + filepath + " with save()");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void save(String filename) {
		try {
			(new File(world + "/FancyPlugin")).mkdirs();
			(new File(world + "/FancyPlugin/" + filename + ".dat")).createNewFile();
			FileOutputStream fos = new FileOutputStream(world + "/FancyPlugin/" + filename + ".dat");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			// write object to file
			oos.writeObject(this);
			
			this.filepath = world+"/FancyPlugin/" + filename + ".dat";
			
			System.out.println("Writing to " + filepath + " with save(filename)");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void load(String filepath) {
		try (FileInputStream fis = new FileInputStream(filepath); ObjectInputStream ois = new ObjectInputStream(fis)) {

			// read object from file
			Door yeet = (Door) ois.readObject();
			x1 = yeet.x1;
			y1 = yeet.y1;
			z1 = yeet.z1;
			x2 = yeet.x2;
			y2 = yeet.y2;
			z2 = yeet.z2;
			leverX = yeet.leverX;
			leverY = yeet.leverY;
			leverZ = yeet.leverZ;
			world = yeet.world;
			name = yeet.name;
			open = yeet.open;
			phases = yeet.phases;
			closed = yeet.closed;
			xPoses = yeet.xPoses;
			yPoses = yeet.yPoses;
			zPoses = yeet.zPoses;
			ticks = yeet.ticks;
			currentPos = yeet.currentPos;
			dir = yeet.dir;
			active = yeet.active;
			this.filepath = filepath;
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// loc.getBlock().setBlockData(Bukkit.createBlockData(datas[i-minX][j-minY][k-minZ]));
}
