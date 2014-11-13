package latmod.core.waila;

import java.util.List;

import latmod.latcore.LCEventHandler;
import mcp.mobius.waila.api.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

public class WailaInvHandler extends BasicWailaHandler
{
	public WailaInvHandler(WailaHelper.RegisterHandlersEvent e)
	{
		super(e);
		registerBody = true;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		IInventory inv = (IInventory)data.getTileEntity();
		
		if(!(inv.getClass().equals(TileEntityChest.class)) && !(inv instanceof ISidedInventory) && config.getConfig(LCEventHandler.WAILA_INV))
		{
			int size = inv.getSizeInventory();
			
			if(size > 0)
			{
				int items = 0;
				int slots = 0;
				
				for(int i = 0; i < size; i++)
				{
					ItemStack item = inv.getStackInSlot(i);
					
					if(item != null && item.stackSize > 0)
					{
						slots++;
						items += item.stackSize;
					}
				}
				
				l.add("Items Stored: " + items + " / " + (size * inv.getInventoryStackLimit()));
				l.add("Slots Used: " + slots + " / " + size);
			}
		}
		
		return l;
	}
}