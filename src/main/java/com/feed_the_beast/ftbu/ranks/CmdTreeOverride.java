package com.feed_the_beast.ftbu.ranks;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.List;

/**
 * Created by LatvianModder on 01.12.2016.
 */
public class CmdTreeOverride extends CommandTreeBase
{
    public final CommandTreeBase parent;
    public final String permissionNode;

    public CmdTreeOverride(CommandTreeBase c, String pn)
    {
        parent = c;
        permissionNode = pn;

        for(ICommand command : c.getSubCommands())
        {
            if(command instanceof CommandTreeBase)
            {
                addSubcommand(new CmdTreeOverride((CommandTreeBase) command, permissionNode + "." + command.getCommandName()));
            }
            else
            {
                addSubcommand(new CmdOverride(command, permissionNode + "." + command.getCommandName()));
            }
        }
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
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return Ranks.checkCommandPermission(server, sender, parent, permissionNode);
    }
}