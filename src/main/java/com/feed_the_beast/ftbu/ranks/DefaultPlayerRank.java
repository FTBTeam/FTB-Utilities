package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbu.api.IRank;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public class DefaultPlayerRank extends Rank
{
    public static final DefaultPlayerRank INSTANCE = new DefaultPlayerRank();

    DefaultPlayerRank()
    {
        super("player");
    }

    @Nullable
    @Override
    public IRank getParent()
    {
        return null;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        return Event.Result.DEFAULT;
    }

    @Override
    public IConfigValue getConfig(IConfigKey id)
    {
        return id.getDefValue();
    }

    @Override
    public boolean allowCommand(MinecraftServer server, ICommandSender sender, ICommand command)
    {
        return command.checkPermission(server, sender);
    }
}