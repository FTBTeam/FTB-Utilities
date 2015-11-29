package latmod.ftbu.api.guide;

import java.io.File;
import java.util.Set;

import cpw.mods.fml.relauncher.ReflectionHelper;
import ftb.lib.FTBLib;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.client.gui.guide.GuideLinkSerializer;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.util.*;

@SuppressWarnings("all")
public class ServerGuideFile extends GuideFile
{
	public static class CachedInfo
	{
		public static final GuideCategory main = new GuideCategory(new ChatComponentTranslation(FTBUFinals.ASSETS + "button.server_info"));
		public static GuideCategory categoryServer, categoryServerAdmin;
		public static final FastMap<String, GuideLink> links = new FastMap<String, GuideLink>();
		public static final FastList<ICommand> commands = new FastList<ICommand>();
		
		public static void reload()
		{
			main.clear();
			
			categoryServerAdmin = new GuideCategory(new ChatComponentText("Admin"));
			categoryServerAdmin.setParent(main);
			
			//categoryServer.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldIDS()));
			
			File file = new File(FTBLib.folderLocal, "guide/");
			if(file.exists() && file.isDirectory())
			{
				File[] f = file.listFiles();
				if(f != null && f.length > 0)
					for(int i = 0; i < f.length; i++)
						loadFromFiles(main, f[i]);
			}
			
			file = new File(FTBLib.folderLocal, "guide_cover.txt");
			if(file.exists() && file.isFile())
			{
				try
				{
					String text = LMFileUtils.loadAsText(file);
					main.println(text);
				}
				catch(Exception ex)
				{ ex.printStackTrace(); }
			}
			
			file = new File(FTBLib.folderLocal, "guide_links.json");
			links.clear();
			LinksMap linksMap = LMJsonUtils.fromJsonFile(GuideLinkSerializer.gson, LMFileUtils.newFile(file), LinksMap.class);
			if(linksMap != null && linksMap.links != null) links.putAll(linksMap.links);
			
			GuideCategory list = categoryServerAdmin.getSub(new ChatComponentText("Entities"));
			
			FastMap<String, Class<?>> map = new FastMap<String, Class<?>>();
			map.putAll(EntityList.stringToClassMapping);
			map.sortFromKeyStrings(true);
			
			Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
			for(Integer i : entityIDset)
				list.println("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
			
			list = categoryServerAdmin.getSub(new ChatComponentText("Enchantments"));
			
			IntList freeIDs = new IntList();
			
			for(int i = 0; i < Enchantment.enchantmentsList.length; i++)
			{
				if(Enchantment.enchantmentsList[i] == null) freeIDs.add(i);
				else list.println("[" + i + "] " + Enchantment.enchantmentsList[i].getTranslatedName(1));
			}
			
			list.println("Empty IDs: " + freeIDs.toString());
			
			commands.clear();
			
			ICommandManager icm = FTBLib.getServer().getCommandManager();
			if(icm != null && icm instanceof CommandHandler)
			{
				try
				{
					Set set = ReflectionHelper.getPrivateValue(CommandHandler.class, (CommandHandler)icm, "commandSet", "field_71561_b");
					for(Object o : set) commands.add((ICommand)o);
				}
				catch(Exception ex)
				{ ex.printStackTrace(); }
			}
			
			main.cleanup();
		}
	}
	
	private FastList<LMPlayerServer> players = null;
	private LMPlayerServer self;
	private GuideCategory categoryTops = null;
	
	public ServerGuideFile(LMPlayerServer pself)
	{
		super(CachedInfo.main.getTitleComponent());
		
		if((self = pself) == null) return;
		boolean isDedi = FTBLib.getServer().isDedicatedServer();
		boolean isOP = !isDedi || self.isOP();
		
		main.copyFrom(CachedInfo.main);
		links.putAll(CachedInfo.links);
		
		categoryTops = main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "top.title"));
		
		players = LMWorldServer.inst.getServerPlayers();
		for(int i = 0; i < players.size(); i++)
			players.get(i).refreshStats();
		
		if(FTBUConfigGeneral.restartTimer.get() > 0F)
			main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_restart", LMStringUtils.getTimeString(FTBUTicks.getSecondsUntilRestart() * 1000L)));
		
		if(FTBUConfigBackups.enabled.get())
			main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_backup", LMStringUtils.getTimeString(Backups.getSecondsUntilNextBackup() * 1000L)));
		
		if(FTBUConfigTops.first_joined.get()) addTop(Top.first_joined);
		if(FTBUConfigTops.deaths.get()) addTop(Top.deaths);
		if(FTBUConfigTops.deaths_ph.get()) addTop(Top.deaths_ph);
		if(FTBUConfigTops.last_seen.get()) addTop(Top.last_seen);
		if(FTBUConfigTops.time_played.get()) addTop(Top.time_played);
		
		new EventFTBUServerGuide(this, self, isOP).post();
		categoryTops.subcategories.sort(null);
		if(isOP) main.addSub(CachedInfo.categoryServerAdmin);
		
		GuideCategory commands = main.getSub(new ChatComponentText("Commands"));
		commands.clear();
		
		for(ICommand c : CachedInfo.commands)
		{
			if(c.canCommandSenderUseCommand(self.getPlayer()))
			{
				GuideCategory cat = commands.getSub(new ChatComponentText('/' + c.getCommandName()));
				String usage = c.getCommandUsage(self.getPlayer());
				
				if(usage != null)
				{
					if(usage.indexOf('\n') != -1)
					{
						String[] usageL = usage.split("\n");
						for(String s1 : usageL)
							cat.println(s1);
					}
					else cat.println(new ChatComponentTranslation(usage));
				}
			}
		}
		
		commands.subcategories.sort(null);
		
		main.cleanup();
		main.subcategories.sort(null);
	}
	
	public void addTop(Top t)
	{
		GuideCategory thisTop = categoryTops.getSub(t.ID);
		
		players.sort(t);
		
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
			sb.append(p.getName());
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