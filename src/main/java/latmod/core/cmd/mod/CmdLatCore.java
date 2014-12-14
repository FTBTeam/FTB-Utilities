package latmod.core.cmd.mod;

import latmod.core.*;
import latmod.core.mod.ThreadCheckVersions;
import net.minecraft.command.*;

public class CmdLatCore extends CommandBaseLC
{
	public CmdLatCore(int e)
	{ super("latcore", e); }
	
	public void printHelp(ICommandSender ics)
	{
		printHelpLine(ics, "<versions>");
		printHelpLine(ics, "<friends> set <friend | enemy | none> <player>");
		printHelpLine(ics, "<friends> list <friend | enemy>");
		printHelpLine(ics, "<friends> clear <friend | enemy | all>");
		printHelpLine(ics, "<friends> gui");
	}
	
	public String[] getSubcommands(ICommandSender ics)
	{ return new String[] { "versions", "friends" }; }
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return getSubcommands(ics);
		if(i > 0 && isArg(args, 0, "friends"))
		{
			if(i == 1) return new String[] { "set", "list", "clear", "gui" };
			if(i == 2)
			{
				if(isArg(args, 1, "set")) return new String[] { "friend", "enemy", "none" };
				if(isArg(args, 1, "list")) return new String[] { "friend", "enemy" };
				if(isArg(args, 1, "clear")) return new String[] { "friend", "enemy", "all" };
			}
		}
		
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{
		if(i == 3 && isArg(args, 0, "friends") && isArg(args, 1, "set")) return NameType.LM_OFF;
		return NameType.NONE;
	}
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args == null || args.length == 0)
			return "Subcommands: " + LatCore.strip(getSubcommands(ics));
		
		if(args[0].equals("versions"))
		{
			ThreadCheckVersions.init(ics, true);
			return null;
		}
		
		if(args[0].equals("friends"))
		{
			LMPlayer p = getLMPlayer(getCommandSenderAsPlayer(ics));
			
			if(args.length == 2 && args[1].equals("gui"))
			{
				//open gui
				return null;
			}
			
			if(args.length < 3) return "Missing arguments!";
			
			if(args[1].equals("set"))
			{
				if(args.length != 4) return "Missing arguments!";
				
				LMPlayer p1 = getLMPlayer(args[3]);
				if(p1 == null) throw new PlayerNotFoundException();
				
				if(p.equals(p1)) return "You can't set your own status!";
				
				LMPlayer.Status s = LMPlayer.Status.NONE;
				if(args[2].equals("friend")) s = LMPlayer.Status.FRIEND;
				else if(args[2].equals("enemy")) s = LMPlayer.Status.ENEMY;
				
				p.setStatusFor(p1.uuid, s);
				return FINE + "Set " + p1.getDisplayName() + " as: " + s;
			}
			else if(args[1].equals("list"))
			{
				LMPlayer.Status s = LMPlayer.Status.NONE;
				if(args[2].equals("friend")) s = LMPlayer.Status.FRIEND;
				else if(args[2].equals("enemy")) s = LMPlayer.Status.ENEMY;
				
				if(s == LMPlayer.Status.NONE)
					return "Invalid status!";
				
				FastList<LMPlayer> l = p.getFriends(s);
				String[] n = new String[l.size()];
				
				for(int i = 0; i < l.size(); i++)
					n[i] = l.get(i).getDisplayName();
				
				if(n.length == 0) return FINE + "The " + s.toString().toLowerCase() + " list is empty";
				
				return FINE + LatCore.strip(n);
			}
			else if(args[1].equals("clear"))
			{
				LMPlayer.Status s = LMPlayer.Status.NONE;
				if(args[2].equals("friend")) s = LMPlayer.Status.FRIEND;
				else if(args[2].equals("enemy")) s = LMPlayer.Status.ENEMY;
				
				p.clearFriends(s);
				return FINE + "List cleared!";
			}
		}
		
		return onCommand(ics, null);
	}
}