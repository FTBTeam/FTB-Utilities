package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import com.feed_the_beast.ftblib.lib.util.NBTUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

/**
 * @author LatvianModder
 */
public class CmdFly extends CmdBase
{
	public CmdFly()
	{
		super("fly", Level.OP);
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		return index == 0;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		NBTTagCompound nbt = NBTUtils.getPersistedData(player, true);

		if (nbt.getBoolean("fly"))
		{
			nbt.removeTag("fly");
			player.capabilities.allowFlying = false;
			player.capabilities.isFlying = false;
		}
		else
		{
			nbt.setBoolean("fly", true);
			player.capabilities.allowFlying = true;
		}

		player.sendPlayerAbilities();
	}
}