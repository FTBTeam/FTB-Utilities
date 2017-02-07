package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.ranks.DefaultOPRank;
import com.feed_the_beast.ftbu.ranks.DefaultPlayerRank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdSet extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "set";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if(args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Ranks.getRankNames());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<player> <rank>");
        IRank r = Ranks.getRank(args[1], null);

        if(r == DefaultPlayerRank.INSTANCE)
        {
            FTBLibLang.RAW.printChat(sender, "Ranks are disabled, forwarding to /deop " + args[0]); //TODO: Lang
            server.getCommandManager().executeCommand(sender, "/deop " + args[0]);
            return;
        }
        else if(r == DefaultOPRank.INSTANCE)
        {
            FTBLibLang.RAW.printChat(sender, "Ranks are disabled, forwarding to /op " + args[0]); //TODO: Lang
            server.getCommandManager().executeCommand(sender, "/op " + args[0]);
            return;
        }

        if(r == null)
        {
            throw FTBLibLang.RAW.commandError("Rank '" + args[1] + "' not found!"); //TODO: Lang
        }

        Ranks.setRank(getForgePlayer(args[0]).getProfile().getId(), r);
    }
}
