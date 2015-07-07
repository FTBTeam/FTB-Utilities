package latmod.ftbu.mod.backups;

import java.io.File;
import java.util.Calendar;

import latmod.ftbu.core.LatCoreMC;
import latmod.ftbu.core.util.LatCore;
import latmod.ftbu.core.world.LMWorld;
import latmod.ftbu.mod.FTBUConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ThreadBackup extends Thread
{
	public final File src;
	public final Calendar calendar;
	
	public ThreadBackup(World w)
	{
		src = w.getSaveHandler().getWorldDirectory();
		calendar = Calendar.getInstance();
	}
	
	public void run()
	{
		Backups.lastTimeRun = calendar.getTimeInMillis();
		LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Starting server backup, expect lag!");
		
		setSave(false);
		
		StringBuilder out = new StringBuilder();
		
		out.append(LMWorld.server.worldIDS);
		out.append(File.separatorChar);
		
		if(FTBUConfig.Backups.inst.oneFolder)
		{
			appendNum(out, calendar.get(Calendar.YEAR), '-');
			appendNum(out, calendar.get(Calendar.MONTH) + 1, '-');
			appendNum(out, calendar.get(Calendar.DAY_OF_MONTH), '-');
			appendNum(out, calendar.get(Calendar.HOUR_OF_DAY), '-');
			appendNum(out, calendar.get(Calendar.MINUTE), '-');
			appendNum(out, calendar.get(Calendar.SECOND), File.separatorChar);
			out.append(src.getName());
		}
		else
		{
			appendNum(out, calendar.get(Calendar.YEAR), File.separatorChar);
			appendNum(out, calendar.get(Calendar.MONTH) + 1, File.separatorChar);
			appendNum(out, calendar.get(Calendar.DAY_OF_MONTH), File.separatorChar);
			appendNum(out, calendar.get(Calendar.HOUR_OF_DAY), '-');
			appendNum(out, calendar.get(Calendar.MINUTE), '-');
			appendNum(out, calendar.get(Calendar.SECOND), File.separatorChar);
			out.append(src.getName());
		}
		
		File dst = new File(Backups.backupsFolder, out.toString());
		dst.mkdirs();
		
		long oldest = Backups.addBackup(out.toString(), Backups.lastTimeRun);
		
		if(oldest != 0L)
		{
			File f = new File(Backups.backupsFolder, Backups.backupMap.get(oldest));
			if(f.exists())
			{
				LatCoreMC.logger.info("Deleting oldest backup: " + f.getAbsolutePath());
				if(LatCore.deleteFile(f))
				{
					Backups.backupMap.remove(oldest);
					Backups.saveBackupsMapFile();
				}
			}
		}
		
		LatCoreMC.logger.info("Saving " + src.getAbsolutePath() + " to " + dst.getAbsolutePath());
		
		if(!LatCore.copyFile(src, dst))
			LatCoreMC.printChatAll(EnumChatFormatting.DARK_RED + "Failed to save world!");
		else
		{
			if(FTBUConfig.Backups.inst.compress) try
			{
				long start = LatCore.millis();
				LatCoreMC.logger.info("Compressing... [Not implemented yet]");
				//GzipCompressorOutputStream os = new GzipCompressorOutputStream(new FileOutputStream(LatCore.newFile(new File(dst.getAbsolutePath() + ".zip"))));
				//os.close();
				
				LatCoreMC.logger.info("Done compressing in " + ((LatCore.millis() - start) / 1000F) + " seconds!");
			}
			catch(Exception e)
			{ e.printStackTrace(); }
			
			if(FTBUConfig.Backups.inst.displayFileSize)
			{
				String sizeB = LatCore.fileSizeS(LatCore.fileSize(dst));
				String sizeT = LatCore.fileSizeS(LatCore.fileSize(Backups.backupsFolder));
				LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + ((LatCore.millis() - Backups.lastTimeRun) / 1000F) + " seconds! (" + sizeB + " | " + sizeT + ")");
			}
			else LatCoreMC.printChatAll(EnumChatFormatting.LIGHT_PURPLE + "Server backup done in " + ((LatCore.millis() - Backups.lastTimeRun) / 1000F) + " seconds!");
		}
		
		setSave(true);
		Backups.thread = null;
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