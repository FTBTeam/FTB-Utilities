package com.feed_the_beast.ftbutilities.cmd.chunks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunk;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CmdUnloadAll extends CmdBase
{
	public CmdUnloadAll()
	{
		super("unload_all", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, LIST_TRUE_FALSE);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (ClaimedChunks.instance == null)
		{
			throw FTBLibLang.FEATURE_DISABLED_SERVER.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p;

		if (args.length >= 2)
		{
			if (!PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			p = getForgePlayer(sender, args[1]);
		}
		else
		{
			p = getForgePlayer(player);
		}

		boolean allDimensions = args.length == 0 || parseBoolean(args[0]);

		for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(p.team))
		{
			if (!allDimensions || player.dimension == chunk.getPos().dim)
			{
				chunk.setLoaded(false);
			}
		}

		FTBUtilitiesLang.CHUNKS_UNLOADED_FOR.sendMessage(sender, p.getName());
	}
}