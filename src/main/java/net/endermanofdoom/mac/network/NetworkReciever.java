package net.endermanofdoom.mac.network;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.dialogue.SubDialogueMessage;
import net.endermanofdoom.mac.interfaces.INetworkReciever;
import net.minecraft.nbt.NBTTagCompound;

public class NetworkReciever implements INetworkReciever
{
	@Override
	public String getID()
	{
		return MACCore.MODID;
	}

	@Override
	public void onClientRecieved(int commandID, NBTTagCompound nbt)
	{
		switch (commandID)
		{
			case 0:
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(nbt));
				break;
			case 1:
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(nbt));
				break;
			default:
				MACCore.warn("NetworkManager has recieved an unknown network message from the server with id of " + commandID + ". Skipping...");
		}
	}

	@Override
	public void onServerRecieved(int commandID, NBTTagCompound nbt)
	{
		switch (commandID)
		{
			default:
				MACCore.warn("NetworkManager has recieved an unknown network message from a client with id of " + commandID + ". Skipping...");
		}
	}
}
