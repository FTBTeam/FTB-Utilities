package ftb.utils.mod.cmd;

import ftb.lib.FTBLib;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdSpawnpointOverride extends CommandSetSpawnpoint
{
	public int getRequiredPermissionLevel()
	{ return 0; }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return true; }
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(ics instanceof EntityPlayerMP && args.length > 0)
		{
			if(!FTBLib.isOP(((EntityPlayerMP) ics).getGameProfile()))
				throw new IllegalArgumentException("Only OPs can set other's spawnpoints!");
		}
		
		super.processCommand(ics, args);
	}
}