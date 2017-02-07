package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Created by LatvianModder on 30.09.2016.
 */
public class CmdEdit extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "edit";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        throw FTBLibLang.FEATURE_DISABLED.commandError();
    }
}