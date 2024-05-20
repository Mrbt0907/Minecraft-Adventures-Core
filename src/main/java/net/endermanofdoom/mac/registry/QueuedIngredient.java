package net.endermanofdoom.mac.registry;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.internal.enums.EnumIngredientType;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;

public class QueuedIngredient
{
	private final EnumIngredientType type;
	private final Object output;
	private final int amount;
	private final int meta;
	
	public QueuedIngredient(Object output)
	{
		int amount = 1;
		int meta = 0;
		type = output instanceof Block ? EnumIngredientType.BLOCK : output instanceof Item ? EnumIngredientType.ITEM : output instanceof ItemStack  ? EnumIngredientType.ITEMSTACK : output instanceof String ? EnumIngredientType.OREDICT : EnumIngredientType.INVALID;
		if (type == EnumIngredientType.INVALID)
			MACCore.error("Tried to register an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
		else if (type == EnumIngredientType.ITEMSTACK)
		{
			amount = ((ItemStack)output).getCount();
			meta = ((ItemStack)output).getMetadata();
		}
		this.output = output;
		this.amount = amount;
		this.meta = meta;
	}
	
	public QueuedIngredient(Object output, int amount)
	{
		this(output, amount, 0);
	}
	
	public QueuedIngredient(Object output, int amount, int meta)
	{
		type = output instanceof Block ? EnumIngredientType.BLOCK : output instanceof Item ? EnumIngredientType.ITEM : output instanceof ItemStack  ? EnumIngredientType.ITEMSTACK : output instanceof String ? EnumIngredientType.OREDICT : EnumIngredientType.INVALID;
		if (type == EnumIngredientType.INVALID)
			MACCore.error("Tried to register an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
		this.output = output;
		this.amount = amount;
		this.meta = meta;
	}
	
	public boolean isValid()
	{
		return type != EnumIngredientType.INVALID;
	}
	
	public Ingredient build()
	{
		switch (type)
		{
			case BLOCK:
				return CraftingHelper.getIngredient(new ItemStack(Item.getItemFromBlock((Block) output), amount, meta));
			case ITEM:
				return CraftingHelper.getIngredient(new ItemStack((Item) output, amount, meta));
			case ITEMSTACK:
				return CraftingHelper.getIngredient((ItemStack) output);
			case OREDICT:
				return CraftingHelper.getIngredient((String) output);
			default:
				MACCore.fatal("Tried to build an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
				return null;
		}
	}
	
	public ItemStack buildStack()
	{
		switch (type)
		{
			case BLOCK:
				return new ItemStack(Item.getItemFromBlock((Block) output), amount, meta);
			case ITEM:
				return new ItemStack((Item) output, amount, meta);
			case ITEMSTACK:
				return (ItemStack) output;
			case OREDICT:
				return null;
			default:
				MACCore.fatal("Tried to build an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
				return null;
		}
	}
	
	@Override
	public String toString()
	{
		switch (type)
		{
			case BLOCK:
				return Item.getItemFromBlock((Block) output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : "");
			case ITEM:
				return ((Item) output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : "");
			case ITEMSTACK:
				return ((ItemStack) output).getItem().getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : "");
			case OREDICT:
				return "ore{" + output + "}";
			default:
				return "invalid";
		}
	}
}
