package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.AsmHelper;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.ranks.DefaultOPRank;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Created by LatvianModder on 30.08.2016.
 */
public enum FTBUtilitiesAPI_Impl implements FTBUtilitiesAPI, IPermissionHandler
{
    INSTANCE;

    private Collection<IFTBUtilitiesPlugin> plugins;

    public void init(ASMDataTable table)
    {
        plugins = AsmHelper.findPlugins(table, IFTBUtilitiesPlugin.class, FTBUtilitiesPlugin.class);

        for(IFTBUtilitiesPlugin p : plugins)
        {
            p.init(this);
        }
    }

    @Override
    public Collection<IFTBUtilitiesPlugin> getAllPlugins()
    {
        return plugins;
    }

    @Override
    public IClaimedChunkStorage getClaimedChunks()
    {
        return ClaimedChunkStorage.INSTANCE;
    }

    @Override
    public IRank getRank(GameProfile profile)
    {
        if(Ranks.defaultRank != null)
        {
            IRank r = Ranks.PLAYER_MAP.get(profile.getId());
            return (r == null) ? Ranks.defaultRank : r;
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
    public IConfigValue getRankConfig(GameProfile profile, String id)
    {
        return getRank(profile).getConfig(id);
    }
}