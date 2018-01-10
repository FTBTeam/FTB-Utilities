package com.feed_the_beast.ftbutilities.cmd.chunks;

import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.handlers.FTBUPlayerEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * @author LatvianModder
 */
public class CmdChunks extends CmdTreeBase
{
	static void updateChunk(EntityPlayerMP player, ChunkDimPos pos)
	{
		FTBUPlayerEventHandler.updateChunkMessage(player, pos);
	}

	public CmdChunks()
	{
		super("chunks");
		addSubcommand(new CmdGui());
		addSubcommand(new CmdClaim());
		addSubcommand(new CmdUnclaim());
		addSubcommand(new CmdLoad());
		addSubcommand(new CmdUnload());
		addSubcommand(new CmdUnclaimAll());
		addSubcommand(new CmdUnloadAll());
	}
}