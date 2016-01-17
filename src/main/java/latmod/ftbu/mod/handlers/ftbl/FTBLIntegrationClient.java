package latmod.ftbu.mod.handlers.ftbl;

import ftb.lib.*;
import ftb.lib.api.*;
import ftb.lib.notification.ClientNotifications;
import latmod.ftbu.api.EventLMWorldClient;
import latmod.ftbu.api.guide.ClientGuideFile;
import latmod.ftbu.api.paint.IPainterItem;
import latmod.ftbu.badges.ClientBadges;
import latmod.ftbu.mod.client.gui.claims.ClaimedAreasClient;
import latmod.ftbu.world.*;
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
		
		if(e.world == null)
		{
			ClaimedAreasClient.clear();
			new EventLMWorldClient(LMWorldClient.inst, true).post();
			LMWorldClient.inst = null;
		}
		else if(e.isFake)
		{
			LMWorldClient.inst = new LMWorldClient(0);
		}
	}
	
	public void readWorldData(ByteIOStream io)
	{
		LMWorldClient.inst = new LMWorldClient(io.readInt());
		LMWorldClient.inst.readDataFromNet(io, true);
		FTBLib.logger.info("Joined the server with PlayerID " + LMWorldClient.inst.clientPlayerID + " on world " + FTBWorld.client.getWorldIDS());
		new EventLMWorldClient(LMWorldClient.inst, false).post();
	}
	
	public boolean hasClientWorld()
	{ return LMWorldServer.inst != null && LMWorldClient.inst.clientPlayerID > 0 && LMWorldClient.inst.clientPlayer != null; }
	
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