package com.feed_the_beast.ftbutilities.cmd.chunks;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUFinals;
import com.feed_the_beast.ftbutilities.FTBUNotifications;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;
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
		if (ClaimedChunks.instance == null)
		{
			throw FTBLibLang.FEATURE_DISABLED.commandError();
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p = getForgePlayer(player);

		if (p.getTeam() == null)
		{
			throw FTBLibLang.TEAM_NO_TEAM.commandError();
		}

		ChunkDimPos pos = new ChunkDimPos(player);

		if (!p.getTeam().equalsTeam(ClaimedChunks.instance.getChunkTeam(pos)) && !PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS, new BlockPosContext(player, pos.getChunkPos())))
		{
			throw FTBLibLang.COMMAND_PERMISSION.commandError();
		}

		if (ClaimedChunks.instance.unclaimChunk(p.getTeam(), pos))
		{
			Notification.of(FTBUFinals.get("chunk_modified"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.chunk_unclaimed")).send(server, player);
			CmdChunks.updateChunk(player, pos);
		}
		else
		{
			FTBUNotifications.sendCantModifyChunk(server, player);
		}
	}
}