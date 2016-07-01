package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.permissions.Context;
import com.feed_the_beast.ftbl.api.permissions.PermissionAPI;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public class CmdHome extends CommandLM
{
    public CmdHome()
    {
        super("home");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Nonnull
    @Override
    public String getCommandUsage(@Nonnull ICommandSender ics)
    {
        return '/' + commandName + " <ID>";
    }

    @Nonnull
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if(args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FTBUPlayerData.get(ForgeWorldMP.inst.getPlayer(sender)).toMP().listHomes());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(ep)).toMP();
        checkArgs(args, 1, "<home>");

        if(args[0].equals("list"))
        {
            Collection<String> list = d.listHomes();
            ics.addChatMessage(new TextComponentString(list.size() + " / " + FTBUPermissions.HOMES_MAX.get(ep.getGameProfile()) + ": "));
            if(!list.isEmpty())
            {
                ics.addChatMessage(new TextComponentString(LMStringUtils.strip(list)));
            }
            return;
        }

        BlockDimPos pos = d.getHome(args[0]);

        if(pos == null)
        {
            throw FTBULang.home_not_set.commandError(args[0]);
        }

        if(ep.dimension != pos.getDim() && !PermissionAPI.hasPermission(ep.getGameProfile(), FTBUPermissions.HOMES_CROSS_DIM, true, new Context(ep)))
        {
            throw FTBULang.home_cross_dim.commandError();
        }

        LMDimUtils.teleportPlayer(ep, pos);
        FTBULang.warp_tp.printChat(ics, args[0]);
    }
}