package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.util.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CmdSetWarp extends CmdBase
{
	public CmdSetWarp()
	{
		super("setwarp", Level.OP);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(args, 1, "<warp> [x] [y] [z]");
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
		BlockPos c;

		args[0] = args[0].toLowerCase();

		if (args.length >= 4)
		{
			int x = parseInt(args[1]);
			int y = parseInt(args[2]);
			int z = parseInt(args[3]);
			c = new BlockPos(x, y, z);
		}
		else
		{
			c = ep.getPosition();
		}

		FTBUUniverseData.WARPS.set(args[0], new BlockDimPos(c, ep.dimension));
		FTBULang.WARP_SET.sendMessage(sender, args[0]);
	}
}