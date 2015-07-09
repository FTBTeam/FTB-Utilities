package latmod.ftbu.core.util;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
public class LatCore
{
	public static final int DAY24 = 24 * 60 * 60;
	public static final String STRIP_SEP = ", ";
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final String ALLOWED_TEXT_CHARS = "!@#$%^&*()_+ -=\\/,.<>?\'\"[]{}|;:`~";
	
	public static class Colors
	{
		public static int getRGBA(int r, int g, int b, int a)
		{ return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0); }
		
		public static int getRed(int c)
		{ return (c >> 16) & 0xFF; }
		
		public static int getGreen(int c)
		{ return (c >> 8) & 0xFF; }
		
		public static int getBlue(int c)
		{ return (c >> 0) & 0xFF; }
		
		public static int getAlpha(int c)
		{ return (c >> 24) & 0xFF; }

		public static String getHex(int c)
		{ return "#" + Integer.toHexString(getRGBA(c, 255)).substring(2).toUpperCase(); }
		
		public static int getRGBA(int c, int a)
		{ return getRGBA(getRed(c), getGreen(c), getBlue(c), a); }
		
		@SideOnly(Side.CLIENT)
		public static void setGLColor(int c, int a)
		{
			int r = getRed(c); int g = getGreen(c); int b = getBlue(c);
			GL11.glColor4f(r / 255F, g / 255F, b / 255F, a / 255F);
		}
		
