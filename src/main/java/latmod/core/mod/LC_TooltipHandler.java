package latmod.core.mod;
import latmod.core.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.player.*;

public class LC_TooltipHandler
{
	public LC_TooltipHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null)
		{
			if(LC.proxy.isShiftDown())
			{
				e.toolTip.add("Registry name:");
				e.toolTip.add("> " + LMUtils.getRegistryName(e.itemStack.getItem(), true));
			}
			
			FastList<String> ores = OreHelper.getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
		}
	}
}
