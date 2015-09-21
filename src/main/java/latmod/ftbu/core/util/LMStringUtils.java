package latmod.ftbu.core.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

public class LMStringUtils
{
	public static final int DAY24 = 24 * 60 * 60;
	public static final Charset UTF_8 = Charset.forName("UTF-8");
	
	public static final String STRIP_SEP = ", ";
	public static final String ALLOWED_TEXT_CHARS = "!@#$%^&*()_+ -=\\/,.<>?\'\"[]{}|;:`~";
	
	public static String toString(InputStream is) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
		String s = null; while((s = br.readLine()) != null) sb.append(s); br.close();
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
		BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is)));
		String s = null; while((s = reader.readLine()) != null)
			l.add(s); reader.close(); return l;
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
	
	public static void replace(FastList<String> txt, String s, String s1)
	{
		for(int i = 0; i < txt.size(); i++)
		{
			String s2 = txt.get(i);
			if(s2 != null && s2.length() > 0 && s2.contains(s))
			{ s2 = s2.replace(s, s1); txt.set(i, s2); }
		}
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
	
	public static String stripD(double... o)
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
	
	public static String stripDI(double... o)
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
	
	public static String stripI(int... o)
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
	
	public static String stripB(boolean... o)
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
	
	public static String unsplitSpaceUntilEnd(int startIndex, String[] o)
	{
		if(o == null || startIndex < 0 || o.length <= startIndex) return null;
		StringBuilder sb = new StringBuilder();
		
		for(int i = startIndex; i < o.length; i++)
		{
			sb.append(o[i]);
			if(i != o.length -1) sb.append(' ');
		}
		
		return sb.toString();
	}
	
	public static String firstUppercase(String s)
	{
		if(s == null || s.length() == 0) return s;
		return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
	}
	
	public static boolean areStringsEqual(String s0, String s1)
	{
		if(s0 == null && s1 == null) return true;
		if(s0 == null || s1 == null) return false;
		if(s0.length() != s1.length()) return false;
		return s0.equals(s1);
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
	
	public static boolean contains(String[] s, String s1)
	{
		for(int i = 0; i < s.length; i++)
			if(s[i] != null && (s[i] == s1 || s[i].equals(s1)))
				return true;
		return false;
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
		String s0 = Integer.toString(i);
		if(z <= 0) return s0;
		z += 1;
		
		StringBuilder s = new StringBuilder();
		
		for(int j = 0; j < z - s0.length(); j++)
			s.append('0');
		
		s.append(i);
		return s.toString();
	}
	
	public static String getTimeString(long millis)
	{
		long secs = millis / 1000L;
		StringBuilder sb = new StringBuilder();
		
		long h = (secs / 3600L) % 24;
		long m = (secs / 60L) % 60L;
		long s = secs % 60L;
		
		if(secs >= DAY24)
		{
			sb.append(secs / DAY24);
			sb.append("d ");
		}
		
		if(h < 10) sb.append('0');
		sb.append(h);
		sb.append("h ");
		if(m < 10) sb.append('0');
		sb.append(m);
		sb.append("m ");
		if(s < 10) sb.append('0');
		sb.append(s);
		sb.append('s');
		
		return sb.toString();
	}
}