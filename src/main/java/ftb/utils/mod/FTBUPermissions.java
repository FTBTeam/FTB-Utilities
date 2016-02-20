package ftb.utils.mod;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.permission.*;
import ftb.utils.world.claims.ChunkloaderType;
import latmod.lib.*;
import latmod.lib.config.*;
import latmod.lib.util.EnumEnabled;

/**
 * Created by LatvianModder on 20.02.2016.
 */
public class FTBUPermissions
{
	// Misc //
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Display server admin guide information (IDs etc.)")
	public static final ForgePermission admin_server_info = new ForgePermission("ftbu.misc.display.admin_server_info", false, true);
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("If set to false, playerMap won't be able to see others Rank in FriendsGUI")
	public static final ForgePermission show_rank = new ForgePermission("ftbu.misc.display.rank", true, true);
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("If set to true, creative players will be able to access protected chests / chunks")
	public static final ForgePermission allow_interact_secure = new ForgePermission("ftbu.misc.allow_interact_secure", false, true);
	
	// Homes //
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Can use /home to teleport to/from another dimension")
	public static final ForgePermission cross_dim_homes = new ForgePermission("ftbu.homes.cross_dim", true, true);
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info("Max home count")
	public static final ForgePermission max_homes = new ForgePermission("ftbu.homes.max", 1, 100);
	
	// Claims //
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	public static final ForgePermission max_claims = new ForgePermission("ftbu.claims.max_chunks", 100, 1000);
	
	@ConfigType(PrimitiveType.ENUM)
	@Info({"'-' - Player setting", "'disabled' - Explosions will never happen in claimed chunks", "'enabled' - Explosions will always happen in claimed chunks"})
	public static final ForgePermissionEnum<EnumEnabled> forced_explosions = new ForgePermissionEnum<>("ftbu.claims.forced_explosions", null, null, EnumEnabled.VALUES, true);
	
	@ConfigType(PrimitiveType.ENUM)
	public static final ForgePermissionEnum<PrivacyLevel> forced_chunk_security = new ForgePermissionEnum<>("ftbu.claims.forced_security", null, null, PrivacyLevel.VALUES_3, true);
	
	@ConfigType(PrimitiveType.STRING_ARRAY)
	@Info("Block IDs that you can break in claimed chunks")
	public static final ForgePermission break_whitelist = new ForgePermission("ftbu.claims.break_whitelist", new String[] {"OpenBlocks:grave"}, new String[0]);
	
	@ConfigType(PrimitiveType.INT_ARRAY)
	@Info("Dimensions where playerMap can't claim")
	public static final ForgePermission dimension_blacklist = new ForgePermission("ftbu.claims.dimension_blacklist", new Number[] {1}, new Number[0]);
	
	// Chunkloader //
	
	@ConfigType(PrimitiveType.ENUM)
	@Info({"disabled - Players won't be able to chunkload", "normal - Chunks stay loaded when player loggs off", "playerMap - Chunks only stay loaded while owner is online"})
	public static final ForgePermissionEnum<ChunkloaderType> chunkloader_type = new ForgePermissionEnum<>("ftbu.chunkloader.type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false);
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info("Max amount of chunks that player can chunkload.\n" + "0 - Disabled")
	public static final ForgePermission max_loaded_chunks = new ForgePermission("ftbu.chunkloader.max_chunks", 50, 5000);
	
	@MinValue(-1D)
	@ConfigType(PrimitiveType.DOUBLE)
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Disabled (will never unload)"})
	public static final ForgePermission offline_chunkloader_timer = new ForgePermission("ftbu.chunkloader.offline_timer", 24D, -1D);
}
