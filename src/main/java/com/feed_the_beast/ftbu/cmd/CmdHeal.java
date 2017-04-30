package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CmdHeal extends CmdBase
{
    public CmdHeal()
    {
        super("heal", Level.OP);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        EntityPlayerMP ep = args.length >= 1 ? getPlayer(server, sender, args[0]) : getCommandSenderAsPlayer(sender);
        ep.setHealth(ep.getMaxHealth());
        ep.getFoodStats().addStats(40, 40F);
        ep.extinguish();
    }
}