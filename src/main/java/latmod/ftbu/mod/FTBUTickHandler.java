package latmod.ftbu.mod;

import static net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.claims.Claims;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class FTBUTickHandler // FTBU // EnkiToolsTickHandler
{
	public static final FTBUTickHandler instance = new FTBUTickHandler();
	public static boolean serverStarted = false;
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e)
	{
		if(LatCoreMC.isServer() && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD)
		{
			long t = LatCore.millis();
			
			if(t - currentMillis >= 1000L)
			{
				currentMillis = t;
				
				if(FTBUConfig.General.restartTimer > 0)
				{
					long secondsLeft = getSecondsUntilRestart();
					
					String msg = null;
					
					if(secondsLeft <= 0) { LatCoreMC.getServer().initiateShutdown(); return; }
					else if(secondsLeft <= 10) msg = secondsLeft + " Seconds";
					else if(secondsLeft == 30) msg = "30 Seconds";
					else if(secondsLeft == 60) msg = "1 Minute";
					else if(secondsLeft == 300) msg = "5 Minutes";
					else if(secondsLeft == 600) msg = "10 Minutes";
					
					if(msg != null && secondsLeft >= 30)
						LatCoreMC.printChatAll(LIGHT_PURPLE + "Server will restart after " + msg);
				}
				
				for(EntityPlayerMP ep : LatCoreMC.getAllOnlinePlayers().values)
				{
					LMPlayer p = LMPlayer.getPlayer(ep);
					if(p == null) continue;
					if(p.lastPos == null) p.lastPos = new EntityPos(ep);
					
					else if(!p.lastPos.equalsPos(ep))
					{
						if(Claims.isOutsideWorldBorderD(ep.dimension, ep.posX, ep.posZ))
						{
							ep.motionX = ep.motionY = ep.motionZ = 0D;
							LatCoreMC.printChat(ep, "You have reached the world border!");
							
							if(Claims.isOutsideWorldBorderD(p.lastPos.dim, p.lastPos.x, p.lastPos.z))
							{
								LatCoreMC.printChat(ep, "Teleporting to spawn!");
								Vertex spawn = new Vertex(LatCoreMC.getSpawnPoint(0));
								
								if(Claims.isOutsideWorldBorderD(0, spawn.x, spawn.z))
								{
									spawn.x = spawn.z = 0.5D;
									spawn.y = DimensionManager.getWorld(0).getTopSolidOrLiquidBlock(0, 0);
								}
								
								Teleporter.travelEntity(ep, spawn.x, spawn.y, spawn.z, 0);
							}
							else
							{
								Teleporter.travelEntity(ep, p.lastPos.x, p.lastPos.y, p.lastPos.z, p.lastPos.dim);
							}
						}
						
						p.lastPos.set(ep);
						updateChunkMessage(ep);
					}
				}
			}
		}
	}
	
	private void updateChunkMessage(EntityPlayerMP ep)
	{
	}
	
	public static void resetTimer(boolean started)
	{
		serverStarted = started;
		
		if(serverStarted)
		{
			currentMillis = startMillis = LatCore.millis();
			restartSeconds = 0;
			
			if(FTBUConfig.General.restartTimer > 0)
			{
				restartSeconds = (long)(FTBUConfig.General.restartTimer * 3600D);
				LatCoreMC.logger.info("Server restart in " + LatCore.formatTime(restartSeconds, false));
			}
		}
		else
		{
			currentMillis = startMillis = restartSeconds = 0;
		}
	}
	
	public static long getSecondsUntilRestart()
	{ return Math.max(0L, restartSeconds - (currentSeconds() - startSeconds())); }
	
	public static void forceShutdown(int sec)
	{
		restartSeconds = sec + 1;
		//currentMillis = LatCore.millis();
		//currentSeconds = startSeconds = startMillis / 1000L;
	}
	
	public static long currentMillis()
	{ return currentMillis; }
	
	public static long currentSeconds()
	{ return currentMillis / 1000L; }
	
	public static long startSeconds()
	{ return startMillis / 1000L; }
}
