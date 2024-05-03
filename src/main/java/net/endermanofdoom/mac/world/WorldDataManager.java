package net.endermanofdoom.mac.world;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldDataManager
{
	public static final boolean isRemote = FMLCommonHandler.instance().getEffectiveSide().equals(Side.CLIENT);
	private static final Map<String, WorldData> worldData = new HashMap<String, WorldData>();
	public static long ticks;
	private static boolean isActive;
	
	public static void tick()
	{
		if (!isActive) return;
		
		if (!isRemote)
		{
			worldData.forEach((key, data) -> {
				if (data.networkReady && ticks >= data.networkNextTicks)
				{
					data.networkReady = false;
					data.networkNextTicks = ticks + 10L;
					sync(key);
				}
				data.networkNextTicks++;
			});
		}
		
		ticks++;
	}
	
	public static void save()
	{
		if (isRemote)
		{
			MACCore.error("Cannot save all world data on the client");
			return;
		}
		worldData.forEach((key, data) -> data.save());
	}
	
	public static void sync(String fileName)
	{
		if (exists(fileName))
		{
			if (isRemote)
			{
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("fileName", fileName);
				nbt.setUniqueId("player", net.minecraft.client.Minecraft.getMinecraft().player.getUniqueID());
				MACCore.NETWORK.sendToServer(2, nbt);
			}
			else
			{
				WorldData data = worldData.get(fileName);
				if (data == null)
				{
					MACCore.warn("World data with the name " + fileName + " does not exist. Skipping...");
					return;
				}
				NBTTagCompound nbt = new NBTTagCompound();
				nbt.setString("fileName", fileName);
				nbt.setTag("data", data.build());
				MACCore.NETWORK.sendToClients(2, nbt);
			}
		}
	}
	
	public static void syncToPlayer(String fileName, UUID playerUUID)
	{
		EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUUID(playerUUID);
		if (player == null)
		{
			MACCore.warn("Could not find player with the uuid " + playerUUID + " for world data sync. Skipping...");
			return;
		}
		WorldData data = worldData.get(fileName);
		if (data == null)
		{
			MACCore.warn("World data with the name " + fileName + " does not exist. Skipping...");
			return;
		}
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("fileName", fileName);
		nbt.setTag("data", data.buildAll());
		MACCore.NETWORK.sendToClients(2, nbt, player);
	}
	
	public static boolean exists(String fileName)
	{
		return worldData.containsKey(fileName);
	}
	
	public static void checkAvailability()
	{
		isActive = DimensionManager.getWorld(0) != null;
	}
	
	public static boolean isAvailable()
	{
		return isActive;
	}
	
	public static void reset()
	{
		isActive = false;
		worldData.clear();
	}
	
	@SideOnly(Side.CLIENT)
	public static void onNetworkRecieved(NBTTagCompound nbt)
	{
		String fileName = nbt.getString("fileName");
		WorldData data = worldData.get(fileName);
		if (data == null)
		{
			MACCore.warn("World data with the name " + fileName + " does not exist. Skipping...");
			return;
		}
		data.onNetworkRecieved(nbt.getCompoundTag("data")); 
	}
}
