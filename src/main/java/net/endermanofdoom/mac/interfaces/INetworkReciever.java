package net.endermanofdoom.mac.interfaces;

import net.endermanofdoom.mac.network.NetworkHandler;
import net.minecraft.nbt.NBTTagCompound;

public interface INetworkReciever
{
	public String getID();
	public void onClientRecieved(int commandID, NBTTagCompound nbt);
	public void onServerRecieved(int commandID, NBTTagCompound nbt);
	public default boolean sendToClients(int commandID, NBTTagCompound nbt, Object... targets)
	{
		return NetworkHandler.sendToClients(getID(), commandID, nbt, targets);
	}
	public default boolean sendToServer(int commandID, NBTTagCompound nbt)
	{
		return NetworkHandler.sendToServer(getID(), commandID, nbt);
	}
}
