package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CmdDelWarp extends CommandLM
{
    public CmdDelWarp()
    {
        super("delwarp", CommandLevel.OP);
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + commandName + " <ID>";
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUWorldData.getW(ForgeWorldMP.inst).toMP().listWarps());
        }

        return super.getTabCompletionOptions(server, ics, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1);

        if(FTBUWorldData.getW(ForgeWorldMP.inst).toMP().setWarp(args[0], null))
        {
            FTBULang.warp_del.printChat(ics, args[0]);
        }
        else
        {
            throw FTBULang.warp_not_set.commandError(args[0]);
        }
    }
}