package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.util.LMServerUtils;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.command.CommandTreeBase;

public class CmdAdminHome extends CommandTreeBase
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
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
            checkArgs(args, 2, "<player> <home>");
            args[1] = args[1].toLowerCase();
            FTBUPlayerData d = FTBUPlayerData.get(getForgePlayer(args[0]));

            BlockDimPos pos = d.getHome(args[1]);

            if(pos != null)
            {
                LMServerUtils.teleportPlayer(ep, pos);
                FTBULang.WARP_TP.printChat(sender, args[1]);
            }

            throw FTBULang.HOME_NOT_SET.commandError(args[1]);
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
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            checkArgs(args, 1, "<player>");
            FTBUPlayerData d = FTBUPlayerData.get(getForgePlayer(args[0]));
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
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            checkArgs(args, 2, "<player> <home>");
            FTBUPlayerData d = FTBUPlayerData.get(getForgePlayer(args[0]));
            args[1] = args[1].toLowerCase();
            BlockDimPos pos = d.getHome(args[1]);

            if(pos != null)
            {
                if(d.setHome(args[1], null))
                {
                    FTBULang.HOME_DEL.printChat(sender, args[1]);
                }
            }

            throw FTBULang.HOME_NOT_SET.commandError(args[1]);
        }
    }

    public CmdAdminHome()
    {
        addSubcommand(new CmdTP());
        addSubcommand(new CmdList());
        addSubcommand(new CmdRem());
    }

    @Override
    public String getCommandName()
    {
        return "admin_home";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "command.ftb.admin_home.usage";
    }
}