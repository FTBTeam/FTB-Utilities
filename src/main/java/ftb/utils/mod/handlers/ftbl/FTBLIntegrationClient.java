package ftb.utils.mod.handlers.ftbl;

import ftb.lib.api.EventFTBReload;
import ftb.lib.api.client.FTBLibClient;
import ftb.lib.notification.ClientNotifications;
import ftb.utils.api.guide.ClientGuideFile;
import ftb.utils.api.paint.IPainterItem;
import ftb.utils.badges.ClientBadges;
import ftb.utils.mod.client.gui.claims.ClaimedAreasClient;
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
	
	public void onFTBWorldClient()
	{
		ClientNotifications.init();
		ClaimedAreasClient.clear();
	}
	
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