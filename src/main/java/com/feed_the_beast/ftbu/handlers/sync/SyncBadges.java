package com.feed_the_beast.ftbu.handlers.sync;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.ISyncData;
import com.feed_the_beast.ftbl.api.SyncData;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.client.CachedClientData;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/**
 * Created by LatvianModder on 17.08.2016.
 */
public enum SyncBadges implements ISyncData
{
    @SyncData
    INSTANCE;

    @Override
    public ResourceLocation getID()
    {
        return new ResourceLocation(FTBUFinals.MOD_ID, "badges");
    }

    @Override
    public NBTTagCompound writeSyncData(EntityPlayerMP player, IForgePlayer forgePlayer)
    {
        NBTTagCompound nbt = new NBTTagCompound();

        for(Badge b : FTBUUniverseData.LOCAL_BADGES.badgeMap.values())
        {
            nbt.setString(b.getName(), b.imageURL);
        }

        return nbt;
    }

    @Override
    public void readSyncData(NBTTagCompound nbt)
    {
        CachedClientData.LOCAL_BADGES.clear();
        CachedClientData.LOCAL_BADGES.copyFrom(CachedClientData.GLOBAL_BADGES);

        for(String key : nbt.getKeySet())
        {
            CachedClientData.LOCAL_BADGES.badgeMap.put(key, new Badge(key, nbt.getString(key)));
        }
    }
}