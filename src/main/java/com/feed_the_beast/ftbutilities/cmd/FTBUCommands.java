package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.cmd.chunks.CmdChunks;
import com.feed_the_beast.ftbutilities.cmd.ranks.CmdRanks;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdBack;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdDelHome;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdDelWarp;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdHome;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdSetHome;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdSetWarp;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdSpawn;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdTplast;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdWarp;
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

		if (FTBUtilitiesConfig.commands.inv)
		{
			event.add(new CmdInv());
		}

		if (FTBUtilitiesConfig.commands.warp)
		{
			event.add(new CmdWarp());
			event.add(new CmdSetWarp());
			event.add(new CmdDelWarp());
		}

		if (FTBUtilitiesConfig.backups.enabled)
		{
			event.add(new CmdBackup());
		}

		if (FTBUtilitiesConfig.commands.home)
		{
			event.add(new CmdHome());
			event.add(new CmdSetHome());
			event.add(new CmdDelHome());
		}

		if (FTBUtilitiesConfig.commands.tpl)
		{
			event.add(new CmdTplast());
		}

		if (FTBUtilitiesConfig.commands.trash_can)
		{
			event.add(new CmdTrashCan());
		}

		if (FTBUtilitiesConfig.commands.back)
		{
			event.add(new CmdBack());
		}

		if (FTBUtilitiesConfig.commands.spawn)
		{
			event.add(new CmdSpawn());
		}

		if (FTBUtilitiesConfig.commands.chunks)
		{
			event.add(new CmdChunks());
		}

		if (FTBUtilitiesConfig.commands.kickme)
		{
			event.add(new CmdKickme());
		}

		if (FTBUtilitiesConfig.commands.ranks)
		{
			event.add(new CmdRanks());
		}

		if (FTBUtilitiesConfig.commands.heal)
		{
			event.add(new CmdHeal());
		}

		if (FTBUtilitiesConfig.commands.set_hour)
		{
			event.add(new CmdSetHour());
		}

		if (FTBUtilitiesConfig.commands.killall)
		{
			event.add(new CmdKillall());
		}

		if (FTBUtilitiesConfig.commands.nbtedit)
		{
			event.add(new CmdEditNBT());
		}

		if (FTBUtilitiesConfig.commands.view_crash)
		{
			event.add(new CmdViewCrash());
		}

		if (FTBUtilitiesConfig.commands.fly)
		{
			event.add(new CmdFly());
		}

		if (FTBUtilitiesConfig.commands.leaderboard)
		{
			event.add(new CmdLeaderboard());
		}

		if (FTBUtilitiesConfig.commands.cycle_block_state)
		{
			event.add(new CmdCycleBlockState());
		}
	}
}