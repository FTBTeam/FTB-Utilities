package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.ICustomPermission;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LatvianModder on 21.02.2016.
 */
public class CmdOverride implements ICommand
{
    public final ICommand parent;
    public final String permissionNode;

    public CmdOverride(ICommand c, String pn)
    {
        parent = c;
        if(c instanceof ICustomPermission)
        {
            String s = ((ICustomPermission) c).getCustomPermission();

            if(!s.isEmpty())
            {
                pn = s;
            }
        }
        permissionNode = pn;

        if(c instanceof CommandTreeBase)
        {
            CommandTreeBase tree = (CommandTreeBase) c;

            for(ICommand command : new ArrayList<>(tree.getSubCommands()))
            {
                tree.addSubcommand(new CmdOverride(command, permissionNode + "." + command.getName()));
            }
        }
    }

    @Override
    public String getName()
    {
        return parent.getName();
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return parent.getUsage(sender);
    }

    @Override
    public List<String> getAliases()
    {
        return parent.getAliases();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        parent.execute(server, sender, args);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return Ranks.checkCommandPermission(server, sender, parent, permissionNode);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        return parent.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return parent.isUsernameIndex(args, index);
    }

    @Override
    public int compareTo(ICommand o)
    {
        return parent.compareTo(o instanceof CmdOverride ? ((CmdOverride) o).parent : o);
    }
}
