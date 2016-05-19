package com.feed_the_beast.ftbu.handlers;

import com.feed_the_beast.ftbl.api.events.ForgeWorldEvent;
import com.feed_the_beast.ftbl.util.ChunkDimPos;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBUFinals;
import com.feed_the_beast.ftbu.config.FTBUConfigGeneral;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import com.feed_the_beast.ftbu.world.FTBUWorldDataSP;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUWorldEventHandler // FTBLIntegration
{
    @SubscribeEvent
    public void addWorldData(ForgeWorldEvent.AttachCapabilities event)
    {
        ResourceLocation r = new ResourceLocation(FTBUFinals.MOD_ID, "data");
        event.addCapability(r, event.world.getSide().isServer() ? new FTBUWorldDataMP() : new FTBUWorldDataSP());
    }

    @SubscribeEvent
    public void onWorldLoaded(ForgeWorldEvent.OnLoaded event)
    {
        if(event.world.hasCapability(FTBUCapabilities.FTBU_WORLD_DATA, null))
        {
            event.world.getCapability(FTBUCapabilities.FTBU_WORLD_DATA, null).onLoaded();
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
        if(e instanceof EntityPlayer) { return true; }

        if(FTBUConfigGeneral.blocked_entities.isEntityBanned(e.getClass())) { return false; }

        if(FTBUConfigGeneral.safe_spawn.getAsBoolean() && FTBUWorldDataMP.isInSpawnD(DimensionType.getById(e.dimension), e.posX, e.posZ))
        {
            if(e instanceof IMob) { return false; }
            else if(e instanceof EntityChicken && !e.getPassengers().isEmpty()) { return false; }
        }

        return true;
    }

    @SubscribeEvent
    public void onExplosionStart(net.minecraftforge.event.world.ExplosionEvent.Start e)
    {
        if(e.getWorld().isRemote) { return; }
        DimensionType dim = e.getWorld().provider.getDimensionType();
        int cx = MathHelperLM.chunk(e.getExplosion().getPosition().xCoord);
        int cz = MathHelperLM.chunk(e.getExplosion().getPosition().yCoord);

        if(!FTBUWorldDataMP.get().allowExplosion(new ChunkDimPos(dim, cx, cz)))
        {
            e.setCanceled(true);
        }
    }

}