package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.cmd.ICommandWithCustomPermission;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class CmdNick extends CmdBase implements ICommandWithCustomPermission
{
	public CmdNick()
	{
		super("nick", Level.OP);
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	{
		if (sender instanceof EntityPlayerMP)
		{
			return PermissionAPI.hasPermission((EntityPlayerMP) sender, FTBUtilitiesPermissions.NICKNAME);
		}

		return level.checkPermission(server, sender, this);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		FTBUtilitiesPlayerData.get(getForgePlayer(sender)).setNickname(StringJoiner.with(' ').joinObjects((Object[]) args));
	}

	@Override
	public Node getCustomPermissionNode()
	{
		return Node.get(FTBUtilitiesPermissions.NICKNAME);
	}
}