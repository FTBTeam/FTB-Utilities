package latmod.ftbu.api.guide;

import ftb.lib.*;
import latmod.ftbu.mod.client.gui.guide.GuideLinkSerializer;
import latmod.lib.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

import java.io.File;
import java.util.*;

public class GuideFile // ServerGuideFile // ClientGuideFile
{
	public final GuideCategory main;
	public final HashMap<String, GuideLink> links;
	
	public GuideFile(IChatComponent title)
	{
		main = new GuideCategory(title);
		main.file = this;
		links = new HashMap<>();
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
	
	protected static class LinksMap
	{
		public Map<String, GuideLink> links;
	}
	
	protected static void loadFromFiles(GuideCategory c, File f)
	{
		if(f == null || !f.exists()) return;
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				Arrays.sort(f1, LMFileUtils.fileComparator);
				GuideCategory c1 = c.getSub(new ChatComponentText(f.getName()));
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(f.getName().endsWith(".txt"))
			{
				try
				{
					GuideCategory c1 = c.getSub(new ChatComponentText(LMFileUtils.getRawFileName(f)));
					String txt = LMFileUtils.loadAsText(f);
					if(txt != null && !txt.isEmpty())
						c1.println(txt.replace("\r", ""));
				}
				catch(Exception e)
				{ e.printStackTrace(); }
			}
		}
	}
	
	protected void loadLinksFromFile(File f)
	{
		if(f == null || !f.exists()) return;
		links.clear();
		LinksMap linksMap = LMJsonUtils.fromJsonFile(GuideLinkSerializer.gson, LMFileUtils.newFile(f), LinksMap.class);
		if(linksMap != null && linksMap.links != null) links.putAll(linksMap.links);
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		links.clear();
		
		if(tag == null) { main.clear(); return; }
		
		if(tag.hasKey("L"))
		{
			NBTTagList linksList = tag.getTagList("L", LMNBTUtils.MAP);
			
			for(int i = 0; i < linksList.tagCount(); i++)
			{
				NBTTagCompound tag1 = linksList.getCompoundTagAt(i);
				GuideLink l = new GuideLink(LinkType.values()[tag1.getByte("I")], tag1.getString("L"));
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
			
			for(Map.Entry<String, GuideLink> e : links.entrySet())
			{
				GuideLink l = e.getValue();
				
				NBTTagCompound tag1 = new NBTTagCompound();
				
				tag1.setByte("I", (byte)l.type.ordinal());
				tag1.setString("ID", e.getKey());
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