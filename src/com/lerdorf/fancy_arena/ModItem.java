package com.lerdorf.fancy_arena;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Multimap;
import com.sk89q.jchronic.utils.StringUtils;

import net.md_5.bungee.api.ChatColor;

public class ModItem implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("moditem")) {

			if (!(sender instanceof Player)) {
	            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
	            return true;
	        }
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("fix_attr")) {
					fixAttribute(sender);
				} else  if (args[0].equalsIgnoreCase("lore")) {
					try {
						ArrayList<String> lores = new ArrayList<String>();
						int loreIndex = 0;
						for (int i = 1; i < args.length; i++) {
							while (lores.size() < loreIndex+1)
								lores.add("");
							lores.set(loreIndex, lores.get(loreIndex) + " " + args[i]);
							if (lores.get(loreIndex).length() > 12) {
								loreIndex++;
							}
						}
						ItemStack item = ((Player)sender).getEquipment().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						meta.setLore(lores);
						item.setItemMeta(meta);
						sender.sendMessage(ChatColor.GREEN + "Successfully updated item lore");
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem lore <string>");
						return false;
					}
				} 
				else if (args[0].equalsIgnoreCase("enchant")) {
					try {
						ItemStack item = ((Player)sender).getEquipment().getItemInMainHand();
						ItemMeta meta = item.getItemMeta();
						
						meta.addEnchant(Enchantment.getByName(args[1]), Integer.parseInt(args[2]), true);

						item.setItemMeta(meta);
						sender.sendMessage(ChatColor.GREEN + "Successfully updated item lore");
						
						return true;
					} catch (Exception e) {
						return false;
					}
				} 
				else if (args[0].equalsIgnoreCase("unbreakable")) {
					Unbreakable(sender);
					return true;
				} else if (args[0].equalsIgnoreCase("damage") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ATTACK_DAMAGE, "generic.attackDamage", new EquipmentSlot[] {EquipmentSlot.HAND});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem damage <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("attackSpeed") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", new EquipmentSlot[] {EquipmentSlot.HAND});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem attackSpeed <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("knockback") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ATTACK_KNOCKBACK, "generic.attackKnockback", new EquipmentSlot[] {EquipmentSlot.HAND});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem knockback <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("jump") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_JUMP_STRENGTH, "generic.jumpStrength", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem jump <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("moveSpeed") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_MOVEMENT_SPEED, "generic.movementSpeed", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem moveSpeed <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("maxHealth") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_MAX_HEALTH, "generic.maxHealth", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem maxHealth <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("armor") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ARMOR, "generic.armor", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem armor <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("toughness") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ARMOR_TOUGHNESS, "generic.armorToughness", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem toughness <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("knockbackResistance") && args.length > 1) {
					try {
						modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_KNOCKBACK_RESISTANCE, "generic.knockbackResistance", new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem knockbackResistance <value>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("modelData") && args.length > 1) {
					try {
						int data = Integer.parseInt(args[1]);
						
				        Player player = (Player) sender;
				        ItemStack item = player.getInventory().getItemInMainHand();

				        if (item == null || item.getType().isAir()) {
				            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
				            return true;
				        }
				        
				        ItemMeta meta = item.getItemMeta();
				        
				        meta.setCustomModelData(data);
				        
				        item.setItemMeta(meta);
				        
				        sender.sendMessage(ChatColor.GREEN + "CustomModelData updated to " + data);
						
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem modelData <integer value>");
						return false;
					}
				}
				else if (args[0].equalsIgnoreCase("color") && args.length > 1) {
					try {
						//modAttribute(sender, Double.parseDouble(args[1]), Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed");
						switch (args[1].toLowerCase()) {
							case "green":
								ColoredName(sender, ChatColor.GREEN);
								break;
							case "red":
								ColoredName(sender, ChatColor.RED);
								break;
							case "aqua":
								ColoredName(sender, ChatColor.AQUA);
								break;
							case "white":
								ColoredName(sender, ChatColor.WHITE);
								break;
							case "blue":
								ColoredName(sender, ChatColor.BLUE);
								break;
							case "dark_red":
								ColoredName(sender, ChatColor.DARK_RED);
								break;
							case "gold":
								ColoredName(sender, ChatColor.GOLD);
								break;
							case "gray":
							case "grey":
								ColoredName(sender, ChatColor.GRAY);
								break;
							case "magic":
								ColoredName(sender, ChatColor.MAGIC);
								break;
							case "purple":
								ColoredName(sender, ChatColor.LIGHT_PURPLE);
								break;
							case "dark_purple":
								ColoredName(sender, ChatColor.DARK_PURPLE);
								break;
							case "yellow":
								ColoredName(sender, ChatColor.YELLOW);
								break;
							case "reset":
								ColoredName(sender, ChatColor.RESET);
								break;
						}
						return true;
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.RED + "Usage: /moditem color <color>");
						return false;
					}
				} else if (args[0].equalsIgnoreCase("glint")) {
					Player player = (Player) sender;
			        ItemStack item = player.getInventory().getItemInMainHand();

			        if (item == null || item.getType().isAir()) {
			            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
			            return true;
			        }
			        
			        ItemMeta meta = item.getItemMeta();
			        if (meta == null) {
			            player.sendMessage(ChatColor.RED + "This item cannot be modified.");
			            return true;
			        }

			        {
			            // Add a harmless enchantment to add the glint
			            //meta.addEnchant(org.bukkit.enchantments., 1, true);
			        	boolean currentGlint = false;
			        	try {
			        		currentGlint = meta.getEnchantmentGlintOverride();
			        	} catch (Exception e) {
			        		
			        	}
			        	meta.setEnchantmentGlintOverride(!currentGlint);
			            item.setItemMeta(meta);
			        	if (currentGlint)
			        		player.sendMessage(ChatColor.YELLOW + "Enchantment glint removed from the item!");
			        	else
			        		player.sendMessage(ChatColor.GREEN + "Enchantment glint added to the item!");
			        }
			        return true;
			    }
			}
		}
		return false;
	}
	
	public static void fixAttribute(CommandSender sender) {
		Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta != null) { 
        	EquipmentSlot slot = null;
        	if (item.getType().toString().toLowerCase().contains("helmet") || item.getType() == Material.PLAYER_HEAD) {
        		slot = EquipmentSlot.HEAD;
        	} else if (item.getType().toString().toLowerCase().contains("chestplate")) {
        		slot = EquipmentSlot.CHEST;
        	} else if (item.getType().toString().toLowerCase().contains("leggings")) {
        		slot = EquipmentSlot.LEGS;
        	} else if (item.getType().toString().toLowerCase().contains("boots")) {
        		slot = EquipmentSlot.FEET;
        	} else {
        		if (item.getType().toString().toLowerCase().contains("helmet") || item.getType().toString().toLowerCase().contains("case") || item.getType() == Material.PLAYER_HEAD || meta.getDisplayName().toLowerCase().contains("helmet") || meta.getDisplayName().toLowerCase().contains("hood") || meta.getDisplayName().toLowerCase().contains("head") || meta.getDisplayName().toLowerCase().contains("hat") || meta.getDisplayName().toLowerCase().contains("crown")) {
            		slot = EquipmentSlot.HEAD;
            	} else if (item.getType().toString().toLowerCase().contains("chestplate")) {
            		slot = EquipmentSlot.CHEST;
            	} else if (item.getType().toString().toLowerCase().contains("leggings")) {
            		slot = EquipmentSlot.LEGS;
            	} else if (item.getType().toString().toLowerCase().contains("boots")) {
            		slot = EquipmentSlot.FEET;
            	} else {
            		slot = EquipmentSlot.HAND;
            	}
        	}
        	
        	Multimap<Attribute, AttributeModifier> attr = meta.getAttributeModifiers();
        	
        	meta.setAttributeModifiers(null);
        	
        	for (Attribute a : attr.keySet()) {
        		Collection<AttributeModifier> modCol = attr.get(a);
        		for (AttributeModifier mod : modCol) {
        			if (mod.getSlot() == slot) {
        				meta.addAttributeModifier(a, mod);
        			}
        		}
        	}
        	
        	item.setItemMeta(meta);
        }
	}

	public static ItemMeta setAttribute(ItemMeta meta, double amount, Attribute attribute, String attrStr, EquipmentSlot slots[]) {
		if (meta != null) {
        	//double damage = meta.getAttributeModifiers().t
        	
        	
        	if (meta.getAttributeModifiers(attribute) != null) {

           	 // Remove existing attack damage modifiers
               meta.getAttributeModifiers(attribute).forEach(modifier -> 
                   meta.removeAttributeModifier(attribute, modifier)
               );
        	}
        	
            
            for (EquipmentSlot slot : slots) {
	            AttributeModifier modifier = new AttributeModifier(
	                UUID.randomUUID(),
	                attrStr,
	                amount,
	                AttributeModifier.Operation.ADD_NUMBER,
	                slot
	            );
	
	            meta.addAttributeModifier(attribute, modifier);
            }
		}
		return meta;
	}

	public static ItemMeta changeAttribute(ItemMeta meta, double amount, Attribute attribute, String attrStr, EquipmentSlot slots[]) {
		if (meta != null) {
        	//double damage = meta.getAttributeModifiers().t
        	
        	double initAmount = 0;
        	
        	if (meta.getAttributeModifiers(attribute) != null) {
        		initAmount = meta.getAttributeModifiers(attribute).stream()
	                    .mapToDouble(AttributeModifier::getAmount)
	                    .sum(); // Sum all modifiers
        		initAmount /= meta.getAttributeModifiers(attribute).size();

           	 // Remove existing attack damage modifiers
               meta.getAttributeModifiers(attribute).forEach(modifier -> 
                   meta.removeAttributeModifier(attribute, modifier)
               );
        	} else {
        		initAmount = 0;
        	}
        	
            
            for (EquipmentSlot slot : slots) {
	            AttributeModifier modifier = new AttributeModifier(
	                UUID.randomUUID(),
	                attrStr,
	                initAmount + amount,
	                AttributeModifier.Operation.ADD_NUMBER,
	                slot
	            );
	
	            meta.addAttributeModifier(attribute, modifier);
            }
		}
		return meta;
	}
	
	public static ItemStack makeItem(String name, int modelData, Material type, double attackSpeed, double moveSpeed, double armor, double jumpHeight, EquipmentSlot[] slots) {
		ItemStack result = new ItemStack(type);
		ItemMeta meta = result.getItemMeta();
		
		meta.setDisplayName(name);
		meta.setCustomModelData(modelData);
		
		if (attackSpeed != 0)
			changeAttribute(meta, attackSpeed, Attribute.GENERIC_ATTACK_SPEED, "generic.attackSpeed", slots);
		if (moveSpeed != 0)
			changeAttribute(meta, moveSpeed, Attribute.GENERIC_MOVEMENT_SPEED, "generic.movementSpeed", slots);
		if (armor != 0)
			changeAttribute(meta, armor, Attribute.GENERIC_ARMOR, "generic.armor", slots);
		if (jumpHeight != 0)
			changeAttribute(meta, jumpHeight, Attribute.GENERIC_JUMP_STRENGTH, "generic.jumpStrength", slots);
		result.setItemMeta(meta);
		return result;
	}
	
	public static void modAttribute(CommandSender sender, double amount, Attribute attribute, String attrStr, EquipmentSlot[] slots) {

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
            return;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
        	changeAttribute(meta, amount, attribute, attrStr, slots);
            item.setItemMeta(meta);
            fixAttribute(sender);
            //player.sendMessage(ChatColor.GREEN + "Set the " + attrStr + " of the item to " + Double.toString(initAmount + amount) + "!");
            player.sendMessage(ChatColor.GREEN + "Added " + amount + " to " + attrStr + "!");
        } else {
            player.sendMessage(ChatColor.RED + "This item cannot be modified.");
        }
	}
	
	public void ColoredName(CommandSender sender, ChatColor chatColor) {

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
            return;
        }
        
        
		ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            // Remove italics by applying ChatColor.RESET
            String displayName = ChatColor.stripColor(meta.getDisplayName());
            meta.setDisplayName(chatColor + displayName);
            item.setItemMeta(meta);
            player.sendMessage(ChatColor.GREEN + "Updated the " + chatColor + "color" + ChatColor.GREEN + " of the custom name!");
        } else {
            player.sendMessage(ChatColor.RED + "This item does not have a custom name.");
        }
	}
	
	public static void Unbreakable(CommandSender sender) {

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
        	if (meta.isUnbreakable()) {
	            meta.setUnbreakable(false);
	            item.setItemMeta(meta);
	            player.sendMessage(ChatColor.YELLOW + "The item in your hand is no longer unbreakable!");
        	} else {
	            meta.setUnbreakable(true);
	            item.setItemMeta(meta);
	            player.sendMessage(ChatColor.GREEN + "The item in your hand is now unbreakable!");
        	}
        } else {
            player.sendMessage(ChatColor.RED + "This item cannot be modified.");
        }
	}
	
}
