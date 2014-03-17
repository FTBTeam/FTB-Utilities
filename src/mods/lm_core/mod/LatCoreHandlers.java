package mods.lm_core.mod;
import mods.lm_core.LCWorldData;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.*;

public class LatCoreHandlers implements IPlayerTracker
{
	public void onPlayerLogin(EntityPlayer ep)
	{
		LCWorldData data = LCWorldData.getData(ep.worldObj);
		boolean b = !data.hasPlayerID(ep);
		int id = data.getPlayerID(ep);
		
		if(b) LC.logger.info("Generated PayerID " + id + " for '" + ep.username + "'");
		else LC.logger.info("Player '" + ep.username + "' logged in with PlayerID " + id);
	}
	
	public void onPlayerLogout(EntityPlayer ep)
	{
	}
	
	public void onPlayerChangedDimension(EntityPlayer ep)
	{
	}
	
	public void onPlayerRespawn(EntityPlayer ep)
	{
	}
}