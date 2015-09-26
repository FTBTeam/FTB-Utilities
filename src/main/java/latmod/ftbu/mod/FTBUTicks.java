package latmod.ftbu.mod;

import latmod.core.util.*;
import latmod.ftbu.backups.Backups;
import latmod.ftbu.mod.config.FTBUConfig;
import latmod.ftbu.net.*;
import latmod.ftbu.util.*;
import latmod.ftbu.world.*;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public class FTBUTicks
{
	private static final FastList<ServerTickCallback> callbacks = new FastList<ServerTickCallback>();
	public static final IntMap areaRequests = new IntMap();
	public static boolean serverStarted = false;
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	private static long areasUpdated = 0L;
	
	public static void addCallback(ServerTickCallback e)
	{ callbacks.add(e); }
	
	public static void serverStarted()
	{
		serverStarted = true;
		
		currentMillis = startMillis = Backups.lastTimeRun = LMUtils.millis();
		restartSeconds = 0;
		
		if(FTBUConfig.general.restartTimer > 0)
		{
			restartSeconds = (long)(FTBUConfig.general.restartTimer * 3600D);
			LatCoreMC.logger.info("Server restart in " + LMStringUtils.getTimeString(restartSeconds));
		}
	}
	
	@SuppressWarnings("all")
	public static void serverStopped()
	{
		serverStarted = false;
		currentMillis = startMillis = restartSeconds = 0L;
	}
	
	public static void update()
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
				
				if(msg != null) LatCoreMC.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server will restart after " + msg);//LANG
			}
			
			if(secondsLeft > 60 && Backups.getSecondsUntilNextBackup() <= 0L) Backups.run();
		}
		
		if(!callbacks.isEmpty())
		{
			for(int i = 0; i < callbacks.size(); i++)
				if(callbacks.get(i).incAndCheck())
					callbacks.remove(i);
		}
		
		if(t - areasUpdated >= 2000L)
		{
			areasUpdated = t;
			
			if(!areaRequests.isEmpty())
			{
				for(int i = 0; i < areaRequests.size(); i++)
				{
					LMPlayerServer owner = LMWorldServer.inst.getPlayer(areaRequests.keys.get(i));
					EntityPlayerMP ep = owner.getPlayer();
					
					if(ep != null)
					{
						int size = areaRequests.values.get(i);
						int x = MathHelperLM.chunk(ep.posX) - size / 2;
						int z = MathHelperLM.chunk(ep.posZ) - size / 2;
						LMNetHelper.sendTo(ep, new MessageAreaUpdate(x, z, ep.dimension, size, owner));
					}
				}
				
				areaRequests.clear();
			}
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
