package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdKickme extends CmdBase
{
    public CmdKickme()
    {
        super("kickme", Level.ALL);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(server.isDedicatedServer())
        {
            getCommandSenderAsPlayer(sender).connection.disconnect("You kicked yourself!");
        }
        else
        {
            server.initiateShutdown();
        }
    }
}