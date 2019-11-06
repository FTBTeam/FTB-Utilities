package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author LatvianModder
 */
public class PlayerRank extends Rank
{
	public final UUID uuid;
	public final GameProfile profile;
	private final Map<String, String> stringCache;
	private final Map<String, ConfigValue> valueCache;

	PlayerRank(Ranks r, UUID id, String name)
	{
		super(r, StringUtils.fromUUID(id));
		displayName = new TextComponentString(name.isEmpty() ? getId() : name);
		displayName.getStyle().setColor(TextFormatting.YELLOW);
		uuid = id;
		comment = name;
		profile = new GameProfile(id, name.isEmpty() ? null : name);
		stringCache = new HashMap<>();
		valueCache = new HashMap<>();
	}

	@Override
	public int getPower()
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isPlayer()
	{
		return true;
	}

	@Override
	public boolean add()
	{
		return ranks.playerRanks.put(uuid, this) != this;
	}

	@Override
	public boolean remove()
	{
		if (!permissions.isEmpty() || !getParents().isEmpty())
		{
			permissions.clear();
			clearParents();
			return true;
		}

		return false;
	}

	@Override
	public boolean isDefaultPlayerRank()
	{
		return false;
	}

	@Override
	public boolean isDefaultOPRank()
	{
		return false;
	}

	@Override
	public Set<Rank> getActualParents()
	{
		List<Rank> list = new ArrayList<>();

		for (String s : getLocalPermission(NODE_PARENT).split(","))
		{
			Rank r = ranks.getRank(s.trim());

			if (r != null && !r.isPlayer())
			{
				list.add(r);
			}
		}

		if (ServerUtils.isOP(ranks.universe.server, profile))
		{
			Rank r = ranks.getDefaultOPRank();

			if (r != null)
			{
				list.add(r);
			}
		}

		Rank r = ranks.getDefaultPlayerRank();

		if (r != null)
		{
			list.add(r);
		}

		list.sort(null);
		return new LinkedHashSet<>(list);
	}

	@Override
	public String getPermission(String originalNode, String node, boolean recursive)
	{
		String s = stringCache.get(node);

		if (s != null)
		{
			return s;
		}

		s = super.getPermission(originalNode, node, recursive);
		stringCache.put(node, s);
		return s;
	}

	@Override
	public ConfigValue getPermissionValue(String originalNode, String node, boolean recursive)
	{
		ConfigValue v = valueCache.get(node);

		if (v == null)
		{
			v = super.getPermissionValue(originalNode, node, recursive);
			valueCache.put(node, v);
		}

		return v;
	}

	@Override
	public void clearCache()
	{
		super.clearCache();
		stringCache.clear();
		valueCache.clear();
	}
}