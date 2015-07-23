package latmod.ftbu.core.util;

import java.io.*;
import java.util.List;

public class LMStringUtils
{
	public static final String STRIP_SEP = ", ";
	public static final String ALLOWED_TEXT_CHARS = "!@#$%^&*()_+ -=\\/,.<>?\'\"[]{}|;:`~";
	
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
}