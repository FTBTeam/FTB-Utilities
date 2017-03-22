package com.feed_the_beast.ftbu.cmd.ranks;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.server.command.CommandTreeBase;

/**
 * Created by LatvianModder on 27.06.2016.
 */
public class CmdRanks extends CommandTreeBase
{
    public CmdRanks()
    {
        addSubcommand(new CmdAdd());
        addSubcommand(new CmdGet());
        addSubcommand(new CmdSet());
        //addSubcommand(new CmdEdit());
        addSubcommand(new CmdCheckPerm());
    }

    @Override
    public String getName()
    {
        return "ranks";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "command.ftb.ranks.usage";
    }
}
