package latmod.ftbu.mod;

import java.util.Set;

import ftb.lib.FTBWorld;
import latmod.ftbu.api.guide.*;
import latmod.ftbu.mod.config.*;
import latmod.ftbu.tile.TileLM;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.*;

public class FTBUCommon // FTBUClient
{
	public void preInit()
	{
	}
	
	public void postInit()
	{
	}
	
	public void onGuideEvent(GuideFile file)
	{
	}
	
	@SuppressWarnings("unchecked")
	public final void addServerInfo(GuideFile file, LMPlayerServer self)
	{
		FastList<LMPlayerServer> players = LMWorldServer.inst.getServerPlayers();
		
		for(int i = 0; i < players.size(); i++)
			players.get(i).refreshStats();
		
		GuideCategory tops = file.main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "top.title"));
		GuideCategory other = file.main.getSub(new ChatComponentTranslation(FTBU.mod.assets + "button.other"));
		
		other.println(new ChatComponentTranslation("ftbl:worldID", FTBWorld.server.getWorldIDS()));
		
		if(FTBUConfigLogin.motd.get().length > 0)
		{
			for(String s : FTBUConfigLogin.motd.get())
				file.main.println(s);
			file.main.println("");
		}
		
		if(!FTBUConfigLogin.rules.get().isEmpty())
		{
			IChatComponent c = new ChatComponentText("[rules_link]");
			c.getChatStyle().setColor(EnumChatFormatting.GOLD);
			file.main.println(c);
			GuideLink l = new GuideLink(GuideLink.TYPE_URL);
			l.link = FTBUConfigLogin.rules.get();
			l.text = "[Click to open rules]";
			file.links.put("rules_link", l);
		}
		
		if(FTBUConfigGeneral.restartTimer.get() > 0F)
			file.main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_restart", LMStringUtils.getTimeString(FTBUTicks.getSecondsUntilRestart() * 1000L)));
		
		if(FTBUConfigBackups.enabled.get())
			file.main.println(new ChatComponentTranslation(FTBU.mod.assets + "cmd.timer_backup", LMStringUtils.getTimeString(Backups.getSecondsUntilNextBackup() * 1000L)));
		
		FastList<Top> topsList = new FastList<Top>();
		topsList.add(Top.age);
		topsList.add(Top.deaths);
		topsList.add(Top.deathsPerHour);
		topsList.add(Top.lastSeen);
		topsList.add(Top.timePlayed);
		
		new EventFTBUServerInfo(file, topsList, self).post();
		
		for(int i = 0; i < topsList.size(); i++)
		{
			Top t = topsList.get(i);
			GuideCategory thisTop = tops.getSub(t.ID);
			
			players.sort(t);
			
			for(int j = 0; j < players.size(); j++)
			{
				LMPlayerServer p = players.get(j);
				
				IChatComponent c = new ChatComponentText("[" + (j + 1) + "] " + p.getName() + ": " + t.getData(p));
				if(p == self) c.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
				else if(j < 3) c.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
				thisTop.println(c);
			}
		}
		
		tops.subcategories.sort(null);
		
		if(self != null && self.isOP())
		{
			GuideCategory list = other.getSub(new ChatComponentText("Entities"));
			
			FastMap<String, Class<?>> map = new FastMap<String, Class<?>>();
			map.putAll(EntityList.stringToClassMapping);
			map.sortFromKeyStrings(true);
			
			Set<Integer> entityIDset = EntityList.IDtoClassMapping.keySet();
			for(Integer i : entityIDset)
				list.println("[" + i + "] " + EntityList.getStringFromID(i.intValue()));
			
			list = other.getSub(new ChatComponentText("Enchantments"));
			
			IntList freeIDs = new IntList();
			
			for(int i = 0; i < Enchantment.enchantmentsList.length; i++)
			{
				if(Enchantment.enchantmentsList[i] == null) freeIDs.add(i);
				else list.println("[" + i + "] " + Enchantment.enchantmentsList[i].getTranslatedName(1));
			}
			
			list.println("Empty IDs: " + freeIDs.toString());
		}
	}
	
	public LMWorld getClientWorldLM()
	{ return null; }
	
	public boolean openClientGui(EntityPlayer ep, String mod, int id, NBTTagCompound data) { return false; }
	public void readTileData(TileLM t, S35PacketUpdateTileEntity p) { }
}