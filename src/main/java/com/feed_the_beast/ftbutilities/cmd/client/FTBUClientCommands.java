package com.feed_the_beast.ftbutilities.cmd.client;

import com.feed_the_beast.ftblib.FTBLibConfig;
import com.feed_the_beast.ftblib.events.client.RegisterFTBClientCommandsEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
public class FTBUClientCommands
{
	@SubscribeEvent
	public static void registerCommands(RegisterFTBClientCommandsEvent event)
	{
		event.add(new CmdShrug());

		if (FTBLibConfig.debugging.special_commands)
		{
			event.add(new CmdScanItems());
		}
	}
}