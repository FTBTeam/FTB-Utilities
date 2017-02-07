package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdGet extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "get";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<player>");
        IForgePlayer p = getForgePlayer(args[0]);
        IRank r = FTBUtilitiesAPI_Impl.INSTANCE.getRank(p.getProfile());
        ITextComponent c = new TextComponentString(r.getDisplayName());
        c.getStyle().setColor(r.getColor());
        c.appendText(" [" + r.getName() + "]");
        ics.addChatMessage(c);
    }
}
