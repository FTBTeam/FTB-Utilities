package com.feed_the_beast.ftbutilities.integration.aurora;

import dev.latvian.mods.aurora.AuroraHomePageEvent;
import dev.latvian.mods.aurora.AuroraPageEvent;
import dev.latvian.mods.aurora.page.HomePageEntry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author LatvianModder
 */
public class AuroraIntegration
{
	public static void init()
	{
		MinecraftForge.EVENT_BUS.register(AuroraIntegration.class);
	}

	@SubscribeEvent
	public static void onAuroraHomePageEvent(AuroraHomePageEvent event)
	{
		event.add(new HomePageEntry("FTB Utilities", "ftb-utilities", "https://i.imgur.com/OtUBSDo.png")
				.add(new HomePageEntry("Ranks", "ranks", "https://i.imgur.com/3o2sHns.png")
						.add(new HomePageEntry("Permission List", "permissions", "https://i.imgur.com/m8KTq4s.png"))
						.add(new HomePageEntry("Command List", "commands", "https://i.imgur.com/aIuCGYZ.png"))
				)
		);
	}

	@SubscribeEvent
	public static void onAuroraEvent(AuroraPageEvent event)
	{
		if (event.getSplitUri()[0].equals("ftb-utilities"))
		{
			if (event.getSplitUri().length >= 3 && event.getSplitUri()[1].equals("ranks"))
			{
				if (event.getSplitUri()[2].equals("permissions"))
				{
					event.setPage(new PermissionListPage());
				}
				else if (event.getSplitUri()[2].equals("commands"))
				{
					event.setPage(new CommandListPage(event.getAuroraServer().getServer()));
				}
			}

			event.setCanceled(true);
		}
	}
}