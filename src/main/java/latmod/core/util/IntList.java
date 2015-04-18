package latmod.core.util;

public class IntList // Improve this
{
	public int array[] = new int[0];
	
	public void add(int i)
	{
		int array1[] = new int[array.length + 1];
		for(int j = 0; j < array.length; j++)
			array1[j] = array[j];
		array1[array1.length - 1] = i;
		array = array1;
	}
	
	public boolean isEmpty()
	{ return array.length <= 0; }
}