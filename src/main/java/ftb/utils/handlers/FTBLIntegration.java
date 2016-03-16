package ftb.utils.handlers;

import ftb.lib.FTBLib;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.mod.FTBUIntegration;
import ftb.utils.api.guide.ServerGuideFile;
import ftb.utils.badges.ServerBadges;
import ftb.utils.config.FTBUConfigGeneral;
import ftb.utils.ranks.Ranks;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.player.*;

public class FTBLIntegration implements FTBUIntegration // FTBLIntegrationClient
{
	public void onReloaded(ReloadEvent e)
	{
		FTBUConfigGeneral.onReloaded(e.world.side);
		
		if(e.world.side.isServer())
		{
			ServerGuideFile.CachedInfo.reload();
			Ranks.instance().reload();
			ServerBadges.reload();
			
			if(FTBLib.getServerWorld() != null) FTBUChunkEventHandler.instance.markDirty(null);
		}
	}
	
	public void renderWorld(float pt)
	{
	}
	
	public void onTooltip(ItemTooltipEvent e)
	{
	}
	
	public void onRightClick(PlayerInteractEvent e)
	{
		if(e.entityPlayer instanceof EntityPlayerMP)
		{
			if(!FTBUWorldDataMP.canPlayerInteract((EntityPlayerMP) e.entityPlayer, e.pos, e.action == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK))
				e.setCanceled(true);
		}
	}
}