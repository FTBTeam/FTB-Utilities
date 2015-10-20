package latmod.ftbu.api.guide;

import java.io.File;
import java.lang.reflect.Field;

import ftb.lib.FTBLib;
import ftb.lib.api.EventFTBModeSet;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.*;
import latmod.lib.LMFileUtils;
import net.minecraft.util.EnumChatFormatting;

public class GuideFile
{
	public static final GuideFile inst = new GuideFile();
	
	public final GuideCategory main;
	
	private GuideFile()
	{ main = new GuideCategory(null, "Guide"); }
	
	public void reload(EventFTBModeSet e)
	{
		if(FTBLibFinals.DEV) FTBLib.logger.info("Guide reloaded @ " + e.side);
		
		main.clear();
		
		File file = e.getCommonFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
				for(int i = 0; i < f.length; i++)
					loadFromFiles(main, f[i]);
		}
		
		file = e.getFile("guide/");
		if(file.exists() && file.isDirectory())
		{
			File[] f = file.listFiles();
			if(f != null && f.length > 0)
				for(int i = 0; i < f.length; i++)
					loadFromFiles(main, f[i]);
		}
		
		file = e.getFile("guide_intro.txt");
		if(file.exists() && file.isFile())
		{
			try
			{
				String text = LMFileUtils.loadAsText(file);
				main.println(text);
			}
			catch(Exception ex)
			{ ex.printStackTrace(); }
		}
		
		FTBUConfig.onGuideEvent(this);
		FTBU.proxy.onGuideEvent(this);
		new EventFTBUGuide(this).post();
	}
	
	private void loadFromFiles(GuideCategory c, File f)
	{
		if(!f.exists()) return;
		
		String name = f.getName();
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				GuideCategory c1 = c.getSub(name);
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(name.endsWith(".txt"))
			{
				try
				{
					GuideCategory c1 = c.getSub(name.substring(0, name.length() - 4));
					String text = LMFileUtils.loadAsText(f);
					c1.println(text);
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
	}
	
	/*
	public void writeToNBT(NBTTagCompound tag)
	{
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
	}*/
	
	public void addConfigFromClass(String mod, String id, Class<?> c)
	{
		if(!FTBUConfigGeneral.configInfoGuide.get()) return;
		
		try
		{
			GuideCategory category = main.getSub("Mods").getSub("Config").getSub(mod).getSub(id);
			
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
					sb.append(EnumChatFormatting.RED);
					sb.append(key);
					sb.append(EnumChatFormatting.RESET);
					sb.append(" - ");
					sb.append(info);
					sb.append(EnumChatFormatting.BLUE);
					sb.append(" Default: ");
					sb.append(def);
					sb.append(EnumChatFormatting.RESET);
					sb.append('\n');
					category.println(sb.toString());
				}
			}
			
			/*
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
					sb.append('\n');
					category.println(sb.toString());
				}
			}
			*/
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}

	public GuideCategory getMod(String s)
	{ return main.getSub("Mods").getSub(s); }
}