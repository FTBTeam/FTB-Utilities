package latmod.ftbu.core.event;

import latmod.ftbu.core.util.FastMap;

public class FTBUReadmeEvent extends EventLM
{
	public final ReadmeFile file;
	
	public FTBUReadmeEvent()
	{ file = new ReadmeFile(); }
	
	public void add(String cat, String id, String text)
	{ file.get(cat).add(id, text); }
	
	public static class ReadmeFile
	{
		private final FastMap<String, Category> map = new FastMap<String, Category>();
		
		public Category get(String s)
		{
			Category c = map.get(s);
			if(c == null) add(c = new Category(s));
			return c;
		}
		
		public void add(Category c)
		{ map.put(c.name, c); }
		
		public static class Category
		{
			public final String name;
			public final FastMap<String, String> lines;
			
			public Category(String s)
			{
				name = s;
				lines = new FastMap<String, String>();
			}
			
			public boolean equals(Object o)
			{ return o.toString().equals(toString()); }
			
			public String toString()
			{ return name; }
			
			public void add(String id, String text)
			{ lines.put(id, text); }
		}
	}
}