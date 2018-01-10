package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.FTBLibModCommon;
import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;

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
	public Rank getParent()
	{
		return this;
	}

	@Override
	public ConfigValue getConfig(String id)
	{
		RankConfigValueInfo config = FTBLibModCommon.RANK_CONFIGS_MIRROR.get(id);
		return config == null ? ConfigNull.INSTANCE : config.defaultValue;
	}
}