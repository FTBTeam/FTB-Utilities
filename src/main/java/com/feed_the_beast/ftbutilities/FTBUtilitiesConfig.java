package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.lib.config.EnumTristate;
import com.feed_the_beast.ftblib.lib.io.DataReader;
import com.feed_the_beast.ftblib.lib.item.ItemStackSerializer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.JsonUtils;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.data.ClaimedChunks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
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
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
@Config(modid = FTBUtilities.MOD_ID, category = "")
public class FTBUtilitiesConfig
{
	@Config.RequiresWorldRestart
	public static final AutoShutdown auto_shutdown = new AutoShutdown();

	public static final AFK afk = new AFK();

	public static final Chat chat = new Chat();

	public static final BackupsConfig backups = new BackupsConfig();

	@Config.RequiresWorldRestart
	@Config.LangKey("commands")
	public static final Commands commands = new Commands();

	public static final Login login = new Login();

	@Config.RequiresWorldRestart
	public static final RanksConfig ranks = new RanksConfig();

	public static final WorldConfig world = new WorldConfig();

	@Config.LangKey("ftblib.debugging")
	public static final Debugging debugging = new Debugging();

	public static class AutoShutdown
	{
		@Config.LangKey("addServer.resourcePack.enabled")
		@Config.Comment("Enables auto-shutdown.")
		public boolean enabled = false;

		@Config.Comment({
				"Server will automatically shut down after X hours.",
				"Time Format: HH:MM. If the system time matches a value, server will shut down.",
				"It will look for closest value available that is not equal to current time."
		})
		public String[] times = {"04:00", "16:00"};
	}

	public static class AFK
	{
		@Config.LangKey("addServer.resourcePack.enabled")
		@Config.Comment("Enables afk timer.")
		public boolean enabled = true;

		@Config.Comment("Enables afk timer in singleplayer.")
		public boolean enabled_singleplayer = false;

		@Config.Comment({"After how much time it will display notification to all players."})
		public String notification_timer = "5m";

		@Config.Comment("Will print in console when someone goes/comes back from AFK.")
		public boolean log_afk = false;

		private long notificationTimer = -1L;

		public boolean isEnabled(MinecraftServer server)
		{
			return enabled && (enabled_singleplayer || !server.isSinglePlayer());
		}

		public long getNotificationTimer()
		{
			if (notificationTimer < 0L)
			{
				notificationTimer = Ticks.fromString(notification_timer);
			}

			return notificationTimer;
		}
	}

	public static class Chat
	{
		@Config.RangeInt(min = 0, max = 10000000)
		public int general_history_limit = 10000;

		@Config.RangeInt(min = 0, max = 10000000)
		public int team_history_limit = 1000;

		@Config.RangeInt(min = 0, max = 10000000)
		public int admin_history_limit = 10000;
	}

	public static class BackupsConfig
	{
		@Config.LangKey("addServer.resourcePack.enabled")
		@Config.Comment("Enables backups.")
		public boolean enabled = true;

		@Config.Comment("If set to true, no messages will be displayed in chat/status bar.")
		public boolean silent = false;

		@Config.RangeInt(min = 0, max = 32000)
		@Config.Comment({
				"The number of backup files to keep.",
				"More backups = more space used",
				"0 - Infinite"
		})
		public int backups_to_keep = 12;

