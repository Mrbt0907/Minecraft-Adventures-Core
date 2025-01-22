package net.mrbt0907.util.dialogue;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.mrbt0907.util.MrbtAPI;

public class DialogueManager
{
	
	public static void sendSubDialogue(String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		sendSubDialogue((World) null, displayName, content, sprite, forced, nameColor, backgroundColor);
	}
	
	public static void sendSubDialogue(EntityPlayerMP player, String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		if (forced)
			if (MrbtAPI.ISREMOTE)
				net.mrbt0907.util.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else
			{
				MrbtAPI.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), player);
			}
		else
			if (MrbtAPI.ISREMOTE)
				net.mrbt0907.util.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else
			{
				MrbtAPI.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), player);
			}
	}
	
	public static void sendSubDialogue(World world, String displayName, String content, ResourceLocation sprite, boolean forced, int nameColor, int backgroundColor)
	{
		if (forced)
			if (MrbtAPI.ISREMOTE)
				net.mrbt0907.util.ClientProxy.DIALOGUE.forceAddSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else if (world != null)
			{
				MrbtAPI.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), world);
			}
			else
			{
				MrbtAPI.NETWORK.sendToClients(1, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()));
			}
		else
			if (MrbtAPI.ISREMOTE)
				net.mrbt0907.util.ClientProxy.DIALOGUE.addSubDialogue(new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor));
			else if (world != null)
			{
				MrbtAPI.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()), world);
			}
			else
			{
				MrbtAPI.NETWORK.sendToClients(0, new SubDialogueMessage(displayName, content, sprite, nameColor, backgroundColor).writeNBT(new NBTTagCompound()));
			}
	}
}
