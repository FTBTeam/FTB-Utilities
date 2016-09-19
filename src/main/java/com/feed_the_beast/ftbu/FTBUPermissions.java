package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.permissions.DefaultPermissionLevel;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.api_impl.config.PropertyDouble;
import com.feed_the_beast.ftbl.api_impl.config.PropertyEnum;
import com.feed_the_beast.ftbl.api_impl.config.PropertyInt;
import com.feed_the_beast.ftbl.api_impl.config.PropertyIntList;
import com.feed_the_beast.ftbl.api_impl.config.PropertyStringList;
import com.feed_the_beast.ftbu.api_impl.ChunkloaderType;
import com.latmod.lib.EnumEnabled;
import net.minecraftforge.common.util.Constants;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //

    public static final String DISPLAY_ADMIN_INFO = PermissionAPI.registerPermission("ftbu.display.admin_info", DefaultPermissionLevel.OP, "Display 'Admin' in Server Info");
    public static final String DISPLAY_RANK = PermissionAPI.registerPermission("ftbu.display.rank", DefaultPermissionLevel.ALL, "Display Rank in FriendsGUI");
    public static final String DISPLAY_PERMISSIONS = PermissionAPI.registerPermission("ftbu.display.permissions", DefaultPermissionLevel.ALL, "Display 'My Permissions' in Server Info");

    // Homes //

    public static final String HOMES_CROSS_DIM = PermissionAPI.registerPermission("ftbu.homes.cross_dim", DefaultPermissionLevel.ALL, "Can use /home to teleport to/from another dimension");

    public static final IRankConfig HOMES_MAX = RankConfigAPI.register("ftbu.homes.max",
            new PropertyInt(Constants.NBT.TAG_SHORT, 1, 0, 30000), new PropertyInt(100),
            "Max home count");

    // Claims //

    public static final String CLAIMS_MODIFY_OTHER_CHUNKS = PermissionAPI.registerPermission("ftbu.claims.modify_other_chunks", DefaultPermissionLevel.OP, "Allow player to edit other player's chunks");

    public static final IRankConfig CLAIMS_MAX_CHUNKS = RankConfigAPI.register("ftbu.claims.max_chunks",
            new PropertyInt(Constants.NBT.TAG_SHORT, 100, 0, 30000), new PropertyInt(1000),
            "Max amount of chunks that player can claim\n0 - Disabled");

    public static final IRankConfig CLAIMS_FORCED_EXPLOSIONS = RankConfigAPI.register("ftbu.claims.forced_explosions",
            new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null), new PropertyEnum<>(EnumEnabled.NAME_MAP_WITH_NULL, null),
            "-: Player setting\ndisabled: Explosions will never happen in claimed chunks\nenabled: Explosions will always happen in claimed chunks");

    public static final IRankConfig CLAIMS_BREAK_WHITELIST = RankConfigAPI.register("ftbu.claims.break_whitelist",
            new PropertyStringList("OpenBlocks:grave"), new PropertyStringList("*"),
            "Block IDs that player can break in claimed chunks");

    public static final IRankConfig CLAIMS_DIMENSION_BLACKLIST = RankConfigAPI.register("ftbu.claims.dimension_blacklist",
            new PropertyIntList(1), new PropertyIntList(),
            "Dimensions where players can't claim");

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
    }
}
