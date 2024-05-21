package net.endermanofdoom.mac.interfaces;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public interface ISetBonus
{
	public boolean isFullSet(EntityLivingBase owner, EntityEquipmentSlot slot, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots, ItemStack mainHand, ItemStack offHand);
	public void onEquip(EntityLivingBase owner, ItemStack stack, EntityEquipmentSlot slot);
	public void onUnequip(EntityLivingBase owner, ItemStack stack, EntityEquipmentSlot slot);
	public void onSetEquip(EntityLivingBase owner, ItemStack stack, EntityEquipmentSlot slot);
	public void onSetUnequip(EntityLivingBase owner, ItemStack stack, EntityEquipmentSlot slot);
	public void onSetTick(EntityLivingBase owner, ItemStack stack, EntityEquipmentSlot slot);
	public List<String> addSetInformation();
}
