package latmod.core.mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
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
		if(e.showAdvancedItemTooltips)
		{
			Item i = e.itemStack.getItem();
			
			String s = Item.itemRegistry.getNameForObject(i);
			
			e.toolTip.add("Unlocalized name:");
			e.toolTip.add(s.startsWith("minecraft:") ? s.substring(10) : s);
		}
	}
}
