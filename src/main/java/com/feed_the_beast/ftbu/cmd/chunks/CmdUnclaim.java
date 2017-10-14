package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;

/**
 * @author LatvianModder
 */
public class CmdUnclaim extends CmdBase
{
	public CmdUnclaim()
	{
		super("unclaim", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (!FTBUConfig.world.chunk_claiming)
		{
			throw FTBLibLang.FEATURE_DISABLED.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		IForgePlayer p = getForgePlayer(player);

		if (p.getTeam() == null)
		{
			throw FTBLibLang.TEAM_NO_TEAM.commandError();
		}

		ChunkDimPos pos = new ChunkDimPos(player);

		if (!p.getTeam().equalsTeam(ClaimedChunks.INSTANCE.getChunkTeam(pos)) && !PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS, new BlockPosContext(player, pos.getChunkPos())))
		{
			throw FTBLibLang.COMMAND_PERMISSION.commandError();
		}

		if (ClaimedChunks.INSTANCE.unclaimChunk(p.getTeam(), pos))
		{
			FTBUNotifications.CHUNK_UNCLAIMED.send(player);
			CmdChunks.updateChunk(player, pos);
		}
		else
		{
			FTBUNotifications.CANT_MODIFY_CHUNK.send(player);
		}
	}
}