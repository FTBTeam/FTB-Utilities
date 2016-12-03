package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdInfo extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "info";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        for(String s : Ranks.INFO)
        {
            ics.addChatMessage(new TextComponentString(s));
        }
    }
}
