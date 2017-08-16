package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftbl.api.events.RegisterFTBCommandsEvent;
import com.feed_the_beast.ftbu.cmd.chunks.CmdChunks;
import com.feed_the_beast.ftbu.cmd.ranks.CmdRanks;
import com.feed_the_beast.ftbu.cmd.tp.CmdAdminHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdBack;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdDelWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetHome;
import com.feed_the_beast.ftbu.cmd.tp.CmdSetWarp;
import com.feed_the_beast.ftbu.cmd.tp.CmdSpawn;
import com.feed_the_beast.ftbu.cmd.tp.CmdTplast;
import com.feed_the_beast.ftbu.cmd.tp.CmdWarp;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigCommands;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
			event.add(new CmdRestart());
		}

		if (FTBUConfigCommands.INV.getBoolean())
		{
			event.add(new CmdInv());
		}

		if (FTBUConfigCommands.WARP.getBoolean())
		{
			event.add(new CmdWarp());
			event.add(new CmdSetWarp());
			event.add(new CmdDelWarp());
		}

		if (FTBUConfigBackups.ENABLED.getBoolean())
		{
			event.add(new CmdBackup());
		}

		if (FTBUConfigCommands.HOME.getBoolean())
		{
			event.add(new CmdAdminHome());
			event.add(new CmdHome());
			event.add(new CmdSetHome());
			event.add(new CmdDelHome());
		}

		if (FTBUConfigCommands.SERVER_INFO.getBoolean())
		{
			event.add(new CmdServerInfo());
		}

		if (FTBUConfigCommands.LOADED_CHUNKS.getBoolean())
		{
			event.add(new CmdLoadedChunks());
		}

		if (FTBUConfigCommands.TPL.getBoolean())
		{
			event.add(new CmdTplast());
		}

		if (FTBUConfigCommands.TRASH_CAN.getBoolean())
		{
			event.add(new CmdTrashCan());
		}

		if (FTBUConfigCommands.BACK.getBoolean())
		{
			event.add(new CmdBack());
		}

		if (FTBUConfigCommands.SPAWN.getBoolean())
		{
			event.add(new CmdSpawn());
		}

		if (FTBUConfigCommands.CHUNKS.getBoolean())
		{
			event.add(new CmdChunks());
		}

		if (FTBUConfigCommands.KICKME.getBoolean())
		{
			event.add(new CmdKickme());
		}

		if (FTBUConfigCommands.RANKS.getBoolean())
		{
			event.add(new CmdRanks());
		}

		if (FTBUConfigCommands.VIEW_CRASH.getBoolean())
		{
			event.add(new CmdViewCrash());
		}

		if (FTBUConfigCommands.HEAL.getBoolean())
		{
			event.add(new CmdHeal());
		}

		if (FTBUConfigCommands.SET_HOUR.getBoolean())
		{
			event.add(new CmdSetHour());
		}

		if (FTBUConfigCommands.KILLALL.getBoolean())
		{
			event.add(new CmdKillall());
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerClientCommands(RegisterFTBClientCommandsEvent event)
	{
		event.add(new CmdSetbadge());
	}
}