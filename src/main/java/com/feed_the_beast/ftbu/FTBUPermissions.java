package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IFTBLibRegistry;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyIntList;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.context.ContextKeys;
import net.minecraftforge.server.permission.context.PlayerContext;

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
    public static final String CLAIMS_CHUNKS_MODIFY_SELF = "ftbu.claims.chunks.modify.self";
    public static final String CLAIMS_CHUNKS_MODIFY_OTHERS = "ftbu.claims.chunks.modify.others";
    public static final String CLAIMS_MAX_CHUNKS = "ftbu.claims.max_chunks";
    public static final String CLAIMS_BLOCK_CNB = "ftbu.claims.block.cnb";
    private static final String CLAIMS_BLOCK_BREAK_PREFIX = "ftbu.claims.block.break.";
    private static final String CLAIMS_BLOCK_INTERACT_PREFIX = "ftbu.claims.block.interact.";
    private static final String CLAIMS_BLOCKED_DIMENSIONS = "ftbu.claims.blocked_dimensions";
    private static final String CLAIMS_UPGRADE_PREFIX = "ftbu.claims.upgrade.";

    public static final String INFINITE_BACK_USAGE = "ftbu.back.infinite";

    // Chunkloader //
    public static final String CHUNKLOADER_MAX_CHUNKS = "ftbu.chunkloader.max_chunks";
    public static final String CHUNKLOADER_OFFLINE_TIMER = "ftbu.chunkloader.offline_timer";

    public static void init()
    {
        PermissionAPI.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
        PermissionAPI.registerNode(DISPLAY_PERMISSIONS, DefaultPermissionLevel.OP, "Display 'My Permissions' in Server Info");
        PermissionAPI.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
        PermissionAPI.registerNode(CLAIMS_CHUNKS_MODIFY_SELF, DefaultPermissionLevel.ALL, "Allow player to claim/unclaim his chunks");
        PermissionAPI.registerNode(CLAIMS_CHUNKS_MODIFY_OTHERS, DefaultPermissionLevel.OP, "Allow player to modify other player's chunks");
        PermissionAPI.registerNode(CLAIMS_BLOCK_CNB, DefaultPermissionLevel.OP, "Allow to edit C&B bits in claimed chunks");
        PermissionAPI.registerNode(INFINITE_BACK_USAGE, DefaultPermissionLevel.NONE, "Allow to use 'back' command infinite times");

        final Map<String, DefaultPermissionLevel> levels = new HashMap<>();
        Block.REGISTRY.iterator().forEachRemaining(block ->
        {
            String blockName = formatBlock(block);
            levels.put(CLAIMS_BLOCK_BREAK_PREFIX + blockName, DefaultPermissionLevel.OP);
            levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + blockName, DefaultPermissionLevel.OP);
        });

        levels.put(CLAIMS_BLOCK_BREAK_PREFIX + "gravestone.gravestone", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_BREAK_PREFIX + "graves.gravestone", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_BREAK_PREFIX + "graves.graveslave", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_BREAK_PREFIX + "graves.headstone", DefaultPermissionLevel.ALL);

        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.CRAFTING_TABLE), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.ANVIL), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.OAK_DOOR), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.BIRCH_DOOR), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.SPRUCE_DOOR), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.JUNGLE_DOOR), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.DARK_OAK_DOOR), DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(Blocks.ACACIA_DOOR), DefaultPermissionLevel.ALL);

        levels.forEach((key, value) -> PermissionAPI.registerNode(key, value, ""));

        for(IChunkUpgrade upgrade : FTBUCommon.CHUNK_UPGRADES)
        {
            if(upgrade != null)
            {
                PermissionAPI.registerNode(CLAIMS_UPGRADE_PREFIX + upgrade.getName(), DefaultPermissionLevel.ALL, "");
            }
        }
    }

    public static void addConfigs(IFTBLibRegistry reg)
    {
        reg.addRankConfig(BADGE, new PropertyString(""), new PropertyString(""), "Prefix of player's nickname");
        reg.addRankConfig(HOMES_MAX, new PropertyShort(1, 0, 30000), new PropertyShort(100), "Max home count");
        reg.addRankConfig(CLAIMS_MAX_CHUNKS, new PropertyShort(100, 0, 30000), new PropertyShort(1000), "Max amount of chunks that player can claim", "0 - Disabled");
        reg.addRankConfig(CLAIMS_BLOCKED_DIMENSIONS, new PropertyIntList(), new PropertyIntList(), "Dimensions where chunk claiming is not allowed");
        reg.addRankConfig(CHUNKLOADER_MAX_CHUNKS, new PropertyShort(50, 0, 30000), new PropertyShort(64), "Max amount of chunks that player can load", "0 - Disabled");
        reg.addRankConfig(CHUNKLOADER_OFFLINE_TIMER, new PropertyDouble(-1D).setMin(-1D), new PropertyDouble(-1D), "Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Chunk will always be loaded");
    }

    public static void addCustomPerms(IFTBUtilitiesRegistry reg)
    {
        reg.addCustomPermPrefix(new NodeEntry("command.", DefaultPermissionLevel.OP, "Permission for commands, if FTBU command overriding is enabled. If not, this node will be inactive"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_BREAK_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break in claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can interact within claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_UPGRADE_PREFIX, DefaultPermissionLevel.ALL, "Permission for claimed chunk upgrades"));
    }

    private static String formatBlock(@Nullable Block block)
    {
        return block == null ? "minecraft:air" : block.getRegistryName().toString().toLowerCase().replace(':', '.');
    }

    public static boolean canBreak(EntityPlayerMP player, BlockPos pos, IBlockState state)
    {
        return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_BREAK_PREFIX + formatBlock(state.getBlock()), new PlayerContext(player).set(ContextKeys.POS, pos).set(ContextKeys.BLOCK_STATE, state));
    }

    public static boolean canInteract(EntityPlayerMP player, BlockPos pos, IBlockState state)
    {
        return PermissionAPI.hasPermission(player.getGameProfile(), CLAIMS_BLOCK_INTERACT_PREFIX + formatBlock(state.getBlock()), new PlayerContext(player).set(ContextKeys.POS, pos).set(ContextKeys.BLOCK_STATE, state));
    }

    public static boolean allowDimension(GameProfile profile, int dimension)
    {
        IConfigValue value = FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(profile, CLAIMS_BLOCKED_DIMENSIONS);
        return !(value instanceof PropertyIntList && ((PropertyIntList) value).getIntList().contains(dimension));
    }

    public static boolean canUpgradeChunk(GameProfile profile, IChunkUpgrade upgrade)
    {
        return PermissionAPI.hasPermission(profile, CLAIMS_UPGRADE_PREFIX + upgrade.getName(), null);
    }
}