package latmod.ftbu.mod.cmd.all;

import java.util.Comparator;

import latmod.ftbu.cmd.*;
import latmod.ftbu.world.*;
import latmod.lib.FastList;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.*;

public class CmdFTBUTopDeaths extends CommandLM
{
	public CmdFTBUTopDeaths(String s)
	{ super(s, CommandLevel.ALL); }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		FastList<LMPlayerServer> players = LMWorldServer.inst.players.clone();
		
		players.sort(new Comparator<LMPlayerServer>()
		{
			public int compare(LMPlayerServer o1, LMPlayerServer o2)
			{ return Integer.compare(o1.deaths, o2.deaths); }
		});
		
		LMPlayerServer self = LMWorldServer.inst.getPlayer(ics);
		boolean isInList = false;
		
		int s = Math.min(players.size(), 10);
		for(int i = 0; i < s; i++)
		{
			LMPlayerServer p = players.get(i);
			
			IChatComponent c = new ChatComponentText("[" + (i + 1) + "] " + p.getName());
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
