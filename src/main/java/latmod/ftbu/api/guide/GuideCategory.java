package latmod.ftbu.api.guide;

import java.lang.reflect.*;

import latmod.lib.FastList;

public class GuideCategory
{
	public final String title;
	public final FastList<GuideText> text;
	public final FastList<GuideCategory> subcategories;
	
	public GuideCategory(String s)
	{
		title = s;
		text = new FastList<GuideText>();
		subcategories = new FastList<GuideCategory>();
	}
	
	public void add(GuideText t)
	{ text.add(t); }
	
	public void add(String text)
	{ add(new GuideText(text)); }
	
	public void add(GuideCategory c)
	{ subcategories.add(c); }
	
	public GuideCategory addFromClass(Class<?> c)
	{
		try
		{
			Field[] fields = c.getDeclaredFields();
			
			if(fields != null && fields.length > 0) for(Field f : fields)
			{
				f.setAccessible(true);
				
				if(f.isAnnotationPresent(GuideInfo.class))
				{
					GuideInfo i = f.getAnnotation(GuideInfo.class);
					
					String key = i.key();
					String info = i.info();
					String def = i.def();
					
					if(key.isEmpty()) key = f.getName();
					
					StringBuilder sb = new StringBuilder();
					sb.append(key);
					sb.append(" - ");
					sb.append(info);
					sb.append(" Default: ");
					sb.append(def);
					add(sb.toString());
				}
			}
			
			Method[] methods = c.getDeclaredMethods();
			
			if(methods != null && methods.length > 0) for(Method m : methods)
			{
				m.setAccessible(true);
				
				if(m.isAnnotationPresent(GuideInfo.class))
				{
					GuideInfo i = m.getAnnotation(GuideInfo.class);
					
					String key = i.key();
					String info = i.info();
					String def = i.def();
					
					if(key.isEmpty()) key = m.getName();
					
					StringBuilder sb = new StringBuilder();
					sb.append(key);
					sb.append(" - ");
					sb.append(info);
					sb.append(" Default: ");
					sb.append(def);
					add(sb.toString());
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return this;
	}
}