package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseClosedEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseLoadedBeforePlayersEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseLoadedEvent;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUWorldEventHandler // FTBLIntegration
{
    @SubscribeEvent
    public void onWorldLoaded(ForgeUniverseLoadedEvent event)
    {
        FTBUUniverseData data = FTBUUniverseData.get();

        if(data != null)
        {
            data.onLoaded();
        }
    }

    @SubscribeEvent
    public void onWorldLoadedBeforePlayers(ForgeUniverseLoadedBeforePlayersEvent event)
    {
        FTBUUniverseData data = FTBUUniverseData.get();

        if(data != null)
        {
            data.onLoadedBeforePlayers();
        }
    }

    @SubscribeEvent
    public void onWorldClosed(ForgeUniverseClosedEvent event)
    {
        FTBUUniverseData data = FTBUUniverseData.get();

        if(data != null)
        {
            data.onClosed();
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

        if(FTBUConfigWorld.BLOCKED_ENTITIES.containsEntity(e.getClass(), true))
        {
            return false;
        }

        if(FTBUConfigWorld.SAFE_SPAWN.getBoolean() && FTBUUniverseData.isInSpawnD(e.dimension, e.posX, e.posZ))
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
        if(!e.getWorld().isRemote && !FTBUUniverseData.allowExplosion(e.getWorld(), e.getExplosion()))
        {
            e.setCanceled(true);
        }
    }
}