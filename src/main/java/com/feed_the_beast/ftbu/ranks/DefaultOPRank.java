package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbl.api.config.IConfigKey;
import com.feed_the_beast.ftbl.api.config.IConfigValue;
import com.feed_the_beast.ftbl.api.rankconfig.IRankConfig;
import com.feed_the_beast.ftbu.api.IRank;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * Created by LatvianModder on 27.09.2016.
 */
public class DefaultOPRank extends Rank
{
    public static final DefaultOPRank INSTANCE = new DefaultOPRank();

    DefaultOPRank()
    {
        super("op");
    }

    @Nullable
    @Override
    public IRank getParent()
    {
        return DefaultPlayerRank.INSTANCE;
    }

    @Override
    public Event.Result hasPermission(String permission)
    {
        return Event.Result.ALLOW;
    }

    @Override
    public IConfigValue getConfig(IConfigKey id)
    {
        return (id instanceof IRankConfig) ? ((IRankConfig) id).getDefOPValue() : id.getDefValue();
    }

    @Override
    public boolean allowCommand(MinecraftServer server, ICommandSender sender, ICommand command)
    {
        return true;
    }
}
