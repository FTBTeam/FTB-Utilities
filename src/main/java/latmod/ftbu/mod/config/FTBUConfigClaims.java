package latmod.ftbu.mod.config;

import ftb.lib.api.config.ConfigSyncRegistry;
import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigClaims
{
	public static final ConfigGroup group = new ConfigGroup("claims");
	
	@GuideInfo(info = "Max amount of chunks that player can claim. 0 - Disabled, Recommended: 25.", def = "500")
	public static final ConfigEntryInt maxClaimsPlayer = new ConfigEntryInt("maxClaimsPlayer", new IntBounds(500, 0, Integer.MAX_VALUE));
	
	@GuideInfo(info = "Max amount of chunks that admin can claim. 0 - Disabled.", def = "1000")
	public static final ConfigEntryInt maxClaimsAdmin = new ConfigEntryInt("maxClaimsAdmin", new IntBounds(1000, 0, Integer.MAX_VALUE));
	
	@GuideInfo(info = "-1 - Player setting, 0 - Explosions will never happen in claimed chunks, 1 - Explosions will always happen in claimed chunks.", def = "1")
	public static final ConfigEntryInt forcedExplosions = new ConfigEntryInt("forcedExplosions", new IntBounds(1, -1, 1));
	
	@GuideInfo(info = "-1 - Player setting, 0 - Public, 1 - Private, 2 - Friends.", def = "-1")
	public static final ConfigEntryInt forcedChunkSecurity = new ConfigEntryInt("forcedChunkSecurity", new IntBounds(-1, -1, 2));
	
	@GuideInfo(info = "Block IDs that you can break in claimed chunks.", def = "[ \"OpenBlocks:grave\" ]")
	public static final ConfigEntryStringArray breakWhitelist = new ConfigEntryStringArray("breakWhitelist", new String[] { "OpenBlocks:grave" });
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigClaims.class);
		f.add(group);
		
		ConfigSyncRegistry.add(forcedExplosions);
	}
}