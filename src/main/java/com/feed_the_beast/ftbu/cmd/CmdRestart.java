package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.latmod.lib.util.LMFileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.io.File;

public class CmdRestart extends CommandLM
{
    public CmdRestart()
    {
        super("restart");
    }

    public static void restart()
    {
        LMFileUtils.newFile(new File(FTBLib.folderMinecraft, "autostart.stamp"));
        FTBLib.getServer().initiateShutdown();
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender ics, @Nonnull String[] args) throws CommandException
    {
        restart();
    }
}