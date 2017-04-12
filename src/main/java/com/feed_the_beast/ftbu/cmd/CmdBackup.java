package com.feed_the_beast.ftbu.cmd;

import com.feed_the_beast.ftbl.lib.BroadcastSender;
import com.feed_the_beast.ftbl.lib.cmd.CmdBase;
import com.feed_the_beast.ftbl.lib.util.FileUtils;
import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.world.backups.Backups;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

public class CmdBackup extends CommandTreeBase
{
    public static class CmdStart extends CmdBase
    {
        @Override
        public String getName()
        {
            return "start";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            boolean b = Backups.INSTANCE.run(server, sender, args.length == 0 ? "" : args[0]);
            if(b)
            {
                FTBULang.BACKUP_MANUAL_LAUNCH.printChat(BroadcastSender.INSTANCE, sender.getName());

                if(!FTBUConfigBackups.USE_SEPARATE_THREAD.getBoolean())
                {
                    Backups.INSTANCE.postBackup();
                }
            }
            else
            {
                FTBULang.BACKUP_ALREADY_RUNNING.printChat(sender);
            }
        }
    }

    public static class CmdStop extends CmdBase
    {
        @Override
        public String getName()
        {
            return "stop";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if(Backups.INSTANCE.thread != null)
            {
                Backups.INSTANCE.thread.interrupt();
                Backups.INSTANCE.thread = null;
                FTBULang.BACKUP_STOP.printChat(sender);
                return;
            }

            throw FTBULang.BACKUP_NOT_RUNNING.commandError();
        }
    }

    public static class CmdGetSize extends CmdBase
    {
        @Override
        public String getName()
        {
            return "getsize";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            String sizeW = FileUtils.getSizeS(sender.getEntityWorld().getSaveHandler().getWorldDirectory());
            String sizeT = FileUtils.getSizeS(Backups.INSTANCE.backupsFolder);
            FTBULang.BACKUP_SIZE.printChat(sender, sizeW, sizeT);
        }
    }

    public CmdBackup()
    {
        addSubcommand(new CmdStart());
        addSubcommand(new CmdStop());
        addSubcommand(new CmdGetSize());
    }

    @Override
    public String getName()
    {
        return "backup";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "command.ftb.backup.usage";
    }
}