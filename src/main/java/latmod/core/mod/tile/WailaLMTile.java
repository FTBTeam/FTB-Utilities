package latmod.core.mod.tile;

import java.util.List;

import latmod.core.apis.BasicWailaHandler;
import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class WailaLMTile extends BasicWailaHandler
{
	public WailaLMTile() throws Exception
	{
		super();
		registerStack = true;
		registerHead = true;
		registerBody = true;
		registerTail = true;
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