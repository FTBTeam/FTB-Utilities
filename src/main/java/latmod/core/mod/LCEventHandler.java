package latmod.core.mod;
import latmod.core.*;
import latmod.core.util.*;
import net.minecraft.item.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.oredict.*;

public class LCEventHandler
{
	private static final FastMap<OreStackEntry, FastList<String>> oreNames = new FastMap<OreStackEntry, FastList<String>>();
	
	public static final class OreStackEntry
	{
		public final ItemStack itemStack;
		public final String oreName;
		
		public OreStackEntry(ItemStack is, String s)
		{
			itemStack = is;
			oreName = s;
		}
		
		public boolean equals(Object o)
		{
			ItemStack is = (o == null) ? null : ((o instanceof OreStackEntry) ? ((OreStackEntry)o).itemStack : (ItemStack)o);
			return is.getItem() == itemStack.getItem() && (is.getItemDamage() == itemStack.getItemDamage() || itemStack.getItemDamage() == LatCore.ANY);
		}
	}
	
	public LCEventHandler()
	{
		String[] allOres = OreDictionary.getOreNames();
		
		for(String s : allOres)
		{
			for(ItemStack is : OreDictionary.getOres(s))
				addOreName(is, s);
		}
	}
	
	@ForgeSubscribe
	public void onTooltip(ItemTooltipEvent e)
	{
		if(e.showAdvancedItemTooltips && e.itemStack != null)
		{
			FastList<String> ores = getOreNames(e.itemStack);
			
			if(ores != null && !ores.isEmpty())
			{
				e.toolTip.add("Ore Dictionary names:");
				for(String or : ores)
				e.toolTip.add("> " + or);
			}
			
			if(LC.proxy.isShiftDown())
			{
				e.toolTip.add("Unlocalized name:");
				e.toolTip.add("> " + e.itemStack.getUnlocalizedName());
			}
		}
	}
	
	@ForgeSubscribe
	public void oreAdded(OreDictionary.OreRegisterEvent e)
	{
		addOreName(e.Ore, e.Name);
	}
	
	private static void addOreName(ItemStack is, String s)
	{
		FastList<String> al = getOreNames(is);
		
		if(al == null)
		{
			al = new FastList<String>();
			oreNames.put(new OreStackEntry(is, s), al);
		}
		
		if(!al.contains(s)) al.add(s);
	}
	
	public static FastList<String> getOreNames(ItemStack is)
	{ return oreNames.get(is); }
}
