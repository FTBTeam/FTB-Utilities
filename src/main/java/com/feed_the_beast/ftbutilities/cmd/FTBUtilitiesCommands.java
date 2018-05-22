package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.FTBLibConfig;
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
import com.feed_the_beast.ftbutilities.cmd.tp.CmdTPA;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdTPAccept;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdTplast;
import com.feed_the_beast.ftbutilities.cmd.tp.CmdWarp;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUtilitiesCommands
{
	public static void registerCommands(FMLServerStartingEvent event)
	{
		if (event.getServer().isDedicatedServer())
		{
			event.registerServerCommand(new CmdShutdown());
		}

		if (FTBUtilitiesConfig.commands.inv)
		{
			event.registerServerCommand(new CmdInv());
		}

		if (FTBUtilitiesConfig.commands.warp)
		{
			event.registerServerCommand(new CmdWarp());
			event.registerServerCommand(new CmdSetWarp());
			event.registerServerCommand(new CmdDelWarp());
		}

		if (FTBUtilitiesConfig.backups.enabled)
		{
			event.registerServerCommand(new CmdBackup());
		}

		if (FTBUtilitiesConfig.commands.home)
		{
			event.registerServerCommand(new CmdHome());
			event.registerServerCommand(new CmdSetHome());
			event.registerServerCommand(new CmdDelHome());
		}

		if (FTBUtilitiesConfig.commands.tpl)
		{
			event.registerServerCommand(new CmdTplast());
		}

		if (FTBUtilitiesConfig.commands.trash_can)
		{
			event.registerServerCommand(new CmdTrashCan());
		}

		if (FTBUtilitiesConfig.commands.back)
		{
			event.registerServerCommand(new CmdBack());
		}

		if (FTBUtilitiesConfig.commands.spawn)
		{
			event.registerServerCommand(new CmdSpawn());
		}

		if (FTBUtilitiesConfig.commands.chunks)
		{
			event.registerServerCommand(new CmdChunks());
		}

		if (FTBUtilitiesConfig.commands.kickme)
		{
			event.registerServerCommand(new CmdKickme());
		}

		if (FTBUtilitiesConfig.commands.ranks)
		{
			event.registerServerCommand(new CmdRanks());
		}

		if (FTBUtilitiesConfig.commands.heal)
		{
			event.registerServerCommand(new CmdHeal());
		}

		if (FTBUtilitiesConfig.commands.killall)
		{
			event.registerServerCommand(new CmdKillall());
		}

		if (FTBUtilitiesConfig.commands.nbtedit)
		{
			event.registerServerCommand(new CmdEditNBT());
		}

		if (FTBUtilitiesConfig.commands.fly)
		{
			event.registerServerCommand(new CmdFly());
		}

		if (FTBUtilitiesConfig.commands.leaderboard)
		{
			event.registerServerCommand(new CmdLeaderboard());
		}

		if (FTBLibConfig.debugging.special_commands)
		{
			event.registerServerCommand(new CmdCycleBlockState());
		}

		if (FTBUtilitiesConfig.commands.tpa)
		{
			event.registerServerCommand(new CmdTPA());
			event.registerServerCommand(new CmdTPAccept());
		}

		if (FTBUtilitiesConfig.commands.nick)
		{
			event.registerServerCommand(new CmdNick());
		}
	}
}