package latmod.ftbu.core.cmd;

import java.util.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public abstract class CommandLM extends CommandBase
{
	public static final String FINE = EnumChatFormatting.WHITE + "";
	
	public final String commandName;
	public final CommandLevel level;
	public final FastList<String> aliases = new FastList<String>();
	
	public CommandLM(String s, CommandLevel l)
	{ commandName = s; level = l; }
	
	public int getRequiredPermissionLevel()
	{ return level.requiredPermsLevel(); }
	
	public boolean canCommandSenderUseCommand(ICommandSender ics)
	{ return super.canCommandSenderUseCommand(ics); }
	
	public final String getCommandName()
	{ return commandName; }
	
	public String getCommandUsage(ICommandSender ics)
	{ return "/" + commandName; }
	
	public final void processCommand(ICommandSender ics, String[] args)
	{
		if(args == null) args = new String[0];
		
		String[] scmds = getSubcommands(ics);
		if(scmds != null && args.length == 0)
		{ LatCoreMC.printChat(ics, "Subcommands: " + LatCore.strip(scmds)); return; }
		
		String s = onCommand(ics, args);
		if(s != null)
		{
			s = EnumChatFormatting.RED + s;
			
			if(s.startsWith(EnumChatFormatting.RED + FINE)) s = s.substring(4);
			LatCoreMC.printChat(ics, s);
		}
		onPostCommand(ics, args);
	}
	
	public List<String> getCommandAliases()
	{ return aliases.isEmpty() ? null : aliases; }
	
	public String[] getSubcommands(ICommandSender ics)
	{ return null; }
	
	public abstract String onCommand(ICommandSender ics, String[] args);
	
	public String retHelp(ICommandSender ics, String cmd)
	{ return "Invalid command syntax!"; }
	
	public final void printHelpLine(ICommandSender ics, String args)
	{ LatCoreMC.printChat(ics, "/" + commandName + (args != null && args.length() > 0 ? (" " + args) : "")); }
	
	public void onPostCommand(ICommandSender ics, String[] args) {}
	
	@SuppressWarnings("all")
	public final List addTabCompletionOptions(ICommandSender ics, String[] args)
	{
		String[] s = getTabStrings(ics, args, args.length - 1);
		if(s != null && s.length > 0)
		{
			if(sortStrings(ics, args, args.length - 1)) Arrays.sort(s);
			return getListOfStringsMatchingLastWord(args, s);
		}
		return null;
	}
	
	public NameType getUsername(String[] args, int i)
	{ return NameType.NONE; }
	
	public static boolean isArg(String[] args, int i, String... s)
	{
		if(args != null && i >= 0 && i < args.length)
		{
			for(int j = 0; j < s.length; j++)
				if(args[i].equals(s[j])) return true;
		}
		
		return false;
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{ return getUsername(args, i).getUsernames(); }
	
	public boolean sortStrings(ICommandSender ics, String args[], int i)
	{ return getUsername(args, i) == NameType.NONE; }
	
	public static EntityPlayerMP getPlayer(ICommandSender ics, String s)
	{
		EntityPlayerMP ep = getLMPlayer(s).getPlayerMP();
		if(ep != null) return ep;
		throw new PlayerNotFoundException();
	}
	
	public static LMPlayerServer getLMPlayer(Object o)
	{
		LMPlayerServer p = LMWorld.server.getPlayer(o);
		if(p == null) throw new PlayerNotFoundException();
		return p;
	}
	
	public static void checkArgs(String[] args, int i)
	{ if(args == null || args.length < i) throw new MissingArgsException(); }
	
	public static void checkArgsStrong(String[] args, int i)
	{ if(args == null || args.length != i) throw new MissingArgsException(); }
}