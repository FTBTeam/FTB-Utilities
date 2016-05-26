package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.List;

public class CmdAdminHome extends CommandLM //FIXME: SubCommand
{
    public CmdAdminHome()
    {
        super("home", CommandLevel.OP);
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + commandName + " <player> <sub> [ID]";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender ics, String[] args, BlockPos pos)
    {
        if(args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, "list", "tp", "remove");
        }
        return super.getTabCompletionOptions(server, ics, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 2);
        FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(args[0])).toMP();

        if(args[1].equals("list"))
        {
            ics.addChatMessage(new TextComponentString(LMStringUtils.strip(d.homes.list())));
            return;
        }

        checkArgs(args, 3);

        BlockDimPos pos = d.homes.get(args[2]);
        if(pos == null)
        {
            throw FTBULang.home_not_set.commandError(args[2]);
        }

        if(args[1].equals("tp"))
        {
            LMDimUtils.teleportPlayer(getCommandSenderAsPlayer(ics), pos);
            FTBULang.warp_tp.printChat(ics, args[2]);
            return;
        }
        else if(args[1].equals("remove"))
        {
            if(d.homes.set(args[2], null))
            {
                FTBULang.home_del.printChat(ics, args[2]);
                return;
            }
        }

        throw FTBLibLang.invalid_subcmd.commandError(args[2]);
    }
}