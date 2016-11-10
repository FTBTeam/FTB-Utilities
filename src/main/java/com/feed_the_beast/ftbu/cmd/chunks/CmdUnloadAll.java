package com.feed_the_beast.ftbu.cmd.chunks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbu.api.chunks.IClaimedChunk;
import com.feed_the_beast.ftbu.api_impl.ClaimedChunkStorage;
import com.feed_the_beast.ftbu.api_impl.LoadedChunkStorage;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdUnloadAll extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "admin_unload_all";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1, "<player>");
        IForgePlayer p = getForgePlayer(args[0]);

        for(IClaimedChunk chunk : ClaimedChunkStorage.INSTANCE.getChunks(p))
        {
            chunk.setLoaded(false);
        }

        LoadedChunkStorage.INSTANCE.checkAll();
        ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
    }
}