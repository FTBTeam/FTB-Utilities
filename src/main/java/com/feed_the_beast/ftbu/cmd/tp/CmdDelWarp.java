package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelWarp extends CmdBase
{
    @Override
    public String getName()
    {
        return "delwarp";
    }

    @Override
    public String getUsage(ICommandSender ics)
    {
        return '/' + getName() + " <ID>";
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUUniverseData.get().listWarps());
        }

        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp>");

        args[0] = args[0].toLowerCase();

        if(FTBUUniverseData.get().setWarp(args[0], null))
        {
            FTBULang.WARP_DEL.printChat(sender, args[0]);
        }
        else
        {
            throw FTBULang.WARP_NOT_SET.commandError(args[0]);
        }
    }
}