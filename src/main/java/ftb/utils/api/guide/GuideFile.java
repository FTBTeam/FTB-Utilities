package ftb.utils.api.guide;

import com.google.gson.*;
import ftb.lib.FTBLib;
import ftb.utils.net.MessageDisplayGuide;
import latmod.lib.*;
import latmod.lib.json.IJsonObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;
import net.minecraftforge.common.util.FakePlayer;

import java.io.File;
import java.util.*;

public class GuideFile implements IJsonObject // ServerGuideFile // ClientGuideFile
{
	public final GuideCategory main;
	public final HashMap<String, GuideLink> links;
	
	public GuideFile(String id)
	{
		main = new GuideCategory(id);
		main.file = this;
		links = new HashMap<>();
	}
	
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
	
	protected static void loadFromFiles(GuideCategory c, File f)
	{
		if(f == null || !f.exists()) return;
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				Arrays.sort(f1, LMFileUtils.fileComparator);
				GuideCategory c1 = c.getSub(f.getName());
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(f.getName().endsWith(".txt"))
			{
				try
				{
					GuideCategory c1 = c.getSub(LMFileUtils.getRawFileName(f));
					String txt = LMFileUtils.loadAsText(f);
					if(txt != null && !txt.isEmpty()) c1.printlnText(txt.replace("\r", ""));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	protected static Map<String, GuideLink> loadLinksFromFile(File f)
	{
		HashMap<String, GuideLink> map = new HashMap<>();
		JsonElement linksMapE = LMJsonUtils.fromJson(LMFileUtils.newFile(f));
		
		if(linksMapE.isJsonObject())
		{
			JsonObject o = linksMapE.getAsJsonObject();
			GuideLink link;
			
			if(o.has("links")) // Old format
			{
				JsonObject o1;
				
				try
				{
					for(Map.Entry<String, JsonElement> e : o.get("links").getAsJsonObject().entrySet())
					{
						o1 = e.getValue().getAsJsonObject();
						
						if(o1.get("type").getAsString().equals("image"))
						{
							link = GuideLink.newInstance(GuideLink.Type.IMAGE);
							link.link = o1.get("link").getAsString();
							
							if(o1.has("hover"))
							{
								link.hover = new IChatComponent[] {new ChatComponentText(o1.get("hover").getAsString())};
							}
							
							map.put(e.getKey(), link);
						}
					}
				}
				catch(Exception ex) {}
			}
			else
			{
				if(o.has("images"))
				{
					for(Map.Entry<String, JsonElement> e : o.get("images").getAsJsonObject().entrySet())
					{
						link = GuideLink.newInstance(GuideLink.Type.IMAGE);
						link.setJson(e.getValue());
						map.put(e.getKey(), link);
					}
				}
				
				if(o.has("urls"))
				{
					for(Map.Entry<String, JsonElement> e : o.get("urls").getAsJsonObject().entrySet())
					{
						link = GuideLink.newInstance(GuideLink.Type.URL);
						link.setJson(e.getValue());
						map.put(e.getKey(), link);
					}
				}
				
				if(o.has("recipes"))
				{
					for(Map.Entry<String, JsonElement> e : o.get("recipes").getAsJsonObject().entrySet())
					{
						link = GuideLink.newInstance(GuideLink.Type.RECIPE);
						link.setJson(e.getValue());
						map.put(e.getKey(), link);
					}
				}
			}
		}
		
		return map;
	}
	
	public static void displayGuide(EntityPlayerMP ep, GuideFile file)
	{
		if(ep != null && file != null && !(ep instanceof FakePlayer)) new MessageDisplayGuide(file).sendTo(ep);
	}
	
	public JsonElement getJson()
	{
		JsonObject o = new JsonObject();
		
		if(links.size() > 0)
		{
			JsonArray a = new JsonArray();
			JsonObject o2;
			
			for(Map.Entry<String, GuideLink> e : links.entrySet())
			{
				GuideLink l = e.getValue();
				o2 = (JsonObject) l.getJson();
				o2.add("ID", new JsonPrimitive(l.type.ordinal()));
				o2.add("key", new JsonPrimitive(e.getKey()));
				a.add(o2);
			}
			
			o.add("L", a);
		}
		
		o.add("G", main.getJson());
		
		return o;
	}
	
	public void setJson(JsonElement e)
	{
		links.clear();
		main.clear();
		
		if(e == null || !e.isJsonObject()) return;
		JsonObject o = e.getAsJsonObject();
		
		if(o.has("L"))
		{
			JsonArray a = o.get("L").getAsJsonArray();
			JsonObject o1;
			
			for(int i = 0; i < a.size(); i++)
			{
				o1 = a.get(i).getAsJsonObject();
				GuideLink l = GuideLink.newInstance(GuideLink.Type.values()[o1.get("type").getAsInt()]);
				l.setJson(o1);
				links.put(o1.get("ID").getAsString(), l);
			}
		}
		
		main.setJson(o.get("G"));
	}
}