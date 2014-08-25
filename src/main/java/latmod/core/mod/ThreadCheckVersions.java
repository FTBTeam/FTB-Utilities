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
				file = LatCoreMC.fromJson(s, VersionsFile.class);
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
				int thisBuild = LatCoreMC.isDevEnv ? -1 : Integer.parseInt(LC.MOD_VERSION);
				
				if(thisBuild != file.latestVersion)
				{
					FastList<IChatComponent> toPrint = new FastList<IChatComponent>();
					
					if(LatCoreMC.isDevEnv)
						toPrint.add(new ChatComponentText("You are in a development environment!"));
					IChatComponent txt = new ChatComponentText("LatvianModder's mods updated ");
					
					IChatComponent dlink = new ChatComponentText("[Download #" + file.latestVersion + "]");
					dlink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Download")));
					dlink.getChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://github.com/LatvianModder/Files/tree/Mods/" + LatCoreMC.MC_VERSION));
					dlink.getChatStyle().setColor(EnumChatFormatting.GOLD);
					
					toPrint.add(txt.appendSibling(dlink));
					
					if(!file.latestChanges.isEmpty()) for(String s : file.latestChanges)
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
		@Expose public Integer latestVersion;
		@Expose public List<String> latestChanges;
	}
}