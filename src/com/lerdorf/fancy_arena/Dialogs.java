package com.lerdorf.fancy_arena;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import net.md_5.bungee.api.ChatColor;

public class Dialogs implements Serializable {

	private String arenaName;
	
	public ArrayList<String> characters = new ArrayList<String>();
	public HashMap<String, String> nameColors = new HashMap<String, String>();
	public HashMap<String, String> textColors = new HashMap<String, String>();
	public HashMap<Integer, ArrayList<ChatMessage>> startMessages = new HashMap<Integer, ArrayList<ChatMessage>>();
	public HashMap<Integer, ArrayList<ChatMessage>> endMessages = new HashMap<Integer, ArrayList<ChatMessage>>();
	public HashMap<Integer, HashMap<String, ArrayList<ChatMessage>>> randomMessages = new HashMap<Integer, HashMap<String, ArrayList<ChatMessage>>>();
	
	public Dialogs() {
		
	}
	
	public Dialogs(String arenaName) {
		this.arenaName = arenaName;
	}
	
	public ChatColor getNameColor(String name) {
		return stringToChatColor(nameColors.get(name));
	}
	
	public ChatColor getTextColor(String name) {
		return stringToChatColor(textColors.get(name));
	}
	
	public void insertLevel(int lvlNum) {
		int maxStartMsgLvl = 0;
		for (int i : startMessages.keySet())
			if (i > maxStartMsgLvl)
				maxStartMsgLvl = i;

		int maxEndMsgLvl = 0;
		for (int i : endMessages.keySet())
			if (i > maxEndMsgLvl)
				maxEndMsgLvl = i;

		int maxRandMsgLvl = 0;
		for (int i : randomMessages.keySet())
			if (i > maxRandMsgLvl)
				maxRandMsgLvl = i;
		
		for (int i = maxStartMsgLvl; i >= lvlNum; i--) {
			if (startMessages.containsKey(i)) {
				startMessages.put(i+1, startMessages.get(i));
				startMessages.put(i, new ArrayList<ChatMessage>());
			}
		}
		for (int i = maxEndMsgLvl; i >= lvlNum; i--) {
			if (endMessages.containsKey(i)) {
				endMessages.put(i+1, endMessages.get(i));
				endMessages.put(i, new ArrayList<ChatMessage>());
			}
		}
		for (int i = maxRandMsgLvl; i >= lvlNum; i--) {
			if (randomMessages.containsKey(i)) {
				randomMessages.put(i+1, randomMessages.get(i));
				randomMessages.put(i, new HashMap<String, ArrayList<ChatMessage>>());
			}
		}
	}
	
	public void addCharacter(String name, String nameColor, String textColor) {
		nameColors.put(name, nameColor);
		textColors.put(name, textColor);
		characters.add(name);
	}
	
	public void addStartMessage(int lvlNum, String name, String message, int tickDuration) {
		if (!startMessages.containsKey(lvlNum))
			startMessages.put(lvlNum, new ArrayList<ChatMessage>());
		
		startMessages.get(lvlNum).add(new ChatMessage(name, message, tickDuration));
	}
	
	public void addEndMessage(int lvlNum, String name, String message, int tickDuration) {
		if (!endMessages.containsKey(lvlNum))
			endMessages.put(lvlNum, new ArrayList<ChatMessage>());
		
		endMessages.get(lvlNum).add(new ChatMessage(name, message, tickDuration));
	}
	
	public void addRandomMessage(int lvlNum, String name, String message) {
		if (!randomMessages.containsKey(lvlNum))
			randomMessages.put(lvlNum, new HashMap<String, ArrayList<ChatMessage>>());
		if (!randomMessages.get(lvlNum).containsKey(name))
			randomMessages.get(lvlNum).put(name, new ArrayList<ChatMessage>());
		
		randomMessages.get(lvlNum).get(name).add(new ChatMessage(name, message, 0));
	}
	
	public ChatMessage getStartMessage(int lvlNum, int index) {
		if (startMessages.containsKey(lvlNum) && startMessages.get(lvlNum).size() > index) {
			return startMessages.get(lvlNum).get(index);
		}
		return null;
	}

	public ChatMessage getEndMessage(int lvlNum, int index) {
		if (endMessages.containsKey(lvlNum) && endMessages.get(lvlNum).size() > index) {
			return endMessages.get(lvlNum).get(index);
		}
		return null;
	}
	
	public ChatMessage getRandomMessage(int lvlNum, String name) {
		if (randomMessages.containsKey(lvlNum) && randomMessages.get(lvlNum).containsKey(name) && randomMessages.get(lvlNum).get(name).size() > 0) {
			return randomMessages.get(lvlNum).get(name).get((int)(Math.random() * (randomMessages.get(lvlNum).get(name).size())));
		}
		return null;
	}
	
	public String constructMessage(ChatMessage msg, String player, String target) {
		
		String name = msg.name;
		if (msg.name.equalsIgnoreCase("Player")) {
			name = player;
		}
		String message = msg.message;
		message = message.replaceAll("@p", player != null ? player : "Steve");
		message = message.replaceAll("@t", target != null ? target : "Steve");
		message = message.replaceAll("@s", name);
		
		String result = ChatColor.WHITE + "" + ChatColor.BOLD + "<" + getNameColor(msg.name) + name + ChatColor.WHITE + "> " + ChatColor.RESET + "" + getTextColor(msg.name) + message;
		
		return result;
	}
	
	private ChatColor stringToChatColor(String str) {
		if (str == null)
			return ChatColor.WHITE;
		if (str.equalsIgnoreCase("blue"))
			return ChatColor.BLUE;
		else if (str.equalsIgnoreCase("dark_blue"))
			return ChatColor.DARK_BLUE;
		else if (str.equalsIgnoreCase("dark_aqua"))
			return ChatColor.DARK_AQUA;
		else if (str.equalsIgnoreCase("aqua") || str.equalsIgnoreCase("cyan"))
			return ChatColor.AQUA;
		else if (str.equalsIgnoreCase("dark_aqua") || str.equalsIgnoreCase("dark_cyan"))
			return ChatColor.DARK_AQUA;
		else if (str.equalsIgnoreCase("red"))
			return ChatColor.RED;
		else if (str.equalsIgnoreCase("gold"))
			return ChatColor.GOLD;
		else if (str.equalsIgnoreCase("yellow"))
			return ChatColor.YELLOW;
		else if (str.equalsIgnoreCase("green"))
			return ChatColor.GREEN;
		else if (str.equalsIgnoreCase("dark_green"))
			return ChatColor.DARK_GREEN;
		else if (str.equalsIgnoreCase("bold"))
			return ChatColor.BOLD;
		else if (str.equalsIgnoreCase("italic"))
			return ChatColor.ITALIC;
		else if (str.equalsIgnoreCase("gray") || str.equalsIgnoreCase("grey"))
			return ChatColor.GRAY;
		else if (str.equalsIgnoreCase("purple") || str.equalsIgnoreCase("light_purple"))
			return ChatColor.LIGHT_PURPLE;
		else if (str.equalsIgnoreCase("dark_purple"))
			return ChatColor.DARK_PURPLE;
		else if (str.equalsIgnoreCase("dark_red"))
			return ChatColor.DARK_RED;
		else if (str.equalsIgnoreCase("magic"))
			return ChatColor.MAGIC;
		
		
		return ChatColor.WHITE;
	}
	
}
