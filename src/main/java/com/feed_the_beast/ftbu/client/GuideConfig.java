package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.lib.config.ConfigRGB;
import com.feed_the_beast.ftbl.lib.icon.Color4I;
import com.feed_the_beast.ftbl.lib.internal.FTBLibFinals;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBLibFinals.MOD_ID, value = Side.CLIENT)
@Config(modid = "guide", category = "config", name = "../local/client/guide")
public class GuideConfig
{
	public static final Border border = new Border();

	public static final Colors colors = new Colors();

	public static class Border
	{
		@Config.LangKey("gui.width")
		@Config.RangeInt(min = 0, max = 200)
		public int width = 15;

		@Config.LangKey("gui.height")
		@Config.RangeInt(min = 0, max = 200)
		public int height = 15;
	}

	public static class Colors
	{
		public final ConfigRGB background = new ConfigRGB(0xFFF7F4DA);
		public final ConfigRGB text = new ConfigRGB(0xFF7B6534);

		private Color4I backgroundColor, textColor;

		public Color4I getBackground()
		{
			return backgroundColor;
		}

		public Color4I getText()
		{
			return textColor;
		}
	}

	public static void sync()
	{
		ConfigManager.sync("guide", Config.Type.INSTANCE);
		colors.backgroundColor = colors.background.createColor();
		colors.textColor = colors.text.createColor();
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals("guide"))
		{
			sync();
		}
	}
}