		@Config.RangeDouble(min = 0.05D, max = 600D)
		@Config.Comment({
				"Timer in hours.",
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

		@Config.Comment("Absolute path to backups folder.")
		public String folder = "";

		@Config.Comment("Prints (current size | total size) when backup is done.")
		public boolean display_file_size = true;

		@Config.Comment("Add extra files that will be placed in backup _extra_/ folder.")
		public String[] extra_files = { };

		@Config.Comment("Maximum total size that is allowed in backups folder. Older backups will be deleted to free space for newer ones.")
		public String max_total_size = "50 GB";

		public long time()
		{
			return Ticks.tms((long) (backup_timer * Ticks.HOUR));
		}

		public long getMaxTotalSize()
		{
			String s = StringUtils.removeAllWhitespace(max_total_size).toUpperCase();

			if (s.endsWith("GB"))
			{
				return Long.parseLong(s.substring(0, s.length() - 2)) * FileUtils.GB;
			}
			else if (s.endsWith("MB"))
			{
				return Long.parseLong(s.substring(0, s.length() - 2)) * FileUtils.MB;
			}
			else if (s.endsWith("KB"))
			{
				return Long.parseLong(s.substring(0, s.length() - 2)) * FileUtils.KB;
			}

			return Long.parseLong(s);
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
		public boolean trash_can = true;
		public boolean chunks = true;
		public boolean kickme = true;
		public boolean ranks = true;
		public boolean heal = true;
		public boolean killall = true;
		public boolean nbtedit = true;
		public boolean fly = true;
		public boolean leaderboard = true;
		public boolean tpa = true;
		public boolean nick = true;
	}

	public static class Login
	{
		@Config.Comment("Enables message of the day.")
		public boolean enable_motd = false;

		@Config.Comment("Enables starting items.")
		public boolean enable_starting_items = false;

		@Config.Comment("Set to false to disable global badges completely, only server-wide badges will be available.")
		public boolean enable_global_badges = true;

		@Config.Comment("Set to false to disable event badges, e.g. Halloween.")
		public boolean enable_event_badges = true;

		@Config.Comment("Message of the day. This will be displayed when player joins the server.")
		public String[] motd = {"\"Hello player!\""};

		private List<ITextComponent> motdComponents = null;
		private List<ItemStack> startingItems = null;

		@Config.Comment({
				"Items to give player when he first joins the server.",
				"Format: '{id:\"ID\",Count:X,Damage:X,tag:{}}'"
		})
		public String[] starting_items = {"{id:\"minecraft:stone_sword\",Count:1,Damage:1,tag:{display:{Name:\"Epic Stone Sword\"}}}"};

		public List<ITextComponent> getMOTD()
		{
			if (motdComponents == null)
			{
				motdComponents = new ArrayList<>();

				if (enable_motd)
				{
					for (String s : motd)
					{
						ITextComponent t = JsonUtils.deserializeTextComponent(DataReader.get(s).safeJson());

						if (t != null)
						{
							motdComponents.add(t);
						}
					}
				}
			}

			return motdComponents;
		}

		public List<ItemStack> getStartingItems()
		{
			if (startingItems == null)
			{
				startingItems = new ArrayList<>();

				if (enable_starting_items)
				{
					for (String s : starting_items)
					{
						try
						{
							ItemStack stack = ItemStackSerializer.parseItem(s);

							if (!stack.isEmpty())
							{
								startingItems.add(stack);
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
			}

			return startingItems;
		}
	}

	public static class RanksConfig
	{
		@Config.LangKey("addServer.resourcePack.enabled")
		@Config.RequiresMcRestart
		@Config.Comment("Enables ranks and adds command.x permissions and allows ranks to control them.")
		public boolean enabled = true;

		@Config.Comment("Adds chat colors/rank-specific syntax.")
		public boolean override_chat = true;

		@Config.Comment("Will crash when something tries to check permissions on client side.")
		public boolean crash_client_side_permissions = false;

		@Config.Comment("Will load config/ftbutilities_ranks.txt instead of local/ftbutilities/ranks.txt.")
		public boolean load_from_config_folder = false;
	}

	public static class WorldConfig
	{
		public static class WorldLogging
		{
			@Config.Comment("Enables world logging.")
			@Config.LangKey("addServer.resourcePack.enabled")
			public boolean enabled = false;

			@Config.Comment("Includes creative players in world logging.")
			public boolean include_creative_players = false;

			@Config.Comment("Includes fake players in world logging.")
			public boolean include_fake_players = false;

			@Config.Comment("Logs block placement.")
			public boolean block_placed = true;

			@Config.Comment("Logs block breaking.")
			public boolean block_broken = true;

			@Config.Comment("Logs item clicking in air.")
			public boolean item_clicked_in_air = true;

			public boolean log(EntityPlayerMP player)
			{
				return enabled && (include_creative_players || !player.capabilities.isCreativeMode) && (include_fake_players || !ServerUtils.isFake(player));
			}
		}

		@Config.Comment("Logs different events in local/ftbutilities/world.log file.")
		public final WorldLogging logging = new WorldLogging();

		@Config.Comment("Enables chunk claiming.")
		@Config.RequiresWorldRestart
		public boolean chunk_claiming = true;

		@Config.Comment("Enables chunk loading. If chunk_claiming is set to false, changing this won't do anything.")
		@Config.RequiresWorldRestart
		public boolean chunk_loading = true;

		@Config.Comment("If set to true, explosions and hostile mobs in spawn area will be disabled, players won't be able to attack each other in spawn area.")
		public boolean safe_spawn = false;

		@Config.Comment("Enable spawn area in singleplayer.")
		public boolean spawn_area_in_sp = false;

		@Config.Comment("Dimensions where chunk claiming isn't allowed.")
		public int[] blocked_claiming_dimensions = { };

		@Config.Comment("If set to DEFAULT, then players can decide their PVP status.")
		@Config.LangKey("player_config.ftbutilities.enable_pvp")
		public EnumTristate enable_pvp = EnumTristate.TRUE;

		@Config.Comment("If set to DEFAULT, then teams can decide their Explosion setting.")
		@Config.LangKey("team_config.ftbutilities.explosions")
		public EnumTristate enable_explosions = EnumTristate.DEFAULT;

		@Config.Comment("Spawn radius. You must set spawn-protection in server.properties file to 0!")
		public int spawn_radius = 0;

		@Config.Comment("Spawn dimension. Overworld by default.")
		public int spawn_dimension = 0;

		@Config.Comment("Unloads erroring chunks if dimension isn't loaded or some other problem occurs.")
		public boolean unload_erroring_chunks = false;

		public boolean allowDimension(int dimension)
		{
			if (!ClaimedChunks.isActive())
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

	public static class Debugging
	{
		@Config.Comment("Print a message in console every time a chunk is forced or unforced. Recommended to be off, because spam.")
		public boolean log_chunkloading = false;
	}

	public static void sync()
	{
		ConfigManager.sync(FTBUtilities.MOD_ID, Config.Type.INSTANCE);
		login.motdComponents = null;
		login.startingItems = null;
		afk.notificationTimer = -1L;
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(FTBUtilities.MOD_ID))
		{
			sync();
		}
	}
}