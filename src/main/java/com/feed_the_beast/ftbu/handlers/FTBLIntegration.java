package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.FTBUIntegration;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.guide.ServerInfoFile;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.ClaimedChunks;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.google.gson.JsonElement;
import latmod.lib.MathHelperLM;
import net.minecraft.util.math.BlockPos;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
    @Override
    public void onReloaded(ReloadEvent e)
    {
        if(e.world.getSide().isServer())
        {
            ServerInfoFile.CachedInfo.reload();
            Ranks.instance().reload();

            FTBUWorldDataMP.reloadServerBadges();

            if(FTBLib.getServerWorld() != null)
            {
                FTBUChunkEventHandler.instance.markDirty(null);
            }
        }
    }

    @Override
    public boolean canPlayerInteract(ForgePlayerMP player, boolean leftClick, BlockPos pos)
    {
        if(player == null)
        {
            return true;
        }

        if(leftClick)
        {
            for(JsonElement e : FTBUPermissions.claims_break_whitelist.getJson(player.getProfile()).getAsJsonArray())
            {
                if(e.getAsString().equals(LMInvUtils.getRegName(player.getPlayer().worldObj.getBlockState(pos).getBlock()).toString()))
                {
                    return true;
                }
            }
        }

        ClaimedChunk chunk = ClaimedChunks.inst.getChunk(new ChunkDimPos(player.getPlayer().dimension, MathHelperLM.chunk(pos.getX()), MathHelperLM.chunk(pos.getZ())));
        return chunk == null || chunk.canInteract(player, leftClick, pos);
    }
}