package com.feed_the_beast.ftbu.world.backups;

import com.feed_the_beast.ftbu.api.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.latmod.lib.BroadcastSender;
import com.latmod.lib.util.LMJsonUtils;
import com.latmod.lib.util.LMServerUtils;
import com.latmod.lib.util.LMUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public enum Backups
{
    INSTANCE;

    public static final Logger logger = LogManager.getLogger("FTBU_Backups");
    public final List<Backup> backups = new ArrayList<>();
    public File backupsFolder;
    public long nextBackup = -1L;
    public ThreadBackup thread;

    public void init()
    {
        backupsFolder = FTBUConfigBackups.folder.getAsString().isEmpty() ? new File(LMUtils.folderMinecraft, "/backups/") : new File(FTBUConfigBackups.folder.getAsString());
        thread = null;

        backups.clear();

        JsonElement element = LMJsonUtils.fromJson(new File(backupsFolder, "backups.json"));

        if(element.isJsonArray())
        {
            try
            {
                for(JsonElement e : element.getAsJsonArray())
                {
                    backups.add(new Backup(e.getAsJsonObject()));
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else if(backupsFolder.exists())
        {
            String[] files = backupsFolder.list();
            int index = 0;

            if(files != null)
            {
                for(String s : files)
                {
                    String[] s1 = s.split("-");

                    if(s1.length >= 6)
                    {
                        int year = Integer.parseInt(s1[0]);
                        int month = Integer.parseInt(s1[1]);
                        int day = Integer.parseInt(s1[2]);
                        int hours = Integer.parseInt(s1[3]);
                        int minutes = Integer.parseInt(s1[4]);
                        int seconds = Integer.parseInt(s1[5]);

                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day, hours, minutes, seconds);

                        if(FTBUConfigBackups.compression_level.getAsInt() > 0)
                        {
                            s += "/backup.zip";
                        }

                        backups.add(new Backup(c.getTimeInMillis(), s, ++index, true));
                    }
                }
            }
        }

        cleanupAndSave();
        logger.info("Backups folder - " + backupsFolder.getAbsolutePath());
    }

    public boolean run(ICommandSender ics)
    {
        if(thread != null)
        {
            return false;
        }
        boolean auto = !(ics instanceof EntityPlayerMP);

        if(auto && !FTBUConfigBackups.enabled.getAsBoolean())
        {
            return false;
        }

        MinecraftServer server = LMServerUtils.getServer();
        World w = server.getEntityWorld();

        ITextComponent c = FTBULang.BACKUP_START.textComponent(ics.getName());
        c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
        BroadcastSender.INSTANCE.addChatMessage(c);

        nextBackup = System.currentTimeMillis() + FTBUConfigBackups.backupMillis();

        try
        {
            new CommandSaveOff().execute(server, server, new String[0]);
            new CommandSaveAll().execute(server, server, new String[0]);
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

    public void cleanupAndSave()
    {
        JsonArray a = new JsonArray();

        if(!backups.isEmpty())
        {
            Collections.sort(backups, Backup.COMPARATOR);

            int size = FTBUConfigBackups.backups_to_keep.getAsInt() - backups.size();

            for(int i = 0; i < backups.size(); i++)
            {
                System.out.println(backups.get(i).time + ": " + backups.get(i).fileID);
            }

            if(size > 0)
            {
                size = Math.min(size, backups.size());

                for(int i = 0; i < size; i++)
                {
                    System.out.println("Deleting " + backups.get(i).fileID);
                }
            }

            for(Backup t : backups)
            {
                a.add(t.toJsonObject());
            }
        }

        LMJsonUtils.toJson(new File(backupsFolder, "backups.json"), a);
    }

    public void postBackup()
    {
        try
        {
            MinecraftServer server = LMServerUtils.getServer();
            new CommandSaveOn().execute(server, server, new String[0]);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    int getLastIndex()
    {
        int i = 0;

        for(Backup b : backups)
        {
            i = Math.max(i, b.index);
        }

        return i;
    }
}