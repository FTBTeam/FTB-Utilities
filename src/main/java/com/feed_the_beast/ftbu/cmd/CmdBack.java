package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.util.LMDimUtils;
import com.feed_the_beast.ftbu.FTBULang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;

public class CmdBack extends CommandLM
{
    public CmdBack()
    {
        super("back");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        ForgePlayerMP p = ForgePlayerMP.get(ep);
        if(p.lastDeath == null)
        {
            throw FTBULang.warp_no_dp.commandError();
        }
        LMDimUtils.teleportPlayer(ep, p.lastDeath);
        p.lastDeath = null;
    }
}