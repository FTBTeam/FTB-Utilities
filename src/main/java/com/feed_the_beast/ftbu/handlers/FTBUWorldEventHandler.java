package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.SyncDataEvent;
import com.feed_the_beast.ftbl.api.events.world.AttachWorldCapabilitiesEvent;
import com.feed_the_beast.ftbl.api.events.world.ForgeWorldClosedEvent;
import com.feed_the_beast.ftbl.api.events.world.ForgeWorldLoadedBeforePlayersEvent;
import com.feed_the_beast.ftbl.api.events.world.ForgeWorldLoadedEvent;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataMP;
import com.feed_the_beast.ftbu.world.data.FTBUWorldDataSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUWorldEventHandler // FTBLIntegration
{
    @SubscribeEvent
    public void attachCapabilities(AttachWorldCapabilitiesEvent event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUWorldDataMP());
    }

    @SubscribeEvent
    public void onWorldLoaded(ForgeWorldLoadedEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoaded();
        }
    }

    @SubscribeEvent
    public void onWorldLoadedBeforePlayers(ForgeWorldLoadedBeforePlayersEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoadedBeforePlayers();
        }
    }

    @SubscribeEvent
    public void onWorldClosed(ForgeWorldClosedEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onClosed();
        }
    }

    @SubscribeEvent
    public void onDataSynced(SyncDataEvent event)
    {
        if(event.getSide().isServer())
        {
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound tag1 = new NBTTagCompound();

            for(Badge b : FTBUWorldDataMP.localBadges.badgeMap.values())
            {
                tag1.setString(b.getID(), b.imageURL);
            }

            tag.setTag("B", tag1);

            event.getData().setTag("FTBU", tag);
        }
        else
        {
            NBTTagCompound tag = event.getData().getCompoundTag("FTBU");

            NBTTagCompound tag1 = tag.getCompoundTag("B");

            FTBUWorldDataSP.localBadges.clear();
            FTBUWorldDataSP.localBadges.copyFrom(FTBUWorldDataSP.globalBadges);

            for(String key : tag1.getKeySet())
            {
                FTBUWorldDataSP.localBadges.badgeMap.put(key, new Badge(key, tag1.getString(key)));
            }
        }
    }

    @SubscribeEvent
    public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
    {
        if(!e.getWorld().isRemote && !isEntityAllowed(e.getEntity()))
        {
            e.getEntity().setDead();
            e.setCanceled(true);
        }
    }

    private boolean isEntityAllowed(Entity e)
    {
        if(e instanceof EntityPlayer)
        {
            return true;
        }

        if(FTBUConfigGeneral.blocked_entities.isEntityBanned(e.getClass()))
        {
            return false;
        }

        if(FTBUConfigGeneral.safe_spawn.getAsBoolean() && FTBUWorldDataMP.isInSpawnD(e.dimension, e.posX, e.posZ))
        {
            if(e instanceof IMob)
            {
                return false;
            }
            else if(e instanceof EntityChicken && !e.getPassengers().isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    @SubscribeEvent
    public void onExplosionStart(ExplosionEvent.Start e)
    {
        if(!e.getWorld().isRemote && !FTBUWorldDataMP.allowExplosion(e.getWorld(), e.getExplosion()))
        {
            e.setCanceled(true);
        }
    }

}