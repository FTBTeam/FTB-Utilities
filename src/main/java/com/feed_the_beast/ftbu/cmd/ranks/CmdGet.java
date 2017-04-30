package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdGet extends CmdBase
{
    public CmdGet()
    {
        super("get", Level.OP);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<player>");
        IForgePlayer p = getForgePlayer(args[0]);
        IRank rank = FTBUtilitiesAPI_Impl.INSTANCE.getRank(p.getProfile());
        ITextComponent name = new TextComponentString(rank.getName() + " - " + rank.getFormattedName(p.getName()));
        name.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + p.getName() + " "));
        name.getStyle().setInsertion(p.getName());
        sender.sendMessage(name);
    }
}
