package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.FTBLibLang;
import com.feed_the_beast.ftbl.api_impl.ForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.math.EntityDimPos;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec3d;

public class CmdTplast extends CommandLM
{
    public CmdTplast()
    {
        super("tpl");
    }

    @Override
    public String getCommandUsage(ICommandSender ics)
    {
        return '/' + commandName + " [who] <to>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "(<x> <y> <z>) | ([who] <player>)");

        if(args.length == 3)
        {
            EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
            double x = parseDouble(ep.posX, args[0], -30000000, 30000000, true);
            double y = parseDouble(ep.posY, args[1], -30000000, 30000000, true);
            double z = parseDouble(ep.posZ, args[2], -30000000, 30000000, true);
            LMServerUtils.teleportPlayer(ep, new Vec3d(x, y, z), ep.dimension);
            return;
        }

        EntityPlayerMP who;
        ForgePlayer to;

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

        BlockDimPos p = null;

        if(to.isOnline())
        {
            p = new EntityDimPos(to.getPlayer()).toBlockDimPos();
        }
        else
        {
            NBTTagCompound tag = to.getPlayerNBT();
        }

        if(p == null)
        {
            throw FTBLibLang.RAW.commandError("No last position!");
        }

        LMServerUtils.teleportPlayer(who, p);
    }
}