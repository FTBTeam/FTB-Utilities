package latmod.ftbu.core.client;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import cpw.mods.fml.relauncher.*;

@SideOnly(Side.CLIENT)
public final class ClientConfig
{
	public final String id;
	public final FastMap<String, Property> map;
	
	public ClientConfig(String s)
	{
		id = s;
		map = new FastMap<String, Property>();
	}
	
	public void add(Property e)
	{ map.put(e.id, e); }
	
	public final static class Registry
	{
		private static File configFile;
		public static final FastMap<String, ClientConfig> map = new FastMap<String, ClientConfig>();
		
		public static void add(ClientConfig c)
		{ map.put(c.id, c); }
		
		public static void init()
		{
			configFile = LatCore.newFile(new File(LatCoreMC.latmodFolder, "client_config.txt"));
		}
		
		public static void load()
		{
			try
			{
				FastList<String> l = LatCore.loadFile(configFile);
				
				for(String s : l) if(!s.isEmpty())
				{
					String[] s1 = s.split("=");
					if(s1.length == 2)
					{
						String[] s2 = s1[0].split(":");
						
						if(s2.length == 2)
						{
							ClientConfig c = map.get(s2[0]);
							
							Property p = c.map.get(s2[1]);
							if(p != null) p.value = Converter.toInt(s1[1], -1);
						}
					}
				}
			}
			catch(Exception e)
			{ e.printStackTrace(); }
			
			save();
		}
		
		public static void save()
		{
			try
			{
				FastList<String> l = new FastList<String>();
				
				for(ClientConfig c : map.values)
					for(Property e : c.map.values)
						l.add(c.id + ":" + e.id + "=" + e.getI());
				
				l.sort(null);
				LatCore.saveFile(configFile, l);
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public final static class Property implements Comparable<Property>
	{
		public final String id;
		public final int def;
		public final String[] values;
		private int value = -1;
		
		public Property(String s, int d, String[] v)
		{ id = s; def = d; values = v; }
		
		public Property(String s, boolean d)
		{ this(s, d ? 1 : 0, new String[] { "False", "True" }); }
		
		public void incValue(boolean b)
		{
			if(b) value = (value + 1) % values.length;
			else
			{
				int i = value - 1;
				if(i >= 0) value = i;
				else value = values.length - 1;
			}
		}
		
		public int getI()
		{ return (value == -1) ? def : value; }
		
		public boolean getB()
		{ return getI() == 1; }
		
		public int compareTo(Property o)
		{ return id.compareTo(o.id); }
		
		public String toString()
		{ return id + ": " + values[getI()]; }
	}
}