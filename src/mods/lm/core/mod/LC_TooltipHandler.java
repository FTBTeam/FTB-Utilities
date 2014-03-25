package mods.lm.core.mod;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
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
		if(e.showAdvancedItemTooltips)
		{
			e.toolTip.add("Unlocalized name:");
			e.toolTip.add(e.itemStack.getUnlocalizedName());
		}
	}
}
