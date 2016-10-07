package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelWarp extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "delwarp";
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + getCommandName() + " <ID>";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUUniverseData.get(FTBLibIntegration.API.getUniverse()).listWarps());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp>");

        args[0] = args[0].toLowerCase();

        if(FTBUUniverseData.get(FTBLibIntegration.API.getUniverse()).setWarp(args[0], null))
        {
            FTBULang.WARP_DEL.printChat(ics, args[0]);
        }
        else
        {
            throw FTBULang.WARP_NOT_SET.commandError(args[0]);
        }
    }
}