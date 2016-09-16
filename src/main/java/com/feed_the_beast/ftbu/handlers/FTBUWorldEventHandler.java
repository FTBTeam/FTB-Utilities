package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.universe.AttachUniverseCapabilitiesEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseClosedEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseLoadedBeforePlayersEvent;
import com.feed_the_beast.ftbl.api.events.universe.ForgeUniverseLoadedEvent;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUWorldEventHandler // FTBLIntegration
{
    @SubscribeEvent
    public void attachCapabilities(AttachUniverseCapabilitiesEvent event)
    {
        event.addCapability(new ResourceLocation(FTBUFinals.MOD_ID, "data"), new FTBUUniverseData());
    }

    @SubscribeEvent
    public void onWorldLoaded(ForgeUniverseLoadedEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoaded();
        }
    }

    @SubscribeEvent
    public void onWorldLoadedBeforePlayers(ForgeUniverseLoadedBeforePlayersEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoadedBeforePlayers();
        }
    }

    @SubscribeEvent
    public void onWorldClosed(ForgeUniverseClosedEvent event)
    {
        if(event.getWorld().hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.getWorld().getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onClosed();
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

        if(FTBUConfigWorld.BLOCKED_ENTITIES.isEntityBanned(e.getClass()))
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