package com.feed_the_beast.ftbutilities.client;

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
@Config(modid = "ftbutilities_client", category = "", name = "../local/client/ftbutilities")
public class FTBUtilitiesClientConfig
{
	@Config.LangKey("stat.generalButton")
	public static final General general = new General();

	public static class General
	{
		@Config.Comment("Render Badges.")
		public boolean render_badges = true;

		@Config.Comment("Enable JourneyMap overlay.")
		public boolean journeymap_overlay = true;

		@Config.Comment("Will be called first. If item ID starts with any of these strings, it won't skip this item.")
		public String[] scan_items_whitelist = {
		};

		@Config.Comment("Will be called after scan_items_whitelist. If item ID starts with any of these strings, it will skip this item.")
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

		@Config.Comment("Show backup completion percentage in corner.")
		public boolean show_backup_progress = true;
	}

	public static void sync()
	{
		ConfigManager.sync("ftbutilities_client", Config.Type.INSTANCE);
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals("ftbutilities_client"))
		{
			sync();
		}
	}
}