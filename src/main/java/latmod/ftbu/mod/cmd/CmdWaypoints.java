package latmod.ftbu.mod.cmd;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.cmd.*;
import latmod.ftbu.mod.client.Waypoints;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CmdWaypoints extends CommandSubLM
{
	public CmdWaypoints()
	{
		super("waypoints", CommandLevel.ALL);
		subCommands.put("add", new CmdWaypointsAdd());
		subCommands.put("rem", new CmdWaypointsRem());
	}
	
	public static class CmdWaypointsAdd extends SubCommand
	{
		public String onCommand(ICommandSender ics, String[] args)
		{
			CommandLM.checkArgs(args, 1);
			if(!(ics instanceof EntityPlayer)) return "Invalid player!";
			EntityPlayer ep = (EntityPlayer)ics;
			
			Waypoints.Waypoint w = new Waypoints.Waypoint(args[0], ep.worldObj.provider.dimensionId);
			w.setPos(ep.posX, ep.posY, ep.posZ);
			
			if(args.length >= 7)
			{
				w.setPos(parseDouble(ics, args[1]), parseDouble(ics, args[2]), parseDouble(ics, args[3]));
				w.setColor(parseInt(ics, args[4]), parseInt(ics, args[5]), parseInt(ics, args[6]));
				
			}
			else
			{
				w.setColor(ep.worldObj.rand.nextInt());
			}
			
			Waypoints.add(w);
			
			return FINE + "Waypoint '" + args[0] + "' (" + LatCoreMC.getDimName(ics.getEntityWorld()) + ") added!";
		}
	}
	
	public static class CmdWaypointsRem extends SubCommand
	{
		public String[] getTabStrings(ICommandSender ics, String args[], int i)
		{
			if(i != 0) return null;
			return Waypoints.getAllNames(Waypoints.getAll());
		}
		
		public String onCommand(ICommandSender ics, String[] args)
		{
			CommandLM.checkArgs(args, 1);
			
			int i = Waypoints.getFirstIndex(args[0]);
			
			if(i != -1)
			{
				Waypoints.remove(i);
				return FINE + "Waypoint removed";
			}
			
			return null;
		}
	}
}