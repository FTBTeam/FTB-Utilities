package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.text_components.Notification;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesNotifications;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CmdHome extends CmdBase
{
	public CmdHome()
	{
		super("home", Level.ALL);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, FTBUtilitiesPlayerData.get(Universe.get().getPlayer(sender)).homes.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args0) throws CommandException
	{
		if (args0.length == 0)
		{
			args0 = new String[] {"home"};
		}

		String[] args = args0;

		if (args[0].equals("list"))
		{
			ForgePlayer p = CommandUtils.getSelfOrOther(sender, args, 1, FTBUtilitiesPermissions.HOMES_LIST_OTHER);
			FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);

			Collection<String> list = data.homes.list();
			ITextComponent msg = p.getDisplayName().appendText(": " + list.size() + " / " + p.getRankConfig(FTBUtilitiesPermissions.HOMES_MAX).getInt() + ": ");

			if (!list.isEmpty())
			{
				boolean first = true;

				for (String s : list)
				{
					if (first)
					{
						first = false;
					}
					else
					{
						msg.appendText(", ");
					}

					ITextComponent h = new TextComponentString(s);
					h.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("/home " + s + " " + p.getName())));
					h.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/home " + s + " " + p.getName()));
					h.getStyle().setColor(TextFormatting.GOLD);
					msg.appendSibling(h);
				}
			}

			sender.sendMessage(msg);

			return;
		}
		else if (args[0].equals("list_all"))
		{
			for (ForgePlayer p : Universe.get().getPlayers())
			{
				execute(server, sender, new String[] {"list", p.getName()});
			}

			return;
		}

		ForgePlayer p = CommandUtils.getSelfOrOther(sender, args, 1, FTBUtilitiesPermissions.HOMES_TELEPORT_OTHER);
		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);
		BlockDimPos pos = data.homes.get(args[0]);

		if (pos == null)
		{
			throw FTBUtilities.error(sender, "ftbutilities.lang.homes.not_set", args[0]);
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);

		if (player.dimension != pos.dim && !PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.HOMES_CROSS_DIM))
		{
			throw FTBUtilities.error(sender, "ftbutilities.lang.homes.cross_dim");
		}

		data.checkTeleportCooldown(sender, FTBUtilitiesPlayerData.Timer.HOME);
		FTBUtilitiesPlayerData.Timer.HOME.teleport(player, pos, universe -> Notification.of(FTBUtilitiesNotifications.TELEPORT, FTBUtilities.lang(sender, "ftbutilities.lang.warps.tp", args[0])).send(server, player));
	}
}