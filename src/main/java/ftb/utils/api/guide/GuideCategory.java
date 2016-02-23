package ftb.utils.api.guide;

import com.google.gson.*;
import ftb.lib.JsonHelper;
import latmod.lib.*;
import latmod.lib.json.IJsonObject;
import net.minecraft.util.*;

import java.util.*;

public class GuideCategory implements Comparable<GuideCategory>, IJsonObject // GuideFile
{
	private static final RemoveFilter<GuideCategory> cleanupFilter = new RemoveFilter<GuideCategory>()
	{
		public boolean remove(GuideCategory c)
		{ return c.subcategories.isEmpty() && c.getUnformattedText().trim().isEmpty(); }
	};
	
	public GuideCategory parent = null;
	private IChatComponent title;
	private ArrayList<IChatComponent> text;
	public final List<GuideCategory> subcategories;
	GuideFile file = null;
	
	public GuideCategory(IChatComponent s)
	{
		title = s;
		text = new ArrayList<>();
		subcategories = new ArrayList<>();
	}
	
	public GuideCategory setParent(GuideCategory c)
	{
		parent = c;
		return this;
	}
	
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
		{
			try
			{
				sb.append(text.get(i).getUnformattedText());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if(i != s - 1) sb.append('\n');
		}
		return sb.toString();
	}
	
	public String getFormattedText()
	{
		if(text.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		int s = text.size();
		for(int i = 0; i < s; i++)
		{
			try
			{
				sb.append(text.get(i).getFormattedText());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			if(i != s - 1) sb.append('\n');
		}
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
		for(GuideCategory subcategory : subcategories) subcategory.clear();
		subcategories.clear();
	}
	
	public void cleanup()
	{
		for(GuideCategory c : subcategories) c.cleanup();
		LMListUtils.removeAll(subcategories, cleanupFilter);
	}
	
	public void sortAll()
	{
		if(subcategories.isEmpty()) return;
		Collections.sort(subcategories, null);
		for(GuideCategory c : subcategories) c.sortAll();
	}
	
	public void copyFrom(GuideCategory c)
	{ for(int i = 0; i < c.subcategories.size(); i++) addSub(c.setParent(this)); }
	
	public GuideCategory getParentTop()
	{
		if(parent == null) return this;
		return parent.getParentTop();
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		JsonArray a;
		
		o.add("N", JsonHelper.serializeICC(title));
		
		if(text.size() > 0)
		{
			a = new JsonArray();
			for(IChatComponent aText : text) a.add(JsonHelper.serializeICC(aText));
			o.add("T", a);
		}
		
		if(!subcategories.isEmpty())
		{
			a = new JsonArray();
			for(GuideCategory subcategory : subcategories) a.add(subcategory.getJson());
			
			o.add("S", a);
		}
		
		return o;
	}
	
	public void setJson(JsonElement e)
	{
		clear();
		
		if(e == null || !e.isJsonObject()) return;
		JsonObject o = e.getAsJsonObject();
		JsonArray a;
		
		title = JsonHelper.deserializeICC(o.get("N"));
		
		if(o.has("T"))
		{
			a = o.get("T").getAsJsonArray();
			for(int i = 0; i < a.size(); i++)
				text.add(JsonHelper.deserializeICC(a.get(i)));
		}
		
		if(o.has("S"))
		{
			a = o.get("S").getAsJsonArray();
			JsonObject o1;
			
			for(int i = 0; i < a.size(); i++)
			{
				o1 = a.get(i).getAsJsonObject();
				GuideCategory c = new GuideCategory(null);
				c.setParent(this);
				c.setJson(o1);
				subcategories.add(c);
			}
		}
	}
}