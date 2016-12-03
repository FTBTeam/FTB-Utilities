package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigContainer;
import com.feed_the_beast.ftbl.lib.cmd.CmdEditConfigBase;
import com.feed_the_beast.ftbu.ranks.Ranks;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

/**
 * Created by LatvianModder on 30.09.2016.
 */
public class CmdEdit extends CmdEditConfigBase
{
    @Override
    public String getCommandName()
    {
        return "edit";
    }

    @Override
    public IConfigContainer getConfigContainer(ICommandSender sender) throws CommandException
    {
        return Ranks.RANKS_CONFIG_CONTAINER;
    }
}