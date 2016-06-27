package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUWorldData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class CmdSetWarp extends CommandLM
{
    public CmdSetWarp()
    {
        super("setwarp");
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        checkArgs(args, 1, "<warp> [x] [y] [z]");
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        BlockPos c;

        args[0] = args[0].toLowerCase();

        if(args.length >= 4)
        {
            int x = parseInt(args[1]);
            int y = parseInt(args[2]);
            int z = parseInt(args[3]);
            c = new BlockPos(x, y, z);
        }
        else
        {
            c = ep.getPosition();
        }

        FTBUWorldData.getW(ForgeWorldMP.inst).toMP().setWarp(args[0], new BlockDimPos(c, ep.dimension));
        FTBULang.warp_set.printChat(ics, args[0]);
    }
}