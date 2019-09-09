package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.ForgeTeam;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.data.ClaimResult;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class CmdClaimAs extends CmdBase
{
	public CmdClaimAs()
	{
		super("claim_as", Level.OP);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			List<String> list = new ArrayList<>();

			for (ForgeTeam team : Universe.get().getTeams())
			{
				if (team.type.isServer)
				{
					list.add(team.getId());
				}
			}

			return getListOfStringsMatchingLastWord(args, list);
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!ClaimedChunks.isActive())
		{
			throw FTBLib.error(sender, "feature_disabled_server");
		}

		checkArgs(sender, args, 1);
		ForgeTeam team = CommandUtils.getTeam(sender, args[0]);

		if (!team.type.isServer)
		{
			throw FTBUtilities.error(sender, "commands.chunks.claim_as.team_not_server", args[0]);
		}

		int radius = 0;

		if (args.length >= 2)
		{
			radius = parseInt(args[1], 0, 30);
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		ForgePlayer p = new ForgePlayer(Universe.get(), UUID.nameUUIDFromBytes("FakePlayerClaimAs".getBytes(StandardCharsets.UTF_8)), "FakePlayerClaimAs");

		p.team = team;
		ChunkDimPos pos = new ChunkDimPos(player);

		for (int x = -radius; x <= radius; x++)
		{
			for (int z = -radius; z <= radius; z++)
			{
				ChunkDimPos pos1 = new ChunkDimPos(pos.posX + x, pos.posZ + z, pos.dim);
				ClaimResult result = ClaimedChunks.instance.claimChunk(p, pos1, false);

				if (x == 0 && z == 0)
				{
					switch (result)
					{
						case SUCCESS:
							Notification.of(FTBUtilitiesNotifications.CHUNK_MODIFIED, FTBUtilities.lang(player, "ftbutilities.lang.chunks.chunk_claimed")).send(server, player);
							FTBUtilitiesNotifications.updateChunkMessage(player, pos1);
							break;
						case DIMENSION_BLOCKED:
							Notification.of(FTBUtilitiesNotifications.CHUNK_CANT_CLAIM, FTBUtilities.lang(player, "ftbutilities.lang.chunks.claiming_not_enabled_dim")).setError().send(server, player);
							break;
						default:
							FTBUtilitiesNotifications.sendCantModifyChunk(server, player);
							break;
					}
				}
			}
		}
	}
}