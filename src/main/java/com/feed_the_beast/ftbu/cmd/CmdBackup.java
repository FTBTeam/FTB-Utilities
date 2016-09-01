package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.api.cmd.CommandLM;
import com.feed_the_beast.ftbl.api.cmd.CommandTreeBase;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.world.backups.Backups;
import com.latmod.lib.BroadcastSender;
import com.latmod.lib.util.LMFileUtils;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CmdBackup extends CommandTreeBase
{
    public static class CmdBackupStart extends CommandLM
    {
        public CmdBackupStart()
        {
            super("start");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            boolean b = Backups.INSTANCE.run(ics);
            if(b)
            {
                FTBULang.BACKUP_MANUAL_LAUNCH.printChat(BroadcastSender.INSTANCE, ics.getName());

                if(!FTBUConfigBackups.use_separate_thread.getAsBoolean())
                {
                    Backups.INSTANCE.postBackup();
                }
            }
            else
            {
                FTBULang.BACKUP_ALREADY_RUNNING.printChat(ics);
            }
        }
    }

    public static class CmdBackupStop extends CommandLM
    {
        public CmdBackupStop()
        {
            super("stop");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            if(Backups.INSTANCE.thread != null)
            {
                Backups.INSTANCE.thread.interrupt();
                Backups.INSTANCE.thread = null;
                FTBULang.BACKUP_STOP.printChat(ics);
                return;
            }

            throw FTBULang.BACKUP_NOT_RUNNING.commandError();
        }
    }

    public static class CmdBackupGetSize extends CommandLM
    {
        public CmdBackupGetSize()
        {
            super("getsize");
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender ics, String[] args) throws CommandException
        {
            String sizeW = LMFileUtils.getSizeS(ics.getEntityWorld().getSaveHandler().getWorldDirectory());
            String sizeT = LMFileUtils.getSizeS(Backups.INSTANCE.backupsFolder);
            FTBULang.BACKUP_SIZE.printChat(ics, sizeW, sizeT);
        }
    }

    public CmdBackup()
    {
        super("backup");
        add(new CmdBackupStart());
        add(new CmdBackupStop());
        add(new CmdBackupGetSize());
    }
}