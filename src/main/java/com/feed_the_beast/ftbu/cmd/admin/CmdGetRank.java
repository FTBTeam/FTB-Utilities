package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbu.ranks.Rank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdGetRank extends CommandLM
{
    public CmdGetRank()
    {
        super("getrank", CommandLevel.OP);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1);
        ForgePlayerMP p = ForgePlayerMP.get(args[0]);
        Rank r = Ranks.instance().getRankOf(p.getProfile());
        ITextComponent c = new TextComponentString(r.getID());
        c.getStyle().setColor(r.color);
        ics.addChatMessage(c);
    }
}
