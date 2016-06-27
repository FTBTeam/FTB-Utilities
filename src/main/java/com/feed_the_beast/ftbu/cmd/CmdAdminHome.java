package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandSubBase;
import com.feed_the_beast.ftbl.util.BlockDimPos;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.feed_the_beast.ftbu.world.FTBUPlayerDataMP;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CmdAdminHome extends CommandSubBase
{
    public static class CmdTP extends CommandLM
    {
        public CmdTP()
        {
            super("tp");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            checkArgs(args, 2, "<player> <home>");
            args[1] = args[1].toLowerCase();
            FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(args[0])).toMP();

            BlockDimPos pos = d.getHome(args[1]);

            if(pos != null)
            {
                LMDimUtils.teleportPlayer(ep, pos);
                FTBULang.warp_tp.printChat(sender, args[1]);
            }

            throw FTBULang.home_not_set.commandError(args[1]);
        }
    }

    public static class CmdList extends CommandLM
    {
        public CmdList()
        {
            super("list");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(args[0])).toMP();
            sender.addChatMessage(new TextComponentString(LMStringUtils.strip(d.listHomes())));
        }
    }

    public static class CmdRem extends CommandLM
    {
        public CmdRem()
        {
            super("remove");
        }

        @Override
        public boolean isUsernameIndex(String[] args, int i)
        {
            return i == 0;
        }

        @Override
        public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
        {
            checkArgs(args, 2, "<player> <home>");
            FTBUPlayerDataMP d = FTBUPlayerData.get(ForgePlayerMP.get(args[0])).toMP();
            args[1] = args[1].toLowerCase();
            BlockDimPos pos = d.getHome(args[1]);

            if(pos != null)
            {
                if(d.setHome(args[1], null))
                {
                    FTBULang.home_del.printChat(sender, args[1]);
                }
            }

            throw FTBULang.home_not_set.commandError(args[1]);
        }
    }

    public CmdAdminHome()
    {
        super("admin_home");
        add(new CmdTP());
        add(new CmdList());
        add(new CmdRem());
    }
}