package latmod.ftbu.core;

import java.util.Iterator;

import latmod.ftbu.core.util.FastList;

public class BasicRegistry<E> implements Iterable<E>
{
	public final Class<E> clazz;
	private final FastList<E> list;
	
	public BasicRegistry(Class<E> c)
	{
		clazz = c;
		list = new FastList<E>();
	}
	
	@SuppressWarnings("unchecked")
	public void add(Object e)
	{
		if(e != null && clazz.isAssignableFrom(e.getClass()) && !list.contains(e))
			list.add((E)e);
	}
	
	public void remove(Object e)
	{ if(e != null) list.removeObj(e); }
	
	public Iterator<E> iterator()
	{ return list.iterator(); }
}