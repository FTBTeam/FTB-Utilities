package latmod.core;

import java.io.File;

import latmod.core.event.*;
import latmod.core.util.*;

public class LMGamerules
{
	public static final String TAG = "Gamerules";
	private static final FastMap<String, Rule> registredRules = new FastMap<String, Rule>();
	public static final FastMap<String, Rule> rules = new FastMap<String, Rule>();
	
	public static class Rule
	{
		public final String id;
		public String value;
		
		public Rule(String i, String v)
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
		{ return id + "=" + value; }
		
		public String toStringID()
		{ return value + ""; }
	}
	
	public static void load(LoadLMDataEvent e)
	{
		rules.clear();
		
		File f = e.getFile("LMGamerules.txt");
		
		if(f.exists())
		{
			try
			{
				FastList<String> l = LatCore.loadFile(f);
				
				for(int i = 0; i < l.size(); i++)
				{
					String[] s = LatCore.split(l.get(i), " = ");
					
					if(s != null && s.length == 2)
						rules.put(s[0], new Rule(s[0], s[1]));
				}
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
		
		for(int i = 0; i < registredRules.size(); i++)
		{
			String id = registredRules.keys.get(i);
			
			if(!rules.keys.contains(id))
				rules.put(id, registredRules.values.get(i));
		}
		
		if(LatCoreMC.isDevEnv) LatCoreMC.logger.info("LMGamerules: " + rules.values);
	}
	
	public static void save(SaveLMDataEvent e)
	{
		File f = LatCore.newFile(e.getFile("LMGamerules.txt"));
		
		try
		{
			FastList<String> l = new FastList<String>();
			
			for(int i = 0; i < rules.size(); i++)
			{
				Rule r = rules.values.get(i);
				l.add(r.id + " = " + r.value);
			}
			
			LatCore.saveFile(f, l);
		}
		catch(Exception ex)
		{ ex.printStackTrace(); }
	}
	
	public static void register(String id, Object val)
	{ registredRules.put(id, new Rule(id, val + "")); }
	
	public static Rule set(String id, String val)
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
	
	public static Rule get(String id)
	{ return rules.get(id); }
}