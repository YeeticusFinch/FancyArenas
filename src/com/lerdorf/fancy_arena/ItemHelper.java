package com.lerdorf.fancy_arena;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Multimap;

public class ItemHelper {

	    /**
	     * Compares two melee weapons.
	     * @param weapon1 The first weapon.
	     * @param weapon2 The second weapon.
	     * @return true if weapon1 has a higher overall damage score than weapon2, false otherwise.
	     */
	    public static boolean isBetterMeleeWeapon(ItemStack weapon1, ItemStack weapon2) {
	        double damage1 = getWeaponDamage(weapon1);
	        double damage2 = getWeaponDamage(weapon2);
	        return damage1 > damage2;
	    }
	    
	    public static boolean isBetterArmor(ItemStack armor1, ItemStack armor2) {
	    	return getArmor(armor1) > getArmor(armor2);
	    }
	    
	    public static boolean isBetterRangedWeapon(ItemStack weapon1, ItemStack weapon2) {
	    	return (getRangedScore(weapon1) > getRangedScore(weapon2));
	    }
	    
	    public static double getRangedScore(ItemStack weapon) {
	    	double score = 0;
	    	String name = weapon.getType().toString().toLowerCase();
	    	if (weapon.hasItemMeta() && weapon.getItemMeta().hasDisplayName())
	    		name = weapon.getItemMeta().getDisplayName().toLowerCase();
	    	
	    	if (name.contains("bow"))
	    		score += 1;
	    	if (name.contains("longbow"))
	    		score += 2;
	    	if (name.contains("wand"))
	    		score += 2;
	    	if (name.contains("staff"))
	    		score += 3;
	    	if (name.contains("musket"))
	    		score += 3;
	    	if (name.contains("heavy"))
	    		score += 2;
	    	if (name.contains("rifle"))
	    		score += 2;
	    	if (name.contains("shotgun"))
	    		score += 3;
	    	if (name.contains("ak-47"))
	    		score += 4;
	    	if (name.contains("magic missile"))
	    		score += 1;
	    	
	    	
	    	if (name.contains("chain lightning"))
	    		score += 2;
	    	if (name.contains("shooting stars"))
	    		score += 5;
	    	if (name.contains("fireball"))
	    		score += 1;
	    	if (name.contains("wither skulls"))
	    		score += 3;
	    	
	    	if (name.contains("royal"))
	    		score += 6;
	    	if (name.contains("divine") || name.contains("holy"))
	    		score += 7;
	    	if (name.contains("demonic") || name.contains("demon"))
	    		score += 8;
	    	if (name.contains("lesser"))
	    		score -= 1;
	    	if (name.contains("greater") || name.contains("deadly"))
	    		score += 2;
	    	if (name.contains("enchanted") || name.contains("super deadly"))
	    		score += 2;
	    	if (name.contains("superior"))
	    		score += 3;
	    	if (name.contains("supreme"))
	    		score += 7;
	    	if (name.contains("sunbeam"))
	    		score += 6;
	    	
	    	return score;
	    }
	    
	    public static double getArmor(ItemStack armor) {
	    	if (armor == null) return 0;
	    	
	    	double prot = 0;
	    	
	    	prot += getBaseProt(armor.getType());
	    	if (armor.hasItemMeta()) {
	    		ItemMeta meta = armor.getItemMeta();
	    		if (meta != null && meta.hasAttributeModifiers()) {
	    			Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
	                if (modifiers != null && modifiers.containsKey(Attribute.GENERIC_ARMOR)) {
	                    for (AttributeModifier modifier : modifiers.get(Attribute.GENERIC_ARMOR)) {
	                        prot += modifier.getAmount();
	                    }
	                }
	                if (modifiers != null && modifiers.containsKey(Attribute.GENERIC_ARMOR_TOUGHNESS)) {
	                    for (AttributeModifier modifier : modifiers.get(Attribute.GENERIC_ARMOR_TOUGHNESS)) {
	                        prot += modifier.getAmount();
	                    }
	                }
	                if (modifiers != null && modifiers.containsKey(Attribute.GENERIC_MAX_HEALTH)) {
	                    for (AttributeModifier modifier : modifiers.get(Attribute.GENERIC_MAX_HEALTH)) {
	                        prot += modifier.getAmount();
	                    }
	                }
	    		}
	    	}
	    	prot += getEnchantmentBonus(armor);
	    	return prot;
	    }
	    