		@SideOnly(Side.CLIENT)
		public static void setGLColor(int c)
		{ setGLColor(c, getAlpha(c)); }
	}
	
	@SuppressWarnings("all")
	public static URL getURL(String s)
	{
		try { return new File(s).toURL(); }
		catch(Exception e) { }
		try { return new URL(s); }
		catch(Exception e) { e.printStackTrace(); }
		return null;
	}
	
	public static String toString(InputStream is) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String s = null; while((s = br.readLine()) != null) sb.append(s);
		return sb.toString();
	}
	
	public static FastList<String> toStringList(String s, String regex)
	{
		FastList<String> al = new FastList<String>();
		String[] s1 = s.split(regex);
		if(s1 != null && s1.length > 0)
		for(int i = 0; i < s1.length; i++)
		al.add(s1[i].trim()); return al;
	}
	
	public static String toString(List<String> l)
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < l.size(); i++)
		{ sb.append(l.get(i)); if(i != l.size() - 1) sb.append('\n'); }
		return sb.toString();
	}
	
	public static FastList<String> toStringList(InputStream is) throws Exception
	{
		FastList<String> l = new FastList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String s = null; while((s = reader.readLine()) != null)
			l.add(s); reader.close(); return l;
	}
	
	public static void saveFile(File f, List<String> al) throws Exception
	{ saveFile(f, toString(al)); }
	
	public static void saveFile(File f, String s) throws Exception
	{ BufferedWriter br = new BufferedWriter(new FileWriter(newFile(f))); br.write(s); br.close(); }
	
	public static FastList<String> loadFile(File f) throws Exception
	{ return toStringList(new FileInputStream(f)); }
	
	public static String loadFileAsText(File f) throws Exception
	{ return toString(new FileInputStream(f)); }
	
	@SuppressWarnings("all")
	public static <E> E newObject(Class<?> c, Object... o) throws Exception
	{
		if(c == null) return null;
		
		if(o != null && o.length > 0)
		{
			Class<?>[] params = new Class<?>[o.length];
			for(int i = 0; i < o.length; i++)
			params[i] = o.getClass();
			
			Constructor<?> c1 = c.getConstructor(params);
			return (E) c1.newInstance(o);
		}
		
		return (E) c.newInstance();
	}
	
	public static boolean isASCIIChar(char c)
	{ return c > 0 && c < 256; }
	
	public static boolean isTextChar(char c, boolean onlyAZ09)
	{
		if(!isASCIIChar(c)) return false;
		if(c >= '0' && c <= '9') return true;
		if(c >= 'a' && c <= 'z') return true;
		if(c >= 'A' && c <= 'Z') return true;
		return !onlyAZ09 && (ALLOWED_TEXT_CHARS.indexOf(c) != -1);
	}
	
	public static FastList<Package> getAllPackages()
	{
		FastList<Package> p = FastList.asList(Package.getPackages());
		
		p.sort(new Comparator<Package>() 
		{
			public int compare(Package o1, Package o2)
			{ return o1.getName().compareTo(o2.getName()); }
		});
		
		return p;
	}
	
	public static void replace(FastList<String> txt, String s, String s1)
	{
		for(int i = 0; i < txt.size(); i++)
		{
			String s2 = txt.get(i);
			if(s2 != null && s2.length() > 0 && s2.contains(s))
			{ s2 = s2.replace(s, s1); txt.set(i, s2); }
		}
	}
	
	public static File newFile(File f)
	{
		if(!f.exists())
		{
			try { f.createNewFile(); }
			catch(Exception e)
			{
				f.getParentFile().mkdirs();
				try { f.createNewFile(); }
				catch(Exception e1)
				{ e1.printStackTrace(); }
			}
		}
		return f;
	}
	
	public static <E> String[] toStrings(E[] o)
	{
		if(o == null) return null;
		String[] s = new String[o.length];
		for(int i = 0; i < o.length; i++)
			s[i] = String.valueOf(o[i]);
		return s;
	}
	
	public static String strip(String... o)
	{
		if(o == null) return null;
		if(o.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < o.length; i++)
		{
			sb.append(o[i]);
			if(i != o.length - 1)
				sb.append(STRIP_SEP);
		}
		
		return sb.toString();
	}
	
	public static String stripDouble(double... o)
	{
		if(o == null) return null;
		if(o.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < o.length; i++)
		{
			sb.append(MathHelperLM.formatDouble(MathHelperLM.toSmallDouble(o[i])));
			if(i != o.length - 1) sb.append(STRIP_SEP);
		}
		
		return sb.toString();
	}
	
	public static String stripDoubleInt(double... o)
	{
		if(o == null) return null;
		if(o.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < o.length; i++)
		{
			sb.append((long)o[i]);
			if(i != o.length - 1)
				sb.append(STRIP_SEP);
		}
		
		return sb.toString();
	}
	
	public static String stripInt(int... o)
	{
		if(o == null) return null;
		if(o.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < o.length; i++)
		{
			sb.append(o[i]);
			if(i != o.length - 1)
				sb.append(STRIP_SEP);
		}
		
		return sb.toString();
	}
	
	public static String stripBool(boolean... o)
	{
		if(o == null) return null;
		if(o.length == 0) return "";
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < o.length; i++)
		{
			sb.append(o[i] ? '1' : '0');
			if(i != o.length - 1)
				sb.append(STRIP_SEP);
		}
		
		return sb.toString();
	}
	
	public static String classpath(Class<?> c)
	{ return (c == null) ? null : (c.toString().split(" ")[1]); }
	
	public static File getSourceDirectory(Class<?> c)
	{ return new File(c.getProtectionDomain().getCodeSource().getLocation().getPath().replace("%20", " ")); }
	
	public static FastList<Class<?>> addSubclasses(Class<?> c, FastList<Class<?>> al, boolean all)
	{
		if(c == null) return null;
		if(al == null) al = new FastList<Class<?>>();
		FastList<Class<?>> al1 = new FastList<Class<?>>();
		al1.addAll(c.getDeclaredClasses());
		if(all && !al1.isEmpty()) for(int i = 0; i < al1.size(); i++)
		al.addAll(addSubclasses(al1.get(i), null, true));
		al.addAll(al1); return al;
	}
	
	@Deprecated
	public static URLConnection connectTo(String s, int timeout) throws Exception
	{
		URLConnection uc = new URL(s).openConnection();
		uc.setConnectTimeout(timeout);
		uc.setDoInput(true);
		uc.setDoOutput(true);
		uc.connect(); return uc;
	}
	
	public static String substring(String s, String pre, String post, boolean ignoreSpace)
	{
		int preI = s.indexOf(pre);
		int postI = s.lastIndexOf(post);
		String s1 = s.substring(preI + 1, postI);
		return ignoreSpace ? s1.trim() : s1;
	}
	
	public static String removeAllWhitespace(String s)
	{
		if(s == null) return null;
		s = s.trim(); if(s.length() == 0) return "";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(!Character.isWhitespace(c))
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	public static String getHostAddress()
	{
		try { return InetAddress.getLocalHost().getHostAddress(); }
		catch(Exception e) { } return null;
	}
	
	public static String getExternalAddress()
	{
		try { return toString(new URL("http://checkip.amazonaws.com").openStream()); }
		catch(Exception e) { } return null;
	}
	
	public static String unsplit(String[] s, String s1)
	{
		if(s == null) return null;
		StringBuilder sb = new StringBuilder();
		if(s.length == 1) return s[0];
		for(int i = 0; i < s.length; i++)
		{
			sb.append(s[i]);
			if(i != s.length - 1)
				sb.append(s1);
		}
		return sb.toString();
	}
	
	public static String unsplit(Object[] o, String s1)
	{
		if(o == null) return null;
		StringBuilder sb = new StringBuilder();
		if(o.length == 1) return String.valueOf(o[0]);
		for(int i = 0; i < o.length; i++)
		{
			sb.append(o[i]);
			if(i != o.length - 1)
				sb.append(s1);
		}
		return sb.toString();
	}

	public static String firstUppercase(String s)
	{
		if(s == null || s.length() == 0) return s;
		return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
	}
	
	public static boolean areObjectsEqual(Object o1, Object o2, boolean allowNulls)
	{
		if(o1 == null && o2 == null && allowNulls) return true;
		if(o1 == null || o2 == null) return false;
		return o1.equals(o2);
	}
	
	public static boolean areStringsEqual(String s0, String s1)
	{
		if(s0 == null && s1 == null) return true;
		if(s0 == null || s1 == null) return false;
		if(s0.length() != s1.length()) return false;
		return s0.equals(s1);
	}
	
	public static <T> T fromJson(String s, Type t)
	{
		if(s == null || s.length() < 2) return null;
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.fromJson(s, t);
	}
	
	public static <T> T fromJsonFile(File f, Type t)
	{
		if(!f.exists()) return null;
		try { return fromJson(toString(new FileInputStream(f)), t); }
		catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	public static String toJson(Object o, boolean asTree)
	{
		if(o == null) return null;
		GsonBuilder gb = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
		if(asTree) gb.setPrettyPrinting(); Gson gson = gb.create(); return gson.toJson(o);
	}
	
	public static boolean toJsonFile(File f, Object o)
	{
		String s = toJson(o, true);
		if(s == null) return false;
		
		try
		{
			FileOutputStream fos = new FileOutputStream(newFile(f));
			fos.write(s.getBytes());
			fos.close();
			return true;
		}
		catch(Exception e)
		{ e.printStackTrace(); }
		return false;
	}
	
	public static <K, V> Type getMapType(Type K, Type V)
	{ return new TypeToken<Map<K, V>>() {}.getType(); }
	
	public static <E> Type getListType(Type E)
	{ return new TypeToken<List<E>>() {}.getType(); }
	
	public static boolean openURL(String url)
	{
		try
		{
			Class<?> oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
			oclass.getMethod("browse", new Class[] { URI.class }).invoke(object, new Object[] { new URI(url) });
			return true;
		}
		catch (Exception e) { e.printStackTrace(); }
		return false;
	}
	
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
	
	public static String formatTime(long secs, boolean wrap)
	{
		long secs1 = secs;
		if(secs < 0L) secs1 = -secs1;
		if(wrap) secs1 %= DAY24;
		
		long h = (secs1 / 3600L);
		if(wrap) h %= 24L;
		
		long m = (secs1 / 60L) % 60L;
		long s = secs1 % 60L;
		
		StringBuilder sb = new StringBuilder();
		if(h < 10) sb.append('0');
		sb.append(h);
		sb.append(':');
		if(m < 10) sb.append('0');
		sb.append(m);
		sb.append(':');
		if(s < 10) sb.append('0');
		sb.append(s);
		return sb.toString();
	}
	
	public static String formatInt(int i)
	{ return formatInt(i, 1); }
	
	public static String formatInt(int i, int z)
	{
		String s0 = "" + i;
		if(z <= 0) return s0;
		z += 1;
		
		StringBuilder s = new StringBuilder();
		
		for(int j = 0; j < z - s0.length(); j++)
			s.append('0');
		
		s.append(i);
		return s.toString();
	}

	public static String fillString(String s, char fill, int length)
	{
		int sl = s.length();
		
		char[] c = new char[Math.max(sl, length)];
		
		for(int i = 0; i < c.length; i++)
		{
			if(i >= sl) c[i] = fill;
			else c[i] = s.charAt(i);
		}
		
		return new String(c);
	}
	
	public static int hashCode(Object... o)
	{
		if(o.length == 0) return 0;
		if(o.length == 1) return (o[0] == null) ? 0 : o[0].hashCode();
		int h = 0;
		for(int i = 0; i < o.length; i++)
			h = h * 31 + ((o[i] == null) ? 0 : o[i].hashCode());
		return h;
	}
	
	public static boolean contains(String[] s, String s1)
	{
		for(int i = 0; i < s.length; i++)
			if(s[i] != null && (s[i] == s1 || s[i].equals(s1)))
				return true;
		return false;
	}

	public static Exception copyFile(File src, File dst)
	{
		if(src != null && dst != null && src.exists() && !src.equals(dst))
		{
			if(src.isDirectory() && dst.isDirectory())
			{
				FastList<File> files = LatCore.getAllFiles(src);
				
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
	
	public static boolean deleteFile(File dir)
	{
		if(!dir.exists()) return false;
		if(dir.isFile()) return dir.delete();
		String[] files = dir.list();
		for(int i = 0; i < files.length; i++)
			deleteFile(new File(dir, files[i]));
		return dir.delete();
	}
	
	public static String getTimeAgo(long t)
	{
		long sec = 1000L;
		long min = 60L * sec;
		long hour = 60L * min;
		long day = 24L * hour;
		
		if(t < sec) return "Now";
		if(t < min) return (t / sec) + getPW(t / sec, " second", " seconds");
		if(t < hour) return (t / min) + getPW(t / min, " minute", " minutes");
		if(t < day) return (t / hour) + getPW(t / hour, " hour", " hours");
		return (t / day) + getPW(t / day, " day", " days");
	}
	
	private static String getPW(long t, String s, String p)
	{
		String s0 = "" + t;
		return (s0.endsWith("1") && !s0.endsWith("11")) ? s : p;
	}
	
	public static long millis()
	{ return System.currentTimeMillis(); }

	public static FastList<File> getAllFiles(File f)
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

	public static long fileSize(File f)
	{
		if(f == null || !f.exists()) return 0L;
		if(f.isFile()) return f.length();
		return FileUtils.sizeOf(f);
	}
	
	public static String fileSizeS(double b)
	{
		if(b >= 1024D * 1024D * 1024D)
		{
			b /= 1024D * 1024D * 1024D;
			b = (long)(b * 10D) / 10D;
			return b + "GB";
		}
		else if(b >= 1024D * 1024D)
		{
			b /= 1024D * 1024D;
			b = (long)(b * 10D) / 10D;
			return b + "MB";
		}
		else if(b >= 1024L)
		{
			b /= 1024D;
			b = (long)(b * 10D) / 10D;
			return b + "KB";
		}
		
		return b + "B";
	}
	
	public static void throwException(Exception e) throws Exception
	{ if(e != null) throw e; }
}