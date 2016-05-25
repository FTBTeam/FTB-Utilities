package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.FTBLibPermissions;
import com.feed_the_beast.ftbl.FTBUIntegration;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.events.ReloadEvent;
import com.feed_the_beast.ftbl.api.item.LMInvUtils;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.guide.ServerInfoFile;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.feed_the_beast.ftbu.world.ChunkType;
import com.feed_the_beast.ftbu.world.ClaimedChunks;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.google.gson.JsonElement;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

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
    public void renderWorld(float pt)
    {
    }

    @Override
    public void onTooltip(ItemTooltipEvent e)
    {
    }

    @Override
    public boolean canPlayerInteract(ForgePlayerMP player, BlockPos pos, boolean leftClick)
    {
        if(player == null)
        {
            return true;
        }
        else if(!player.isFake() && PermissionAPI.hasPermission(player.getProfile(), FTBLibPermissions.interact_secure, false))
        {
            return true;
        }

        //TODO: World border

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

        ChunkType type = ClaimedChunks.inst.getTypeD(player, player.getPlayer().dimension, pos);
        return type.canInteract(player, leftClick);
    }
}