package com.feed_the_beast.ftbutilities.cmd.tp;

import com.feed_the_beast.ftblib.lib.cmd.CmdBase;
import com.feed_the_beast.ftblib.lib.math.BlockDimPos;
import com.feed_the_beast.ftblib.lib.util.ServerUtils;
import com.feed_the_beast.ftbutilities.data.FTBUtilitiesPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
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
		FTBUtilitiesPlayerData self = FTBUtilitiesPlayerData.get(getForgePlayer(sender));
		FTBUtilitiesPlayerData other = FTBUtilitiesPlayerData.get(getForgePlayer(sender, args[0]));

		if (self.player.equalsPlayer(other.player) || !other.player.isOnline() || !self.tpaRequestsFrom.containsKey(other.player))
		{
			throw new CommandException("ftbutilities.lang.tpa.no_request", other.player.getName());
		}

		self.tpaRequestsFrom.remove(other.player);
		other.updateLastTPA();
		sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "ftbutilities.lang.tpa.request_accepted", self.player.getName()));
		other.player.getPlayer().sendMessage(TextComponentHelper.createComponentTranslation(other.player.getPlayer(), "ftbutilities.lang.tpa.request_accepted", self.player.getName()));

		ServerUtils.teleportEntity(other.player.getPlayer(), new BlockDimPos(self.player.getPlayer()));
	}
}