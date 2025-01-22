package net.mrbt0907.util.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.mrbt0907.util.MrbtAPI;
import net.mrbt0907.util.dialogue.SubDialogueMessage;
import net.mrbt0907.util.interfaces.INetworkReciever;
import net.mrbt0907.util.internal.CapabilityHandler;
import net.mrbt0907.util.world.WorldDataManager;

public class NetworkReciever implements INetworkReciever
{
	@Override
	public String getID()
	{
		return MrbtAPI.MODID;
	}

	@Override
	public void onClientRecieved(int commandID, NBTTagCompound nbt)
	{
		switch (commandID)
		{
			case 0:
				net.mrbt0907.util.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(nbt));
				break;
			case 1:
				net.mrbt0907.util.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(nbt));
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
		}
	}
}
