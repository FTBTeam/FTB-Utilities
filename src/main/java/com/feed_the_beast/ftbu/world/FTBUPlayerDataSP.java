package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import gnu.trove.map.TIntIntMap;
import com.latmod.lib.util.LMTroveUtils;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by LatvianModder on 23.02.2016.
 */
public class FTBUPlayerDataSP extends FTBUPlayerData
{
    public short claimedChunks, loadedChunks, maxClaimedChunks, maxLoadedChunks;

    @Override
    public FTBUPlayerDataSP toSP()
    {
        return this;
    }

    @Override
    public void readSyncData(ForgePlayer player, NBTTagCompound tag, boolean self)
    {
        TIntIntMap map = LMTroveUtils.fromArray(tag.getIntArray("F"));

        renderBadge.set(map.get(0) == 1);

        if(self)
        {
            claimedChunks = (short) map.get(10);
            loadedChunks = (short) map.get(11);
            maxClaimedChunks = (short) map.get(12);
            maxLoadedChunks = (short) map.get(13);
        }
    }
}
