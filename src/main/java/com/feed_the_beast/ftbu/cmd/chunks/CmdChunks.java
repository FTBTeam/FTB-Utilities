package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CommandTreeBase
{
    static void updateChunk(EntityPlayerMP ep, ChunkDimPos pos)
    {
        FTBUPlayerEventHandler.updateChunkMessage(ep, pos);
    }

    public CmdChunks()
    {
        addSubcommand(new CmdClaim());
        addSubcommand(new CmdUnclaim());
        addSubcommand(new CmdLoad());
        addSubcommand(new CmdUnload());

        addSubcommand(new CmdUnclaimAll());
        addSubcommand(new CmdUnloadAll());
        addSubcommand(new CmdAdminUnclaimAll());
        addSubcommand(new CmdClaimFor());
    }

    @Override
    public String getCommandName()
    {
        return "chunks";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "command.ftb.chunks.usage";
    }
}
