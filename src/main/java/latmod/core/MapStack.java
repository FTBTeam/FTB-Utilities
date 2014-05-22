package latmod.core;
import net.minecraft.item.*;

public class MapStack
{
	public final ItemStack stack;
	public final Item item;
	public final int damage;
	
	public MapStack(ItemStack is)
	{
		stack = is;
		item = stack.getItem();
		damage = stack.getItemDamage();
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof ItemStack)
		return item == ((ItemStack)o).getItem() && damage == ((ItemStack)o).getItemDamage();
		return equals(((MapStack)o).stack);
	}
	
	public int hashCode()
	{ return item.hashCode() ^ (damage * 32); }
}
