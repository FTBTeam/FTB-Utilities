package com.feed_the_beast.ftbu.ranks;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.command.ForgeCommand;

/**
 * @author LatvianModder
 */
class CmdForgeOverride extends CommandTreeBase
{
	CmdForgeOverride(ForgeCommand cmd)
	{
		for (ICommand command : cmd.getSubCommands())
		{
			addSubcommand(command);
		}
	}

	@Override
	public String getName()
	{
		return "forge";
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		return true;
	}

	@Override
	public String getUsage(ICommandSender icommandsender)
	{
		return "commands.forge.usage";
	}
}