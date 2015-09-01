package latmod.ftbu.core.api.readme;

import latmod.ftbu.core.util.FastMap;

public class ReadmeCategory
{
	public final String name;
	public final FastMap<String, String> lines;
	
	public ReadmeCategory(String s)
	{
		name = s;
		lines = new FastMap<String, String>();
	}
	
	public boolean equals(Object o)
	{ return o.toString().equals(toString()); }
	
	public String toString()
	{ return name; }
	
	public void add(String text)
	{ lines.put("", text); }
	
	public void add(String id, String text, Object def)
	{ lines.put(id, text + " Default: " + def); }
}