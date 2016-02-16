package ftb.utils.mod.handlers.ftbl;

import ftb.lib.FTBLib;
import ftb.lib.api.EventFTBReload;
import ftb.lib.mod.FTBUIntegration;
import ftb.utils.api.guide.ServerGuideFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.mod.handlers.FTBUChunkEventHandler;
import ftb.utils.world.ranks.Ranks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.*;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	public void onReloaded(EventFTBReload e)
	{
		FTBUConfigGeneral.onReloaded(e.world.side);
		
		if(e.world.side.isServer())
		{
			ServerGuideFile.CachedInfo.reload();
			Ranks.reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null) FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public final void onFTBWorldServer()
	{
	}
	
	public void onFTBWorldClient()
	{
	}
	
	public final void onServerTick(World w)
	{
		
	}
	
	public void renderWorld(float pt)
	{
	}
	
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	public void onRightClick(PlayerInteractEvent e)
	{
		if(!FTBUWorldData.canPlayerInteract((EntityPlayerMP) e.entityPlayer, e.pos, e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
			e.setCanceled(true);
	}
}