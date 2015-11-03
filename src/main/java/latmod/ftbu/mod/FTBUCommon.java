package latmod.ftbu.mod;

import latmod.ftbu.tile.TileLM;
import latmod.ftbu.world.LMWorld;
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