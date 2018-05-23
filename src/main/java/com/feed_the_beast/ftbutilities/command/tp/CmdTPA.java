package com.feed_the_beast.ftbutilities.command.tp;

import com.feed_the_beast.ftblib.FTBLib;
import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.math.Ticks;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

/**
 * @author LatvianModder
 */
public class CmdTPA extends CmdBase
{
	public CmdTPA()
	{
		super("tpa", Level.ALL);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		checkArgs(sender, args, 1);
		FTBUtilitiesPlayerData self = FTBUtilitiesPlayerData.get(getForgePlayer(sender));

		long cooldown = self.getTeleportCooldown(FTBUtilitiesPlayerData.Timer.TPA);

		if (cooldown > 0)
		{
			throw new CommandException("cant_use_now_cooldown", StringUtils.getTimeStringTicks(cooldown));
		}

		FTBUtilitiesPlayerData other = FTBUtilitiesPlayerData.get(getForgePlayer(sender, args[0]));

		ITextComponent selfName = StringUtils.color(self.player.getPlayer().getDisplayName(), TextFormatting.BLUE);
		ITextComponent otherName = StringUtils.color(other.player.getPlayer().getDisplayName(), TextFormatting.BLUE);

		if (self.player.equalsPlayer(other.player) || !other.player.isOnline() || other.tpaRequestsFrom.contains(self.player))
		{
			ITextComponent component = FTBUtilities.lang(sender, "ftbutilities.lang.tpa.cant_request");
			component.getStyle().setColor(TextFormatting.RED);
			component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBUtilities.lang(sender, "ftbutilities.lang.tpa.from_to", selfName, otherName)));
			sender.sendMessage(component);
			return;
		}

		ITextComponent c = FTBUtilities.lang(sender, "ftbutilities.lang.tpa.request_sent");
		c.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBUtilities.lang(sender, "ftbutilities.lang.tpa.from_to", selfName, otherName)));
		sender.sendMessage(c);

		other.tpaRequestsFrom.add(self.player);

		ITextComponent accept = FTBLib.lang(other.player.getPlayer(), "click_here");
		accept.getStyle().setColor(TextFormatting.GOLD);
		accept.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb tpaccept " + self.player.getName()));
		accept.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("/ftb tpaccept " + self.player.getName())));

		other.player.getPlayer().sendMessage(FTBUtilities.lang(other.player.getPlayer(), "ftbutilities.lang.tpa.request_received", otherName, accept));

		Universe.get().scheduleTask(server.getWorld(0).getTotalWorldTime() + Ticks.st(30L), universe -> {
			if (other.tpaRequestsFrom.remove(self.player))
			{
				ITextComponent component = FTBUtilities.lang(sender, "ftbutilities.lang.tpa.request_expired");
				component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBUtilities.lang(sender, "ftbutilities.lang.tpa.from_to", selfName, otherName)));
				sender.sendMessage(component);

				component = FTBUtilities.lang(other.player.getPlayer(), "ftbutilities.lang.tpa.request_expired");
				component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, FTBUtilities.lang(other.player.getPlayer(), "ftbutilities.lang.tpa.from_to", selfName, otherName)));
				other.player.getPlayer().sendMessage(component);
			}
		});
	}
}