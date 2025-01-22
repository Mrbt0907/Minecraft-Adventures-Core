package net.mrbt0907.util.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.mrbt0907.util.internal.enums.EnumIngredientType;

public class QueuedSmeltingRecipe extends QueuedRecipe
{
	public final float xp;
	private final boolean isValid;
	
	public QueuedSmeltingRecipe(Object output, int meta, float xp, QueuedIngredient ingredient)
	{
		super(output, 1, meta, null, new QueuedIngredient[] {ingredient});
		this.xp = xp;
		isValid = !type.equals(EnumIngredientType.OREDICT) && !type.equals(EnumIngredientType.INVALID);
	}
	
	@Override
	public boolean isValid()
	{
		return isValid && super.isValid();
	}
	
	@Override
	public String toString()
	{
		if (!isValid())
			return "Smelting Recipe {invalid}";
		
		String output;
		switch (type)
		{
			case BLOCK:
				output = Item.getItemFromBlock((Block) this.output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			case ITEM:
				output = ((Item) this.output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			case ITEMSTACK:
				output = ((ItemStack) this.output).getItem().getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			default:
				output = "invalid";
		}
		
		return "Smelting Recipe {output=" + output +", input=" + getIngredient(0).toString() + ", xp=" + xp + "}";
	}
}
