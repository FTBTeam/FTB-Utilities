package com.feed_the_beast.ftbutilities.ranks;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandOverride extends CommandBase
{
	public static ICommand create(ICommand command, String parent, @Nullable ModContainer container)
	{
		if (command instanceof CommandTreeBase)
		{
			return new CommandTreeOverride((CommandTreeBase) command, parent, container);
		}
		else
		{
			return new CommandOverride(command, parent, container);
		}
	}

	public final ICommand mirrored;
	public final String node;
	public final ITextComponent usage;
	public final ModContainer modContainer;

	private CommandOverride(ICommand c, String parent, @Nullable ModContainer container)
	{
		mirrored = c;
		node = parent + '.' + mirrored.getName();
		Ranks.INSTANCE.commands.put(node, this);

		String usageS = getUsage(Ranks.INSTANCE.universe.server);

		if (usageS == null || usageS.isEmpty() || usageS.indexOf('/') != -1 || usageS.indexOf('%') != -1 || usageS.indexOf(' ') != -1)
		{
			usage = new TextComponentString(usageS);
		}
		else
		{
			usage = new TextComponentTranslation(usageS);
		}

		modContainer = container;
	}

	@Override
	public String getName()
	{
		return mirrored.getName();
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		mirrored.execute(server, sender, args);
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return mirrored instanceof CommandBase ? ((CommandBase) mirrored).getRequiredPermissionLevel() : 4;
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		if (sender instanceof EntityPlayerMP)
		{
			Event.Result result = Ranks.INSTANCE.getPermissionResult((EntityPlayerMP) sender, node, true);

			if (result != Event.Result.DEFAULT)
			{
				return result == Event.Result.ALLOW;
			}
		}

		return mirrored.checkPermission(server, sender);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		return mirrored.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return mirrored.isUsernameIndex(args, index);
	}

	@Override
	public int compareTo(ICommand o)
	{
		return o instanceof CommandOverride ? node.compareTo(((CommandOverride) o).node) : getName().compareTo(o.getName());
	}
}