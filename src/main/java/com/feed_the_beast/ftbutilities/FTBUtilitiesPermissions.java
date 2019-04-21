package com.feed_the_beast.ftbutilities;

import com.feed_the_beast.ftblib.events.RegisterRankConfigEvent;
import com.feed_the_beast.ftblib.events.RegisterRankConfigHandlerEvent;
import com.feed_the_beast.ftblib.lib.config.ConfigEnum;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.config.ConfigTimer;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftblib.lib.util.text_components.TextComponentParser;
import com.feed_the_beast.ftbutilities.data.Leaderboard;
import com.feed_the_beast.ftbutilities.events.CustomPermissionPrefixesRegistryEvent;
import com.feed_the_beast.ftbutilities.ranks.FTBUtilitiesPermissionHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.HashSet;

/**
 * @author LatvianModder
 */
@Mod.EventBusSubscriber(modid = FTBUtilities.MOD_ID)
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

	public static final String HOMES_BACK = "ftbutilities.back.home";
	public static final String WARPS_BACK = "ftbutilities.back.warp";
	public static final String SPAWN_BACK = "ftbutilities.back.spawn";
	public static final String TPA_BACK = "ftbutilities.back.tpa";
	public static final String RTP_BACK = "ftbutilities.back.rtp";
	public static final String RESPAWN_BACK = "ftbutilities.back.respawn";
	public static final String BACK_BACK = "ftbutilities.back.back";

	// Claims //
	public static final String CLAIMS_OTHER_SEE_INFO = "ftbutilities.other_player.claims.see_info";
	public static final String CLAIMS_OTHER_CLAIM = "ftbutilities.other_player.claims.claim";
	public static final String CLAIMS_OTHER_UNCLAIM = "ftbutilities.other_player.claims.unclaim";
	public static final String CLAIMS_OTHER_LOAD = "ftbutilities.other_player.claims.load";
	public static final String CLAIMS_OTHER_UNLOAD = "ftbutilities.other_player.claims.unload";
	public static final Node CLAIMS_MAX_CHUNKS = Node.get("ftbutilities.claims.max_chunks");
	public static final Node CLAIMS_BLOCK_EDIT_PREFIX = Node.get("ftbutilities.claims.block.edit");
	public static final Node CLAIMS_BLOCK_INTERACT_PREFIX = Node.get("ftbutilities.claims.block.interact");
	public static final Node CLAIMS_ITEM_PREFIX = Node.get("ftbutilities.claims.item");
	public static final String CLAIMS_BYPASS_LIMITS = "ftbutilities.claims.bypass_limits";
	public static final String CLAIMS_ATTACK_ANIMALS = "ftbutilities.claims.attack_animals";

	public static final HashSet<Block> CLAIMS_BLOCK_EDIT_WHITELIST = new HashSet<>();
	public static final HashSet<Block> CLAIMS_BLOCK_INTERACT_WHITELIST = new HashSet<>();
	public static final HashSet<Item> CLAIMS_ITEM_BLACKLIST = new HashSet<>();

	// Chunkloader //
	public static final Node CHUNKLOADER_MAX_CHUNKS = Node.get("ftbutilities.chunkloader.max_chunks");
	//public static final String CHUNKLOADER_OFFLINE_TIMER = FTBUtilities.MOD_ID + ".chunkloader.offline_timer";
	public static final String CHUNKLOADER_LOAD_OFFLINE = "ftbutilities.chunkloader.load_offline";

	// Chat //
	public static final String CHAT_SPEAK = "ftbutilities.chat.speak";
	public static final String CHAT_FORMATTING = "ftbutilities.chat.formatting";
	public static final String CHAT_NICKNAME_SET = "ftbutilities.chat.nickname.set";
	public static final String CHAT_NICKNAME_COLORS = "ftbutilities.chat.nickname.colors";
	public static final Node CHAT_NAME_FORMAT = Node.get("ftbutilities.chat.name_format");
	public static final Node CHAT_TEXT_COLOR = Node.get("ftbutilities.chat.text.color");
	public static final Node CHAT_TEXT_BOLD = Node.get("ftbutilities.chat.text.bold");
	public static final Node CHAT_TEXT_ITALIC = Node.get("ftbutilities.chat.text.italic");
	public static final Node CHAT_TEXT_UNDERLINED = Node.get("ftbutilities.chat.text.underlined");
	public static final Node CHAT_TEXT_STRIKETHROUGH = Node.get("ftbutilities.chat.text.strikethrough");
	public static final Node CHAT_TEXT_OBFUSCATED = Node.get("ftbutilities.chat.text.obfuscated");

	// Other //
	public static final String INFINITE_BACK_USAGE = "ftbutilities.back.infinite";
	public static final String CRASH_REPORTS_VIEW = "admin_panel.ftbutilities.crash_reports.view";
	public static final String CRASH_REPORTS_DELETE = "admin_panel.ftbutilities.crash_reports.delete";
	private static final String LEADERBOARD_PREFIX = "ftbutilities.leaderboard.";
	public static final String EDIT_WORLD_GAMERULES = "admin_panel.ftbutilities.edit_world.gamerules";
	public static final String RANKS_VIEW = "admin_panel.ftbutilities.ranks.view";

	public static final Node TPA_COOLDOWN = Node.get("ftbutilities.tpa.cooldown");
	public static final Node SPAWN_COOLDOWN = Node.get("ftbutilities.spawn.cooldown");
	public static final Node BACK_COOLDOWN = Node.get("ftbutilities.back.cooldown");
	public static final Node RTP_COOLDOWN = Node.get("ftbutilities.rtp.cooldown");

	public static final Node TPA_WARMUP = Node.get("ftbutilities.tpa.warmup");
	public static final Node SPAWN_WARMUP = Node.get("ftbutilities.spawn.warmup");
	public static final Node BACK_WARMUP = Node.get("ftbutilities.back.warmup");
	public static final Node RTP_WARMUP = Node.get("ftbutilities.rtp.warmup");

	public static final String TPA_CROSS_DIM = "ftbutilities.tpa.cross_dim";
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

	public static void registerPermissions()
	{
		PermissionAPI.registerNode(CHAT_SPEAK, DefaultPermissionLevel.ALL, "Controls if player is muted or not");
		PermissionAPI.registerNode(CHAT_FORMATTING, DefaultPermissionLevel.ALL, "Allows to use **bold**, *italic* and ~~striketrough~~ in chat");
		PermissionAPI.registerNode(CHAT_NICKNAME_SET, DefaultPermissionLevel.OP, "Allow to change nickname");
		PermissionAPI.registerNode(CHAT_NICKNAME_COLORS, DefaultPermissionLevel.OP, "Allow to use formatting codes in nickname, requires " + CHAT_NICKNAME_SET);
		PermissionAPI.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
		PermissionAPI.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
		PermissionAPI.registerNode(HOMES_LIST_OTHER, DefaultPermissionLevel.OP, "Allow to list other people homes");
		PermissionAPI.registerNode(HOMES_TELEPORT_OTHER, DefaultPermissionLevel.OP, "Allow to teleport to other people homes");
		PermissionAPI.registerNode(CLAIMS_OTHER_SEE_INFO, DefaultPermissionLevel.OP, "Allow player to see info of other team chunks");
		PermissionAPI.registerNode(CLAIMS_OTHER_CLAIM, DefaultPermissionLevel.OP, "Allow player to claim other team chunks");
		PermissionAPI.registerNode(CLAIMS_OTHER_UNCLAIM, DefaultPermissionLevel.OP, "Allow player to unclaim other team chunks");
		PermissionAPI.registerNode(CLAIMS_OTHER_LOAD, DefaultPermissionLevel.OP, "Allow player to load other team chunks");
		PermissionAPI.registerNode(CLAIMS_OTHER_UNLOAD, DefaultPermissionLevel.OP, "Allow player to unload other team chunks");
		PermissionAPI.registerNode(CLAIMS_BYPASS_LIMITS, DefaultPermissionLevel.NONE, "Allow to bypass claiming and loading limits");
		PermissionAPI.registerNode(CLAIMS_ATTACK_ANIMALS, DefaultPermissionLevel.OP, "Allow to attack animals in claimed chunks");
		PermissionAPI.registerNode(CHUNKLOADER_LOAD_OFFLINE, DefaultPermissionLevel.ALL, "Keep loaded chunks working when player goes offline");
		PermissionAPI.registerNode(INFINITE_BACK_USAGE, DefaultPermissionLevel.NONE, "Allow to use 'back' command infinite times");
		PermissionAPI.registerNode(CRASH_REPORTS_VIEW, DefaultPermissionLevel.OP, "Allow to view crash reports via Admin Panel");
		PermissionAPI.registerNode(CRASH_REPORTS_DELETE, DefaultPermissionLevel.OP, "Allow to delete crash reports, requires " + CRASH_REPORTS_VIEW);
		PermissionAPI.registerNode(EDIT_WORLD_GAMERULES, DefaultPermissionLevel.OP, "Allow to edit gamerules via Admin Panel");
		PermissionAPI.registerNode(TPA_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /tpa to teleport to/from another dimension");
		PermissionAPI.registerNode(HEAL_OTHER, DefaultPermissionLevel.OP, "Allow to heal other players");
		PermissionAPI.registerNode(HOMES_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /home is used");
		PermissionAPI.registerNode(WARPS_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /warp is used");
		PermissionAPI.registerNode(BACK_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /back is used");
		PermissionAPI.registerNode(SPAWN_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /spawn is used");
		PermissionAPI.registerNode(TPA_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /tpa is used");
		PermissionAPI.registerNode(RTP_BACK, DefaultPermissionLevel.OP, "Allow player back to last time where /rtp is used");
		PermissionAPI.registerNode(RESPAWN_BACK, DefaultPermissionLevel.ALL, "Allow player back to last death point");

		for (Block block : Block.REGISTRY)
		{
			String name = formatId(block);

			if (name.endsWith(".grave") || name.endsWith(".gravestone"))
			{
				CLAIMS_BLOCK_EDIT_WHITELIST.add(block);
			}

			if (block instanceof BlockDoor || block instanceof BlockWorkbench || block instanceof BlockAnvil)
			{
				CLAIMS_BLOCK_INTERACT_WHITELIST.add(block);
			}
		}

		for (Item item : Item.REGISTRY)
		{
			if (item instanceof ItemBucket)
			{
				CLAIMS_ITEM_BLACKLIST.add(item);
			}
		}

		CLAIMS_ITEM_BLACKLIST.add(Items.END_CRYSTAL);

		for (Block block : Block.REGISTRY)
		{
			String name = formatId(block);
			PermissionAPI.registerNode(CLAIMS_BLOCK_EDIT_PREFIX.append(name).toString(), CLAIMS_BLOCK_EDIT_WHITELIST.contains(block) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP, "");
			PermissionAPI.registerNode(CLAIMS_BLOCK_INTERACT_PREFIX.append(name).toString(), CLAIMS_BLOCK_INTERACT_WHITELIST.contains(block) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP, "");
		}

		for (Item item : Item.REGISTRY)
		{
			PermissionAPI.registerNode(CLAIMS_ITEM_PREFIX.append(formatId(item)).toString(), CLAIMS_ITEM_BLACKLIST.contains(item) ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL, "");
		}

		for (Leaderboard leaderboard : FTBUtilitiesCommon.LEADERBOARDS.values())
		{
			PermissionAPI.registerNode(getLeaderboardNode(leaderboard), DefaultPermissionLevel.ALL, "");
		}
	}

	@SubscribeEvent
	public static void registerConfigs(RegisterRankConfigEvent event)
	{
		event.register(CHAT_NAME_FORMAT, new ConfigString("<{name}>"), new ConfigString("<&2{name}&r>"));
		event.register(CHAT_TEXT_COLOR, new ConfigEnum<>(TextComponentParser.TEXT_FORMATTING_COLORS_NAME_MAP));
		event.register(BADGE, new ConfigString(""));
		event.register(HOMES_MAX, new ConfigInt(1, 0, 30000), new ConfigInt(100));
		event.register(HOMES_COOLDOWN, new ConfigTimer(Ticks.MINUTE.x(5)), new ConfigTimer(Ticks.NO_TICKS));
		event.register(WARPS_COOLDOWN, new ConfigTimer(Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(TPA_COOLDOWN, new ConfigTimer(Ticks.MINUTE.x(3)), new ConfigTimer(Ticks.NO_TICKS));
		event.register(SPAWN_COOLDOWN, new ConfigTimer(Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(BACK_COOLDOWN, new ConfigTimer(Ticks.MINUTE.x(3)), new ConfigTimer(Ticks.NO_TICKS));
		event.register(RTP_COOLDOWN, new ConfigTimer(Ticks.MINUTE.x(10)), new ConfigTimer(Ticks.NO_TICKS));
		event.register(HOMES_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(WARPS_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(TPA_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(SPAWN_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(BACK_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(RTP_WARMUP, new ConfigTimer(Ticks.SECOND.x(5), Ticks.MINUTE), new ConfigTimer(Ticks.NO_TICKS));
		event.register(CLAIMS_MAX_CHUNKS, new ConfigInt(100, 0, 30000), new ConfigInt(1000));
		event.register(CHUNKLOADER_MAX_CHUNKS, new ConfigInt(50, 0, 30000), new ConfigInt(64));
		//event.register(CHUNKLOADER_OFFLINE_TIMER, new ConfigDouble(-1D).setMin(-1D), new ConfigDouble(-1D));
		event.register(AFK_TIMER, new ConfigTimer(Ticks.NO_TICKS));
	}

	@SubscribeEvent
	public static void registerCustomPermissionPrefixes(CustomPermissionPrefixesRegistryEvent event)
	{
		event.register(Node.COMMAND, DefaultPermissionLevel.OP, "Permission for commands, if FTBUtilities command overriding is enabled. If not, this node will be inactive");
		event.register(CLAIMS_BLOCK_EDIT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break and place within claimed chunks");
		event.register(CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can right-click within claimed chunks");
		event.register(CLAIMS_ITEM_PREFIX, DefaultPermissionLevel.ALL, "Permission for items that players can right-click in air within claimed chunks");
		event.register(Node.get(LEADERBOARD_PREFIX), DefaultPermissionLevel.ALL, "Permission for leaderboards that players can view");
	}

	public static String formatId(@Nullable IForgeRegistryEntry item)
	{
		return (item == null || item.getRegistryName() == null) ? "minecraft.air" : item.getRegistryName().toString().toLowerCase().replace(':', '.');
	}

	public static boolean hasBlockEditingPermission(EntityPlayer player, Block block)
	{
		return PermissionAPI.hasPermission(player, CLAIMS_BLOCK_EDIT_PREFIX.append(formatId(block)).toString());
	}

	public static boolean hasBlockInteractionPermission(EntityPlayer player, Block block)
	{
		return PermissionAPI.hasPermission(player, CLAIMS_BLOCK_INTERACT_PREFIX.append(formatId(block)).toString());
	}

	public static boolean hasItemUsePermission(EntityPlayer player, Item block)
	{
		return PermissionAPI.hasPermission(player, CLAIMS_ITEM_PREFIX.append(formatId(block)).toString());
	}

	public static String getLeaderboardNode(Leaderboard leaderboard)
	{
		return LEADERBOARD_PREFIX + leaderboard.id.getNamespace() + "." + leaderboard.id.getPath();
	}
}