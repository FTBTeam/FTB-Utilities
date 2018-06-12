package com.feed_the_beast.ftbutilities.integration;

import com.feed_the_beast.ftblib.events.client.CustomClickEvent;
import com.feed_the_beast.ftblib.lib.client.ClientUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.gui.GuiClaimedChunks;
import com.feed_the_beast.ftbutilities.net.MessageLeaderboardList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
public class FTBLibClientIntegration
{
	@SubscribeEvent
	public static void onCustomClick(CustomClickEvent event)
	{
		if (event.getID().getResourceDomain().equals(FTBUtilities.MOD_ID))
		{
			switch (event.getID().getResourcePath())
			{
				case "toggle_gamemode":
					ClientUtils.execClientCommand("/gamemode " + (ClientUtils.MC.player.capabilities.isCreativeMode ? "survival" : "creative"));
					break;
				case "daytime":
					ClientUtils.execClientCommand("/time add " + (24000L - (ClientUtils.MC.world.getWorldTime() % 24000L) + 6000));
					break;
				case "nighttime":
					ClientUtils.execClientCommand("/time add " + (24000L - (ClientUtils.MC.world.getWorldTime() % 24000L) + 18000));
					break;
				case "claims_gui":
					GuiClaimedChunks.instance = new GuiClaimedChunks();
					GuiClaimedChunks.instance.openGui();
					break;
				case "leaderboards_gui":
					new MessageLeaderboardList().sendToServer();
					break;
			}

			event.setCanceled(true);
		}
	}
}