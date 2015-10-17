package latmod.ftbu.mod.cmd.all;

import java.util.Comparator;

import latmod.ftbu.cmd.*;
import latmod.ftbu.world.*;
import latmod.lib.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUTops extends CommandSubLM
{
	public CmdFTBUTops(String s)
	{
		super(s, CommandLevel.ALL);
		
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
			{ return LMStringUtils.getTimeString(p.timePlayed); }
		});
		
		add(new CmdTop("age")
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Long.compare(o1.firstJoined, o2.firstJoined); }
			
			public String getData(LMPlayerServer p)
			{ return LMStringUtils.getTimeString(LMUtils.millis() - p.firstJoined); }
		});
	}
	
	public abstract class CmdTop extends CommandLM implements Comparator<LMPlayerServer>
	{
		public CmdTop(String s)
		{ super(s, CommandLevel.ALL); }
		
		public abstract String getData(LMPlayerServer p);
		
		public IChatComponent onCommand(ICommandSender ics, String[] args)
		{
			FastList<LMPlayerServer> players = LMWorldServer.inst.getServerPlayers();
			
			for(int i = 0; i < players.size(); i++)
				players.get(i).refreshStats();
			
			players.sort(this);
			
			LMPlayerServer self = LMWorldServer.inst.getPlayer(ics);
			boolean isInList = false;
			
			ics.addChatMessage(new ChatComponentText(LMStringUtils.firstUppercase(commandName) + ":"));
			
			int s = Math.min(players.size(), 10);
			for(int i = 0; i < s; i++)
			{
				LMPlayerServer p = players.get(i);
				
				IChatComponent c = new ChatComponentText("[" + (i + 1) + "] " + p.getName() + ": " + getData(p));
				if(p == self) { isInList = true; c.getChatStyle().setColor(EnumChatFormatting.GREEN); }
				else if(i < 3) c.getChatStyle().setColor(EnumChatFormatting.GOLD);
				ics.addChatMessage(c);
			}
			
			if(!isInList && self != null)
			{
				IChatComponent c = new ChatComponentText("[" + (players.indexOf(self) + 1) + "] " + self.getName());
				c.getChatStyle().setColor(EnumChatFormatting.GREEN);
				ics.addChatMessage(c);
			}
			
			return null;
		}
	}
}