	    /**
	     * Calculates the overall damage score for a weapon.
	     * It adds the base damage, any attribute modifiers, and enchantment bonuses.
	     * @param weapon The weapon to evaluate.
	     * @return The computed damage score.
	     */
	    public static double getWeaponDamage(ItemStack weapon) {
	        if (weapon == null) return 0;
	        
	        double damage = 0;
	        // 1. Get the base damage based on the weapon’s material.
	        damage += getBaseDamage(weapon.getType());
	        
	        // 2. Add any attack damage attribute modifiers.
	        if (weapon.hasItemMeta()) {
	            ItemMeta meta = weapon.getItemMeta();
	            if (meta != null && meta.hasAttributeModifiers()) {
	                Multimap<Attribute, AttributeModifier> modifiers = meta.getAttributeModifiers();
	                if (modifiers != null && modifiers.containsKey(Attribute.GENERIC_ATTACK_DAMAGE)) {
	                    for (AttributeModifier modifier : modifiers.get(Attribute.GENERIC_ATTACK_DAMAGE)) {
	                        damage += modifier.getAmount();
	                    }
	                }
	            }
	        }
	        
	        // 3. Include bonus damage from enchantments.
	        damage += getEnchantmentBonus(weapon);
	        
	        return damage;
	    }
	    
	    /**
	     * Returns a base damage value for common melee weapons.
	     * Adjust these values if your plugin uses different scaling.
	     * @param material The material type of the weapon.
	     * @return The base damage value.
	     */
	    public static double getBaseDamage(Material material) {
	        switch (material) {
	            case WOODEN_SWORD:   return 4;
	            case STONE_SWORD:    return 5;
	            case IRON_SWORD:     return 6;
	            case GOLDEN_SWORD:   return 4;
	            case DIAMOND_SWORD:  return 7;
	            case NETHERITE_SWORD:return 8;
	            case WOODEN_AXE:     return 3;
	            case STONE_AXE:      return 4;
	            case IRON_AXE:       return 5;
	            case GOLDEN_AXE:     return 3;
	            case DIAMOND_AXE:    return 6;
	            case NETHERITE_AXE:  return 6;
	            default:             return 1;
	        }
	    }
	    
	    public static double getBaseProt(Material material) {
	        String name = material.name().toLowerCase();
	        if (name.contains("leather"))
	        	return 2;
	        if (name.contains("gold"))
	        	return 3;
	        if (name.contains("chainmail"))
	        	return 4;
	        if (name.contains("iron"))
	        	return 5;
	        if (name.contains("diamond"))
	        	return 6;
	        if (name.contains("netherite"))
	        	return 7;
	        return 0;
	    }
	    
	    /**
	     * Calculates bonus damage from enchantments.
	     * In this example, each level of a damage enchantment adds 1 bonus damage.
	     * Adjust the multipliers if needed.
	     * @param weapon The weapon to check for enchantments.
	     * @return The extra damage from enchantments.
	     */
	    public static double getEnchantmentBonus(ItemStack weapon) {
	        double bonus = 0;
	        // Sharpness (DAMAGE_ALL)
	        if (weapon.containsEnchantment(Enchantment.SHARPNESS)) {
	            int level = weapon.getEnchantmentLevel(Enchantment.SHARPNESS);
	            bonus += level; // Example: 1 extra damage per level
	        }
	        // Smite (DAMAGE_UNDEAD)
	        if (weapon.containsEnchantment(Enchantment.SMITE)) {
	            int level = weapon.getEnchantmentLevel(Enchantment.SMITE);
	            bonus += level;
	        }
	        // Bane of Arthropods (DAMAGE_ARTHROPODS)
	        if (weapon.containsEnchantment(Enchantment.BANE_OF_ARTHROPODS)) {
	            int level = weapon.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS);
	            bonus += level;
	        }
	        if (weapon.containsEnchantment(Enchantment.PROTECTION)) {
	        	int level = weapon.getEnchantmentLevel(Enchantment.PROTECTION);
	        	bonus += level;
	        }
	        return bonus;
	    }

}
