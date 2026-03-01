package com.lerdorf.fancy_arena;

import java.io.Serializable;

import net.md_5.bungee.api.ChatColor;

public class ChatMessage implements Serializable {

	public String name;
	public String message;
	public int tickDuration;
	
	public ChatMessage() {
		name = "Person";
		message = "Hello world!";
		tickDuration = 20;
	}
	
	public ChatMessage(String name, String message, int tickDuration) {
		this.name = name;
		this.message = message;
		this.tickDuration = tickDuration;
	}
	
}
