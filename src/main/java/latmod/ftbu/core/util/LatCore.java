package latmod.ftbu.core.util;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.*;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

/** Made by LatvianModder */
public class LatCore
{
	public static final int DAY24 = 24 * 60 * 60;
	public static final String STRIP_SEP = ", ";
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	public static final String ALLOWED_TEXT_CHARS = "!@#$%^&*()_+ -=\\/,.<>?\'\"[]{}|;:`~";
	
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
	
	public static String toString(FastList<String> l)
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
	
	public static void saveFile(File f, FastList<String> al) throws Exception
	{ saveFile(f, toString(al)); }
	
	public static void saveFile(File f, String s) throws Exception
	{
		OutputStream os = new FileOutputStream(newFile(f));
		os.write(s.getBytes()); os.close();
	}
	
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
	
	public static boolean isTextChar(char c)
	{
		if(!isASCIIChar(c)) return false;
		if(c >= '0' && c <= '9') return true;
		if(c >= 'a' && c <= 'z') return true;
		if(c >= 'A' && c <= 'Z') return true;
		return (ALLOWED_TEXT_CHARS.indexOf(c) != -1);
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
		if(s == null || s.length() < 2) s = "{}";
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		return gson.fromJson(s, t);
	}
	
	public static <T> T fromJsonFromFile(File f, Type t)
	{
		try { return fromJson(toString(new FileInputStream(f)), t); }
		catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	public static String toJson(Object o, boolean asTree)
	{
		GsonBuilder gb = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
		if(asTree) gb.setPrettyPrinting(); Gson gson = gb.create(); return gson.toJson(o);
	}
	
	public static void toJsonFile(File f, Object o)
	{
		String s = toJson(o, true);
		
		try
		{
			FileOutputStream fos = new FileOutputStream(newFile(f));
			fos.write(s.getBytes()); fos.close();
		}
		catch(Exception e)
		{ e.printStackTrace(); }
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

	public static boolean copyFile(File oldFile, File newFile)
	{
		boolean result = false;
		
		if(oldFile != null && newFile != null && oldFile.exists() && !oldFile.equals(newFile))
		{
			newFile = newFile(newFile);
			
			FileChannel oldC = null;
			FileChannel newC = null;
			
			try
			{
				oldC = new FileInputStream(oldFile).getChannel();
				newC = new FileOutputStream(newFile).getChannel();
				newC.transferFrom(oldC, 0, oldC.size());
				if(oldC != null) oldC.close();
				if(newC != null) newC.close();
				result = true;
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		
		return result;
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
}