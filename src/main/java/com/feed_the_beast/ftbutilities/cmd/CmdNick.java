package com.feed_the_beast.ftbutilities.cmd;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.cmd.ICommandWithCustomPermission;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftblib.lib.util.misc.Node;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.TextComponentHelper;
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
		return sender instanceof EntityPlayerMP && PermissionAPI.hasPermission((EntityPlayerMP) sender, FTBUtilitiesPermissions.NICKNAME);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(getForgePlayer(player));
		data.setNickname(StringJoiner.with(' ').joinObjects((Object[]) args));

		if (data.getNickname().isEmpty())
		{
			Notification.of(Notification.VANILLA_STATUS, TextComponentHelper.createComponentTranslation(player, "ftbutilities.lang.nickname_reset")).send(server, player);
		}
		else
		{
			Notification.of(Notification.VANILLA_STATUS, TextComponentHelper.createComponentTranslation(player, "ftbutilities.lang.nickname_changed", data.getNickname())).send(server, player);
		}
	}

	@Override
	public Node getCustomPermissionNode()
	{
		return Node.get(FTBUtilitiesPermissions.NICKNAME);
	}
}