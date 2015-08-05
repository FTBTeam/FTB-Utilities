package latmod.ftbu.core.util;
import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.*;

import latmod.ftbu.core.*;
import latmod.ftbu.core.event.LMGsonEvent;
import latmod.ftbu.core.inv.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

import org.lwjgl.opengl.GL11;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.*;

/** Made by LatvianModder */
public class LatCore
{
	public static final int DAY24 = 24 * 60 * 60;
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
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
		
		@SideOnly(Side.CLIENT)
		public static final void recolor()
		{ GL11.glColor4f(1F, 1F, 1F, 1F); }
		
		public static int getHSB(float h, float s, float b)
		{ return java.awt.Color.HSBtoRGB(h, s, b); }
		
		public static float[] getHSB(int r, int g, int b)
		{
			float[] f = new float[3];
			java.awt.Color.RGBtoHSB(r, g, b, f);
			return f;
		}
		
		public static float[] getHSB(int c)
		{ return getHSB(getRed(c), getGreen(c), getBlue(c)); }
		
		public static float getHue(int c)
		{ return getHSB(c)[0]; }
		
		public static float getSaturation(int c)
		{ return getHSB(c)[1]; }
		
		public static float getBrightness(int c)
		{ return getHSB(c)[2]; }
	}
	
	public static enum OS
	{
		WINDOWS,
		LINUX,
		OSX,
		OTHER;
		
		private static OS current = null;
		
		public static OS get()
		{ return (current == null) ? (current = get0()) : current; }
		
		private static OS get0()
		{
			String s = System.getProperty("os.name");
			if(s == null || s.isEmpty()) return OTHER;
			s = s.toLowerCase();
			if(s.contains("win")) return WINDOWS;
			else if(s.contains("mac")) return OSX;
			else if(s.contains("linux") || s.contains("unix")) return LINUX;
			return OTHER;
		}
	}
	
	private static Gson gson = null;
	//public static boolean jsonPrettyPrinting = true;
	
	public static void updateGson()
	{
		GsonBuilder gb = new GsonBuilder();
		gb.excludeFieldsWithoutExposeAnnotation();
		gb.setPrettyPrinting();
		gb.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
		gb.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
		gb.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
		
		NBTSerializer.init(gb);
		gb.registerTypeHierarchyAdapter(IntList.class, new IntList.Serializer());
		gb.registerTypeHierarchyAdapter(IntMap.class, new IntMap.Serializer());
		gb.registerTypeHierarchyAdapter(ItemStack.class, new ItemStackSerializer());
		gb.registerTypeHierarchyAdapter(UUID.class, new UUIDSerializer());
		gb.registerTypeHierarchyAdapter(Notification.class, new Notification.Serializer());
		
		new LMGsonEvent(gb).post();
		gson = gb.create();
	}
	
	public static Gson getGson()
	{
		if(gson == null) updateGson();
		return gson;
	}
	
	public static <T> T fromJson(String s, Type t)
	{
		if(s == null || s.length() < 2) return null;
		return getGson().fromJson(s, t);
	}
	
	public static <T> T fromJsonFile(File f, Type t)
	{
		if(!f.exists()) return null;
		try { return fromJson(LMStringUtils.toString(new FileInputStream(f)), t); }
		catch(Exception e) { e.printStackTrace(); return null; }
	}
	
	public static String toJson(Object o)
	{
		if(o == null) return null;
		return getGson().toJson(o);
	}
	
	public static boolean toJsonFile(File f, Object o)
	{
		String s = toJson(o);
		if(s == null) return false;
		
		try
		{
			FileOutputStream fos = new FileOutputStream(LMFileUtils.newFile(f));
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
	
	public static String classpath(Class<?> c)
	{ return (c == null) ? null : c.getName(); }
	
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
		try { return LMStringUtils.toString(new URL("http://checkip.amazonaws.com").openStream()); }
		catch(Exception e) { } return null;
	}
	
	public static boolean areObjectsEqual(Object o1, Object o2, boolean allowNulls)
	{
		if(o1 == null && o2 == null && allowNulls) return true;
		if(o1 == null || o2 == null) return false;
		return o1.equals(o2);
	}
	
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
	
	public static String formatJson(String s, boolean array)
	{
		if(s == null) return null;
		s = s.trim();
		if(s.length() < 2) return array ? "[]" : "{}";
		StringBuilder sb = new StringBuilder();
		if(array) { if(s.charAt(0) != '[') sb.append('['); }
		else { if(s.charAt(0) != '{') sb.append('{'); }
		sb.append(s);
		if(array) { if(s.charAt(s.length() - 1) != ']') sb.append(']'); }
		else { if(s.charAt(s.length() - 1) != '}') sb.append('}'); }
		return sb.toString();
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
	
	public static String getTimeAgo(long t)
	{
		long sec = 1000L;
		long min = 60L * sec;
		long hour = 60L * min;
		long day = 24L * hour;
		
		if(t < sec) return "0 seconds";
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

	public static void throwException(Exception e) throws Exception
	{ if(e != null) throw e; }
}