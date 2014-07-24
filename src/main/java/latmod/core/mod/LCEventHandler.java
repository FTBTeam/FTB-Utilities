package latmod.core.mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import latmod.core.*;
import latmod.core.util.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.oredict.*;

public class LCEventHandler
{
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null)
		{
			FastList<String> ores = ODItems.getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
			
			if(LC.proxy.isShiftDown())
			{
				e.toolTip.add("Registry name:");
				e.toolTip.add("> " + LMUtils.getRegName(e.itemStack.getItem(), true));
			}
		}
	}
	
	@SubscribeEvent
	public void oreAdded(OreDictionary.OreRegisterEvent e)
	{ ODItems.addOreName(e.Ore, e.Name); }
}