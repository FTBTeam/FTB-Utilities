package ftb.utils.mod.handlers;

import ftb.lib.api.*;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.mod.handlers.ftbl.*;
import ftb.utils.world.claims.ClaimedChunks;
import latmod.lib.MathHelperLM;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FTBUWorldEventHandler // FTBLIntegration
{
	@SubscribeEvent
	public void addWorldData(ForgeWorldDataEvent event)
	{
		event.add(new FTBUWorldData("FTBU", event.LMWorld));
	}
	
	@SubscribeEvent
	public void addPlayerData(ForgePlayerDataEvent event)
	{
		event.add(new FTBUPlayerData("FTBU", event.LMPlayer));
	}
	
	@SubscribeEvent
	public void onMobSpawned(net.minecraftforge.event.entity.EntityJoinWorldEvent e)
	{
		if(!e.world.isRemote && !isEntityAllowed(e.entity))
		{
			e.entity.setDead();
			e.setCanceled(true);
		}
	}
	
	private boolean isEntityAllowed(Entity e)
	{
		if(e instanceof EntityPlayer) return true;
		
		if(FTBUConfigGeneral.isEntityBanned(e.getClass())) return false;
		
		if(FTBUConfigGeneral.safe_spawn.get() && ClaimedChunks.isInSpawnD(e.dimension, e.posX, e.posZ))
		{
			if(e instanceof IMob) return false;
			else if(e instanceof EntityChicken && e.riddenByEntity != null) return false;
		}
		
		return true;
	}
	
	@SubscribeEvent
	public void onExplosionStart(net.minecraftforge.event.world.ExplosionEvent.Start e)
	{
		if(e.world.isRemote) return;
		int dim = e.world.provider.getDimensionId();
		int cx = MathHelperLM.chunk(e.explosion.getPosition().xCoord);
		int cz = MathHelperLM.chunk(e.explosion.getPosition().yCoord);
		if(!ClaimedChunks.instance.allowExplosion(dim, cx, cz)) e.setCanceled(true);
	}
	
}