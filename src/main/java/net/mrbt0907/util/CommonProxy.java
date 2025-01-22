package net.mrbt0907.util;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.mrbt0907.util.util.chunk.MobChunkLoader;

public class CommonProxy 
{
	public static void preInit(FMLPreInitializationEvent event)
	{
		MobChunkLoader.init();
	}
	
	public static void init(FMLInitializationEvent event)
	{
		
	}
	
	public static void postInit(FMLPostInitializationEvent event) 
	{
		
	}
}
