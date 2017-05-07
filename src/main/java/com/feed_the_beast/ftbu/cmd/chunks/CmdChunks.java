package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.lib.cmd.CmdTreeBase;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbu.handlers.FTBUPlayerEventHandler;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdChunks extends CmdTreeBase
{
    static void updateChunk(EntityPlayerMP ep, ChunkDimPos pos)
    {
        FTBUPlayerEventHandler.updateChunkMessage(ep, pos);
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
        addSubcommand(new CmdClaimFor());
        addSubcommand(new CmdPlayersInChunks());
        addSubcommand(new CmdPlayersInOwnChunk());
    }
}
