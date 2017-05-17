package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
        IForgePlayer p = getForgePlayer(player);
        ChunkDimPos pos = new ChunkDimPos(player);

        if(FTBUUniverseData.setLoaded(p, pos, false))
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CHUNK_UNLOADED);
            CmdChunks.updateChunk(player, pos);
        }
        else
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CANT_MODIFY_CHUNK);
        }
    }
}