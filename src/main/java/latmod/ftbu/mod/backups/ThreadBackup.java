package latmod.ftbu.mod.backups;

import java.io.*;
import java.util.Calendar;
import java.util.zip.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorldServer;
import latmod.ftbu.mod.config.FTBUConfig;
import net.minecraft.server.MinecraftServer;
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
	}
	
	public void run()
	{
		Backups.lastTimeRun = time;
		LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Starting server backup, expect lag!");
		setSave(false);
		
		try
		{
			StringBuilder out = new StringBuilder();
			appendNum(out, calendar.get(Calendar.YEAR), '-');
			appendNum(out, calendar.get(Calendar.MONTH) + 1, '-');
			appendNum(out, calendar.get(Calendar.DAY_OF_MONTH), '-');
			appendNum(out, calendar.get(Calendar.HOUR_OF_DAY), '-');
			appendNum(out, calendar.get(Calendar.MINUTE), '-');
			appendNum(out, calendar.get(Calendar.SECOND), File.separatorChar);
			
			File dst;
			
			if(FTBUConfig.backups.compress)
			{
				out.append(LMWorldServer.inst.worldIDS);
				out.append(".zip");
				
				dst = LatCore.newFile(new File(Backups.backupsFolder, out.toString()));
				
				long start = LatCore.millis();
				
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dst));
				
				FastList<File> files = LatCore.getAllFiles(src);
				
				for(int i = 0; i < files.size(); i++)
				{
					String filePath = files.get(i).getAbsolutePath();
					ZipEntry ze = new ZipEntry(src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1, filePath.length()));
					zos.putNextEntry(ze);
					FileInputStream fis = new FileInputStream(filePath);
					byte[] buffer = new byte[1024];
					int len;
					while ((len = fis.read(buffer)) > 0)
						zos.write(buffer, 0, len);
					zos.closeEntry();
					fis.close();
				}
				
				zos.close();
				
				LatCoreMC.logger.info("Done compressing in " + ((LatCore.millis() - start) / 1000F) + " seconds (" + LatCore.fileSizeS(dst.length()) + ")!");
			}
			else
			{
				out.append(src.getName());
				
				dst = new File(Backups.backupsFolder, out.toString());
				dst.mkdirs();
				LatCore.throwException(LatCore.copyFile(src, dst));
			}
			
			LatCoreMC.logger.info("Created " + dst.getAbsolutePath() + " from " + src.getAbsolutePath());
			
			Backups.clearOldBackups();
			
			if(FTBUConfig.backups.displayFileSize)
			{
				String sizeB = LatCore.fileSizeS(LatCore.fileSize(dst));
				String sizeT = LatCore.fileSizeS(LatCore.fileSize(Backups.backupsFolder));
				LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + ((LatCore.millis() - time) / 1000F) + " seconds! (" + sizeB + " | " + sizeT + ")");
			}
			else LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + ((LatCore.millis() - time) / 1000F) + " seconds!");
		}
		catch(Exception e)
		{
			LatCoreMC.printChatAll(EnumChatFormatting.DARK_RED + "Failed to save world! (" + LatCore.classpath(e.getClass()) + ")");
			e.printStackTrace();
		}
		
		setSave(true);
		Backups.canRun = true;
		System.gc();
	}
	
	private static void appendNum(StringBuilder sb, int num, char c)
	{
		if(num < 10) sb.append('0');
		sb.append(num);
		if(c != 0) sb.append(c);
	}
	
	private static void setSave(boolean b)
	{
		MinecraftServer ms = LatCoreMC.getServer();
		
		for(int i = 0; i < ms.worldServers.length; ++i)
		{
			if(ms.worldServers[i] != null)
				ms.worldServers[i].levelSaving = b;
		}
	}
}