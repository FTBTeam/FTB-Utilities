package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgeTeam;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by LatvianModder on 01.06.2016.
 */
public class FTBUTeamDataSP extends FTBUTeamData
{
    @Override
    public FTBUTeamDataSP toSP()
    {
        return this;
    }

    public void readSyncData(ForgeTeam team, NBTTagCompound tag, ForgePlayer player)
    {
    }
}