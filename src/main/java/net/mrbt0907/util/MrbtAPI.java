package net.mrbt0907.util;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.mrbt0907.util.network.NetworkReciever;

public class MrbtAPI
{
	public static final boolean ISREMOTE = FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
	public static String MODID;
	public static Object INSTANCE;
	public static NetworkReciever NETWORK;
	public static boolean DEBUGMODE;
	
	public static void initialize(String modid, Object modInstance)
	{
		if (MODID == null)
		{
			MODID = modid;
			INSTANCE = modInstance;
			NETWORK = new NetworkReciever();
		}
	}
	
	public static void preInit(FMLPreInitializationEvent event)
	{
		if (ISREMOTE)
			ClientProxy.preInit(event);
		else
			CommonProxy.preInit(event);
	}
	
	public static void init(FMLInitializationEvent event)
	{
		if (ISREMOTE)
			ClientProxy.init(event);
		else
			CommonProxy.init(event);
	}
	
	public static void postInit(FMLPostInitializationEvent event)
	{
		if (ISREMOTE)
			ClientProxy.postInit(event);
		else
			CommonProxy.postInit(event);
	}
	
	
	
	public static void error(Object message)
	{
			Throwable exception;
			
			if (message instanceof Throwable)
				exception = (Throwable) message;
			else
				exception = new Exception(String.valueOf(message));

			exception.printStackTrace();
	}
	
	public static void fatal(Object message)
	{
		Error error;
		
		if (message instanceof Error)
			error = (Error) message;
		else
			error = new Error(String.valueOf(message));
		
		throw error;
	}
}