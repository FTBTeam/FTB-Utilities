package ftb.utils.cmd;

import ftb.lib.FTBLib;
import ftb.lib.PrivacyLevel;
import ftb.lib.api.ForgePlayerMP;
import ftb.lib.api.cmd.CommandLM;
import ftb.lib.api.cmd.CommandLevel;
import ftb.lib.api.cmd.CommandSubLM;
import ftb.utils.world.FTBUPlayerData;
import ftb.utils.world.FTBUPlayerDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.List;

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
		
		@Override
		public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
		{
			if(args.length == 1)
			{
				return getListOfStringsMatchingLastWord(args, "true", "false");
			}
			
			return super.getTabCompletionOptions(server, ics, args, pos);
		}
		
		@Override
		public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ForgePlayerMP p = ForgePlayerMP.get(ics);
			boolean b = parseBoolean(args[0]);
			FTBUPlayerDataMP.get(p).setFlag(flag, b);
			p.sendUpdate();
			FTBLib.printChat(ics, commandName + " set to " + b);
		}
	}
	
	public static class CmdBlockSecurity extends CommandLM
	{
		public CmdBlockSecurity(String s)
		{ super(s, CommandLevel.ALL); }
		
		@Override
		public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
		{
			if(args.length == 1)
			{
				return getListOfStringsMatchingLastWord(args, Arrays.asList(PrivacyLevel.PUBLIC, PrivacyLevel.PRIVATE, PrivacyLevel.FRIENDS));
			}
			
			return super.getTabCompletionOptions(server, ics, args, pos);
		}
		
		@Override
		public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
		{
			checkArgs(args, 1);
			ForgePlayerMP p = ForgePlayerMP.get(ics);
			PrivacyLevel l = PrivacyLevel.get(args[0]);
			if(l != null)
			{
				FTBUPlayerDataMP.get(p).blocks = l;
				FTBLib.printChat(ics, commandName + " set to " + args[0]);
			}
		}
	}
}
