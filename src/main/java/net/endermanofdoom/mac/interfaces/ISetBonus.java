package net.endermanofdoom.mac.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public interface ISetBonus
{
	public boolean isFullSet(ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack mainHand, ItemStack offHand);
	public void onEquip(Entity entity, ItemStack stack, EntityEquipmentSlot slot);
	public void onUnequip(Entity entity, ItemStack stack, EntityEquipmentSlot slot);
	public void onEquipFull(Entity entity, ItemStack stack, EntityEquipmentSlot slot);
	public void onUnequipFull(Entity entity, ItemStack stack, EntityEquipmentSlot slot);
	public void onFullSet();
}
