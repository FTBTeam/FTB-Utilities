package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
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
    public static final String CLAIMS_FORCED_EXPLOSIONS = "ftbu.claims.forced_explosions";
    public static final String CLAIMS_BLOCK_CNB = "ftbu.claims.block.cnb";
    private static final String CLAIMS_BLOCK_BREAK_PREFIX = "ftbu.claims.block.break.";
    private static final String CLAIMS_BLOCK_INTERACT_PREFIX = "ftbu.claims.block.interact.";
    private static final String CLAIMS_DIMENSION_ALLOWED_PREFIX = "ftbu.claims.dimension_allowed.";
    public static final String INFINITE_BACK_USAGE = "ftbu.back.infinite";

    // Chunkloader //
    public static final String CHUNKLOADER_TYPE = "ftbu.chunkloader.type";
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

        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + "minecraft.crafting_table", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + "minecraft.anvil", DefaultPermissionLevel.ALL);
        levels.put(CLAIMS_BLOCK_INTERACT_PREFIX + "minecraft.wooden_door", DefaultPermissionLevel.ALL);

        for(int i : DimensionManager.getStaticDimensionIDs())
        {
            levels.put(CLAIMS_DIMENSION_ALLOWED_PREFIX + i, DefaultPermissionLevel.ALL);
        }

        levels.forEach((key, value) -> PermissionAPI.registerNode(key, value, ""));
    }

    public static void addCustomPerms(IFTBUtilitiesRegistry reg)
    {
        reg.addCustomPermPrefix(new NodeEntry("command.", DefaultPermissionLevel.OP, "Permission for commands, if FTBU command overriding is enabled. If not, this node will be inactive"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_BREAK_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break in claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can interact within claimed chunks"));
        reg.addCustomPermPrefix(new NodeEntry(CLAIMS_DIMENSION_ALLOWED_PREFIX, DefaultPermissionLevel.ALL, "Permission for dimensions where claiming chunks is allowed"));
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
        return PermissionAPI.hasPermission(profile, CLAIMS_DIMENSION_ALLOWED_PREFIX + dimension, null);
    }
}