package latmod.core;
import java.util.*;

/** <b>Made by LatvianModder</b>
 * <br>Use only in cases when 100% sure
 * <br>That this won't crash*/
public class FastList<E> implements Iterable<E>, List<E> //ArrayList
{
	public Object[] objects;
	private int initSize;
	private int incr;
	private int size = 0;
	
	public FastList(int init, int inc)
	{
		initSize = init;
		if(incr < 1) incr = 1;
		if(incr > 100) incr = 100;
		objects = new Object[initSize];
	}
	
	public FastList(int init)
	{ this(init, 5); }
	
	public FastList()
	{ this(10); }
	
	private void expand()
	{
		Object[] o = new Object[objects.length + incr];
		for(int i = 0; i < objects.length; i++)
		o[i] = objects[i];
		objects = o;
	}
	
	public boolean add(E e)
	{
		if(size == objects.length) expand();
		objects[size++] = e;
		return true;
	}
	
	public E set(int i, E e)
	{ objects[i] = e; return e; }
	
	@SuppressWarnings("unchecked")
	public E get(int i)
	{ return (E)objects[i]; }
	
	public E remove(int i)
	{
		size--;
		for(int j = i; j < size; j++)
		objects[j] = objects[j + 1];
		objects[size] = null;
		return null;
	}
	
	public boolean remove(Object o)
	{ int i = indexOf(o); if(i != -1) remove(i); return i != -1; }
	
	public int indexOf(Object o)
	{
		if(o == null || size == 0) return -1;
		
		for(int i = 0; i < size; i++)
		if(objects[i] == o) return i;
		
		for(int i = 0; i < size; i++)
		if(objects[i] != null && objects[i].equals(o)) return i;
		
		return -1;
	}
	
	public E getObj(Object o)
	{ int i = indexOf(o); return (i == -1) ? null : get(i); }
	
	public int size()
	{ return size; }
	
	public void clear()
	{
		objects = new Object[initSize];
		size = 0;
	}
	
	public Iterator<E> iterator()
	{ return listIterator(); }
	
	public Object[] toArray()
	{
		Object o[] = new Object[size];
		System.arraycopy(objects, 0, o, 0, size);
		return o;
	}
	
	@SuppressWarnings("all")
	public <E> E[] toArray(E[] a)
	{
		if(a.length < size)
		return (E[]) Arrays.copyOf(objects, size, a.getClass());
		System.arraycopy(objects, 0, a, 0, size);
		if(a.length > size) a[size] = null;
		return null;
	}
	
	public boolean removeAll(Collection<?> list)
	{ for(Object e : list) remove(e); return true; }
	
	public boolean contains(Object e)
	{ return indexOf(e) != -1; }
	
	public FastList<E> clone()
	{
		FastList<E> l = new FastList<E>(initSize, incr);
		l.objects = toArray();
		l.size = size;
		return l;
	}
	
	public void sort()
	{ if(size > 0) Arrays.sort(objects, 0, size); }
	
	@SuppressWarnings("all")
	public void sort(Comparator<E> c)
	{ if(size > 0) Arrays.sort((E[])objects, 0, size, c); }
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("List { ");
		
		for(int i = 0; i < size; i++)
		{
			sb.append(objects[i]);
			
			if(i != size() - 1)
			sb.append(", ");
		}
		
		sb.append(" }");
		return sb.toString();
	}
	
	private class FastIterator implements ListIterator<E>
	{
		private int pos = 0;
		
		public boolean hasNext()
		{ return pos < size; }
		
		public E next()
		{ E e = get(pos); pos++; return e; }
		
		public void remove()
		{ FastList.this.remove(pos); }

		public boolean hasPrevious()
		{ return pos > 0; }

		public E previous()
		{ E e = get(pos - 1); pos--; return e; }

		public int nextIndex()
		{ return pos + 1; }

		public int previousIndex()
		{ return pos - 1; }

		public void set(E e)
		{ FastList.this.set(pos, e); }

		public void add(E e)
		{ FastList.this.add(e); }
	}

	public boolean isEmpty()
	{ return size <= 0; }

	public boolean containsAll(Collection<?> c)
	{ for(Object o : c) if(!contains(o))
	return false; return true; }
	
	public boolean addAll(Collection<? extends E> l)
	{ if(l != null && l.size() > 0) addAll(l.toArray()); return true; }
	
	public void addAll(Object[] e)
	{
		if(e != null && e.length > 0)
		{
			int s = e.length;
			int incr0 = incr; incr = size + s;
			expand(); incr = incr0;
			System.arraycopy(e, 0, objects, size, s);
			size += s;
		}
	}

	public boolean addAll(int index, Collection<? extends E> c)
	{ return addAll(c); }

	public boolean retainAll(Collection<?> c)
	{ return false; }

	public void add(int i, E e)
	{ add(e); }

	public int lastIndexOf(Object o)
	{ return -1; }

	public ListIterator<E> listIterator()
	{ return new FastIterator(); }

	public ListIterator<E> listIterator(int i)
	{ FastIterator it = new FastIterator();
	it.pos = i; return it; }

	public List<E> subList(int fromIndex, int toIndex)
	{
		if(fromIndex < 0 || toIndex <= 0 || toIndex - fromIndex >= size) return null;
		FastList<E> al = new FastList<E>();
		al.objects = new Object[toIndex - fromIndex];
		System.arraycopy(objects, fromIndex, al.objects, 0, fromIndex + toIndex);
		return al;
	}
}