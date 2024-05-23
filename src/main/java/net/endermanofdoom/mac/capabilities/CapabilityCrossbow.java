package net.endermanofdoom.mac.capabilities;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.endermanofdoom.mac.network.PacketMagazine;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapabilityCrossbow implements IMagazineCapability, ICapabilitySerializable<NBTTagCompound>
{
	@CapabilityInject(CapabilityCrossbow.class)
	public static final Capability<CapabilityCrossbow> INSTANCE = null;
	protected List<ItemStack> ammunition = new LinkedList<ItemStack>();
	protected boolean isLoaded;
	public int maxAmmo;
	public int reloadTime;
	protected boolean changed;
	
	@Override
	public List<ItemStack> getAmmo()
	{
		return new LinkedList<ItemStack>(ammunition);
	}

	@Override
	public boolean isMagazineEmpty()
	{
		return ammunition.isEmpty();
	}
	
	@Override
	public boolean isMagazineFull()
	{
		return ammunition.size() >= maxAmmo;
	}
	
	@Override
	public int getAmmoCount()
	{
		int ammo = 0;
		for (ItemStack stack : ammunition)
			ammo += stack.getCount();
		return ammo;
	}

	@Override
	public void loadMagazine(IInventory inventory, boolean shouldShrink)
	{
		if (inventory.isEmpty()) return;
		int size = inventory.getSizeInventory();
		for (int i = 0; i < size; i++)
			toMagazine(inventory.getStackInSlot(i), shouldShrink);
	}

	@Override
	public void loadMagazine(List<ItemStack> ammunition, boolean shouldShrink)
	{
		if (ammunition.isEmpty()) return;
		ammunition.forEach(stack -> toMagazine(stack, shouldShrink));
	}

	@Override
	public void unloadMagazine()
	{
		ammunition.clear();
		isLoaded = false;
		changed = true;
	}

	@Override
	public void toMagazine(ItemStack stack, boolean shouldShrink)
	{
		toMagazine(ammunition.size(), stack, shouldShrink);
	}

	@Override
	public void toMagazine(int index, ItemStack stack, boolean shouldShrink) {
		if (!(stack.getItem() instanceof ItemArrow) || isMagazineFull()) return;
		int count = Math.min(maxAmmo - getAmmoCount(), stack.getCount());
		ammunition.add(Math.min(index, ammunition.size()), new ItemStack(stack.getItem(), count, stack.getMetadata()));
		if (shouldShrink)
			stack.shrink(count);
		changed = true;
	}
	
	@Override
	public ItemStack fromMagazine()
	{
		Iterator<ItemStack> iterator = ammunition.iterator();
		ItemStack ammo;
		while (iterator.hasNext())
		{
			ammo = iterator.next();
			if (ammo.getItem() instanceof ItemArrow)
			{
				changed = true;
				iterator.remove();
				return ammo;
			}
			else
			{
				changed = true;
				iterator.remove();
			}
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public void markDirty(Entity entity, String inventoryFieldName, String inventoryObfName, int inventoryIndex)
	{
		if (changed)
		{
			changed = false;
			PacketMagazine.sendMagazineInfo("crossbow", entity.getClass().getCanonicalName(), entity.getUniqueID(), inventoryFieldName, inventoryObfName, inventoryIndex, serializeNBT());
		}
	}
	
	public static class Storage implements Capability.IStorage<CapabilityCrossbow>
	{
		@Override
		public NBTBase writeNBT(Capability<CapabilityCrossbow> capability, CapabilityCrossbow instance, EnumFacing side)
		{
			List<ItemStack> stacks = instance.ammunition;
			NBTTagCompound nbtMagazine = new NBTTagCompound();
			NBTTagList magazine = new NBTTagList();
			NBTTagCompound nbt; ItemArrow arrow;
			
			for(ItemStack stack : stacks)
			{
				if (!(stack.getItem() instanceof ItemArrow)) continue;
				arrow = (ItemArrow) stack.getItem();
				nbt = new NBTTagCompound();
				nbt.setString("type", arrow.getRegistryName().toString());
				nbt.setInteger("amount", stack.getCount());
				nbt.setInteger("metadata", stack.getMetadata());
				magazine.appendTag(nbt);
			}
			
			nbtMagazine.setTag("magazine", magazine);
			return nbtMagazine;
		}

		@Override
		public void readNBT(Capability<CapabilityCrossbow> capability, CapabilityCrossbow instance, EnumFacing side, NBTBase nbtData)
		{
			if (!(nbtData instanceof NBTTagCompound)) return;
			NBTTagCompound nbtBase = (NBTTagCompound) nbtData;
			if (!nbtBase.hasKey("magazine")) return;
			List<ItemStack> ammo = new LinkedList<ItemStack>();
			NBTTagList magazine = (NBTTagList) nbtBase.getTag("magazine");
			NBTTagCompound nbt; Item item; int amount;
			for (NBTBase entry : magazine)
			{
				nbt = (NBTTagCompound) entry;
				item = Item.getByNameOrId(nbt.getString("type"));
				amount = nbt.getInteger("amount");
				if (!(item instanceof ItemArrow) || amount < 1) continue;
				ammo.add(new ItemStack(item, amount, nbt.getInteger("metadata")));
			}
			instance.unloadMagazine();
			instance.loadMagazine(ammo, false);
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return capability == INSTANCE;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return capability == INSTANCE ? INSTANCE.cast(this) : null;
	}

	public NBTTagCompound serializeNBT()
	{
		return (NBTTagCompound) INSTANCE.getStorage().writeNBT(INSTANCE, this, null);
	}

	public void deserializeNBT(NBTTagCompound nbtBase)
	{
		INSTANCE.getStorage().readNBT(INSTANCE, this, null, nbtBase);
	}

	
}
