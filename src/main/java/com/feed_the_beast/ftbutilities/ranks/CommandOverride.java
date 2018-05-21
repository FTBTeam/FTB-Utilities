package com.feed_the_beast.ftbutilities.ranks;

import com.feed_the_beast.ftblib.lib.cmd.ICommandWithCustomPermission;
import com.feed_the_beast.ftblib.lib.cmd.ICommandWithParent;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.command.CommandTreeBase;
import net.minecraftforge.server.permission.context.PlayerContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author LatvianModder
 */
public class CommandOverride implements ICommandWithParent
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
	private ICommand parent;

	private CommandOverride(ICommand c, Node parent)
	{
		mirrored = c;
		node = mirrored instanceof ICommandWithCustomPermission ? ((ICommandWithCustomPermission) mirrored).getCustomPermissionNode() : parent.append(mirrored.getName());
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
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		if (sender instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP) sender;
			Event.Result result = FTBUtilitiesPermissionHandler.INSTANCE.getPermissionResult(player.getGameProfile(), node.toString(), new PlayerContext(player));

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
		return getName().compareTo(o.getName());
	}

	@Override
	public void setParent(@Nullable ICommand c)
	{
		parent = c;
	}

	@Nullable
	@Override
	public ICommand getParent()
	{
		return parent;
	}
}