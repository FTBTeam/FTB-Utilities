package latmod.ftbu.mod;

import ftb.lib.*;
import latmod.ftbu.api.ServerTickCallback;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.net.MessageAreaUpdate;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

public class FTBUTicks
{
	private static final FastList<ServerTickCallback> callbacks = new FastList<ServerTickCallback>();
	public static final IntMap areaRequests = new IntMap();
	private static MinecraftServer server;
	public static boolean isDediServer = false;
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	private static long areasUpdated = 0L;
	private static String lastRestartMessage = "";
	
	public static void addCallback(ServerTickCallback e)
	{ callbacks.add(e); }
	
	public static void serverStarted()
	{
		server = FTBLib.getServer();
		isDediServer = server.isDedicatedServer();
		
		currentMillis = startMillis = Backups.lastTimeRun = LMUtils.millis();
		restartSeconds = 0;
		
		if(FTBUConfigGeneral.restartTimer.get() > 0)
		{
			restartSeconds = (long)(FTBUConfigGeneral.restartTimer.get() * 3600D);
			FTBLib.logger.info("Server restart in " + LMStringUtils.getTimeString(restartSeconds));
		}
		
		areaRequests.setDefVal(0);
	}
	
	@SuppressWarnings("all")
	public static void serverStopped()
	{
		server = null;
		isDediServer = false;
		currentMillis = startMillis = restartSeconds = 0L;
	}
	
	public static void update()
	{
		long t = LMUtils.millis();
		
		if(t - currentMillis >= 750L)
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
					FTBLib.getServer().initiateShutdown();
					return;
				}
				else if(secondsLeft <= 10) msg = secondsLeft + " Seconds";
				else if(secondsLeft == 30) msg = "30 Seconds";
				else if(secondsLeft == 60) msg = "1 Minute";
				else if(secondsLeft == 300) msg = "5 Minutes";
				else if(secondsLeft == 600) msg = "10 Minutes";
				
				if(msg != null && !lastRestartMessage.equals(msg))
				{
					lastRestartMessage = msg;
					FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server will restart after " + msg);//LANG
				}
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
					
					if(owner != null && owner.isOnline())
					{
						int size = Math.max(5, areaRequests.values.get(i));
						new MessageAreaUpdate(owner.getPos(), size, size, owner).sendTo(owner.getPlayer());
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
		restartSeconds = Math.max(0, sec) + 1L;
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
