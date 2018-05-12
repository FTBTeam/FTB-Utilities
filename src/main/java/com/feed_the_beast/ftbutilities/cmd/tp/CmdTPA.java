package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.data.Universe;
import com.feed_the_beast.ftblib.lib.util.CommonUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.command.TextComponentHelper;

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
		FTBUtilitiesPlayerData other = FTBUtilitiesPlayerData.get(getForgePlayer(sender, args[0]));

		if (self.player.equalsPlayer(other.player) || !other.player.isOnline() || other.tpaRequestsFrom.containsKey(self.player))
		{
			throw new CommandException("ftbutilities.lang.tpa.cant_request", other.player.getName());
		}

		sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.tpa.request_sent", other.player.getName()));
		other.player.getPlayer().sendMessage(TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.request_received", self.player.getName()));

		long now = server.getWorld(0).getTotalWorldTime();
		other.tpaRequestsFrom.put(self.player, now);

		ITextComponent accept = TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.accept");
		accept.getStyle().setColor(TextFormatting.DARK_GREEN);
		accept.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb tpaccept " + self.player.getName()));

		ITextComponent deny = TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.deny");
		deny.getStyle().setColor(TextFormatting.RED);
		deny.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ftb tpdeny " + self.player.getName()));

		other.player.getPlayer().sendMessage(TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.question", accept, deny));

		Universe.get().scheduleTask((int) (CommonUtils.TICKS_SECOND * 30), () -> other.tpaRequestsFrom.remove(self.player));
	}
}