package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.util.StringJoiner;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class CmdNick extends CmdBase
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
			player.sendStatusMessage(FTBUtilities.lang(player, "ftbutilities.lang.nickname_reset"), true);
		}
		else
		{
			player.sendStatusMessage(FTBUtilities.lang(player, "ftbutilities.lang.nickname_changed", data.getNickname()), true);
		}
	}
}