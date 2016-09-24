package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbu.api_impl.ChunkloaderType;
import com.latmod.lib.EnumEnabled;
import com.latmod.lib.config.PropertyDouble;
import com.latmod.lib.config.PropertyEnum;
import com.latmod.lib.config.PropertyInt;
import net.minecraft.block.Block;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //

    public static final String DISPLAY_ADMIN_INFO = PermissionAPI.registerNode("ftbu.display.admin_info", DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
    public static final String DISPLAY_RANK = PermissionAPI.registerNode("ftbu.display.rank", DefaultPermissionLevel.ALL, "Display Rank in FriendsGUI");
    public static final String DISPLAY_PERMISSIONS = PermissionAPI.registerNode("ftbu.display.permissions", DefaultPermissionLevel.ALL, "Display 'My Permissions' in Server Info");

    // Homes //

    public static final String HOMES_CROSS_DIM = PermissionAPI.registerNode("ftbu.homes.cross_dim", DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");

    public static final IRankConfig HOMES_MAX = RankConfigAPI.register("ftbu.homes.max",
            new PropertyInt(Constants.NBT.TAG_SHORT, 1, 0, 30000), new PropertyInt(100),
            "Max home count");

    // Claims //

    public static final String CLAIMS_MODIFY_OTHER_CHUNKS = PermissionAPI.registerNode("ftbu.claims.modify_other_chunks", DefaultPermissionLevel.OP, "Allow player to edit other player's chunks");

    public static final IRankConfig CLAIMS_MAX_CHUNKS = RankConfigAPI.register("ftbu.claims.max_chunks",
            new PropertyInt(Constants.NBT.TAG_SHORT, 100, 0, 30000), new PropertyInt(1000),
            "Max amount of chunks that player can claim\n0 - Disabled");

    public static final IRankConfig CLAIMS_FORCED_EXPLOSIONS = RankConfigAPI.register("ftbu.claims.forced_explosions",
            new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null), new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null),
            "-: Player setting\ndisabled: Explosions will never happen in claimed chunks\nenabled: Explosions will always happen in claimed chunks");

    public static final String CLAIMS_BLOCK_BREAK_PREFIX = "ftbu.claims.block.break.";

    public static final String CLAIMS_DIMENSION_ALLOWED_PREFIX = "ftbu.claims.dimension_allowed.";

    // Chunkloader //

    public static final IRankConfig CHUNKLOADER_TYPE = RankConfigAPI.register("ftbu.chunkloader.type",
            new PropertyEnum<>(ChunkloaderType.NAME_MAP, ChunkloaderType.OFFLINE),
            new PropertyEnum<>(ChunkloaderType.NAME_MAP, ChunkloaderType.OFFLINE),
            "disabled: Players won't be able to chunkload\noffline: Chunks stay loaded when player loggs off\nonline: Chunks only stay loaded while owner is online");

    public static final IRankConfig CHUNKLOADER_MAX_CHUNKS = RankConfigAPI.register("ftbu.chunkloader.max_chunks",
            new PropertyInt(Constants.NBT.TAG_SHORT, 50, 0, 30000), new PropertyInt(5000),
            "Max amount of chunks that player can load\n0 - Disabled");

    public static final IRankConfig CHUNKLOADER_OFFLINE_TIMER = RankConfigAPI.register("ftbu.chunkloader.offline_timer",
            new PropertyDouble(24D).setMin(-1D), new PropertyDouble(-1D),
            "Max hours player can be offline until he's chunks unload\n0 - Disabled, will unload instantly when he disconnects\n-1 - Chunk will always be loaded");

    public static void init()
    {
        final Map<String, DefaultPermissionLevel> levels = new HashMap<>();

        Block.REGISTRY.iterator().forEachRemaining(block ->
        {
            levels.put(CLAIMS_BLOCK_BREAK_PREFIX + formatBlock(block), DefaultPermissionLevel.OP);
        });

        levels.put(CLAIMS_BLOCK_BREAK_PREFIX + "openblocks.grave", DefaultPermissionLevel.ALL);

        //"Dimensions where players can't claim"
        for(int i : DimensionManager.getStaticDimensionIDs())
        {
            levels.put(CLAIMS_DIMENSION_ALLOWED_PREFIX + i, DefaultPermissionLevel.ALL);
        }

        levels.put(CLAIMS_DIMENSION_ALLOWED_PREFIX + "1", DefaultPermissionLevel.OP);

        levels.forEach((key, value) -> PermissionAPI.registerNode(key, value, ""));
    }

    public static String formatBlock(Block block)
    {
        return block.getRegistryName().toString().toLowerCase(Locale.ENGLISH).replace(':', '.');
    }
}