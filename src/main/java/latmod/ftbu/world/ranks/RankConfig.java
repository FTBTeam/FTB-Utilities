package latmod.ftbu.world.ranks;

import latmod.ftbu.util.LMSecurityLevel;
import latmod.ftbu.world.claims.ChunkloaderType;
import latmod.lib.IntList;
import latmod.lib.config.*;
import latmod.lib.util.*;
import net.minecraft.entity.player.EntityPlayer;

public class RankConfig
{
	public final ConfigGroup custom = new ConfigGroup("custom_config");
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", new IntBounds(100, 0, 30000)).sync().setInfo("Max amount of chunks that player can claim\n0 - Disabled");
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", new IntBounds(1, 0, Integer.MAX_VALUE)).setInfo("Max home count");
	public final ConfigEntryBool cross_dim_homes = new ConfigEntryBool("cross_dim_homes", true).setInfo("Can use /home to teleport to/from another dimension");
	public final ConfigEntryEnum<EnumEnabled> forced_explosions = ConfigEntryEnum.enabledWithNull("forced_explosions", null).sync().setInfo("'-' - Player setting\n'disabled' - Explosions will never happen in claimed chunks\n'enabled' - Explosions will always happen in claimed chunks");
	public final ConfigEntryEnum<LMSecurityLevel> forced_chunk_security = new ConfigEntryEnum<>("forced_chunk_security", LMSecurityLevel.class, LMSecurityLevel.VALUES_3, null, true).sync();
	public final ConfigEntryStringArray break_whitelist = new ConfigEntryStringArray("break_whitelist", "OpenBlocks:grave").setInfo("Block IDs that you can break in claimed chunks");
	public final ConfigEntryIntArray dimension_blacklist = new ConfigEntryIntArray("dimension_blacklist", IntList.asList()).sync().setInfo("Dimensions where playerMap can't claim");
	public final ConfigEntryBool admin_server_info = new ConfigEntryBool("admin_server_info", false).setInfo("Display server admin guide information (IDs etc.)");
	public final ConfigEntryBool allow_creative_interact_secure = new ConfigEntryBool("allow_creative_interact_secure", false).sync().setInfo("If set to true, creative playerMap will be able to access protected chests / chunks");
	public final ConfigEntryEnum<ChunkloaderType> chunkloader_type = new ConfigEntryEnum<>("chunkloader_type", ChunkloaderType.class, ChunkloaderType.values(), ChunkloaderType.OFFLINE, false).sync().setInfo("disabled - Players won't be able to chunkload\nnormal - Chunks stay loaded when player loggs off\nplayerMap - Chunks only stay loaded while owner is online");
	public final ConfigEntryInt max_loaded_chunks = new ConfigEntryInt("max_loaded_chunks", new IntBounds(50, 0, 30000)).sync().setInfo("Max amount of chunks that player can chunkload.\n0 - Disabled");
	public final ConfigEntryBool show_rank = new ConfigEntryBool("show_rank", true).setInfo("If set to false, playerMap won't be able to see others Rank in FriendsGUI");
	public final ConfigEntryDouble offline_chunkloader_timer = new ConfigEntryDouble("offline_chunkloader_timer", new DoubleBounds(24D, 0D, Double.POSITIVE_INFINITY)).sync().setInfo("Max hours player can be offline until he's chunks unload. 0 - Disabled, will unload instantly when he disconnects");
	public final ConfigEntryString badge = new ConfigEntryString("badge", "").setInfo("Badge ID");

	public boolean allowCreativeInteractSecure(EntityPlayer ep)
	{ return ep != null && allow_creative_interact_secure.get() && ep.capabilities.isCreativeMode/* && !(ep instanceof FakePlayer)*/; }
}