package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * @author LatvianModder
 */
public class CmdClaimFor extends CmdBase
{
	public CmdClaimFor()
	{
		super("claim_for", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int i)
	{
		return i == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(args, 3, "<player> <chunkX> <chunkZ> [dimension]");
		String playerName = args[0];
		IForgePlayer claimFor = getForgePlayer(playerName);
		int chunkXPos = parseInt(args[1]);
		int chunkZPos = parseInt(args[2]);

		int dimension = args.length > 3 ? parseInt(args[3]) : sender.getEntityWorld().provider.getDimension();

		ChunkDimPos pos = new ChunkDimPos(chunkXPos, chunkZPos, dimension);
		if (FTBUUniverseData.claimChunk(claimFor, pos))
		{
			sender.sendMessage(new TextComponentString(String.format("Claimed %d, %d in %d for %s", chunkXPos, chunkZPos, dimension, playerName))); //LANG

			if (claimFor.isOnline())
			{
				CmdChunks.updateChunk(claimFor.getPlayer(), pos);
			}
		}
		else
		{
			sender.sendMessage(new TextComponentString(String.format("ERROR: Can't claim %d, %d in %d for %s", chunkXPos, chunkZPos, dimension, playerName))); //LANG
		}
	}
}