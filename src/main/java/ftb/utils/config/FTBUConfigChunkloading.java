package ftb.utils.config;

import ftb.lib.api.config.*;
import latmod.lib.annotations.*;

public class FTBUConfigChunkloading
{
	@Flags(Flags.SYNC)
	@Info("Enables chunkloading")
	public static final ConfigEntryBool enabled = new ConfigEntryBool("enabled", true);
	
	@Flags(Flags.SYNC)
	@NumberBounds(min = -1D)
	@Info({"Max hours player can be offline until he's chunks unload", "0 - Disabled, will unload instantly when he disconnects", "-1 - Chunk will always be loaded"})
	public static final ConfigEntryDouble max_player_offline_hours = new ConfigEntryDouble("max_player_offline_hours", -1D);
}