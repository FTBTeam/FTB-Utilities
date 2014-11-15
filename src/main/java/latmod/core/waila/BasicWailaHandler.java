package latmod.core.waila;
import java.util.List;

import latmod.core.util.FastList;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;

public abstract class BasicWailaHandler implements IWailaDataProvider
{
	public final FastList<WailaType> types;
	
	public BasicWailaHandler(RegisterWailaEvent e, WailaType... t)
	{ types = FastList.asList(t); }
	
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
}