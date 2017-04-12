package com.feed_the_beast.ftbu.cmd.ranks;

import com.feed_the_beast.ftbl.api.IForgePlayer;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Created by LatvianModder on 07.02.2017.
 */
public class CmdCheckPerm extends CmdBase
{
    @Override
    public String getName()
    {
        return "check_permission";
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return index == 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        checkArgs(args, 2, "<player> <permission>");
        IForgePlayer player = getForgePlayer(args[0]);
        boolean perm = player.isOnline() ? PermissionAPI.hasPermission(player.getPlayer(), args[1]) : PermissionAPI.hasPermission(player.getProfile(), args[1], null);
        sender.sendMessage(new TextComponentString(args[1] + " for " + player.getName() + " is " + perm));
    }
}