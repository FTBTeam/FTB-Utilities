package com.feed_the_beast.ftbu.ranks;

import com.feed_the_beast.ftbu.api_impl.FTBUtilitiesAPI_Impl;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.server.command.CommandTreeBase;

import javax.annotation.Nullable;
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
        if(sender instanceof EntityPlayerMP)
        {
            Event.Result result = FTBUtilitiesAPI_Impl.INSTANCE.getRank(((EntityPlayerMP) sender).getGameProfile()).hasPermission(permissionNode);
            return result == Event.Result.DEFAULT ? parent.checkPermission(server, sender) : (result == Event.Result.ALLOW);
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
}
