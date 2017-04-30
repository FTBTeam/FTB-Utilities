package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbl.lib.util.LMUtils;
import com.feed_the_beast.ftbl.lib.util.ServerUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.File;

public class CmdRestart extends CmdBase
{
    public CmdRestart()
    {
        super("restart", Level.OP);
    }

    public static void restart()
    {
        FileUtils.newFile(new File(LMUtils.folderMinecraft, "autostart.stamp"));
        ServerUtils.getServer().initiateShutdown();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        restart();
    }
}