package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.DefaultRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.IRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
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
public enum FTBUtilitiesPermissionHandler implements IPermissionHandler, IRankConfigHandler
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
			throw new RuntimeException("Do not check permissions on client side! Node: " + nodeS);
		}
		else if (Ranks.INSTANCE == null)
		{
			return DefaultPermissionHandler.INSTANCE.hasPermission(profile, nodeS, context);
		}

		Node node = Node.get(nodeS);
		MinecraftServer server = context != null && context.getWorld() != null ? context.getWorld().getMinecraftServer() : null;

		switch (Ranks.INSTANCE.getRank(server, profile, context).hasPermission(node))
		{
			case ALLOW:
				return true;
			case DENY:
				return false;
			default:
				return DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node.toString()) == DefaultPermissionLevel.ALL || ServerUtils.isOP(server, profile);
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
		ConfigValue value = ConfigNull.INSTANCE;

		if (Ranks.INSTANCE != null)
		{
			value = Ranks.INSTANCE.getRank(server, profile, context).getConfig(node);
		}

		return value.isNull() ? DefaultRankConfigHandler.INSTANCE.getConfigValue(server, profile, node, context) : value;
	}

	@Nullable
	@Override
	public RankConfigValueInfo getInfo(Node node)
	{
		return DefaultRankConfigHandler.INSTANCE.getInfo(node);
	}
}