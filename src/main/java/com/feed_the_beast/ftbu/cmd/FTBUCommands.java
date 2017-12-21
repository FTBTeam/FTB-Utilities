package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftblib.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftbu.FTBUConfig;
import com.feed_the_beast.ftbu.cmd.chunks.CmdChunks;
import com.feed_the_beast.ftbu.cmd.ranks.CmdRanks;
import com.feed_the_beast.ftbu.cmd.tp.CmdBack;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdSpawn;
import com.feed_the_beast.ftbu.cmd.tp.CmdTplast;
import com.feed_the_beast.ftbu.cmd.tp.CmdWarp;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUCommands
{
	@SubscribeEvent
	public static void registerCommands(RegisterFTBCommandsEvent event)
	{
		if (event.isDedicatedServer())
		{
			event.add(new CmdShutdown());
		}

		if (FTBUConfig.commands.inv)
		{
			event.add(new CmdInv());
		}

		if (FTBUConfig.commands.warp)
		{
			event.add(new CmdWarp());
			event.add(new CmdSetWarp());
			event.add(new CmdDelWarp());
		}

		if (FTBUConfig.backups.enabled)
		{
			event.add(new CmdBackup());
		}

		if (FTBUConfig.commands.home)
		{
			event.add(new CmdHome());
			event.add(new CmdSetHome());
			event.add(new CmdDelHome());
		}

		if (FTBUConfig.commands.tpl)
		{
			event.add(new CmdTplast());
		}

		if (FTBUConfig.commands.trash_can)
		{
			event.add(new CmdTrashCan());
		}

		if (FTBUConfig.commands.back)
		{
			event.add(new CmdBack());
		}

		if (FTBUConfig.commands.spawn)
		{
			event.add(new CmdSpawn());
		}

		if (FTBUConfig.commands.chunks)
		{
			event.add(new CmdChunks());
		}

		if (FTBUConfig.commands.kickme)
		{
			event.add(new CmdKickme());
		}

		if (FTBUConfig.commands.ranks)
		{
			event.add(new CmdRanks());
		}

		if (FTBUConfig.commands.heal)
		{
			event.add(new CmdHeal());
		}

		if (FTBUConfig.commands.set_hour)
		{
			event.add(new CmdSetHour());
		}

		if (FTBUConfig.commands.killall)
		{
			event.add(new CmdKillall());
		}

		if (FTBUConfig.commands.nbtedit)
		{
			event.add(new CmdEditNBT());
		}

		if (FTBUConfig.commands.view_crash)
		{
			event.add(new CmdViewCrash());
		}

		if (FTBUConfig.commands.fly)
		{
			event.add(new CmdFly());
		}

		if (FTBUConfig.commands.leaderboard)
		{
			event.add(new CmdLeaderboard());
		}
	}
}