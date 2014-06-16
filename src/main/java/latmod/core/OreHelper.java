package latmod.core;
import java.util.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.OreDictionary;

public class OreHelper
{
	public static final class StackEntry implements Comparable<StackEntry>
	{
		public final Item item;
		public final int damage;
		public final String oreName;
		
		public StackEntry(ItemStack is, String o)
		{
			item = is.getItem();
			damage = is.getItemDamage();
			oreName = o;
		}
		
		public boolean equals(Object o)
		{
			StackEntry e = (StackEntry)o;
			return item == e.item && ((e.damage == LatCore.ANY || damage == LatCore.ANY) ? true : damage == e.damage);
		}

		public int compareTo(StackEntry se)
		{ return Item.itemRegistry.getNameForObject(item).compareTo(Item.itemRegistry.getNameForObject(item)); }
	}
	
	public static final FastMap<StackEntry, ArrayList<String>> oreNames = new FastMap<StackEntry, ArrayList<String>>();
	
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
				StackEntry se = new StackEntry(is, ore);
				ArrayList<String> al1 = oreNames.get(se);
				if(al1 == null) al1 = new ArrayList<String>();
				al1.add(ore);
				oreNames.put(se, al1);
				
				oresLoaded++;
			}
		}
		
		System.out.println("[LatCore] Loaded " + oresLoaded + " ores");
	}
	
	public static ArrayList<String> getOreNames(ItemStack is)
	{ return oreNames.get(new StackEntry(is, null)); }
}