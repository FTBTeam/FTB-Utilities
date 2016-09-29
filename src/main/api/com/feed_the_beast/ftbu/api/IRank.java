package com.feed_the_beast.ftbu.api;

import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IJsonSerializable;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public interface IRank extends IStringSerializable, IJsonSerializable
{
    @Nullable
    IRank getParent();

    Event.Result hasPermission(String permission);

    IConfigValue getConfig(IConfigKey id);

    default boolean allowCommand(MinecraftServer server, ICommandSender sender, ICommand command)
    {
        switch(hasPermission("command." + command.getCommandName()))
        {
            case ALLOW:
                return true;
            case DENY:
                return false;
            default:
            {
                IRank parent = getParent();
                return parent == null ? command.checkPermission(server, sender) : parent.allowCommand(server, sender, command);
            }
        }
    }
}