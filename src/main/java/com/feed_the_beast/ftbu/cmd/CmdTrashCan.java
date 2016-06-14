package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.item.BasicInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
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
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);

        ep.displayGUIChest(new BasicInventory(18)
        {
            @Nonnull
            @Override
            public String getName()
            {
                return "Trash Can";
            }

            @Override
            public boolean hasCustomName()
            {
                return true;
            }
        });
    }
}