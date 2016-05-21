package com.feed_the_beast.ftbu.cmd.admin;

import com.feed_the_beast.ftbl.api.ForgePlayer;
import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.ForgeWorldMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandLevel;
import com.feed_the_beast.ftbu.world.ClaimedChunk;
import com.feed_the_beast.ftbu.world.ClaimedChunks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CmdUnloadAll extends CommandLM
{
    public CmdUnloadAll()
    { super("unload_all", CommandLevel.OP); }

    @Override
    public String getCommandUsage(ICommandSender ics)
    { return '/' + commandName + " <player | @a>"; }

    @Override
    public boolean isUsernameIndex(String[] args, int i)
    { return i == 0; }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        checkArgs(args, 1);

        if(args[0].equals("@a"))
        {
            for(ClaimedChunk c : ClaimedChunks.inst.getAllChunks(null))
            {
                c.isChunkloaded = false;
            }
            for(ForgePlayer p : ForgeWorldMP.inst.getOnlinePlayers())
            {
                p.toPlayerMP().sendUpdate();
            }
            ics.addChatMessage(new TextComponentString("Unloaded all chunks")); //TODO: Lang
            return;
        }

        ForgePlayerMP p = ForgePlayerMP.get(args[0]);
        for(ClaimedChunk c : ClaimedChunks.inst.getChunks(p.getProfile().getId(), null))
        {
            c.isChunkloaded = false;
        }
        if(p.isOnline())
        {
            p.sendUpdate();
        }
        ics.addChatMessage(new TextComponentString("Unloaded all " + p.getProfile().getName() + "'s chunks")); //TODO: Lang
    }
}