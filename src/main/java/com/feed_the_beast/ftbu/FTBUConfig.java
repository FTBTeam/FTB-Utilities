package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.lib.gui.GuiLang;
import com.feed_the_beast.ftbl.lib.item.ItemStackSerializer;
import com.feed_the_beast.ftbl.lib.util.CommonUtils;
import com.feed_the_beast.ftbl.lib.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUFinals.MOD_ID)
@Config(modid = FTBUFinals.MOD_ID, category = "config", name = "../local/ftbu/config")
public class FTBUConfig
{
	@Config.RequiresWorldRestart
	public static final AutoShutdown auto_shutdown = new AutoShutdown();

	public static final ServerInfo server_info = new ServerInfo();
	public static final Chat chat = new Chat();
	public static final Backups backups = new Backups();

	@Config.RequiresWorldRestart
	public static final Commands commands = new Commands();

	public static final Login login = new Login();

	@Config.RequiresWorldRestart
	public static final Ranks ranks = new Ranks();

	public static final World world = new World();

	public static class AutoShutdown
	{
		@Config.LangKey(GuiLang.LANG_ENABLED)
		public boolean enabled = false;

		@Config.Comment({
				"Server will automatically shut down after X hours",
				"Time Format: HH:MM. If the system time matches a value, server will shut down",
				"It will look for closest value available that is not equal to current time"
		})
		public String[] times = {"04:00", "16:00"};
	}

	public static class ServerInfo
	{
		public boolean difficulty = true;

		@Config.LangKey("ftbu.config.login.motd")
		public boolean motd = true;

		//public boolean admin_quick_access = true;
	}

	public static class Chat
	{
		public boolean randomize_colors = true;

		@Config.RangeInt(min = 0, max = 10000000)
		public int general_history_limit = 10000;

		@Config.RangeInt(min = 0, max = 10000000)
		public int team_history_limit = 1000;

		@Config.RangeInt(min = 0, max = 10000000)
		public int admin_history_limit = 1000;
	}

	public static class Backups
	{
		@Config.LangKey(GuiLang.LANG_ENABLED)
		public boolean enabled = true;

		public boolean silent = false;

		@Config.RangeInt(min = 0, max = 32000)
		@Config.Comment({
				"The number of backup files to keep",
				"More backups = more space used",
				"0 - Infinite"
		})
		public int backups_to_keep = 12;

		@Config.RangeDouble(min = 0.05D, max = 600D)
		@Config.Comment({
				"Timer in hours",
				"1.0 - backups every hour",
				"6.0 - backups every 6 hours",
				"0.5 - backups every 30 minutes"
		})
		public double backup_timer = 2D;

		@Config.RangeInt(min = 0, max = 9)
		@Config.Comment({
				"0 - Disabled (output = folders)",
				"1 - Best speed",
				"9 - Smallest file size"
		})
		public int compression_level = 1;

		@Config.Comment("Absolute path to backups folder")
		public String folder = "";

		@Config.Comment("Prints (current size | total size) when backup is done")
		public boolean display_file_size = true;

		@Config.Comment("Run backup in a separated Thread (recommended)")
		public boolean use_separate_thread = true;

		public long ticks()
		{
			return (long) (backup_timer * CommonUtils.TICKS_HOUR);
		}
	}

	public static class Commands
	{
		public boolean warp = true;
		public boolean home = true;
		public boolean back = true;
		public boolean spawn = true;
		public boolean inv = true;
		public boolean tpl = true;
		public boolean server_info = true;
		public boolean trash_can = true;
		public boolean chunks = true;
		public boolean kickme = true;
		public boolean ranks = true;
		public boolean view_crash = true;
		public boolean heal = true;
		public boolean set_hour = true;
		public boolean killall = true;
		public boolean nbtedit = true;
	}

	public static class Login
	{
		public boolean enable_motd = false;
		public boolean enable_starting_items = false;

		@Config.Comment("Message of the day. This will be displayed when player joins the server")
		public String[] motd = {"\"Hello player!\""};

		private List<ITextComponent> motdComponents = new ArrayList<>();
		private List<ItemStack> startingItems = new ArrayList<>();

		@Config.Comment({
				"Items to give player when he first joins the server",
				"Format: '{id:\"ID\",Count:X,Damage:X,tag:{}'"
		})
		public String[] starting_items = {"{id:\"minecraft:stone_sword\",Count:1,Damage:1,tag:{display:{Name:\"Epic Stone Sword\"}}}"};

		public List<ITextComponent> getMOTD()
		{
			return motdComponents;
		}

		public List<ItemStack> getStartingItems()
		{
			return startingItems;
		}
	}

	public static class Ranks
	{
		@Config.LangKey(GuiLang.LANG_ENABLED)
		@Config.RequiresMcRestart
		public boolean enabled = true;

		public boolean override_chat = true;
		public boolean override_commands = true;
	}

	public static class World
	{
		public boolean chunk_claiming = true;
		public boolean chunk_loading = true;

		@Config.Comment("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area")
		public boolean safe_spawn = false;

		@Config.Comment("Enable spawn area in singleplayer")
		public boolean spawn_area_in_sp = false;

		@Config.Comment("Print a message in console every time a chunk is forced or unforced. Recommended to be off, because spam")
		public boolean log_chunkloading = false;

		@Config.Comment("Dimensions where chunk claiming isn't allowed")
		public int[] blocked_claiming_dimensions = { };

		public boolean allowDimension(int dimension)
		{
			if (!FTBUConfig.world.chunk_claiming)
			{
				return false;
			}

			if (blocked_claiming_dimensions.length > 0)
			{
				for (int i : blocked_claiming_dimensions)
				{
					if (i == dimension)
					{
						return false;
					}
				}
			}

			return true;
		}
	}

	public static void sync()
	{
		ConfigManager.sync(FTBUFinals.MOD_ID, Config.Type.INSTANCE);
		login.motdComponents.clear();
		login.startingItems.clear();

		if (login.enable_motd)
		{
			for (String s : login.motd)
			{
				ITextComponent t = JsonUtils.deserializeTextComponent(JsonUtils.fromJson(s));

				if (t != null)
				{
					login.motdComponents.add(t);
				}
			}
		}

		if (login.enable_starting_items)
		{
			for (String s : login.starting_items)
			{
				try
				{
					ItemStack stack = ItemStackSerializer.parseItem(s);

					if (!stack.isEmpty())
					{
						login.startingItems.add(stack);
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBUFinals.MOD_ID))
		{
			sync();
		}
	}
}