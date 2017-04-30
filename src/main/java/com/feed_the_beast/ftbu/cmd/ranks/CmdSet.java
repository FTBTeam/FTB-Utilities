package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
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
public class CmdSet extends CmdBase
{
    public CmdSet()
    {
        super("set", Level.OP);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if(args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, Ranks.getRankNames());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<player> <rank>");

        IRank r = (args[1].equalsIgnoreCase("none") || args[1].equals("-")) ? null : Ranks.getRank(args[1], null);

        if(r == DefaultPlayerRank.INSTANCE)
        {
            FTBLibLang.RAW.printChat(sender, "Can't set rank as builtin_player, use /deop " + args[0]); //TODO: Lang
            return;
        }
        else if(r == DefaultOPRank.INSTANCE)
        {
            FTBLibLang.RAW.printChat(sender, "Can't set rank as builtin_op, use /op " + args[0]); //TODO: Lang
            return;
        }
        else if(!Ranks.getRankNames().contains(args[1]))
        {
            throw FTBLibLang.RAW.commandError("Rank '" + args[1] + "' not found!"); //TODO: Lang
        }

        IForgePlayer p = getForgePlayer(args[0]);
        Ranks.setRank(p.getId(), r);
        FTBLibLang.RAW.printChat(sender, p.getName() + " now is " + (r == null ? "unset" : r.getName())); //TODO: Lang
    }
}
