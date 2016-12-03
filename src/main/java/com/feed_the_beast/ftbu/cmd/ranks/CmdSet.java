package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.ranks.Rank;
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
            if(Ranks.defaultRank != null)
            {
                return getListOfStringsMatchingLastWord(args, Ranks.RANKS.keySet());
            }
            else
            {
                return getListOfStringsMatchingLastWord(args, "op", "player");
            }
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<player> <rank>");

        if(Ranks.defaultRank == null)
        {
            if(args[1].equals("op"))
            {
                FTBLibLang.RAW.printChat(sender, "Ranks are disabled, forwarding to /op " + args[0]); //TODO: Lang
                server.getCommandManager().executeCommand(sender, "/op " + args[0]);
            }
            else
            {
                FTBLibLang.RAW.printChat(sender, "Ranks are disabled, forwarding to /deop " + args[0]); //TODO: Lang
                server.getCommandManager().executeCommand(sender, "/deop " + args[0]);
            }

            return;
        }

        IForgePlayer player = getForgePlayer(args[0]);
        Rank r = Ranks.RANKS.get(args[1]);

        if(r == null)
        {
            throw FTBLibLang.RAW.commandError("Rank '" + args[1] + "' not found!"); //TODO: Lang
        }

        Ranks.PLAYER_MAP.put(player.getProfile().getId(), r);
        Ranks.saveRanks();
    }
}
