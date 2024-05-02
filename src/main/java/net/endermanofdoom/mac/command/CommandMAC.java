package net.endermanofdoom.mac.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
		
	}
}
