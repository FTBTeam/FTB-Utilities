package com.feed_the_beast.ftbutilities.cmd.ranks;


import com.feed_the_beast.ftblib.lib.cmd.CmdTreeBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdRanks extends CmdTreeBase
{
	public CmdRanks()
	{
		super("ranks");
		addSubcommand(new CmdAdd());
		addSubcommand(new CmdGet());
		addSubcommand(new CmdSet());
		addSubcommand(new CmdGetPlayerPermission());
		addSubcommand(new CmdSetPermission());
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		/*if (args.length == 0)
		{
			getCommandMap().get("gui").execute(server, sender, StringUtils.shiftArray(args));
		}
		else*/
		{
			super.execute(server, sender, args);
		}
	}
}