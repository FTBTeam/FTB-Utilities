package latmod.ftbu.mod.cmd.admin;

import latmod.core.util.MathHelperLM;
import latmod.ftbu.cmd.*;
import latmod.ftbu.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminUnclaim extends CommandLM
{
	public CmdAdminUnclaim(String s)
	{ super(s, CommandLevel.OP); }

	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		ClaimedChunk c = Claims.get(ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ));
		
		if(c != null)
		{
			String s = c.toString();
			c.claims.unclaim(c.dim, c.posX, c.posZ, 1, 1, true);
			return new ChatComponentText("Unclaimed " + s); //LANG
		}
		
		return error(new ChatComponentText("Chunk not claimed!"));
	}
}