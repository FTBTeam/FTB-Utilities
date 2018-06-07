package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.events.RegisterPermissionsEvent;
import com.feed_the_beast.ftblib.events.RegisterRankConfigEvent;
import com.feed_the_beast.ftblib.events.RegisterRankConfigHandlerEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.ConfigTimer;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.data.BlockInteractionType;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.events.CustomPermissionPrefixesRegistryEvent;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import com.feed_the_beast.ftbutilities.ranks.Rank;
import com.google.gson.JsonElement;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.BlockPosContext;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUtilitiesPermissions
{
	// Display //
	public static final String DISPLAY_ADMIN_INFO = "ftbutilities.display.admin_info";
	public static final Node BADGE = Node.get("ftbutilities.badge");

	// Homes //
	public static final String HOMES_CROSS_DIM = "ftbutilities.homes.cross_dim";
	public static final Node HOMES_MAX = Node.get("ftbutilities.homes.max");
	public static final Node HOMES_COOLDOWN = Node.get("ftbutilities.homes.cooldown");
	public static final Node HOMES_WARMUP = Node.get("ftbutilities.homes.warmup");
	public static final String HOMES_LIST_OTHER = "ftbutilities.other_player.homes.list";
	public static final String HOMES_TELEPORT_OTHER = "ftbutilities.other_player.homes.teleport";

	// Warps //
	public static final Node WARPS_COOLDOWN = Node.get("ftbutilities.warps.cooldown");
	public static final Node WARPS_WARMUP = Node.get("ftbutilities.warps.warmup");

	// Claims //
	public static final String CLAIMS_OTHER_SEE_INFO = "ftbutilities.other_player.claims.see_info";
	public static final String CLAIMS_OTHER_CLAIM = "ftbutilities.other_player.claims.claim";
	public static final String CLAIMS_OTHER_UNCLAIM = "ftbutilities.other_player.claims.unclaim";
	public static final String CLAIMS_OTHER_LOAD = "ftbutilities.other_player.claims.load";
	public static final String CLAIMS_OTHER_UNLOAD = "ftbutilities.other_player.claims.unload";
	public static final Node CLAIMS_MAX_CHUNKS = Node.get("ftbutilities.claims.max_chunks");
	public static final String CLAIMS_BLOCK_CNB = "ftbutilities.claims.block.cnb";
	private static final String CLAIMS_BLOCK_EDIT_PREFIX = "ftbutilities.claims.block.edit.";
	private static final String CLAIMS_BLOCK_INTERACT_PREFIX = "ftbutilities.claims.block.interact.";
	private static final String CLAIMS_ITEM_PREFIX = "ftbutilities.claims.item.";

	// Chunkloader //
	public static final Node CHUNKLOADER_MAX_CHUNKS = Node.get("ftbutilities.chunkloader.max_chunks");
	//public static final String CHUNKLOADER_OFFLINE_TIMER = FTBUtilities.MOD_ID + ".chunkloader.offline_timer";
	public static final String CHUNKLOADER_LOAD_OFFLINE = "ftbutilities.chunkloader.load_offline";

	public static class ChatPart
	{
		public final Node color, text, bold, italic, underlined, strikethrough, obfuscated;

		public ChatPart(String s)
		{
			Node node = CHAT.append(s);
			color = node.append("color");
			text = node.append("text");
			bold = node.append("bold");
			italic = node.append("italic");
			underlined = node.append("underlined");
			strikethrough = node.append("strikethrough");
			obfuscated = node.append("obfuscated");
		}

		public ITextComponent format(Rank rank, ITextComponent component, @Nullable ChatPart alt)
		{
			JsonElement json;
			TextFormatting colortf = TextFormatting.WHITE;

			if (alt != null)
			{
				json = rank.getConfigRaw(alt.color);

				if (json.isJsonPrimitive())
				{
					colortf = StringUtils.TEXT_FORMATTING_COLORS_NAME_MAP.get(json.getAsString());
				}
			}

			json = rank.getConfigRaw(color);

			if (json.isJsonPrimitive())
			{
				colortf = StringUtils.TEXT_FORMATTING_COLORS_NAME_MAP.get(json.getAsString());
			}

			if (colortf != TextFormatting.WHITE)
			{
				component.getStyle().setColor(colortf);
			}

			if (getStyleBoolean(rank, bold, alt == null ? null : alt.bold))
			{
				component.getStyle().setBold(true);
			}

			if (getStyleBoolean(rank, italic, alt == null ? null : alt.italic))
			{
				component.getStyle().setItalic(true);
			}

			if (getStyleBoolean(rank, underlined, alt == null ? null : alt.underlined))
			{
				component.getStyle().setUnderlined(true);
			}

			if (getStyleBoolean(rank, strikethrough, alt == null ? null : alt.strikethrough))
			{
				component.getStyle().setStrikethrough(true);
			}

			if (getStyleBoolean(rank, obfuscated, alt == null ? null : alt.obfuscated))
			{
				component.getStyle().setObfuscated(true);
			}

			return component;
		}

		private boolean getStyleBoolean(Rank rank, Node main, @Nullable Node alt)
		{
			Event.Result result = rank.getPermissionRaw(main, false);

			if (result == Event.Result.DEFAULT && alt != null)
			{
				result = rank.getPermissionRaw(alt, false);
			}

			return result == Event.Result.ALLOW;
		}
	}

	// Chat //
	public static final Node CHAT = Node.get("ftbutilities.chat");
	public static final Node CHAT_PREFIX_PART_COUNT = CHAT.append("prefix.part_count");
	public static final ChatPart CHAT_PREFIX = new ChatPart("prefix");
	public static final ChatPart CHAT_NAME = new ChatPart("name");
	public static final Node CHAT_SUFFIX_PART_COUNT = CHAT.append("suffix.part_count");
	public static final ChatPart CHAT_SUFFIX = new ChatPart("suffix");
	public static final ChatPart CHAT_TEXT = new ChatPart("text");

	// Other //
	public static final String INFINITE_BACK_USAGE = "ftbutilities.back.infinite";
	public static final String VIEW_CRASH_REPORTS = "admin_panel.ftbutilities.crash_reports.view";
	public static final String DELETE_CRASH_REPORTS = "admin_panel.ftbutilities.crash_reports.delete";
	private static final String LEADERBOARD_PREFIX = "ftbutilities.leaderboard.";
	public static final String EDIT_WORLD_GAMERULES = "admin_panel.ftbutilities.edit_world.gamerules";
	public static final Node TPA_COOLDOWN = Node.get("ftbutilities.tpa.cooldown");
	public static final Node SPAWN_COOLDOWN = Node.get("ftbutilities.spawn.cooldown");
	public static final Node BACK_COOLDOWN = Node.get("ftbutilities.back.cooldown");
	public static final Node TPA_WARMUP = Node.get("ftbutilities.tpa.warmup");
	public static final Node SPAWN_WARMUP = Node.get("ftbutilities.spawn.warmup");
	public static final Node BACK_WARMUP = Node.get("ftbutilities.back.warmup");
	public static final String TPA_CROSS_DIM = "ftbutilities.tpa.cross_dim";
	public static final String NICKNAME_SET = "ftbutilities.nickname.set";
	public static final String NICKNAME_COLORS = "ftbutilities.nickname.colors";
	public static final Node AFK_TIMER = Node.get("ftbutilities.afk.timer");
	public static final String HEAL_OTHER = "ftbutilities.other_player.heal";

	@SubscribeEvent
	public static void registerRankConfigHandler(RegisterRankConfigHandlerEvent event)
	{
		if (FTBUtilitiesConfig.ranks.enabled)
		{
			event.setHandler(FTBUtilitiesPermissionHandler.INSTANCE);
		}
	}

	@SubscribeEvent
	public static void registerPermissions(RegisterPermissionsEvent event)
	{
		event.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
		event.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
		event.registerNode(HOMES_LIST_OTHER, DefaultPermissionLevel.OP, "Allow to list other people homes");
		event.registerNode(HOMES_TELEPORT_OTHER, DefaultPermissionLevel.OP, "Allow to teleport to other people homes");
		event.registerNode(CLAIMS_OTHER_SEE_INFO, DefaultPermissionLevel.OP, "Allow player to see info of other team chunks");
		event.registerNode(CLAIMS_OTHER_CLAIM, DefaultPermissionLevel.OP, "Allow player to claim other team chunks");
		event.registerNode(CLAIMS_OTHER_UNCLAIM, DefaultPermissionLevel.OP, "Allow player to unclaim other team chunks");
		event.registerNode(CLAIMS_OTHER_LOAD, DefaultPermissionLevel.OP, "Allow player to load other team chunks");
		event.registerNode(CLAIMS_OTHER_UNLOAD, DefaultPermissionLevel.OP, "Allow player to unload other team chunks");
		event.registerNode(CLAIMS_BLOCK_CNB, DefaultPermissionLevel.OP, "Allow to edit C&B bits in claimed chunks");
		event.registerNode(CHUNKLOADER_LOAD_OFFLINE, DefaultPermissionLevel.ALL, "Keep loaded chunks working when player goes offline");
		event.registerNode(INFINITE_BACK_USAGE, DefaultPermissionLevel.NONE, "Allow to use 'back' command infinite times");
		event.registerNode(VIEW_CRASH_REPORTS, DefaultPermissionLevel.OP, "Allow to view crash reports via Admin Panel");
		event.registerNode(DELETE_CRASH_REPORTS, DefaultPermissionLevel.OP, "Allow to delete crash reports, requires " + VIEW_CRASH_REPORTS);
		event.registerNode(EDIT_WORLD_GAMERULES, DefaultPermissionLevel.OP, "Allow to edit gamerules via Admin Panel");
		event.registerNode(TPA_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /tpa to teleport to/from another dimension");
		event.registerNode(NICKNAME_SET, DefaultPermissionLevel.OP, "Allow to change nickname");
		event.registerNode(NICKNAME_COLORS, DefaultPermissionLevel.OP, "Allow to use formatting codes in nickname, requires " + NICKNAME_SET);

		for (Block block : Block.REGISTRY)
		{
			String name = formatId(block);
			event.registerNode(CLAIMS_BLOCK_EDIT_PREFIX + name, (name.startsWith("graves.") || name.startsWith("gravestone.")) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
			event.registerNode(CLAIMS_BLOCK_INTERACT_PREFIX + name, (block instanceof BlockDoor || block instanceof BlockWorkbench || block instanceof BlockAnvil) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
		}

		for (Item item : Item.REGISTRY)
		{
			String name = formatId(item);
			event.registerNode(CLAIMS_ITEM_PREFIX + name, (item instanceof ItemBucket) ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL);
		}

		event.registerNode(CLAIMS_BLOCK_EDIT_PREFIX + "gravestone.gravestone", DefaultPermissionLevel.ALL);
		event.registerNode(CLAIMS_BLOCK_EDIT_PREFIX + "openblocks.grave", DefaultPermissionLevel.ALL);
		event.registerNode(CLAIMS_ITEM_PREFIX + formatId(Items.END_CRYSTAL), DefaultPermissionLevel.OP);
		event.registerNode(CLAIMS_ITEM_PREFIX + "forge.bucketfilled", DefaultPermissionLevel.OP);

		for (Leaderboard leaderboard : FTBUtilitiesCommon.LEADERBOARDS.values())
		{
			event.registerNode(getLeaderboardNode(leaderboard), DefaultPermissionLevel.ALL);
		}
	}

	@SubscribeEvent
	public static void registerConfigs(RegisterRankConfigEvent event)
	{
		event.register(BADGE, new ConfigString(""));
		event.register(HOMES_MAX, new ConfigInt(1, 0, 30000), new ConfigInt(100));
		event.register(HOMES_COOLDOWN, new ConfigTimer(Ticks.mt(5)), new ConfigTimer(0L));
		event.register(WARPS_COOLDOWN, new ConfigTimer(Ticks.mt(1)), new ConfigTimer(0L));
		event.register(TPA_COOLDOWN, new ConfigTimer(Ticks.mt(3)), new ConfigTimer(0L));
		event.register(SPAWN_COOLDOWN, new ConfigTimer(Ticks.mt(1)), new ConfigTimer(0L));
		event.register(BACK_COOLDOWN, new ConfigTimer(Ticks.mt(3)), new ConfigTimer(0L));
		event.register(HOMES_WARMUP, new ConfigTimer(Ticks.st(5), Ticks.mt(1)), new ConfigTimer(0L));
		event.register(WARPS_WARMUP, new ConfigTimer(Ticks.st(5), Ticks.mt(1)), new ConfigTimer(0L));
		event.register(TPA_WARMUP, new ConfigTimer(Ticks.st(5), Ticks.mt(1)), new ConfigTimer(0L));
		event.register(SPAWN_WARMUP, new ConfigTimer(Ticks.st(5), Ticks.mt(1)), new ConfigTimer(0L));
		event.register(BACK_WARMUP, new ConfigTimer(Ticks.st(5), Ticks.mt(1)), new ConfigTimer(0L));
		event.register(CLAIMS_MAX_CHUNKS, new ConfigInt(100, 0, 30000), new ConfigInt(1000));
		event.register(CHUNKLOADER_MAX_CHUNKS, new ConfigInt(50, 0, 30000), new ConfigInt(64));
		//event.register(CHUNKLOADER_OFFLINE_TIMER, new ConfigDouble(-1D).setMin(-1D), new ConfigDouble(-1D));
		event.register(AFK_TIMER, new ConfigTimer(0));
	}

	@SubscribeEvent
	public static void registerCustomPermissionPrefixes(CustomPermissionPrefixesRegistryEvent event)
	{
		event.register(Node.COMMAND, DefaultPermissionLevel.OP, "Permission for commands, if FTBUtilities command overriding is enabled. If not, this node will be inactive");
		event.register(Node.get(CLAIMS_BLOCK_EDIT_PREFIX), DefaultPermissionLevel.OP, "Permission for blocks that players can break and place within claimed chunks");
		event.register(Node.get(CLAIMS_BLOCK_INTERACT_PREFIX), DefaultPermissionLevel.OP, "Permission for blocks that players can right-click within claimed chunks");
		event.register(Node.get(CLAIMS_ITEM_PREFIX), DefaultPermissionLevel.ALL, "Permission for items that players can right-click in air within claimed chunks");
		event.register(Node.get(LEADERBOARD_PREFIX), DefaultPermissionLevel.ALL, "Permission for leaderboards that players can view");
	}

	private static String formatId(@Nullable IForgeRegistryEntry item)
	{
		return (item == null || item.getRegistryName() == null) ? "minecraft.air" : item.getRegistryName().toString().toLowerCase().replace(':', '.');
	}

	public static boolean canModifyBlock(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		IContext context = new BlockPosContext(player, block.getPos(), block.getState(), null);

		switch (type)
		{
			case EDIT:
				return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_EDIT_PREFIX + formatId(block.getState().getBlock()), context);
			case INTERACT:
				return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_INTERACT_PREFIX + formatId(block.getState().getBlock()), context);
			case CNB_BREAK:
			case CNB_PLACE:
				return PermissionAPI.hasPermission(player.getGameProfile(), FTBUtilitiesPermissions.CLAIMS_BLOCK_CNB, context);
			case ITEM:
				return !player.getHeldItem(hand).isEmpty() || PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_ITEM_PREFIX + formatId(player.getHeldItem(hand).getItem()), context);
			default:
				return false;
		}
	}

	public static String getLeaderboardNode(Leaderboard leaderboard)
	{
		return LEADERBOARD_PREFIX + leaderboard.id.getResourceDomain() + "." + leaderboard.id.getResourcePath();
	}
}