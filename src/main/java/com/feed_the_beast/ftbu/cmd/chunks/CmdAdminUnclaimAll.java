package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdAdminUnclaimAll extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "admin_unclaim_all";
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + getCommandName() + " <player | @a>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender ics)
    {
        return !server.isDedicatedServer() || super.checkPermission(server, ics);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<player>");
        IForgePlayer p = getForgePlayer(args[0]);
        FTBUUniverseData.unclaimAllChunks(p, null);
        ics.addChatMessage(new TextComponentString("Unclaimed all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
    }
}