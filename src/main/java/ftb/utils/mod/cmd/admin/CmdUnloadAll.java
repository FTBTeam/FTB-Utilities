package ftb.utils.mod.cmd.admin;

import ftb.lib.api.cmd.*;
import ftb.lib.api.players.*;
import ftb.utils.world.*;
import net.minecraft.command.*;
import net.minecraft.util.ChatComponentText;

public class CmdUnloadAll extends CommandLM
{
	public CmdUnloadAll()
	{ super("unload_all", CommandLevel.OP); }
	
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player | @a>"; }
	
	public boolean isUsernameIndex(String[] args, int i)
	{ return i == 0; }
	
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		
		if(args[0].equals("@a"))
		{
			for(ClaimedChunk c : FTBUWorldDataMP.inst.getAllChunks())
				c.isChunkloaded = false;
			for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
				p.toPlayerMP().sendUpdate();
			ics.addChatMessage(new ChatComponentText("Unloaded all chunks"));
			return;
		}
		
		ForgePlayerMP p = ForgePlayerMP.get(args[0]);
		for(ClaimedChunk c : FTBUWorldDataMP.inst.getChunks(p.getProfile().getId(), null))
			c.isChunkloaded = false;
		if(p.isOnline()) p.sendUpdate();
		ics.addChatMessage(new ChatComponentText("Unloaded all " + p.getProfile().getName() + "'s chunks"));
	}
}