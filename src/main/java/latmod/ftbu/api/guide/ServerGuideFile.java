package latmod.ftbu.api.guide;

import java.lang.reflect.Field;
import java.util.Set;

import ftb.lib.*;
import latmod.ftbu.mod.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.util.*;

public class ServerGuideFile extends GuideFile
{
	public static final ServerGuideFile instance = new ServerGuideFile(new ChatComponentTranslation(FTBUFinals.ASSETS + "button.server_info"));
	
	private FastList<LMPlayerServer> players = null;
	private LMPlayerServer self;
	private boolean isOP;
	private GuideCategory categoryTops = null;
	private GuideCategory categoryOther = null;
	
	public ServerGuideFile(IChatComponent title)
	{ super(title); }
	
	@SuppressWarnings("all")
	public void reload(LMPlayerServer pself)
	{
		if((self = pself) == null) return;
		isOP = self.isOP();
		
		main.clear();
		
		players = LMWorldServer.inst.getServerPlayers();
		for(int i = 0; i < players.size(); i++)
			players.get(i).refreshStats();
		
		categoryTops = main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "top.title"));
		categoryOther = main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "button.other"));
		
		categoryOther.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldIDS()));
		
		for(String s : FTBUConfigLogin.motd.get())
			main.println(s);
		
		if(!FTBUConfigLogin.rules.get().isEmpty())
		{
			IChatComponent c = new ChatComponentText("[rules_link]");
			c.getChatStyle().setColor(EnumChatFormatting.GOLD);
			main.println(c);
			GuideLink l = new GuideLink(GuideLink.TYPE_URL);
			l.link = FTBUConfigLogin.rules.get();
			l.title = new ChatComponentTranslation(FTBU.mod.assets + "cmd.rules");
			links.put("rules_link", l);
		}
		
		if(!FTBUConfigLogin.motd.get().isEmpty() || !FTBUConfigLogin.rules.get().isEmpty())
			main.println("");
		
		if(FTBUConfigGeneral.restartTimer.get() > 0F)
			main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_restart", LMStringUtils.getTimeString(FTBUTicks.getSecondsUntilRestart() * 1000L)));
		
		if(FTBUConfigBackups.enabled.get())
			main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_backup", LMStringUtils.getTimeString(Backups.getSecondsUntilNextBackup() * 1000L)));
		
		FTBUConfig.onGuideEvent(this);
		
		if(FTBUConfigTops.first_joined.get()) addTop(Top.first_joined);
		if(FTBUConfigTops.deaths.get()) addTop(Top.deaths);
		if(FTBUConfigTops.deaths_ph.get()) addTop(Top.deaths_ph);
		if(FTBUConfigTops.last_seen.get()) addTop(Top.last_seen);
		if(FTBUConfigTops.time_played.get()) addTop(Top.time_played);
		
		new EventFTBUServerGuide(this, self, isOP).post();
		
		categoryTops.subcategories.sort(null);
		
		if(isOP)
		{
			GuideCategory list = categoryOther.getSub(new ChatComponentText("Entities"));
			
			FastMap<String, Class<?>> map = new FastMap<String, Class<?>>();
			map.putAll(EntityList.stringToClassMapping);
			map.sortFromKeyStrings(true);
			
			Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
			for(Integer i : entityIDset)
				list.println("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
			
			list = categoryOther.getSub(new ChatComponentText("Enchantments"));
			
			IntList freeIDs = new IntList();
			
			for(int i = 0; i < Enchantment.enchantmentsList.length; i++)
			{
				if(Enchantment.enchantmentsList[i] == null) freeIDs.add(i);
				else list.println("[" + i + "] " + Enchantment.enchantmentsList[i].getTranslatedName(1));
			}
			
			list.println("Empty IDs: " + freeIDs.toString());
		}
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
	
	public void addConfigFromClass(String mod, String id, Class<?> c)
	{
		if(!isOP) return;
		
		try
		{
			GuideCategory category = getMod(mod).getSub(new ChatComponentText("Config")).getSub(new ChatComponentText(id));
			
			Field[] fields = c.getDeclaredFields();
			
			if(fields != null && fields.length > 0) for(Field f : fields)
			{
				f.setAccessible(true);
				
				if(f.isAnnotationPresent(GuideInfo.class))
				{
					GuideInfo i = f.getAnnotation(GuideInfo.class);
					
					String key = i.key();
					String info = i.info();
					String def = i.def();
					
					if(key.isEmpty()) key = f.getName();
					
					StringBuilder sb = new StringBuilder();
					sb.append(EnumChatFormatting.RED);
					sb.append('[');
					sb.append(key);
					sb.append(']');
					sb.append('\n');
					sb.append(EnumChatFormatting.BLUE);
					sb.append("Default: ");
					sb.append(def);
					sb.append(EnumChatFormatting.RESET);
					sb.append('\n');
					sb.append(info);
					sb.append(EnumChatFormatting.RESET);
					sb.append('\n');
					category.println(sb.toString());
				}
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
	}
}