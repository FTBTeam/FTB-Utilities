package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesPlayerEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.server.command.CommandTreeHelp;

/**
 * @author LatvianModder
 */
public class CmdChunks extends CmdTreeBase
{
	static void updateChunk(EntityPlayerMP player, ChunkDimPos pos)
	{
		FTBUtilitiesPlayerEventHandler.updateChunkMessage(player, pos);
	}

	public CmdChunks()
	{
		super("chunks");
		addSubcommand(new CmdClaim());
		addSubcommand(new CmdUnclaim());
		addSubcommand(new CmdLoad());
		addSubcommand(new CmdUnload());
		addSubcommand(new CmdUnclaimAll());
		addSubcommand(new CmdUnloadAll());
		addSubcommand(new CommandTreeHelp(this));
	}
}