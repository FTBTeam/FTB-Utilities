package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftblib.events.PermissionRegistryEvent;
import com.feed_the_beast.ftblib.events.RegisterRankConfigEvent;
import com.feed_the_beast.ftblib.lib.EventHandler;
import com.feed_the_beast.ftblib.lib.config.ConfigInt;
import com.feed_the_beast.ftblib.lib.config.ConfigString;
import com.feed_the_beast.ftblib.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbu.data.BlockInteractionType;
import com.feed_the_beast.ftbu.data.NodeEntry;
import com.feed_the_beast.ftbu.events.RegisterCustomPermissionPrefixesEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;

/**
 * @author LatvianModder
 */
@EventHandler
public class FTBUPermissions
{
	// Display //
	public static final String DISPLAY_ADMIN_INFO = FTBUFinals.MOD_ID + ".display.admin_info";
	public static final String BADGE = FTBUFinals.MOD_ID + ".badge";

	// Homes //
	public static final String HOMES_CROSS_DIM = FTBUFinals.MOD_ID + ".homes.cross_dim";
	public static final String HOMES_MAX = FTBUFinals.MOD_ID + ".homes.max";
	public static final String HOMES_LIST_OTHER = FTBUFinals.MOD_ID + ".homes.list_other";
	public static final String HOMES_TELEPORT_OTHER = FTBUFinals.MOD_ID + ".homes.teleport_other";

	// Claims //
	public static final String CLAIMS_CHUNKS_MODIFY_OTHERS = FTBUFinals.MOD_ID + ".claims.modify.others";
	public static final String CLAIMS_MAX_CHUNKS = FTBUFinals.MOD_ID + ".claims.max_chunks";
	public static final String CLAIMS_BLOCK_CNB = FTBUFinals.MOD_ID + ".claims.block.cnb";
	private static final String CLAIMS_BLOCK_EDIT_PREFIX = FTBUFinals.MOD_ID + ".claims.block.edit.";
	private static final String CLAIMS_BLOCK_INTERACT_PREFIX = FTBUFinals.MOD_ID + ".claims.block.interact.";
	private static final String CLAIMS_ITEM_PREFIX = FTBUFinals.MOD_ID + ".claims.item.";

	public static final String INFINITE_BACK_USAGE = FTBUFinals.MOD_ID + ".back.infinite";

	// Chunkloader //
	public static final String CHUNKLOADER_MAX_CHUNKS = FTBUFinals.MOD_ID + ".chunkloader.max_chunks";
	//public static final String CHUNKLOADER_OFFLINE_TIMER = FTBUFinals.MOD_ID + ".chunkloader.offline_timer";
	public static final String CHUNKLOADER_LOAD_OFFLINE = FTBUFinals.MOD_ID + ".chunkloader.load_offline";

	@SubscribeEvent
	public static void registerPermissions(PermissionRegistryEvent event)
	{
		event.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
		event.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
		event.registerNode(HOMES_LIST_OTHER, DefaultPermissionLevel.ALL, "");
		event.registerNode(HOMES_TELEPORT_OTHER, DefaultPermissionLevel.ALL, "");
		event.registerNode(CLAIMS_CHUNKS_MODIFY_OTHERS, DefaultPermissionLevel.OP, "Allow player to modify other team chunks");
		event.registerNode(CLAIMS_BLOCK_CNB, DefaultPermissionLevel.OP, "Allow to edit C&B bits in claimed chunks");
		event.registerNode(INFINITE_BACK_USAGE, DefaultPermissionLevel.NONE, "Allow to use 'back' command infinite times");
		event.registerNode(CHUNKLOADER_LOAD_OFFLINE, DefaultPermissionLevel.ALL, "Keep loaded chunks working when player goes offline");

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
		event.registerNode(CLAIMS_ITEM_PREFIX + formatId(Items.END_CRYSTAL), DefaultPermissionLevel.OP);
		event.registerNode(CLAIMS_ITEM_PREFIX + "forge.bucketfilled", DefaultPermissionLevel.OP);
	}

	@SubscribeEvent
	public static void addConfigs(RegisterRankConfigEvent event)
	{
		event.register(BADGE, new ConfigString(""), new ConfigString(""));
		event.register(HOMES_MAX, new ConfigInt(1, 0, 30000), new ConfigInt(100));
		event.register(CLAIMS_MAX_CHUNKS, new ConfigInt(100, 0, 30000), new ConfigInt(1000));
		event.register(CHUNKLOADER_MAX_CHUNKS, new ConfigInt(50, 0, 30000), new ConfigInt(64));
		//event.register(CHUNKLOADER_OFFLINE_TIMER, new ConfigDouble(-1D).setMin(-1D), new ConfigDouble(-1D));
	}

	@SubscribeEvent
	public static void addCustomPerms(RegisterCustomPermissionPrefixesEvent event)
	{
		event.register(new NodeEntry("command.", DefaultPermissionLevel.OP, "Permission for commands, if FTBU command overriding is enabled. If not, this node will be inactive"));
		event.register(new NodeEntry(CLAIMS_BLOCK_EDIT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break and place within claimed chunks"));
		event.register(new NodeEntry(CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can right-click within claimed chunks"));
		event.register(new NodeEntry(CLAIMS_ITEM_PREFIX, DefaultPermissionLevel.ALL, "Permission for items that players can right-click in air within claimed chunks"));
	}

	private static String formatId(@Nullable IForgeRegistryEntry<?> item)
	{
		return (item == null || item.getRegistryName() == null) ? "minecraft.air" : item.getRegistryName().toString().toLowerCase().replace(':', '.');
	}

	public static boolean canModifyBlock(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
	{
		switch (type)
		{
			case EDIT:
				return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_EDIT_PREFIX + formatId(block.getState().getBlock()), null);
			case INTERACT:
				return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_INTERACT_PREFIX + formatId(block.getState().getBlock()), null);
			case CNB_BREAK:
			case CNB_PLACE:
				return PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_BLOCK_CNB, null);
			case ITEM:
				return !player.getHeldItem(hand).isEmpty() || PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_ITEM_PREFIX + formatId(player.getHeldItem(hand).getItem()), null);
			default:
				return false;
		}
	}
}