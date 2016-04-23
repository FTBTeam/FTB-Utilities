package ftb.utils.world.ranks;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.config.ConfigEntryBool;
import ftb.lib.api.config.ConfigEntryEnum;
import ftb.lib.api.config.ConfigEntryInt;
import ftb.lib.api.config.ConfigEntryIntList;
import ftb.lib.api.config.ConfigEntryString;
import ftb.lib.api.config.ConfigEntryStringList;
import ftb.lib.api.config.ConfigGroup;
import latmod.lib.IntList;
import latmod.lib.annotations.Flags;
import latmod.lib.annotations.Info;
import latmod.lib.annotations.NumberBounds;
import latmod.lib.util.EnumEnabled;

import java.util.Collections;

public class RankConfig
{
	public final ConfigGroup custom = new ConfigGroup("custom_config");
	
	@NumberBounds(min = 0, max = 30000)
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", 100);
	
	@NumberBounds(min = 0, max = 30000)
	@Info("Max home count")
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", 1);
	
	@Info("Can use /home to teleport to/from another dimension")
	public final ConfigEntryBool cross_dim_homes = new ConfigEntryBool("cross_dim_homes", true);
	
	@Flags(Flags.SYNC)
	@Info({"'-' - Player setting", "'disabled' - Explosions will never happen in claimed chunks\", \"'enabled' - Explosions will always happen in claimed chunks"})
	public final ConfigEntryEnum<EnumEnabled> forced_explosions = new ConfigEntryEnum<>("forced_explosions", EnumEnabled.values(), null, true);
	
	@Flags(Flags.SYNC)
	public final ConfigEntryEnum<PrivacyLevel> forced_chunk_security = new ConfigEntryEnum<>("forced_chunk_security", PrivacyLevel.VALUES_3, null, true);
	
	@Info("Block IDs that you can break in claimed chunks")
	public final ConfigEntryStringList break_whitelist = new ConfigEntryStringList("break_whitelist", Collections.singletonList("OpenBlocks:grave"));
	
	@Flags(Flags.SYNC)
	@Info("Dimensions where players can't claim")
	public final ConfigEntryIntList dimension_blacklist = new ConfigEntryIntList("dimension_blacklist", IntList.asList());
	
	@Flags(Flags.SYNC)
	@Info("Allow creative players access protected chests / chunks")
	public final ConfigEntryBool allow_creative_interact_secure = new ConfigEntryBool("allow_creative_interact_secure", false);
	
	@NumberBounds(min = 0, max = 30000)
	@Info({"Max amount of chunks that player can chunkload", "0 - Disabled"})
	public final ConfigEntryInt max_loaded_chunks = new ConfigEntryInt("max_loaded_chunks", 50);
	
	@Info("If set to false, players won't be able to see others Rank in FriendsGUI")
	public final ConfigEntryBool show_rank = new ConfigEntryBool("show_rank", true);
	
	@Info("Badge ID")
	public final ConfigEntryString badge = new ConfigEntryString("badge", "");
	
	public ConfigGroup getAsGroup(String id, boolean copy)
	{
		ConfigGroup g = new ConfigGroup(id);
		g.addAll(RankConfig.class, this, copy);
		return g;
	}
}