package latmod.ftbu.mod.config;

import latmod.ftbu.api.guide.GuideInfo;
import latmod.lib.config.*;
import latmod.lib.util.IntBounds;

public class FTBUConfigClaims
{
	public static final ConfigGroup group = new ConfigGroup("claims");
	
	@GuideInfo(info = "Max amount of chunks that player can claim. EnkiTools mod overrides this. 0 - Disabled, -1 - Infinite, Recommended: 25.", def = "0")
	public static final ConfigEntryInt maxClaimsPlayer = new ConfigEntryInt("maxClaimsPlayer", new IntBounds(1000, 0, Integer.MAX_VALUE));
	
	@GuideInfo(info = "Max amount of chunks that admin can claim. EnkiTools mod overrides this. 0 - Disabled, -1 - Infinite, Recommended: 5000.", def = "0")
	public static final ConfigEntryInt maxClaimsAdmin = new ConfigEntryInt("maxClaimsAdmin", new IntBounds(10000, 0, Integer.MAX_VALUE));
	
	public static final ConfigEntryInt forcedExplosions = new ConfigEntryInt("forcedExplosions", new IntBounds(0, -1, 1));
	//public static final ConfigEntryInt forcedBreakMode = new ConfigEntryInt("forcedBreakMode", new IntBounds(-1, -1, 1));
	//public static final ConfigEntryInt forcedPlaceMode = new ConfigEntryInt("forcedPlaceMode", new IntBounds(-1, -1, 1));
	
	public static void load(ConfigFile f)
	{
		group.addAll(FTBUConfigClaims.class);
		f.add(group);
	}
}