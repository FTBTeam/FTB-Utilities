package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.api.IRank;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdSetRank extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "setrank";
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
            return getListOfStringsMatchingLastWord(args, Ranks.INSTANCE.RANKS.keySet());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        if(Ranks.INSTANCE.defaultRank == null)
        {
            throw FTBLibLang.RAW.commandError("Ranks are not enabled!"); //TODO: Lang
        }

        checkArgs(args, 2, "<player> <rank>");
        IForgePlayer player = getForgePlayer(args[0]);
        IRank r = Ranks.INSTANCE.RANKS.get(args[1]);

        if(r == null)
        {
            throw FTBLibLang.RAW.commandError("Rank '" + args[1] + "' not found!"); //TODO: Lang
        }

        Ranks.INSTANCE.PLAYER_MAP.put(player.getProfile().getId(), r);
        Ranks.INSTANCE.saveRanks();
    }
}
