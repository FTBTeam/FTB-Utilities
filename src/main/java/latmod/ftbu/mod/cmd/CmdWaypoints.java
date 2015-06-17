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
			
			Waypoints.Waypoint w = new Waypoints.Waypoint(args[0], ep.posX, ep.posY, ep.posZ, ep.worldObj.provider.dimensionId);
			
			if(args.length >= 4)
			{
				w.colR = parseInt(ics, args[1]);
				w.colG = parseInt(ics, args[2]);
				w.colB = parseInt(ics, args[3]);
			}
			else
			{
				w.colR = ep.worldObj.rand.nextInt(256);
				w.colG = ep.worldObj.rand.nextInt(256);
				w.colB = ep.worldObj.rand.nextInt(256);
			}
			
			Waypoints.waypoints.add(w);
			Waypoints.save();
			
			return FINE + "Waypoint '" + args[0] + "' (" + LatCoreMC.getDimName(ics.getEntityWorld()) + ") added!";
		}
	}
	
	public static class CmdWaypointsRem extends SubCommand
	{
		public String[] getTabStrings(ICommandSender ics, String args[], int i)
		{
			if(i != 0) return null;
			String[] s = new String[Waypoints.waypoints.size()];
			for(int j = 0; j < s.length; j++)
				s[j] = Waypoints.waypoints.get(j).name;
			return s;
		}
		
		public String onCommand(ICommandSender ics, String[] args)
		{
			CommandLM.checkArgs(args, 1);
			
			int i = Waypoints.getFirstIndex(args[0]);
			
			if(i != -1)
			{
				Waypoints.waypoints.remove(i);
				Waypoints.save();
				return FINE + "Waypoint removed";
			}
			
			return null;
		}
	}
}