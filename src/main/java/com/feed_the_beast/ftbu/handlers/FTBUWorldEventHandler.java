package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.ForgeWorldEvent;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.badges.Badge;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
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
    public void attachCapabilities(ForgeWorldEvent.AttachCapabilities event)
    {
        ResourceLocation r = new ResourceLocation(FTBUFinals.MOD_ID, "data");
        event.addCapability(r, event.world.getSide().isServer() ? new FTBUWorldDataMP() : new FTBUWorldDataSP());
    }

    @SubscribeEvent
    public void onWorldLoaded(ForgeWorldEvent.Loaded event)
    {
        if(event.world.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.world.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoaded();
        }
    }

    @SubscribeEvent
    public void onWorldLoadedBeforePlayers(ForgeWorldEvent.LoadedBeforePlayers event)
    {
        if(event.world.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.world.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoadedBeforePlayers();
        }
    }

    @SubscribeEvent
    public void onWorldClosed(ForgeWorldEvent.Closed event)
    {
        if(event.world.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.world.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onClosed();
        }
    }

    @SubscribeEvent
    public void onDataSynced(ForgeWorldEvent.Sync event)
    {
        if(event.world.getSide().isServer())
        {
            NBTTagCompound tag = new NBTTagCompound();

            NBTTagCompound tag1 = new NBTTagCompound();

            for(Badge b : FTBUWorldDataMP.localBadges.badgeMap.values())
            {
                tag1.setString(b.getID(), b.imageURL);
            }

            tag.setTag("B", tag1);

            event.syncData.setTag("FTBU", tag);
        }
        else
        {
            NBTTagCompound tag = event.syncData.getCompoundTag("FTBU");

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