package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CmdUnloadAll extends CommandLM
{
    public CmdUnloadAll()
    {
        super("unload_all_chunks");
    }

    @Nonnull
    @Override
    public String getCommandUsage(@Nonnull ICommandSender ics)
    {
        return '/' + commandName + " <player | @a>";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    {
        return i == 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        checkArgs(args, 1);

        if(args[0].equals("@a"))
        {
            for(ClaimedChunk c : FTBUWorldDataMP.chunks.getAllChunks())
            {
                c.loaded = false;
            }
            for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
            {
                p.toMP().sendUpdate();
            }
            ics.addChatMessage(new TextComponentString("Unloaded all chunks")); //TODO: Lang
            return;
        }

        ForgePlayerMP p = ForgePlayerMP.get(args[0]);
        for(ClaimedChunk c : FTBUWorldDataMP.chunks.getChunks(p.getProfile().getId()))
        {
            c.loaded = false;
        }
        if(p.isOnline())
        {
            p.sendUpdate();
        }
        ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
    }
}