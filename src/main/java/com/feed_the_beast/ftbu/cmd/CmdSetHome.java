package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class CmdSetHome extends CommandLM
{
    public CmdSetHome()
    {
        super("sethome", CommandLevel.ALL);
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
            return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(ForgeWorldMP.inst.getPlayer(ics)).toMP().homes.list());
        }
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        ForgePlayerMP p = ForgePlayerMP.get(ics);
        FTBUPlayerDataMP d = FTBUPlayerData.get(p).toMP();
        checkArgs(args, 1);

        int maxHomes = FTBUPermissions.homes_max.get(p.getProfile()).getAsShort();

        if(maxHomes <= 0 || d.homes.size() >= maxHomes)
        {
            if(maxHomes == 0 || d.homes.get(args[0]) == null)
            {
                throw FTBULang.home_limit.commandError();
            }
        }

        d.homes.set(args[0], p.getPos());
        FTBULang.home_set.printChat(ics, args[0]);
    }
}