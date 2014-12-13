package latmod.core.waila;

import java.util.List;

import latmod.latcore.LC;
import mcp.mobius.waila.api.*;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;

public class WailaInvHandler extends BasicWailaHandler
{
	public static final String CONFIG_INV = "latcoremc.inv";
	
	public WailaInvHandler(RegisterWailaEvent e)
	{
		super(e, WailaType.BODY);
		e.registry.addConfig(LC.mod.modID, CONFIG_INV);
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		if(config.getConfig(CONFIG_INV))
		{
			IInventory inv = (IInventory)data.getTileEntity();
			
			if(!(inv.getClass().equals(TileEntityChest.class)) && !(inv instanceof ISidedInventory))
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
		}
		
		return l;
	}
}