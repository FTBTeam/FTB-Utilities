package latmod.ftbu.core.util;
import java.lang.reflect.Constructor;
import java.net.*;
import java.util.Comparator;

/** Made by LatvianModder */
public class LMUtils
{
	// Class / Object //
	
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
	
	public static boolean areObjectsEqual(Object o1, Object o2, boolean allowNulls)
	{
		if(o1 == null && o2 == null) return allowNulls;
		if(o1 == null || o2 == null) return false;
		return o1.equals(o2);
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
	
	public static void throwException(Exception e) throws Exception
	{ if(e != null) throw e; }
	
	// Net //
	
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
	
	// Misc //
	
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
	
	public static long millis()
	{ return System.currentTimeMillis(); }
}