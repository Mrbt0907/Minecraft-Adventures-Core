package net.endermanofdoom.mac.dialogue;

import net.endermanofdoom.mac.MACCore;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class DialogueManager
{
	
	public static void sendSubDialogue(String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		sendSubDialogue((World) null, displayName, content, sprite, forced, nameColor, backgroundColor);
	}
	
	public static void sendSubDialogue(EntityPlayerMP player, String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		if (forced)
			if (MACCore.isRemote)
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else
			{
				MACCore.debug("Sending forced sub dialogue to player " + player.getName());
				MACCore.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), player);
			}
		else
			if (MACCore.isRemote)
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else
			{
				MACCore.debug("Sending sub dialogue to player " + player.getName());
				MACCore.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), player);
			}
	}
	
	public static void sendSubDialogue(World world, String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		if (forced)
			if (MACCore.isRemote)
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else if (world != null)
			{
				MACCore.debug("Sending forced sub dialogue to all players in dimension " + world.provider.getDimension());
				MACCore.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), world);
			}
			else
			{
				MACCore.debug("Sending forced sub dialogue to all players in the server");
				MACCore.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()));
			}
		else
			if (MACCore.isRemote)
				net.endermanofdoom.mac.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else if (world != null)
			{
				MACCore.debug("Sending sub dialogue to all players in dimension " + world.provider.getDimension());
				MACCore.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), world);
			}
			else
			{
				MACCore.debug("Sending sub dialogue to all players in the server");
				MACCore.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()));
			}
	}
}
