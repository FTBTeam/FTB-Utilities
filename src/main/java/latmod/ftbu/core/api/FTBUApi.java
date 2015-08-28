package latmod.ftbu.core.api;

import java.io.File;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.LMFileUtils;
import latmod.ftbu.mod.FTBU;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import com.google.gson.GsonBuilder;

import cpw.mods.fml.relauncher.Side;

public class FTBUApi
{
	public static final BasicRegistry<IFTBUReloadable> reloadableRegistry = new BasicRegistry<IFTBUReloadable>(IFTBUReloadable.class);
	public static final BasicRegistry<IFTBUGsonProvider> gsonProviderRegistry = new BasicRegistry<IFTBUGsonProvider>(IFTBUGsonProvider.class);
	public static final BasicRegistry<ICustomActionFromClient> clientHandlers = new BasicRegistry<ICustomActionFromClient>(ICustomActionFromClient.class);
	public static final BasicRegistry<ICustomActionFromServer> serverHandlers = new BasicRegistry<ICustomActionFromServer>(ICustomActionFromServer.class);
	public static final BasicRegistry<IReadmeProvider> readmeRegistry = new BasicRegistry<IReadmeProvider>(IReadmeProvider.class);
	
	public static void add(Object o)
	{
		if(o == null) return;
		reloadableRegistry.add(o);
		gsonProviderRegistry.add(o);
		clientHandlers.add(o);
		serverHandlers.add(o);
		readmeRegistry.add(o);
	}
	
	public static void remove(Object o)
	{
		if(o == null) return;
		reloadableRegistry.remove(o);
		gsonProviderRegistry.remove(o);
		clientHandlers.remove(o);
		serverHandlers.remove(o);
		readmeRegistry.remove(o);
	}
	
	// Functions //
	
	public static void reload(Side s, ICommandSender sender)
	{
		for(IFTBUReloadable i : reloadableRegistry)
		{
			try { i.onReloaded(s, sender); } catch(Exception e)
			{ sender.addChatMessage(new ChatComponentText("Error @ " + e.toString())); }
		}
	}
	
	public static void addGsonHandlers(GsonBuilder builder)
	{
		for(IFTBUGsonProvider i : gsonProviderRegistry)
			i.addGsonHandlers(builder);
	}
	
	public static final ICustomActionFromClient getCustomActionClientHandler(String s)
	{
		if(s == null || s.isEmpty()) return null;
		for(ICustomActionFromClient c : clientHandlers)
			if(c.getActionHandlerID().equals(s))
				return c;
		return null;
	}
	
	public static final ICustomActionFromServer getCustomActionServerHandler(String s)
	{
		if(s == null || s.isEmpty()) return null;
		for(ICustomActionFromServer c : serverHandlers)
			if(c.getActionHandlerID().equals(s))
				return c;
		return null;
	}
	
	public static void saveReadme() throws Exception
	{
		ReadmeFile file = new ReadmeFile();
		FTBUConfig.saveReadme(file);
		FTBU.proxy.onReadmeEvent(file);
		
		for(IReadmeProvider p : readmeRegistry)
			p.saveReadme(file);
		
		StringBuilder sb = new StringBuilder();
		
		for(int j = 0; j < file.map.size(); j++)
		{
			ReadmeCategory c = file.map.values.get(j);
			
			sb.append('[');
			sb.append(c.name);
			sb.append(']');
			sb.append('\n');
			
			for(int i = 0; i < c.lines.size(); i++)
			{
				String k = c.lines.keys.get(i); 
				
				if(!k.isEmpty())
				{
					sb.append(k);
					sb.append(" - ");
				}
				
				sb.append(c.lines.values.get(i));
				sb.append('\n');
			}
			
			sb.append('\n');
		}
		
		LMFileUtils.save(new File(LatCoreMC.latmodFolder, "readme.txt"), sb.toString().trim());
	}
}