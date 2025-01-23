package net.mrbt0907.util.internal.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.mrbt0907.util.ClientProxy;
import net.mrbt0907.util.events.WorldEvent;
import net.mrbt0907.util.world.WorldDataManager;

public class ClientEventHandler
{
	private static final Minecraft MC = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public static void onWorldLeave(WorldEvent.Stop event)
	{
		ClientProxy.DIALOGUE.reset();
	}
	
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		if (MC.world == null && FMLClientHandler.instance().getServer() == null && WorldDataManager.isAvailable())
		{
			MinecraftForge.EVENT_BUS.post(new WorldEvent.Stop());
			WorldDataManager.reset();
		}
		
		WorldDataManager.tick();
	}
}