package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
import ftb.utils.ranks.*;
import net.minecraft.command.*;
import net.minecraft.util.*;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdGetRank extends CommandLM
{
	public CmdGetRank()
	{ super("getrank", CommandLevel.OP); }
	
	public Boolean getUsername(String[] args, int i)
	{ return (i == 0) ? Boolean.FALSE : null; }
	
	public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		LMPlayerMP p = LMPlayerMP.get(args[0]);
		Rank r = Ranks.instance().getRankOf(p.getProfile());
		IChatComponent c = new ChatComponentText(r.ID);
		c.getChatStyle().setColor(r.color);
		return c;
	}
}
