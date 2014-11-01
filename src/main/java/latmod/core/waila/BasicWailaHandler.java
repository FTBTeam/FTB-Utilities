package latmod.core.waila;
import java.util.*;

import net.minecraft.item.*;
import mcp.mobius.waila.api.*;

public abstract class BasicWailaHandler implements IWailaDataProvider
{
	public boolean registerStack = false;
	public boolean registerHead = false;
	public boolean registerBody = false;
	public boolean registerTail = false;
	
	public BasicWailaHandler() throws Exception
	{
	}
	
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config)
	{
		return null;
	}
	
	public List<String> getWailaHead(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		return l;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		return l;
	}
	
	public List<String> getWailaTail(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		return l;
	}
	
	public String formatDouble(double d)
	{
		String s = "" + d;
		if(s.endsWith(".0"))
			s = s.substring(0, s.length() - 2);
		return s;
	}
}