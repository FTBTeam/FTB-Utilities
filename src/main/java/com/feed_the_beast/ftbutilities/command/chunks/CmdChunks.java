package com.feed_the_beast.ftbutilities.command.chunks;

import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.command.CmdTreeHelp;
import com.feed_the_beast.ftblib.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbutilities.handlers.FTBUtilitiesPlayerEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;

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
		addSubcommand(new CmdUnclaimEverything());
		addSubcommand(new CmdUnloadEverything());
		addSubcommand(new CmdTreeHelp(this));
	}
}