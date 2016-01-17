package latmod.ftbu.mod;

import ftb.lib.*;
import latmod.ftbu.badges.ServerBadges;
import latmod.ftbu.mod.cmd.admin.CmdRestart;
import latmod.ftbu.mod.config.FTBUConfigGeneral;
import latmod.ftbu.mod.handlers.FTBUChunkEventHandler;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.util.*;

public class FTBUTicks
{
	public static long nextChunkloaderUpdate = 0L;
	
	private static long startMillis = 0L;
	private static long currentMillis = 0L;
	private static long restartSeconds = 0L;
	private static String lastRestartMessage = "";
	
	public static void serverStarted()
	{
		currentMillis = startMillis = Backups.lastTimeRun = LMUtils.millis();
		restartSeconds = 0;
		
		if(FTBUConfigGeneral.restart_timer.get() > 0)
		{
			restartSeconds = (long) (FTBUConfigGeneral.restart_timer.get() * 3600D);
			FTBU.mod.logger.info("Server restart in " + LMStringUtils.getTimeString(restartSeconds * 1000L));
		}
	}
	
	@SuppressWarnings("all")
	public static void serverStopped()
	{
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
				String msg = LMStringUtils.getTimeString(secondsLeft * 1000L);
				if(msg != null && !lastRestartMessage.equals(msg))
				{
					lastRestartMessage = msg;
					
					if(secondsLeft <= 0)
					{
						CmdRestart.restart();
						return;
					}
					else if(secondsLeft <= 10 || secondsLeft == 60 || secondsLeft == 300 || secondsLeft == 600 || secondsLeft == 1800)
					{
						IChatComponent c = new ChatComponentTranslation(FTBU.mod.assets + "server_restart", msg);
						c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
						FTBLib.printChat(BroadcastSender.inst, c);
					}
				}
			}
			
			if(secondsLeft > 60 && Backups.getSecondsUntilNextBackup() <= 0L) Backups.run();
		}
		
		long now = LMUtils.millis();
		if(nextChunkloaderUpdate < now)
		{
			nextChunkloaderUpdate = now + 300000L;
			FTBUChunkEventHandler.instance.markDirty(null);
		}
		
		if(Backups.shouldKillThread)
		{
			Backups.shouldKillThread = false;
			boolean wasBackup = Backups.thread instanceof ThreadBackup;
			Backups.thread = null;
			
			if(wasBackup)
			{
				try
				{
					new CommandSaveOn().processCommand(FTBLib.getServer(), new String[0]);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		
		if(ServerBadges.updateBadges && !ServerBadges.isReloading)
		{
			ServerBadges.sendToPlayer(null);
			ServerBadges.updateBadges = false;
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
