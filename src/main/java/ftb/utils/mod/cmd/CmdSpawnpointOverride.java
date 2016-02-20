package ftb.utils.mod.cmd;

import ftb.lib.FTBLib;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created by LatvianModder on 20.02.2016.
 */
public class CmdSpawnpointOverride extends CommandSetSpawnpoint
{
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	public void processCommand(ICommandSender ics, String[] args)
	{
		if(args.length > 0)
		{
			if(ics instanceof EntityPlayerMP && !FTBLib.isOP(((EntityPlayerMP) ics).getGameProfile()))
			{
				throw new net.minecraft.command.NumberInvalidException();
			}
		}
		
		super.processCommand(ics, args);
	}
}
