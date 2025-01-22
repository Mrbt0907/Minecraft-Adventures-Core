package net.mrbt0907.util.network;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.mrbt0907.util.MrbtAPI;

public class PacketMagazine 
{
	public static void sendMagazineInfo(String capability, String entityClass, UUID entityUUID, String fieldName, String obfName, int inventoryIndex, NBTTagCompound nbtMagazine)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("capability", capability);
		nbt.setString("entityClass", entityClass);
		nbt.setUniqueId("entityUUID", entityUUID);
		nbt.setString("fieldName", fieldName);
		nbt.setString("obfName", obfName);
		nbt.setInteger("inventoryIndex", inventoryIndex);
		nbt.setTag("magazine", nbtMagazine);
		MrbtAPI.NETWORK.sendToClients(5, nbt);
	}
}
