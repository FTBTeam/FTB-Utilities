package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class CmdTrashCan extends CommandLM
{
    public CmdTrashCan()
    {
        super("trash_can");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        getCommandSenderAsPlayer(ics).displayGUIChest(new InventoryBasic("Trash Can", true, 18));
    }
}