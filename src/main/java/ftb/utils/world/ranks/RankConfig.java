package ftb.utils.world.ranks;

import ftb.lib.PrivacyLevel;
import ftb.utils.world.claims.ChunkloaderType;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.EnumEnabled;

public class RankConfig
{
	public final ConfigGroup custom = new ConfigGroup("custom_config");
	
	@MinValue(0)
	@MaxValue(30000)
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", 100);
	
	@MinValue(0)
	@MaxValue(30000)
	@Info("Max home count")
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", 1);
	
	@Info("Can use /home to teleport to/from another dimension")
	public final ConfigEntryBool cross_dim_homes = new ConfigEntryBool("cross_dim_homes", true);
	
	@Sync
	@Info({"'-' - Player setting", "'disabled' - Explosions will never happen in claimed chunks\", \"'enabled' - Explosions will always happen in claimed chunks"})
	public final ConfigEntryEnum<EnumEnabled> forced_explosions = new ConfigEntryEnum<>("forced_explosions", EnumEnabled.VALUES, null, true);
	
	@Sync
	@Info({ })
	public final ConfigEntryEnum<PrivacyLevel> forced_chunk_security = new ConfigEntryEnum<>("forced_chunk_security", PrivacyLevel.VALUES_3, null, true);
	
	@Info("Block IDs that you can break in claimed chunks")
	public final ConfigEntryStringArray break_whitelist = new ConfigEntryStringArray("break_whitelist", "OpenBlocks:grave");
	
	@Sync
	@Info("Dimensions where players can't claim")
	public final ConfigEntryIntArray dimension_blacklist = new ConfigEntryIntArray("dimension_blacklist", IntList.asList());
	
	@Info("Display server admin guide information (IDs etc.)")
	public final ConfigEntryBool admin_server_info = new ConfigEntryBool("admin_server_info", false);
	
	@Sync
	@Info("Allow creative players access protected chests / chunks")
	public final ConfigEntryBool allow_creative_interact_secure = new ConfigEntryBool("allow_creative_interact_secure", false);
	
	@Sync
	@Info({"disabled - Players won't be able to chunkload", "normal - Chunks stay loaded when player loggs off", "playerMap - Chunks only stay loaded while owner is online"})
	public final ConfigEntryEnum<ChunkloaderType> chunkloader_type = new ConfigEntryEnum<>("chunkloader_type", ChunkloaderType.values(), ChunkloaderType.OFFLINE, false);
	
	@MinValue(0)
	@MaxValue(30000)
	@Info({"Max amount of chunks that player can chunkload", "0 - Disabled"})
	public final ConfigEntryInt max_loaded_chunks = new ConfigEntryInt("max_loaded_chunks", 50);
	
	@Info("If set to false, players won't be able to see others Rank in FriendsGUI")
	public final ConfigEntryBool show_rank = new ConfigEntryBool("show_rank", true);
	
	@Sync
	@MinValue(-1D)
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects"})
	public final ConfigEntryDouble offline_chunkloader_timer = new ConfigEntryDouble("offline_chunkloader_timer", 24D);
	
	@Info("Badge ID")
	public final ConfigEntryString badge = new ConfigEntryString("badge", "");
	
	public ConfigGroup getAsGroup(String id, boolean copy)
	{
		ConfigGroup g = new ConfigGroup(id);
		g.addAll(RankConfig.class, this, copy);
		return g;
	}
}