package net.endermanofdoom.mac.registry;

import java.util.LinkedList;
import java.util.List;
import net.endermanofdoom.mac.MACCore;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class AbstractRecipeRegistry
{
	private final List<QueuedRecipe> recipes = new LinkedList<QueuedRecipe>();
	private final String MODID;
	
	public AbstractRecipeRegistry(String modid)
	{
		MODID = modid;
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	public abstract void init();
	public abstract void register();
	
	@SubscribeEvent
	public void register(RegistryEvent.Register<IRecipe> event)
	{
		MACCore.debug("Registering recipes for mod " + MODID + "...");
		register();
		recipes.forEach(recipe ->
		{
			if (recipe.isValid())
			{
				if (recipe instanceof QueuedSmeltingRecipe)
					GameRegistry.addSmelting(recipe.getIngredient(0).buildStack(), recipe.build(), ((QueuedSmeltingRecipe)recipe).xp);
				else if(recipe.shaped)
					GameRegistry.addShapedRecipe(new ResourceLocation(MODID + ":recipes/" + recipe.id), null, recipe.build(), recipe.buildIngredients());
				else
					GameRegistry.addShapelessRecipe(new ResourceLocation(MODID + ":recipes/" + recipe.id), null, recipe.build(), recipe.buildShapelessIngredients());
			}
		});
		recipes.clear();
	}
	
	public void addShapedRecipe(Object output, String id, String pattern, Object... ingredients)
	{
		addShapedRecipe(true, output, 1, 0, id, pattern, ingredients);
	}
	public void addShapedRecipe(Object output, int amount, String id, String pattern, Object... ingredients)
	{
		addShapedRecipe(true, output, amount, 0, id, pattern, ingredients);
	}
	public void addShapedRecipe(Object output, int amount, int meta, String id, String pattern, Object... ingredients)
	{
		addShapedRecipe(true, output, amount, meta, id, pattern, ingredients);
	}
	public void addShapedRecipe(boolean enabled, Object output, String id, String pattern, Object... ingredients)
	{
		addShapedRecipe(true, output, 1, 0, id, pattern, ingredients);
	}
	public void addShapedRecipe(boolean enabled, Object output, int amount, String id, String pattern, Object... ingredients)
	{
		addShapedRecipe(true, output, 1, 0, id, pattern, ingredients);
	}
	public void addShapedRecipe(boolean enabled, Object output, int amount, int meta, String id, String pattern, Object... ingredients)
	{
		if (!enabled)
			return;
		if (pattern == null)
		{
			MACCore.error("Tried to register a shaped recipe without a pattern");
			return;
		}
		registerRecipe(output, amount, meta, id, pattern, ingredients);
	}
	
	public void addShapelessRecipe(Object output, String id, Object... ingredients)
	{
		addShapelessRecipe(true, output, 1, 0, id, ingredients);
	}
	public void addShapelessRecipe(Object output, int amount, String id, Object... ingredients)
	{
		addShapelessRecipe(true, output, amount, 0, id, ingredients);
	}
	public void addShapelessRecipe(Object output, int amount, int meta, String id, Object... ingredients)
	{
		addShapelessRecipe(true, output, amount, meta, id, ingredients);
		
	}
	public void addShapelessRecipe(boolean enabled, Object output, String id, Object... ingredients)
	{
		addShapelessRecipe(enabled, output, 1, 0, id, ingredients);
		
	}
	public void addShapelessRecipe(boolean enabled, Object output, int amount, String id, Object... ingredients)
	{
		addShapelessRecipe(enabled, output, amount, 0, id, ingredients);
	}
	public void addShapelessRecipe(boolean enabled, Object output, int amount, int meta, String id, Object... ingredients)
	{
		if (!enabled)
			return;
		registerRecipe(output, amount, meta, id, null, ingredients);
	}
	
	public void addSmeltingRecipe(Object output, Object input)
	{
		addSmeltingRecipe(true, output, 0, 0.0F, input);
	}
	public void addSmeltingRecipe(Object output, int meta, Object input)
	{
		addSmeltingRecipe(true, output, meta, 0.0F, input);
	}
	public void addSmeltingRecipe(Object output, int meta, float xp, Object input)
	{
		addSmeltingRecipe(true, output, meta, xp, input);
	}
	public void addSmeltingRecipe(boolean enabled, Object output, Object input)
	{
		addSmeltingRecipe(enabled, output, 0, 0.0F, input);
	}
	public void addSmeltingRecipe(boolean enabled, Object output, int meta, Object input)
	{
		addSmeltingRecipe(enabled, output, meta, 0.0F, input);
	}
	public void addSmeltingRecipe(boolean enabled, Object output, int meta, float xp, Object input)
	{
		QueuedIngredient ingredient;
		if (input instanceof QueuedIngredient)
			ingredient = (QueuedIngredient) input;
		else
			ingredient = new QueuedIngredient(input);
		QueuedSmeltingRecipe recipe = new QueuedSmeltingRecipe(output, meta, xp, ingredient);
		if (recipe.isValid())
		{
			recipes.add(recipe);
			MACCore.debug("Registered smelting recipe " + recipe.toString());
		}
	}
	
	private void registerRecipe(Object output, int amount, int meta, String id, String pattern, Object... ingredients)
	{
		QueuedIngredient[] inputs = new QueuedIngredient[Math.min(ingredients.length, 9)];
		Object input;
		QueuedIngredient ingredient;
		for (int i = 0, ii = 0; i < ingredients.length && i < 9; i++)
		{
			input = ingredients[i];
			if (input instanceof QueuedIngredient)
				ingredient = (QueuedIngredient) input;
			else
				ingredient = new QueuedIngredient(input);
			
			if (ingredient.isValid())
			{
				inputs[ii] = ingredient;
				ii++;
			}
		}
		QueuedRecipe recipe = new QueuedRecipe(output, amount, meta, id, pattern, inputs);
		if (recipe.isValid())
		{
			recipes.add(recipe);
			MACCore.debug("Registered recipe " + recipe.toString());
		}
	}
}