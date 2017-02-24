package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.internal.FTBLibLang;
import com.feed_the_beast.ftbu.FTBUPermissions;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdUnloadAll extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "unload_all";
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

        checkArgs(args, 1, "<all_dimensions> [player]");

        IForgePlayer p;

        if(args.length >= 2)
        {
            if(!PermissionAPI.hasPermission(ep, FTBUPermissions.CLAIMS_CHUNKS_MODIFY_OTHERS))
            {
                throw FTBLibLang.COMMAND_PERMISSION.commandError();
            }

            p = getForgePlayer(args[1]);
        }
        else
        {
            p = getForgePlayer(ep);
        }

        boolean allDimensions = parseBoolean(args[0]);
        int currentDim = sender.getEntityWorld().provider.getDimension();

        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(p))
        {
            if(!allDimensions || currentDim == chunk.getPos().dim)
            {
                chunk.setLoaded(false);
            }
        }

        LoadedChunkStorage.INSTANCE.checkAll();
        sender.addChatMessage(new TextComponentString("Unloaded " + p.getProfile().getName() + "'s chunks"));
    }
}