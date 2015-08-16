package latmod.ftbu.core.util;
import java.util.*;

/** Made by LatvianModder */
@SuppressWarnings("all")
public class FastList<E> implements Iterable<E>, List<E> //ArrayList
{
	public E[] objects;
	private int initSize;
	private int incr;
	private int size = 0;
	
	public FastList(int init, int inc)
	{
		initSize = init;
		incr = MathHelperLM.clampInt(inc, 1, 100);
		objects = (E[])new Object[initSize];
	}
	
	public FastList(int init)
	{ this(init, 5); }
	
	public FastList()
	{ this(10); }
	
	public FastList(Object[] o)
	{ this(); addAll(o); }
	
	public FastList<E> blankCopy()
	{ return new FastList<E>(initSize, incr); }
	
	private void expand()
	{
		E[] o = (E[])new Object[objects.length + incr];
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
	
	public E get(int i)
	{ return objects[i]; }
	
	public E remove(int i)
	{
		if(size == 0 || i == -1) return null;
		E e0 = get(i);
		size--;
		for(int j = i; j < size; j++)
		objects[j] = objects[j + 1];
		objects[size] = null;
		return e0;
	}
	
	public boolean remove(Object o)
	{
		if(size == 0 || o == null) return false;
		return removeObj(o);
	}
	
	public boolean removeObj(Object o)
	{
		int i = indexOf(o);
		if(i != -1) remove(i);
		return i != -1;
	}
	
	public void removeAll(int... i)
	{
		if(size == 0 || i == null || i.length == 0) return;
		for(int j : i) remove(j);
	}
	
	public int indexOf(Object o)
	{
		if(size == 0 || o == null) return -1;
		
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
		if(size == 0) return;
		for(int i = 0; i < size; i++)
			objects[i] = null;
		size = 0;
	}
	
	public Iterator<E> iterator()
	{ return listIterator(); }
	
	public E[] toArray()
	{
		if(size == objects.length) return objects.clone();
		E o[] = (E[])new Object[size];
		if(size == 0) return o;
		System.arraycopy(objects, 0, o, 0, size);
		objects = o;
		return objects;
	}
	
	public <E> E[] toArray(E[] a)
	{
		if(size == 0) return (E[])new Object[0];
		if(a.length == size)
		{
			for(int i = 0; i < size; i++)
				a[i] = (E)objects[i];
			return a;
		}
		return (E[]) Arrays.copyOf(objects, size, a.getClass());
	}
	
	public boolean removeAll(Collection<?> list)
	{ for(Object e : list) remove(e); return true; }
	
	public boolean contains(Object e)
	{ return indexOf(e) != -1; }
	
	public FastList<E> clone()
	{
		FastList<E> l = blankCopy();
		if(size == 0) return l;
		l.objects = toArray();
		l.size = size;
		return l;
	}
	
	public void sort(Comparator<? super E> c)
	{
		if(size == 0) return;
		if(c == null) Arrays.sort(objects, 0, size);
		else Arrays.sort((E[])objects, 0, size, c);
	}
	
	public FastList<E> sortToNew(Comparator<? super E> c)
	{
		FastList<E> l = blankCopy();
		if(size == 0) return l;
		l.addAll(this);
		if(c == null) Arrays.sort(l.objects, 0, size);
		else Arrays.sort((E[])l.objects, 0, size, c);
		return l;
	}
	
	public String toString()
	{
		if(size == 0) return "[ ]";
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		
		for(int i = 0; i < size; i++)
		{
			sb.append(String.valueOf(objects[i]));
			if(i != size - 1)
				sb.append(", ");
		}
		
		sb.append(" ]");
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
	
	public boolean containsAny(Collection<?> c)
	{ for(Object o : c) if(contains(o))
	return true; return false; }
	
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
		FastList<E> al = blankCopy();
		al.objects = (E[])new Object[toIndex - fromIndex];
		System.arraycopy(objects, fromIndex, al.objects, 0, fromIndex + toIndex);
		return al;
	}
	
	public boolean trim(int t)
	{
		if(size > t)
		{
			FastList<E> newRes = blankCopy();
			for(int i = 0; i < t; i++)
				newRes.add(get(i));
			clear();
			addAll(newRes);

			return true;
		}

		return false;
	}
	
	public FastList<E> flip()
	{
		FastList<E> al1 = blankCopy();
		if(size == 0) return al1;
		for(int i = size - 1; i >= 0; i--)
		al1.add(get(i)); return al1;
	}
	
	public boolean allObjectsEquals(E e)
	{
		if(e == null) return (size > 0) ? allObjectsEquals(get(0)) : false;
		
		for(int i = 0; i < size; i++)
		{
			if(objects[i] != null && !objects[i].equals(e))
				return false;
		}
		
		return true;
	}
	
	public static <T> FastList<T> asList(T... a)
	{ return new FastList<T>(a); }
	
	public void removeNullValues()
	{
		E[] e0 = (E[])objects.clone();
		int size0 = size;
		
		clear();
		
		for(int i = 0; i < size0; i++)
			if(e0[i] != null) add(e0[i]);
	}
}