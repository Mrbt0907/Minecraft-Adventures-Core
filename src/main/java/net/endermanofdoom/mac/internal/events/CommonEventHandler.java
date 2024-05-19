package net.endermanofdoom.mac.internal.events;

import net.endermanofdoom.mac.events.WorldEvent;
import net.endermanofdoom.mac.util.ReflectionUtil;
import net.endermanofdoom.mac.world.WorldDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class CommonEventHandler
{
	@SubscribeEvent
	public static void onServerTick(ServerTickEvent event)
	{
		if (WorldDataManager.isAvailable())
			WorldDataManager.tick();
	}
	
	@SubscribeEvent
	public static void onWorldLoad(Load event)
	{
		if (!WorldDataManager.isAvailable())
		{
			WorldDataManager.setAvailable();
			MinecraftForge.EVENT_BUS.post(new WorldEvent.Start());
		}
	}
	
	@SubscribeEvent
	public static void onWorldSave(Save event)
	{
		WorldDataManager.save();
	}
	
	@Mod.EventHandler
	public static void onWorldUnload(FMLServerStoppingEvent event)
	{
		MinecraftForge.EVENT_BUS.post(new WorldEvent.Stop());
		WorldDataManager.reset();
	}

	@SubscribeEvent
	public static void onProjectileImpact(ProjectileImpactEvent event)
	{
		Entity victim = event.getRayTraceResult().entityHit;
		if (victim == null) return;
		
		NBTTagCompound nbt = event.getEntity().getEntityData();
		if (nbt.hasKey("multiShot"))
		{
			victim.hurtResistantTime = 0;
			if (victim instanceof EntityLivingBase)
				ReflectionUtil.set(EntityLivingBase.class, (EntityLivingBase) victim, "recentlyHit", "field_70718_bc", 100);
		}
	}
	
	@SubscribeEvent
	public static void onMobSpawnEvent(EntityJoinWorldEvent event)
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
