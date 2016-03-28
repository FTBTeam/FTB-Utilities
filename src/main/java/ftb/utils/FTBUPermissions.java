package ftb.utils;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.permissions.*;
import ftb.utils.world.ChunkloaderType;
import latmod.lib.annotations.*;
import latmod.lib.util.EnumEnabled;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
	// Display //
	
	@ForgePermission(false)
	@Info("Display 'Admin' in Server Info")
	public static final String display_admin_info = "ftbu.display.admin_info";
	
	@ForgePermission(true)
	@Info("Display Rank in FriendsGUI")
	public static final String display_rank = "ftbu.display.rank";
	
	@ForgePermission(true)
	@Info("Display 'My Permissions' in Server Info")
	public static final String display_permissions = "ftbu.display.permissions";
	
	// Homes //
	
	@ForgePermission(true)
	@Info("Can use /home to teleport to/from another dimension")
	public static final String homes_cross_dim = "ftbu.homes.cross_dim";
	
	//@ConfigType(PrimitiveType.INT)
	@NumberBounds(min = 0, max = 30000)
	@Info("Max home count")
	public static final RankConfig homes_max = new RankConfig("ftbu.homes.max", 1, 100);
	
	// Claims //
	
	//@ConfigType(PrimitiveType.INT)
	@NumberBounds(min = 0, max = 30000)
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	public static final RankConfig claims_max_chunks = new RankConfig("ftbu.claims.max_chunks", 100, 1000);
	
	//@ConfigType(PrimitiveType.ENUM)
	@Info({"-: Player setting", "disabled: Explosions will never happen in claimed chunks", "enabled: Explosions will always happen in claimed chunks"})
	public static final RankConfigEnum<EnumEnabled> claims_forced_explosions = new RankConfigEnum<>("ftbu.claims.forced_explosions", null, null, EnumEnabled.VALUES, true);
	
	//@ConfigType(PrimitiveType.ENUM)
	public static final RankConfigEnum<PrivacyLevel> claims_forced_security = new RankConfigEnum<>("ftbu.claims.forced_security", null, null, PrivacyLevel.VALUES_3, true);
	
	//@ConfigType(PrimitiveType.STRING_ARRAY)
	@Info("Block IDs that player can break in claimed chunks")
	public static final RankConfig claims_break_whitelist = new RankConfig("ftbu.claims.break_whitelist", new String[] {"OpenBlocks:grave"}, new String[] {"*"});
	
	//@ConfigType(PrimitiveType.INT_ARRAY)
	@Info("Dimensions where players can't claim")
	public static final RankConfig claims_dimension_blacklist = new RankConfig("ftbu.claims.dimension_blacklist", new Number[] {1}, new Number[0]);
	
	// Chunkloader //
	
	//@ConfigType(PrimitiveType.ENUM)
	@Info({"disabled: Players won't be able to chunkload", "offline: Chunks stay loaded when player loggs off", "online: Chunks only stay loaded while owner is online"})
	public static final RankConfigEnum<ChunkloaderType> chunkloader_type = new RankConfigEnum<>("ftbu.chunkloader.type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false);
	
	//@ConfigType(PrimitiveType.INT)
	@NumberBounds(min = 0, max = 30000)
	@Info({"Max amount of chunks that player can load", "0 - Disabled"})
	public static final RankConfig chunkloader_max_chunks = new RankConfig("ftbu.chunkloader.max_chunks", 50, 5000);
	
	//@ConfigType(PrimitiveType.DOUBLE)
	@NumberBounds(min = -1D)
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Disabled (will never unload)"})
	public static final RankConfig chunkloader_offline_timer = new RankConfig("ftbu.chunkloader.offline_timer", 24D, -1D);
}
