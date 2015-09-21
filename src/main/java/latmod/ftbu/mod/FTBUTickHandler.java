package latmod.ftbu.mod;

import static net.minecraft.util.EnumChatFormatting.LIGHT_PURPLE;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import latmod.ftbu.mod.backups.Backups;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class FTBUTickHandler
{
	public static final FastList<ServerTickCallback> callbacks = new FastList<ServerTickCallback>();
	public static boolean serverStarted = false;
	
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	
	@SubscribeEvent
	public void onChunkChanged(net.minecraftforge.event.entity.EntityEvent.EnteringChunk e)
	{
		if(!e.entity.worldObj.isRemote && e.entity instanceof EntityPlayerMP)
		{
			EntityPlayerMP ep = (EntityPlayerMP)e.entity;
			LMPlayerServer player = LMWorldServer.inst.getPlayer(ep);
			if(player == null) return;
			
			if(player.lastPos == null) player.lastPos = new EntityPos(ep);
			
			else if(!player.lastPos.equalsPos(ep))
			{
				if(Claims.isOutsideWorldBorderD(ep.dimension, ep.posX, ep.posZ))
				{
					ep.motionX = ep.motionY = ep.motionZ = 0D;
					IChatComponent warning = new ChatComponentTranslation(FTBU.mod.assets + ChunkType.WORLD_BORDER.lang + ".warning");
					warning.getChatStyle().setColor(EnumChatFormatting.RED);
					LatCoreMC.notifyPlayer(ep, new Notification("world_border", warning, 3000));
					
					if(Claims.isOutsideWorldBorderD(player.lastPos.dim, player.lastPos.x, player.lastPos.z))
					{
						LatCoreMC.printChat(ep, "Teleporting to spawn!");
						World w = LMDimUtils.getWorld(0);
						ChunkCoordinates pos = w.getSpawnPoint();
						pos.posY = w.getTopSolidOrLiquidBlock(pos.posX, pos.posZ);
						LMDimUtils.teleportPlayer(ep, pos.posX + 0.5D, pos.posY + 1.25D, pos.posZ + 0.5D, 0);
					}
					else LMDimUtils.teleportPlayer(ep, player.lastPos);
					ep.worldObj.playSoundAtEntity(ep, "random.fizz", 1F, 1F);
				}
				
				player.lastPos.set(ep);
			}
			
			int currentChunkType = ChunkType.getChunkTypeI(ep.dimension, e.newChunkX, e.newChunkZ, player);
			
			if(player.lastChunkType == -99 || player.lastChunkType != currentChunkType)
			{
				player.lastChunkType = currentChunkType;
				
				ChunkType type = ChunkType.getChunkTypeFromI(currentChunkType, player);
				IChatComponent msg = null;
				
				if(type.isClaimed())
					msg = new ChatComponentText("" + LMWorldServer.inst.getPlayer(currentChunkType));
				else
					msg = new ChatComponentTranslation(FTBU.mod.assets + type.lang);
				
				Notification n = new Notification("chunk_changed", msg, 3000);
				n.setColor(type.areaColor);
				
				LatCoreMC.notifyPlayer(ep, n);
			}
		}
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent e)
	{
		if(LatCoreMC.isServer() && e.side == Side.SERVER && e.phase == TickEvent.Phase.END && e.type == TickEvent.Type.WORLD)
		{
			long t = LMUtils.millis();
			
			if(t - currentMillis >= 200L)
			{
				currentMillis = t;
				
				long secondsLeft = 3600L;
				
				if(restartSeconds > 0L)
				{
					secondsLeft = getSecondsUntilRestart();
					
					String msg = null;
					
					if(secondsLeft <= 0)
					{
						new CommandSaveAll().processCommand(BroadcastSender.inst, new String[] { "flush" });
						LatCoreMC.getServer().initiateShutdown();
						return;
					}
					else if(secondsLeft <= 10) msg = secondsLeft + " Seconds";
					else if(secondsLeft == 30) msg = "30 Seconds";
					else if(secondsLeft == 60) msg = "1 Minute";
					else if(secondsLeft == 300) msg = "5 Minutes";
					else if(secondsLeft == 600) msg = "10 Minutes";
					
					if(msg != null) LatCoreMC.printChat(BroadcastSender.inst, LIGHT_PURPLE + "Server will restart after " + msg);
				}
				
				if(secondsLeft > 60 && Backups.getSecondsUntilNextBackup() <= 0L) Backups.run();
			}
			
			if(!callbacks.isEmpty())
			{
				for(int i = 0; i < callbacks.size(); i++)
					if(callbacks.get(i).incAndCheck())
						callbacks.remove(i);
			}
		}
	}
	
	public static void serverStarted()
	{
		serverStarted = true;
		
		currentMillis = startMillis = Backups.lastTimeRun = LMUtils.millis();
		restartSeconds = 0;
		
		if(FTBUConfig.general.restartTimer > 0)
		{
			restartSeconds = (long)(FTBUConfig.general.restartTimer * 3600D);
			LatCoreMC.logger.info("Server restart in " + LMStringUtils.formatTime(restartSeconds, false));
		}
	}
	
	@SuppressWarnings("all")
	public static void serverStopped()
	{
		serverStarted = false;
		currentMillis = startMillis = restartSeconds = 0L;
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
