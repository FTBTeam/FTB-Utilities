package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelHome extends CommandLM
{
    public CmdDelHome()
    {
        super("delhome");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + commandName + " <ID>";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibAPI.get().getUniverse().getPlayer(sender)).listHomes());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        IForgePlayer p = getForgePlayer(ics);
        checkArgs(args, 1, "<home>");

        args[0] = args[0].toLowerCase();

        if(FTBUPlayerData.get(p).setHome(args[0], null))
        {
            FTBULang.HOME_DEL.printChat(ics, args[0]);
        }
        else
        {
            throw FTBULang.HOME_NOT_SET.commandError(args[0]);
        }
    }
}