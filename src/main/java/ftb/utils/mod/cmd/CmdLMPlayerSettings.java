package ftb.utils.mod.cmd;

import ftb.lib.*;
import ftb.lib.api.cmd.*;
import ftb.utils.world.*;
import net.minecraft.command.*;

import java.util.List;

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
		add(new CmdRenderBadge("render_badge"));
	}
	
	public static class CmdSettingBool extends CommandLM
	{
		public final byte flag;
		
		public CmdSettingBool(String s, byte f)
		{
			super(s, CommandLevel.ALL);
			flag = f;
		}
		
		@Override
		public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
		{
			if(args.length == 1) return getListOfStringsMatchingLastWord(args, "true", "false");
			return null;
		}
		
		@Override
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerServer p = LMPlayerServer.get(ics);
			boolean b = args[0].equals("toggle") ? !p.getSettings().get(flag) : parseBoolean(ics, args[0]);
			p.getSettings().set(flag, b);
			p.sendUpdate();
			if(!args[0].equals("toggle")) FTBLib.printChat(ics, commandName + " set to " + b);
		}
	}
	
	public static class CmdBlockSecurity extends CommandLM
	{
		public CmdBlockSecurity(String s)
		{ super(s, CommandLevel.ALL); }
		
		@Override
		public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
		{
			if(args.length == 1) return getListOfStringsMatchingLastWord(args, PrivacyLevel.getNames());
			return null;
		}
		
		@Override
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerServer p = LMPlayerServer.get(ics);
			
			if(args[0].equals("toggle"))
			{
				p.getSettings().blocks = PrivacyLevel.VALUES_3[(p.getSettings().blocks.ID + 1) % 3];
				p.sendUpdate();
				return;
			}
			
			PrivacyLevel l = PrivacyLevel.get(args[0]);
			if(l != null)
			{
				p.getSettings().blocks = l;
				FTBLib.printChat(ics, commandName + " set to " + l.name().toLowerCase());
			}
		}
	}
	
	public static class CmdRenderBadge extends CommandLM
	{
		public CmdRenderBadge(String s)
		{
			super(s, CommandLevel.ALL);
		}
		
		@Override
		public List<String> addTabCompletionOptions(ICommandSender ics, String[] args)
		{
			if(args.length == 1) return getListOfStringsMatchingLastWord(args, "true", "false");
			return null;
		}
		
		@Override
		public void processCommand(ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			LMPlayerServer p = LMPlayerServer.get(ics);
			boolean b = args[0].equals("toggle") ? !p.renderBadge : parseBoolean(ics, args[0]);
			p.renderBadge = b;
			p.sendUpdate();
			if(!args[0].equals("toggle")) FTBLib.printChat(ics, commandName + " set to " + b);
		}
	}
}
