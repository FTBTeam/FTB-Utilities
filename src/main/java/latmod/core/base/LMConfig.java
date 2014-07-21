package latmod.core.base;
import latmod.core.*;
import latmod.core.util.FastMap;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.common.event.*;

public class LMConfig
{
	public class Category
	{
		public final String cat;
		
		public Category(String s)
		{ cat = s; }
		
		public boolean getBool(String s, boolean def, String... comment)
		{
			boolean b1 = config.get(cat, s, def).getBoolean(def);
			if(comment.length > 0) setComment(s, comment); return b1;
		}
		
		public int getInt(String s, int def, String... comment)
		{
			int i1 = config.get(cat, s, def).getInt();
			if(comment.length > 0) setComment(s, comment); return i1;
		}
		
		public int getInt(String s, int def, int min, int max, String... comment)
		{
			int i = getInt(s, def);
			if(i < min) i = min;
			if(i > max) i = max;
			if(comment.length > 0) setComment(s, comment);
			return i;
		}
		
		public double getDouble(String s, double def, String... comment)
		{
			double d1 = config.get(cat, s, def).getDouble(def);
			if(comment.length > 0) setComment(s, comment); return d1;
		}
		
		public void setComment(String property, String... comment)
		{
			ConfigCategory cat1 = config.getCategory(cat);
			Property prop = cat1.get(property);
			
			if(prop != null)
			{
				String s = "";
				for(int i = 0; i < comment.length; i++)
				{ s += comment[i]; if(i < comment.length - 1) s += '\n'; }
				prop.comment = s;
			}
		}
	}
	
	public Configuration config;
	public FastMap<String, Category> categories;
	
	public LMConfig(FMLPreInitializationEvent e, String s)
	{
		config = LatCore.loadConfig(e, s);
		categories = new FastMap<String, Category>();
	}
	
	public void add(Category c)
	{ categories.put(c.cat, c); }
	
	public void save()
	{
		if(config.hasChanged())
			config.save();
	}
}