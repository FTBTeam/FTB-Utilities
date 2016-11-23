package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 10.11.2016.
 */
public class CmdClaimFor extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "claim_for";
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + getCommandName() + " <player> <chunkX> <chunkZ> [<dimension>]";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 3, "<player> <chunkX> <chunkZ> <dimension>");
        String playerName = args[0];
        IForgePlayer claim_for = getForgePlayer(playerName);
        int chunkXPos = parseInt(args[1]);
        int chunkZPos = parseInt(args[2]);

        int dimension = args.length > 3 ? parseInt(args[3]) : ics.getEntityWorld().provider.getDimension();

        EntityPlayerMP player = claim_for.getPlayer();
        if(player == null)
        {
            ics.addChatMessage(new TextComponentString("Can't find player " + playerName));
            return;
        }

        ChunkDimPos pos = new ChunkDimPos(chunkXPos, chunkZPos, dimension);
        if(FTBUUniverseData.claimChunk(claim_for, pos))
        {
            String msg = String.format("Claimed %d, %d in %d for %s", chunkXPos, chunkZPos, dimension, playerName);
            ics.addChatMessage(new TextComponentString(msg));
            CmdChunks.updateChunk(player, pos);
        }
        else
        {
            String msg = String.format("ERROR: Can't claim %d, %d in %d for %s", chunkXPos, chunkZPos, dimension, playerName);
            ics.addChatMessage(new TextComponentString(msg));
        }
    }
}