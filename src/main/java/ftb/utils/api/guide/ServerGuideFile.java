package ftb.utils.api.guide;

import ftb.lib.*;
import ftb.lib.api.cmd.CommandLM;
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

public class ServerGuideFile extends GuideFile
{
	public static class CachedInfo
	{
		public static final GuideCategory main = new GuideCategory(new ChatComponentTranslation("player_action.ftbu.server_info"));
		public static GuideCategory categoryServer, categoryServerAdmin;
		public static final HashMap<String, GuideLink> links = new HashMap<>();
		
		public static void reload()
		{
			main.clear();
			
			categoryServerAdmin = new GuideCategory(new ChatComponentText("Admin"));
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
					if(text != null && !text.isEmpty()) main.println(text.replace("\r", ""));
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			
			links.clear();
			links.putAll(loadLinksFromFile(new File(FTBLib.folderLocal, "guide_links.json")));
			
			reloadRegistries();
			
			main.cleanup();
		}
		
		@SuppressWarnings("all")
		public static void reloadRegistries()
		{
			GuideCategory list = categoryServerAdmin.getSub(new ChatComponentText("Entities"));
			
			Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
			for(Integer i : entityIDset)
				list.println("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
			
			list = categoryServerAdmin.getSub(new ChatComponentText("Enchantments"));
			
			IntList freeIDs = new IntList();
			
			for(int i = 0; i < 256; i++)
			{
				Enchantment e = Enchantment.enchantmentsList[i];
				if(e == null) freeIDs.add(i);
				else list.println("[" + i + "] " + e.getTranslatedName(1));
			}
			
			list.println("Empty IDs: " + freeIDs.toString());
		}
	}
	
	private List<LMPlayerServer> players = null;
	private LMPlayerServer self;
	private GuideCategory categoryTops = null;
	
	public ServerGuideFile(LMPlayerServer pself)
	{
		super(CachedInfo.main.getTitleComponent());
		
		if((self = pself) == null) return;
		boolean isDedi = FTBLib.getServer().isDedicatedServer();
		boolean isOP = !isDedi || self.getRank().config.admin_server_info.get();
		
		main.copyFrom(CachedInfo.main);
		links.putAll(CachedInfo.links);
		
		categoryTops = main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "top.title"));
		
		players = LMWorldServer.inst.getServerPlayers();
		for(int i = 0; i < players.size(); i++)
			players.get(i).refreshStats();
		
		if(FTBUConfigGeneral.restart_timer.get() > 0F)
			main.println(FTBU.mod.chatComponent("cmd.timer_restart", LMStringUtils.getTimeString(FTBUTicks.restartMillis - LMUtils.millis())));
		
		if(FTBUConfigBackups.enabled.get())
			main.println(FTBU.mod.chatComponent("cmd.timer_backup", LMStringUtils.getTimeString(Backups.nextBackup - LMUtils.millis())));
		
		if(FTBUConfigGeneral.server_info_difficulty.get())
			main.println(FTBU.mod.chatComponent("cmd.world_difficulty", LMStringUtils.firstUppercase(pself.getPlayer().worldObj.difficultySetting.toString().toLowerCase())));
		
		if(FTBUConfigGeneral.server_info_mode.get())
			main.println(FTBU.mod.chatComponent("cmd.ftb_gamemode", LMStringUtils.firstUppercase(FTBWorld.server.getMode().toString().toLowerCase())));
		
		if(FTBUConfigTops.first_joined.get()) addTop(Top.first_joined);
		if(FTBUConfigTops.deaths.get()) addTop(Top.deaths);
		if(FTBUConfigTops.deaths_ph.get()) addTop(Top.deaths_ph);
		if(FTBUConfigTops.last_seen.get()) addTop(Top.last_seen);
		if(FTBUConfigTops.time_played.get()) addTop(Top.time_played);
		
		new EventFTBUServerGuide(this, self, isOP).post();
		Collections.sort(categoryTops.subcategories, null);
		if(isOP) main.addSub(CachedInfo.categoryServerAdmin);
		
		GuideCategory commands = main.getSub(new ChatComponentText("Commands"));
		commands.clear();
		
		CommandLM.extendedUsageInfo = true;
		
		try
		{
			for(ICommand c : FTBLib.getAllCommands(self.getPlayer()))
			{
				try
				{
					GuideCategory cat = new GuideCategory(new ChatComponentText('/' + c.getCommandName()));
					
					@SuppressWarnings("unchecked") List<String> al = c.getCommandAliases();
					if(al != null && !al.isEmpty()) for(String s : al)
						cat.println('/' + s);
					
					String usage = c.getCommandUsage(self.getPlayer());
					
					if(usage != null)
					{
						if(usage.indexOf('\n') != -1)
						{
							String[] usageL = usage.split("\n");
							for(String s1 : usageL)
								cat.println(s1);
						}
						else
						{
							if(usage.indexOf('%') != -1 || usage.indexOf('/') != -1)
								cat.println(new ChatComponentText(usage));
							else cat.println(new ChatComponentTranslation(usage));
						}
					}
					
					cat.setParent(commands);
					commands.addSub(cat);
				}
				catch(Exception ex1)
				{
					IChatComponent cc = new ChatComponentText('/' + c.getCommandName());
					cc.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
					commands.getSub(cc).println("Errored");
				}
			}
		}
		catch(Exception ex) { }
		
		CommandLM.extendedUsageInfo = false;
		Collections.sort(commands.subcategories, null);
		
		main.cleanup();
		Collections.sort(main.subcategories, null);
	}
	
	public void addTop(Top t)
	{
		GuideCategory thisTop = categoryTops.getSub(t.ID);
		
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