package latmod.ftbu.api.guide;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

import ftb.lib.*;
import ftb.lib.api.EventFTBModeSet;
import ftb.lib.mod.FTBLibFinals;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.client.gui.guide.GuideLinkSerializer;
import latmod.ftbu.mod.config.*;
import latmod.lib.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public class GuideFile
{
	public static final GuideFile modpackGuide = new GuideFile(new ChatComponentTranslation(FTBU.mod.assets + "button.guide"));
	
	public final GuideCategory main;
	public final FastMap<String, GuideLink> links;
	
	public GuideFile(IChatComponent title)
	{
		main = new GuideCategory(null, title);
		main.file = this;
		links = new FastMap<String, GuideLink>();
	}
	
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
		
		links.clear();
		LinksMap linksMap = LMJsonUtils.fromJsonFile(GuideLinkSerializer.gson, LMFileUtils.newFile(new File(FTBLib.folderModpack, "guide_links.json")), LinksMap.class);
		if(linksMap != null && linksMap.links != null) links.putAll(linksMap.links);
		
		if(FTBUConfigGeneral.configInfoGuide.get())
		{
			FTBUConfig.onGuideEvent(this);
			FTBU.proxy.onGuideEvent(this);
			new EventFTBUGuide(this).post();
		}
	}
	
	private static class LinksMap
	{
		public Map<String, GuideLink> links;
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
				GuideCategory c1 = c.getSub(new ChatComponentText(name));
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(name.endsWith(".txt"))
			{
				try
				{
					GuideCategory c1 = c.getSub(new ChatComponentText(name.substring(0, name.length() - 4)));
					String text = LMFileUtils.loadAsText(f);
					c1.println(text);
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
	}
	
	public void addConfigFromClass(String mod, String id, Class<?> c)
	{
		if(!FTBUConfigGeneral.configInfoGuide.get()) return;
		
		try
		{
			GuideCategory category = getMod("Config").getSub(new ChatComponentText(mod)).getSub(new ChatComponentText(id));
			
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
					sb.append('[');
					sb.append(key);
					sb.append(']');
					sb.append('\n');
					sb.append(EnumChatFormatting.BLUE);
					sb.append("Default: ");
					sb.append(def);
					sb.append(EnumChatFormatting.RESET);
					sb.append('\n');
					sb.append(info);
					sb.append(EnumChatFormatting.RESET);
					sb.append('\n');
					category.println(sb.toString());
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}

	public GuideCategory getMod(String s)
	{ return main.getSub(new ChatComponentText("Mods")).getSub(new ChatComponentText(s)); }
	
	public GuideLink getGuideLink(String s)
	{
		if(s != null)
		{
			s = FTBLib.removeFormatting(s.trim());
			if(s.length() > 2 && s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']')
				return links.get(s.substring(1, s.length() - 1));
		}
		
		return null;
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		links.clear();
		
		if(tag.hasKey("L"))
		{
			NBTTagList linksList = tag.getTagList("L", LMNBTUtils.MAP);
			
			for(int i = 0; i < linksList.tagCount(); i++)
			{
				NBTTagCompound tag1 = linksList.getCompoundTagAt(i);
				GuideLink l = new GuideLink(tag1.getByte("I"));
				
				l.link = tag1.getString("L");
				if(tag1.hasKey("T")) l.title = IChatComponent.Serializer.func_150699_a(tag1.getString("T"));
				if(tag1.hasKey("H")) l.hover = IChatComponent.Serializer.func_150699_a(tag1.getString("H"));
				
				links.put(tag1.getString("ID"), l);
			}
		}
		
		main.readFromNBT(tag);
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		if(links.size() > 0)
		{
			NBTTagList linksList = new NBTTagList();
			
			for(int i = 0; i < links.size(); i++)
			{
				GuideLink l = links.values.get(i);
				
				NBTTagCompound tag1 = new NBTTagCompound();
				
				tag1.setByte("I", (byte)l.type);
				tag1.setString("ID", links.keys.get(i));
				if(!l.link.isEmpty()) tag1.setString("L", l.link);
				if(l.title != null) tag1.setString("T", IChatComponent.Serializer.func_150696_a(l.title));
				if(l.hover != null) tag1.setString("H", IChatComponent.Serializer.func_150696_a(l.hover));
				
				linksList.appendTag(tag1);
			}
			
			tag.setTag("L", linksList);
		}
		
		main.writeToNBT(tag);
	}
}