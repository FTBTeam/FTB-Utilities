package latmod.latcore;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import latmod.core.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.*;
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
				file = LatCore.fromJson(s, VersionsFile.class);
				failed = 0;
				
				if(file.latestVersion == null || file.latestChanges == null)
					failed = 2;
			}
		}
		catch(Exception ex) { file = null; if(LatCoreMC.isDevEnv) ex.printStackTrace(); }
		
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
			else
			{
				String thisBuild = "" + (LatCoreMC.isDevEnv ? "Development" : LC.VERSION);
				
				if(!thisBuild.equals(file.latestVersion))
				{
					FastList<IChatComponent> toPrint = new FastList<IChatComponent>();
					
					IChatComponent txt = new ChatComponentText("LatvianModder's mods updated ");
					
					IChatComponent dlink = new ChatComponentText("[Download #" + file.latestVersion + "]");
					dlink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Download")));
					dlink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://minecraft.curseforge.com/mc-mods/224778-latcoremc"));
					dlink.getChatStyle().setColor(EnumChatFormatting.GOLD);
					
					toPrint.add(txt.appendSibling(dlink));
					toPrint.add(new ChatComponentText("Current version: " + thisBuild));
					
					
					if(!LatCoreMC.isDevEnv && !file.latestChanges.isEmpty()) for(String s : file.latestChanges)
						toPrint.add(new ChatComponentText(s));
					
					if(!toPrint.isEmpty()) for(IChatComponent s : toPrint)
						output.addChatMessage(s);
				}
				else if(chatCommand) LatCoreMC.printChat(output, "Everyting is up to date");
			}
		}
		
		thread = null;
	}
	
	public static class VersionsFile
	{
		@Expose public String latestVersion;
		@Expose public List<String> latestChanges;
	}
}