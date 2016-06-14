package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.util.FTBLib;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdOverride implements ICommand
{
    public final ICommand parent;

    public CmdOverride(ICommand c)
    {
        parent = c;
    }

    @Nonnull
    @Override
    public String getCommandName()
    {
        return parent.getCommandName();
    }

    @Nonnull
    @Override
    public String getCommandUsage(@Nonnull ICommandSender p_71518_1_)
    {
        return parent.getCommandName();
    }

    @Nonnull
    @Override
    public List<String> getCommandAliases()
    {
        return parent.getCommandAliases();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        parent.execute(server, ics, args);
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics)
    {
        FTBLib.dev_logger.info("FTBU: Checking permission for " + parent.getCommandName());
        if(ics instanceof EntityPlayerMP)
        {
            Rank r = Ranks.instance().getRankOf(((EntityPlayerMP) ics).getGameProfile());
            return r.allowCommand(server, ics, parent);
        }

        return parent.checkPermission(server, ics);
    }

    @Nonnull
    @Override
    public List<String> getTabCompletionOptions(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args, BlockPos pos)
    {
        return parent.getTabCompletionOptions(server, ics, args, pos);
    }

    @Override
    public boolean isUsernameIndex(@Nonnull String[] args, int i)
    {
        return parent.isUsernameIndex(args, i);
    }

    @Override
    public int compareTo(@Nonnull ICommand o)
    {
        return getCommandName().compareToIgnoreCase(o.getCommandName());
    }
}
