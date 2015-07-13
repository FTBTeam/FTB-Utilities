package latmod.ftbu.core.waila;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.*;

public class WailaDataProvider implements IWailaDataProvider
{
	public final BasicWailaHandler handler;
	private static WailaDataAccessor dataAccessor = null;
	
	public WailaDataProvider(BasicWailaHandler h)
	{ handler = h; }
	
	private static WailaDataAccessor getData(IWailaDataAccessor i)
	{
		if(dataAccessor == null) dataAccessor = new WailaDataAccessor();
		dataAccessor.player = i.getPlayer();
		dataAccessor.world = i.getWorld();
		dataAccessor.position = i.getPosition();
		dataAccessor.tile = i.getTileEntity();
		dataAccessor.block = i.getBlock();
		dataAccessor.meta = i.getMetadata();
		dataAccessor.side = dataAccessor.position.sideHit;
		return dataAccessor;
	}
	
	public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config)
	{ return handler.getWailaStack(getData(data)); }
	
	public List<String> getWailaHead(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{ return handler.getWailaHead(is, l, getData(data)); }
	
	public List<String> getWailaBody(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{ return handler.getWailaBody(is, l, getData(data)); }
	
	public List<String> getWailaTail(ItemStack is, List<String> l, IWailaDataAccessor data, IWailaConfigHandler config)
	{ return handler.getWailaTail(is, l, getData(data)); }
}