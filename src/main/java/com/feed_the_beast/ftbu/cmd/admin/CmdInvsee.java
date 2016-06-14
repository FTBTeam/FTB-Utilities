package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.cmd.InvSeeInventory;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class CmdInvsee extends CommandLM
{
    public CmdInvsee()
    {
        super("invsee");
    }

    @Nonnull
    @Override
    public String getCommandUsage(@Nonnull ICommandSender ics)
    {
        return '/' + commandName + " <player>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        checkArgs(args, 1);
        EntityPlayerMP ep0 = getCommandSenderAsPlayer(ics);
        EntityPlayerMP ep = getPlayer(server, ics, args[0]);
        ep0.displayGUIChest(new InvSeeInventory(ep));
    }
}