package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.lib.EnumEnabled;
import com.feed_the_beast.ftbl.lib.config.PropertyDouble;
import com.feed_the_beast.ftbl.lib.config.PropertyEnum;
import com.feed_the_beast.ftbl.lib.config.PropertyShort;
import com.feed_the_beast.ftbl.lib.config.PropertyString;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbu.api_impl.ChunkloaderType;
import net.minecraft.block.Block;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //
    public static final String DISPLAY_ADMIN_INFO = "ftbu.display.admin_info";
    public static final String DISPLAY_PERMISSIONS = "ftbu.display.permissions";
    public static final IRankConfig DISPLAY_COLOR = RankConfigAPI.register("display.color", new PropertyEnum<>(LMServerUtils.TEXT_FORMATTING_NAME_MAP, TextFormatting.WHITE), new PropertyEnum<>(LMServerUtils.TEXT_FORMATTING_NAME_MAP, TextFormatting.GREEN), "Color of player's nickname");
    public static final IRankConfig DISPLAY_PREFIX = RankConfigAPI.register("display.prefix", new PropertyString(""), new PropertyString(""), "Prefix of player's nickname");
    public static final IRankConfig DISPLAY_BADGE = RankConfigAPI.register("display.badge", new PropertyString(""), new PropertyString(""), "Prefix of player's nickname");

    // Homes //
    public static final String HOMES_CROSS_DIM = "ftbu.homes.cross_dim";
    public static final IRankConfig HOMES_MAX = RankConfigAPI.register("ftbu.homes.max", new PropertyShort(1, 0, 30000), new PropertyShort(100), "Max home count");

    // Claims //
    public static final String CLAIMS_CLAIM_CHUNKS = "ftbu.claims.claim_chunks";
    public static final String CLAIMS_MODIFY_OTHER_CHUNKS = "ftbu.claims.modify_other_chunks";
    public static final IRankConfig CLAIMS_MAX_CHUNKS = RankConfigAPI.register("ftbu.claims.max_chunks", new PropertyShort(100, 0, 30000), new PropertyShort(1000), "Max amount of chunks that player can claim", "0 - Disabled");
    public static final IRankConfig CLAIMS_FORCED_EXPLOSIONS = RankConfigAPI.register("ftbu.claims.forced_explosions", new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null), new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null), "-: Player setting", "disabled: Explosions will never happen in claimed chunks", "enabled: Explosions will always happen in claimed chunks");
    public static final String CLAIMS_BLOCK_CNB = "ftbu.claims.block.cnb";
    public static final String CLAIMS_BLOCK_BREAK_PREFIX = "ftbu.claims.block.break.";
    public static final String CLAIMS_BLOCK_INTERACT_PREFIX = "ftbu.claims.block.interact.";
    public static final String CLAIMS_DIMENSION_ALLOWED_PREFIX = "ftbu.claims.dimension_allowed.";
    public static final String INFINITE_BACK_USAGE = "ftbu.back.infinite";

    // Chunkloader //
    public static final IRankConfig CHUNKLOADER_TYPE = RankConfigAPI.register("ftbu.chunkloader.type", new PropertyEnum<>(ChunkloaderType.NAME_MAP, ChunkloaderType.OFFLINE), new PropertyEnum<>(ChunkloaderType.NAME_MAP, ChunkloaderType.OFFLINE), "disabled: Players won't be able to chunkload", "offline: Chunks stay loaded when player loggs off", "online: Chunks only stay loaded while owner is online");
    public static final IRankConfig CHUNKLOADER_MAX_CHUNKS = RankConfigAPI.register("ftbu.chunkloader.max_chunks", new PropertyShort(50, 0, 30000), new PropertyShort(64), "Max amount of chunks that player can load", "0 - Disabled");
    public static final IRankConfig CHUNKLOADER_OFFLINE_TIMER = RankConfigAPI.register("ftbu.chunkloader.offline_timer", new PropertyDouble(24D).setMin(-1D), new PropertyDouble(-1D), "Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Chunk will always be loaded");

    public static void init()
    {
        PermissionAPI.registerNode(DISPLAY_ADMIN_INFO, DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
        PermissionAPI.registerNode(DISPLAY_PERMISSIONS, DefaultPermissionLevel.OP, "Display 'My Permissions' in Server Info");
        PermissionAPI.registerNode(HOMES_CROSS_DIM, DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");
        PermissionAPI.registerNode(CLAIMS_CLAIM_CHUNKS, DefaultPermissionLevel.ALL, "Allow player to claim chunks");
        PermissionAPI.registerNode(CLAIMS_MODIFY_OTHER_CHUNKS, DefaultPermissionLevel.OP, "Allow player to edit other player's chunks");
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

    public static String formatBlock(@Nullable Block block)
    {
        return block == null ? "minecraft:air" : block.getRegistryName().toString().toLowerCase(Locale.ENGLISH).replace(':', '.');
    }
}