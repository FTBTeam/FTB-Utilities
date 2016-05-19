package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.permissions.ForgePermissionRegistry;
import com.feed_the_beast.ftbl.api.permissions.RankConfig;
import com.feed_the_beast.ftbl.api.permissions.RankConfigEnum;
import com.feed_the_beast.ftbl.util.PrivacyLevel;
import com.feed_the_beast.ftbu.world.ChunkloaderType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import latmod.lib.annotations.Info;
import latmod.lib.annotations.NumberBounds;
import latmod.lib.util.EnumEnabled;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
    // Display //

    @Info("Display 'Admin' in Server Info")
    public static final String display_admin_info = ForgePermissionRegistry.registerPermission("ftbu.display.admin_info", false);

    @Info("Display Rank in FriendsGUI")
    public static final String display_rank = ForgePermissionRegistry.registerPermission("ftbu.display.rank", true);

    @Info("Display 'My Permissions' in Server Info")
    public static final String display_permissions = ForgePermissionRegistry.registerPermission("ftbu.display.permissions", true);

    // Homes //

    @Info("Can use /home to teleport to/from another dimension")
    public static final String homes_cross_dim = ForgePermissionRegistry.registerPermission("ftbu.homes.cross_dim", true);

    //@ConfigType(PrimitiveType.INT)
    @NumberBounds(min = 0, max = 30000)
    @Info("Max home count")
    public static final RankConfig homes_max = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.homes.max")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        { return new JsonPrimitive(op ? 100 : 1); }
    });

    // Claims //

    //@ConfigType(PrimitiveType.INT)
    @NumberBounds(min = 0, max = 30000)
    @Info({"Max amount of chunks that player can claim", "0 - Disabled"})
    public static final RankConfig claims_max_chunks = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.claims.max_chunks")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        { return new JsonPrimitive(op ? 1000 : 100); }
    });

    //@ConfigType(PrimitiveType.ENUM)
    @Info({"-: Player setting", "disabled: Explosions will never happen in claimed chunks", "enabled: Explosions will always happen in claimed chunks"})
    public static final RankConfigEnum<EnumEnabled> claims_forced_explosions = new RankConfigEnum<>("ftbu.claims.forced_explosions", null, null, EnumEnabled.values(), true);

    //@ConfigType(PrimitiveType.ENUM)
    public static final RankConfigEnum<PrivacyLevel> claims_forced_security = new RankConfigEnum<>("ftbu.claims.forced_security", null, null, PrivacyLevel.VALUES_3, true);

    //@ConfigType(PrimitiveType.STRING_ARRAY)
    @Info("Block IDs that player can break in claimed chunks")
    public static final RankConfig claims_break_whitelist = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.claims.break_whitelist")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        {
            JsonArray a = new JsonArray();

            if(op)
            {
                a.add(new JsonPrimitive("*"));
            }
            else
            {
                a.add(new JsonPrimitive("OpenBlocks:grave"));
            }

            return a;
        }
    });

    //@ConfigType(PrimitiveType.INT_ARRAY)
    @Info("Dimensions where players can't claim")
    public static final RankConfig claims_dimension_blacklist = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.claims.dimension_blacklist")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        {
            JsonArray a = new JsonArray();

            if(!op)
            {
                a.add(new JsonPrimitive(1));
            }

            return a;
        }
    });

    // Chunkloader //

    @Info({"disabled: Players won't be able to chunkload", "offline: Chunks stay loaded when player loggs off", "online: Chunks only stay loaded while owner is online"})
    public static final RankConfigEnum<ChunkloaderType> chunkloader_type = ForgePermissionRegistry.registerRankConfig(new RankConfigEnum<>("ftbu.chunkloader.type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false));

    @NumberBounds(min = 0, max = 30000)
    @Info({"Max amount of chunks that player can load", "0 - Disabled"})
    public static final RankConfig chunkloader_max_chunks = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.chunkloader.max_chunks")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        { return new JsonPrimitive(op ? 5000 : 50); }
    });

    @NumberBounds(min = -1D)
    @Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Chunk will always be loaded"})
    public static final RankConfig chunkloader_offline_timer = ForgePermissionRegistry.registerRankConfig(new RankConfig("ftbu.chunkloader.offline_timer")
    {
        @Override
        public JsonElement getDefaultValue(boolean op)
        { return new JsonPrimitive(op ? -1D : 24D); }
    });

    public static void init()
    {
    }
}
