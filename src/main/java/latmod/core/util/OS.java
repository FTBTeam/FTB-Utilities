package latmod.core.util;

public enum OS
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