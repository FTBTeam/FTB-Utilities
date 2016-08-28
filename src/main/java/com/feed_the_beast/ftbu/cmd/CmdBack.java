package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBUCapabilities;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.world.data.FTBUPlayerData;
import com.latmod.lib.util.LMDimUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(ics);
        IForgePlayer p = getForgePlayer(ep);

        if(p.hasCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null))
        {
            FTBUPlayerData d = p.getCapability(FTBUCapabilities.FTBU_PLAYER_DATA, null);

            if(d.lastDeath == null)
            {
                throw FTBULang.WARP_NO_DP.commandError();
            }

            LMDimUtils.teleportPlayer(ep, d.lastDeath);
            //TODO: Add config for infinite times
            d.lastDeath = null;
        }
    }
}