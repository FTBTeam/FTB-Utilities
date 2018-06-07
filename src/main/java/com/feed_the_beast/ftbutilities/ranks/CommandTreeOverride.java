package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.command.CmdTreeBase;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandTreeOverride extends CmdTreeBase
{
	public final CommandTreeBase mirrored;

	public CommandTreeOverride(CommandTreeBase c, Node parent)
	{
		super(c.getName());
		mirrored = c;
		Node node = parent.append(mirrored.getName());

		for (ICommand command : mirrored.getSubCommands())
		{
			addSubcommand(CommandOverride.create(command, node));
		}
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return mirrored.getUsage(sender);
	}

	@Override
	public List<String> getAliases()
	{
		return mirrored.getAliases();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return mirrored.isUsernameIndex(args, index);
	}
}