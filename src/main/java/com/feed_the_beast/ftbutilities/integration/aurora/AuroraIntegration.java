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
		event.add(new HomePageEntry("FTB Utilities", "ftb-utilities", "https://i.imgur.com/SDV8WV5.png")
				.add(new HomePageEntry("Ranks", "ranks", "https://i.imgur.com/3o2sHns.png")
						.add(new HomePageEntry("Permission List", "permissions", "https://i.imgur.com/m8KTq4s.png"))
						.add(new HomePageEntry("Command List", "commands", "https://i.imgur.com/aIuCGYZ.png"))
				)
		);
	}

	@SubscribeEvent
	public static void onAuroraEvent(AuroraPageEvent event)
	{
		if (event.checkPath("ftb-utilities", "ranks", "permissions"))
		{
			event.returnPage(new PermissionListPage());
		}
		else if (event.checkPath("ftb-utilities", "ranks", "commands"))
		{
			event.returnPage(new CommandListPage(event.getAuroraServer().getServer()));
		}
		else if (event.checkPath("ftb-utilities", "ranks"))
		{
			event.returnPage(new RankPage());
		}
	}
}