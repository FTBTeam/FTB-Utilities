package com.feed_the_beast.ftbu.client;

import com.feed_the_beast.ftbu.FTBUFinals;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID, value = Side.CLIENT)
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
}