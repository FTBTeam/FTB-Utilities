package latmod.ftbu.api.guide;

import cpw.mods.fml.relauncher.*;
import ftb.lib.LMNBTUtils;
import latmod.lib.FastList;
import net.minecraft.nbt.*;
import net.minecraft.util.*;

public class GuideCategory implements Comparable<GuideCategory> // GuideFile
{
	private static final FastList.RemoveFilter<GuideCategory> cleanupFilter = new FastList.RemoveFilter<GuideCategory>()
	{
		public boolean remove(GuideCategory c)
		{ return c.subcategories.isEmpty() && c.getUnformattedText().trim().isEmpty(); }
	};

	public GuideCategory parent = null;
	private IChatComponent title;
	private FastList<IChatComponent> text;
	public final FastList<GuideCategory> subcategories;
	GuideFile file = null;
	
	public GuideCategory(IChatComponent s)
	{
		title = s;
		text = new FastList<>();
		subcategories = new FastList<>();
	}
	
	public GuideCategory setParent(GuideCategory c)
	{ parent = c; return this; }
	
	public GuideFile getFile()
	{
		if(file != null) return file;
		else return parent == null ? null : parent.getFile();
	}
	
	public void println(IChatComponent c)
	{ text.add(c); }
	
	public void println(String s)
	{ if(s != null) println(new ChatComponentText(s)); }
	
	public String getUnformattedText()
	{
		if(text.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		int s = text.size();
		for(int i = 0; i < s; i++)
		{ sb.append(text.get(i).getUnformattedText());
		if(i != s - 1) sb.append('\n'); }
		return sb.toString();
	}
	
	@SideOnly(Side.CLIENT)
	public String getFormattedText()
	{
		if(text.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		int s = text.size();
		for(int i = 0; i < s; i++)
		{ sb.append(text.get(i).getFormattedText());
		if(i != s - 1) sb.append('\n'); }
		return sb.toString();
	}
	
	public void addSub(GuideCategory c)
	{ subcategories.add(c); }
	
	public IChatComponent getTitleComponent()
	{ return title; }
	
	public String toString()
	{ return title.getUnformattedText().trim(); }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || toString().equals(o.toString())); }
	
	public GuideCategory getSub(IChatComponent s)
	{
		for(GuideCategory c : subcategories)
		{ if(c.toString().equalsIgnoreCase(s.getUnformattedText().trim())) return c; }
		
		GuideCategory c = new GuideCategory(s);
		c.setParent(this);
		subcategories.add(c);
		return c;
	}
	
	public int compareTo(GuideCategory o)
	{ return toString().compareToIgnoreCase(o.toString()); }

	public void clear()
	{
		text.clear();
		for(int i = 0; i < subcategories.size(); i++)
			subcategories.get(i).clear();
		subcategories.clear();
	}
	
	void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("N", IChatComponent.Serializer.func_150696_a(title));
		
		if(text.size() > 0)
		{
			NBTTagList list = new NBTTagList();
			for(int i = 0; i < text.size(); i++)
				list.appendTag(new NBTTagString(IChatComponent.Serializer.func_150696_a(text.get(i))));
			tag.setTag("T", list);
		}
		
		if(!subcategories.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			for(int i = 0; i < subcategories.size(); i++)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				subcategories.get(i).writeToNBT(tag1);
				list.appendTag(tag1);
			}
			
			tag.setTag("S", list);
		}
	}
	
	void readFromNBT(NBTTagCompound tag)
	{
		clear();
		
		title = IChatComponent.Serializer.func_150699_a(tag.getString("N"));
		
		if(tag.hasKey("T"))
		{
			NBTTagList list = tag.getTagList("T", LMNBTUtils.STRING);
			for(int i = 0; i < list.tagCount(); i++)
				text.add(IChatComponent.Serializer.func_150699_a(list.getStringTagAt(i)));
		}
		
		if(tag.hasKey("S"))
		{
			NBTTagList list = tag.getTagList("S", LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				GuideCategory c = new GuideCategory(null);
				c.setParent(this);
				c.readFromNBT(tag1);
				subcategories.add(c);
			}
		}
	}
	
	public void cleanup()
	{
		subcategories.removeAll(cleanupFilter);
		for(GuideCategory c : subcategories)
			c.cleanup();
	}

	public void copyFrom(GuideCategory c)
	{ for(int i = 0; i < c.subcategories.size(); i++) addSub(c.setParent(this)); }

	public GuideCategory getParentTop()
	{ if(parent == null) return this; return parent.getParentTop(); }
}