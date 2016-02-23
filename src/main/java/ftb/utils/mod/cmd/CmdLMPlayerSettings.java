package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.lib.api.players.LMPlayerMP;
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
		add(new CmdSettingBool("chat_links", FTBUPlayerData.CHAT_LINKS));
		add(new CmdSettingBool("explosions", FTBUPlayerData.EXPLOSIONS));
		add(new CmdSettingBool("fake_players", FTBUPlayerData.FAKE_PLAYERS));
		add(new CmdBlockSecurity("block_security"));
	}
	
	public static class CmdSettingBool extends CommandLM
	{
		public final byte flag;
		
		public CmdSettingBool(String s, byte f)
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
			LMPlayerMP p = LMPlayerMP.get(ics);
			boolean b = parseBoolean(args[0]);
			FTBUPlayerDataMP.get(p).setFlag(flag, b);
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
			if(i == 0) return PrivacyLevel.getNames();
			return null;
		}
		
		public IChatComponent onCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerMP p = LMPlayerMP.get(ics);
			PrivacyLevel l = PrivacyLevel.get(args[0]);
			if(l != null)
			{
				FTBUPlayerDataMP.get(p).blocks = l;
				FTBLib.printChat(ics, commandName + " set to " + l.uname);
			}
			return null;
		}
	}
}
