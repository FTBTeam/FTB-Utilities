package latmod.ftbu.world.ranks;

import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class RankConfig
{
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", new IntBounds(100, 0, Integer.MAX_VALUE)).setInfo("Max claimable chunk count");
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", new IntBounds(1, 0, Integer.MAX_VALUE)).setInfo("Max home count");
	public final ConfigEntryBool cross_dim_homes = new ConfigEntryBool("cross_dim_homes", true).setInfo("Can use /home to teleport to/from another dimension");
	public final ConfigEntryInt max_loaded_chunks = new ConfigEntryInt("max_loaded_chunks", new IntBounds(64, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can chunkload.\n0 - Disabled");
	public final ConfigGroup custom = new ConfigGroup("custom_config");
}