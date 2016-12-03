package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Created by LatvianModder on 25.11.2016.
 */
public class CmdAdd extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "add";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<id> [parent]");
    }
}