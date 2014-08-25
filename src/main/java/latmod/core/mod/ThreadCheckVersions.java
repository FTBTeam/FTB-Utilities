package latmod.core.mod;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import latmod.core.LatCoreMC;
import latmod.core.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.*;

import com.google.gson.annotations.Expose;

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
		
		if(LC.MOD_VERSION.equals("@VERSION@")) failed = 3;
		
		VersionsFile file = null;
		
		try
		{
			failed = 1;
			
			InputStream is = new URL("http://pastebin.com/raw.php?i=iR88TgSZ").openStream();
			byte[] b = new byte[is.available()];
			is.read(b);
			String s = new String(b);
			
			failed = 2;
			
			if(s.length() > 0)
			{
				file = LatCoreMC.fromJson(s, LatCoreMC.getMapType(String.class, LatCoreMC.getMapType(String.class, String.class)));
				failed = 0;
				
				if(file.latestVersion == null || file.latestChanges == null)
					failed = 2;
			}
		}
		catch(Exception ex)
		{ file = null; }
		
		if(output != null)
		{
			if(failed == 1)
			{
				if(chatCommand) LatCoreMC.printChat(output, "Failed to check versions!");
			}
			else if(failed == 2)
			{
				if(chatCommand) LatCoreMC.printChat(output, "Invalid versions file!");	
			}
			else if(failed == 3)
			{
				if(chatCommand) LatCoreMC.printChat(output, "You are in a development environment!");
			}
			else
			{
				int thisBuild = Integer.parseInt(LC.MOD_VERSION);
				
				if(thisBuild != file.latestVersion)
				{
					if(!LC.modsToCheck.isEmpty() && !file.latestChanges.isEmpty())
					{
						FastList<IChatComponent> toPrint = new FastList<IChatComponent>();
						
						FastMap<String, String> map = new FastMap<String, String>();
						
						for(int i = 0; i < map.size(); i++)
						{
							String mod_id = map.keys.get(i);
							
							if(LC.modsToCheck.contains(mod_id))
							{
								if(toPrint.isEmpty()) toPrint.add(new ChatComponentText("These LatvianModder's mods has updates: [#" + file.latestVersion + "]"));
								
								IChatComponent link = new ChatComponentText(mod_id);
								link.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Download")));
								link.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/LatvianModder/Files/tree/Mods/" + mod_id + "/" + LatCoreMC.MC_VERSION));
								link.getChatStyle().setColor(EnumChatFormatting.GOLD);
								
								String changes = map.values.get(i);
								
								if(!changes.equals("-"))
									link.appendSibling(new ChatComponentText(": " + changes));
								
								toPrint.add(link);
							}
						}
						
						if(!toPrint.isEmpty()) for(IChatComponent s : toPrint)
							output.addChatMessage(s);
					}
				}
				else if(chatCommand)
					LatCoreMC.printChat(output, "Everyting is up to date");
			}
		}
		
		thread = null;
	}
	
	public static class VersionsFile
	{
		@Expose public Integer latestVersion;
		@Expose public Map<String, String> latestChanges;
	}
}