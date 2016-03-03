package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.ranks.*;
import latmod.lib.LMListUtils;
import net.minecraft.command.*;
import net.minecraft.util.*;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdSetRank extends CommandLM
{
	public CmdSetRank()
	{ super("setrank", CommandLevel.OP); }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.TRUE : null; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
	{
		if(i == 1) return LMListUtils.toStringArray(Ranks.instance().ranks.keySet());
		return super.getTabStrings(ics, args, i);
	}
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 2);
		LMPlayerMP player = LMPlayerMP.get(args[0]);
		Rank r = Ranks.instance().ranks.get(args[1]);
		if(r == null) return error(new ChatComponentText("Rank '" + args[1] + "' not found!"));
		Ranks.instance().playerMap.put(player.getProfile().getId(), r);
		Ranks.instance().saveRanks();
		return null;
	}
}
