package com.lerdorf.fancy_arena;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Castle {
	
	
	
	// Function to asynchronously clone a region from "aeons_lobby" to "aeons_castle"
    public static void cloneRegionAsync(Plugin plugin) {
        final World source = Bukkit.getWorld("aeons_lobby");
        final World target = Bukkit.getWorld("aeons_castle");
        if (source == null || target == null) {
            Bukkit.getLogger().severe("One or both of the worlds were not found!");
            return;
        }

        // Define source region coordinates
        final int srcMinX = -2920, srcMinY = -59, srcMinZ = -3140;
        final int srcMaxX = -2640, srcMaxY = 158, srcMaxZ = -2907;
        final int width  = srcMaxX - srcMinX + 1;
        final int height = srcMaxY - srcMinY + 1;
        final int depth  = srcMaxZ - srcMinZ + 1;

        // Define target region so that it’s centered at 0,0.
        // In this case, the target region is given as -140,-50,-81 to 140,167,81.
        final int tgtMinX = -140, tgtMinY = -50, tgtMinZ = -121;
        
        // Preload all chunks in the source region
        for (int x = srcMinX; x <= srcMaxX; x += 16) {
            for (int z = srcMinZ; z <= srcMaxZ; z += 16) {
            	//Chunk chunk = source.getChunkAt(x >> 4, z >> 4);
            	Chunk chunk = source.getChunkAt(new Location(source, x, 0, z));
                source.loadChunk(chunk);
                //chunk.setForceLoaded(true);
            }
        }
        // Preload all chunks in the target region
        for (int x = tgtMinX; x < tgtMinX + width; x += 16) {
            for (int z = tgtMinZ; z < tgtMinZ + depth; z += 16) {
            	//Chunk chunk = target.getChunkAt(x >> 4, z >> 4);
            	Chunk chunk = source.getChunkAt(new Location(source, x, 0, z));
                target.loadChunk(chunk);
                //chunk.setForceLoaded(true);
            }
        }
        
        // Use an asynchronous task to process the cloning in small batches.
        BukkitTask task = new BukkitRunnable() {
            int x = srcMinX, y = srcMinY, z = srcMinZ;
            @Override
            public void run() {
                int blocksPerTick = 15000;  // adjust as needed to balance speed and lag
                int count = 0;
                while (count < blocksPerTick) {
                    // When finished with a column, move to the next coordinate
                    if (y > srcMaxY) {
                        y = srcMinY;
                        z++;
                        if (z > srcMaxZ) {
                            z = srcMinZ;
                            x++;
                            if (x > srcMaxX) {
                                cancel();
                                // After finishing, unload the chunks that we loaded.
                                unloadChunks(source, srcMinX, srcMaxX, srcMinZ, srcMaxZ);
                                unloadChunks(target, tgtMinX, tgtMinX + width - 1, tgtMinZ, tgtMinZ + depth - 1);
                                //getLogger().info("Region clone complete!");
                                return;
                            }
                        }
                    }
                    
                    // Calculate the relative coordinates and determine the target block position.
                    Block srcBlock = source.getBlockAt(x, y, z);
                    int relX = x - srcMinX;
                    int relY = y - srcMinY;
                    int relZ = z - srcMinZ;
                    int targetX = tgtMinX + relX;
                    int targetY = tgtMinY + relY;
                    int targetZ = tgtMinZ + relZ;
                    
                    // Capture block data
                    final BlockData data = srcBlock.getBlockData();
                    
                    // Schedule synchronous update to modify the target block (must be on main thread)
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        target.getBlockAt(targetX, targetY, targetZ).setBlockData(data);
                    });
                    
                    y++;
                    count++;
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
        
        Arena.bigTasks.add(task);
    }
    
    // Helper method to unload chunks in a rectangular region.
    private static void unloadChunks(World world, int minX, int maxX, int minZ, int maxZ) {
        for (int x = minX; x <= maxX; x += 16) {
            for (int z = minZ; z <= maxZ; z += 16) {
            	try {
	            	//Chunk chunk = world.getChunkAt(x >> 4, z >> 4);
	            	Chunk chunk = world.getChunkAt(new Location(world, x, 0, z));
	            	//chunk.setForceLoaded(false);
	                world.unloadChunk(chunk);
            	} catch (Exception e) {}
            }
        }
    }
    
    // Function to slowly delete (or "eat away at") the pasted structure in "aeons_castle".
    // This function deletes blocks layer by layer along the Y-axis.
    public static void deleteStructureAsync(Plugin plugin, int delay) {
        final World target = Bukkit.getWorld("aeons_castle");
        if (target == null) {
            Bukkit.getLogger().severe("World aeons_castle not found!");
            return;
        }
        
        // The structure was pasted into the region defined below.
        final int tgtMinX = -140, tgtMinZ = -121;
        final int width = 281;   // from -140 to 140 (inclusive)
        final int depth = 121*2;   // from -121 to 121 (inclusive)
        final int startY = -64;
        final int endY = 167;
        
        // Schedule a synchronous repeating task to delete one y-layer at a time.
        BukkitTask task = new BukkitRunnable() {
            int currentY = startY;
            @Override
            public void run() {
                if (currentY > endY) {
                    cancel();
                    //getLogger().info("Deletion of structure complete!");
                    return;
                }
                // Optionally, load the chunks for this layer if they are not loaded.
                for (int x = tgtMinX; x < tgtMinX + width; x += 16) {
                    for (int z = tgtMinZ; z < tgtMinZ + depth; z += 16) {
                        //target.loadChunk(target.getChunkAt(x >> 4, z >> 4));
                    	Chunk chunk = target.getChunkAt(new Location(target, x, 0, z));
                    }
                }
                
                // Delete every block in the current y-layer.
                for (int x = tgtMinX; x < tgtMinX + width; x++) {
                    for (int z = tgtMinZ; z < tgtMinZ + depth; z++) {
                        target.getBlockAt(x, currentY, z).setType(Material.AIR);
                    }
                }
                
                // Optionally, you can unload these chunks afterwards.
                for (int x = tgtMinX; x < tgtMinX + width; x += 16) {
                    for (int z = tgtMinZ; z < tgtMinZ + depth; z += 16) {
                        //target.unloadChunk(target.getChunkAt(x >> 4, z >> 4));
                    	Chunk chunk = target.getChunkAt(new Location(target, x, 0, z));
                    }
                }
                
                currentY++;
            }
        }.runTaskTimer(plugin, 0L, delay); // one layer per second (20 ticks)
        
        Arena.bigTasks.add(task);
    }
}
