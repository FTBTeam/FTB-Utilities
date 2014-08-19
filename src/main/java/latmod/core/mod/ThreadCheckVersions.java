package latmod.core.mod;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import latmod.core.*;
import latmod.core.util.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.*;

public class ThreadCheckVersions implements Runnable
{
	public ICommandSender output;
	public boolean chatCommand;
	
	public Thread thread;
	
	public static void init(ICommandSender ics, boolean b)
	{
		ThreadCheckVersions t = new ThreadCheckVersions();
		t.output = ics;
		t.chatCommand = b;
		
		t.thread = new Thread(t, "LatMod_CheckVersions");
		t.thread.start();
	}
	
	public void run()
	{
		int failed = 0;
		
		try
		{
			failed = 1;
			
			InputStream is = new URL("http://pastebin.com/raw.php?i=N8gUpQj8").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			failed = 2;
			
			if(s.length() > 0 && s.startsWith("{") && s.endsWith("}"))
			{
				LC.versionsFile = LMUtils.fromJson(s, LMUtils.getMapType(String.class, LMUtils.getMapType(String.class, String.class)));
				failed = 0;
			}
		}
		catch(Exception ex)
		{ LC.versionsFile = new HashMap<String, Map<String, String>>(); }
		
		if(output != null)
		{
			if(failed == 1)
			{
				if(chatCommand) LatCore.printChat(output, "Failed to check versions!");
			}
			else if(failed == 2)
			{
				if(chatCommand) LatCore.printChat(output, "Invalid version file!");	
			}
			else
			{
				FastList<IChatComponent> toPrint = new FastList<IChatComponent>();
				
				for(int i = 0; i < LC.versionsToCheck.size(); i++)
				{
					String mod_id = LC.versionsToCheck.keys.get(i);
					String mod_version = LC.versionsToCheck.values.get(i);
					
					Map<String, String> m = LC.versionsFile.get(mod_id);
					
					if(m != null && m.size() > 0)
					{
						String[] versions = m.keySet().toArray(new String[0]);
						
						if(versions.length > 0)
						{
							if(versions.length > 1) Arrays.sort(versions, comparator);
							
							String lver = versions[0];
							
							if(!lver.equals(mod_version))
							{
								if(toPrint.isEmpty())
									toPrint.add(new ChatComponentText("These LatvianModder's mods has updates:"));
								
								IChatComponent ic1 = new ChatComponentText(mod_id + EnumChatFormatting.GOLD + " ");
								
								IChatComponent ic2 = new ChatComponentText("[ " + lver + " ]");
								
								ic2.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Download " + mod_id)));
								ic2.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/LatvianModder/Files/tree/Mods/" + mod_id + "/1.7.10"));
								ic2.getChatStyle().setColor(EnumChatFormatting.GOLD);
								
								ic1.appendSibling(ic2);
					            
					            ic1.appendText(": " + EnumChatFormatting.GRAY + m.get(lver));
								
								toPrint.add(ic1);
								
								//mod_id + EnumChatFormatting.GOLD + " [ " + lver + " ])
							}
						}
					}
				}
				
				if(!toPrint.isEmpty()) for(IChatComponent s : toPrint)
					//LatCore.printChat(output, s);
					output.addChatMessage(s);
				else if(chatCommand)
					LatCore.printChat(output, "Everyting is up to date");
			}
		}
		
		thread = null;
	}
	
	public static final VersionComparator comparator = new VersionComparator();
	
	public static class VersionComparator implements Comparator<String>
	{
		public int compare(String s1, String s2)
		{ return -compareCorrectOrder(s1, s2); }
		
		public int compareCorrectOrder(String s1, String s2)
		{
			String[] s1_s = s1.split("\\.");
			String[] s2_s = s2.split("\\.");
			
			if(s1_s.length == 3 && s2_s.length == 3)
			{
				int v0 = Integer.parseInt(s1_s[0]);
				int v1 = Integer.parseInt(s2_s[0]);
				
				if(v0 == v1)
				{
					v0 = Integer.parseInt(s1_s[1]);
					v1 = Integer.parseInt(s2_s[1]);
					
					if(v0 == v1)
					{
						v0 = Integer.parseInt(s1_s[2]);
						v1 = Integer.parseInt(s2_s[2]);
					}
				}
				
				return Integer.compare(v0, v1);
			}
			else if(s1_s.length == 4 && s2_s.length == 3)
				return 1;
			else if(s1_s.length == 4 && s2_s.length == 4)
				return s1_s[3].compareTo(s2_s[3]);
			
			return s1.compareTo(s2);
		}
	}
}