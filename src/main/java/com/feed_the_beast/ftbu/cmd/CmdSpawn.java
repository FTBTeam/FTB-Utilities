package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CommandLM;
import com.feed_the_beast.ftbl.lib.math.BlockDimPos;
import com.feed_the_beast.ftbl.lib.util.LMServerUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CmdSpawn extends CommandLM
{
    @Override
    public String getCommandName()
    {
        return "spawn";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
    {
        EntityPlayerMP player = getCommandSenderAsPlayer(ics);
        World w = server.getEntityWorld();
        BlockPos spawnpoint = w.getSpawnPoint();

        while(w.getBlockState(spawnpoint).isFullCube())
        {
            spawnpoint = spawnpoint.up(2);
        }

        LMServerUtils.teleportPlayer(player, new BlockDimPos(spawnpoint, 0));
        FTBULang.WARP_SPAWN.printChat(ics);
    }
}