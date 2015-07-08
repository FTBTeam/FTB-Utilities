package latmod.ftbu.mod.backups;

import java.io.*;
import java.util.Calendar;
import java.util.zip.*;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.*;
import latmod.ftbu.core.world.LMWorld;
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
			out.append(LMWorld.server.worldIDS);
			
			File dst = new File(Backups.backupsFolder, out.toString());
			dst.mkdirs();
			
			LatCoreMC.logger.info("Saving " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
			
			LatCore.throwException(LatCore.copyFile(src, dst));
			
			if(FTBUConfig.backups.compress)
			{
				File dst0 = new File(dst.getAbsolutePath());
				dst = new File(dst0.getAbsolutePath() + ".zip");
				
				long start = LatCore.millis();
				LatCoreMC.logger.info("Compressing...");
				if(compress(dst0, dst) && LatCore.deleteFile(dst0))
					LatCoreMC.logger.info("Done compressing in " + ((LatCore.millis() - start) / 1000F) + " seconds (" + LatCore.fileSizeS(new File(dst.getAbsolutePath() + ".zip").length()) + ")!");
				else LatCoreMC.logger.info("Failed to compress backup!");
				
			}
			
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
	
	private boolean compress(File src, File dst)
	{
		try
		{
			FileOutputStream fos = new FileOutputStream(dst);
			ZipOutputStream zos = new ZipOutputStream(fos);
			
			FastList<File> files = LatCore.getAllFiles(src);
			
			for(File f : files)
			{
				String filePath = f.getAbsolutePath();
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
			fos.close();
			return true;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return false;
	}
}