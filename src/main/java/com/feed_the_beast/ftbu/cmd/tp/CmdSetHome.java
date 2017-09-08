package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdSetHome extends CmdBase
{
	public CmdSetHome()
	{
		super("sethome", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibAPI.API.getUniverse().getPlayer(sender)).listHomes());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
		FTBUPlayerData data = FTBUPlayerData.get(getForgePlayer(ep));

		if (args.length == 0)
		{
			args = new String[] {"home"};
		}

		args[0] = args[0].toLowerCase();

		int maxHomes = FTBUtilitiesAPI.API.getRankConfig(ep, FTBUPermissions.HOMES_MAX).getInt();

		if (maxHomes <= 0 || data.homesSize() >= maxHomes)
		{
			if (maxHomes == 0 || data.getHome(args[0]) == null)
			{
				throw FTBULang.HOME_LIMIT.commandError();
			}
		}

		data.setHome(args[0], new BlockDimPos(ep));
		FTBULang.HOME_SET.printChat(sender, args[0]);
	}
}