package net.endermanofdoom.mac.capabilities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IMagazineCapability
{
	public boolean isMagazineEmpty();
	public boolean isMagazineFull();
	public List<ItemStack> getAmmo();
	public int getAmmoCount();
	public void loadMagazine(IInventory inventory, boolean shouldShrink);
	public void loadMagazine(List<ItemStack> ammunition, boolean shouldShrink);
	public void unloadMagazine();
	public void toMagazine(ItemStack stack, boolean shouldShrink);
	public ItemStack fromMagazine();
	public default void markDirty(Entity entity, String inventoryFieldName, int inventoryIndex) {markDirty(entity, inventoryFieldName, inventoryFieldName, inventoryIndex);}
	public void markDirty(Entity entity, String inventoryFieldName, String inventoryObfName, int inventoryIndex);
}
