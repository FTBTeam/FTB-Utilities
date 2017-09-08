package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.lib.config.ConfigNull;
import com.feed_the_beast.ftbl.lib.config.ConfigValue;
import com.feed_the_beast.ftbl.lib.config.RankConfigKey;
import com.feed_the_beast.ftbu.api.IRank;

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
	public IRank getParent()
	{
		return DefaultPlayerRank.INSTANCE;
	}

	@Override
	public ConfigValue getConfig(String id)
	{
		RankConfigKey config = FTBLibAPI.API.getRankConfigRegistry().get(id);
		return config == null ? ConfigNull.INSTANCE : config.getDefOPValue();
	}
}