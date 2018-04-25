package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.IRankConfigHandler;
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
	public void registerNode(String s, DefaultPermissionLevel defaultPermissionLevel, String s1)
	{
		DefaultPermissionHandler.INSTANCE.registerNode(Node.get(s).toString(), defaultPermissionLevel, s1);
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

		Node node = Node.get(permission);

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
	public String getNodeDescription(String s)
	{
		return DefaultPermissionHandler.INSTANCE.getNodeDescription(s);
	}

	@Override
	public ConfigValue getConfigValue(MinecraftServer server, GameProfile profile, Node node, @Nullable IContext context)
	{
		return Ranks.INSTANCE == null ? ConfigNull.INSTANCE : Ranks.INSTANCE.getRank(server, profile, context).getConfig(node);
	}
}