package net.mrbt0907.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.mrbt0907.util.client.BossBarManager;
import net.mrbt0907.util.client.gui.GuiBossBar;
import net.mrbt0907.util.client.gui.GuiDialogue;
import net.mrbt0907.util.internal.events.ClientEventHandler;

public class ClientProxy extends CommonProxy
{
	public static final GuiDialogue DIALOGUE = new GuiDialogue();
	public static final GuiBossBar BOSSBARS = new GuiBossBar();
	
	public static void preInit(FMLPreInitializationEvent event)
	{
		CommonProxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		MinecraftForge.EVENT_BUS.register(BossBarManager.class);
		MinecraftForge.EVENT_BUS.register(DIALOGUE);
	}

	public static void init(FMLInitializationEvent event)
	{
		CommonProxy.init(event);
	}
	
	public static void postInit(FMLPostInitializationEvent event)
	{
		CommonProxy.postInit(event);
	}
}
