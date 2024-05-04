package net.endermanofdoom.mac.network;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.interfaces.INetworkReciever;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
public class PacketNBT implements IMessage, IMessageHandler<PacketNBT, IMessage>
{
	private static final List<INetworkReciever> RECIEVERS = new ArrayList<INetworkReciever>();
	private String recieverID; 
	private int commandID;
	private NBTTagCompound nbt;
	
	public PacketNBT() {}
	
	public PacketNBT(String id, int index, NBTTagCompound nbt)
	{
		this.recieverID = id;
		this.commandID = index;
		this.nbt = nbt;
	}

	public static void register(@Nonnull INetworkReciever reciever)
	{
		if (reciever != null)
		{
			if (!RECIEVERS.contains(reciever))
			{
				if (reciever.getID() != null)
				{
					RECIEVERS.add(reciever);
					MACCore.debug("Registered instance of reciever " + reciever.getID());
				}
				else
					MACCore.error("Tried to register a network reciever that has a null id. Skipping...");
			}
			else
				MACCore.error("Tried to register a network reciever that was already registered. Skipping...");
		}
		else
			MACCore.error("Tried to register a network reciever that was null. Skipping...");
	}
	
	@Override
	public void fromBytes(ByteBuf buffer)
	{
		recieverID = ByteBufUtils.readUTF8String(buffer);
		commandID = buffer.readInt();
		nbt = ByteBufUtils.readTag(buffer);
	}

	@Override
	public void toBytes(ByteBuf buffer)
	{
		ByteBufUtils.writeUTF8String(buffer, recieverID);
		buffer.writeInt(commandID);
		ByteBufUtils.writeTag(buffer, nbt);
	}

	@Override
	public PacketNBT onMessage(PacketNBT message, MessageContext ctx)
	{
		if (ctx.side.isClient())
			return onClientMessage(message, ctx);
		else
			return onServerMessage(message, ctx);
	}

	@SideOnly(Side.CLIENT)
	protected PacketNBT onClientMessage(PacketNBT message, MessageContext ctx)
	{
		Minecraft.getMinecraft().addScheduledTask(() ->
		{
			RECIEVERS.forEach(reciever ->
			{
				if (reciever.getID().equals(message.recieverID))
					try {reciever.onClientRecieved(message.commandID, message.nbt);}
					catch (Exception e) {MACCore.error(e);}
			});
		});
		return null;
	}

	protected PacketNBT onServerMessage(PacketNBT message, MessageContext ctx)
	{
		ctx.getServerHandler().player.mcServer.addScheduledTask(() ->
		{
			RECIEVERS.forEach(reciever ->
			{
				if (reciever.getID().equals(message.recieverID))
					try {reciever.onServerRecieved(message.commandID, message.nbt, ctx.getServerHandler().player);}
					catch (Exception e) {MACCore.error(e);}
			});
		});
		return null;
	}
}


