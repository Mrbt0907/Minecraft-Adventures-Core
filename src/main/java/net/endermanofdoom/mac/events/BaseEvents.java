package net.endermanofdoom.mac.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BaseEvents 
{
	@SubscribeEvent
	public void onMobSpawnEvent(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof EntityItem)
		{
			EntityItem item = (EntityItem)entity;
			
			if (
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.CHAIN_COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.REPEATING_COMMAND_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.BARRIER) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.BEDROCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_PORTAL_FRAME) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_PORTAL) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.END_GATEWAY) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.PORTAL) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.STRUCTURE_BLOCK) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.STRUCTURE_VOID) ||
					item.getItem().getItem() == Item.getItemFromBlock(Blocks.DRAGON_EGG) ||
					item.getItem().getItem() == Items.NETHER_STAR
				)
			{
			item.setEntityInvulnerable(true);
			item.setNoDespawn();
			}
		}
	}
}
