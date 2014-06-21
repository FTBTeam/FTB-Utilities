package latmod.core.mod;
import latmod.core.FastList;
import latmod.core.OreHelper;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.item.*;
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
			Item i = e.itemStack.getItem();
			
			String s = Item.itemRegistry.getNameForObject(i);
			
			e.toolTip.add("Unlocalized name:");
			e.toolTip.add(s.startsWith("minecraft:") ? s.substring(10) : s);
			
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
