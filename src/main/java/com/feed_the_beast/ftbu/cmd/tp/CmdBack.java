package com.feed_the_beast.ftbu.cmd.tp;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.world.FTBUPlayerData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

public class CmdBack extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "back";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        IForgePlayer p = getForgePlayer(ep);

        FTBUPlayerData data = FTBUPlayerData.get(p);

        if(data == null)
        {
            return;
        }
        else if(data.lastDeath == null)
        {
            throw FTBULang.WARP_NO_DP.commandError();
        }

        LMServerUtils.teleportPlayer(ep, data.lastDeath);

        if(!PermissionAPI.hasPermission(ep, FTBUPermissions.INFINITE_BACK_USAGE))
        {
            data.lastDeath = null;
        }
    }
}