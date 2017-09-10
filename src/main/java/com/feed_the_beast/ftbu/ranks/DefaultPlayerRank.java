package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.config.ConfigNull;
import com.feed_the_beast.ftbl.lib.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.RankConfigValueInfo;
import com.feed_the_beast.ftbu.api.IRank;

/**
 * @author LatvianModder
 */
public class DefaultPlayerRank extends DefaultRank
{
	public static final DefaultPlayerRank INSTANCE = new DefaultPlayerRank();

	private DefaultPlayerRank()
	{
		super("builtin_player");
	}

	@Override
	public IRank getParent()
	{
		return this;
	}

	@Override
	public ConfigValue getConfig(String id)
	{
		RankConfigValueInfo config = FTBLibAPI.API.getRankConfigRegistry().get(id);
		return config == null ? ConfigNull.INSTANCE : config.defaultValue;
	}
}