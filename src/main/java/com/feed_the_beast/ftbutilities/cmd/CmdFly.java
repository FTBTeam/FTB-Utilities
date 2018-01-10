package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftbutilities.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdFly extends CmdBase
{
	public CmdFly()
	{
		super("fly", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getSelfOrOther(sender, args, 0);
		FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(player));
		data.fly = !data.fly;
		player.capabilities.allowFlying = data.fly;

		if (!data.fly)
		{
			player.capabilities.isFlying = false;
		}

		player.sendPlayerAbilities();
	}
}