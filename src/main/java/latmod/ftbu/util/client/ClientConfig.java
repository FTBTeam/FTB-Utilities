package latmod.ftbu.util.client;

import java.io.File;

import cpw.mods.fml.relauncher.*;
import latmod.core.util.*;
import latmod.ftbu.util.LatCoreMC;
import net.minecraft.client.resources.I18n;

@SideOnly(Side.CLIENT)
public final class ClientConfig
{
	public final String id;
	public final FastMap<String, Property> map;
	public boolean isHidden = false;
	
	public ClientConfig(String s)
	{
		id = s;
		map = new FastMap<String, Property>();
	}
	
	public void add(Property p)
	{ map.put(p.id, p); }
	
	public String getIDS()
	{ return I18n.format("config.group." + id); }
	
	public String toString()
	{ return getIDS() + ": " + map; }
	
	public ClientConfig setHidden()
	{ isHidden = true; return this; }
	
	public final static class Registry
	{
		private static File configFile;
		public static final FastMap<String, ClientConfig> map = new FastMap<String, ClientConfig>();
		
		public static void add(ClientConfig c)
		{ map.put(c.id, c); }
		
		public static void init()
		{
			configFile = LMFileUtils.newFile(new File(LatCoreMC.latmodFolder, "client/config.txt"));
		}
		
		public static void load()
		{
			try
			{
				FastList<String> l = LMFileUtils.load(configFile);
				
				for(String s : l) if(!s.isEmpty())
				{
					String[] s1 = s.split("=");
					if(s1.length == 2)
					{
						String[] s2 = s1[0].split(":");
						
						if(s2.length == 2)
						{
							ClientConfig c = map.get(s2[0]);
							
							if(c != null)
							{
								Property p = c.map.get(s2[1]);
								if(p != null) p.value = Converter.toInt(s1[1], -1);
							}
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
				LMFileUtils.save(configFile, l);
			}
			catch(Exception e)
			{ e.printStackTrace(); }
		}
	}
	
	public static class Property implements Comparable<Property>
	{
		public final String id;
		public final int def;
		public final String[] values;
		public final int[] texCol;
		public final int[] texColMO;
		
		private int value = -1;
		private boolean translateValues = true;
		
		public Property(String s, int d, String... v)
		{
			id = s;
			def = d;
			values = v;
			
			texCol = new int[values.length];
			texColMO = new int[values.length];
			
			for(int i = 0; i < values.length; i++)
			{
				texCol[i] = 0xFF999999;
				texColMO[i] = 0xFFFFFFFF;
				
				if(values[i].equals("edit"))
				{
					texCol[i] = 0xFFAA00;
					texColMO[i] = 0xFFFF00;
				}
				else if(values[i].equals("true") || values[i].equals("enabled"))
				{
					texCol[i] = 0xFF339933;
					texColMO[i] = 0xFF33D333;
				}
				else if(values[i].equals("false") || values[i].equals("disabled"))
				{
					texCol[i] = 0xFF993333;
					texColMO[i] = 0xFFD33333;
				}
			}
		}
		
		public Property(String s, boolean d)
		{ this(s, d ? 1 : 0, "disabled", "enabled"); }
		
		public void onClicked()
		{ setValue(value + 1); }
		
		public void setValue(int i)
		{
			value = i % values.length;
			if(value < 0) value = values.length + value;
		}
		
		public int getI()
		{ return (value == -1) ? def : value; }
		
		public boolean getB()
		{ return getI() == 1; }
		
		public int compareTo(Property o)
		{ return getIDS().compareTo(o.getIDS()); }
		
		public String toString()
		{ return getIDS() + ": " + getValueS(getI()); }
		
		public String getIDS()
		{ return I18n.format("config.property." + id); }
		
		public String getValueS(int i)
		{ return translateValues ? I18n.format("config.value." + values[i]) : values[i]; }
		
		public Property setRawValues()
		{ translateValues = false; return this; }
		
		public void initGui() { }
	}
}