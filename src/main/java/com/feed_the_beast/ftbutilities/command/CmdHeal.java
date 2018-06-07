package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.command.CommandUtils;
import com.feed_the_beast.ftbutilities.FTBUtilitiesPermissions;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdHeal extends CmdBase
{
	public CmdHeal()
	{
		super("heal", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = CommandUtils.getSelfOrOther(sender, args, 0, FTBUtilitiesPermissions.HEAL_OTHER).getCommandPlayer(sender);
		player.setHealth(player.getMaxHealth());
		player.getFoodStats().addStats(40, 40F);
		player.extinguish();
	}
}