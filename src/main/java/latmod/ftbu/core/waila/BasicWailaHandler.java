package latmod.ftbu.core.waila;
import java.util.List;

import latmod.ftbu.core.event.RegisterWailaEvent;
import net.minecraft.item.ItemStack;

public abstract class BasicWailaHandler
{
	public final WailaType[] types;
	
	public BasicWailaHandler(RegisterWailaEvent e, WailaType... t)
	{ types = t; }
	
	public ItemStack getWailaStack(WailaDataAccessor data)
	{
		return null;
	}
	
	public List<String> getWailaHead(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		return l;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		return l;
	}
	
	public List<String> getWailaTail(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		return l;
	}
}