package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbl.lib.util.LMStringUtils;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class CmdHome extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "home";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
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
            return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(FTBLibIntegration.API.getUniverse().getPlayer(sender)).listHomes());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        FTBUPlayerData d = FTBUPlayerData.get(getForgePlayer(ep));
        checkArgs(args, 1, "<home>");

        if(args[0].equals("list"))
        {
            Collection<String> list = d.listHomes();
            ics.addChatMessage(new TextComponentString(list.size() + " / " + FTBUtilitiesAPI_Impl.INSTANCE.getRankConfig(ep, FTBUPermissions.HOMES_MAX).getInt() + ": "));
            if(!list.isEmpty())
            {
                ics.addChatMessage(new TextComponentString(LMStringUtils.strip(list)));
            }
            return;
        }

        BlockDimPos pos = d.getHome(args[0]);

        if(pos == null)
        {
            throw FTBULang.HOME_NOT_SET.commandError(args[0]);
        }

        if(ep.dimension != pos.dim && !PermissionAPI.hasPermission(ep, FTBUPermissions.HOMES_CROSS_DIM))
        {
            throw FTBULang.HOME_CROSS_DIM.commandError();
        }

        LMServerUtils.teleportPlayer(ep, pos);
        FTBULang.WARP_TP.printChat(ics, args[0]);
    }
}