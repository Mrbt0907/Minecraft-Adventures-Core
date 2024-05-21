package net.endermanofdoom.mac;

import net.endermanofdoom.mac.client.BossBarManager;
import net.endermanofdoom.mac.client.gui.GuiBossBar;
import net.endermanofdoom.mac.client.gui.GuiDialogue;
import net.endermanofdoom.mac.internal.client.EntityRendererEX;
import net.endermanofdoom.mac.internal.events.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	public static final GuiDialogue DIALOGUE = new GuiDialogue();
	public static final GuiBossBar BOSSBARS = new GuiBossBar();
	
	@Override
	public void preInit(FMLPreInitializationEvent e)
	{
		super.preInit(e);
		MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
		MinecraftForge.EVENT_BUS.register(BossBarManager.class);
		MinecraftForge.EVENT_BUS.register(DIALOGUE);
	}

	@Override
	public void init(FMLInitializationEvent e)
	{
		super.init(e);
	}
	
	@Override
	public void postInit(FMLPostInitializationEvent e)
	{
		super.postInit(e);
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.entityRenderer.getClass().equals(EntityRenderer.class))
			mc.entityRenderer = new EntityRendererEX(mc, mc.getResourceManager());
	}
}
