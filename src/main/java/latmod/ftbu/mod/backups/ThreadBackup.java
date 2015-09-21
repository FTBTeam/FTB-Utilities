package latmod.ftbu.mod.backups;

import java.io.*;
import java.util.Calendar;
import java.util.zip.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldServer;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.command.server.*;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ThreadBackup extends Thread
{
	public final File src;
	public final Calendar calendar;
	public final long time;
	
	public ThreadBackup(World w)
	{
		src = w.getSaveHandler().getWorldDirectory();
		calendar = Calendar.getInstance();
		time = calendar.getTimeInMillis();
		setPriority(7);
	}
	
	public void run()
	{
		Backups.lastTimeRun = time;
		LatCoreMC.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Starting server backup, expect lag!");
		new CommandSaveAll().processCommand(LatCoreMC.getServer(), new String[] { "flush" });
		new CommandSaveOff().processCommand(LatCoreMC.getServer(), new String[0]);
		
		File dstFile = null;
		
		try
		{
			StringBuilder out = new StringBuilder();
			appendNum(out, calendar.get(Calendar.YEAR), '-');
			appendNum(out, calendar.get(Calendar.MONTH) + 1, '-');
			appendNum(out, calendar.get(Calendar.DAY_OF_MONTH), '-');
			appendNum(out, calendar.get(Calendar.HOUR_OF_DAY), '-');
			appendNum(out, calendar.get(Calendar.MINUTE), '-');
			appendNum(out, calendar.get(Calendar.SECOND), File.separatorChar);
			
			FastList<File> files = LMFileUtils.listAll(src);
			int allFiles = files.size();
			
			LatCoreMC.logger.info("Backing up " + files.size() + " files...");
			
			if(FTBUConfig.backups.compressionLevel > 0)
			{
				out.append(LMWorldServer.inst.worldIDS);
				out.append(".zip");
				dstFile = LMFileUtils.newFile(new File(Backups.backupsFolder, out.toString()));
				
				long start = LMUtils.millis();
				
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dstFile));
				//zos.setLevel(9);
				zos.setLevel(FTBUConfig.backups.compressionLevel);
				
				long logMillis = LMUtils.millis() + 5000L;
				
				byte[] buffer = new byte[1024];
				
				LatCoreMC.logger.info("Compressing " + allFiles + " files!");
				
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
						LatCoreMC.logger.info(log.toString());
					}
					
					zos.putNextEntry(ze);
					FileInputStream fis = new FileInputStream(file);
					
					int len;
					while ((len = fis.read(buffer)) > 0)
						zos.write(buffer, 0, len);
					zos.closeEntry();
					fis.close();
				}
				
				zos.close();
				
				LatCoreMC.logger.info("Done compressing in " + getDoneTime(start) + " seconds (" + LMFileUtils.getSizeS(dstFile) + ")!");
			}
			else
			{
				out.append(src.getName());
				dstFile = new File(Backups.backupsFolder, out.toString());
				dstFile.mkdirs();
				
				String dstPath = dstFile.getAbsolutePath() + File.separator;
				String srcPath = src.getAbsolutePath();
				
				long logMillis = LMUtils.millis() + 5000L;
				
				for(int i = 0; i < allFiles; i++)
				{
					File file = files.get(i);
					
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
						log.append(file.getName());
						LatCoreMC.logger.info(log.toString());
					}
					
					File dst1 = new File(dstPath + (file.getAbsolutePath().replace(srcPath, "")));
					LMUtils.throwException(LMFileUtils.copyFile(file, dst1));
				}
			}
			
			LatCoreMC.logger.info("Created " + dstFile.getAbsolutePath() + " from " + src.getAbsolutePath());
			
			Backups.clearOldBackups();
			
			if(FTBUConfig.backups.displayFileSize)
			{
				String sizeB = LMFileUtils.getSizeS(dstFile);
				String sizeT = LMFileUtils.getSizeS(Backups.backupsFolder);
				LatCoreMC.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + getDoneTime(time) + "! (" + (sizeB.equals(sizeT) ? sizeB : (sizeB + " | " + sizeT)) + ")");
			}
			else LatCoreMC.printChat(BroadcastSender.inst, EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + getDoneTime(time) + "!");
		}
		catch(Exception e)
		{
			LatCoreMC.printChat(BroadcastSender.inst, EnumChatFormatting.DARK_RED + "Failed to save world! (" + LMUtils.classpath(e.getClass()) + ")");
			e.printStackTrace();
			if(dstFile != null) LMFileUtils.delete(dstFile);
		}
		
		new CommandSaveOn().processCommand(LatCoreMC.getServer(), new String[0]);
		Backups.thread = null;
		System.gc();
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