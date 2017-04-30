package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.net.MessageOpenClaimedChunksGui;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * Created by LatvianModder on 26.02.2017.
 */
public class CmdGui extends CmdBase
{
    public CmdGui()
    {
        super("gui", Level.ALL);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
        IForgePlayer player = getForgePlayer(ep);

        if(player.getTeam() == null)
        {
            if(player.isOnline())
            {
                FTBLibIntegration.API.sendNotification(player.getPlayer(), FTBUNotifications.NO_TEAM);
            }

            return;
        }

        new MessageOpenClaimedChunksGui().sendTo(ep);
    }
}