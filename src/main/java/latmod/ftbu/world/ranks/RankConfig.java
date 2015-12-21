package latmod.ftbu.world.ranks;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.*;

public class RankConfig
{
	public final ConfigGroup custom = new ConfigGroup("custom_config");
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", new IntBounds(100, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can claim\n0 - Disabled");
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", new IntBounds(1, 0, Integer.MAX_VALUE)).setInfo("Max home count");
	public final ConfigEntryBool cross_dim_homes = new ConfigEntryBool("cross_dim_homes", true).setInfo("Can use /home to teleport to/from another dimension");
	//public final ConfigEntryInt max_loaded_chunks = new ConfigEntryInt("max_loaded_chunks", new IntBounds(64, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can chunkload.\n0 - Disabled");
	
	public final ConfigEntryEnum<EnumEnabled> forced_explosions = ConfigEntryEnum.enabledWithNull("forced_explosions", null).sync().setInfo("'-' - Player setting\n'disabled' - Explosions will never happen in claimed chunks\n'enabled' - Explosions will always happen in claimed chunks");
	public final ConfigEntryEnum<LMSecurityLevel> forced_chunk_security = new ConfigEntryEnum<LMSecurityLevel>("forced_chunk_security", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, null, true).sync();
	public final ConfigEntryStringArray break_whitelist = new ConfigEntryStringArray("break_whitelist", new FastList<String>("OpenBlocks:grave")).setInfo("Block IDs that you can break in claimed chunks");
	public final ConfigEntryIntArray dimension_blacklist = new ConfigEntryIntArray("dimension_blacklist", IntList.asList()).sync().setInfo("Dimensions where players can't claim");
}