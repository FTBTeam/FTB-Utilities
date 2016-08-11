package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.latmod.lib.math.BlockDimPos;
import com.latmod.lib.util.LMDimUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;

public class CmdTplast extends CommandLM
{
    public CmdTplast()
    {
        super("tpl");
    }

    @Nonnull
    @Override
    public String getCommandUsage(@Nonnull ICommandSender ics)
    {
        return '/' + commandName + " [who] <to>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        checkArgs(args, 1, "(<x> <y> <z>) | ([who] <player>)");

        if(args.length == 3)
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
            double x = parseDouble(ep.posX, args[0], -30000000, 30000000, true);
            double y = parseDouble(ep.posY, args[1], -30000000, 30000000, true);
            double z = parseDouble(ep.posZ, args[2], -30000000, 30000000, true);
            LMDimUtils.teleportPlayer(ep, new Vec3d(x, y, z), ep.dimension);
            return;
        }

        EntityPlayerMP who;
        IForgePlayer to;

        if(args.length == 1)
        {
            who = getCommandSenderAsPlayer(ics);
            to = getForgePlayer(args[0]);
        }
        else
        {
            who = getPlayer(server, ics, args[0]);
            to = getForgePlayer(args[1]);
        }

        BlockDimPos p = to.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null) ? to.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null).lastPos : null;
        if(p == null)
        {
            throw FTBLibLang.raw.commandError("No last position!");
        }

        LMDimUtils.teleportPlayer(who, p);
    }
}