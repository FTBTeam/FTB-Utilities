package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbl.util.FTBLib;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.feed_the_beast.ftbu.config.FTBUConfigModules;
import com.latmod.lib.util.LMFileUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;

public class Backups
{
    public static final Logger logger = LogManager.getLogger("FTBU Backups");

    public static File backupsFolder;
    public static long nextBackup = -1L;
    public static ThreadBackup thread = null;
    public static boolean hadPlayer = false;

    public static void init()
    {
        backupsFolder = FTBUConfigBackups.folder.getAsString().isEmpty() ? new File(FTBLib.folderMinecraft, "/backups/") : new File(FTBUConfigBackups.folder.getAsString());
        if(!backupsFolder.exists())
        {
            backupsFolder.mkdirs();
        }
        thread = null;
        clearOldBackups();
        logger.info("Backups folder - " + backupsFolder.getAbsolutePath());
    }

    public static boolean run(ICommandSender ics)
    {
        if(thread != null)
        {
            return false;
        }
        boolean auto = !(ics instanceof EntityPlayerMP);

        if(auto && !FTBUConfigModules.backups.getAsBoolean())
        {
            return false;
        }

        World w = FTBLib.getServerWorld();
        if(w == null)
        {
            return false;
        }

        ITextComponent c = FTBULang.backup_start.textComponent(ics.getName());
        c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        BroadcastSender.inst.addChatMessage(c);

        nextBackup = System.currentTimeMillis() + FTBUConfigBackups.backupMillis();

        if(auto && FTBUConfigBackups.need_online_players.getAsBoolean())
        {
            if(!FTBLib.hasOnlinePlayers() && !hadPlayer)
            {
                return true;
            }
            hadPlayer = false;
        }

        try
        {
            new CommandSaveOff().execute(FTBLib.getServer(), FTBLib.getServer(), new String[0]);
            new CommandSaveAll().execute(FTBLib.getServer(), FTBLib.getServer(), new String[0]);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        File wd = w.getSaveHandler().getWorldDirectory();

        if(FTBUConfigBackups.use_separate_thread.getAsBoolean())
        {
            thread = new ThreadBackup(wd);
            thread.start();
        }
        else
        {
            ThreadBackup.doBackup(wd);
        }

        return true;
    }

    public static void clearOldBackups()
    {
        String[] s = backupsFolder.list();

        if(s.length > FTBUConfigBackups.backups_to_keep.getAsInt())
        {
            Arrays.sort(s);

            int j = s.length - FTBUConfigBackups.backups_to_keep.getAsInt();
            logger.info("Deleting " + j + " old backups");

            for(int i = 0; i < j; i++)
            {
                File f = new File(backupsFolder, s[i]);
                if(f.isDirectory())
                {
                    logger.info("Deleted old backup: " + f.getPath());
                    LMFileUtils.delete(f);
                }
            }
        }
    }

    public static void postBackup()
    {
        try
        {
            new CommandSaveOn().execute(FTBLib.getServer(), FTBLib.getServer(), new String[0]);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}