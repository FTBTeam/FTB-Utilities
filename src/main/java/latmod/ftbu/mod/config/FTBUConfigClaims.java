package latmod.ftbu.mod.config;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigClaims
{
	public static final ConfigGroup group = new ConfigGroup("claims");
	public static final ConfigEntryInt maxClaimsPlayer = new ConfigEntryInt("maxClaimsPlayer", new IntBounds(500, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can claim.\n0 - Disabled\n25 - Recommended");
	public static final ConfigEntryInt maxClaimsAdmin = new ConfigEntryInt("maxClaimsAdmin", new IntBounds(1000, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that admin can claim.\n0 - Disabled");
	public static final ConfigEntryEnum<EnumEnabled> forcedExplosions = new ConfigEntryEnum<EnumEnabled>("forcedExplosions", EnumEnabled.class, EnumEnabled.VALUES, null, true).sync().setInfo("- - Player setting, disabled - Explosions will never happen in claimed chunks, enabled - Explosions will always happen in claimed chunks");
	public static final ConfigEntryEnum<LMSecurityLevel> forcedChunkSecurity = new ConfigEntryEnum<LMSecurityLevel>("forcedChunkSecurity", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, null, true).sync();
	public static final ConfigEntryStringArray breakWhitelist = new ConfigEntryStringArray("breakWhitelist", FastList.asList("OpenBlocks:grave")).setInfo("Block IDs that you can break in claimed chunks");
	public static final ConfigEntryIntArray dimensionBlacklist = new ConfigEntryIntArray("dimensionBlacklist", IntList.asList()).sync().setInfo("Dimensions where players can't claim");
}