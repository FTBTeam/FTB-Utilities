package latmod.ftbu.core.api.readme;

import java.lang.reflect.*;

import latmod.ftbu.core.util.FastList;

public class ReadmeCategory
{
	private static final String SEP_ID = " - ";
	private static final String SEP_DEF = " Default: ";
	
	public final String name;
	public final FastList<String> lines;
	
	public ReadmeCategory(String s)
	{
		name = s;
		lines = new FastList<String>();
	}
	
	public boolean equals(Object o)
	{ return o.toString().equals(toString()); }
	
	public String toString()
	{ return name; }
	
	public void add(String text)
	{ lines.add(text); }
	
	public void add(String id, String text, Object def)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append(SEP_ID);
		sb.append(text);
		sb.append(SEP_DEF);
		sb.append(def);
		add(sb.toString());
	}
	
	public ReadmeCategory addFromClass(Class<?> c)
	{
		try
		{
			Field[] fields = c.getDeclaredFields();
			
			if(fields != null && fields.length > 0) for(Field f : fields)
			{
				f.setAccessible(true);
				
				if(f.isAnnotationPresent(ReadmeInfo.class))
				{
					ReadmeInfo i = f.getAnnotation(ReadmeInfo.class);
					
					String key = i.key();
					String info = i.info();
					String def = i.def();
					
					if(key.isEmpty()) key = f.getName();
					add(key, info, def);
				}
			}
			
			Method[] methods = c.getDeclaredMethods();
			
			if(methods != null && methods.length > 0) for(Method m : methods)
			{
				m.setAccessible(true);
				
				if(m.isAnnotationPresent(ReadmeInfo.class))
				{
					ReadmeInfo i = m.getAnnotation(ReadmeInfo.class);
					
					String key = i.key();
					String info = i.info();
					String def = i.def();
					
					if(key.isEmpty()) key = m.getName();
					add(key, info, def);
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return this;
	}
}