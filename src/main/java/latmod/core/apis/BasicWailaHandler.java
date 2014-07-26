package latmod.core.apis;
import java.util.*;

import net.minecraft.item.*;
import mcp.mobius.waila.api.*;

public abstract class BasicWailaHandler implements IWailaDataProvider
{
	public final boolean registerHead;
	public final boolean registerBody;
	public final boolean registerTail;
	
	public BasicWailaHandler(boolean head, boolean body, boolean tail) throws Exception
	{
		registerHead = head;
		registerBody = body;
		registerTail = tail;
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