package net.endermanofdoom.mac.network;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.dialogue.SubDialogueMessage;
import net.endermanofdoom.mac.interfaces.INetworkReciever;
import net.endermanofdoom.mac.internal.CapabilityHandler;
import net.endermanofdoom.mac.internal.ExtendedReachHandler;
import net.endermanofdoom.mac.world.WorldDataManager;
import net.minecraft.entity.player.EntityPlayerMP;
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
			case 2:
				WorldDataManager.onNetworkRecieved(nbt);
				break;
			case 3:
				WorldDataManager.onNetworkRecieved(nbt);
				break;
			case 5:
				CapabilityHandler.onNetworkRecieve(nbt.getString("capability"), nbt.getString("entityClass"), nbt.getUniqueId("entityUUID"), nbt.getString("fieldName"), nbt.getString("obfName"), nbt.getInteger("inventoryIndex"), (NBTTagCompound) nbt.getTag("magazine"));
				break;
			default:
				MACCore.warn("NetworkManager has recieved an unknown network message from the server with id of " + commandID + ". Skipping...");
		}
	}

	@Override
	public void onServerRecieved(int commandID, NBTTagCompound nbt, EntityPlayerMP player)
	{
		switch (commandID)
		{
			case 2:
				WorldDataManager.syncToPlayer(nbt.getString("fileName"), player);
				break;
			case 3:
				break;
			case 4:
				ExtendedReachHandler.onAttack(player, nbt.getUniqueId("entityUUID"), nbt.hasKey("partIndex") ? nbt.getInteger("partIndex") : -1);
				break;
			default:
				MACCore.warn("NetworkManager has recieved an unknown network message from a client with id of " + commandID + ". Skipping...");
		}
	}
}
