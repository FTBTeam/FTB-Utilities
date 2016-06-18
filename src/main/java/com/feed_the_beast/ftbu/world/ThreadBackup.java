package com.feed_the_beast.ftbu.world;

import com.feed_the_beast.ftbl.util.BroadcastSender;
import com.feed_the_beast.ftbu.FTBULang;
import com.feed_the_beast.ftbu.config.FTBUConfigBackups;
import com.latmod.lib.Time;
import com.latmod.lib.math.MathHelperLM;
import com.latmod.lib.util.LMFileUtils;
import com.latmod.lib.util.LMStringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ThreadBackup extends Thread
{
    public boolean isDone = false;
    private File src0;

    public ThreadBackup(File w)
    {
        src0 = w;
        setPriority(7);
    }

    public static void doBackup(File src)
    {
        Time time = Time.now();
        File dstFile = null;

        try
        {
            StringBuilder out = new StringBuilder();
            appendNum(out, time.year, '-');
            appendNum(out, time.month, '-');
            appendNum(out, time.day, '-');
            appendNum(out, time.hours, '-');
            appendNum(out, time.minutes, '-');
            appendNum(out, time.seconds, File.separatorChar);

            List<File> files = LMFileUtils.listAll(src);
            int allFiles = files.size();

            Backups.logger.info("Backing up " + files.size() + " files...");

            if(FTBUConfigBackups.compression_level.getAsInt() > 0)
            {
                out.append("backup.zip");
                dstFile = LMFileUtils.newFile(new File(Backups.backupsFolder, out.toString()));

                long start = System.currentTimeMillis();

                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dstFile));
                //zos.setLevel(9);
                zos.setLevel(FTBUConfigBackups.compression_level.getAsInt());

                long logMillis = System.currentTimeMillis() + 5000L;

                byte[] buffer = new byte[4096];

                Backups.logger.info("Compressing " + allFiles + " files!");

                for(int i = 0; i < allFiles; i++)
                {
                    File file = files.get(i);
                    String filePath = file.getAbsolutePath();
                    ZipEntry ze = new ZipEntry(src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1, filePath.length()));

                    long millis = System.currentTimeMillis();

                    if(i == 0 || millis > logMillis || i == allFiles - 1)
                    {
                        logMillis = millis + 5000L;
                        Backups.logger.info("[" + i + " | " + MathHelperLM.toSmallDouble((i / (double) allFiles) * 100D) + "%]: " + ze.getName());
                    }

                    zos.putNextEntry(ze);
                    FileInputStream fis = new FileInputStream(file);

                    int len;
                    while((len = fis.read(buffer)) > 0)
                    {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                    fis.close();
                }

                zos.close();

                Backups.logger.info("Done compressing in " + getDoneTime(start) + " seconds (" + LMFileUtils.getSizeS(dstFile) + ")!");
            }
            else
            {
                out.append(src.getName());
                dstFile = new File(Backups.backupsFolder, out.toString());
                dstFile.mkdirs();

                String dstPath = dstFile.getAbsolutePath() + File.separator;
                String srcPath = src.getAbsolutePath();

                long logMillis = System.currentTimeMillis() + 2000L;

                for(int i = 0; i < allFiles; i++)
                {
                    File file = files.get(i);

                    long millis = System.currentTimeMillis();

                    if(i == 0 || millis > logMillis || i == allFiles - 1)
                    {
                        logMillis = millis + 2000L;
                        Backups.logger.info("[" + i + " | " + MathHelperLM.toSmallDouble((i / (double) allFiles) * 100D) + "%]: " + file.getName());
                    }

                    File dst1 = new File(dstPath + (file.getAbsolutePath().replace(srcPath, "")));
                    LMFileUtils.copyFile(file, dst1);
                }
            }

            Backups.logger.info("Created " + dstFile.getAbsolutePath() + " from " + src.getAbsolutePath());

            Backups.clearOldBackups();

            if(FTBUConfigBackups.display_file_size.getAsBoolean())
            {
                String sizeB = LMFileUtils.getSizeS(dstFile);
                String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);

                ITextComponent c = FTBULang.backup_end_2.textComponent(getDoneTime(time.millis), (sizeB.equals(sizeT) ? sizeB : (sizeB + " | " + sizeT)));
                c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
                BroadcastSender.inst.addChatMessage(c);
            }
            else
            {
                ITextComponent c = FTBULang.backup_end_1.textComponent(getDoneTime(time.millis));
                c.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
                BroadcastSender.inst.addChatMessage(c);
            }
        }
        catch(Exception ex)
        {
            ITextComponent c = FTBULang.backup_fail.textComponent(ex.getClass().getName());
            c.getStyle().setColor(TextFormatting.DARK_RED);
            BroadcastSender.inst.addChatMessage(c);

            ex.printStackTrace();
            if(dstFile != null)
            {
                LMFileUtils.delete(dstFile);
            }
        }
        //System.gc();
    }

    private static String getDoneTime(long l)
    {
        return LMStringUtils.getTimeString(System.currentTimeMillis() - l);
    }

    private static void appendNum(StringBuilder sb, int num, char c)
    {
        if(num < 10)
        {
            sb.append('0');
        }
        sb.append(num);
        if(c != 0)
        {
            sb.append(c);
        }
    }

    @Override
    public void run()
    {
        isDone = false;
        doBackup(src0);
        isDone = true;
    }
}