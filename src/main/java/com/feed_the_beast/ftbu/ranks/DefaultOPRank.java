package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftblib.FTBLibModCommon;
import com.feed_the_beast.ftblib.lib.config.ConfigNull;
import com.feed_the_beast.ftblib.lib.config.ConfigValue;
import com.feed_the_beast.ftblib.lib.config.RankConfigValueInfo;

/**
 * @author LatvianModder
 */
public class DefaultOPRank extends DefaultRank
{
	public static final DefaultOPRank INSTANCE = new DefaultOPRank();

	private DefaultOPRank()
	{
		super("builtin_op");
	}

	@Override
	public Rank getParent()
	{
		return DefaultPlayerRank.INSTANCE;
	}

	@Override
	public ConfigValue getConfig(String id)
	{
		RankConfigValueInfo config = FTBLibModCommon.RANK_CONFIGS_MIRROR.get(id);
		return config == null ? ConfigNull.INSTANCE : config.defaultOPValue;
	}
}