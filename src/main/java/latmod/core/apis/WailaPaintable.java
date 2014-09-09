package latmod.core.apis;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;

public class WailaPaintable extends BasicWailaHandler
{
	public WailaPaintable() throws Exception
	{
		super(false, true, false);
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		return l;
	}
}