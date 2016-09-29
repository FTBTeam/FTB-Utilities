package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfigHandler;
import com.feed_the_beast.ftbl.lib.util.ASMUtils;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAddon;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.api.chunks.ILoadedChunkStorage;
import com.feed_the_beast.ftbu.api.leaderboard.ILeaderboard;
import com.feed_the_beast.ftbu.api.leaderboard.Leaderboard;
import com.feed_the_beast.ftbu.ranks.DefaultOPRank;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.mojang.authlib.GameProfile;
import net.minecraft.stats.StatBase;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public enum FTBUtilitiesAPI_Impl implements FTBUtilitiesAPI, IPermissionHandler, IRankConfigHandler
{
    INSTANCE;

    public final Map<StatBase, ILeaderboard> LEADERBOARDS = new HashMap<>();

    public void init(ASMDataTable table)
    {
        ASMUtils.findAnnotatedObjects(table, FTBUtilitiesAPI.class, FTBUtilitiesAddon.class, (obj, field, data) -> field.set(null, INSTANCE));
        ASMUtils.findAnnotatedMethods(table, FTBUtilitiesAddon.class, (method, params, data) -> method.invoke(null));
        ASMUtils.findAnnotatedObjects(table, ILeaderboard.class, Leaderboard.class, (obj, field, data) -> LEADERBOARDS.put(obj.getStat(), obj));
    }

    @Override
    public IClaimedChunkStorage getClaimedChunks()
    {
        return ClaimedChunkStorage.INSTANCE;
    }

    @Override
    public ILoadedChunkStorage getLoadedChunks()
    {
        return LoadedChunkStorage.INSTANCE;
    }

    @Override
    public IRank getRank(GameProfile profile)
    {
        if(Ranks.INSTANCE.defaultRank != null)
        {
            IRank r = Ranks.INSTANCE.PLAYER_MAP.get(profile.getId());
            return (r == null) ? Ranks.INSTANCE.defaultRank : r;
        }

        return LMServerUtils.isOP(profile) ? DefaultOPRank.INSTANCE : DefaultPlayerRank.INSTANCE;
    }

    @Override
    public void registerNode(String s, DefaultPermissionLevel defaultPermissionLevel, String s1)
    {
        DefaultPermissionHandler.INSTANCE.registerNode(s, defaultPermissionLevel, s1);
    }

    @Override
    public Collection<String> getRegisteredNodes()
    {
        return DefaultPermissionHandler.INSTANCE.getRegisteredNodes();
    }

    @Override
    public boolean hasPermission(GameProfile profile, String permission, @Nullable IContext context)
    {
        switch(getRank(profile).hasPermission(permission))
        {
            case ALLOW:
                return true;
            case DENY:
                return false;
            default:
                return DefaultPermissionHandler.INSTANCE.hasPermission(profile, permission, context);
        }
    }

    @Override
    public String getNodeDescription(String s)
    {
        return DefaultPermissionHandler.INSTANCE.getNodeDescription(s);
    }

    @Override
    public IConfigValue getRankConfig(GameProfile profile, IRankConfig id)
    {
        return getRank(profile).getConfig(id);
    }
}