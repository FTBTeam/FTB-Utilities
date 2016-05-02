package ftb.utils.cmd.admin;

import ftb.lib.BlockDimPos;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.utils.FTBULang;
import ftb.utils.world.FTBUWorldDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;

public class CmdSetWarp extends CommandLM
{
	public CmdSetWarp()
	{ super("setwarp", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <ID> [x] [y] [z]"; }
	
	@Override
	public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		BlockPos c;
		
		if(args.length >= 4)
		{
			int x = parseInt(args[1]);
			int y = parseInt(args[2]);
			int z = parseInt(args[3]);
			c = new BlockPos(x, y, z);
		}
		else { c = ep.getPosition(); }
		
		FTBUWorldDataMP.get().warps.set(args[0], new BlockDimPos(c, DimensionType.getById(ep.dimension)));
		FTBULang.warp_set.printChat(ics, args[0]);
	}
}