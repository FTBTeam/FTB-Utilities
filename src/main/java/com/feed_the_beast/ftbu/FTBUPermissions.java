package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyInt;
import com.feed_the_beast.ftbl.lib.config.PropertyList;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.math.BlockPosContainer;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.chunks.BlockInteractionType;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //
    public static final String DISPLAY_ADMIN_INFO = "ftbu.display.admin_info";
    public static final String DISPLAY_PERMISSIONS = "ftbu.display.permissions";
    public static final String BADGE = "ftbu.badge";

    // Homes //
    public static final String HOMES_CROSS_DIM = "ftbu.homes.cross_dim";
    public static final String HOMES_MAX = "ftbu.homes.max";

    // Claims //
    public static final String CLAIMS_CHUNKS_MODIFY_SELF = "ftbu.claims.modify.self";
    public static final String CLAIMS_CHUNKS_MODIFY_OTHERS = "ftbu.claims.modify.others";
    public static final String CLAIMS_MAX_CHUNKS = "ftbu.claims.max_chunks";
    public static final String CLAIMS_BLOCK_CNB = "ftbu.claims.block.cnb";
    private static final String CLAIMS_BLOCK_EDIT_PREFIX = "ftbu.claims.block.edit.";
    private static final String CLAIMS_BLOCK_INTERACT_PREFIX = "ftbu.claims.block.interact.";
    private static final String CLAIMS_ITEM_PREFIX = "ftbu.claims.item.";
    private static final String CLAIMS_BLOCKED_DIMENSIONS = "ftbu.claims.blocked_dimensions";
    private static final String CLAIMS_UPGRADE_PREFIX = "ftbu.claims.upgrade.";

    public static final String INFINITE_BACK_USAGE = "ftbu.back.infinite";

    // Chunkloader //
    public static final String CHUNKLOADER_MAX_CHUNKS = "ftbu.chunkloader.max_chunks";
    //public static final String CHUNKLOADER_OFFLINE_TIMER = "ftbu.chunkloader.offline_timer";
    public static final String CHUNKLOADER_LOAD_OFFLINE = "ftbu.chunkloader.load_offline";

    public static void init()
    {
        PermissionAPI.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
        PermissionAPI.registerNode(DISPLAY_PERMISSIONS, DefaultPermissionLevel.OP, "Display 'My Permissions' in Server Info");
        PermissionAPI.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
        PermissionAPI.registerNode(CLAIMS_CHUNKS_MODIFY_SELF, DefaultPermissionLevel.ALL, "Allow player to claim/unclaim his chunks");
        PermissionAPI.registerNode(CLAIMS_CHUNKS_MODIFY_OTHERS, DefaultPermissionLevel.OP, "Allow player to modify other player's chunks");
        PermissionAPI.registerNode(CLAIMS_BLOCK_CNB, DefaultPermissionLevel.OP, "Allow to edit C&B bits in claimed chunks");
        PermissionAPI.registerNode(INFINITE_BACK_USAGE, DefaultPermissionLevel.NONE, "Allow to use 'back' command infinite times");
        PermissionAPI.registerNode(CHUNKLOADER_LOAD_OFFLINE, DefaultPermissionLevel.ALL, "Keep loaded chunks working when player goes offline");

        Map<String, DefaultPermissionLevel> levels = new HashMap<>();

        for(Block block : Block.REGISTRY)
        {
            String name = formatId(block);
            levels.put(CLAIMS_BLOCK_EDIT_PREFIX + name, (name.startsWith("graves.") || name.startsWith("gravestone.")) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
            levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + name, (block instanceof BlockDoor || block instanceof BlockWorkbench || block instanceof BlockAnvil) ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
        }

        for(Item item : Item.REGISTRY)
        {
            String name = formatId(item);
            levels.put(CLAIMS_ITEM_PREFIX + name, (item instanceof ItemBucket) ? DefaultPermissionLevel.OP : DefaultPermissionLevel.ALL);
        }

        levels.put(CLAIMS_BLOCK_EDIT_PREFIX + "gravestone.gravestone", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_ITEM_PREFIX + formatId(Items.END_CRYSTAL), DefaultPermissionLevel.OP);
        levels.put(CLAIMS_ITEM_PREFIX + "forge.bucketfilled", DefaultPermissionLevel.OP);

        for(IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
        {
            if(upgrade != null)
            {
                levels.put(CLAIMS_UPGRADE_PREFIX + upgrade.getName(), DefaultPermissionLevel.ALL);
            }
        }

        levels.forEach(FTBUPermissions::registerNoDescNode);
    }

    private static void registerNoDescNode(String key, DefaultPermissionLevel level)
    {
        PermissionAPI.registerNode(key, level, "");
    }

    public static void addConfigs(IFTBLibRegistry reg)
    {
        reg.addRankConfig(BADGE, new PropertyString(""), new PropertyString(""));
        reg.addRankConfig(HOMES_MAX, new PropertyShort(1, 0, 30000), new PropertyShort(100));
        reg.addRankConfig(CLAIMS_MAX_CHUNKS, new PropertyShort(100, 0, 30000), new PropertyShort(1000));
        reg.addRankConfig(CLAIMS_BLOCKED_DIMENSIONS, new PropertyList(PropertyInt.ID), new PropertyList(PropertyInt.ID));
        reg.addRankConfig(CHUNKLOADER_MAX_CHUNKS, new PropertyShort(50, 0, 30000), new PropertyShort(64));
        //reg.addRankConfig(CHUNKLOADER_OFFLINE_TIMER, new PropertyDouble(-1D).setMin(-1D), new PropertyDouble(-1D));
    }

    public static void addCustomPerms(IFTBUtilitiesRegistry reg)
    {
        reg.addCustomPermPrefix(new NodeEntry("command.", DefaultPermissionLevel.OP, "Permission for commands, if FTBU command overriding is enabled. If not, this node will be inactive"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_EDIT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break and place within claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can right-click within claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_ITEM_PREFIX, DefaultPermissionLevel.ALL, "Permission for items that players can right-click in air within claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_UPGRADE_PREFIX, DefaultPermissionLevel.ALL, "Permission for claimed chunk upgrades"));
    }

    private static String formatId(@Nullable IForgeRegistryEntry<?> item)
    {
        return item == null ? "minecraft.air" : item.getRegistryName().toString().toLowerCase().replace(':', '.');
    }

    public static boolean canModifyBlock(EntityPlayerMP player, EnumHand hand, BlockPosContainer block, BlockInteractionType type)
    {
        switch(type)
        {
            case EDIT:
                return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_EDIT_PREFIX + formatId(block.getState().getBlock()), null);//new PlayerContext(player).set(ContextKeys.POS, pos).set(ContextKeys.BLOCK_STATE, state));
            case INTERACT:
                return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_INTERACT_PREFIX + formatId(block.getState().getBlock()), null);//new PlayerContext(player).set(ContextKeys.POS, pos).set(ContextKeys.BLOCK_STATE, state));
            case CNB_BREAK:
            case CNB_PLACE:
                return PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_BLOCK_CNB, null);//new PlayerContext(player).set(ContextKeys.POS, pos)))
            case ITEM:
                return player.getHeldItem(hand) == null || PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_ITEM_PREFIX + formatId(player.getHeldItem(hand).getItem()), null);//new PlayerContext(player).set(ContextKeys.POS, pos).set(ContextKeys.BLOCK_STATE, state));
            default:
                return false;
        }
    }

    public static boolean allowDimension(GameProfile profile, int dimension)
    {
        IConfigValue value = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(profile, CLAIMS_BLOCKED_DIMENSIONS);
        return !(value instanceof PropertyList && ((PropertyList) value).containsValue(dimension));
    }

    public static boolean canUpgradeChunk(GameProfile profile, IChunkUpgrade upgrade)
    {
        return PermissionAPI.hasPermission(profile, CLAIMS_UPGRADE_PREFIX + upgrade.getName(), null);
    }
}