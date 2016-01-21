package ftb.utils.mod;

import ftb.lib.api.tile.TileLM;
import ftb.utils.world.LMWorld;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class FTBUCommon // FTBUClient
{
	public void preInit()
	{
	}
	
	public void postInit()
	{
	}
	
	public LMWorld getClientWorldLM()
	{ return null; }
	
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p) { }
}