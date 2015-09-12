package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.*;
import latmod.ftbu.core.util.MathHelperLM;
import latmod.ftbu.core.world.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.*;

public class CmdAdminUnclaim extends SubCommand
{
	public IChatComponent onCommand(ICommandSender ics, String[] args)
	{
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		
		ClaimedChunk c = Claims.get(ep.dimension, MathHelperLM.chunk(ep.posX), MathHelperLM.chunk(ep.posZ));
		
		if(c != null)
		{
			String s = c.toString();
			if(c.claims.unclaim(c.dim, c.posX, c.posZ, true))
				return new ChatComponentText("Unclaimed " + s); //LANG
			else
				return CommandLM.error(new ChatComponentText("Can't unclaim " + s + "!"));
		}
		
		return CommandLM.error(new ChatComponentText("Chunk not claimed!"));
	}
}