package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbl.api.EventHandler;
import com.feed_the_beast.ftbl.api.events.registry.RegisterClientConfigEvent;
import com.feed_the_beast.ftbl.lib.client.ImageProvider;
import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@EventHandler(Side.CLIENT)
@Config(modid = FTBUFinals.MOD_ID + "_client", category = "config", name = "../local/client/" + FTBUFinals.MOD_ID)
public class FTBUClientConfig
{
	@Config.LangKey("ftbl.config.general")
	public static final General general = new General();

	public static class General
	{
		public boolean render_badges = true;
		public boolean journeymap_overlay = true;
	}

	public static void sync()
	{
		ConfigManager.sync(FTBUFinals.MOD_ID + "_client", Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBUFinals.MOD_ID + "_client"))
		{
			sync();
		}
	}

	@SubscribeEvent
	public static void registerClientConfig(RegisterClientConfigEvent event)
	{
		event.register(FTBUFinals.MOD_ID + "_client", FTBUFinals.MOD_NAME, ImageProvider.get(FTBUFinals.MOD_ID + ":textures/logo_guide.png"));
	}
}