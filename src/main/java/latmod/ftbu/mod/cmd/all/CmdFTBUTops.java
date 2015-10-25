package latmod.ftbu.mod.cmd.all;

import java.util.Comparator;

import latmod.ftbu.api.guide.*;
import latmod.ftbu.cmd.*;
import latmod.ftbu.net.MessageDisplayGuide;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdFTBUTops extends CommandSubLM
{
	public CmdFTBUTops(String s)
	{
		super(s, CommandLevel.ALL);
		
		add(new CommandLM("all", CommandLevel.ALL)
		{
			public IChatComponent onCommand(ICommandSender ics, String[] args)
			{
				EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
				final GuideFile file = new GuideFile("Top10s");
				
				file.main.println("All Top10s");
				
				for(CommandLM c : subCommands.values)
				{
					if(c != this && c instanceof CmdTop)
					{
						GuideCategory cat = new GuideCategory(file.main, LMStringUtils.firstUppercase(c.commandName));
						FastList<IChatComponent> list = new FastList<IChatComponent>();
						
						((CmdTop)c).printToList(list, LMWorldServer.inst.getPlayer(ics), true);
						
						for(int i = 0; i < list.size(); i++)
						{
							IChatComponent icc = list.get(i);
							if(icc.getChatStyle().getColor() == EnumChatFormatting.GOLD)
								icc.getChatStyle().setColor(EnumChatFormatting.LIGHT_PURPLE);
							else if(icc.getChatStyle().getColor() == EnumChatFormatting.GREEN)
								icc.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
							cat.println(icc.getFormattedText());
						}
						
						file.main.subcategories.add(cat);
						file.main.subcategories.sort(null);
					}
				}
				
				new MessageDisplayGuide(file).sendTo(ep);
				return null;
			}
		});
		
		add(new CmdTop("deaths")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Integer.compare(o2.deaths, o1.deaths); }
			
			public String getData(LMPlayerServer p)
			{ return Integer.toString(p.deaths); }
		});
		
		add(new CmdTop("timePlayed")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o2.timePlayed, o1.timePlayed); }
			
			public String getData(LMPlayerServer p)
			{ return LMStringUtils.getTimeString(p.timePlayed) + " [" + (p.timePlayed / 3600000L) + "h]"; }
		});
		
		add(new CmdTop("age")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o1.firstJoined, o2.firstJoined); }
			
			public String getData(LMPlayerServer p)
			{ return LMStringUtils.getTimeString(LMUtils.millis() - p.firstJoined); }
		});
		
		add(new CmdTop("deathsPerHour")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Double.compare(o2.getDeathsPerHour(), o1.getDeathsPerHour()); }
			
			public String getData(LMPlayerServer p)
			{ return MathHelperLM.toSmallDouble(p.getDeathsPerHour()); }
		});
	}
	
	public abstract class CmdTop extends CommandLM implements Comparator<LMPlayerServer>
	{
		public CmdTop(String s)
		{ super(s, CommandLevel.ALL); }
		
		public abstract String getData(LMPlayerServer p);
		
		public IChatComponent onCommand(ICommandSender ics, String[] args)
		{
			FastList<IChatComponent> list = new FastList<IChatComponent>();
			printToList(list, LMWorldServer.inst.getPlayer(ics), args.length > 0 && args[0].equals("full"));
			for(IChatComponent c : list) ics.addChatMessage(c);
			return null;
		}
		
		public void printToList(FastList<IChatComponent> list, LMPlayerServer self, boolean full)
		{
			FastList<LMPlayerServer> players = LMWorldServer.inst.getServerPlayers();
			
			for(int i = 0; i < players.size(); i++)
				players.get(i).refreshStats();
			
			players.sort(this);
			
			boolean isInList = false;
			
			list.add(new ChatComponentText(LMStringUtils.firstUppercase(commandName) + ":"));
			
			int s = full ? players.size() : Math.min(players.size(), 10);
			for(int i = 0; i < s; i++)
			{
				LMPlayerServer p = players.get(i);
				
				IChatComponent c = new ChatComponentText("[" + (i + 1) + "] " + p.getName() + ": " + getData(p));
				if(p == self) { isInList = true; c.getChatStyle().setColor(EnumChatFormatting.GREEN); }
				else if(i < 3) c.getChatStyle().setColor(EnumChatFormatting.GOLD);
				list.add(c);
			}
			
			if(!isInList && self != null)
			{
				IChatComponent c = new ChatComponentText("[" + (players.indexOf(self) + 1) + "] " + self.getName());
				c.getChatStyle().setColor(EnumChatFormatting.GREEN);
				list.add(c);
			}
		}
	}
}
