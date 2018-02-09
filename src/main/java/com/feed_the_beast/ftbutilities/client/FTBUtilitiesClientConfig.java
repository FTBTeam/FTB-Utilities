package com.feed_the_beast.ftbutilities.client;

import com.feed_the_beast.ftblib.lib.gui.GuiLang;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID, value = Side.CLIENT)
@Config(modid = FTBUtilities.MOD_ID + "_client", category = "", name = "../local/client/" + FTBUtilities.MOD_ID)
public class FTBUtilitiesClientConfig
{
	@Config.LangKey(GuiLang.LANG_GENERAL)
	public static final General general = new General();

	public static class General
	{
		public boolean render_badges = true;
		public boolean journeymap_overlay = true;

		@Config.Comment("Will be called first. If item ID starts with any of these strings, it won't skip this item")
		public String[] scan_items_whitelist = {
		};

		@Config.Comment("Will be called after scan_items_whitelist. If item ID starts with any of these strings, it will skip this item")
		public String[] scan_items_blacklist = {
				"minecraft:arrow",
				"minecraft:tipped_arrow",
				"minecraft:potion",
				"minecraft:splash_potion",
				"minecraft:lingering_potion",
				"minecraft:enchanted_book",
				"minecraft:spawn_egg",
				"chisel:",
				"chiselsandbits:",
				"tconstruct:",
				"silentgems:",
				"tcomplement:",
				"storagedrawers:",
				"storagedrawersextra:",
				"bibliocraft:",
				"forestry:sapling",
				"forestry:leaves",
				"forestry:greenhouse",
				"forestry:can",
				"forestry:ffarm",
				"forestry:fence",
				"forestry:bee",
				"forestry:door",
				"thermalfoundation:armor",
				"ic2:fluid_cell",
				"appliedenergistics2:facade",
				"forge:bucketfilled",
				"actuallyadditions:item_potion_ring",
				"actuallyadditions:potion_ring_advanced_bauble",
				"actuallyadditions:item_potion_ring_advanced",
				"thermaldynamics:cover"
		};
	}

	public static void sync()
	{
		ConfigManager.sync(FTBUtilities.MOD_ID + "_client", Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBUtilities.MOD_ID + "_client"))
		{
			sync();
		}
	}
}