package net.endermanofdoom.mac.network;

import java.util.UUID;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.nbt.NBTTagCompound;

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
		MACCore.NETWORK.sendToClients(5, nbt);
	}
}
