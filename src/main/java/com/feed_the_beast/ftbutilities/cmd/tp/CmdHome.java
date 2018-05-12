package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.ForgePlayer;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
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
import net.minecraftforge.server.command.TextComponentHelper;
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
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		if (args.length == 0)
		{
			args = new String[] {"home"};
		}

		if (args[0].equals("list"))
		{
			ForgePlayer senderp = getForgePlayer(sender);
			ForgePlayer p;

			if (args.length >= 2)
			{
				p = getForgePlayer(sender, args[1]);
			}
			else
			{
				p = senderp;
			}

			if (sender instanceof EntityPlayer && !p.equalsPlayer(senderp) && senderp.hasPermission(FTBUtilitiesPermissions.HOMES_TELEPORT_OTHER))
			{
				throw new CommandException("commands.generic.permission");
			}

			FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);

			Collection<String> list = data.homes.list();
			ITextComponent msg = new TextComponentString(p.getName() + ": " + list.size() + " / " + p.getRankConfig(FTBUtilitiesPermissions.HOMES_MAX).getInt() + ": ");

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
			for (ForgePlayer p : Universe.get().getPlayers())
			{
				execute(server, sender, new String[] {"list", p.getName()});
			}

			return;
		}

		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		ForgePlayer p;

		if (args.length >= 2)
		{
			p = getForgePlayer(sender, args[1]);
		}
		else
		{
			p = getForgePlayer(sender);
		}

		if (sender instanceof EntityPlayer && !p.equalsPlayer(getForgePlayer(sender)) && !PermissionAPI.hasPermission((EntityPlayer) sender, FTBUtilitiesPermissions.HOMES_TELEPORT_OTHER))
		{
			throw new CommandException("commands.generic.permission");
		}

		FTBUtilitiesPlayerData data = FTBUtilitiesPlayerData.get(p);
		BlockDimPos pos = data.homes.get(args[0]);

		if (pos == null)
		{
			throw new CommandException("ftbutilities.lang.homes.not_set", args[0]);
		}
		else if (player.dimension != pos.dim && !PermissionAPI.hasPermission(player, FTBUtilitiesPermissions.HOMES_CROSS_DIM))
		{
			throw new CommandException("ftbutilities.lang.homes.cross_dim");
		}

		long cooldown = data.getHomeCooldown();

		if (cooldown > 0)
		{
			throw new CommandException("ftbutilities.lang.homes.cooldown", StringUtils.getTimeStringTicks(cooldown));
		}

		ServerUtils.teleportEntity(player, pos);
		data.updateLastHome();
		sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.warps.tp", args[0]));
	}
}