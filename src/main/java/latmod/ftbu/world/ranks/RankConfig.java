package latmod.ftbu.world.ranks;

import latmod.ftbu.mod.config.*;
import latmod.ftbu.world.LMPlayerServer;
import latmod.lib.config.ConfigEntryInt;
import latmod.lib.util.IntBounds;

public class RankConfig
{
	public static LMPlayerServer currentPlayer = null;
	
	public final ConfigEntryInt max_claims = new ConfigEntryInt("max_claims", new IntBounds(100, 0, Integer.MAX_VALUE))
	{
		public int get()
		{ return currentPlayer.isOP() ? FTBUConfigClaims.max_claims_admin.get() : FTBUConfigClaims.max_claims_player.get(); }
	};
	
	public final ConfigEntryInt max_homes = new ConfigEntryInt("max_homes", new IntBounds(1, 0, Integer.MAX_VALUE))
	{
		public int get()
		{ return currentPlayer.isOP() ? FTBUConfigCmd.maxHomesAdmin.get() : FTBUConfigCmd.maxHomesPlayer.get(); }
	};
}