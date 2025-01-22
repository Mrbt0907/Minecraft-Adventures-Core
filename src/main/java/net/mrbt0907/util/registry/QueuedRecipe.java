package net.mrbt0907.util.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.mrbt0907.util.MrbtAPI;
import net.mrbt0907.util.internal.enums.EnumIngredientType;
import net.mrbt0907.util.util.StringUtil;

public class QueuedRecipe
{
	public final QueuedIngredient[] ingredients;
	public final String[] pattern;
	public final int length;
	public final String id;
	public boolean shaped;
	protected final EnumIngredientType type;
	protected final Object output;
	protected final int amount;
	protected final int meta;
	private final boolean isValid;
	
	public QueuedRecipe(Object output, int amount, int meta, String id, QueuedIngredient... ingredients)
	{
		this(output, amount, meta, id, null, ingredients);
	}
	
	public QueuedRecipe(Object output, int amount, int meta, String id, String pattern, QueuedIngredient... ingredients)
	{
		type = output instanceof Block ? EnumIngredientType.BLOCK : output instanceof Item ? EnumIngredientType.ITEM : output instanceof ItemStack  ? EnumIngredientType.ITEMSTACK : output instanceof String ? EnumIngredientType.OREDICT : EnumIngredientType.INVALID;
		if (type == EnumIngredientType.INVALID || type == EnumIngredientType.OREDICT)
			MrbtAPI.error("Tried to register a recipe with an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
		this.output = output;
		this.amount = amount;
		this.meta = meta;
		
		QueuedIngredient[] inputs = new QueuedIngredient[Math.min(ingredients.length, 9)];
		for (int i = 0; i < ingredients.length && i < 9; i++)
			if (ingredients[i].isValid())
				inputs[i] = ingredients[i];
		this.ingredients = inputs;
		length = inputs.length;
		if (length <= 0)
			MrbtAPI.error("Tried to register a recipe with that does not have enough valid ingredients \"" + String.valueOf(output) + "\"");

		this.id = id;
		this.shaped = pattern != null;
		if (shaped)
		{
			String[] sections = StringUtil.split(pattern, ",");
			String[] newPattern = new String[Math.min(sections.length, 3)];
			for (int i = 0; i < sections.length && i < 3; i++)
				newPattern[i] = sections[i];
			this.pattern = sections;
		}
		else
			this.pattern = null;
		isValid = length > 0 && type != EnumIngredientType.INVALID && type != EnumIngredientType.OREDICT;
	}
	
	public QueuedIngredient getIngredient(int index)
	{
		return ingredients[index];
	}
	
	public ItemStack build()
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
				MrbtAPI.fatal("Tried to build an oredict as an output for a recipe \"" + output + "\"");
				return null;
			default:
				MrbtAPI.fatal("Tried to build an invalid ingredient type \"" + output.getClass().getCanonicalName() + "\"");
				return null;
		}
	}
	
	public Object[] buildIngredients()
	{
		Object[] output = new Object[pattern.length + ingredients.length * 2];
		Character[] indexes = StringUtil.getUniqueCharacters(String.join("", pattern));
		for (int i = 0; i < pattern.length; i++)
			output[i] = pattern[i];

		
		for (int i = pattern.length, ii = 0; i < pattern.length + ingredients.length * 2 && ii < indexes.length; i += 2, ii++)
		{
			output[i] = indexes[ii];
			output[i + 1] = ingredients[ii].build();
		}
		
		return output;
	}
	
	public Ingredient[] buildShapelessIngredients()
	{
		Ingredient[] ingredients = new Ingredient[this.ingredients.length];
		for (int i = 0; i < this.ingredients.length; i++)
			ingredients[i] = this.ingredients[i].build();
		return ingredients;
	}
	
	public boolean isValid()
	{
		return isValid;
	}
	
	@Override
	public String toString()
	{
		String prefix = shaped ? "Shaped Recipe" : "Shapeless Recipe";
		String suffix = "";
		if (!isValid())
			return prefix + "{invalid}";
		
		String output;
		switch (type)
		{
			case BLOCK:
				output = Item.getItemFromBlock((Block) this.output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			case ITEM:
				output = ((Item) this.output).getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			case ITEMSTACK:
				output = ((ItemStack) this.output).getItem().getRegistryName().toString() + (meta != 0 ? "#" + meta : "") + (amount > 1 ? " * " + amount : ""); break;
			case OREDICT:
				output = "ore{" + this.output + "}"; break;
			default:
				output = "invalid";
		}
		
		for (QueuedIngredient ingredient : ingredients)
			if (suffix.isEmpty())
				suffix = ingredient.toString();
			else
				suffix += ", " + ingredient.toString();
		
		if (shaped)
			return prefix + " {output=" + output +", pattern=\"" + String.join(",", pattern) + "\", inputs=" + suffix + "}";
		else
			return prefix + "{output=" + output + ", inputs=" + suffix + "}";
	}
}
