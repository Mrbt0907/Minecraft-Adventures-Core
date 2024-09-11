package net.endermanofdoom.mac;

import net.endermanofdoom.mac.util.chunk.MobChunkLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy 
{
	public void preInit(FMLPreInitializationEvent e)
	{
		MobChunkLoader.init();
	}
	
	public void init(FMLInitializationEvent e)
	{
		
	}
	
	public void postInit(FMLPostInitializationEvent e) 
	{
		
	}
}
