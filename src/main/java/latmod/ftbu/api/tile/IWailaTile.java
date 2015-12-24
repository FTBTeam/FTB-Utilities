package latmod.ftbu.api.tile;

import latmod.ftbu.waila.WailaDataAccessor;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract interface IWailaTile
{
	public static interface Stack extends IWailaTile
	{
		public ItemStack getWailaStack(WailaDataAccessor data);
	}
	
	public static interface Head extends IWailaTile
	{
		public void addWailaHead(WailaDataAccessor data, List<String> info);
	}
	
	public static interface Body extends IWailaTile
	{
		public void addWailaBody(WailaDataAccessor data, List<String> info);
	}
	
	public static interface Tail extends IWailaTile
	{
		public void addWailaTail(WailaDataAccessor data, List<String> info);
	}
}