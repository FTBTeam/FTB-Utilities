package com.feed_the_beast.ftbu.handlers.sync;

import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
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

        for(Badge b : FTBUUniverseData.LOCAL_BADGES.badgeMap.values())
        {
            nbt.setString(b.getName(), b.imageURL);
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        CachedClientData.LOCAL_BADGES.clear();
        CachedClientData.LOCAL_BADGES.copyFrom(CachedClientData.GLOBAL_BADGES);

        for(String key : nbt.getKeySet())
        {
            CachedClientData.LOCAL_BADGES.badgeMap.put(key, new Badge(key, nbt.getString(key)));
        }
    }
}