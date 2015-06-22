package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.*;
import latmod.ftbu.core.cmd.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;

public class CmdTplast extends CommandLM
{
	public CmdTplast()
	{ super("tpl", CommandLevel.OP); }
	
	public NameType getUsername(String[] args, int i)
	{ if(i == 0) return NameType.OFF; return NameType.NONE; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		checkArgs(args, 1);
		EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
		
		LMPlayer p = getLMPlayer(args[0]);
		
		if(p.isOnline())
		{
			EntityPlayerMP ep1 = p.getPlayerMP();
			Teleporter.travelEntity(ep, ep1.posX, ep1.posY, ep1.posZ, ep1.dimension);
		}
		else
		{
			//EnkiData.Data d = EnkiData.getData(p);
			if(p.last == null) return "No last position!";
			Teleporter.travelEntity(ep, p.last.x, p.last.y, p.last.z, p.last.dim);
		}
		
		return FINE + "Teleported to " + p.getName() + "!";
	}
}