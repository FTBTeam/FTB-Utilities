package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
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

public class CmdWarp extends CommandLM
{
    public CmdWarp()
    {
        super("warp");
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
            return getListOfStringsMatchingLastWord(args, FTBUWorldData.getW(ForgeWorldMP.inst).toMP().listWarps());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp>");

        args[0] = args[0].toLowerCase();

        if(args[0].equals("list"))
        {
            Collection<String> list = FTBUWorldData.getW(ForgeWorldMP.inst).toMP().listWarps();
            ics.addChatMessage(new TextComponentString(list.isEmpty() ? "-" : LMStringUtils.strip(list)));
            return;
        }

        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        BlockDimPos p = FTBUWorldData.getW(ForgeWorldMP.inst).toMP().getWarp(args[0]);
        if(p == null)
        {
            throw FTBULang.warp_not_set.commandError(args[0]);
        }
        LMDimUtils.teleportPlayer(ep, p);
        FTBULang.warp_tp.printChat(ics, args[0]);
    }
}