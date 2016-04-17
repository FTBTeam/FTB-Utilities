package ftb.utils.api.guide;

import com.google.gson.JsonPrimitive;
import ftb.lib.FTBLib;
import ftb.lib.api.*;
import ftb.lib.api.cmd.ICustomCommandInfo;
import ftb.lib.api.info.*;
import ftb.lib.api.notification.*;
import ftb.lib.api.permissions.ForgePermissionRegistry;
import ftb.utils.*;
import ftb.utils.config.*;
import ftb.utils.world.*;
import latmod.lib.*;
import net.minecraft.command.ICommand;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.util.text.*;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.*;

public class ServerGuideFile extends InfoPage
{
	public static class CachedInfo
	{
		public static final InfoPage main = new InfoPage("ServerInfo").setTitle(new TextComponentTranslation("player_action.ftbu.server_info"));
		public static InfoPage categoryServer, categoryServerAdmin;
		
		public static void reload()
		{
			main.clear();
			
			categoryServerAdmin = new InfoPage("Admin"); //LANG
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
		
		public static void reloadRegistries()
		{
			InfoPage list = categoryServerAdmin.getSub("Entities"); //LANG
			
			for(String s : EntityList.stringToClassMapping.keySet())
				list.printlnText("[" + EntityList.getIDFromString(s) + "] " + s);
			
			list = categoryServerAdmin.getSub("Enchantments"); //LANG
			
			for(Enchantment e : Enchantment.enchantmentRegistry)
			{
				list.printlnText("[" + e.getRegistryName() + "] " + e.getTranslatedName(1));
			}
		}
	}
	
	private List<ForgePlayerMP> players = null;
	private ForgePlayerMP self;
	private InfoPage categoryTops = null;
	
	public ServerGuideFile(ForgePlayerMP pself)
	{
		super(CachedInfo.main.getID());
		setTitle(CachedInfo.main.getTitleComponent());
		
		if((self = pself) == null) return;
		boolean isDedi = FTBLib.getServer().isDedicatedServer();
		boolean isOP = !isDedi || ForgePermissionRegistry.hasPermission(FTBUPermissions.display_admin_info, self.getProfile());
		
		copyFrom(CachedInfo.main);
		
		categoryTops = getSub("Tops").setTitle(FTBU.mod.chatComponent("top.title"));
		
		players = ForgeWorldMP.inst.getServerPlayers();
		
		for(ForgePlayerMP p : players)
			p.refreshStats();
		
		if(FTBUConfigGeneral.restart_timer.getAsDouble() > 0D)
			println(FTBU.mod.chatComponent("cmd.timer_restart", LMStringUtils.getTimeString(FTBUWorldDataMP.get().restartMillis - LMUtils.millis())));
		
		if(FTBUConfigBackups.enabled.getAsBoolean())
			println(FTBU.mod.chatComponent("cmd.timer_backup", LMStringUtils.getTimeString(Backups.nextBackup - LMUtils.millis())));
		
		if(FTBUConfigGeneral.server_info_difficulty.getAsBoolean())
			println(FTBU.mod.chatComponent("cmd.world_difficulty", LMStringUtils.firstUppercase(pself.getPlayer().worldObj.getDifficulty().toString().toLowerCase())));
		
		if(FTBUConfigGeneral.server_info_mode.getAsBoolean())
			println(FTBU.mod.chatComponent("cmd.ftb_gamemode", LMStringUtils.firstUppercase(ForgeWorldMP.inst.getMode().toString().toLowerCase())));
		
		if(FTBUConfigTops.first_joined.getAsBoolean()) addTop(Top.first_joined);
		if(FTBUConfigTops.deaths.getAsBoolean()) addTop(Top.deaths);
		if(FTBUConfigTops.deaths_ph.getAsBoolean()) addTop(Top.deaths_ph);
		if(FTBUConfigTops.last_seen.getAsBoolean()) addTop(Top.last_seen);
		if(FTBUConfigTops.time_played.getAsBoolean()) addTop(Top.time_played);
		
		MinecraftForge.EVENT_BUS.post(new EventFTBUServerGuide(this, self, isOP));
		if(isOP) addSub(CachedInfo.categoryServerAdmin);
		
		InfoPage page = getSub("Commands"); //LANG
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
						cat.printlnText('/' + s);
					
					if(c instanceof ICustomCommandInfo)
					{
						List<ITextComponent> list = new ArrayList<>();
						((ICustomCommandInfo) c).addInfo(list, self.getPlayer());
						
						for(ITextComponent c1 : list)
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
									cat.println(new TextComponentString(usage));
								else cat.println(new TextComponentTranslation(usage));
							}
						}
					}
					
					cat.setParent(page);
					page.addSub(cat);
				}
				catch(Exception ex1)
				{
					ITextComponent cc = new TextComponentString('/' + c.getCommandName());
					cc.getChatStyle().setColor(TextFormatting.DARK_RED);
					page.getSub('/' + c.getCommandName()).setTitle(cc).printlnText("Errored");
					
					if(FTBLib.DEV_ENV) ex1.printStackTrace();
				}
			}
		}
		catch(Exception ex) { }
		
		page = getSub("Warps"); //LANG
		InfoExtendedTextLine line;
		
		for(String s : FTBUWorldDataMP.get().warps.list())
		{
			line = new InfoExtendedTextLine(page, new TextComponentString(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("warp " + s)));
			page.text.add(line);
		}
		
		page = getSub("Homes"); //LANG
		
		for(String s : FTBUPlayerDataMP.get(self).homes.list())
		{
			line = new InfoExtendedTextLine(page, new TextComponentString(s));
			line.setClickAction(new ClickAction(ClickActionType.CMD, new JsonPrimitive("home " + s)));
			page.text.add(line);
		}
		
		cleanup();
		sortAll();
	}
	
	public void addTop(Top t)
	{
		InfoPage thisTop = categoryTops.getSub(t.ID).setTitle(t.title);
		
		Collections.sort(players, t);
		
		int size = Math.min(players.size(), 250);
		
		for(int j = 0; j < size; j++)
		{
			ForgePlayerMP p = players.get(j);
			
			Object data = t.getData(p);
			StringBuilder sb = new StringBuilder();
			sb.append('[');
			sb.append(j + 1);
			sb.append(']');
			sb.append(' ');
			sb.append(p.getProfile().getName());
			sb.append(':');
			sb.append(' ');
			if(!(data instanceof ITextComponent)) sb.append(data);
			
			ITextComponent c = new TextComponentString(sb.toString());
			if(p == self) c.getChatStyle().setColor(TextFormatting.DARK_GREEN);
			else if(j < 3) c.getChatStyle().setColor(TextFormatting.LIGHT_PURPLE);
			if(data instanceof ITextComponent) c.appendSibling(FTBLib.getChatComponent(data));
			thisTop.println(c);
		}
	}
}