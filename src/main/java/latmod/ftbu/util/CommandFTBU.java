package latmod.ftbu.util;

import ftb.lib.cmd.*;
import latmod.ftbu.world.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class CommandFTBU extends CommandLM
{
	public CommandFTBU(String s, CommandLevel l)
	{ super(s, l); }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{ return LMWorldServer.inst.getAllPlayerNames(getUsername(args, i)); }
	
	public static EntityPlayerMP getPlayer(ICommandSender ics, String s)
	{
		EntityPlayerMP ep = getLMPlayer(s).getPlayer();
		if(ep != null) return ep;
		throw new PlayerNotFoundException();
	}
	
	public static LMPlayerServer getLMPlayer(Object o)
	{
		LMPlayerServer p = LMWorldServer.inst.getPlayer(o);
		if(p == null) throw new PlayerNotFoundException();
		return p;
	}
}