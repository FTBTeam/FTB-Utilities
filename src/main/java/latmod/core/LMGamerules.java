package latmod.core;

import net.minecraft.nbt.*;

public class LMGamerules
{
	public static final String TAG = "Gamerules";
	private static final FastMap<RuleID, Rule> registredRules = new FastMap<RuleID, Rule>();
	public static final FastMap<RuleID, Rule> rules = new FastMap<RuleID, Rule>();
	
	public static class RuleID
	{
		public final String group;
		public final String key;
		
		public RuleID(String g, String k)
		{
			group = g;
			key = k;
		}
		
		public String toString()
		{ return group + ":" + key; }
		
		public int hashCode()
		{ return key.hashCode(); }
		
		public boolean equals(Object o)
		{
			if(o == null) return false;
			else if(o == this) return true;
			else return o.toString().equals(toString());
		}
	}
	
	public static class Rule
	{
		public final RuleID id;
		public String value;
		
		public Rule(RuleID i, String v)
		{
			id = i;
			value = v;
		}
		
		public boolean getBool()
		{ return Boolean.parseBoolean(value); }
		
		public Number getNum()
		{ return new Double(Double.parseDouble(value)); }
		
		public Number getNum(double min, double max)
		{
			Number n = getNum();
			if(n.doubleValue() < min) n = min;
			if(n.doubleValue() > max) n = max;
			return n;
		}
		
		public String toString()
		{ return id.toString() + ":" + value; }
		
		public String toStringID()
		{ return value + ""; }
	}
	
	public static void readFromNBT(NBTTagCompound tag)
	{
		rules.clear();
		
		if(tag.func_150299_b(TAG) == NBTHelper.LIST)
		{
			NBTTagList list = tag.getTagList(TAG, NBTHelper.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				String g = tag1.getString("G");
				String k = tag1.getString("K");
				String v = tag1.getString("V");
				
				RuleID id = new RuleID(g, k);
				rules.put(id, new Rule(id, v));
			}
			
			LatCoreMC.logger.info("Found old LMGamerules");
		}
		else
		{
			FastMap<String, NBTTagCompound> map = NBTHelper.toFastMapWithType(tag.getCompoundTag(TAG));
			
			for(int i = 0; i < map.size(); i++)
			{
				String g = map.keys.get(i);
				FastMap<String, NBTTagString> map1 = NBTHelper.toFastMapWithType(map.values.get(i));
				
				for(int j = 0; j < map1.size(); j++)
				{
					String k = map1.keys.get(j);
					String v = map1.values.get(j).func_150285_a_();
					
					RuleID id = new RuleID(g, k);
					rules.put(id, new Rule(id, v));
				}
			}
		}
	}
	
	public static void postInit()
	{
		for(int i = 0; i < registredRules.size(); i++)
		{
			RuleID id = registredRules.keys.get(i);
			
			if(!rules.keys.contains(id))
				rules.put(id, registredRules.values.get(i));
		}
		
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("LMGamerules: " + rules.values);
	}
	
	public static void writeToNBT(NBTTagCompound tag)
	{
		FastMap<String, FastMap<String, String>> map = new FastMap<String, FastMap<String, String>>();
		
		for(int i = 0; i < rules.size(); i++)
		{
			Rule r = rules.values.get(i);
			
			FastMap<String, String> m = map.get(r.id.group);
			
			if(m == null)
			{
				m = new FastMap<String, String>();
				map.put(r.id.group, m);
			}
			
			m.put(r.id.key, r.value);
		}
		
		NBTTagCompound groups = new NBTTagCompound();
		
		for(int i = 0; i < map.size(); i++)
		{
			NBTTagCompound tag1 = new NBTTagCompound();
			FastMap<String, String> map1 = map.values.get(i);
			for(int j = 0; j < map1.size(); j++)
				tag1.setString(map1.keys.get(j), map1.values.get(j));
			groups.setTag(map.keys.get(i), tag1);
		}
		
		tag.setTag(TAG, groups);
	}
	
	public static void register(RuleID id, Object val)
	{ registredRules.put(id, new Rule(id, val + "")); }
	
	public static Rule set(RuleID id, String val)
	{
		Rule r = rules.get(id);
		
		if(r == null)
		{
			r = new Rule(id, val);
			rules.put(id, r);
		}
		
		r.value = val;
		return r;
	}
	
	public static Rule get(RuleID id)
	{ return rules.get(id); }
}