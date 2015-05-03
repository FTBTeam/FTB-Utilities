package latmod.core;
import java.io.File;
import java.util.regex.Pattern;

import latmod.core.mod.LC;
import latmod.core.util.*;
import net.minecraft.nbt.NBTTagCompound;
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
		
		public String getString(String s, String def)
		{ return getString(s, def, (Pattern)null); }
		
		public String getString(String s, String def, String[] valid)
		{ return config.config.getString(s, cat, def, "", valid, config.getLang(s)); }
		
		public String getString(String s, String def, Pattern p)
		{ return config.config.getString(s, cat, def, "", config.getLang(s), p); }
		
		public boolean getBool(String s, boolean def)
		{ return config.config.getBoolean(s, cat, def, "", config.getLang(s)); }
		
		public int getInt(String s, int def, int min, int max)
		{ return config.config.getInt(s, cat, def, min, max, "", config.getLang(s)); }
		
		public int getInt(String s, int def)
		{ return getInt(s, def, -1, Integer.MAX_VALUE); }
		
		public float getFloat(String s, float def, float min, float max)
		{ return config.config.getFloat(s, cat, def, min, max, "", config.getLang(s)); }
		
		public float getFloat(String s, float def)
		{ return getFloat(s, def, -1F, Float.MAX_VALUE); }
		
		public FastList<String> getStringArray(String s, String[] def, String[] valid)
		{ return FastList.asList(config.config.getStringList(s, cat, def, "", valid, config.getLang(s))); }
		
		public FastList<String> getStringArray(String s, String[] def)
		{ return getStringArray(s, def, new String[0]); }
		
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
		
		public void setLang(String property, String langKey)
		{
			ConfigCategory cat1 = getCategory();
			Property prop = cat1.get(property);
			if(prop != null) prop.setLanguageKey(langKey);
		}
		
		public void remove(String s)
		{
			ConfigCategory cat1 = getCategory();
			if(cat1.containsKey(s)) cat1.remove(s);
		}
	}
	
	public final File loadedFrom;
	public final Configuration config;
	public LMMod mod = null;
	
	public LMConfig(File f)
	{
		loadedFrom = f;
		config = new Configuration(loadedFrom);
		load(); save();
	}
	
	public LMConfig(FMLPreInitializationEvent e, String s)
	{ this(new File(e.getModConfigurationDirectory(), s)); }
	
	public void setMod(LMMod m)
	{ mod = m; }
	
	public void save()
	{ if(config.hasChanged()) config.save(); }
	
	public Category get(String s)
	{ return new Category(this, s); }
	
	public ConfigCategory getCategory(String s)
	{ return config.getCategory(s); }
	
	@SideOnly(Side.CLIENT)
	public String getAbridgedPath()
	{ return GuiConfig.getAbridgedConfigPath(LC.mod.config.config.toString()); }
	
	public String getLang(String s)
	{ return (mod == null ? "" : mod.assets.replace(':', '.')) + "config." + s; }
	
	public abstract void load();
	
	public void reload()
	{ config.load(); load(); }
	
	public String getConfigName()
	{ return (mod == null) ? null : mod.modID; }
	
	public static boolean[] readBools(NBTTagCompound tag, String s)
	{ return Bits.fromBits(tag.getByte(s) & 255, 8); }
	
	public static void writeBools(NBTTagCompound tag, String s, boolean... b)
	{
		if(b.length <= 0 || b.length > 8) throw new IllegalArgumentException("0 < " + b.length + " <= 8");
		tag.setByte(s, (byte)Bits.toBits(b));
	}
}