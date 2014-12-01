package latmod.core;

import latmod.core.util.FastMap;
import net.minecraft.nbt.*;

public class LMGamerules
{
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
		{ return value + ""; }
		
		public String toStringID()
		{ return id.toString() + ":" + value; }
	}
	
	public static void readFromNBT(NBTTagCompound tag, String s)
	{
		rules.clear();
		
		NBTTagList list = (NBTTagList)tag.getTag(s);
		
		if(list != null) for(int i = 0; i < list.tagCount(); i++)
		{
			NBTTagCompound tag1 = list.getCompoundTagAt(i);
			String g = tag1.getString("G");
			String k = tag1.getString("K");
			String v = tag1.getString("V");
			
			RuleID id = new RuleID(g, k);
			rules.put(id, new Rule(id, v));
		}
		
		for(int i = 0; i < registredRules.size(); i++)
		{
			RuleID id = registredRules.keys.get(i);
			
			if(!rules.keys.contains(id))
				rules.put(id, registredRules.values.get(i));
		}
		
		LatCoreMC.logger.info("Loaded LMGamerules: " + rules.values);
	}
	
	public static void writeToNBT(NBTTagCompound tag, String s)
	{
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < rules.size(); i++)
		{
			Rule r = rules.values.get(i);
			
			NBTTagCompound tag1 = new NBTTagCompound();
			tag1.setString("G", r.id.group);
			tag1.setString("K", r.id.key);
			tag1.setString("V", r.value);
			list.appendTag(tag1);
		}
		
		tag.setTag(s, list);
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