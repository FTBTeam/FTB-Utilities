package ftb.utils.mod.handlers.ftbl;

import ftb.lib.PrivacyLevel;
import ftb.lib.api.permissions.*;
import ftb.utils.world.claims.ChunkloaderType;
import latmod.lib.Info;
import latmod.lib.config.*;
import latmod.lib.util.EnumEnabled;

/**
 * Created by LatvianModder on 14.02.2016.
 */
public class FTBUPermissions
{
	@Info("Can use /home to teleport to/from another dimension")
	public static final ForgePermission cross_dim_homes = new ForgePermission("ftbu.cross_dim_homes", true, true);
	
	@Info("Display server admin guide information (IDs etc.)")
	public static final ForgePermission admin_server_info = new ForgePermission("ftbu.admin_server_info", false, true);
	
	@Sync
	@Info("If set to true, creative players will be able to access protected chests / chunks")
	public static final ForgePermission allow_creative_interact_secure = new ForgePermission("ftbu.allow_creative_interact_secure", false, true);
	
	@Info("If set to false, playerMap won't be able to see others Rank in FriendsGUI")
	public static final ForgePermission show_rank = new ForgePermission("ftbu.show_rank", true, true);
	
	@Sync
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	@MinValue(0)
	@MaxValue(30000)
	public static final ForgePermission max_claims = new ForgePermission("max_claims", 100, 1000);
	
	@Info("Max home count")
	@MinValue(0)
	public static final ForgePermission max_homes = new ForgePermission("max_homes", 1, 100);
	
	@Sync
	@Info({"'-' - Player setting", "'disabled' - Explosions will never happen in claimed chunks", "'enabled' - Explosions will always happen in claimed chunks"})
	public static final ForgePermissionEnum<EnumEnabled> forced_explosions = new ForgePermissionEnum<>("forced_explosions", null, null, EnumEnabled.VALUES, true);
	
	@Sync
	public static final ForgePermissionEnum<PrivacyLevel> forced_chunk_security = new ForgePermissionEnum<>("forced_chunk_security", null, null, PrivacyLevel.VALUES_3, true);
	
	@Info("Block IDs that you can break in claimed chunks")
	public static final ForgePermission break_whitelist = new ForgePermission("break_whitelist", "OpenBlocks:grave", "*");
	
	@Sync
	@Info("Dimensions where playerMap can't claim")
	public static final ForgePermission dimension_blacklist = new ForgePermission("dimension_blacklist", new Number[] {1}, new Number[0]);
	
	@Sync
	@Info({"disabled - Players won't be able to chunkload", "normal - Chunks stay loaded when player loggs off", "playerMap - Chunks only stay loaded while owner is online"})
	public static final ForgePermissionEnum<ChunkloaderType> chunkloader_type = new ForgePermissionEnum<>("chunkloader_type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false);
	
	@Sync
	@Info("Max amount of chunks that player can chunkload.\n" + "0 - Disabled")
	@MinValue(0)
	@MaxValue(30000)
	public static final ForgePermission max_loaded_chunks = new ForgePermission("max_loaded_chunks", 50, 5000);
	
	@Sync
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Disabled (will never unload)"})
	@MinValue(-1)
	public static final ForgePermission offline_chunkloader_timer = new ForgePermission("offline_chunkloader_timer", 24D, -1D);
	
	@Info("Badge ID")
	public static final ForgePermission badge = new ForgePermission("badge", "", "");
}
