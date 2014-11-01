package latmod.core.waila;

import java.util.List;

import latmod.core.tile.IWailaTile;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class WailaLMTile extends BasicWailaHandler
{
	public WailaLMTile(WailaHelper.RegisterHandlersEvent e, boolean stack, boolean head, boolean body, boolean tail)
	{
		super(e);
		registerStack = stack;
		registerHead = head;
		registerBody = body;
		registerTail = tail;
	}
	
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Stack)
			return ((IWailaTile.Stack)te).getWailaStack(data, config);
		
		return null;
	}
	
	public List<String> getWailaHead(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Head)
			((IWailaTile.Head)te).addWailaHead(data, config, l);
		
		return l;
	}
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Body)
			((IWailaTile.Body)te).addWailaBody(data, config, l);
		
		return l;
	}
	
	public List<String> getWailaTail(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{
		TileEntity te = data.getTileEntity();
		
		if(te != null && !te.isInvalid() && te instanceof IWailaTile.Tail)
			((IWailaTile.Tail)te).addWailaTail(data, config, l);
		
		return l;
	}
}