package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.ForgePlayerMP;
import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.notification.Notification;
import com.feed_the_beast.ftbu.world.FTBUWorldDataMP;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

public class CmdUnclaimAllChunks extends CommandLM
{
    public CmdUnclaimAllChunks()
    {
        super("unclaim_all");
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException
    {
        EntityPlayerMP ep = getCommandSenderAsPlayer(sender);
        ForgePlayerMP p = ForgePlayerMP.get(ep);

        checkArgs(args, 1, "<all_dimensions>");
        FTBUWorldDataMP.unclaimAllChunks(p, parseBoolean(args[0]) ? null : ep.dimension);
        new Notification("unclaimed_all").addText(new TextComponentString("Unclaimed all chunks")).sendTo(ep); //TODO: Lang
    }
}