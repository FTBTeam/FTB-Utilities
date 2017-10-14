package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
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