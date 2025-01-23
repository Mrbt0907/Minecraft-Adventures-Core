package net.mrbt0907.util.internal.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.mrbt0907.util.events.WorldEvent;
import net.mrbt0907.util.mixin.CameraHandler;
import net.mrbt0907.util.world.WorldDataManager;

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
				((EntityLivingBase)victim).recentlyHit = 100;
		}
		
		CameraHandler.shakeCamera(1.0F, true);
	}
}
