package com.feed_the_beast.ftbu.cmd.chunks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.api.chunks.IPlayerInChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdPlayersInOwnChunk extends CmdBase {

	public CmdPlayersInOwnChunk() {
		super("players_in_own_chunk", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		checkArgs(args, 2, "<chunkX> <chunkZ> [dimension]");
		int chunkXPos = parseInt(args[0]);
        int chunkZPos = parseInt(args[1]);

        int dimension = args.length > 2 ? parseInt(args[2]) : sender.getEntityWorld().provider.getDimension();

        ChunkDimPos pos = new ChunkDimPos(chunkXPos, chunkZPos, dimension);
		if (ClaimedChunkStorage.INSTANCE.getChunkOwner(pos).getName().equals(sender.getName())) {
			Collection<IPlayerInChunk> players = ClaimedChunkStorage.INSTANCE.getChunk(pos).getAllPlayersInChunk();
        	ArrayList<String> messages = new ArrayList<String>();
        	for (IPlayerInChunk player : players) {
        		messages.add(String.format("player %s was in the chunk from %s to %s for %s", player.getPlayer().getName(), player.getEnterTime(), player.getLeaveTime(), player.getStayTime()));
        	}
        	Collections.reverse(messages);
        	for (String message : messages) {
        		sender.sendMessage(new TextComponentString(message));
        	}
		} else {
			sender.sendMessage(new TextComponentString("not your claim"));
		}
	}

}
