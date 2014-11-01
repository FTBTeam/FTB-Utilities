package latmod.core.tile;

import java.util.List;

import mcp.mobius.waila.api.*;
import net.minecraft.item.ItemStack;

public abstract interface IWailaTile extends ITileInterface
{
	public static interface Stack extends IWailaTile
	{
		public ItemStack getWailaStack(IWailaDataAccessor data, IWailaConfigHandler config);
	}
	
	public static interface Head extends IWailaTile
	{
		public void addWailaHead(IWailaDataAccessor data, IWailaConfigHandler config, List<String> info);
	}
	
	public static interface Body extends IWailaTile
	{
		public void addWailaBody(IWailaDataAccessor data, IWailaConfigHandler config, List<String> info);
	}
	
	public static interface Tail extends IWailaTile
	{
		public void addWailaTail(IWailaDataAccessor data, IWailaConfigHandler config, List<String> info);
	}
}