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
	public static final ConfigEntryEnum<EnumEnabled> forcedExplosions = new ConfigEntryEnum<EnumEnabled>("forcedExplosions", EnumEnabled.class, EnumEnabled.VALUES, null, true).sync().setInfo("- - Player setting\ndisabled - Explosions will never happen in claimed chunks\nenabled - Explosions will always happen in claimed chunks");
	public static final ConfigEntryEnum<LMSecurityLevel> forcedChunkSecurity = new ConfigEntryEnum<LMSecurityLevel>("forcedChunkSecurity", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, null, true).sync();
	public static final ConfigEntryStringArray breakWhitelist = new ConfigEntryStringArray("breakWhitelist", FastList.asList("OpenBlocks:grave")).setInfo("Block IDs that you can break in claimed chunks");
	public static final ConfigEntryIntArray dimensionBlacklist = new ConfigEntryIntArray("dimensionBlacklist", IntList.asList()).sync().setInfo("Dimensions where players can't claim");
	//public static final ConfigEntryEnum<ChunkloaderType> chunkloaders = new ConfigEntryEnum<ChunkloaderType>("chunkloaders", ChunkloaderType.class, ChunkloaderType.values(), ChunkloaderType.DISABLED, false).sync().setInfo("disabled - Players won't be able to chunkload\nnormal - Chunks stay loaded when player loggs off\nplayers - Chunks only stay loaded while owner is online");
	//public static final ConfigEntryInt maxLoadedChunksPlayer = new ConfigEntryInt("maxLoadedChunksPlayer", new IntBounds(64, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that player can chunkload.\n0 - Disabled");
	//public static final ConfigEntryInt maxLoadedChunksAdmin = new ConfigEntryInt("maxLoadedChunksAdmin", new IntBounds(1000, 0, Integer.MAX_VALUE)).setInfo("Max amount of chunks that admin can chunkload.\n0 - Disabled");
}