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
	@Info("Enabled access to protected chests / chunks")
	public static final ForgePermission allow_interact_secure = new ForgePermission("ftbu.misc.allow_interact_secure", false, true);
	
	// Display //
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Display 'Admin' in Server Info")
	public static final ForgePermission display_admin_info = new ForgePermission("ftbu.display.admin_info", false, true);
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Display Rank in FriendsGUI")
	public static final ForgePermission display_rank = new ForgePermission("ftbu.display.rank", true, true);
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Display 'My Permissions' in Server Info")
	public static final ForgePermission display_permissions = new ForgePermission("ftbu.display.permissions", true, true);
	
	// Homes //
	
	@ConfigType(PrimitiveType.BOOLEAN)
	@Info("Can use /home to teleport to/from another dimension")
	public static final ForgePermission homes_cross_dim = new ForgePermission("ftbu.homes.cross_dim", true, true);
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info("Max home count")
	public static final ForgePermission homes_max = new ForgePermission("ftbu.homes.max", 1, 100);
	
	// Claims //
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info({"Max amount of chunks that player can claim", "0 - Disabled"})
	public static final ForgePermission claims_max_chunks = new ForgePermission("ftbu.claims.max_chunks", 100, 1000);
	
	@ConfigType(PrimitiveType.ENUM)
	@Info({"-: Player setting", "disabled: Explosions will never happen in claimed chunks", "enabled: Explosions will always happen in claimed chunks"})
	public static final ForgePermissionEnum<EnumEnabled> claims_forced_explosions = new ForgePermissionEnum<>("ftbu.claims.forced_explosions", null, null, EnumEnabled.VALUES, true);
	
	@ConfigType(PrimitiveType.ENUM)
	public static final ForgePermissionEnum<PrivacyLevel> claims_forced_security = new ForgePermissionEnum<>("ftbu.claims.forced_security", null, null, PrivacyLevel.VALUES_3, true);
	
	@ConfigType(PrimitiveType.STRING_ARRAY)
	@Info("Block IDs that player can break in claimed chunks")
	public static final ForgePermission claims_break_whitelist = new ForgePermission("ftbu.claims.break_whitelist", new String[] {"OpenBlocks:grave"}, new String[] {"*"});
	
	@ConfigType(PrimitiveType.INT_ARRAY)
	@Info("Dimensions where players can't claim")
	public static final ForgePermission claims_dimension_blacklist = new ForgePermission("ftbu.claims.dimension_blacklist", new Number[] {1}, new Number[0]);
	
	// Chunkloader //
	
	@ConfigType(PrimitiveType.ENUM)
	@Info({"disabled: Players won't be able to chunkload", "offline: Chunks stay loaded when player loggs off", "online: Chunks only stay loaded while owner is online"})
	public static final ForgePermissionEnum<ChunkloaderType> chunkloader_type = new ForgePermissionEnum<>("ftbu.chunkloader.type", ChunkloaderType.OFFLINE, ChunkloaderType.OFFLINE, ChunkloaderType.values(), false);
	
	@MinValue(0)
	@MaxValue(30000)
	@ConfigType(PrimitiveType.INT)
	@Info({"Max amount of chunks that player can load", "0 - Disabled"})
	public static final ForgePermission chunkloader_max_chunks = new ForgePermission("ftbu.chunkloader.max_chunks", 50, 5000);
	
	@MinValue(-1D)
	@ConfigType(PrimitiveType.DOUBLE)
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Disabled (will never unload)"})
	public static final ForgePermission chunkloader_offline_timer = new ForgePermission("ftbu.chunkloader.offline_timer", 24D, -1D);
}
