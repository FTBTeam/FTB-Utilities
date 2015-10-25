package latmod.ftbu.api.guide;

import ftb.lib.LMNBTUtils;
import latmod.lib.FastList;
import net.minecraft.nbt.*;

public class GuideCategory implements Comparable<GuideCategory> // GuideFile
{
	public final GuideCategory parent;
	private String title;
	private StringBuilder text;
	public final FastList<GuideCategory> subcategories;
	
	public GuideCategory(GuideCategory p, String s)
	{
		parent = p;
		title = s;
		text = new StringBuilder();
		subcategories = new FastList<GuideCategory>();
	}
	
	public void print(String s)
	{ text.append(s); }
	
	public void println(String s)
	{ text.append(s); text.append('\n'); }
	
	public String getText()
	{ return text.toString(); }
	
	public void addSub(GuideCategory c)
	{ subcategories.add(c); }
	
	public String getTitle()
	{ return title; }
	
	public String toString()
	{ return title + ": " + text + " + " + subcategories; }
	
	public boolean equals(Object o)
	{ return o != null && (o == this || (o instanceof GuideCategory && getTitle().equals(((GuideCategory)o).getTitle()))); }
	
	public GuideCategory getSub(String s)
	{
		GuideCategory c = null;
		
		for(int i = 0; i < subcategories.size(); i++)
		{
			GuideCategory c1 = subcategories.get(i);
			if(c1.getTitle().equals(s)) { c = c1; break; }
		}
		
		if(c == null)
		{
			c = new GuideCategory(this, s);
			subcategories.add(c);
		}
		
		return c;
	}
	
	public int compareTo(GuideCategory o)
	{ return getTitle().compareToIgnoreCase(o.getTitle()); }

	public void clear()
	{
		text = new StringBuilder();
		for(int i = 0; i < subcategories.size(); i++)
			subcategories.get(i).clear();
		subcategories.clear();
	}
	
	public void writeToNBT(NBTTagCompound tag)
	{
		tag.setString("Title", title);
		
		if(text.length() > 0)
			tag.setString("Text", text.toString());
		
		if(!subcategories.isEmpty())
		{
			NBTTagList list = new NBTTagList();
			for(int i = 0; i < subcategories.size(); i++)
			{
				NBTTagCompound tag1 = new NBTTagCompound();
				subcategories.get(i).writeToNBT(tag1);
				list.appendTag(tag1);
			}
			tag.setTag("Sub", list);
		}
	}
	
	public void readFromNBT(NBTTagCompound tag)
	{
		clear();
		
		title = tag.getString("Title");
		
		if(tag.hasKey("Text"))
			text.append(tag.getString("Text"));
		
		if(tag.hasKey("Sub"))
		{
			NBTTagList list = tag.getTagList("Sub", LMNBTUtils.MAP);
			
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				GuideCategory c = new GuideCategory(this, null);
				c.readFromNBT(tag1);
				subcategories.add(c);
			}
		}
	}
}