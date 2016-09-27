package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.lib.util.LMUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
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

    @Override
    public String getCommandName()
    {
        return parent.getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return parent.getCommandName();
    }

    @Override
    public List<String> getCommandAliases()
    {
        return parent.getCommandAliases();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        parent.execute(server, sender, args);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        LMUtils.DEV_LOGGER.info("FTBU: Checking permission for " + parent.getCommandName());

        if(sender instanceof EntityPlayerMP)
        {
            return Ranks.INSTANCE.getRankOf(((EntityPlayerMP) sender).getGameProfile()).allowCommand(server, sender, parent);
        }

        return parent.checkPermission(server, sender);
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return parent.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return parent.isUsernameIndex(args, index);
    }

    @Override
    public int compareTo(ICommand o)
    {
        return getCommandName().compareToIgnoreCase(o.getCommandName());
    }
}
