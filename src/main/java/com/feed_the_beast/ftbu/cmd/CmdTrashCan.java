package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.server.MinecraftServer;

public class CmdTrashCan extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "trash_can";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        getCommandSenderAsPlayer(sender).displayGUIChest(new InventoryBasic("Trash Can", true, 18));
    }
}