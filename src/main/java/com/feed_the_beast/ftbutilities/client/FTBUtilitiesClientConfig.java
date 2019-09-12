package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import net.minecraftforge.common.config.Config;

/**
 * @author LatvianModder
 */
@Config(modid = FTBUtilities.MOD_ID, category = "", name = "../local/client/" + FTBUtilities.MOD_ID)
@Config.LangKey(FTBUtilities.MOD_ID + "_client")
public class FTBUtilitiesClientConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Render Badges.")
		public boolean render_badges = true;

		@Config.Comment("Enable JourneyMap overlay. Requires a restart to work.")
		public boolean journeymap_overlay = false;

		@Config.Comment("Show when server will shut down in corner.")
		public boolean show_shutdown_timer = true;

		@Config.Comment("When will it start to show the shutdown timer.")
		public String shutdown_timer_start = "1m";

		private long show_shutdown_timer_ms = -1L;

		public long getShowShutdownTimer()
		{
			if (show_shutdown_timer_ms == -1L)
			{
				show_shutdown_timer_ms = Ticks.get(shutdown_timer_start).millis();
			}

			return show_shutdown_timer_ms;
		}

		@Config.RangeInt(min = 0, max = 23999)
		public int button_daytime = 6000;

		@Config.RangeInt(min = 0, max = 23999)
		public int button_nighttime = 18000;
	}

	public static void sync()
	{
		general.show_shutdown_timer_ms = -1L;
	}
}