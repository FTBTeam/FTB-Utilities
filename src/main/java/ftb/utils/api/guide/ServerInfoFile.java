package ftb.utils.api.guide;

import com.google.gson.JsonPrimitive;
import ftb.lib.FTBLib;
import ftb.lib.FTBWorld;
import ftb.lib.api.cmd.ICustomCommandInfo;
import ftb.lib.api.info.InfoPage;
import ftb.lib.api.info.lines.InfoExtendedTextLine;
import ftb.lib.api.notification.ClickAction;
import ftb.lib.api.notification.ClickActionType;
import ftb.lib.mod.FTBLibLang;
import ftb.utils.mod.FTBULang;
import ftb.utils.mod.FTBUTicks;
import ftb.utils.mod.config.FTBUConfigBackups;
import ftb.utils.mod.config.FTBUConfigGeneral;
import ftb.utils.mod.config.FTBUConfigLogin;
import ftb.utils.world.Backups;
import ftb.utils.world.LMPlayerServer;
import ftb.utils.world.LMWorldServer;
import latmod.lib.LMFileUtils;
import latmod.lib.LMStringUtils;
import latmod.lib.LMUtils;
import net.minecraft.command.ICommand;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServerInfoFile extends InfoPage
{
	public static class CachedInfo
	{
		public static final InfoPage main = new InfoPage("server_info").setTitle(new ChatComponentTranslation("player_action.ftbu.server_info"));
		public static InfoPage categoryServer;
		
		public static void reload()
		{
			main.clear();
			
			File file = new File(FTBLib.folderLocal, "guide/");
			if(file.exists() && file.isDirectory())
			{
				File[] f = file.listFiles();
				if(f != null && f.length > 0)
				{
					Arrays.sort(f, LMFileUtils.fileComparator);
					for(int i = 0; i < f.length; i++)
						loadFromFiles(main, f[i]);
				}
			}
			
			file = new File(FTBLib.folderLocal, "guide_cover.txt");
			if(file.exists() && file.isFile())
			{
				try
				{
					String text = LMFileUtils.loadAsText(file);
					if(text != null && !text.isEmpty()) main.printlnText(text.replace("\r", ""));
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
			main.cleanup();
		}
	}
	
	public static void loadFromFiles(InfoPage c, File f)
	{
		if(f == null || !f.exists()) return;
		
		if(f.isDirectory())
		{
			File[] f1 = f.listFiles();
			
			if(f1 != null && f1.length > 0)
			{
				Arrays.sort(f1, LMFileUtils.fileComparator);
				InfoPage c1 = c.getSub(f.getName());
				for(File f2 : f1) loadFromFiles(c1, f2);
			}
		}
		else if(f.isFile())
		{
			if(f.getName().endsWith(".txt"))
			{
				try
				{
					InfoPage c1 = c.getSub(LMFileUtils.getRawFileName(f));
					c1.loadText(LMFileUtils.load(f));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<LMPlayerServer> players = null;
	private LMPlayerServer self;
	private InfoPage categoryTops = null;
	
	public ServerInfoFile(LMPlayerServer pself)
	{
		super(CachedInfo.main.getID());
		setTitle(CachedInfo.main.getTitleComponent());
		useUnicodeFont = Boolean.FALSE;
		
		if((self = pself) == null) return;
		boolean isDedi = FTBLib.getServer().isDedicatedServer();
		
		if(!FTBUConfigLogin.motd.components.isEmpty())
		{
			for(IChatComponent c : FTBUConfigLogin.motd.components)
			{
				println(c);
			}
			
			text.add(null);
		}
		
		copyFrom(CachedInfo.main);
		
		categoryTops = getSub("tops").setTitle(Top.langTopTitle.chatComponent());
		
		players = new ArrayList<>();
		players.addAll(LMWorldServer.inst.playerMap.values());
		
		for(LMPlayerServer p : players)
		{
			p.refreshStats();
		}
		
		if(FTBUConfigGeneral.restart_timer.getAsDouble() > 0D)
			println(FTBULang.timer_restart.chatComponent(LMStringUtils.getTimeString(FTBUTicks.restartMillis - LMUtils.millis())));
		
		if(FTBUConfigBackups.enabled.getAsBoolean())
			println(FTBULang.timer_backup.chatComponent(LMStringUtils.getTimeString(Backups.nextBackup - LMUtils.millis())));
		
		if(FTBUConfigGeneral.server_info_difficulty.getAsBoolean())
			println(FTBLibLang.difficulty.chatComponent(LMStringUtils.firstUppercase(pself.getPlayer().worldObj.difficultySetting.toString().toLowerCase())));
		
		if(FTBUConfigGeneral.server_info_mode.getAsBoolean())
			println(FTBLibLang.mode_current.chatComponent(LMStringUtils.firstUppercase(FTBWorld.server.getMode().toString().toLowerCase())));
		
		List<String> blocked_tops = FTBUConfigGeneral.blocked_tops.getAsStringList();
		
		for(Top t : Top.registry.values())
		{
			if(!blocked_tops.contains(t.getID()))
			{
				InfoPage thisTop = categoryTops.getSub(t.getID()).setTitle(t.langKey.chatComponent());
				
				Collections.sort(players, t);
				
				int size = Math.min(players.size(), 250);
				
				for(int j = 0; j < size; j++)
				{
					LMPlayerServer p = players.get(j);
					
					Object data = t.getData(p);
					StringBuilder sb = new StringBuilder();
					sb.append('[');
					sb.append(j + 1);
					sb.append(']');
					sb.append(' ');
					sb.append(p.getProfile().getName());
					sb.append(':');
					sb.append(' ');
					if(!(data instanceof IChatComponent)) sb.append(data);
					
					IChatComponent c = new ChatComponentText(sb.toString());
					if(p == self) c.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
					else if(j < 3) c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
					if(data instanceof IChatComponent) c.appendSibling(FTBLib.getChatComponent(data));
					thisTop.println(c);
				}
			}
		}
		
		new EventFTBUServerGuide(this, self).post();
		
		InfoPage page = getSub("commands").setTitle(new ChatComponentText("Commands")); //TODO: Lang
		page.clear();
		
		try
		{
			for(ICommand c : FTBLib.getAllCommands(self.getPlayer()))
			{
				try
				{
					InfoPage cat = new InfoPage('/' + c.getCommandName());
					
					@SuppressWarnings("unchecked") List<String> al = c.getCommandAliases();
					if(al != null && !al.isEmpty()) for(String s : al)
					{
						cat.printlnText('/' + s);
					}
					
					if(c instanceof ICustomCommandInfo)
					{
						List<IChatComponent> list = new ArrayList<>();
						((ICustomCommandInfo) c).addInfo(list, self.getPlayer());
						
						for(IChatComponent c1 : list)
						{
							cat.println(c1);
						}
					}
					else
					{
						String usage = c.getCommandUsage(self.getPlayer());
						
						if(usage != null)
						{
							if(usage.indexOf('\n') != -1)
							{
								String[] usageL = usage.split("\n");
								for(String s1 : usageL)
									cat.printlnText(s1);
							}
							else
							{
								if(usage.indexOf('%') != -1 || usage.indexOf('/') != -1)
									cat.println(new ChatComponentText(usage));
								else cat.println(new ChatComponentTranslation(usage));
							}
						}
					}
					
					cat.setParent(page);
					page.addSub(cat);
				}
				catch(Exception ex1)
				{
					IChatComponent cc = new ChatComponentText('/' + c.getCommandName());
					cc.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
					page.getSub('/' + c.getCommandName()).setTitle(cc).printlnText("Errored");
					
					if(FTBLib.DEV_ENV) ex1.printStackTrace();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		page = getSub("warps").setTitle(new ChatComponentText("Warps")); //TODO: Lang
		InfoExtendedTextLine line;
		
		for(String s : LMWorldServer.inst.warps.list())
		{
			line = new InfoExtendedTextLine(page, new ChatComponentText(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("warp " + s)));
			page.text.add(line);
		}
		
		page = getSub("homes").setTitle(new ChatComponentText("Homes")); //TODO: Lang
		
		for(String s : self.homes.list())
		{
			line = new InfoExtendedTextLine(page, new ChatComponentText(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("home " + s)));
			page.text.add(line);
		}
		
		cleanup();
		sortAll();
	}
}