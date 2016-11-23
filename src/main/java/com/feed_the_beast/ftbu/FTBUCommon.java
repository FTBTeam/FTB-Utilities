package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.Leaderboard;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.stats.StatBase;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FTBUCommon implements IFTBUtilitiesRegistry // FTBUClient
{
    public final Map<StatBase, Leaderboard> leaderboards = new HashMap<>();
    public final Collection<NodeEntry> customPermPrefixRegistry = new HashSet<>();

    public void preInit()
    {
        addLeaderboard(FTBULeaderboards.DEATHS);
        addLeaderboard(FTBULeaderboards.MOB_KILLS);
        addLeaderboard(FTBULeaderboards.DEATHS_PER_HOUR_LB);
        addLeaderboard(FTBULeaderboards.PLAY_ONE_MINUTE);
        addLeaderboard(FTBULeaderboards.LAST_SEEN);

        addCustomPermPrefix(new NodeEntry("command.", DefaultPermissionLevel.OP, "Permission for commands, if FTBU command overriding is enabled. If not, this node will be inactive"));
        addCustomPermPrefix(new NodeEntry(FTBUPermissions.CLAIMS_BLOCK_BREAK_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can break in claimed chunks"));
        addCustomPermPrefix(new NodeEntry(FTBUPermissions.CLAIMS_BLOCK_INTERACT_PREFIX, DefaultPermissionLevel.OP, "Permission for blocks that players can interact within claimed chunks"));
        addCustomPermPrefix(new NodeEntry(FTBUPermissions.CLAIMS_DIMENSION_ALLOWED_PREFIX, DefaultPermissionLevel.ALL, "Permission for dimensions where claiming chunks is allowed"));

        for(IFTBUtilitiesPlugin p : FTBUtilitiesAPI_Impl.INSTANCE.getAllPlugins())
        {
            p.registerCommon(this);
        }
    }

    public void postInit()
    {
    }

    public void onReloadedClient()
    {
    }

    @Override
    public void addLeaderboard(Leaderboard leaderboard)
    {
        leaderboards.put(leaderboard.getStat(), leaderboard);
    }

    @Override
    public void addCustomPermPrefix(NodeEntry entry)
    {
        customPermPrefixRegistry.add(entry);
    }
}