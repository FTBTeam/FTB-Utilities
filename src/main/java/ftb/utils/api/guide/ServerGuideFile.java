package ftb.utils.api.guide;

import com.google.gson.JsonPrimitive;
import ftb.lib.*;
import ftb.lib.api.cmd.ICustomCommandInfo;
import ftb.lib.api.notification.*;
import ftb.utils.api.guide.lines.GuideExtendedTextLine;
import ftb.utils.mod.*;
import ftb.utils.mod.config.*;
import ftb.utils.world.*;
import latmod.lib.*;
import net.minecraft.command.ICommand;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.util.*;

import java.io.File;
import java.util.*;

public class ServerGuideFile extends GuidePage
{
	public static class CachedInfo
	{
		public static final GuidePage main = new GuidePage("ServerInfo").setTitle(new ChatComponentTranslation("player_action.ftbu.server_info"));
		public static GuidePage categoryServer, categoryServerAdmin;
		
		public static void reload()
		{
			main.clear();
			
			categoryServerAdmin = new GuidePage("Admin"); //LANG
			categoryServerAdmin.setParent(main);
			
			//categoryServer.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldID()));
			
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
			
			reloadRegistries();
			
			main.cleanup();
		}
		
		@SuppressWarnings("all")
		public static void reloadRegistries()
		{
			GuidePage list = categoryServerAdmin.getSub("Entities"); //LANG
			
			Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
			for(Integer i : entityIDset)
				list.printlnText("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
			
			list = categoryServerAdmin.getSub("Enchantments"); //LANG
			
			IntList freeIDs = new IntList();
			
			for(int i = 0; i < 256; i++)
			{
				Enchantment e = Enchantment.enchantmentsList[i];
				if(e == null) freeIDs.add(i);
				else list.printlnText("[" + i + "] " + e.getTranslatedName(1));
			}
			
			list.printlnText("Empty IDs: " + freeIDs.toString());
		}
	}
	
	private List<LMPlayerServer> players = null;
	private LMPlayerServer self;
	private GuidePage categoryTops = null;
	
	public ServerGuideFile(LMPlayerServer pself)
	{
		super(CachedInfo.main.getID());
		setTitle(CachedInfo.main.getTitleComponent());
		
		if((self = pself) == null) return;
		boolean isDedi = FTBLib.getServer().isDedicatedServer();
		boolean isOP = !isDedi || self.getRank().config.admin_server_info.get();
		
		copyFrom(CachedInfo.main);
		
		categoryTops = getSub("Tops").setTitle(FTBU.mod.chatComponent("top.title"));
		
		players = LMWorldServer.inst.getServerPlayers();
		for(int i = 0; i < players.size(); i++)
			players.get(i).refreshStats();
		
		if(FTBUConfigGeneral.restart_timer.get() > 0F)
			println(FTBU.mod.chatComponent("cmd.timer_restart", LMStringUtils.getTimeString(FTBUTicks.restartMillis - LMUtils.millis())));
		
		if(FTBUConfigBackups.enabled.get())
			println(FTBU.mod.chatComponent("cmd.timer_backup", LMStringUtils.getTimeString(Backups.nextBackup - LMUtils.millis())));
		
		if(FTBUConfigGeneral.server_info_difficulty.get())
			println(FTBU.mod.chatComponent("cmd.world_difficulty", LMStringUtils.firstUppercase(pself.getPlayer().worldObj.difficultySetting.toString().toLowerCase())));
		
		if(FTBUConfigGeneral.server_info_mode.get())
			println(FTBU.mod.chatComponent("cmd.ftb_gamemode", LMStringUtils.firstUppercase(FTBWorld.server.getMode().toString().toLowerCase())));
		
		if(FTBUConfigTops.first_joined.get()) addTop(Top.first_joined);
		if(FTBUConfigTops.deaths.get()) addTop(Top.deaths);
		if(FTBUConfigTops.deaths_ph.get()) addTop(Top.deaths_ph);
		if(FTBUConfigTops.last_seen.get()) addTop(Top.last_seen);
		if(FTBUConfigTops.time_played.get()) addTop(Top.time_played);
		
		new EventFTBUServerGuide(this, self, isOP).post();
		if(isOP) addSub(CachedInfo.categoryServerAdmin);
		
		GuidePage page = getSub("Commands"); //LANG
		page.clear();
		
		try
		{
			for(ICommand c : FTBLib.getAllCommands(self.getPlayer()))
			{
				try
				{
					GuidePage cat = new GuidePage('/' + c.getCommandName());
					
					@SuppressWarnings("unchecked") List<String> al = c.getCommandAliases();
					if(al != null && !al.isEmpty()) for(String s : al)
						cat.printlnText('/' + s);
					
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
		catch(Exception ex) { }
		
		page = getSub("Warps"); //LANG
		GuideExtendedTextLine line;
		
		for(String s : LMWorldServer.inst.warps.list())
		{
			line = new GuideExtendedTextLine(page, new ChatComponentText(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("warp " + s)));
			page.text.add(line);
		}
		
		page = getSub("Homes"); //LANG
		
		for(String s : self.homes.list())
		{
			line = new GuideExtendedTextLine(page, new ChatComponentText(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("home " + s)));
			page.text.add(line);
		}
		
		cleanup();
		sortAll();
	}
	
	public void addTop(Top t)
	{
		GuidePage thisTop = categoryTops.getSub(t.ID).setTitle(t.title);
		
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