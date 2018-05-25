package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
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
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!ClaimedChunks.isActive())
		{
			throw new CommandException("feature_disabled_server");
		}

		ForgePlayer p;

		if (args.length >= 2)
		{
			if (!(sender instanceof EntityPlayerMP) || PermissionAPI.hasPermission((EntityPlayerMP) sender, FTBUtilitiesPermissions.CLAIMS_CHUNKS_MODIFY_OTHER))
			{
				p = getForgePlayer(sender, args[1]);
			}
			else
			{
				throw new CommandException("commands.generic.permission");
			}
		}
		else
		{
			p = getForgePlayer(sender);
		}

		if (p.hasTeam())
		{
			boolean allDimensions = args.length == 0 || parseBoolean(args[0]);
			int dim = sender.getEntityWorld().provider.getDimension();

			for (ClaimedChunk chunk : ClaimedChunks.instance.getTeamChunks(p.team))
			{
				if (!allDimensions || dim == chunk.getPos().dim)
				{
					chunk.setLoaded(false);
				}
			}

			Notification.of(FTBUtilitiesNotifications.UNCLAIMED_ALL, FTBUtilities.lang(sender, "ftbutilities.lang.chunks.unloaded_all")).send(server, sender);
		}
		else
		{
			throw new CommandException("ftblib.lang.team.error.no_team");
		}
	}
}