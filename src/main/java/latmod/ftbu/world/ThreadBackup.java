package latmod.ftbu.world;

import java.io.*;
import java.util.zip.*;

import ftb.lib.*;
import latmod.ftbu.mod.config.FTBUConfigBackups;
import latmod.lib.*;
import net.minecraft.command.server.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ThreadBackup extends Thread
{
	public final File src;
	public final Time calendar;
	
	public ThreadBackup(World w)
	{
		calendar = Time.now();
		src = w.getSaveHandler().getWorldDirectory();
		setPriority(7);
	}
	
	public void run()
	{
		Backups.lastTimeRun = calendar.millis;
		FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Starting server backup, expect lag!");
		
		try
		{
			new CommandSaveOff().processCommand(FTBLib.getServer(), new String[0]);
			new CommandSaveAll().processCommand(FTBLib.getServer(), new String[] { "flush" });
		}
		catch(Exception e) { }
		
		File dstFile = null;
		
		try
		{
			StringBuilder out = new StringBuilder();
			appendNum(out, calendar.year, '-');
			appendNum(out, calendar.month, '-');
			appendNum(out, calendar.day, '-');
			appendNum(out, calendar.hours, '-');
			appendNum(out, calendar.minutes, '-');
			appendNum(out, calendar.seconds, File.separatorChar);
			
			FastList<File> files = LMFileUtils.listAll(src);
			int allFiles = files.size();
			
			Backups.logger.info("Backing up " + files.size() + " files...");
			
			if(FTBUConfigBackups.compression_level.get() > 0)
			{
				out.append(FTBWorld.server.getWorldIDS());
				out.append(".zip");
				dstFile = LMFileUtils.newFile(new File(Backups.backupsFolder, out.toString()));
				
				long start = LMUtils.millis();
				
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dstFile));
				//zos.setLevel(9);
				zos.setLevel(FTBUConfigBackups.compression_level.get());
				
				long logMillis = LMUtils.millis() + 5000L;
				
				byte[] buffer = new byte[8192];
				
				Backups.logger.info("Compressing " + allFiles + " files!");
				
				for(int i = 0; i < allFiles; i++)
				{
					File file = files.get(i);
					String filePath = file.getAbsolutePath();
					ZipEntry ze = new ZipEntry(src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1, filePath.length()));
					
					long millis = LMUtils.millis();
					
					if(i == 0 || millis > logMillis || i == allFiles - 1)
					{
						logMillis = millis + 5000L;
						
						StringBuilder log = new StringBuilder();
						log.append('[');
						log.append(i);
						log.append(" | ");
						log.append(MathHelperLM.toSmallDouble((i / (double)allFiles) * 100D));
						log.append("%]: ");
						log.append(ze.getName());
						Backups.logger.info(log.toString());
					}
					
					zos.putNextEntry(ze);
					FileInputStream fis = new FileInputStream(file);
					
					int len;
					while ((len = fis.read(buffer)) > 0)
						zos.write(buffer, 0, len);
					zos.closeEntry();
					zos.close();
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
				
				long logMillis = LMUtils.millis() + 2000L;
				
				for(int i = 0; i < allFiles; i++)
				{
					File file = files.get(i);
					
					long millis = LMUtils.millis();
					
					if(i == 0 || millis > logMillis || i == allFiles - 1)
					{
						logMillis = millis + 2000L;
						
						StringBuilder log = new StringBuilder();
						log.append('[');
						log.append(i);
						log.append(" | ");
						log.append(MathHelperLM.toSmallDouble((i / (double)allFiles) * 100D));
						log.append("%]: ");
						log.append(file.getName());
						Backups.logger.info(log.toString());
					}
					
					File dst1 = new File(dstPath + (file.getAbsolutePath().replace(srcPath, "")));
					LMUtils.throwException(LMFileUtils.copyFile(file, dst1));
				}
			}
			
			Backups.logger.info("Created " + dstFile.getAbsolutePath() + " from " + src.getAbsolutePath());
			
			Backups.clearOldBackups();
			
			if(FTBUConfigBackups.display_file_size.get())
			{
				String sizeB = LMFileUtils.getSizeS(dstFile);
				String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
				FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + getDoneTime(calendar.millis) + "! (" + (sizeB.equals(sizeT) ? sizeB : (sizeB + " | " + sizeT)) + ")");
			}
			else FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + getDoneTime(calendar.millis) + "!");
		}
		catch(Exception e)
		{
			FTBLib.printChat(BroadcastSender.inst, EnumChatFormatting.DARK_RED + "Failed to save world! (" + LMUtils.classpath(e.getClass()) + ")");
			e.printStackTrace();
			if(dstFile != null) LMFileUtils.delete(dstFile);
		}
		
		Backups.thread = null;
		new CommandSaveOn().processCommand(FTBLib.getServer(), new String[0]);
		//System.gc();
	}
	
	private static String getDoneTime(long l)
	{ return LMStringUtils.getTimeString(LMUtils.millis() - l); }
	
	private static void appendNum(StringBuilder sb, int num, char c)
	{
		if(num < 10) sb.append('0');
		sb.append(num);
		if(c != 0) sb.append(c);
	}
}