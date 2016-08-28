package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.FTBLibAPI;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.data.FTBUWorldData;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.util.LMDimUtils;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
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
            return getListOfStringsMatchingLastWord(args, FTBUWorldData.getW(FTBLibAPI.get().getUniverse()).toMP().listWarps());
        }

        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp>");

        args[0] = args[0].toLowerCase();

        if(args[0].equals("list"))
        {
            Collection<String> list = FTBUWorldData.getW(FTBLibAPI.get().getUniverse()).toMP().listWarps();
            ics.addChatMessage(new TextComponentString(list.isEmpty() ? "-" : LMStringUtils.strip(list)));
            return;
        }

        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        BlockDimPos p = FTBUWorldData.getW(FTBLibAPI.get().getUniverse()).toMP().getWarp(args[0]);
        if(p == null)
        {
            throw FTBULang.WARP_NOT_SET.commandError(args[0]);
        }
        LMDimUtils.teleportPlayer(ep, p);
        FTBULang.WARP_TP.printChat(ics, args[0]);
    }
}