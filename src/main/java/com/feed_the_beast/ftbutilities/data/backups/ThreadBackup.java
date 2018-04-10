package com.feed_the_beast.ftbutilities.data.backups;

import com.feed_the_beast.ftblib.lib.util.FileUtils;
import com.feed_the_beast.ftblib.lib.util.StringUtils;
import com.feed_the_beast.ftbutilities.FTBUtilities;
import com.feed_the_beast.ftbutilities.FTBUtilitiesConfig;
import com.feed_the_beast.ftbutilities.FTBUtilitiesLang;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ThreadBackup extends Thread
{
	public boolean isDone = false;
	private final MinecraftServer server;
	private final File src0;
	private final String customName;

	public ThreadBackup(MinecraftServer ms, File w, String s)
	{
		server = ms;
		src0 = w;
		customName = s;
		setPriority(7);
	}

	public static void doBackup(MinecraftServer server, File src, String customName)
	{
		Calendar time = Calendar.getInstance();
		File dstFile = null;
		boolean success = false;
		StringBuilder out = new StringBuilder();

		if (customName.isEmpty())
		{
			appendNum(out, time.get(Calendar.YEAR), '-');
			appendNum(out, time.get(Calendar.MONTH) + 1, '-');
			appendNum(out, time.get(Calendar.DAY_OF_MONTH), '-');
			appendNum(out, time.get(Calendar.HOUR_OF_DAY), '-');
			appendNum(out, time.get(Calendar.MINUTE), '-');
			appendNum(out, time.get(Calendar.SECOND), '\0');
		}
		else
		{
			out.append(customName);
		}

		try
		{
			LinkedHashMap<File, String> fileMap = new LinkedHashMap<>();

			for (File file : FileUtils.listTree(src))
			{
				String filePath = file.getAbsolutePath();
				fileMap.put(file, src.getName() + File.separator + filePath.substring(src.getAbsolutePath().length() + 1, filePath.length()));
			}

			String mcdir = server.getDataDirectory().getCanonicalFile().getAbsolutePath();

			for (String s : FTBUtilitiesConfig.backups.extra_files)
			{
				for (File file : FileUtils.listTree(new File(s)))
				{
					String s1 = file.getAbsolutePath().replace(mcdir, "");

					if (s1.startsWith(File.separator))
					{
						s1 = s1.substring(File.separator.length());
					}

					fileMap.put(file, "_extra_" + File.separator + s1);
				}
			}

			FTBUtilities.LOGGER.info("Backing up " + fileMap.size() + " files...");

			if (FTBUtilitiesConfig.backups.compression_level > 0)
			{
				out.append(".zip");
				dstFile = FileUtils.newFile(new File(Backups.INSTANCE.backupsFolder, out.toString()));

				long start = System.currentTimeMillis();

				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(dstFile));
				zos.setLevel(FTBUtilitiesConfig.backups.compression_level);

				long logMillis = System.currentTimeMillis() + 5000L;
				byte[] buffer = new byte[4096];

				FTBUtilities.LOGGER.info("Compressing " + fileMap.size() + " files!");

				int i = 0;
				for (Map.Entry<File, String> entry : fileMap.entrySet())
				{
					try
					{
						ZipEntry ze = new ZipEntry(entry.getValue());
						long millis = System.currentTimeMillis();

						if (i == 0 || millis > logMillis || i == fileMap.size() - 1)
						{
							logMillis = millis + 5000L;
							FTBUtilities.LOGGER.info("[" + i + " | " + StringUtils.formatDouble((i / (double) fileMap.size()) * 100D) + "%]: " + entry.getValue());
						}

						zos.putNextEntry(ze);
						FileInputStream fis = new FileInputStream(entry.getKey());

						int len;
						while ((len = fis.read(buffer)) > 0)
						{
							zos.write(buffer, 0, len);
						}
						zos.closeEntry();
						fis.close();
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					i++;
				}

				zos.close();

				FTBUtilities.LOGGER.info("Done compressing in " + getDoneTime(start) + " seconds (" + FileUtils.getSizeString(dstFile) + ")!");
			}
			else
			{
				dstFile = new File(new File(Backups.INSTANCE.backupsFolder, out.toString()), src.getName());
				dstFile.mkdirs();

				long logMillis = System.currentTimeMillis() + 2000L;

				int i = 0;
				for (Map.Entry<File, String> entry : fileMap.entrySet())
				{
					try
					{
						File file = entry.getKey();

						long millis = System.currentTimeMillis();

						if (i == 0 || millis > logMillis || i == fileMap.size() - 1)
						{
							logMillis = millis + 2000L;
							FTBUtilities.LOGGER.info("[" + i + " | " + StringUtils.formatDouble((i / (double) fileMap.size()) * 100D) + "%]: " + entry.getValue());
						}

						File dst1 = new File(dstFile, entry.getValue());
						FileUtils.copyFile(file, dst1);
					}
					catch (Exception ex)
					{
						ex.printStackTrace();
					}

					i++;
				}
			}

			FTBUtilities.LOGGER.info("Created " + dstFile.getAbsolutePath() + " from " + src.getAbsolutePath());
			success = true;

			if (!FTBUtilitiesConfig.backups.silent)
			{
				if (FTBUtilitiesConfig.backups.display_file_size)
				{
					String sizeB = FileUtils.getSizeString(dstFile);
					String sizeT = FileUtils.getSizeString(Backups.INSTANCE.backupsFolder);
					Backups.notifyAll(server, player -> FTBUtilitiesLang.BACKUP_END_2.textComponent(player, getDoneTime(time.getTimeInMillis()), (sizeB.equals(sizeT) ? sizeB : (sizeB + " | " + sizeT))), false);
				}
				else
				{
					Backups.notifyAll(server, player -> FTBUtilitiesLang.BACKUP_END_1.textComponent(player, getDoneTime(time.getTimeInMillis())), false);
				}
			}
		}
		catch (Exception ex)
		{
			if (!FTBUtilitiesConfig.backups.silent)
			{
				Backups.notifyAll(server, player -> FTBUtilitiesLang.BACKUP_FAIL.textComponent(player, ex.getClass().getName()), true);
			}

			ex.printStackTrace();
			if (dstFile != null)
			{
				FileUtils.delete(dstFile);
			}
		}

		Backups.INSTANCE.backups.add(new Backup(time.getTimeInMillis(), out.toString().replace('\\', '/'), Backups.INSTANCE.getLastIndex() + 1, success));
		Backups.INSTANCE.cleanupAndSave();
	}

	private static String getDoneTime(long l)
	{
		l = System.currentTimeMillis() - l;

		if (l < 1000L)
		{
			return l + "ms";
		}

		return StringUtils.getTimeString(l);
	}

	private static void appendNum(StringBuilder sb, int num, char c)
	{
		if (num < 10)
		{
			sb.append('0');
		}
		sb.append(num);
		if (c != '\0')
		{
			sb.append(c);
		}
	}

	@Override
	public void run()
	{
		isDone = false;
		doBackup(server, src0, customName);
		isDone = true;
	}
}