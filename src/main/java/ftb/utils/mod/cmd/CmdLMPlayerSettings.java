package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.world.*;
import net.minecraft.command.*;
import net.minecraft.util.IChatComponent;

/**
 * Created by LatvianModder on 14.01.2016.
 */
public class CmdLMPlayerSettings extends CommandSubLM
{
	public CmdLMPlayerSettings()
	{
		super("lmplayer_settings", CommandLevel.ALL);
		add(new CmdSettingBool("chat_links", PersonalSettings.CHAT_LINKS));
		add(new CmdSettingBool("explosions", PersonalSettings.EXPLOSIONS));
		add(new CmdSettingBool("fake_players", PersonalSettings.FAKE_PLAYERS));
		add(new CmdBlockSecurity("block_security"));
	}
	
	public static class CmdSettingBool extends CommandLM
	{
		public final int flag;
		
		public CmdSettingBool(String s, int f)
		{
			super(s, CommandLevel.ALL);
			flag = f;
		}
		
		public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
		{
			if(i == 0) return new String[] {"true", "false"};
			return null;
		}
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerServer p = LMPlayerServer.get(ics);
			boolean b = parseBoolean(ics, args[0]);
			p.getSettings().set(flag, b);
			p.sendUpdate();
			FTBLib.printChat(ics, commandName + " set to " + b);
			return null;
		}
	}
	
	public static class CmdBlockSecurity extends CommandLM
	{
		public CmdBlockSecurity(String s)
		{ super(s, CommandLevel.ALL); }
		
		public String[] getTabStrings(ICommandSender ics, String args[], int i) throws CommandException
		{
			if(i == 0) return LMSecurityLevel.getNames();
			return null;
		}
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerServer p = LMPlayerServer.get(ics);
			LMSecurityLevel l = LMSecurityLevel.get(args[0]);
			if(l != null)
			{
				p.getSettings().blocks = l;
				FTBLib.printChat(ics, commandName + " set to " + l.uname);
			}
			return null;
		}
	}
}
