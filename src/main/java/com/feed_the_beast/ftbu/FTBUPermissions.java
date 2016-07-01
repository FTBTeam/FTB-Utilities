package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfig;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfigAPI;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfigDouble;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfigEnum;
import com.feed_the_beast.ftbl.api.permissions.rankconfig.RankConfigInt;
import com.feed_the_beast.ftbu.world.ChunkloaderType;
import com.latmod.lib.EnumEnabled;
import com.latmod.lib.json.LMJsonUtils;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //

    //Display 'Admin' in Server Info
    /***/
    public static final String DISPLAY_ADMIN_INFO = "ftbu.display.admin_info";

    /**
     * Display Rank in FriendsGUI
     */
    public static final String DISPLAY_RANK = "ftbu.display.rank"; // true

    /**
     * Display 'My Permissions' in Server Info
     */
    public static final String DISPLAY_PERMISSIONS = "ftbu.display.permissions"; // true

    // Homes //

    /**
     * Can use /home to teleport to/from another dimension
     */
    public static final String HOMES_CROSS_DIM = "ftbu.homes.cross_dim";

    /**
     * Max home count
     */
    public static final RankConfigInt HOMES_MAX = new RankConfigInt("ftbu.homes.max", 1, 100, 0, 30000);

    // Claims //

    /**
     * Allow player to edit other player's chunks
     */
    public static final String CLAIMS_MODIFY_OTHER_CHUNKS = "ftbu.claims.modify_other_chunks"; // false

    /**
     * "Max amount of chunks that player can claim
     * 0 - Disabled
     */
    public static final RankConfigInt CLAIMS_MAX_CHUNKS = new RankConfigInt("ftbu.claims.max_chunks", 100, 1000, 0, 30000);

    /**
     * -: Player setting
     * disabled: Explosions will never happen in claimed chunks
     * enabled: Explosions will always happen in claimed chunks
     */
    public static final RankConfigEnum<EnumEnabled> CLAIMS_FORCED_EXPLOSIONS = new RankConfigEnum<>("ftbu.claims.forced_explosions", null, null, EnumEnabled.values(), true);

    /**
     * Block IDs that player can break in claimed chunks
     */
    public static final RankConfig CLAIMS_BREAK_WHITELIST = new RankConfig("ftbu.claims.break_whitelist", LMJsonUtils.toStringArray("OpenBlocks:grave"), LMJsonUtils.toStringArray("*"));

    /**
     * Dimensions where players can't claim
     */
    public static final RankConfig CLAIMS_DIMENSION_BLACKLIST = new RankConfig("ftbu.claims.dimension_blacklist", LMJsonUtils.toIntArray(1), LMJsonUtils.toIntArray());

    // Chunkloader //

    /**
     * disabled: Players won't be able to chunkload
     * offline: Chunks stay loaded when player loggs off
     * online: Chunks only stay loaded while owner is online
     */
    public static final RankConfigEnum<ChunkloaderType> CHUNKLOADER_TYPE = new RankConfigEnum<>("ftbu.chunkloader.type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false);

    /**
     * Max amount of chunks that player can load
     * 0 - Disabled
     */
    public static final RankConfigInt CHUNKLOADER_MAX_CHUNKS = new RankConfigInt("ftbu.chunkloader.max_chunks", 50, 5000, 0, 30000);

    /**
     * Max hours player can be offline until he's chunks unload
     * 0 - Disabled, will unload instantly when he disconnects
     * -1 - Chunk will always be loaded
     */
    public static final RankConfigDouble CHUNKLOADER_OFFLINE_TIMER = new RankConfigDouble("ftbu.chunkloader.offline_timer", 24D, -1D, -1D, null);

    public static void init()
    {
        RankConfigAPI.registerAll(FTBUPermissions.class);
    }
}
