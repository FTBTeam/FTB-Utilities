package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdUnclaimAll extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "unclaim_all";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);

        checkArgs(args, 1, "<all_dimensions> [player]");

        IForgePlayer p;

        if(args.length >= 2)
        {
            if(!PermissionAPI.hasPermission(ep, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
            {
                throw new CommandException("commands.generic.permission");
            }

            p = getForgePlayer(args[1]);
        }
        else
        {
            p = getForgePlayer(ep);
        }

        FTBUUniverseData.unclaimAllChunks(p, parseBoolean(args[0]) ? null : ep.dimension);
        FTBLibIntegration.API.sendNotification(ep, FTBUNotifications.UNCLAIMED_ALL);
    }
}