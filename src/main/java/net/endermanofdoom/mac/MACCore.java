package net.endermanofdoom.mac;

import org.apache.logging.log4j.Logger;

import net.endermanofdoom.mac.command.CommandMAC;
import net.endermanofdoom.mac.config.ConfigCore;
import net.endermanofdoom.mac.internal.events.CommonEventHandler;
import net.endermanofdoom.mac.network.NetworkHandler;
import net.endermanofdoom.mac.network.NetworkReciever;
import net.endermanofdoom.mac.util.ReflectionUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=MACCore.MODID, name=MACCore.MODNAME, version=MACCore.VERSION, acceptedMinecraftVersions="[1.12.2]")
public class MACCore 
{
	public static final String MODNAME = "Minecraft Adventures Core";
	public static final String MODID = "mac";
	public static final String VERSION = "2.6";
	public static final String CLIENT = "net.endermanofdoom.mac.ClientProxy";
	public static final String SERVER = "net.endermanofdoom.mac.CommonProxy";
	
	@SidedProxy(clientSide=CLIENT, serverSide=SERVER)
	public static CommonProxy proxy;
	@Mod.Instance
	public static MACCore instance;
	public static final NetworkReciever NETWORK = new NetworkReciever();
	public static Logger logger;
	public static boolean isRemote = FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e)
	{
		logger = e.getModLog();
		ConfigManager.sync(MODID, Type.INSTANCE);
		info(MODNAME +  " is coming alive!");
		debug("Initializing network handler...");
		NetworkHandler.preInit();
		NetworkHandler.register(NETWORK);
		debug("Registering events...");
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(CommonEventHandler.class);
		debug("Overwriting fields...");
		ReflectionUtil.set(RangedAttribute.class, (RangedAttribute)SharedMonsterAttributes.MAX_HEALTH, "maximumValue", "field_111118_b", Integer.MAX_VALUE);
		ReflectionUtil.set(RangedAttribute.class, (RangedAttribute)SharedMonsterAttributes.ATTACK_DAMAGE, "maximumValue", "field_111118_b", Integer.MAX_VALUE);
		proxy.preInit(e);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		proxy.init(e);
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e)
	{
		proxy.postInit(e);
		logger.info(MODNAME +  " is ready to go!");
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent event)
	{
		if (event.getModID().equals(MODID))
			ConfigManager.sync(MODID, Type.INSTANCE);
	}
	
	@EventHandler
	public void onServerStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandMAC());
	}
	
	public static boolean checkFriendlyFire(EntityPlayer player, Entity entity, boolean refinedCheck)
	{
		if (refinedCheck)
			return entity != null && player != null && (entity instanceof EntityLivingBase && (player.isOnSameTeam((EntityLivingBase)entity) || (entity instanceof EntityTameable && (((EntityTameable)entity).getOwner() != null && (((EntityTameable)entity).getOwner().isEntityEqual(player) || player.isOnSameTeam(((EntityTameable)entity).getOwner())) || ((EntityTameable)entity).isTamed())) || (entity instanceof EntityIronGolem && ((EntityIronGolem)entity).isPlayerCreated()) || entity instanceof EntitySnowman || entity instanceof EntityVillager || (entity instanceof EntityHorse && ((EntityHorse)entity).isTame())));
		else
			return entity != null && player != null && (entity instanceof EntityLivingBase && (player.isOnSameTeam((EntityLivingBase)entity) || entity instanceof EntityTameable || entity instanceof EntityGolem || entity instanceof EntityVillager || entity instanceof EntityHorse || entity instanceof EntityAnimal));
	}
	
	public static void info(Object message)
	{
		logger.info(message);
	}
	
	public static void debug(Object message)
	{
		if (ConfigCore.debug_mode)
			logger.info("[DEBUG] " + message);
	}
	
	public static void warn(Object message)
	{
		if (ConfigCore.debug_mode)
			logger.warn(message);
	}

	public static void error(Object message)
	{
		if (ConfigCore.debug_mode)
		{
			Throwable exception;
			
				if (message instanceof Throwable)
					exception = (Throwable) message;
				else
					exception = new Exception(String.valueOf(message));

				exception.printStackTrace();
		}
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
