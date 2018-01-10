package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.FTBLibLang;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbutilities.FTBULang;
import com.feed_the_beast.ftbutilities.FTBUPermissions;
import com.feed_the_beast.ftbutilities.ranks.Ranks;
import com.feed_the_beast.ftbutilities.util.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
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
			return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(Universe.get().getPlayer(sender)).homes.list());
		}

		return super.getTabCompletions(server, sender, args, pos);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 1;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			args = new String[] {"home"};
		}

		if (args[0].equals("list"))
		{
			ForgePlayer p;

			if (args.length >= 2)
			{
				p = getForgePlayer(args[1]);
			}
			else
			{
				p = getForgePlayer(sender);
			}

			if (sender instanceof EntityPlayer && !p.equalsPlayer(getForgePlayer(sender)) && !PermissionAPI.hasPermission((EntityPlayer) sender, FTBUPermissions.HOMES_LIST_OTHER))
			{
				throw FTBLibLang.COMMAND_PERMISSION.commandError();
			}

			FTBUPlayerData data = FTBUPlayerData.get(p);

			Collection<String> list = data.homes.list();
			ITextComponent msg = new TextComponentString(p.getName() + ": " + list.size() + " / " + Ranks.getRank(p.getProfile()).getConfig(FTBUPermissions.HOMES_MAX).getInt() + ": ");

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
					h.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("/ftb home " + s + " " + p.getName())));
					h.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb home " + s + " " + p.getName()));
					h.getStyle().setColor(TextFormatting.GOLD);
					msg.appendSibling(h);
				}
			}

			sender.sendMessage(msg);

			return;
		}
		else if (args[0].equals("list_all"))
		{
			for (ForgePlayer p : Universe.get().getRealPlayers())
			{
				execute(server, sender, new String[] {"list", p.getName()});
			}

			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p;

		if (args.length >= 2)
		{
			p = getForgePlayer(args[1]);
		}
		else
		{
			p = getForgePlayer(sender);
		}

		if (sender instanceof EntityPlayer && !p.equalsPlayer(getForgePlayer(sender)) && !PermissionAPI.hasPermission((EntityPlayer) sender, FTBUPermissions.HOMES_TELEPORT_OTHER))
		{
			throw FTBLibLang.COMMAND_PERMISSION.commandError();
		}

		FTBUPlayerData data = FTBUPlayerData.get(p);

		BlockDimPos pos = data.homes.get(args[0]);

		if (pos == null)
		{
			throw FTBULang.HOME_NOT_SET.commandError(args[0]);
		}
		else if (player.dimension != pos.dim && !PermissionAPI.hasPermission(player, FTBUPermissions.HOMES_CROSS_DIM))
		{
			throw FTBULang.HOME_CROSS_DIM.commandError();
		}

		ServerUtils.teleportEntity(player, pos);
		FTBULang.WARP_TP.sendMessage(sender, args[0]);
	}
}