package latmod.core;
import java.io.File;

import latmod.core.util.*;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public abstract class LMConfig
{
	public static final class Category
	{
		public final LMConfig config;
		public final String cat;
		
		private Category(LMConfig c, String s)
		{ config = c; cat = s; }
		
		public String getString(String s, String def, String... comment)
		{
			String s1 = config.config.get(cat, s, def).getString();
			if(comment.length > 0) setComment(s, comment); return s1;
		}
		
		public boolean getBool(String s, boolean def, String... comment)
		{
			boolean b1 = config.config.get(cat, s, def).getBoolean(def);
			if(comment.length > 0) setComment(s, comment); return b1;
		}
		
		public int getInt(String s, int def, String... comment)
		{
			int i1 = config.config.get(cat, s, def).getInt();
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
			double d1 = config.config.get(cat, s, def).getDouble(def);
			if(comment.length > 0) setComment(s, comment); return d1;
		}
		
		public FastList<String> getStringArray(String s, String[] def, String... comment)
		{
			String[] s1 = config.config.get(cat, s, def).getStringList();
			if(comment.length > 0) setComment(s, comment); return new FastList<String>(s1);
		}
		
		public void setComment(String property, String... comment)
		{
			ConfigCategory cat1 = config.config.getCategory(cat);
			Property prop = cat1.get(property);
			
			if(prop != null)
			{
				String s = "";
				for(int i = 0; i < comment.length; i++)
				{ s += comment[i]; if(i < comment.length - 1) s += '\n'; }
				prop.comment = s;
			}
		}
		
		public void setCategoryDesc(String... desc)
		{ config.config.setCategoryComment(cat, LatCore.unsplit(desc, "\n")); }

		public void setName(String property, String name)
		{
			ConfigCategory cat1 = config.config.getCategory(cat);
			Property prop = cat1.get(property);
			if(prop != null) prop.setName(name);
		}
	}
	
	public final File loadedFrom;
	public final Configuration config;
	
	public LMConfig(File f)
	{ loadedFrom = f; config = new Configuration(loadedFrom); load(); save(); }
	
	public LMConfig(FMLPreInitializationEvent e, String s)
	{ this(new File(e.getModConfigurationDirectory(), s)); }
	
	public void save()
	{ if(config.hasChanged()) config.save(); }
	
	public Category get(String s)
	{ return new Category(this, s); }
	
	public abstract void load();
}