package com.feed_the_beast.ftbutilities.command;

import com.feed_the_beast.ftblib.lib.command.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.server.MinecraftServer;

public class CmdTrashCan extends CmdBase
{
	public CmdTrashCan()
	{
		super("trash_can", Level.ALL);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	{
		getCommandSenderAsPlayer(sender).displayGUIChest(new InventoryBasic("Trash Can", true, 36));
	}
}