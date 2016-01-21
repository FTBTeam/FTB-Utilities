package ftb.utils.mod.handlers.ftbl;

import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.notification.ClientNotifications;
import ftb.utils.api.EventLMWorldClient;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.api.paint.IPainterItem;
import ftb.utils.badges.ClientBadges;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
import ftb.utils.world.LMWorldClient;
import latmod.lib.ByteIOStream;
import net.minecraft.item.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegration
{
	public void onReloaded(EventFTBReload e)
	{
		super.onReloaded(e);
		
		if(e.world.side.isClient())
		{
			FTBLibClient.clearCachedData();
			ClientBadges.clear();
			ClientGuideFile.instance.reload(e);
		}
	}
	
	public void onFTBWorldClient(EventFTBWorldClient e)
	{
		ClientNotifications.init();
		ClaimedAreasClient.clear();
		
		if(e.world == null)
		{
			if(LMWorldClient.inst != null) new EventLMWorldClient(LMWorldClient.inst, true).post();
			LMWorldClient.inst = null;
		}
	}
	
	public void readWorldData(ByteIOStream io)
	{
		LMWorldClient.inst = new LMWorldClient(io.readInt());
		LMWorldClient.inst.readDataFromNet(io, true);
		FTBLib.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayerID);
		new EventLMWorldClient(LMWorldClient.inst, false).post();
	}
	
	public boolean hasClientWorld()
	{ return LMWorldClient.inst != null && LMWorldClient.inst.clientPlayerID > 0 && LMWorldClient.inst.clientPlayer != null; }
	
	public void renderWorld(float pt)
	{
	}
	
	public void onTooltip(ItemTooltipEvent e)
	{
		Item item = e.itemStack.getItem();
		
		if(item instanceof IPainterItem)
		{
			ItemStack paint = ((IPainterItem) item).getPaintItem(e.itemStack);
			if(paint != null)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(EnumChatFormatting.WHITE);
				sb.append(EnumChatFormatting.BOLD);
				sb.append(paint.getDisplayName());
				sb.append(EnumChatFormatting.RESET);
				e.toolTip.add(sb.toString());
			}
		}
		
		//if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
		//	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
	}
}