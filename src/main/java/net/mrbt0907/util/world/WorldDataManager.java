package net.mrbt0907.util.world;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.mrbt0907.util.MrbtAPI;

public class WorldDataManager
{
	private static final FMLCommonHandler HANDLER = FMLCommonHandler.instance();
	private static final Map<String, WorldData> worldData = new HashMap<String, WorldData>();
	public static long ticks, tickDelay;
	private static boolean isActive;
	
	public static void tick()
	{
		if (!isActive)
		{
			tickDelay = ticks + 1L;
			return;
		}
		if (!isRemote())
		{
			worldData.forEach((key, data) -> {
				if (data.networkReady && ticks >= data.networkNextTicks)
				{
					data.networkReady = false;
					data.networkNextTicks = ticks + 10L;
					sync(key);
				}
			});
		}
		else
		{
			if (tickDelay == ticks)
				worldData.forEach((key, data) -> {
					if (data.networkReady)
					{
						data.networkReady = false;
						sync(key);
					}
				});
		}
		ticks++;
	}
	
	public static void addWorldData(WorldData data)
	{
		if (exists(data.fileName))
		{
			MrbtAPI.error("Attempted to register duplicate world data for data " + data.fileName);
			return;
		}
		worldData.put(data.fileName, data);
	}
	
	public static void save()
	{
		if (isRemote())
		{
			MrbtAPI.error("Cannot save all world data on the client");
			return;
		}
		worldData.forEach((key, data) -> data.save());
	}
	
	public static void sync(String fileName)
	{
		if (exists(fileName))
		{
			if (isRemote())
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("fileName", fileName);
				MrbtAPI.NETWORK.sendToServer(2, nbt);
			}
			else
			{
				WorldData data = worldData.get(fileName);
				if (data == null)
				{
					return;
				}
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("fileName", fileName);
				nbt.setTag("data", data.build());
				MrbtAPI.NETWORK.sendToClients(3, nbt);
			}
		}
	}
	
	public static void syncToPlayer(String fileName, EntityPlayerMP player)
	{
		WorldData data = worldData.get(fileName);
		if (data == null)
		{
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("fileName", fileName);
		nbt.setTag("data", data.buildAll());
		MrbtAPI.NETWORK.sendToClients(2, nbt, player);
	}
	
	public static boolean exists(String fileName)
	{
		return worldData.containsKey(fileName);
	}
	
	public static void setAvailable()
	{
		isActive = true;
	}
	
	public static boolean isAvailable()
	{
		return isActive;
	}
	
	public static void reset()
	{
		worldData.clear();
		isActive = false;
	}
	
	public static boolean isRemote()
	{
		return HANDLER.getMinecraftServerInstance() == null || HANDLER.getEffectiveSide().equals(Side.CLIENT) && !HANDLER.getMinecraftServerInstance().isSinglePlayer();
	}
	
	@SideOnly(Side.CLIENT)
	public static void onNetworkRecieved(NBTTagCompound nbt)
	{
		String fileName = nbt.getString("fileName");
		WorldData data = worldData.get(fileName);
		if (data == null)
		{
			return;
		}
		data.onNetworkRecieved(nbt.getCompoundTag("data")); 
	}
}
