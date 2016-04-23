package ftb.utils.mod.cmd.admin;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.world.LMPlayerServer;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;

import java.io.File;

//FIXME: UNFINISHED
public class CmdResetPos extends CommandLM
{
	public CmdResetPos()
	{ super("reset_pos", CommandLevel.OP); }
	
	@Override
	public String getCommandUsage(ICommandSender ics)
	{ return '/' + commandName + " <player>"; }
	
	@Override
	public void processCommand(ICommandSender ics, String[] args) throws CommandException
	{
		checkArgs(args, 1);
		LMPlayerServer p = LMPlayerServer.get(args[0]);
		if(p.isOnline())
		{
			error("Player can't be online!");
		}
		
		double x, y, z;
		
		if(args.length >= 4)
		{
			EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
			x = func_110665_a(ics, ep.posX, args[1], -30000000, 30000000);
			y = func_110665_a(ics, ep.posY, args[2], -30000000, 30000000);
			z = func_110665_a(ics, ep.posZ, args[3], -30000000, 30000000);
		}
		else
		{
			ChunkCoordinates c = FTBLib.getServerWorld().getSpawnPoint();
			x = c.posX + 0.5D;
			y = c.posY + 0.5D;
			z = c.posZ + 0.5D;
		}
		
		File file = new File(FTBLib.getServerWorld().getSaveHandler().getWorldDirectory(), "playerdata/" + p.getProfile().getId() + ".dat");
		
		if(!file.exists())
		{
			error("Cannot load the file!");
		}
		
		NBTTagCompound data = LMNBTUtils.readMap(file);
		LMNBTUtils.writeMap(file, data);
		ics.addChatMessage(new ChatComponentText("Reset position of " + p.getProfile().getName()));
	}
}