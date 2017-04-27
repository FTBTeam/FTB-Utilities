package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.gui.IDrawableObject;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.ILeaderboardData;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.events.FTBUtilitiesRegistryEvent;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import com.feed_the_beast.ftbu.cmd.CmdInternalClient;
import net.minecraft.stats.StatBase;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

public class FTBUCommon implements IFTBUtilitiesRegistry // FTBUClient
{
    public static final Collection<Leaderboard> LEADERBOARDS = new ArrayList<>();
    public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
    public static final IChunkUpgrade[] CHUNK_UPGRADES = new IChunkUpgrade[32];

    public void preInit()
    {
        FTBULeaderboards.addLeaderboards(this);
        FTBUPermissions.addCustomPerms(this);
        ChunkUpgrade.addUpgrades(this);
        MinecraftForge.EVENT_BUS.post(new FTBUtilitiesRegistryEvent(this));
    }

    public void postInit()
    {
    }

    public void onReloadedClient()
    {
    }

    public void registerClientCommands(CmdInternalClient cmd)
    {
    }

    @Override
    public void addLeaderboard(StatBase stat, @Nullable Comparator<IForgePlayer> comparator, ILeaderboardData data, ITextComponent component, IDrawableObject icon)
    {
        LEADERBOARDS.add(new Leaderboard(stat, comparator, data, component, icon));
    }

    @Override
    public void addCustomPermPrefix(NodeEntry entry)
    {
        CUSTOM_PERM_PREFIX_REGISTRY.add(entry);
    }

    @Override
    public void addChunkUpgrade(IChunkUpgrade upgrade)
    {
        CHUNK_UPGRADES[upgrade.getId()] = upgrade;
    }
}