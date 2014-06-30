package latmod.core.mod;
import latmod.core.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.*;

public class LC_TooltipHandler
{
	public LC_TooltipHandler()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@ForgeSubscribe
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null)
		{
			if(LC.proxy.isShiftDown())
			{
				e.toolTip.add("Unlocalized name:");
				e.toolTip.add("> " + e.itemStack.getUnlocalizedName());
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
