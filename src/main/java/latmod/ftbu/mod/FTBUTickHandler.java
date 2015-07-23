package latmod.ftbu.mod;

import static net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE;
import latmod.ftbu.core.*;
import latmod.ftbu.core.net.*;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.claims.*;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;

public class FTBUTickHandler
{
	public static final FTBUTickHandler instance = new FTBUTickHandler();
	public static boolean serverStarted = false;
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	
	@SubscribeEvent
	public void onChunkChanged(net.minecraftforge.event.entity.EntityEvent.EnteringChunk e)
	{
		if(e.entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e.entity;
			LMPlayerServer p = LMWorldServer.inst.getPlayer(ep);
			if(p == null) return;
			
			if(p.lastPos == null) p.lastPos = new EntityPos(ep);
			
			else if(!p.lastPos.equalsPos(ep))
			{
				if(Claims.isOutsideWorldBorderD(ep.dimension, ep.posX, ep.posZ))
				{
					ep.motionX = ep.motionY = ep.motionZ = 0D;
					IChatComponent warning = new ChatComponentTranslation("ftbu:chunktype." + ChunkType.WORLD_BORDER.lang + ".warning");
					warning.getChatStyle().setColor(EnumChatFormatting.RED);
					ep.addChatMessage(warning);
					
					if(Claims.isOutsideWorldBorderD(p.lastPos.dim, p.lastPos.x, p.lastPos.z))
					{
						LatCoreMC.printChat(ep, "Teleporting to spawn!");
						FTBUEventHandler.instance.teleportToSpawn(ep);
					}
					else LMDimUtils.teleportPlayer(ep, p.lastPos);
					ep.worldObj.playSoundAtEntity(ep, "random.fizz", 1F, 1F);
				}
				
				p.lastPos.set(ep);
			}
			
			LMNetHelper.sendTo(ep, new MessageAreaUpdate(e.newChunkX, e.newChunkZ, ep.dimension, (byte)1, p));
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e)
	{
		if(LatCoreMC.isServer() && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD)
		{
			long t = LatCore.millis();
			
			if(t - currentMillis >= 200L)
			{
				currentMillis = t;
				
				long secondsLeft = 3600L;
				
				if(restartSeconds > 0L)
				{
					secondsLeft = getSecondsUntilRestart();
					
					String msg = null;
					
					if(secondsLeft <= 0) { LatCoreMC.getServer().initiateShutdown(); return; }
					else if(secondsLeft <= 10) msg = secondsLeft + " Seconds";
					else if(secondsLeft == 30) msg = "30 Seconds";
					else if(secondsLeft == 60) msg = "1 Minute";
					else if(secondsLeft == 300) msg = "5 Minutes";
					else if(secondsLeft == 600) msg = "10 Minutes";
					
					if(msg != null) LatCoreMC.printChat(BroadcastSender.inst, LIGHT_PURPLE + "Server will restart after " + msg);
				}
				
				if(secondsLeft > 60 && Backups.getSecondsUntilNextBackup() <= 0L) Backups.run();
			}
		}
	}
	
	public static void resetTimer(boolean started)
	{
		serverStarted = started;
		
		if(serverStarted)
		{
			currentMillis = startMillis = Backups.lastTimeRun = LatCore.millis();
			restartSeconds = 0;
			
			if(FTBUConfig.general.restartTimer > 0)
			{
				restartSeconds = (long)(FTBUConfig.general.restartTimer * 3600D);
				LatCoreMC.logger.info("Server restart in " + LatCore.formatTime(restartSeconds, false));
			}
		}
		else
		{
			currentMillis = startMillis = restartSeconds = 0L;
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
