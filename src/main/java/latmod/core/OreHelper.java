package latmod.core;
import java.util.*;

import latmod.core.base.recipes.StackEntry;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public class OreHelper
{
	public static final class OreStackEntry
	{
		protected final ItemStack itemStack;
		public final Item item;
		public final int damage;
		public final String oreName;
		
		public OreStackEntry(ItemStack is, String o)
		{
			itemStack = is;
			item = is.getItem();
			damage = is.getItemDamage();
			oreName = o;
		}
		
		public boolean equals(Object o)
		{
			ItemStack is = (o == null) ? null : ((o instanceof OreStackEntry) ? ((OreStackEntry)o).itemStack : (ItemStack)o);
			return StackEntry.itemsEquals(itemStack, is);
		}
	}
	
	public static final FastMap<OreStackEntry, FastList<String>> oreNames = new FastMap<OreStackEntry, FastList<String>>();
	
	public static void load()
	{
		int oresLoaded = 0;
		
		oreNames.clear();
		String[] oreNamesS = OreDictionary.getOreNames();
		Arrays.sort(oreNamesS);
		
		for(String ore : oreNamesS)
		{
			ArrayList<ItemStack> al = OreDictionary.getOres(ore);
			
			if(al != null && al.size() > 0) for(ItemStack is : al)
			{
				OreStackEntry se = new OreStackEntry(is, ore);
				FastList<String> al1 = oreNames.get(se);
				if(al1 == null) al1 = new FastList<String>();
				al1.add(ore);
				oreNames.put(se, al1);
				
				oresLoaded++;
			}
		}
		
		System.out.println("[LatCore] Loaded " + oresLoaded + " ores");
	}
	
	public static FastList<String> getOreNames(ItemStack is)
	{ return oreNames.get(is); }
}