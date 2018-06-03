package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilities;
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
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.context.PlayerContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandOverride extends CommandBase
{
	public static ICommand create(ICommand command, Node parent)
	{
		if (command instanceof CommandTreeBase)
		{
			return new CommandTreeOverride((CommandTreeBase) command, parent);
		}
		else
		{
			return new CommandOverride(command, parent);
		}
	}

	public final ICommand mirrored;
	public final Node node;
	public final ITextComponent usage;

	private CommandOverride(ICommand c, Node parent)
	{
		mirrored = c;
		node = parent.append(mirrored.getName());
		Ranks.INSTANCE.commands.put(node, this);

		String usageS = getUsage(Ranks.INSTANCE.universe.server);

		if (usageS == null || usageS.isEmpty() || usageS.indexOf('/') != -1 || usageS.indexOf('%') != -1 || usageS.indexOf(' ') != -1)
		{
			FTBUtilities.LOGGER.warn("Command " + node + " (class: " + mirrored.getClass().getName() + ") has invalid usage language key: " + usageS);
			usage = new TextComponentString(String.valueOf(usageS));
		}
		else
		{
			usage = new TextComponentTranslation(usageS);
		}
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
			EntityPlayerMP player = (EntityPlayerMP) sender;
			Event.Result result = Ranks.getPermissionResult(server, player.getGameProfile(), node, new PlayerContext(player));

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