package com.lerdorf.fancy_arena;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class FancyLevel implements Serializable {

	private String arenaName;
	private String levelName = "Fancy Level";
	private HashMap<String, Integer> monsters;
	private HashMap<String, Integer> bossMonsters;
	private int minBossHealth = 0;
	private List<String> monsterSpawn;
	private String playerSpawn;
	private List<String> beginCommands;
	private List<String> endCommands;
	private List<String> chestLoots;
	private String minPos;
	private String maxPos;
	
	
	private boolean running = false;
	private int nextWaveDelay = 40;
	private int maxMonstersAlive = 2;
	private int waveStartDelay = 60;
	
	
	public FancyLevel() {
		
	}
	
	public FancyLevel(String arenaName) {
		
	}
	
	public void setMinBossHealth(int minBossHealth) {
		this.minBossHealth = minBossHealth;
	}
	
	public int getMinBossHealth() {
		return minBossHealth;
	}
	
	public void setMinPos(String minPos) {
		this.minPos = minPos;
	}
	
	public String getMinPos() {
		return minPos;
	}

	public void setMaxPos(String minPos) {
		this.maxPos = minPos;
	}
	
	public String getMaxPos() {
		return maxPos;
	}

	public Location getMinPosLoc() {
		return stringToLoc(getMinPos());
	}
	public Location getMaxPosLoc() {
		return stringToLoc(getMaxPos());
	}

	public List<String> getChestLoots() {
		return chestLoots;
	}
	
	public void setChestLoots(List<String> chestLoots) {
		this.chestLoots = chestLoots;
	}
	
	public void addChestLoot(String loc) {
		if (chestLoots == null)
			chestLoots = new ArrayList<String>();
		chestLoots.add(loc);
	}
	
	public int getNextWaveDelay() {
		return nextWaveDelay;
	}
	
	public void setNextWaveDelay(int nextWaveDelay) {
		this.nextWaveDelay = nextWaveDelay;
	}
	
	public int getMaxMonstersAlive() {
		return maxMonstersAlive;
	}
	
	public void setMaxMonstersAlive(int maxMonstersAlive) {
		this.maxMonstersAlive = maxMonstersAlive;
	}
	
	public int getWaveStartDelay() {
		return waveStartDelay;
	}
	
	public void setWaveStartDelay(int waveStartDelay) {
		this.waveStartDelay = waveStartDelay;
	}
	
	public List<String> getBeginCommands() {
		return beginCommands;
	}
	
	public void setBeginCommands(List<String> beginCommands) {
		this.beginCommands = beginCommands;
	}

	public void addBeginCommand(String command) {
		if (beginCommands == null)
			beginCommands = new ArrayList<String>();
		beginCommands.add(command);
	}
	
	public boolean removeBeginCommand(String command) {
		if (beginCommands.contains(command)) {
			beginCommands.remove(command);
			return true;
		}
		return false;
	}
	
	public List<String> getEndCommands() {
		return endCommands;
	}
	
	public void setEndCommands(List<String> endCommands) {
		this.endCommands = endCommands;
	}
	
	public void addEndCommand(String command) {
		if (endCommands == null)
			endCommands = new ArrayList<String>();
		endCommands.add(command);
	}
	
	public boolean removeEndCommand(String command) {
		if (endCommands.contains(command)) {
			endCommands.remove(command);
			return true;
		}
		return false;
	}
	
	public String getLevelName() {
		return levelName;
	}
	
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public String getPlayerSpawn() {
		return playerSpawn;
	}
	
	public void setPlayerSpawn(String playerSpawn) {
		this.playerSpawn = playerSpawn;
	}
	
	public String getArenaName() {
		return arenaName;
	}
	
	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}
	
	public HashMap<String, Integer> getMonsters() {
		return monsters;
	}
	
	public void addMonster(String monster) {
		if (monsters == null)
			monsters = new HashMap<String, Integer>();
		if (monsters.containsKey(monster)) {
			monsters.put(monster, monsters.get(monster)+1);
		} else {
			monsters.put(monster, 1);
		}
	}
	
	public void removeMonster(String monster) {
		if (monsters == null)
			monsters = new HashMap<String, Integer>();
		if (monsters.containsKey(monster)) {
			monsters.put(monster, monsters.get(monster)-1);
			if (monsters.get(monster) <= 0) {
				monsters.remove(monster);
			}
		}
	}
	
	public void setMonsters(HashMap<String, Integer> monsters)
	{
		this.monsters = monsters;
	}

	public HashMap<String, Integer> getBossMonsters() {
		return bossMonsters;
	}
	
	public void setBossMonsters(HashMap<String, Integer> bossMonsters)
	{
		this.bossMonsters = bossMonsters;
	}

	public void addBossMonster(String monster) {
		if (bossMonsters == null)
			bossMonsters = new HashMap<String, Integer>();
		if (bossMonsters.containsKey(monster)) {
			bossMonsters.put(monster, bossMonsters.get(monster)+1);
		} else {
			bossMonsters.put(monster, 1);
		}
	}
	
	public void removeBossMonster(String monster) {
		if (bossMonsters == null)
			bossMonsters = new HashMap<String, Integer>();
		if (bossMonsters.containsKey(monster)) {
			bossMonsters.put(monster, bossMonsters.get(monster)-1);
			if (bossMonsters.get(monster) <= 0) {
				bossMonsters.remove(monster);
			}
		}
	}

	public List<String> getMonsterSpawn() {
		return monsterSpawn;
	}
	
	public void addMonsterSpawnLoc(Location loc) {
		if (monsterSpawn == null)
			monsterSpawn = new ArrayList<String>();
		monsterSpawn.add(locToString(loc));
	}
	
	public int monsterCount() {
		return monsters.size();
	}
	
	public int mosnterSpawnCount() {
		return monsterSpawn.size();
	}
	
	public void setMonsterSpawn(List<String> monsterSpawn)
	{
		this.monsterSpawn = monsterSpawn;
	}
	
	public static String locToString(Location loc) {
		return loc.getWorld().getName() + ":" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getPitch() + ";" + loc.getYaw();
	}
	
	public void spawnWave() {
		if (monsters != null) {
			for (String monster : monsters.keySet()) {
				if (monster != null && monster.length() > 0)
					for (int i = 0; i < monsters.get(monster); i++)
						spawnMonster(monster, monsterSpawn.get((int)(Math.random()*monsterSpawn.size())));
			}
		}
		if (bossMonsters != null) {
			for (String monster : bossMonsters.keySet()) {
				if (monster != null && monster.length() > 0)
					for (int i = 0; i < bossMonsters.get(monster); i++)
						spawnMonster(monster, monsterSpawn.get((int)(Math.random()*monsterSpawn.size())), true);
			}
		}
	}
	
	public Location getPlayerSpawnLoc() {
		return stringToLoc(getPlayerSpawn());
	}
	
	public void spawnMonster(String monster, String location) {
		spawnMonster(monster, location, false);
	}
	
	public void spawnMonster(String monster, String location, boolean boss) {
		//Bukkit.broadcastMessage("Spawning " + monster + " at " + location);
		Location loc = stringToLoc(location);
		Entity entity = FancyMob.Spawn(loc, monster);
		if (entity == null) {
			try {
				EntityType entityType = EntityType.valueOf(monster.toUpperCase());
				
				entity = loc.getWorld().spawnEntity(loc, entityType);
			} catch (Exception e) {
			}
		}
		if (entity != null && entity instanceof LivingEntity le) {
			if (Arena.arenaEntities.get(arenaName) == null) {
				Arena.arenaEntities.put(arenaName, new ArrayList<LivingEntity>());
			}
			if (boss) {
				entity.addScoreboardTag("FancyBoss");
				//Bukkit.broadcastMessage("Creating bossbar");
				BossBar bossBar = Bukkit.getServer().createBossBar(entity.getCustomName(), BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY);
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					bossBar.addPlayer(p);
				}
				Arena.bossBars.put(le, bossBar);
			}
			entity.addScoreboardTag("Arena_" + arenaName);
			entity.addScoreboardTag("ArenaMob");
			Arena.arenaEntities.get(arenaName).add(le);
		}
		else {
			Bukkit.broadcastMessage(ChatColor.RED + "Unknown entity: " + monster);
		}
	}
	
	public void killAllMonsters() {
		for (LivingEntity e : Arena.arenaEntities.get(arenaName)) {
			e.remove();
		}
		Arena.arenaEntities.clear();
	}
	
	public static Location stringToLoc(String str) {
		String worldStr = str.substring(0, str.indexOf(':'));
		String x = str.substring(str.indexOf(':')+1, str.indexOf(';'));
		String y = str.substring(str.indexOf(';')+1, str.indexOf(';', str.indexOf(';')+1));
		String z = str.substring(str.indexOf(';', str.indexOf(';')+1)+1);
		String pitch = "0";
		String yaw = "0";
		
		if (str.indexOf(';', str.indexOf(';', str.indexOf(';')+1)+1) > 0) {
			z = str.substring(str.indexOf(';', str.indexOf(';')+1)+1, str.indexOf(';', str.indexOf(';', str.indexOf(';')+1)+1));
			pitch = str.substring(str.indexOf(';', str.indexOf(';', str.indexOf(';')+1)+1)+1, str.indexOf(';', str.indexOf(';', str.indexOf(';', str.indexOf(';')+1)+1)+1));
			yaw = str.substring(str.indexOf(';', str.indexOf(';', str.indexOf(';', str.indexOf(';')+1)+1)+1)+1);
		}
		
		Location loc = new Location(Bukkit.getWorld(worldStr), Double.parseDouble(x), Double.parseDouble(y), Double.parseDouble(z));
		loc.setPitch(Float.parseFloat(pitch));
		loc.setYaw(Float.parseFloat(yaw));
		
		return loc;
	}
	
	public Location getMonsterSpawnLoc(int i) {
		
		
		return stringToLoc(monsterSpawn.get(i));
		
		
	}

	public Inventory getRandomChestLoot() {
		// TODO Auto-generated method stub
		
		//Bukkit.broadcastMessage("Getting random chest loot");
		
		if (chestLoots != null && chestLoots.size() > 0) {
			//Bukkit.broadcastMessage("Level has chest loot");
			
			int rand = (int)(Math.random() * chestLoots.size());
			//Bukkit.broadcastMessage("Selecting loot " + rand);
			
			Location loc = stringToLoc(chestLoots.get(rand));
			if (loc.getBlock().getState().getType() == Material.BARREL) {
				//Bukkit.broadcastMessage("Found barrel");
				return ((Barrel)loc.getBlock().getState()).getInventory();
			} else {
				//Bukkit.broadcastMessage("Stored container isn't a barrel, it is " + loc.getBlock().getType());
			}
		}
		
		return null;
	}
}
