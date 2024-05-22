package net.endermanofdoom.mac.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.endermanofdoom.mac.interfaces.ISetBonus;
import net.endermanofdoom.mac.registry.MACAttributes;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

public abstract class ItemSwordEX extends ItemSword implements ISetBonus
{
	public final double attackReach;
	public ItemSwordEX(ToolMaterial material, double extendedAttackReach)
	{
		super(material);
		attackReach = extendedAttackReach;
	}

	@Override
	public boolean onEntitySwing(EntityLivingBase player, ItemStack stack)
    {
		return super.onEntitySwing(player, stack);
    }
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
    {
		Multimap<String, AttributeModifier> modifiers = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
			modifiers.put(MACAttributes.ATTACK_RANGE.getName(), new AttributeModifier(MACAttributes.ATTACK_RANGE_UUID, "Weapon modifier", attackReach, 0));
		return modifiers;
    }
}
