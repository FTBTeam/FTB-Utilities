package latmod.ftbu.api.guide;

import ftb.lib.*;
import latmod.lib.FastMap;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public class GuideFile // ServerGuideFile // ClientGuideFile
{
	public final GuideCategory main;
	public final FastMap<String, GuideLink> links;
	
	public GuideFile(IChatComponent title)
	{
		main = new GuideCategory(null, title);
		main.file = this;
		links = new FastMap<String, GuideLink>();
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
		
		if(tag == null) { main.clear(); return; }
		
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