package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import latmod.lib.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.util.Collection;
import java.util.List;

public class CmdHome extends CommandLM
{
    public CmdHome()
    {
        super("home", CommandLevel.ALL);
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

        return super.getTabCompletionOptions(server, ics, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(ep)).toMP();
        checkArgs(args, 1);

        if(args[0].equals("list"))
        {
            Collection<String> list = d.homes.list();
            ics.addChatMessage(new TextComponentString(list.size() + " / " + FTBUPermissions.homes_max.get(ep.getGameProfile()) + ": "));
            if(!list.isEmpty())
            {
                ics.addChatMessage(new TextComponentString(LMStringUtils.strip(list)));
            }
            return;
        }

        BlockDimPos pos = d.homes.get(args[0]);

        if(pos == null)
        {
            throw FTBULang.home_not_set.commandError(args[0]);
        }

        if(ep.dimension != pos.dim && !PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.homes_cross_dim, true))
        {
            throw FTBULang.home_cross_dim.commandError();
        }

        LMDimUtils.teleportPlayer(ep, pos);
        FTBULang.warp_tp.printChat(ics, args[0]);
    }
}