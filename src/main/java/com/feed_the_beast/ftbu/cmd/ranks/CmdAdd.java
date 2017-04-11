package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Rank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * Created by LatvianModder on 25.11.2016.
 */
public class CmdAdd extends CommandLM
{
    @Override
    public String getName()
    {
        return "add";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<id> [parent]");

        String id = args[0].toLowerCase();

        if(Ranks.getRankNames().contains(id))
        {
            throw FTBLibLang.RAW.commandError("Rank '" + id + "' already exists!");
        }

        IRank parent = args.length == 1 ? DefaultPlayerRank.INSTANCE : Ranks.getRank(args[1], null);

        if(parent == null)
        {
            throw FTBLibLang.RAW.commandError("Rank '" + id + "' not found!"); //TODO: Lang
        }

        Ranks.addRank(new Rank(id, parent));
    }
}