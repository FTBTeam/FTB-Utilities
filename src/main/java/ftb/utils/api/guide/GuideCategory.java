package ftb.utils.api.guide;

import com.google.gson.*;
import ftb.lib.JsonHelper;
import latmod.lib.*;
import latmod.lib.json.IJsonObject;
import latmod.lib.util.FinalIDObject;
import net.minecraft.util.*;

import java.util.*;

public class GuideCategory extends FinalIDObject implements IJsonObject // GuideFile
{
	private static final RemoveFilter<Map.Entry<String, GuideCategory>> cleanupFilter = new RemoveFilter<Map.Entry<String, GuideCategory>>()
	{
		public boolean remove(Map.Entry<String, GuideCategory> entry)
		{ return entry.getValue().subcategories.isEmpty() && entry.getValue().getUnformattedText().trim().isEmpty(); }
	};
	
	public GuideCategory parent = null;
	private IChatComponent title;
	public final ArrayList<IChatComponent> text;
	public final Map<String, GuideCategory> subcategories;
	GuideFile file = null;
	
	public GuideCategory(String id)
	{
		super(id);
		text = new ArrayList<>();
		subcategories = new LinkedHashMap<>();
	}
	
	public GuideCategory setTitle(IChatComponent c)
	{
		title = c;
		return this;
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
	
	public void printlnText(String s)
	{ println((s == null || s.isEmpty()) ? null : new ChatComponentText(s)); }
	
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
	{ subcategories.put(c.ID, c); }
	
	public IChatComponent getTitleComponent()
	{ return title == null ? new ChatComponentText(ID) : title; }
	
	public GuideCategory getSub(String id)
	{
		GuideCategory c = subcategories.get(id);
		if(c == null)
		{
			c = new GuideCategory(id);
			c.setParent(this);
			subcategories.put(id, c);
		}
		
		return c;
	}
	
	public void clear()
	{
		text.clear();
		subcategories.clear();
	}
	
	public void cleanup()
	{
		for(GuideCategory c : subcategories.values()) c.cleanup();
		LMMapUtils.removeAll(subcategories, cleanupFilter);
	}
	
	public void sortAll()
	{
		//TODO: sort
		for(GuideCategory c : subcategories.values()) c.sortAll();
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
		
		if(title != null) o.add("N", JsonHelper.serializeICC(title));
		
		if(!text.isEmpty())
		{
			JsonArray a = new JsonArray();
			for(IChatComponent c : text)
				a.add(JsonHelper.serializeICC(c));
			o.add("T", a);
		}
		
		if(!subcategories.isEmpty())
		{
			JsonObject o1 = new JsonObject();
			for(GuideCategory c : subcategories.values())
				o1.add(c.ID, c.getJson());
			o.add("S", o1);
		}
		
		return o;
	}
	
	public void setJson(JsonElement e)
	{
		clear();
		
		if(e == null || !e.isJsonObject()) return;
		JsonObject o = e.getAsJsonObject();
		
		title = o.has("N") ? JsonHelper.deserializeICC(o.get("N")) : null;
		
		if(o.has("T"))
		{
			JsonArray a = o.get("T").getAsJsonArray();
			for(int i = 0; i < a.size(); i++)
				text.add(JsonHelper.deserializeICC(a.get(i)));
		}
		
		if(o.has("S"))
		{
			JsonObject o1 = o.get("S").getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> entry : o1.entrySet())
			{
				GuideCategory c = new GuideCategory(entry.getKey());
				c.setParent(this);
				c.setJson(entry.getValue());
				subcategories.put(c.ID, c);
			}
		}
	}
}