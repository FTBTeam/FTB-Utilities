package latmod.core.mod;
import java.util.*;
import latmod.core.*;
import net.minecraft.item.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.oredict.*;

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
			Item i = e.itemStack.getItem();
			
			FastList<String> ores = OreHelper.getOreNames(e.itemStack);
			
			if(ores != null)
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
		}
	}
}
