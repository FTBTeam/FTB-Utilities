package latmod.ftbu.mod.cmd.admin;

import latmod.ftbu.core.cmd.SubCommand;
import net.minecraft.command.ICommandSender;

public class CmdAdminUnclaim extends SubCommand
{
	public String onCommand(ICommandSender ics, String[] args)
	{
		/*
		EntityPlayerMP ep = CommandLM.getCommandSenderAsPlayer(ics);
		EnkiData.Data d = EnkiData.getData(ep);
		Claim cc = new Claim(d.claims, ep);
		
		ClaimResult r = d.claims.changeChunk(ep, cc, false, false);
		
		EnkiToolsTickHandler.instance.printChunkChangedMessage(ep);
		
		if(r == ClaimResult.SUCCESS)
			return CommandLM.FINE + "Unclaimed " + CmdClaim.chStr(1);
		else if(r == ClaimResult.SPAWN)
			return CommandLM.FINE + "You can't unclaim land in spawn!";
		else
			return CommandLM.FINE + "Chunk is not claimed!";
			*/
		
		return "Unimplemented!";
	}
}