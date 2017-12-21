package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.data.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
public class CmdUnload extends CmdBase
{
	public CmdUnload()
	{
		super("unload", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p = getForgePlayer(player);
		ChunkDimPos pos = new ChunkDimPos(player);

		if (p.getTeam() != null && ClaimedChunks.get().setLoaded(p.getTeam(), pos, false))
		{
			Notification.of(FTBUFinals.get("chunk_modified"), TextComponentHelper.createComponentTranslation(player, FTBUFinals.MOD_ID + ".lang.chunks.chunk_unloaded")).send(player);
			CmdChunks.updateChunk(player, pos);
		}
		else
		{
			FTBUNotifications.sendCantModifyChunk(player);
		}
	}
}