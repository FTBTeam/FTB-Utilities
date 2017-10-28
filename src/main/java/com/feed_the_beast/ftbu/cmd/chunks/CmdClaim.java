package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.util.text_components.Notification;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import com.feed_the_beast.ftbu.util.FTBUTeamData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author LatvianModder
 */
public class CmdClaim extends CmdBase
{
	public CmdClaim()
	{
		super("claim", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!FTBUConfig.world.chunk_claiming)
		{
			throw FTBLibLang.FEATURE_DISABLED.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		IForgePlayer p;

		if (args.length >= 1)
		{
			if (!PermissionAPI.hasPermission(player, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			p = getForgePlayer(args[0]);
		}
		else
		{
			p = getForgePlayer(player);
		}

		if (p.getTeam() == null)
		{
			throw FTBLibLang.TEAM_NO_TEAM.commandError();
		}

		ChunkDimPos pos = new ChunkDimPos(player);

		if (!FTBUConfig.world.allowDimension(pos.dim))
		{
			Notification.of(FTBUFinals.get("cant_claim_chunk"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.claiming_not_enabled_dim")).setError().send(player);
			return;
		}

		switch (ClaimedChunks.INSTANCE.claimChunk(FTBUTeamData.get(p.getTeam()), pos))
		{
			case SUCCESS:
				Notification.of(FTBUFinals.get("chunk_modified"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.chunk_claimed")).send(player);
				CmdChunks.updateChunk(player, pos);
				break;
			case DIMENSION_BLOCKED:
				break;
			case NO_POWER:
				break;
			case ALREADY_CLAIMED:
				FTBUNotifications.sendCantModifyChunk(player);
				break;
		}
	}
}