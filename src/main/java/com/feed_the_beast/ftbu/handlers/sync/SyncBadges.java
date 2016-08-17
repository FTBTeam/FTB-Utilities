package com.feed_the_beast.ftbu.handlers.sync;

import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataMP;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public class SyncBadges implements INBTSerializable<NBTTagCompound>
{
    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();

        for(Badge b : FTBUWorldDataMP.localBadges.badgeMap.values())
        {
            nbt.setString(b.getID(), b.imageURL);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        FTBUWorldDataSP.localBadges.clear();
        FTBUWorldDataSP.localBadges.copyFrom(FTBUWorldDataSP.globalBadges);

        for(String key : nbt.getKeySet())
        {
            FTBUWorldDataSP.localBadges.badgeMap.put(key, new Badge(key, nbt.getString(key)));
        }
    }
}