package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.data.FTBUWorldData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public class CmdDelWarp extends CommandLM
{
    public CmdDelWarp()
    {
        super("delwarp");
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
            return getListOfStringsMatchingLastWord(args, FTBUWorldData.getW(FTBLibAPI.get().getWorld()).toMP().listWarps());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp>");

        args[0] = args[0].toLowerCase();

        if(FTBUWorldData.getW(FTBLibAPI.get().getWorld()).toMP().setWarp(args[0], null))
        {
            FTBULang.WARP_DEL.printChat(ics, args[0]);
        }
        else
        {
            throw FTBULang.WARP_NOT_SET.commandError(args[0]);
        }
    }
}