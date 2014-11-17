package latmod.core;

import latmod.core.util.FastMap;
import net.minecraft.nbt.*;

public class LMGamerules
{
	public static final FastMap<String, FastMap<String, String>> rules = new FastMap<String, FastMap<String, String>>();
	
	public static void readFromNBT(NBTTagCompound tag, String s)
	{
		rules.clear();
		
		FastMap<String, NBTBase> tags = LatCoreMC.toFastMap(tag.getCompoundTag(s));
		
		for(int i = 0; i < tags.size(); i++)
		{
			FastMap<String, String> map1 = new FastMap<String, String>();
			
			NBTTagCompound tag1 = (NBTTagCompound)tags.values.get(i);
			
			FastMap<String, NBTBase> tags1 = LatCoreMC.toFastMap(tag1);
			
			for(int j = 0; j < tags1.size(); j++)
			{
				String key1 = tags1.getKey(j);
				map1.put(key1, tag1.getString(key1));
			}
			
			rules.put(tags.keys.get(i), map1);
		}
		
		LatCoreMC.logger.info("Loaded LMGamerules: " + rules);
	}
	
	public static void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagCompound tag1 = new NBTTagCompound();
		
		for(int i = 0; i < rules.size(); i++)
		{
			NBTTagCompound tag2 = new NBTTagCompound();
			
			FastMap<String, String> map1 = rules.values.get(i);
			
			for(int j = 0; j < map1.size(); j++)
				tag2.setString(map1.keys.get(j), map1.values.get(j));
			
			tag1.setTag(rules.keys.get(i), tag2);
		}
		
		tag.setTag(s, tag1);
	}
	
	public static void add(String group, String key, String val)
	{ if(get(group, key) == null) set(group, key, val); }
	
	public static void set(String group, String key, String val)
	{
		FastMap<String, String> map = rules.get(group);
		
		if(map == null)
		{
			map = new FastMap<String, String>();
			rules.put(group, map);
		}
		
		map.put(key, val);
	}
	
	public static String get(String group, String key)
	{
		FastMap<String, String> map = rules.get(group);
		return (map == null) ? null : map.get(key);
	}
	
	public static String get(String group, String key, String def)
	{
		String s = get(group, key);
		return (s == null) ? def : s;
	}
	
	public static Boolean get(String group, String key, Boolean def)
	{
		String s = get(group, key);
		return (s == null) ? def : (key.equals("true"));
	}
	
	public static Number get(String group, String key, Number def)
	{
		String s = get(group, key);
		return (s == null) ? def : (Double.parseDouble(s));
	}
}