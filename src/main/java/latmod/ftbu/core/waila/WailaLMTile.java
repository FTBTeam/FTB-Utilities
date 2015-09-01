package latmod.ftbu.core.waila;

import java.util.List;

import latmod.ftbu.core.api.RegisterWailaEvent;
import latmod.ftbu.core.tile.IWailaTile;
import net.minecraft.item.ItemStack;

public class WailaLMTile extends BasicWailaHandler
{
	public WailaLMTile(RegisterWailaEvent e, WailaType... t) 
	{ super(e, t); }

	public ItemStack getWailaStack(WailaDataAccessor data)
	{
		if(data.tile != null && !data.tile.isInvalid() && data.tile instanceof IWailaTile.Stack)
			return ((IWailaTile.Stack)data.tile).getWailaStack(data);
		
		return null;
	}
	
	public List<String> getWailaHead(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		if(data.tile != null && !data.tile.isInvalid() && data.tile instanceof IWailaTile.Head)
			((IWailaTile.Head)data.tile).addWailaHead(data, l);
		
		return l;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		if(data.tile != null && !data.tile.isInvalid() && data.tile instanceof IWailaTile.Body)
			((IWailaTile.Body)data.tile).addWailaBody(data, l);
		
		return l;
	}
	
	public List<String> getWailaTail(ItemStack is, List<String> l, WailaDataAccessor data)
	{
		if(data.tile != null && !data.tile.isInvalid() && data.tile instanceof IWailaTile.Tail)
			((IWailaTile.Tail)data.tile).addWailaTail(data, l);
		
		return l;
	}
}