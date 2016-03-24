package ftb.utils.api.guide;

import com.google.gson.*;
import cpw.mods.fml.relauncher.*;
import ftb.lib.JsonHelper;
import ftb.utils.api.guide.lines.*;
import ftb.utils.mod.client.gui.guide.*;
import ftb.utils.net.MessageDisplayGuide;
import latmod.lib.*;
import latmod.lib.json.IJsonObject;
import latmod.lib.util.FinalIDObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;

import java.io.File;
import java.util.*;

public class GuidePage extends FinalIDObject implements IJsonObject // GuideFile
{
	private static final RemoveFilter<Map.Entry<String, GuidePage>> cleanupFilter = new RemoveFilter<Map.Entry<String, GuidePage>>()
	{
		public boolean remove(Map.Entry<String, GuidePage> entry)
		{ return entry.getValue().childPages.isEmpty() && entry.getValue().getUnformattedText().trim().isEmpty(); }
	};
	
	public GuidePage parent = null;
	private IChatComponent title;
	public final List<GuideTextLine> text;
	public final Map<String, GuidePage> childPages;
	
	public GuidePage(String id)
	{
		super(id);
		text = new ArrayList<>();
		childPages = new LinkedHashMap<>();
	}
	
	public GuidePage setTitle(IChatComponent c)
	{
		title = c;
		return this;
	}
	
	public GuidePage setParent(GuidePage c)
	{
		parent = c;
		return this;
	}
	
	public GuidePage getOwner()
	{
		if(parent == null) return this;
		return parent.getOwner();
	}
	
	public void println(IChatComponent c)
	{ text.add(c == null ? null : new GuideExtendedTextLine(this, c)); }
	
	public void printlnText(String s)
	{ text.add((s == null || s.isEmpty()) ? null : new GuideTextLine(this, s)); }
	
	public String getUnformattedText()
	{
		if(text.isEmpty()) return "";
		StringBuilder sb = new StringBuilder();
		int s = text.size();
		for(int i = 0; i < s; i++)
		{
			GuideTextLine c = text.get(i);
			
			if(c == null || c.getText() == null) sb.append('\n');
			else
			{
				try { sb.append(c.getText().getUnformattedText()); }
				catch(Exception ex) { ex.printStackTrace(); }
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
			GuideTextLine c = text.get(i);
			
			if(c == null || c.getText() == null) sb.append('\n');
			else
			{
				try { sb.append(c.getText().getFormattedText()); }
				catch(Exception ex) { ex.printStackTrace(); }
			}
			
			if(i != s - 1) sb.append('\n');
		}
		return sb.toString();
	}
	
	public void addSub(GuidePage c)
	{ childPages.put(c.getID(), c); }
	
	public IChatComponent getTitleComponent()
	{ return title == null ? new ChatComponentText(getID()) : title; }
	
	public GuidePage getSub(String id)
	{
		GuidePage c = childPages.get(id);
		if(c == null)
		{
			c = new GuidePage(id);
			c.setParent(this);
			childPages.put(id, c);
		}
		
		return c;
	}
	
	public void clear()
	{
		text.clear();
		childPages.clear();
	}
	
	public void cleanup()
	{
		for(GuidePage c : childPages.values()) c.cleanup();
		LMMapUtils.removeAll(childPages, cleanupFilter);
	}
	
	public void sortAll()
	{
		//TODO: sort
		for(GuidePage c : childPages.values()) c.sortAll();
	}
	
	public void copyFrom(GuidePage c)
	{ for(int i = 0; i < c.childPages.size(); i++) addSub(c.setParent(this)); }
	
	public GuidePage getParentTop()
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
			for(GuideTextLine c : text)
				a.add(c == null ? JsonNull.INSTANCE : c.getJson());
			o.add("T", a);
		}
		
		if(!childPages.isEmpty())
		{
			JsonObject o1 = new JsonObject();
			for(GuidePage c : childPages.values())
				o1.add(c.getID(), c.getJson());
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
				text.add(GuideTextLine.get(this, a.get(i)));
		}
		
		if(o.has("S"))
		{
			JsonObject o1 = o.get("S").getAsJsonObject();
			
			for(Map.Entry<String, JsonElement> entry : o1.entrySet())
			{
				GuidePage c = new GuidePage(entry.getKey());
				c.setParent(this);
				c.setJson(entry.getValue());
				childPages.put(c.getID(), c);
			}
		}
	}
	
	protected static void loadFromFiles(GuidePage c, File f)
	{
		if(f == null || !f.exists()) return;
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				Arrays.sort(f1, LMFileUtils.fileComparator);
				GuidePage c1 = c.getSub(f.getName());
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(f.getName().endsWith(".txt"))
			{
				try
				{
					GuidePage c1 = c.getSub(LMFileUtils.getRawFileName(f));
					
					for(String s : LMFileUtils.load(f))
					{
						if(s.isEmpty()) c1.text.add(null);
						else if(s.startsWith("{") && s.endsWith("}"))
						{
							c1.text.add(GuideTextLine.get(c1, LMJsonUtils.fromJson(s)));
						}
						else c1.text.add(new GuideTextLine(c1, s));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void displayGuide(EntityPlayerMP ep)
	{
		if(ep != null && !(ep instanceof FakePlayer)) new MessageDisplayGuide(this).sendTo(ep);
	}
	
	public LMColor getBackgroundColor()
	{ return (parent == null) ? null : parent.getBackgroundColor(); }
	
	public LMColor getTextColor()
	{ return (parent == null) ? null : parent.getTextColor(); }
	
	public Boolean useUnicodeFont()
	{ return (parent == null) ? null : parent.useUnicodeFont(); }
	
	@SideOnly(Side.CLIENT)
	public ButtonGuidePage createButton(GuiGuide gui)
	{ return new ButtonGuidePage(gui, this); }
}