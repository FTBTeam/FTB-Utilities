package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbl.lib.math.ChunkDimPos;
import com.feed_the_beast.ftbl.lib.math.EntityDimPos;
import com.feed_the_beast.ftbu.FTBLibIntegration;
import com.feed_the_beast.ftbu.FTBUNotifications;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.config.FTBUConfigWorld;
import com.feed_the_beast.ftbu.world.FTBUUniverseData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdClaim extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "claim";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(!FTBUConfigWorld.CHUNK_CLAIMING.getBoolean())
        {
            throw FTBLibLang.FEATURE_DISABLED.commandError();
        }

        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        IForgePlayer p = getForgePlayer(player);
        ChunkDimPos pos = new EntityDimPos(player).toBlockDimPos().toChunkPos();

        if(!FTBUConfigWorld.CHUNK_CLAIMING.getBoolean())
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CLAIMING_NOT_ENABLED);
            return;
        }

        if(!PermissionAPI.hasPermission(player.getGameProfile(), FTBUPermissions.CLAIMS_CLAIM_CHUNKS, null))
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CLAIMING_NOT_ALLOWED);
            return;
        }

        if(FTBUUniverseData.claimChunk(p, pos))
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CHUNK_CLAIMED);
            CmdChunks.updateChunk(player, pos);
        }
        else
        {
            FTBLibIntegration.API.sendNotification(player, FTBUNotifications.CANT_MODIFY_CHUNK);
        }

    }
}