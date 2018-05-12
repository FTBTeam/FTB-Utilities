package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.server.command.TextComponentHelper;

/**
 * @author LatvianModder
 */
public class CmdTPAccept extends CmdBase
{
	public CmdTPAccept()
	{
		super("tpaccept", Level.ALL);
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
		EntityPlayerMP selfPlayer = getCommandSenderAsPlayer(sender);
		FTBUtilitiesPlayerData self = FTBUtilitiesPlayerData.get(getForgePlayer(selfPlayer));
		FTBUtilitiesPlayerData other = FTBUtilitiesPlayerData.get(getForgePlayer(sender, args[0]));

		ITextComponent selfName = other.player.getPlayer().getDisplayName();
		selfName.getStyle().setColor(TextFormatting.BLUE);

		ITextComponent otherName = other.player.getPlayer().getDisplayName();
		otherName.getStyle().setColor(TextFormatting.BLUE);

		if (self.player.equalsPlayer(other.player) || !other.player.isOnline() || !self.tpaRequestsFrom.contains(other.player))
		{
			throw new CommandException("ftbutilities.lang.tpa.no_request", otherName);
		}

		self.tpaRequestsFrom.remove(other.player);
		other.updateLastTPA();
		ITextComponent component = TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.tpa.request_accepted");
		component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.tpa.from_to", selfName, otherName)));
		sender.sendMessage(component);

		component = TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.request_accepted");
		component.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.from_to", selfName, otherName)));
		other.player.getPlayer().sendMessage(component);

		ServerUtils.teleportEntity(selfPlayer.mcServer, other.player.getPlayer(), selfPlayer.posX, selfPlayer.posY, selfPlayer.posZ, selfPlayer.dimension);
	}
}