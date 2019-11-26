package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.DefaultRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.IRankConfigHandler;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
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
	public boolean hasPermission(GameProfile profile, String node, @Nullable IContext context)
	{
		if (context != null && context.getWorld() != null)
		{
			if (context.getWorld().isRemote)
			{
				return DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node) == DefaultPermissionLevel.ALL;
			}
		}
		else if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
			return DefaultPermissionHandler.INSTANCE.getDefaultPermissionLevel(node) == DefaultPermissionLevel.ALL;
		}

		if (profile.getId() == null) //TODO: PR this fix in Forge
		{
			if (profile.getName() == null)
			{
				return false;
			}

			profile = new GameProfile(EntityPlayer.getOfflineUUID(profile.getName()), profile.getName());
		}

		switch (Ranks.INSTANCE.getPermissionResult(profile, node, true))
		{
			case ALLOW:
				return true;
			case DENY:
				return false;
			default:
				return DefaultPermissionHandler.INSTANCE.hasPermission(profile, node, context);
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
	public ConfigValue getConfigValue(MinecraftServer server, GameProfile profile, String node)
	{
		ConfigValue value = ConfigNull.INSTANCE;

		if (Ranks.isActive())
		{
			value = Ranks.INSTANCE.getPermission(profile, node, true);
		}

		return value.isNull() ? DefaultRankConfigHandler.INSTANCE.getConfigValue(server, profile, node) : value;
	}

	@Nullable
	@Override
	public RankConfigValueInfo getInfo(String node)
	{
		return DefaultRankConfigHandler.INSTANCE.getInfo(node);
	}
}