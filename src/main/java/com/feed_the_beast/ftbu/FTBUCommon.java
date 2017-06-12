package com.feed_the_beast.ftbu;

import com.feed_the_beast.ftbu.api.IFTBUtilitiesRegistry;
import com.feed_the_beast.ftbu.api.NodeEntry;
import com.feed_the_beast.ftbu.api.chunks.IChunkUpgrade;
import com.feed_the_beast.ftbu.api.events.FTBUtilitiesRegistryEvent;
import com.feed_the_beast.ftbu.api_impl.ChunkUpgrade;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.HashSet;

public class FTBUCommon implements IFTBUtilitiesRegistry // FTBUClient
{
	public static final Collection<NodeEntry> CUSTOM_PERM_PREFIX_REGISTRY = new HashSet<>();
	public static final IChunkUpgrade[] CHUNK_UPGRADES = new IChunkUpgrade[32];

	public void preInit()
	{
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