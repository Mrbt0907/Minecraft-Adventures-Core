package net.endermanofdoom.mac.command;

import java.util.List;

import net.endermanofdoom.mac.MACCore;
import net.endermanofdoom.mac.registry.MACAttributes;
import net.endermanofdoom.mac.util.ReflectionUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandMAC extends CommandBase
{

	@Override
	public String getName()
	{
		return "mac";
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return "commands.mac.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length > 0)
			switch(args[0].toLowerCase())
			{
				case "class":
					if (args.length > 1)
					{
						List<String> entries = ReflectionUtil.viewFields(args[1]);
						String output = "RESULTS FOR CLASS " + args[1];
						for (String entry : entries)
							output += "\n" + entry;
						entries = ReflectionUtil.viewMethods(args[1]);
						for (String entry : entries)
							output += "\n" + entry;
						entries = null;
						MACCore.info(output);
						sender.sendMessage(new TextComponentString("Sent class fields and methods to latest.log"));
					}
					else
						sender.sendMessage(new TextComponentString("/mac class <Class To Get>"));
					break;
				case "reach":
					if (args.length > 1 && sender instanceof EntityPlayerMP)
					{
						double reach = Math.max(Double.parseDouble(args[1]), 0.0D);
						((EntityPlayerMP)sender).getEntityAttribute(MACAttributes.ATTACK_RANGE).setBaseValue(reach);
						sender.sendMessage(new TextComponentString("Set Extra Attack Reach to " + reach + " blocks"));
					}
					else
						sender.sendMessage(new TextComponentString("/mac reach <Extra Attack Reach Distance>"));
					break;
				case "mana":
					if (args.length > 1 && sender instanceof EntityPlayerMP)
					{
						double reach = Math.max(Double.parseDouble(args[1]), 0.0D);
						((EntityPlayerMP)sender).getEntityAttribute(MACAttributes.MAX_MANA).setBaseValue(reach);
						((EntityPlayerMP)sender).getEntityAttribute(MACAttributes.CURRENT_MANA).setBaseValue(reach);
						sender.sendMessage(new TextComponentString("Set Mana to " + reach + " units"));
					}
					else
						sender.sendMessage(new TextComponentString("/mac mana <Extra Mana>"));
					break;
				default:
					sender.sendMessage(new TextComponentString("/mac <class>"));
			}
	}
}
