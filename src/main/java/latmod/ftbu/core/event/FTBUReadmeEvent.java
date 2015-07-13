package latmod.ftbu.core.event;

import java.io.File;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.*;

public class FTBUReadmeEvent extends EventLM
{
	public final ReadmeFile file;
	
	public FTBUReadmeEvent()
	{ file = new ReadmeFile(); }
	
	public static class ReadmeFile
	{
		public final FastMap<String, Category> map = new FastMap<String, Category>();
		
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
			
			private Category(String s)
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
	}
	
	public static void saveReadme() throws Exception
	{
		FTBUReadmeEvent e = new FTBUReadmeEvent();
		
		ConfigGeneral.saveReadme(e);
		ConfigLogin.saveReadme(e);
		ConfigWorldBorder.saveReadme(e);
		ConfigBackups.saveReadme(e);
		
		FTBU.proxy.addInfo(e);
		
		e.post();
		
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < e.file.map.size(); j++)
		{
			FTBUReadmeEvent.ReadmeFile.Category c = e.file.map.values.get(j);
			
			sb.append('[');
			sb.append(c.name);
			sb.append(']');
			sb.append('\n');
			
			for(int i = 0; i < c.lines.size(); i++)
			{
				String k = c.lines.keys.get(i); 
				
				if(!k.isEmpty())
				{
					sb.append(k);
					sb.append(" - ");
				}
				
				sb.append(c.lines.values.get(i));
				sb.append('\n');
			}
			
			sb.append('\n');
		}
		
		LMFileUtils.save(new File(LatCoreMC.latmodFolder, "readme.txt"), sb.toString().trim());
	}
}