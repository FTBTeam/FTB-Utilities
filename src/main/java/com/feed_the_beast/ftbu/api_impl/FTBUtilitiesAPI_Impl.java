package com.feed_the_beast.ftbu.api_impl;

import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.lib.AsmHelper;
import com.feed_the_beast.ftbu.api.FTBUtilitiesAPI;
import com.feed_the_beast.ftbu.api.FTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IFTBUtilitiesPlugin;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunkStorage;
import com.feed_the_beast.ftbu.ranks.Ranks;
import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;

public enum FTBUtilitiesAPI_Impl implements FTBUtilitiesAPI, IPermissionHandler
{
	INSTANCE;

	/**
	 * @author LatvianModder
	 */
	private Collection<IFTBUtilitiesPlugin> plugins;

	public void init(ASMDataTable table)
	{
		plugins = AsmHelper.findPlugins(table, IFTBUtilitiesPlugin.class, FTBUtilitiesPlugin.class);

		for (IFTBUtilitiesPlugin p : plugins)
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
		return Ranks.getRank(profile);
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
		if (context != null && context.getWorld() != null && context.getWorld().isRemote)
		{
			return true;
		}

		switch (getRank(profile).hasPermission(permission))
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