package ftb.utils.handlers;

import ftb.lib.api.client.FTBLibClient;
import ftb.lib.api.events.ReloadEvent;
import ftb.lib.api.paint.IPainterItem;
import ftb.utils.badges.ClientBadges;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FTBLIntegrationClient extends FTBLIntegration
{
	@Override
	public void onReloaded(ReloadEvent e)
	{
		super.onReloaded(e);
		
		if(e.world.side.isClient())
		{
			FTBLibClient.clearCachedData();
			ClientBadges.clear();
			
			//if(e.modeChanged)
			{
				//FIXME: GuideRepoList.reloadFromFolder(e.world.getMode());
			}
		}
	}
	
	@Override
	public void renderWorld(float pt)
	{
	}
	
	@Override
	public void onTooltip(ItemTooltipEvent e)
	{
		Item item = e.getItemStack().getItem();
		
		if(item instanceof IPainterItem)
		{
			ItemStack paint = ((IPainterItem) item).getPaintItem(e.getItemStack());
			if(paint != null)
			{
				e.getToolTip().add(String.valueOf(TextFormatting.WHITE) + TextFormatting.BOLD + paint.getDisplayName() + TextFormatting.RESET);
			}
		}
		
		//if(FTBUConfigGeneral.isItemBanned(item, e.itemStack.getItemDamage()))
		//	e.toolTip.add(EnumChatFormatting.RED + "Banned item");
	}
}