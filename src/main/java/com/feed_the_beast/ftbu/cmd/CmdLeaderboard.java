package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBUCommon;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.net.MessageSendLeaderboard;
import com.feed_the_beast.ftbu.net.MessageSendLeaderboardList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class CmdLeaderboard extends CmdBase
{
	public CmdLeaderboard()
	{
		super("leaderboards", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUCommon.LEADERBOARDS.getKeys());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			for (Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
			{
				sender.sendMessage(new TextComponentString(leaderboard.getRegistryName().toString()).appendText(": ").appendSibling(leaderboard.getTitle()));
			}
		}
		else if (args[0].equals("gui"))
		{
			EntityPlayerMP player = getCommandSenderAsPlayer(sender);

			if (args.length == 1)
			{
				Map<ResourceLocation, ITextComponent> map = new LinkedHashMap<>();

				for (Leaderboard leaderboard : FTBUCommon.LEADERBOARDS)
				{
					map.put(leaderboard.getRegistryName(), leaderboard.getTitle());
				}

				new MessageSendLeaderboardList(map).sendTo(player);
				return;
			}

			Leaderboard leaderboard = FTBUCommon.LEADERBOARDS.getValue(new ResourceLocation(args[1]));

			if (leaderboard != null)
			{
				new MessageSendLeaderboard(leaderboard).sendTo(player);
			}
		}
		else if (FTBUCommon.LEADERBOARDS.getValue(new ResourceLocation(args[0])) != null)
		{
		}
		else
		{
			sender.sendMessage(new TextComponentString("Invalid ID!"));
		}
	}
}