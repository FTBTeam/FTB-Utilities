package latmod.ftbu.mod.config;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigClaims
{
	public static final ConfigGroup group = new ConfigGroup("claims");
	public static final ConfigEntryInt max_claims_player = new ConfigEntryInt("max_claims_player", new IntBounds(500, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can claim.\n0 - Disabled\n25 - Recommended");
	public static final ConfigEntryInt max_claims_admin = new ConfigEntryInt("max_claims_admin", new IntBounds(1000, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that admin can claim.\n0 - Disabled");
	public static final ConfigEntryEnum<EnumEnabled> forced_explosions = new ConfigEntryEnum<EnumEnabled>("forced_explosions", EnumEnabled.class, EnumEnabled.VALUES, null, true).sync().setInfo("- - Player setting\ndisabled - Explosions will never happen in claimed chunks\nenabled - Explosions will always happen in claimed chunks");
	public static final ConfigEntryEnum<LMSecurityLevel> forced_chunk_security = new ConfigEntryEnum<LMSecurityLevel>("forced_chunk_security", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, null, true).sync();
	public static final ConfigEntryStringArray break_whitelist = new ConfigEntryStringArray("break_whitelist", new FastList<String>("OpenBlocks:grave")).setInfo("Block IDs that you can break in claimed chunks");
	public static final ConfigEntryIntArray dimension_blacklist = new ConfigEntryIntArray("dimension_blacklist", IntList.asList()).sync().setInfo("Dimensions where players can't claim");
}