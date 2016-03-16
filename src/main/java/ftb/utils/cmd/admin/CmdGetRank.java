package ftb.utils.cmd.admin;

import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.cmd.*;
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
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		ForgePlayerMP p = ForgePlayerMP.get(args[0]);
		Rank r = Ranks.instance().getRankOf(p.getProfile());
		IChatComponent c = new ChatComponentText(r.getID());
		c.getChatStyle().setColor(r.color);
		ics.addChatMessage(c);
	}
}
