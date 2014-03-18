package mods.lm_core.mod;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.*;

public class LCHandlers implements IPlayerTracker
{
	public void onPlayerLogin(EntityPlayer ep)
	{
		boolean b = PlayerID.inst.hasID(ep.username);
		int id = PlayerID.inst.get(ep);
		
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