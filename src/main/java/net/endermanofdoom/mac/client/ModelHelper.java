package net.endermanofdoom.mac.client;

import net.endermanofdoom.mac.client.model.BaseItemModel;
import net.endermanofdoom.mac.client.render.BaseRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ModelHelper
{
	private static BaseRenderer renderer;
	
	public static void render(Item item, BaseItemModel model, String domain, String location)
	{
		renderer = new BaseRenderer();
		item.setTileEntityItemStackRenderer(renderer);
		renderer.render(new ItemStack(item), model, new ResourceLocation(domain, location + ".png"));
	}
}
