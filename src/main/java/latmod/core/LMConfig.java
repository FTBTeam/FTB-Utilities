package latmod.core;
import java.io.File;
import java.util.regex.Pattern;

import latmod.core.util.*;
import latmod.latcore.LC;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.*;

public abstract class LMConfig
{
	public static final class Category
	{
		public final LMConfig config;
		public final String cat;
		
		private Category(LMConfig c, String s)
		{ config = c; cat = s; }
		
		public String getString(String s, String def, String lang)
		{ return getString(s, def, (Pattern)null, lang); }
		
		public String getString(String s, String def, String[] valid, String lang)
		{ return config.config.getString(s, cat, def, "", valid, lang); }
		
		public String getString(String s, String def, Pattern p, String lang)
		{ return config.config.getString(s, cat, def, "", lang, p); }
		
		public boolean getBool(String s, boolean def, String lang)
		{ return config.config.getBoolean(s, cat, def, "", lang); }
		
		public int getInt(String s, int def, int min, int max, String lang)
		{ return config.config.getInt(s, cat, def, min, max, "", lang); }
		
		public int getInt(String s, int def, String lang)
		{ return getInt(s, def, -1, Integer.MAX_VALUE, lang); }
		
		public float getFloat(String s, float def, float min, float max, String lang)
		{ return config.config.getFloat(s, cat, def, min, max, "", lang); }
		
		public float getFloat(String s, float def, String lang)
		{ return getFloat(s, def, -1F, Float.MAX_VALUE, lang); }
		
		public FastList<String> getStringArray(String s, String[] def, String[] valid, String lang)
		{ return FastList.asList(config.config.getStringList(s, cat, def, "", valid, lang)); }
		
		public FastList<String> getStringArray(String s, String[] def, String lang)
		{ return getStringArray(s, def, new String[0], lang); }
		
		public ConfigCategory getCategory()
		{ return config.config.getCategory(cat); }
		
		public void setComment(String property, String... comment)
		{
			if(comment == null || comment.length == 0) return;
			
			ConfigCategory cat1 = getCategory();
			Property prop = cat1.get(property);
			
			if(prop != null)
			{
				String s = "";
				for(int i = 0; i < comment.length; i++)
				{ s += comment[i]; if(i < comment.length - 1) s += '\n'; }
				prop.comment = s;
			}
		}
		
		public void setCategoryComment(String... comment)
		{ config.config.setCategoryComment(cat, LatCore.unsplit(comment, "\n")); }
		
		@Deprecated
		public void setName(String property, String name)
		{
			ConfigCategory cat1 = getCategory();
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
	
	public ConfigCategory getCategory(String s)
	{ return config.getCategory(s); }
	
	@SideOnly(Side.CLIENT)
	public String getAbridgedPath()
	{ return GuiConfig.getAbridgedConfigPath(LC.mod.config.config.toString()); }
	
	public abstract void load();
}