package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.cmd.CommandMirror;
import com.feed_the_beast.ftblib.lib.cmd.ICommandWithCustomPermission;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * @author LatvianModder
 */
public class CommandOverride extends CommandMirror
{
	public CommandOverride(ICommand c)
	{
		super(c);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		if (sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = ((EntityPlayerMP) sender);
			Event.Result result = Ranks.INSTANCE.getRank(player.mcServer, player.getGameProfile()).hasPermission(ICommandWithCustomPermission.getPermissionNode(this));

			if (result != Event.Result.DEFAULT)
			{
				return result == Event.Result.ALLOW;
			}
		}

		return mirrored.checkPermission(server, sender);
	}
}