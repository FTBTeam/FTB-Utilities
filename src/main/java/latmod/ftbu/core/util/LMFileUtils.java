package latmod.ftbu.core.util;

import java.io.*;
import java.net.URL;
import java.nio.channels.*;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class LMFileUtils
{
	public static final int KB = 1024;
	public static final int MB = KB * 1024;
	public static final int GB = MB * 1024;
	
	public static final double KB_D = 1024D;
	public static final double MB_D = KB_D * 1024D;
	public static final double GB_D = MB_D * 1024D;
	
	public static File newFile(File f)
	{
		try
		{
			if(!f.exists())
			{
				File pf = f.getParentFile();
				
				if(!pf.exists()) pf.mkdirs();
				f.createNewFile();
				return f;
			}
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		
		return f;
	}
	
	public static void save(File f, List<String> al) throws Exception
	{ save(f, LMStringUtils.toString(al)); }
	
	public static void save(File f, String s) throws Exception
	{ BufferedWriter br = new BufferedWriter(new FileWriter(newFile(f))); br.write(s); br.close(); }
	
	public static FastList<String> load(File f) throws Exception
	{ return LMStringUtils.toStringList(new FileInputStream(f)); }
	
	public static String loadAsText(File f) throws Exception
	{ return LMStringUtils.toString(new FileInputStream(f)); }
	
	public static boolean downloadFile(String url, File out)
	{
		try
		{
			URL website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(out);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			return true;
		}
		catch(Exception e) { }
		return false;
	}
	
	public static FastList<File> listAll(File f)
	{ FastList<File> l = new FastList<File>(); addAllFiles(l, f); return l; }
	
	private static void addAllFiles(FastList<File> l, File f)
	{
		//FileUtils.listFiles(directory, extensions, recursive);
		
		if(f.isDirectory())
		{
			File[] fl = f.listFiles();
			
			if(fl != null && fl.length > 0)
			{
				for(int i = 0; i < fl.length; i++)
					addAllFiles(l, fl[i]);
			}
		}
		else if(f.isFile()) l.add(f);
	}

	public static long getSize(File f)
	{
		if(f == null || !f.exists()) return 0L;
		if(f.isFile()) return f.length();
		return FileUtils.sizeOf(f);
	}
	
	public static String getSizeS(double b)
	{
		if(b >= GB_D)
		{
			b /= GB_D;
			b = (long)(b * 10D) / 10D;
			return b + "GB";
		}
		else if(b >= MB_D)
		{
			b /= MB_D;
			b = (long)(b * 10D) / 10D;
			return b + "MB";
		}
		else if(b >= KB_D)
		{
			b /= KB_D;
			b = (long)(b * 10D) / 10D;
			return b + "KB";
		}
		
		return b + "B";
	}
	
	public static String getSizeS(File f)
	{ return getSizeS(getSize(f)); }
	
	public static Exception copyFile(File src, File dst)
	{
		if(src != null && dst != null && src.exists() && !src.equals(dst))
		{
			if(src.isDirectory() && dst.isDirectory())
			{
				FastList<File> files = listAll(src);
				
				for(File f : files)
				{
					File dst1 = new File(dst.getAbsolutePath() + File.separator + (f.getAbsolutePath().replace(src.getAbsolutePath(), "")));
					Exception e = copyFile(f, dst1); if(e != null) return e;
				}
				
				return null;
			}
			
			dst = newFile(dst);
			
			FileChannel srcC = null;
			FileChannel dstC = null;
			
			try
			{
				srcC = new FileInputStream(src).getChannel();
				dstC = new FileOutputStream(dst).getChannel();
				dstC.transferFrom(srcC, 0L, srcC.size());
				if(srcC != null) srcC.close();
				if(dstC != null) dstC.close();
				return null;
			}
			catch(Exception e) { return e; }
		}
		
		return null;
	}
	
	public static boolean delete(File dir)
	{
		if(!dir.exists()) return false;
		if(dir.isFile()) return dir.delete();
		String[] files = dir.list();
		for(int i = 0; i < files.length; i++)
			delete(new File(dir, files[i]));
		return dir.delete();
	}
	
	public static File getSourceDirectory(Class<?> c)
	//{ return new File(c.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")); }
	{ return new File(c.getProtectionDomain().getCodeSource().getLocation().getFile()); }
}