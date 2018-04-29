package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.DefaultRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.IRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.IPermissionHandler;
import net.minecraftforge.server.permission.context.IContext;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author LatvianModder
 */
public enum FTBUPermissionHandler implements IPermissionHandler, IRankConfigHandler
{
	INSTANCE;

	@Override
	public void registerNode(String node, DefaultPermissionLevel level, String desc)
	{
		DefaultPermissionHandler.INSTANCE.registerNode(node, level, desc);
	}

	@Override
	public Collection<String> getRegisteredNodes()
	{
		return DefaultPermissionHandler.INSTANCE.getRegisteredNodes();
	}

	@Override
	public boolean hasPermission(GameProfile profile, String nodeS, @Nullable IContext context)
	{
		if (context != null && context.getWorld() != null && context.getWorld().isRemote)
		{
			return true;
		}

		if (Ranks.INSTANCE == null)
		{
			return DefaultPermissionHandler.INSTANCE.hasPermission(profile, nodeS, context);
		}

		Node node = Node.get(nodeS);

		switch (Ranks.INSTANCE.getRank(context != null && context.getWorld() != null ? context.getWorld().getMinecraftServer() : null, profile, context).hasPermission(node))
		{
			case ALLOW:
				return true;
			case DENY:
				return false;
			default:
				return DefaultPermissionHandler.INSTANCE.hasPermission(profile, node.toString(), context);
		}
	}

	@Override
	public String getNodeDescription(String node)
	{
		return DefaultPermissionHandler.INSTANCE.getNodeDescription(node);
	}

	@Override
	public void registerRankConfig(RankConfigValueInfo info)
	{
		DefaultRankConfigHandler.INSTANCE.registerRankConfig(info);
	}

	@Override
	public Collection<RankConfigValueInfo> getRegisteredConfigs()
	{
		return DefaultRankConfigHandler.INSTANCE.getRegisteredConfigs();
	}

	@Override
	public ConfigValue getConfigValue(MinecraftServer server, GameProfile profile, Node node, @Nullable IContext context)
	{
		if (Ranks.INSTANCE == null)
		{
			return DefaultRankConfigHandler.INSTANCE.getConfigValue(server, profile, node, context);
		}
		else
		{
			return Ranks.INSTANCE.getRank(server, profile, context).getConfig(node);
		}
	}

	@Nullable
	@Override
	public RankConfigValueInfo getInfo(Node node)
	{
		return DefaultRankConfigHandler.INSTANCE.getInfo(node);
	}
}